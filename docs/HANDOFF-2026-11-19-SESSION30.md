# Handoff, 2026-11-19, Session 30

Read this first, then docs/LIST-0.1.8.md. This session picked up from docs/HANDOFF-2026-11-19-SESSION29.md and stopped
on the owner's "due to week usage, just wrap up and do a handoff". 0.1.7 is the latest release. 0.1.8 is NOT published,
and publishing has not been asked for: finish the list, then ask the owner. Nothing is committed: all of 0.1.8 is
uncommitted in this repo and in the Precursor submodule.

Nothing is running: every batch, build, watch and gk.exe was stopped at the wrap-up. Still, check first:
`tasklist | grep -iE "gk.exe|goalc|extractor"`.

## Where the list stands

- Items 7 and 8: [x] (item 8, Where your files are kept, was finished, compiled and checked before this session's
  part: DataFolderTest 50 ok, VrOptionsTest 25 ok).
- Items 1 to 4: coded and built (gk build 100 on). Their sims are NOT run yet (batch31b, mp2, null18 below).
- Item 6 (Steer by grabbing the wheel): coded, built, and checked by sims for most vehicles (results below). Jak 3's
  glider, sled and hellcat still need their aligned runs (rw3g3, rw3s3, rw3f3).
- Item 5 (Hold the gun while driving): the gun is drawn, fired and put away seated by the log, and it is Jak's gun, not
  Daxter's (build 103). The pictures do NOT show it in the hand yet: the game's seated animation holds the gun about
  1.29 m from his hand. The fix is written (build 105, below) but NOT built.

## What is built, and what the source has that is not

- gk: build 103. Jak II and Jak 3 GOAL: build 104. Jak 1 GOAL: build 101.
- The source is ahead of that (build 105, NOT built; C:\JakQuest\sim\build105.sh builds gk, then Jak II's and Jak 3's
  GOAL):
  - jak_vr.cpp: `ride_gun_hold()` (after mat_place, about line 3318), and the carry in "7. Jak II and 3's gun" (about
    line 5655): seated with Hold the gun while driving applying (`s_pad.ride_gun_here` and no kFlagFirstPersonOk), the
    gun is carried by the right hand with the hold his gun stance gives it on foot. The hold is kept from the last frame
    on foot (`[vr-body] the gun's hold on foot kept (N m from his hand), for a seat: 9 numbers / tx ty tz`, logged once),
    or before any, `ride_gun_hold()`, which is a PLACEHOLDER (-0.02, -0.21, -0.20 m, no turn) until measured. Logs
    `[vr-body] Hold the gun while driving: the gun held as on foot (...)` once.
  - target-gun.gc (Jak II and 3): build 104's right-arm mask (Jak II's set-gun-joint-mask! branch, Jak 3's
    vr-ride-gun-mask!) is taken out again (sim rg2c: it moved the hand, not the gun). Kept: `*vr-ride-gun?*` and the
    gun-daxter condition (build 103).
- Jak 3's pc-vr.gc has the seated mark (build 104): when a test continue seats Jak (`focus-test? pilot-riding`) it logs
  `[vr] test: seated by the continue, the set-up done` and calls `pc-vr-test-ready!`, so QUEST_SIM_ALIGN=10000 counts
  the seated lines from there.

## Sim results so far (C:\JakQuest\sim\s08; batch scripts in C:\JakQuest\sim)

- batch31a (builds 100/101): wh2b, Jak II's city bike (handlebars): 27° gives 0.73, full lock about 0.98, back to 0 at
  once. wh2c, the city car (a wheel): 27° gives 0.43. wh1, Jak 1's Zoomer: 27° gives 0.65. rg2: the gun drawn and fired
  seated but never in the hand (gun-daxter, fixed in build 103). rw3g/rw3s/rw3f: the grips came before Jak was seated
  (held nothing, as they should); rw3f's factoryd-start was the Titan suit (factoryb-start is the hellcat).
- batch32 (build 103): rg2b: "in hand 1", but the gun 1.29 m from the hand. rg3: the pickup continue never seated Jak.
  wh3b, Jak 3's buggy: a quick squeeze gives R2, squeezed again and held gives R2 held, the right stick's click with
  that hand on the wheel gives R2, 29° gives 0.43, grips before seating ignored. rw3g2/rw3s2/rw3f2: the seating
  frame varies, so the lines missed (hence the seated mark and alignment in build 104).
- batch33 (build 104): rg2c: the arm mask did not bring the gun to the hand (taken out again). rg3b: the hijacked
  pickup's continue (ctyport-hijack-decoy) loads the city but leaves Jak standing at the port, even after 36000 frames
  (600 s watchdog). So the pickup is a headset check only (HEADSET_CHECKLIST says so) and the sims use the glider.
  wh3c: stopped by the wrap-up at 901 lines, after `test: continue "wasdoors-desert-turtle" "found, starting there"`
  and before any seated mark: NOT concluded. rw3g3, rw3s3, rw3f3: not run.

## Next, in this order (one build or sim at a time)

1. `sh /c/JakQuest/sim/build105.sh > /c/JakQuest/sim/build105.out 2>&1` (in the background). Look for `gk BUILD OK`
   and two `Successfully built` lines (Jak II and Jak 3).
