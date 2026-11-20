# Handoff, 2026-11-19, Session 32

Read this first, then docs/LIST-0.1.8.md. This session picked up from docs/HANDOFF-2026-11-19-SESSION31.md (and a
session between whose work is in the list: items 11 and 12, item 9's depth-fail count, builds 110 and 111). 0.1.7 is
the latest release. 0.1.8 is NOT published, and publishing has not been asked for: finish the list, then ask the owner.
Nothing is committed: all of 0.1.8 is uncommitted in this repo and in the Precursor submodule.

Check first that nothing is still running: `tasklist | grep -iE "gk.exe|goalc|extractor"` and `ps -ef | grep -E
"batch|run_sim|sleep"` (a stopped batch leaves its watchdog subshell, which can taskkill a later run's gk.exe).

## Built now

- gk: build 118 (113's game code; 118 adds only the shadow test aid's pictures). Jak 1 GOAL: 117. Jak II and 3 GOAL:
  116. The source matches these builds.

## New reports this session (the owner passing on testers' and players' lines)

- Item 11, Jak 1's credits "annoyingly flickery": found and fixed (build 113, kFlagCredits: the eyes black behind
  the credits; the game's black behind them is a one-frame screen-space particle with no room among the mist's in
  the 128 HUD particles much of the time, so the world showed through the panel). Checked (fbc2). The tester will send
  a log after the credits (they play on to look at the scenery).
- Item 13, Jak 1's final boss going third person: the left stick's click (the menu button on SteamVR with Touch)
  held while running. Fixed (build 112) and checked (vh).
- Item 14, Jak II's pole: first person for the flip, then third, then first. Fixed in Jak II and 3 (build 112) and
  checked in Jak II (p2 runs).
- Item 9, the shadows: "if next build hasn't fixed shadows, shadows need fixed". Build 110's depth-fail count is the
  fix; batch40 checks it.
- Item 15, Jak 1, Geyser Rock: a player double jumping but not reaching the first ledge. Found real: at the headset's
  rate every jump is lower than at sixty (dj72 3.21 m against dj60 3.47; a double jump 5.37 against 5.92). Two of the
  original's sums count on sixty: mod-var-jump's 0.1 s A-hold window (Jak 1's clock steps 4 ticks at 72 and the last
  frame inside is 0.93 of it) and collide-shape.gc's jump-animation offset as a speed (its change times a fixed 60).
  Fixed in all three games (builds 115 and 116) and checked (dj72f/dj60f, jdj72/jdj60: the same heights at both
  rates). Not changed, written up in the list: Jak 1's clock counts whole ticks (288 a second at 72, 240 at 120); Jak
  II and 3 carry the fraction. The owner's reply to the player: hold A on both jumps, the second near the top of the
  first (a quick double press is one jump); Game frame rate 60 gives the full height before 0.1.8.

## Where the list stands

- [x]: 5, 6, 7, 8, 10, 11, 12, 13, 14, 15.
- [~] 9: batch40 (each eye's cover, depth fail against z-pass).
- [ ] 1 to 4: coded and built; batch31b, mp2, null18 and the ARM64 check still to run.

## Next, in this order (one build or sim at a time)

1. Running when this was written (one background chain): build117.sh, batch48.sh (dj72g: Jak 1 at 72 on build 117,
   the same heights as dj72f, no `test: jf` lines), then batch40.sh (13 runs: sh2z and sh3z with the old count for
   comparison, the walks on the new; `[vr] shadow in each eye` lines: apart in 0 on the new count). Read
   C:\JakQuest\sim\s08\batch48.txt and batch40.txt. Look at the pictures where the eyes part, if any. Then item 9's
   release note and what's new line (none yet: write them once the walks show the eyes alike), and mark item 9.
2. `sh /c/JakQuest/sim/batch31b.sh > /c/JakQuest/sim/s08/batch31b.txt 2>&1`: ps2 (one `[vr-mem]` line only), pmp3,
   boat2 (no "streamed animation" line), hol3, race3, board3, zoom3, buggy4.
3. `OUT=s08/mp2 GUEST_SIM=/c/JakQuest/sim/mp2_guest_pmp.sim sh /c/JakQuest/sim/mp2.sh` (port mapping off; nothing sent
   to the router).
4. `powershell -NoProfile -ExecutionPolicy Bypass -File C:\JakQuest\sim\pcvr_null_jak.ps1 -Game jak2 -Name null18 -Out
   C:\JakQuest\sim\s08` (item 4's `[openxr] session state` lines; steamvr.vrsettings is put back after).
5. `sh /c/JakQuest/sim/android_native_check.sh` (gk changed: jak_vr.cpp, jak_vr.h).
6. Docs: tests/README's "The checks added in 0.1.8" completed (batch40, batch31b, mp2, null18; the jump runs, vh, p2,
   fbc, fbc2 and rjs3 are in); LIST items 1 to 4 and 9 marked with how each was checked.
7. Privacy scan of the changed public files. Then ask the owner before publishing 0.1.8 (release steps: SESSION21,
   SESSION27 and SESSION28 handoffs; tools/make_patch.sh writes patches/0001-jak-quest-vr.patch from the submodule).

## Changed in this session (all uncommitted)

- jak_vr.cpp: fill_pad's left stick click held with the stick pushed past half its reach is no view switch and no
  Start (PadState menu_hold_pushed); FrameTag.credits and the eye passes skipped (eyes cleared black) while it is
  set, with a `[vr-view] the end credits ...` line as it starts and ends.
- jak_vr.h: kFlagCredits (1 << 22).
- jak_vr.cpp and xr_sim.cpp (build 118, test aid only): with JAKVR_SHADOW_EYES=1 the first six frames whose eyes part
  by over 2% of the picture are pictured by the simulated headset (`quest::sim_shot_apart`, `apart-<frame>-left.png`
  and `-right.png`), each with a `[vr] shadow in each eye apart: ...` line; nothing on the Quest or a real headset.
- goal_src/jak1/pc/pc-vr.gc: *vr-end-credits-frame* and kFlagCredits in vr-camera-update; the jump rise in
  vr-state-trace (*vr-jump-air*, *vr-jump-from*, *vr-jump-top*, *vr-jump-last*: a move of over 2 m in a frame is no
  jump).
- goal_src/jak1/levels/finalboss/sage-finalboss.gc: the credits loop sets *vr-end-credits-frame* each frame.
- goal_src/jak2/pc/pc-vr.gc and jak3/pc/pc-vr.gc: vr-pole-hold? #t in the pole's own states; Jak II's vr-test-warp!
  puts Jak a metre over a swing pole for `JAKVR_TEST_WARP=swingpole`; the jump rise in vr-state-trace (as Jak 1's).
- goal_src/jak{1,2,3}/engine/target/target.gc mod-var-jump: the frame that steps past the 0.1 s A-hold window counts
  as its end when the frame before was short of it (*display* old-base-frame-counter in Jak 1, the clock's
  old-frame-counter in Jak II and 3).
- goal_src/jak{1,2,3}/engine/collide/collide-shape.gc integrate-and-collide! (control-info): the jump animation's
  offset change times the game's frames a second (Jak 1 *display* frames-per-second, twice; Jak II and 3 the clock's)
  in place of a fixed 60.
- tests: jak1_view_hold.sim, jak2_pole.sim, jak1_double_jump.sim, jak2_double_jump.sim (new); README rows (swingpole,
  the jump rise, JAKVR_TEST_CREDITS) and bullets (the credits, the view held, the pole, the jump); a stray line from
  an older edit put back into its rj3 bullet.
- docs: LIST-0.1.8.md (items 5, 6, 9 to 15), HEADSET_CHECKLIST.md (the credits, the view held in a fight, off a swing
  pole, jumps at the headset's rate), CONTROLS.md (the hold needs the stick at rest), release-notes.md and
  launcher/common/texts/whats_new.txt (items 11 to 15 and the health log).
- C:\JakQuest\sim (scratch): build112.sh to build117.sh, batch43.sh to batch48.sh, gkcfg-60 (game-frame-rate 0 for
  Jak 1 and Jak II), tools/framediff.py and scrollstep.py.

## Rules that stay

- Break and regress nothing; everything goes in this release; new toggles on to start with plain names; multiplayer
  never changes a one-player game; every button reachable, no conflicts.
- Targeted sims only; one build or sim at a time. Every crash and close explains itself in the game's own log; never
  ask for Event Viewer. Resolving and checking is ours; never wait on the owner.
- Shell: never `python -` or other stdin scripts (write a file, `python -I file.py`; `python -E -P` for Pillow); LF
  scripts; Edit or sed for source edits; read before edit; never git stash; `${VAR:?}` in rm.
- No commit unless asked. Ratchet (C:\RACQuest) and HaloQuest are read-only. The testers' logs are read-only data
  (C:\JakQuest\downloads\tester-log-20261119-jak1-10 is the boss-fight one). Public notes name no player and never the
  tester's Windows user name.
