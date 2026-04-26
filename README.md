# MaaGF2Exilium Android

`MaaGF2Exilium Android` 是 MaaGF2Exilium 的 Android Root 宿主应用。框架代码不复制到本仓库，而是通过 Git submodule 引用 GitHub 上的 `MaaFramework-Android`。

## 项目关系

- `MaaFramework-Android/`
  - Git submodule
  - URL: `git@github.com:jh-akt/MaaFramework-Android.git`
  - `settings.gradle.kts` 会把其中的 `framework/` 挂载成 Gradle 子项目 `:framework`
  - `app/build.gradle.kts` 会从 GitHub Release 下载 runtime zip，或使用本地覆盖路径
- `app/`
  - MaaGF2Exilium Android 应用模块
  - 包含 MaaGF2Exilium 的任务 UI、配置导入导出、虚拟屏预览和 Root Runtime 控制

## 克隆

```bash
git clone --recurse-submodules <this-repo-url>
```

如果已经普通 clone：

```bash
git submodule update --init --recursive
```

## 构建

```bash
JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH" \
ANDROID_SDK_ROOT="$HOME/Library/Android/sdk" \
ANDROID_HOME="$HOME/Library/Android/sdk" \
./gradlew :app:assembleDebug
```

调试安装：

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Framework 引用

这个项目在 `settings.gradle.kts` 中引用 submodule 里的框架模块：

```kotlin
include(":framework")
project(":framework").projectDir = file("MaaFramework-Android/framework")
```

App 模块继续通过 Gradle 子项目依赖框架：

```kotlin
implementation(project(":framework"))
```

Runtime 打包目录默认指向：

```kotlin
MaaFramework-Android/runtime
```

## Runtime 自动获取

默认构建会按下面的优先级解析 Android runtime：

1. `local.properties` 中的 `maafwRuntimeDir`
2. submodule 内已准备好的 `MaaFramework-Android/runtime`
3. GitHub Release asset

默认 Release 配置：

```properties
maafwRuntimeRepo=jh-akt/MaaFramework-Android
maafwRuntimeTag=android-runtime-v1
maafwRuntimeAsset=maaframework-android-runtime-arm64-v8a.zip
```

默认 URL 等价于：

```text
https://github.com/jh-akt/MaaFramework-Android/releases/download/android-runtime-v1/maaframework-android-runtime-arm64-v8a.zip
```

开发机可以用 `local.properties` 覆盖：

```properties
maafwRuntimeDir=/Users/haojiang/Code/MaaFramework-Android/runtime
```

也可以直接指向一个本地或远程 zip：

```properties
maafwRuntimeUrl=file:///Users/haojiang/Code/MaaFramework-Android/dist/maaframework-android-runtime-arm64-v8a.zip
```

如果需要重新下载同名 zip：

```bash
./gradlew :app:assembleDebug -PmaafwRuntimeRefresh=true
```

GitHub 上的 framework 仓库只跟踪 runtime 说明和工具脚本，实际 Android runtime 二进制需要通过 `MaaFramework-Android/tools/package_android_runtime.py` 打包并发布为 Release asset，或在本机用 `maafwRuntimeDir` 覆盖。
