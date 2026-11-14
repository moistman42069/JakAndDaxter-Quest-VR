# Bringing in a newer OpenGOAL release

The port runs on Precursor (Android, Quest and the ARM64 GOAL compiler) with OpenGOAL's latest release merged in,
and the port's own changes as patches on top:

| Layer | Where | Made by |
|---|---|---|
| Precursor at its pinned commit | `third_party/Precursor` (submodule) | `git submodule update` |
| OpenGOAL's release merged in | `patches/upstream/` (`BASE`, `take.txt`, `remove.txt`, `fixups.patch`) | `tools/setup.sh` |
| The port | `patches/0001-jak-quest-vr.patch`, `patches/0002-openxr-headers.patch` | `tools/setup.sh` |

`tools/setup.sh` checks out the files the merge takes whole from the release tag, removes the files it removes, and
applies `fixups.patch` (the files merged by hand). It then checks that the result is the tree recorded in `BASE` and
keeps it as `refs/jakvr/base`. `tools/make_patch.sh` writes the port's patches against that base. Nothing is committed
in Precursor.

## The rule

- **Precursor's ARM64 backend stays.** OpenGOAL added its own ARM64 backend (Apple Silicon, #4390, and the goalc
  fixes #4377 and #4384-4386, #4419) after Precursor forked. The two backends model registers differently (Precursor:
  `Register(id, is_arm64_vector)`, OpenGOAL: separate vector register ids). Precursor fixed the same encoder bugs the
  same way, and the Quest build runs on Precursor's. A file only those commits changed keeps Precursor's version. A
  file they and other commits changed gets Precursor's version plus the other commits. The commits are listed in
  `patches/upstream/arm64-commits.txt`; add any newer OpenGOAL ARM64 backend commit there first.
- **Everything else comes from OpenGOAL:** features, fixes, decompiler and level work, texture packs, mods, the PC
  settings, speedrun mode, the vendored libraries.
- **By hand:** where both sides changed the same lines. For v0.3.9 that was 22 files:
  - the build files and CI workflows (both sides' options kept);
  - the extractor and the decompiler config (both sides' options);
  - the compiler's code generator, debugger and main (Precursor's ARM64 work beside OpenGOAL's other changes);
  - the game's main and renderer entry, and the mips2c table;
  - the sound library, now position independent for OpenGOAL's sound extraction;
  - Jak 3's loader and Jak X's kernel.

## Steps

```
sh tools/update_upstream.sh start v0.3.10
```

This makes a worktree at `../precursor-merge` from Precursor's pinned commit (or pass a newer Precursor commit as
the third argument), merges the tag without committing, applies the rule, and lists what is left. Resolve those
files there and `git add` them.

```
sh tools/update_upstream.sh finish
```

This records the merge in `patches/upstream`.

```
sh tools/update_upstream.sh move
```

This takes the port's patches off `third_party/Precursor` (they are kept in `out/upstream-merge/old-patches`), puts
it on the new base and applies the patches again, three-way. Resolve what it lists, then run
`git read-tree refs/jakvr/base` in `third_party/Precursor` and `sh tools/make_patch.sh` here.

Then:

1. `tools\build_pc.bat`.
2. Compile the three games.
3. Run their sim tests (`tools/run_sim.sh`), with Max draw distance on.
4. Build the Quest APK.
5. `git worktree remove ../precursor-merge` once done.

## v0.3.9 (2026-11-13)

- Precursor 8c6f2c6e (forked at e0dc1155, #4358) merged with OpenGOAL v0.3.9 (b6c8430c, 2026-10-13).
- 4,882 files taken from the release (4,345 of them curl's new vendored copy), 1,232 removed (curl's old copy and
  old decompiler test data), 22 merged by hand.
- On top of that, the port's patches had two clashes:
  - The top-level `CMakeLists.txt`: OpenGOAL now copies `compile_commands.json` with `file(COPY)`, which fixes the
    same endless reconfigure, so theirs was taken.
  - curl's `CMakeLists.txt`: the port's write-only-when-changed fix was redone for the new curl.
