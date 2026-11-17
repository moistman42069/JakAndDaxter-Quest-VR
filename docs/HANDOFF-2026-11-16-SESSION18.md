# Handoff, 2026-11-16, Session 18

Read this first, then docs/HANDOFF-2026-11-16-SESSION17.md, SESSION16.md, SESSION15.md and HANDOFF-2026-11-16.md
"Session 14" (its rules still hold). The owner, this session: "was working in another session. pick up from exactly
where it was. forget/miss nothing" (Session 17's "Next"). Nothing of 0.1.3 is committed or published; PC dev builds
only.

## Done this session

- **Item 21, Jak 3's vehicles in first person:** new sim C:\JakQuest\sim\jak3_vehicle.sim (batch28c veh3; the continue
  point wasdoors-desert-turtle seats Jak in the Tough Puppy; A drives, the stick turns). b74 (the defaults): first
  person in the seat, the view in the seat 0.0 to 0.2 cm and 0.0 to 0.5 degrees a frame, no jumps, every picture drawn.
  b74v (C:\JakQuest\sim\gkcfg-vview: height 20, forward 20, tilt 15): the view 0.29 game metres higher and further
  forward (20 cm at life size), tipped 15 degrees down, hands on the bars. Item [x].
- **Item 4, Automatic's retry and stop:** tests/jak1_play_long.sim (batch28c autolong, b75): 60 for 45 s, the headset's
  rate tried again twice 45 s apart, late each time, then "60 a second until the next load" and no fourth try in the
  81 s left. Item [x] (a real headset's numbers go with items 5 to 8).
- **Item 1, found a fault and fixed it (build 61):** the calibration sim tests/jak1_ptr_settings_cal.sim (batch28c
  ptrcal1, b75) opened Jak 1's Game Options with the hand at rest; its dot lay on the bottom row shown (pitch 0 is that
  row), and the edge's auto-scroll moved the page on by itself to Back ("[vr-ptr] the page moves on a row: 6 ... 9 of
  10" with no hand move), so A there went back out. Jak II's and Jak 3's long settings pages (progress-generic-draw-pc.gc)
  had the same. Now the edge's scroll runs only for the row the dot chose on that page (a move onto it, a click, or a
  move onto the arrows past the rows) and the rows the scroll moves on to: goal_src/jak1/pc/progress-pc.gc
  (vr-ptr-edge-take!, vr-ptr-edge-mine?, the row and the arrows), goal_src/jak2 and jak3
  pc/progress/progress-generic-draw-pc.gc (vr-ptr-generic-take!, vr-ptr-generic-mine?). The calibration found each row
  about 5 degrees of hand tilt apart and the aims match the formula in the sims' notes.
- **Item 1, more (build 62) and every kind of row in pictures:** Jak 1's list rows (language, frame rate, resolution...)
  draw their own carousel and leave the row's text empty, so draw-options offered them no pointer while being changed
  (b76 ptrset1: "found no row"); now the row being changed is always offered. b77 (ptrset1/2/3, new
  tests/jak2_ptr_settings.sim and tests/jak3_ptr_settings.sim): On / Off, lists and sliders by the pointer in all three
  games, each step and "done" logged, none "found no row". Item [x]; LIST item 1 "Found more (Session 18)".
