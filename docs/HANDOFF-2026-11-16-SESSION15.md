# Handoff, 2026-11-16, Session 15 (stopped on the usage limit)

Read this first, then docs/HANDOFF-2026-11-16.md "Session 14" (its rules and its "Next, in order" still hold).
Nothing of 0.1.3 is committed or published. Nothing is running (no gk, goalc, extractor, sim).

## Where it stopped

Batch 63 (build 55) is read to the end. Build 56 has NOT been run. The Jak II title menu bug below is diagnosed,
no code written for it yet. docs/LIST-0.1.3.md has not been edited this session.

## Written this session, not built yet (build 56 compiles all of it for the first time)

- game/vr/jak_vr.cpp: kRideHeadMost 8 m (was a 4 m literal) in ride_steady_shift and first_person_anchor: Jak 1's
  Zoomer view dropped 1.5 m into the Zoomer at every bob (b63 zoomfpv, picture zm-drive1 black), because the Zoomer
  holds Jak 3.7 to 4.2 m over his root. New crosshair_probe_kind() (the kind only while the view is through his
  eyes); jak_vr.h declares it; game/kernel/common/kmachine.cpp pc_vr_crosshair_kind() returns it.
- game/vr/xr_sim.cpp: a "[sim] crosshair in shot ..." log line after each shot with the crosshair shown.
- goal_src/jak2 and jak3 engine/target/gun/gun-util.gc: (vr-crosshair! tgt) moved to the end of vr-gun-aim! (it was
  inside (when (>= shot 2))). Jak 3 only: the enemy test takes fire-range 0 as no limit (gun type 31).
