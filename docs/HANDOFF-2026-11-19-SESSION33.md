# Handoff, 2026-11-19, Session 33

Read this first, then docs/LIST-0.1.8.md. This session picked up from docs/HANDOFF-2026-11-19-SESSION32.md. 0.1.7 is
the latest release. 0.1.8 is NOT published, and publishing has not been asked for: the list is done (items 1 to 15
all [x]); ask the owner before publishing. Nothing is committed: all of 0.1.8 is uncommitted in this repo and in the
Precursor submodule.

Check first that nothing is still running: `tasklist | grep -iE "gk.exe|goalc|extractor"` and `ps -ef | grep -E
"chain|batch|run_sim"`. Orphan `sleep 1500` processes whose parent is 1 are harmless (a finished run's watchdog
subshell was killed; its sleep just ends); a batch stopped by hand can leave a live watchdog subshell, which can
taskkill a later run's gk.exe.

## Built now (the source matches)

- gk: build 120. Jak 1 GOAL: 117. Jak II and 3 GOAL: 119. ARM64 compile check passed after the last C++ edit
  (arm64b).

## Found and fixed this session

- Item 9 closed (batch51 on build 118): every frame still apart on the new count is Jak's own body near the eyes
  (shoulder pads in s2w-fort, his arms in s3w-desert), the shadow itself alike in both eyes.
- Item 2, a regression of 0.1.8's own guard (never released): Jak II's title scene ended 4 s after Start in every
  Jak II run since build 101. Start sets the game's skip frame and current-str-pos then pins the position at 37717 on
  purpose (the scene held at the logo until a menu choice). Fix in build 119 (jak2/jak3 loader.gc): no count while a
  skip frame is set. (The title's camera moves after Start with or without the fix: the menu's own camera.)
- Items 2 and 3 had no check that made them happen. New test aid (PC only): the sim's `audio_loss <seconds>` script
  line, or `JAKVR_TEST_AUDIO_LOSS=<from>,<for>`; with `old` in JAKVR_TEST_AUDIO_LOSS the watch leaves the synth stopped
  too (as before 0.1.8). game/sound/989snd/audio_loss_test.h (new, header-only atomic), player.cpp, game/vr/xr_sim.cpp.
- Item 4's every-10-s line and the window title had never run (SteamVR's null driver gives focus at once). New test
  aid `JAKVR_TEST_UNSHOWN=<from>,<for>` (xr_openxr.cpp test_unshown) and a log line when the window title's note comes
  and goes (graphics/pipelines/opengl.cpp).
- Item 15 in Jak 3: tests/jak3_double_jump.sim (new; wascitya-start), gkcfg-60's jak3 vr-options.txt made
  (`game-frame-rate 0`).
- New sims: tests/jak3_double_jump.sim, jak1_cell_sound.sim, jak2_title_sound.sim. tests/README: the test aid rows
  and "The checks added in 0.1.8" bullets for all of this session's runs.

## Runs (C:\JakQuest\sim\s08), all read

- batch31b (ps2, pmp3, boat2, hol3, race3, board3, zoom3, buggy4): all exit 0, no errors, crashes or stalls; each
  Jak II run had the title-scene line above. mp2: host and guest exit 0, no router lines. null18: item 4's states
  named. arm64: 188/188.
- build 119, batch54: pmp4 (no title-scene line), al2 (Jak II title, the watch took over, reopened every 3 s,
  playing again after 14.0 s, the scene went on), al2o (`title-disk-intro held at 10076`), al1/al1o (the device away
  before the cell's animation: al1 played it through, al1o the game's own 4 s guard at position 0).
- batch53: j3dj72 3.50, 3.50, 5.82, 5.43 against j3dj60 3.48, 3.48, 5.84, 5.33.
- build 120, null19 (`JAKVR_TEST_UNSHOWN=20,25`): the lines at 10 and 20 s, the window title and back. arm64b: 5/5.
- batch55: al1b (the device away 4 s into the cell's animation: played to its end), al1ob (`fuel-cell-victory-7
  held at 4544 for 4 s ... it ends here`, Jak in play again: the tester's case).
- Not a 0.1.8 problem, noted in tests/jak1_cell_sound.sim: after the warp onto Geyser Rock's first cell the cell
  waits about 10 s on another camera already running (the game's own `WARNING: fuel-cell stall` lines), the same in
  every run with the sound on or stopped; the tester's log has none.
- boat2 tells nothing about item 2 in Jak 1: this copy's Jak 1 has no stream for the opening scene (`[FAKEISO]
  failed to find SAISB STR`), so the game's own guard ends it after 4 s.

## Next

1. Ask the owner whether to publish 0.1.8 (release steps: SESSION21, SESSION27, SESSION28 handoffs;
   tools/make_patch.sh writes patches/0001-jak-quest-vr.patch from the submodule). The privacy scan of the changed
   public files is done (session 33): nothing new names a player, the tester's Windows user name or the owner's
   email; the tester-name comments in code and older sims were all in the published 0.1.7 patch already.
2. Then players' reports and headset logs.

## Rules that stay

- Break and regress nothing; everything goes in this release; new toggles on to start with plain names; multiplayer
  never changes a one-player game; every button reachable, no conflicts.
- Targeted sims only; one build or sim at a time; never edit source a queued chain may build. Every crash and close
  explains itself in the game's own log; never ask for Event Viewer. Resolving and checking is ours.
- Shell: never `python -` or other stdin scripts (write a file, `python -I file.py`; `python -E -P` for Pillow); LF
  scripts; Edit or sed for source edits; read before edit; never git stash; `${VAR:?}` in rm.
- No commit unless asked. Ratchet (C:\RACQuest) and HaloQuest are read-only. The testers' logs are read-only data.
  Public notes name no player and never the tester's Windows user name.
