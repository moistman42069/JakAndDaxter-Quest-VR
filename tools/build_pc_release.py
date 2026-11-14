#!/usr/bin/env python3
"""
The PC release, after Ratchet & Clank Quest VR's tools/build_pc.py --package (the same author's project): the launcher
(pc/launcher with the headset launcher's shared option table, launcher/common) as JakDaxterVR.exe with its own Java
runtime; OpenGOAL's gk and extractor as built for this port (tools/build_pc.bat) with their DLLs, the OpenXR loader and
the C runtime; the three games' sources the extractor builds each game from (Precursor's own toolchain set: goal_src,
decompiler/config, game/assets, the shaders, custom_assets); the launcher's texts and the licences. Zipped as
pc/build/JakQuestVR-<version>-windows.zip, the name UpdatePolicy expects. No game files: each is made from the
player's own disc.

  python -I tools/build_pc_release.py

Needs gk and extractor built (tools\\build_pc.bat), the JDK 17 (JAVA_HOME, else C:/RACQuest/toolchain/jdk17), and
Visual Studio 2022's C++ tools for the C runtime's redistributable DLLs.
"""
import hashlib
import os
import re
import shutil
import stat
import subprocess
import sys
import urllib.request
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
PRECURSOR = ROOT / "third_party/Precursor"
BIN = PRECURSOR / "out/build/Release/bin"
PC = ROOT / "pc"
BUILD_ROOT = Path(os.environ.get("JAKQUEST_BUILD_ROOT", "C:/JakQuest/b"))
WORK = BUILD_ROOT / "pc-package"
EXE = "JakDaxterVR"
# The folder in the zip: unzipped over an earlier release, it lands on it.
PACKAGE_NAME = "JakDaxterVR"
JAVA_MODULES = "java.base,java.desktop,jdk.crypto.ec"
VS_REDIST = Path("C:/Program Files (x86)/Microsoft Visual Studio/2022/BuildTools/VC/Redist/MSVC")

# The Khronos OpenXR loader for Windows, the version and SHA-256 Ratchet & Clank Quest VR's PC build pins
# (its build root's copy is used when there, as the same file).
OPENXR_VERSION = "1.1.63"
OPENXR_URL = ("https://github.com/KhronosGroup/OpenXR-SDK-Source/releases/download/"
              f"release-{OPENXR_VERSION}/openxr_loader_windows-{OPENXR_VERSION}.zip")
OPENXR_SHA256 = "01c631aeabbfe0879540f77ef833416c532a20746285b494630160c23588b771"
OPENXR_KEEP = ("x64/bin/openxr_loader.dll", "share/doc/openxr/LICENSE")

# What the extractor builds a game from, as Precursor's Android launcher bundles it (apk/app/build.gradle,
# bundleLauncherToolchain): the published path in game/data and what is left out.
TEMPLATE = ("goal_src", "decompiler/config", "game/assets", "game/graphics/opengl_renderer/shaders", "custom_assets")
TEMPLATE_SKIP = ("goal_src/jakx", "goal_src/user", "decompiler/config/jakx", "game/assets/jakx")


def run(command, cwd=None, env=None):
    print("$", " ".join(str(c) for c in command), flush=True)
    subprocess.run([str(c) for c in command], cwd=cwd, env=env, check=True)


def version() -> str:
    v = (ROOT / "VERSION").read_text(encoding="utf8").strip()
    if not re.fullmatch(r"\d+\.\d+\.\d+", v):
        sys.exit(f"VERSION is {v!r}, not 1.2.3")
    return v


def java_tool(name: str) -> Path:
    home = Path(os.environ.get("JAVA_HOME") or "C:/RACQuest/toolchain/jdk17")
    tool = home / "bin" / f"{name}.exe"
    if not tool.is_file():
        sys.exit(f"{home} has no {name}: set JAVA_HOME to a JDK 17")
    return tool


def openxr_loader() -> Path:
    """The loader's folder: Ratchet's build root's copy, or this build root's, downloaded once and checked."""
    for folder in (Path("C:/RACQuest/b") / f"openxr-windows-{OPENXR_VERSION}", BUILD_ROOT / f"openxr-windows-{OPENXR_VERSION}"):
        if all((folder / keep).is_file() for keep in OPENXR_KEEP):
            return folder
    folder = BUILD_ROOT / f"openxr-windows-{OPENXR_VERSION}"
    archive = BUILD_ROOT / f"openxr_loader_windows-{OPENXR_VERSION}.zip"
    BUILD_ROOT.mkdir(parents=True, exist_ok=True)
    print("downloading", OPENXR_URL, flush=True)
    with urllib.request.urlopen(OPENXR_URL) as response, open(archive, "wb") as f:
        shutil.copyfileobj(response, f)
    digest = hashlib.sha256(archive.read_bytes()).hexdigest()
    if digest != OPENXR_SHA256:
        archive.unlink()
        sys.exit(f"the OpenXR loader package's SHA-256 is {digest}, not {OPENXR_SHA256}")
    with zipfile.ZipFile(archive) as z:
        for member in z.namelist():
            if member in OPENXR_KEEP:
                target = folder / member
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_bytes(z.read(member))
    archive.unlink()
    if not all((folder / keep).is_file() for keep in OPENXR_KEEP):
        sys.exit(f"{OPENXR_URL} has not {OPENXR_KEEP}")
    return folder