- **Item 20, the lesson's pass, [x]:** the trigger must come inside the 0.7 s after the punch's hit (the sim taps it six
  times); the dummies stand 20 and 27 m from the start, so the sims' walks (b79 overshot, b80 short) never brought a
  punch to one. Build 65 adds to the JAKVR_TEST_COMBO test aid (gun-dummy.gc): Jak set 1.4 m before dummy 0 as the
  lesson opens. **b81: "the lesson passed (sigc080)"**: the punch broke dummy 0, R1 50 ticks later took dummy 1 (10 m
  off, the hand turned 60 degrees away) as the combo's target and its shot broke it. Test aids (all under flag 2048):
  target-gun.gc (each R1, the combo's target), gun-dummy.gc (hits, where each stands, the placing), gungame-obs.gc
  (passed or not).
- **Item 18, the owner later:** "i didnt know the jak 2 light billboards actually just have that flicker effect on them
  natively". Nothing to undo (item 18 only made depth finer, the eyes' near plane); whats_new and the notes now say the
  city's lit signs flicker by design.
- **Item 29 (new), texture packs (the owner: people download packs and cannot find them in the launcher; "add a way to
  import texture packs (compatible ones)"; then "add a seperate section in the launcher for texture packs"):** both
  launchers have a **Texture packs** tab (between Controls and Mods) with **Import a .zip** and **Import a folder** (PC:
  drop either on the window too), each game's packs (On / Off, Up / Down, Remove, Update, rebuild), and **Find texture
  packs** (the lists' packs; Browse mods keeps the rest). launcher/common TexturePacks.Stager: a zip in the zip, folders,
  the game from the pack's folders or metadata.json (else the page's game, or PC asks), packs zipped from inside
  texture_replacements; plain refusals for .7z/.rar, PCSX2 packs, photos, non-zips. Quest: a texture pack given to
  Import a mod goes in as one; tab_texture_packs in all six languages. TexturePacksTest 37 checks PASS (14 new), the
  other launcher tests pass, the PC launcher -Xlint:all -Werror, its tabs drawn by --render, the Quest's Java
  type-checks. Texts: whats_new.txt, NOTES-0.1.3-DRAFT.md, docs/MODS.md, docs/HEADSET_CHECKLIST.md, tests/README.md.
- **make_patch.sh** run at the end: patches/0001 2966 files (33.4 MB), 0002 8 files.

## Builds and sims this session

- Build 61 (C:\JakQuest\sim\build061.sh): Jak 1, II and 3 GOAL (the menus' edge scroll). Build 62: Jak 1 (the list rows
  offered). Builds 63, 64, 65: Jak II (the combo test aids, the dummies' places, Jak placed at dummy 0). gk is still
  build 60's (no C++ change this session).
- b74 veh3, b74v veh3 (gkcfg-vview), b75 ptrcal1 + autolong + combopass (gk 60, build 60 GOAL), b76 (build 61):
  ptrset1 + scroll1 + scroll2 + combopass + ptr3; b77 (builds 62/63) ptrset1 + ptrset2 + ptrset3; b78 (build 64), b79,
  b80, b81 (build 65) combopass.
- New: tests/jak1_ptr_settings.sim (batch28c ptrset1, on its own settings copy C:\JakQuest\sim\gkcfg-ptrset since it
  changes them), tests/jak2_ptr_settings.sim, tests/jak3_ptr_settings.sim (ptrset2, ptrset3; their shared body
  C:\JakQuest\sim\ps_body.txt), tests/jak1_ptr_settings_cal.sim, tests/jak1_play_long.sim,
  C:\JakQuest\sim\jak3_vehicle.sim, jak2_combo_pass.sim (b75, b78, b79, b80 versions kept beside it).
- Java: C:\JakQuest\sim\javatest016 (the four launcher tests), C:\JakQuest\sim\javac-out (the PC launcher),
  C:\JakQuest\sim\android_check (the Quest app's Java, stubs remade), C:\JakQuest\sim\renderhome (sample packs for
  --render: JAKQUESTVR_HOME).

## 0.1.3 released, 2026-11-16

The owner: "i want you to work through EVERYTHING and just publish it as a new release". Steps as 0.1.1's:

- VERSION 0.1.3 (PcBuild.java too; Jak II's *mod-version-text* already "0.3.1-jakvr.0.1.3"). docs/release-notes.md:
  0.1.3's banner, downloads, Updating, What's new (from NOTES-0.1.3-DRAFT.md, now moved out of the repo to
  C:\JakQuest\sim\edits013), Known limits and Thanks; 0.1.2's and 0.1.1's notes kept below (all three out the same
  day). README's banner, CREDITS.md (himstan's Jak II multiplayer, miniupnpc and libnatpmp), LIST-0.1.3's top.
- **Corrected:** the texts said the PC now builds again only what changed. Not for this update: a 0.1.2 ready mark has
  no compiler line (PcSetup sameCompiler), so from 0.1.2 each game is built whole once; only the updates after 0.1.3
  build what changed. whats_new and the notes say so.
- Private details: one handoff line had the session scratchpad's full path (the user folder); now "the session
  scratchpad's sheet\sheet.ps1". Nothing else found (player names in code comments and lists were public before).
- build_apk.sh (C:\JakQuest\sim\apk_build_013.log, 13 min): JakQuestVR-0.1.3.apk, versionCode 10399, signed with 0.1.0's
  key (certificate 66390b53...d59d52a), toolchain.sha256 219b3f5c...4893 (new: the Quest prepares each game again),
  SHA-256 97f7ce544ad496bd5c6fc66d2148df21825b7a5f80b25a8ff81d25184b8d3073.
- `python -I tools/build_pc_release.py` (C:\JakQuest\sim\pc_release_013.log): self-test OK, gk of build 60, game code
  75c68a17...fb1e (pc/game-code.txt), JakQuestVR-0.1.3-windows.zip SHA-256
  25ca58e6371a0a6d53bd7e3b9ced64ee04b2d553db47da0c198cdba2a56997fb. out/release-0.1.3 with SHA256SUMS.txt.
- make_patch.sh: 2966 files, 33,379,430 bytes (as before the builds). Commit, push, `gh release create v0.1.3`.
- **Published:** https://github.com/moistman42069/JakAndDaxter-Quest-VR/releases/tag/v0.1.3 ("Jak and Daxter VR
  0.1.3", latest, not a prerelease, body docs/release-notes.md; the APK, the Windows zip and SHA256SUMS.txt, GitHub's
  digests the same as above). Commit 058c500 on main, pushed.

## Next, in order

1. Items 5 to 8 need the headset (Show performance's numbers, dynamic resolution, multiview, Max draw distance's cost
   on the Quest), with docs/HEADSET_CHECKLIST.md (its new Texture packs part too: import from Downloads, a folder, a
   pack for another game, Import a mod given a pack). Carried to the next list; in 0.1.3's Known limits.
2. Item 24 ("intro for jak 3 was upside down") stays open: not reproduced on the sim; the log line for an upside-down
   still view is in. Look again only with a new report or log.
3. Players' reports and logs on 0.1.3, as they come (Vixel's Discord only when the owner says), then the next list.
4. Cleanup, shown to the owner, nothing deleted (their go first): C:\JakQuest\sim b44 to b81 pictures (about 21 GB) and
   b70's crash dumps; javatest012 to 016; mp_stage (31 MB), mp_backup (20 MB); C:\JakQuest\downloads\jak2-multiplayer
   (196 MB); the gkcfg-* copies; *.b63, *.b75, *.b78, *.b79, *.b80 sim copies, batch28c.sh.b64, ps_body.txt (keep while
   ptrset2/3 are used), TexturePacks.java.b81; build056 to build065 scripts, mp2d.sh; renderhome, javac-out.
