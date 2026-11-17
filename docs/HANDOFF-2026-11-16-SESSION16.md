# Handoff, 2026-11-16, Session 16

Read this first, then docs/HANDOFF-2026-11-16-SESSION15.md (its batch 63 results), then docs/HANDOFF-2026-11-16.md
"Session 14" (its rules still hold). The owner, this session: "was working in another session, proceed from exactly
where it left off. miss/forget nothing. proceed with list". Nothing of 0.1.3 is committed or published.

## Done this session

- **Jak II's title menu with Multiplayer on** (Session 15's open bug, LIST-0.1.3 item 15): the hidden Secrets row is
  found by name (goal_src/jak2/pc/progress/progress-static-pc.gc vr-title-secrets-index, vr-title-row-hidden?,
  vr-title-row-shift) in progress-generic-pc.gc (up and down skip it), progress-pc.gc (off it), progress-draw-pc.gc
  (no highlight on it) and engine/ui/progress/progress.gc (the pointer leaves it out; each row's box where it is
  drawn: Quit Game 45 up locked, 35 open; the title's rows 30 up open). tests/jak2_ptr_menu.sim needs no new pitches:
  its pitch 0 dot (y 298) is on Quit Game with the mod on (box 289-336) and off (257-304).
- **Which games join each other** (item 15): goal_src/jak2/engine/mods/mod-settings.gc *mod-version-text* is
  "0.3.1-jakvr.0.1.3" (was the placeholder, which made the identity "dev-" + Precursor's git commit: the Quest's build
  has no .git, so no identity there, and the PC's could differ). tools/build_pc_release.py's version() stops a release
  whose identity does not end in -jakvr.<VERSION>: **at release, bump it with VERSION.**
- Docs: LIST item 15 (both of the above), NOTES-0.1.3-DRAFT.md and whats_new (the Zoomer fix, the title fix).
  **C:\JakQuest\sim\edits013\whats_new_013.txt is now folded into the top of launcher/common/texts/whats_new.txt**:
  edit whats_new.txt from now on, not the draft.
- Build 56 (build056.sh): gk, goalc and the extractor OK (the crosshair's C++ compiled). The games' GOAL stopped on
  kernel-defs.gc's `(function vector int int)` for pc-vr-crosshair! (vector is unknown that early): now `structure`
  in jak2 and jak3. build056b.sh compiled the two games again: Jak II 1171, Jak 3 1165 targets, no errors. Patch
  written (make_patch.sh, 2966 files).
- Batch 64's first part started (b64a.out).

## Next, in order

1. (done: build 56, the patch)
2. Batch 64: `OUT=b64 SIMS="cross2 cross3 crossc2 crossd2 combo2 dark2 eyes3 turret3 auto120 zoomst zoomfpv ptr2" sh
   /c/JakQuest/sim/batch28c.sh > /c/JakQuest/sim/b64a.out 2>&1`, then `OUT=b64 O_CFG=gkcfg-mpoff SIMS="open2 ptr2"`
   (into b64b.out), then `OUT=b64 sh /c/JakQuest/sim/mp2.sh > /c/JakQuest/sim/b64mp.out`. Look at: the crosshair in both
   eyes and "on an enemy", Dark Jak's arms, Jak 3's temple eyes and turret view, the Zoomer steady ("a jump of" lines
   gone), ptr2 (the dot on Quit Game, no memory card notice), the two games joined (and the title's version text).
3. LIST-0.1.3: write the results (items 1, 2, 4, 5, 6, 7, 8, 15, 16, 19, 20, 21, 22, 23, 26, 27), fix what fails.
4. `sh /c/JakQuest/sim/android_native_check.sh` once after the last C++ change (it resumes; counts as a build).
5. The cleanup list for the owner, shown, not deleted: C:\JakQuest\sim b44 to b64 pictures, javatest012/013/014,
   mp_stage, mp_backup, the Jak II mod clone (C:\JakQuest\downloads\jak2-multiplayer), the gkcfg-* copies, *.b63 sim
   backups, build056b.sh.