def vc_runtime() -> list:
    """The Visual C++ runtime gk and the extractor were built against: the newest x64 CRT of VS 2022's redistributable."""
    found = sorted(VS_REDIST.glob("*/x64/Microsoft.VC14*.CRT"))
    if not found:
        sys.exit(f"{VS_REDIST} has no x64 C runtime")
    dlls = sorted(p for p in found[-1].glob("*.dll") if p.name.lower().startswith(("msvcp140", "vcruntime140", "concrt140")))
    for wanted in ("msvcp140.dll", "vcruntime140.dll", "vcruntime140_1.dll"):
        if not any(p.name.lower() == wanted for p in dlls):
            sys.exit(f"{found[-1]} has no {wanted}")
    return dlls


def remove_tree(path: Path) -> None:
    """A folder and all in it, its read-only files too (jpackage leaves the exe read-only)."""
    def writable(function, name, _error):
        os.chmod(name, stat.S_IWRITE)
        function(name)
    if path.exists():
        shutil.rmtree(path, onexc=writable)


def build_launcher(out: Path, ver: str) -> Path:
    """The launcher's jar, with PcBuild (the version) written for it, and its icon."""
    remove_tree(out)
    generated = out / "gen/dev/jakquest/vr"
    generated.mkdir(parents=True)
    (generated / "PcBuild.java").write_text(
        "// Written by tools/build_pc_release.py.\npackage dev.jakquest.vr;\n\n"
        f"final class PcBuild {{\n    static final String VERSION = \"{ver}\";\n\n    private PcBuild() {{}}\n}}\n",
        encoding="utf8", newline="\n")
    sources = sorted((ROOT / "launcher/common/src").rglob("*.java"))
    sources += sorted(p for p in (PC / "launcher/src").rglob("*.java") if p.name != "PcBuild.java")
    sources.append(generated / "PcBuild.java")
    classes = out / "classes"
    run([java_tool("javac"), "--release", "17", "-encoding", "UTF-8", "-Xlint:all", "-Xlint:-serial", "-Werror",
         "-d", classes, *sources])
    jar = out / "input/launcher.jar"
    jar.parent.mkdir(parents=True)
    with zipfile.ZipFile(jar, "w", zipfile.ZIP_DEFLATED) as z:
        z.writestr("META-INF/MANIFEST.MF", "Manifest-Version: 1.0\r\nMain-Class: dev.jakquest.vr.PcLauncher\r\n\r\n")
        for path in sorted(classes.rglob("*.class")):
            z.write(path, path.relative_to(classes).as_posix())
    run([java_tool("java"), "-cp", classes, "dev.jakquest.vr.PcLauncher", "--write-icon", out / "orb.ico"])
    return jar


def copy_template(data: Path) -> None:
    """The games' sources into game/data, as the Android launcher's toolchain.zip carries them."""
    for part in TEMPLATE:
        source = PRECURSOR / part
        if not source.is_dir():
            sys.exit(f"{source} is missing")
        for path in sorted(source.rglob("*")):
            rel = path.relative_to(PRECURSOR).as_posix()
            if not path.is_file() or any(rel == s or rel.startswith(s + "/") for s in TEMPLATE_SKIP):
                continue
            target = data / rel
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(path, target)
    shutil.copy2(PRECURSOR / "LICENSE", data / "LICENSE")