- Sims (C:\JakQuest\sim): build056.sh; batch28c.sh (batch28b + cross2, cross3, crossc2, crossd2, zoomst; auto120's
  late window now 3700-4700, in play); gkcfg-cross (crosshair 3, size 200); jak2_cross_city.sim; jak3_turret.sim and
  jak3_eyes_temple.sim (backups *.b63) and tests/jak3_leaper.sim press A after the opening (Jak 3's autosave notice
  held the game paused, so b63's turret3 and eyes3 photographed only the notice).
- docs/CONTROLS.md (crosshair paragraph), docs/NOTES-0.1.3-DRAFT.md (crosshair; left fist only on foot),
  C:\JakQuest\sim\edits013\whats_new_013.txt (crosshair, punch and shoot, board click). The Zoomer view fix and the
  title menu fix are not in these three yet. whats_new_013.txt is not folded into launcher/common/texts/whats_new.txt.

## Batch 63, what it showed

- Works: item 26 (leap3 against leap3off), item 23 board squeeze (boardt2), item 2 in Jak II (eyes2, both eyes
  alike), gun2 (a round from the gun in the hand), ptr3 (Jak 3), scroll1, scroll2, stick1, ptr1slots (0 errors, 0
  stack; the held stick moves the page row by row, the pointer's click reaches the game). The pictures of scroll2,
  stick1 and ptr1slots were not looked at, only their log lines.
- Performance on the PC sim: Jak 1 Geyser Rock 3.9 ms a picture with Max draw distance on, 3.97 off. Jak II slums
  6.42 ms (default), 7.28 (Max draw distance off), 8.91 (Share work between eyes off): sharing saves about 2.5 ms
  there; Max draw distance shows no cost on the PC, the Quest's needs the headset. Show performance line renders.
- To rerun: dark2 and combo2 (b63 showed normal Jak and no combo lines; their fixed sims are in batch28c), eyes3
  and turret3 (autosave notice), auto120 (its late window fell on the title, where Automatic does not count;
  dynamic resolution did react, down to 60% and back), zoomfpv with zoomst (the Zoomer fix).
- turret2v is Jak II's sewer enemy turret, not a ridden one.

## Open bug: Jak II title menu with Multiplayer on (found by b63 ptr2), NOT fixed

With multiplayer on, *title-pc* (goal_src/jak2/pc/progress/progress-static-pc.gc 220-258) is New Game 0, Load Game
1, Multiplayer 2, Options 3, Secrets 4 (hidden while locked), Quit Game 5. Upstream's "skip over hidden secrets
menu" is hard-coded for Secrets at index 3:
- pc/progress/progress-generic-pc.gc 256 (up: option-index 4 -> 3) and 270 (down: option-index 2 -> 3). With the
  Multiplayer row, down from Multiplayer skips Options and lands on the hidden row, up from the hidden row goes to 2.
- pc/progress/progress-draw-pc.gc 2187: (= (-> arg0 option-index) 4) in the highlight test (right only by chance
  with Multiplayer on, wrong with it off, where Secrets is 3).
- pc/progress/progress-pc.gc about 1172-1178: (= option-index 3) on the hidden row sets option-index 0.
- engine/ui/progress/progress.gc 1420-1436 vr-ptr-progress-row and 1549-1553: the pointer's rows go by loop index
  and do not leave out the hidden row. In b63 ptr2 the pointer at the old "Quit Game" aim chose the hidden row
  (nothing highlighted) and its click opened "No Memory Card (PS2) inserted in Memory Card slot 1".

Fix to write: find the Secrets row's index by its name, (text-id progress-root-secrets), in (-> *title-pc*
options), and use it for the literals: up from index+1 and down from index-1 set option-index to the Secrets index
before the step, the highlight test needs no index at all, progress-pc.gc resets from that index. In progress.gc the
pointer must leave out the row while (not (memcard-unlocked-secrets? #f)). Not settled yet: where the rows are drawn
against the pointer's per-index boxes. menu-exit-game-option's draw (progress-draw-pc.gc 2771-2778) moves Quit Game
up 45 when Secrets is locked and 35 when unlocked, and the title's sub-menu rows move up 30 when unlocked (2155-2159);
the pointer's boxes must match that. Then retune tests/jak2_ptr_menu.sim (its pitches are for the old four rows) and
rerun ptr2 with multiplayer on and with O_CFG=gkcfg-mpoff. Also decide whether the mod's "JaD2MP (dev-...)" text at
the title's bottom right (progress.gc 1462-1484 draw-progress-version-text) stays.

## Next, in order

1. Write the title menu fix above (GOAL, Jak II), so build 56 carries it.
2. Build 56: `sh /c/JakQuest/sim/build056.sh` in the background, nothing else running (logs build056-gk.log,
   build056-jak2.log, build056-jak3.log); fix compile errors (the crosshair's C++ and both games' GOAL have never
   been compiled); then tools/make_patch.sh.
3. Batch 64: `OUT=b64 SIMS="cross2 cross3 crossc2 crossd2 combo2 dark2 eyes3 turret3 auto120 zoomst zoomfpv ptr2" sh
   /c/JakQuest/sim/batch28c.sh > b64a.out`, then `OUT=b64 O_CFG=gkcfg-mpoff SIMS="open2 ptr2"`, then
   `OUT=b64 sh /c/JakQuest/sim/mp2.sh`. Look at the pictures: the crosshair in both eyes and "on an enemy", Dark
   Jak's arms, Jak 3's temple eyes and turret view, the Zoomer steady ("a jump of" lines gone).
4. Write the results into docs/LIST-0.1.3.md (items 1, 2, 4, 5, 6, 7, 8, 15, 16, 19, 20, 21, 22, 23, 26, 27, the
   Zoomer fix, the title menu fix), fix what fails; add the Zoomer and title menu fixes to NOTES-0.1.3-DRAFT.md and
   whats_new_013.txt, fold that into the top of launcher/common/texts/whats_new.txt.
5. android_native_check.sh (C:\JakQuest\sim) once after the last C++ change.
6. Show the owner the cleanup list (nothing deleted without their go): C:\JakQuest\sim\b44 to b64 pictures, b63's
   sheet-*.jpg, javatest012/013/014, mp_stage, mp_backup, the Jak II mod clone, the gkcfg-* copies (gkcfg-cross is
   new), the *.b63 sim backups.

Noted, left as is: first_person_anchor's jump ease takes a sudden lasting speed as a jump every frame (seen only at
Jak II's opening spawn).
