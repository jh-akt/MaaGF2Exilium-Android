import org.gradle.api.tasks.Sync
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties().apply {
    if (localPropertiesFile.isFile) {
        localPropertiesFile.inputStream().use(::load)
    }
}

fun runtimeProperty(name: String, defaultValue: String? = null): String? {
    return providers.gradleProperty(name).orNull
        ?: localProperties.getProperty(name)
        ?: defaultValue
}

fun runtimePath(value: String): File {
    val file = File(value)
    return if (file.isAbsolute) file else rootProject.file(value)
}

fun File.hasAndroidRuntimeArtifacts(): Boolean {
    val maafwDir = resolve("maafw")
    val agentDir = resolve("agent")
    return maafwDir.isDirectory &&
        maafwDir.walkTopDown().any { it.isFile && it.extension == "so" } &&
        agentDir.isDirectory &&
        agentDir.walkTopDown().any { it.isFile }
}

fun requireAndroidRuntimeArtifacts(runtimeDir: File) {
    if (runtimeDir.hasAndroidRuntimeArtifacts()) {
        return
    }
    throw GradleException(
        """
        MaaFramework Android runtime is missing or incomplete: ${runtimeDir.absolutePath}

        Provide one of these:
        - local.properties: maafwRuntimeDir=/absolute/path/to/MaaFramework-Android/runtime
        - local.properties: maafwRuntimeUrl=file:///absolute/path/to/maaframework-android-runtime-arm64-v8a.zip
        - GitHub Release asset configured by maafwRuntimeRepo / maafwRuntimeTag / maafwRuntimeAsset
        """.trimIndent(),
    )
}

fun downloadRuntimeArchive(url: String, target: File) {
    target.parentFile.mkdirs()
    val connection = URI(url).toURL().openConnection()
    connection.connectTimeout = 30_000
    connection.readTimeout = 120_000
    System.getenv("GITHUB_TOKEN")?.takeIf { it.isNotBlank() }?.let { token ->
        connection.setRequestProperty("Authorization", "Bearer $token")
    }
    if (connection is HttpURLConnection) {
        connection.requestMethod = "GET"
        val status = connection.responseCode
        if (status !in 200..299) {
            throw GradleException("Failed to download runtime archive: HTTP $status from $url")
        }
    }
    connection.getInputStream().use { input ->
        target.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}

val frameworkRuntimeDir = rootProject.layout.projectDirectory.dir("MaaFramework-Android/runtime")
val resolvedRuntimeDir = layout.buildDirectory.dir("generated/resolvedRuntime")
val downloadedRuntimeDir = layout.buildDirectory.dir("generated/downloadedRuntime")
val runtimeReleaseRepo = runtimeProperty("maafwRuntimeRepo", "jh-akt/MaaFramework-Android")!!
val runtimeReleaseTag = runtimeProperty("maafwRuntimeTag", "android-runtime-v1")!!
val runtimeReleaseAsset = runtimeProperty("maafwRuntimeAsset", "maaframework-android-runtime-arm64-v8a.zip")!!
val runtimeDownloadUrl = runtimeProperty(
    "maafwRuntimeUrl",
    "https://github.com/$runtimeReleaseRepo/releases/download/$runtimeReleaseTag/$runtimeReleaseAsset",
)!!
val runtimeArchiveFile = layout.buildDirectory.file("downloads/$runtimeReleaseAsset")
val runtimeDirOverride = runtimeProperty("maafwRuntimeDir")
val runtimeRefresh = runtimeProperty("maafwRuntimeRefresh", "false").toBoolean()
val generatedRuntimeAssetsDir = layout.buildDirectory.dir("generated/runtimeAssets")
val generatedJniLibsDir = layout.buildDirectory.dir("generated/jniLibs")
val signingPropertiesFile = rootProject.file("key.properties")
val signingProperties = Properties().apply {
    if (signingPropertiesFile.exists()) {
        signingPropertiesFile.inputStream().use(::load)
    }
}

val resolveAndroidRuntime by tasks.registering {
    inputs.property("maafwRuntimeDir", runtimeDirOverride.orEmpty())
    inputs.property("maafwRuntimeUrl", runtimeDownloadUrl)
    inputs.property("maafwRuntimeRefresh", runtimeRefresh)
    outputs.dir(resolvedRuntimeDir)
    outputs.upToDateWhen { false }

    doLast {
        val resolvedDir = resolvedRuntimeDir.get().asFile
        delete(resolvedDir)

        val sourceDir = when {
            !runtimeDirOverride.isNullOrBlank() -> {
                runtimePath(runtimeDirOverride).also(::requireAndroidRuntimeArtifacts)
            }

            frameworkRuntimeDir.asFile.hasAndroidRuntimeArtifacts() -> {
                frameworkRuntimeDir.asFile
            }

            else -> {
                val archive = runtimeArchiveFile.get().asFile
                if (!archive.isFile || runtimeRefresh) {
                    logger.lifecycle("Downloading MaaFramework Android runtime from $runtimeDownloadUrl")
                    downloadRuntimeArchive(runtimeDownloadUrl, archive)
                }
                val unpackedDir = downloadedRuntimeDir.get().asFile
                delete(unpackedDir)
                copy {
                    from(zipTree(archive))
                    into(unpackedDir)
                    includeEmptyDirs = true
                }
                unpackedDir.also(::requireAndroidRuntimeArtifacts)
            }
        }

        copy {
            from(sourceDir)
            into(resolvedDir)
            includeEmptyDirs = true
        }
        logger.lifecycle("Resolved MaaFramework Android runtime from ${sourceDir.absolutePath}")
    }
}

val prepareBundledRuntimeAssets by tasks.registering(Sync::class) {
    dependsOn(resolveAndroidRuntime)
    from(resolvedRuntimeDir)
    into(generatedRuntimeAssetsDir.map { it.dir("bundled_runtime") })
    includeEmptyDirs = true
}

val prepareBundledRuntimeJniLibs by tasks.registering(Sync::class) {
    dependsOn(resolveAndroidRuntime)
    from(resolvedRuntimeDir.map { it.dir("maafw") })
    into(generatedJniLibsDir.map { it.dir("arm64-v8a") })
    include("*.so")
    includeEmptyDirs = false
}

android {
    namespace = "com.maaframework.android.gf2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.maaframework.android.gf2"
        minSdk = 30
        targetSdk = 36
        versionCode = 2
        versionName = "0.1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    signingConfigs {
        if (signingPropertiesFile.exists()) {
            create("release") {
                storeFile = file(signingProperties.getProperty("storeFile"))
                storePassword = signingProperties.getProperty("storePassword")
                keyAlias = signingProperties.getProperty("keyAlias")
                keyPassword = signingProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            if (signingPropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets.getByName("main").assets.srcDirs(
        "src/main/assets",
        generatedRuntimeAssetsDir,
    )
    sourceSets.getByName("main").jniLibs.srcDirs(generatedJniLibsDir)

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
            )
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.named("preBuild") {
    dependsOn(prepareBundledRuntimeAssets)
    dependsOn(prepareBundledRuntimeJniLibs)
}

dependencies {
    implementation(project(":framework"))
    implementation(project(":framework-ui"))

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.12.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    implementation(platform("androidx.compose:compose-bom:2026.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    testImplementation("junit:junit:4.13.2")
}