def package(ver: str) -> Path:
    for needed in ("gk.exe", "extractor.exe"):
        if not (BIN / needed).is_file():
            sys.exit(f"{BIN / needed} was not built (tools\\build_pc.bat)")
    remove_tree(WORK)
    WORK.mkdir(parents=True)
    jar = build_launcher(WORK / "launcher", ver)
    runtime = WORK / "runtime"
    run([java_tool("jlink"), "--add-modules", JAVA_MODULES, "--strip-debug", "--no-header-files", "--no-man-pages",
         "--compress=2", "--output", runtime])
    run([java_tool("jpackage"), "--type", "app-image", "--name", EXE, "--input", jar.parent, "--main-jar", jar.name,
         "--main-class", "dev.jakquest.vr.PcLauncher", "--runtime-image", runtime,
         "--icon", WORK / "launcher/orb.ico", "--app-version", ver,
         "--vendor", "Jak and Daxter Quest VR contributors", "--description", "Jak and Daxter VR",
         "--copyright", "GPL-3.0-or-later", "--dest", WORK / "image"])
    folder = WORK / "image" / EXE
    if not (folder / f"{EXE}.exe").is_file():
        sys.exit(f"jpackage made no {EXE}.exe")

    # OpenGOAL's programs as built for this port, their DLLs, the OpenXR loader and the C runtime beside them.
    game = folder / "game"
    game.mkdir()
    for name in ("gk.exe", "extractor.exe"):
        shutil.copy2(BIN / name, game / name)
    for dll in sorted(BIN.glob("*.dll")):
        shutil.copy2(dll, game / dll.name)
    loader = openxr_loader()
    shutil.copy2(loader / "x64/bin/openxr_loader.dll", game / "openxr_loader.dll")
    for dll in vc_runtime():
        shutil.copy2(dll, game / dll.name.lower())
    copy_template(game / "data")

    # The launcher's texts: the shared What's new, the controls and credits as the repository has them, the notices.
    texts = folder / "texts"
    texts.mkdir()
    shutil.copy2(ROOT / "launcher/common/texts/whats_new.txt", texts / "whats_new.txt")
    shutil.copy2(ROOT / "docs/CONTROLS.md", texts / "controls.md")
    shutil.copy2(ROOT / "CREDITS.md", texts / "credits.md")
    with (texts / "third_party_notices.txt").open("w", encoding="utf8", newline="\n") as out:
        out.write((ROOT / "THIRD_PARTY_NOTICES.txt").read_text(encoding="utf8"))
        out.write("\n\nIN THE PC RELEASE ONLY\n\n"
                  "runtime\\ (the launcher's Java runtime): OpenJDK, GPL-2.0 with the Classpath Exception; its notices are in\n"
                  "  runtime\\legal.\n"
                  "game\\openxr_loader.dll: the Khronos OpenXR loader " + OPENXR_VERSION + ", Apache-2.0 OR MIT\n"
                  "  (licenses\\openxr-loader-LICENSE.txt).\n"
                  "game\\msvcp140*.dll, vcruntime140*.dll, concrt140.dll: the Microsoft Visual C++ runtime, redistributed\n"
                  "  under Visual Studio's licence terms for its redistributable files.\n")
    licenses = folder / "licenses"
    licenses.mkdir()
    shutil.copy2(loader / "share/doc/openxr/LICENSE", licenses / "openxr-loader-LICENSE.txt")
    shutil.copy2(PRECURSOR / "LICENSE", licenses / "OpenGOAL-and-Precursor-LICENSE.txt")
    shutil.copy2(ROOT / "LICENSE", folder / "LICENSE.txt")
    shutil.copy2(PC / "README.txt", folder / "README.txt")
    shutil.copy2(ROOT / "docs/MODS.md", folder / "MODS.md")
    # Jak 1's Multiplayer: how to play it, and the party server for games over the internet (docs/MULTIPLAYER.md).
    party = folder / "party-server"
    party.mkdir()
    shutil.copy2(ROOT / "tools/party_server.py", party / "party_server.py")
    shutil.copy2(ROOT / "docs/MULTIPLAYER.md", party / "MULTIPLAYER.md")
    # Nothing read-only (jpackage's exe is): an update's robocopy and the player's own unzip write over all of it.
    for path in folder.rglob("*"):
        if path.is_file():
            os.chmod(path, stat.S_IREAD | stat.S_IWRITE)

    # The launcher's own checks on what was just put together.
    env = dict(os.environ)
    for key in ("JAKQUESTVR_HOME", "JAKQUESTVR_LOGS"):
        env.pop(key, None)
    env["JAKQUESTVR_INSTALL"] = str(folder)
    run([runtime / "bin/java.exe", "-cp", folder / "app" / jar.name, "dev.jakquest.vr.PcLauncher", "--self-test"], env=env)

    out = PC / f"build/JakQuestVR-{ver}-windows.zip"
    out.parent.mkdir(parents=True, exist_ok=True)
    out.unlink(missing_ok=True)
    with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as z:
        for path in sorted(folder.rglob("*")):
            if path.is_file():
                z.write(path, f"{PACKAGE_NAME}/{path.relative_to(folder).as_posix()}")
    digest = hashlib.sha256(out.read_bytes()).hexdigest()
    print(f"{out}: {out.stat().st_size / 1e6:.0f} MB, SHA-256 {digest}", flush=True)
    return out


if __name__ == "__main__":
    package(version())
