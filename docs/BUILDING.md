# Building

## PC (Windows)

With Git Bash, Visual Studio 2022 Build Tools, LLVM (clang-cl) and NASM:

```bash
git clone --recursive https://github.com/moistman42069/JakAndDaxter-Quest-VR.git
cd JakAndDaxter-Quest-VR
sh tools/setup.sh            # Precursor at its pinned commit, with this port's patches applied
tools/build_pc.bat           # gk, goalc, decompiler, extractor (or name some: tools/build_pc.bat gk)
```

Then extract and compile a game from your disc (the disc's files go under `third_party/Precursor/iso_data/<game>`):

```bash
cd third_party/Precursor
out/build/Release/bin/extractor.exe -g jak1 -e -d -c --proj-path "$(cygpath -w $PWD)" path/to/your/jak1.iso
```

After a change to a game's GOAL code, compile it again from the disc extracted before:

```bash
out/build/Release/bin/extractor.exe -g jak1 -c -f --proj-path "$(cygpath -w $PWD)" "$(cygpath -w $PWD/iso_data/jak1)"
```

Run it in VR with `JAKVR=1` set (an OpenXR runtime running), or on the simulated headset the tests use
([tests/README.md](../tests/README.md)):

```bash
sh tools/run_sim.sh jak1 tests/jak1_play.sim out/jak1_play
```

The PC release (the launcher as JakDaxterVR.exe with its own Java, gk, the extractor and the games' sources, the party
server): `python tools/build_pc_release.py` (JDK 17 in `JAVA_HOME`, or Ratchet & Clank Quest VR's toolchain folder).

## Quest (Android)

The Quest app is Precursor's Android app with the VR layer, in `third_party/Precursor/scripts/android-port/apk`. The
toolchain is the one Ratchet & Clank Quest VR builds with, so one set serves both:

| What | Version |
| --- | --- |
| JDK | 17 |
| Android SDK | platform 34 (compileSdk and targetSdk 34) and build-tools 34 |
| Android NDK | 27.2.12479018 |
| Gradle and its Android plugin | Gradle 8.10.2 (the wrapper's), the Android Gradle plugin 8.7.3 |
| CMake and Ninja | any recent CMake (4.4 here) with Ninja on the PATH, named in `local.properties` |
| OpenXR loader for Android | `org.khronos.openxr:openxr_loader_for_android:1.1.65` from Maven Central, the AAR unpacked into a folder |

Point the build at the unpacked loader with `-PopenxrLoaderDir=<folder>` or the `JAKVR_OPENXR_ANDROID_DIR`
environment variable: its headers build the runtime and its `jni/arm64-v8a/libopenxr_loader.so` goes into the APK.
The app is ARM64 only.

Signing: the port's own key lives outside the repository, in `~/.jakquestvr/release-signing.properties` (`storeFile`,
`storePassword`, `keyAlias`, `keyPassword`; `-PprecursorSigning=<file>` for another place). Without it the build uses
the usual debug key.

`local.properties` in that folder (not in the repository) names the SDK and CMake, for example:

```
sdk.dir=C\:/Android/sdk
cmake.dir=C\:/Program Files/CMake
```

```bash
cd third_party/Precursor/scripts/android-port/apk
./gradlew assembleRelease -PopenxrLoaderDir=/path/to/openxr_loader_for_android-1.1.65
```

The APK is `app/build/outputs/apk/release/app-release.apk`.

The launcher build (the default, `-PopenGoalGame=launcher`) bundles the three games' sources and the decompiler's
settings; the headset builds each game from the player's disc image when it is added. The 0.1.0 APK was built this
way (about 16 minutes from clean, under a minute after a change to the Java side alone). The native libraries are
Precursor's release setting: CMake's Debug type with -O2, assertions and symbols kept, as on the phones it was tested
on.
