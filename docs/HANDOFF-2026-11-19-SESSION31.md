# Handoff, 2026-11-19, Session 31

Read this first, then docs/LIST-0.1.8.md. This session picked up from docs/HANDOFF-2026-11-19-SESSION30.md. 0.1.7 is
the latest release. 0.1.8 is NOT published, and publishing has not been asked for: finish the list, then ask the owner.
Nothing is committed: all of 0.1.8 is uncommitted in this repo and in the Precursor submodule.

Check first that nothing is still running: `tasklist | grep -iE "gk.exe|goalc|extractor"` and `ps -ef | grep -E
"batch|run_sim|sleep"` (a stopped batch leaves its watchdog subshell, which can taskkill a later run's gk.exe; the
`sleep 900` leftovers alone are harmless).

## Built now

- gk: build 109. Jak 3 GOAL: 106. Jak II GOAL: 105. Jak 1 GOAL: 101. The source matches the builds.

## Where the list stands

- Items 7 and 8: [x].
- Item 5 (Hold the gun while driving): the on-foot hold measured (sims gun2m, gun3m on build 106: the same in both
  games to 1 mm) and in `ride_gun_hold()` (jak_vr.cpp, build 107). Still to look at: the seated pictures in rg2e (Jak
  II's bike) and rg3c (Jak 3's glider), the gun in the right hand.
- Item 6 (Steer by grabbing the wheel): checked for most vehicles (tests/README "The checks added in 0.1.8"); wh3c and
  Jak 3's glider, sled and hellcat (rw3g3, rw3s3, rw3f3) in batch37b.
- Items 1 to 4: coded and built; their sims (batch31b, mp2, null18) and the ARM64 check are still to run, after
  batch37b.
- Item 9 (the shadows in each eye, a tester's "rendered differently for each eye"): nothing different found yet. Jak
  1, sh1b on build 107 with the new test aid `JAKVR_SHADOW_EYES=1`: the two eyes' shadow cover the same to 0.07% of
  the picture every 600 frames while the head turned and nodded and Jak walked, no frame apart. Jak II and 3 (sh2,
  sh3) in batch37b. If they match too: say honestly that the sims find no difference, keep the headset check
  (HEADSET_CHECKLIST "Shadows in each eye") and ask the tester which shadows and where.
- Item 10 (a tester's Jak 1 log: the body's guard 1,734 times, most in the citadel): the cause found, the game sending
  his skeleton for a frame at 0.998 to 0.9998 of its place from the world's origin (metres in the far levels). The fix
  (jak_vr.cpp, before the guard; build 108, its log lines in 109) takes his bones near his main joint back out along
  that line when his neck still sits on his main joint. rjc3 (build 107) confirmed his whole spine moves with his main
  joint at each jump. Still to see: rjc4 (citadel) and rj3b (crater) on build 109, `[vr-body] the game's skeleton
  sent at ... taken back out along that line` and no `left his root` lines after them; the first lines' "N m up (M at
  that share of his height)" says whether his height moved by the same share (the fix assumes so).

## Running at the end of this file's last update

- C:\JakQuest\sim\s08\batch37b.txt: `RUNS="rjc4 sh2 sh3 rg2e rg3c wh3c rw3g3 rw3s3 rw3f3 rj3b" sh
  /c/JakQuest/sim/batch37.sh` on build 109.

## Next, in this order (one build or sim at a time)

1. Read batch37b (each run's lines are in batch37b.txt, the full logs in s08/<name>/run.log; the pictures are PNGs).
2. If rg3c held the gun on the glider: the release notes' and whats_new.txt's "(Jak 3 also the hijacked pickup)"
   become "(Jak 3 also the glider and the hijacked pickup)".
3. `sh /c/JakQuest/sim/batch31b.sh > /c/JakQuest/sim/s08/batch31b.txt 2>&1`: ps2 (one `[vr-mem]` line only), pmp3,
   boat2 (no "streamed animation" line), hol3, race3, board3, zoom3, buggy4.
4. `OUT=s08/mp2 GUEST_SIM=/c/JakQuest/sim/mp2_guest_pmp.sim sh /c/JakQuest/sim/mp2.sh` (port mapping off; nothing sent
   to the router).
5. `powershell -NoProfile -ExecutionPolicy Bypass -File C:\JakQuest\sim\pcvr_null_jak.ps1 -Game jak2 -Name null18 -Out
   C:\JakQuest\sim\s08` (item 4's `[openxr] session state` lines; steamvr.vrsettings is put back after).
6. `sh /c/JakQuest/sim/android_native_check.sh` (gk changed).
7. Docs: tests/README's "The checks added in 0.1.8" (written up to rjc3) completed with the rest; LIST items 1 to 6,
   9 and 10 marked [x] with how each was checked; the release notes and whats_new.txt (item 10's lines are written;
   item 9's per the results).
8. Privacy scan of the changed public files (this session's: clean). Then ask the owner before publishing 0.1.8
   (release steps: SESSION21, SESSION27 and SESSION28 handoffs).

## Changed in this session (all uncommitted)

- third_party/Precursor/game/vr/xr_sim.cpp: `head_yaw_rate`, `head_pitch_rate` (degrees a second) for the sims.
- jak_vr.h/.cpp: `shadow_eyes_measure()`, `shadow_eyes_note()` (the JAKVR_SHADOW_EYES=1 test aid, PC only);
  `ride_gun_hold()` measured; item 10's fix before the skeleton guard (s_body's jump_* and origin_* fields).
- ShadowRenderer.cpp (Jak 1) and foreground/Shadow2.cpp (Jak II and 3, not on the Quest): each eye's shadow counted
  for the test aid.
- tests/jak1_shadow_eyes.sim (new header, the sweep), jak2_shadow_eyes.sim, jak3_shadow_eyes.sim, jak1_root_jumps.sim
  (new).
- tests/README.md: the JAKVR_SHADOW_EYES and head-rate rows, JAKVR_BODY_TRACE's jump line, "The checks added in
  0.1.8".
- docs/LIST-0.1.8.md: item 5's last paragraph; "Later reports" with items 9 and 10.
- docs/HEADSET_CHECKLIST.md: "Shadows in each eye", "Jak 1's body far from the world's middle".
- docs/release-notes.md, launcher/common/texts/whats_new.txt: item 10's Fixed line.
- C:\JakQuest\sim: batch35.sh to batch38.sh, build107.sh to build109.sh (scratch).

## Rules that stay

- Break and regress nothing; everything goes in this release; new toggles on to start with plain names; multiplayer
  never changes a one-player game; every button reachable, no conflicts.
- Targeted sims only; one build or sim at a time. Every crash and close explains itself in the game's own log; never
  ask for Event Viewer. Resolving and checking is ours; never wait on the owner.
- Shell: never `python -` or other stdin scripts (write a file, `python -I file.py`; `python -E -P` for Pillow); LF
  scripts; Edit or sed for source edits; read before edit; never git stash; `${VAR:?}` in rm.
- No commit unless asked. Ratchet (C:\RACQuest) and HaloQuest are read-only. The tester's logs are read-only data.
  Public notes name no player and never the tester's Windows user name.
