# Claude Notes

This is the primary agent guidance file for this repository. Read this file before making changes.

## Project Context

`MaaGF2Exilium Android` is the MaaGF2Exilium Android Root host app. It uses `MaaFramework-Android` from GitHub as a Git submodule, then mounts the submodule's `framework/` directory as Gradle project `:framework`.

Submodule:

```text
MaaFramework-Android -> git@github.com:jh-akt/MaaFramework-Android.git
```

Gradle reference:

```kotlin
include(":framework")
project(":framework").projectDir = file("MaaFramework-Android/framework")
```

App-specific code lives in `app/`. Reusable framework behavior belongs in the framework repository, then this submodule pointer should be updated.

## Important Boundaries

- App-specific UI, settings, project manifest, and MaaGF2Exilium resource behavior live in `app/`.
- Framework implementation lives in the `MaaFramework-Android` submodule; make reusable framework fixes in `../MaaFramework-Android`, then update the submodule pointer here.
- Runtime files are resolved by `app/build.gradle.kts`: local override first, prepared submodule runtime second, GitHub Release download last.
- Do not duplicate framework source into this app repo outside the submodule.
- Do not mix MaaEnd-specific app behavior into this app. Shared behavior should move into the framework instead.

## Privacy And Git Hygiene

- Before committing or pushing, verify `git config user.name` and `git config user.email`; use the GitHub noreply identity for this project, not a personal email or local machine identity.
- Before pushing, scan recent commits with `git log --format='%an <%ae>%n%cn <%ce>' -n 10` and make sure author/committer metadata does not contain personal emails, local hostnames, or machine-specific identities.
- Do not commit secrets, tokens, signing materials, private absolute paths, local-only service URLs, or raw diagnostic logs that may contain personal data. Prefer repo-relative paths and redacted examples in docs.
- If privacy-sensitive data appears in commit history, stop and clean the history before pushing. After rewriting, check `git log --all` and relevant tags/remote refs for the sensitive string, then force-push only the refs that must be corrected.
- Treat local stashes and unpushed branches as user data. Do not delete them just to remove old metadata unless the user explicitly approves.

## Important Files

Start here for MaaGF2Exilium app behavior:

1. `app/src/main/java/com/maaframework/android/gf2/MainViewModel.kt`
2. `app/src/main/java/com/maaframework/android/gf2/SampleScreen.kt`
3. `app/src/main/java/com/maaframework/android/gf2/ProjectInterfaceSupport.kt`
4. `app/src/main/java/com/maaframework/android/gf2/AppSettingsRepository.kt`
5. `app/src/main/assets/maa_project_manifest.json`
6. `app/build.gradle.kts`
7. `settings.gradle.kts`

For framework/runtime behavior, inspect:

1. `MaaFramework-Android/framework/src/main/java/com/maaframework/android/session/MaaFrameworkSession.kt`
2. `MaaFramework-Android/framework/src/main/java/com/maaframework/android/session/MaaRuntimeClient.kt`
3. `MaaFramework-Android/framework/src/main/java/com/maaframework/android/catalog/InterfaceCatalogLoader.kt`
4. `MaaFramework-Android/framework/src/main/java/com/maaframework/android/root/RootRuntimeService.kt`
5. `MaaFramework-Android/framework/src/main/java/com/maaframework/android/runtime/RuntimeBootstrapper.kt`
6. `MaaFramework-Android/framework/src/main/java/com/maaframework/android/preview/VirtualDisplayManager.kt`

## MaaGF2Exilium Project Manifest

`app/src/main/assets/maa_project_manifest.json` identifies this app as `maa-gf2-exilium`.

Current resource source:

```text
owner: DarkLingYun
repo: MaaGF2Exilium
branch: main
asset_root_path: .
```

The manifest maps upstream assets into Android resource layout, including `assets/interface.json`, `assets/resource`, and `agent`.

Default package names include official, Bilibili, Darkwinter global, and Haoplay global Android resources. PC resource entries remain visible from upstream but do not map to Android package launch targets.

## App Behavior Notes

- Task list supports multi-select. Keep checkbox state separate from row selection: checkbox controls run inclusion, row/card selection controls the task detail panel.
- `startSelectedTask()` should run checked tasks as a sequence and fall back to the focused task when nothing is checked.
- Config import/export lives in app settings and should continue to use Android system file pickers.
- If task labels or option labels leak raw keys, inspect the pulled MaaGF2Exilium `interface.json`/locale files and then the framework `InterfaceCatalogLoader`.
- Keep app UI generic and catalog-driven; avoid MaaGF2Exilium-only hardcoded task panels unless there is a concrete blocker.

## Build Notes

Known working command:

```bash
JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH" \
ANDROID_SDK_ROOT="$HOME/Library/Android/sdk" \
ANDROID_HOME="$HOME/Library/Android/sdk" \
./gradlew :app:assembleDebug
```

Run framework unit tests through the included submodule:

```bash
JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH" \
ANDROID_SDK_ROOT="$HOME/Library/Android/sdk" \
ANDROID_HOME="$HOME/Library/Android/sdk" \
./gradlew :framework:testDebugUnitTest
```

If the framework submodule is missing:

```bash
git submodule update --init --recursive
```

## Runtime Resolution

Runtime binaries are not fully tracked in git. `app/build.gradle.kts` resolves a complete Android runtime in this order:

1. `maafwRuntimeDir=/absolute/path/to/MaaFramework-Android/runtime`
2. `MaaFramework-Android/runtime` in the submodule
3. GitHub Release archive from `maafwRuntimeUrl`

Runtime override options live in `local.properties`:

```properties
maafwRuntimeDir=/absolute/path/to/MaaFramework-Android/runtime
maafwRuntimeUrl=file:///absolute/path/to/maaframework-android-runtime-arm64-v8a.zip
maafwRuntimeRepo=jh-akt/MaaFramework-Android
maafwRuntimeTag=android-runtime-v1
maafwRuntimeAsset=maaframework-android-runtime-arm64-v8a.zip
maafwRuntimeRefresh=false
```

Default download target:

```text
https://github.com/jh-akt/MaaFramework-Android/releases/download/android-runtime-v1/maaframework-android-runtime-arm64-v8a.zip
```

## Device Notes

- Target environment: Android 11+, `arm64-v8a`.
- The app process needs executable `su`; `adb root` alone is not enough.
- Physical device `382b528f` has previously been verified with app root authorization.
- Before long device sessions:

```bash
adb -s 382b528f shell svc power stayon usb
adb -s 382b528f shell settings put system screen_off_timeout 2147483647
adb -s 382b528f shell input keyevent 224
```