2. `RUNS="wh3c rw3g3 rw3s3 rw3f3" sh /c/JakQuest/sim/batch33.sh > /c/JakQuest/sim/s08/batch33b.txt 2>&1`. Expect
   `[vr] test: seated by the continue`, `the wheel held by ... (a wheel | handlebars or a bar ...)` (glider: handlebars
   or a bar; sled and hellcat: a wheel) and `the wheel: turned ... steering ...` lines. If wh3c never logs the seated
   mark before its watchdog, the desert's seat is not caught by vr-test-continue!'s `pilot-riding` check (wh3b on build
   103 did seat him and steer): look at what the buggy's seat sets, fix the mark, and rerun.
3. `sh /c/JakQuest/sim/batch34.sh > /c/JakQuest/sim/s08/batch34.txt 2>&1`: rg2d (Jak II bike, then on foot after
   getting off), gun3m (Jak 3 gun on foot), rg3c (Jak 3's glider with Hold the gun while driving). From rg2d and gun3m
   take the `[vr-body] the gun's hold on foot kept ...` numbers (rows 0-2 then the translation, game units, 4096 a
   metre) and put them in `ride_gun_hold()` per game (`s_game == (int)GameVersion::Jak2` / `Jak3`), replacing the
   PLACEHOLDER and its comment.
4. Build 106 (gk only: `cmd //c "tools\build_pc.bat gk"` as build105.sh does), then rerun rg2d and rg3c and look at
   the seated shots (rg-*/rg3-* pictures in each run folder): the gun in the right hand. Delete the BMPs after (15 MB
   each).
5. If rg3c works on the glider, the release notes' and whats_new.txt's "(Jak 3 also the hijacked pickup)" become
   "(Jak 3 also the glider and the hijacked pickup)".
6. `sh /c/JakQuest/sim/batch31b.sh`: ps2 (one `[vr-mem]` line only), pmp3, boat2 (no "streamed animation" line), hol3,
   race3, board3, zoom3, buggy4.
7. The Jak II LAN check: `OUT=s08/mp2 GUEST_SIM=/c/JakQuest/sim/mp2_guest_pmp.sim sh /c/JakQuest/sim/mp2.sh` (port
   mapping off; nothing sent to the router).
8. The SteamVR null-driver run for item 4's `[openxr] session state` lines: `powershell -NoProfile -ExecutionPolicy
   Bypass -File C:\JakQuest\sim\pcvr_null_jak.ps1 -Game jak2 -Name null18 -Out C:\JakQuest\sim\s08`.
9. The ARM64 check: `sh /c/JakQuest/sim/android_native_check.sh` (gk changed).
10. Docs:
    - tests/README "## The checks added in 0.1.8" before "## Without a game": runs in s08, builds 100 to 106, the new
      sims (jak1_wheel, jak2_wheel, jak2_ride_gun, jak2_pause_soak, jak3_wheel, jak3_ride_wheel, jak3_ride_gun) and
      the results above;
    - LIST items 1 to 6 marked [x] with how each was checked. Item 5's last paragraph already describes build 105's
      runtime hold and names sims rg2d and gun3m as the measure: make it true (step 3) or correct it;
    - release notes and whats_new.txt adjusted if anything changed.
11. Privacy scan of the changed public files (this session's scan: the only find, a scratch path naming another
    project in tests/README, is fixed). No tester username, no owner path or email, no new player names.
12. Ask the owner before publishing 0.1.8. Release steps: SESSION21, SESSION27 and SESSION28 handoffs (VERSION,
    PcBuild.java, jak2 mod-settings.gc "0.3.1-jakvr.0.1.8", the APK, the PC zip, SHA256SUMS, the tag).

## Changed in this session's part (all uncommitted)

- C:\JakQuest\sim\batch34.sh: rg3c added (the glider, WD=900); its log filter fixed.
- tests/jak3_ride_wheel.sim and tests/jak3_ride_gun.sim: headers say the seated lines count from the seated mark
  (QUEST_SIM_ALIGN=10000); jak3_ride_gun.sim is now run with hanga-start (the glider).
- docs/LIST-0.1.8.md item 5: build 104's arm mask marked as tried and taken out; build 105's runtime hold described.
- docs/HEADSET_CHECKLIST.md: the gun-while-driving check names Jak 3's glider and pickup (the pickup only there).
- tests/README.md: QUEST_SIM_ALIGN's row names Jak 3's seated mark; DataFolderTest's run names "a scratch folder on D:".

## Rules that stay

- Break and regress nothing; everything goes in this release; new toggles on to start with plain names; multiplayer
  never changes a one-player game; every button reachable, no conflicts.
- Targeted sims only; one build or sim at a time. Every crash and close explains itself in the game's own log; never
  ask for Event Viewer. Resolving and checking is ours; never wait on the owner.
- Shell: never `python -` or other stdin scripts (write a file, `python -I file.py`); LF scripts; read before edit;
  never git stash; `${VAR:?}` in rm. A stopped background batch can leave its sh and gk.exe running: check `ps -ef`
  and tasklist after stopping one.
- No commit unless asked. Ratchet (C:\RACQuest) and HaloQuest are read-only. The tester's logs are read-only data.
  Public notes name no player and never the tester's Windows user name.
