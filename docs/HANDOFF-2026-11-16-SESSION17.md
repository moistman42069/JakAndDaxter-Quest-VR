# Handoff, 2026-11-16, Session 17

Read this first, then docs/HANDOFF-2026-11-16-SESSION16.md, SESSION15.md and HANDOFF-2026-11-16.md "Session 14" (its
rules still hold). The owner, this session: "was working in another session, proceed from exactly where it left off.
miss/forget nothing. proceed with list", then a player's question to see about: "Is there a way to configure where the
game data installs? ... Is there a way we choose another drive?" (LIST-0.1.3 item 28). Nothing of 0.1.3 is committed
or published; PC dev builds only.

## Done this session

- **Item 28, the games on another drive (PC launcher):** the Play tab's "Where the games are kept" card: the folder,
  its drive's free space, Move to another drive or folder..., Back to the default place, Open the folder; the move on
  its own thread with a bar and Stop (pc/launcher/src/dev/jakquest/vr/PcDataFolder.java, PcPaths.chooseData /
  data-folder.txt, PcLauncher.java; disabled buttons look disabled everywhere: PcWidgets.Btn).
  tests/java/dev/jakquest/vr/DataFolderTest.java: 32 checks (C:\JakQuest\sim\javatest015). Docs: LIST item 28,
  pc/README.txt, tests/README.md, NOTES draft, whats_new. Not tried: the real folder picker dialog by hand.
- **Item 15, Jak II's title:** b65 ptr2 (Multiplayer on, five rows) and b65b ptr2off (off, four rows) pass; both
  pointer sims retuned (tests/jak2_ptr_menu.sim, tests/jak2_ptr_menu_mpoff.sim; batch28c ptr2 takes O_CFG, new
  ptr2off).
- **Item 16, Jak 3's turret:** the turret sim reaches the turret now (test aid JAKVR_TEST_TURRETS=1 in Jak 3,
  pc-vr.gc vr-test-turret!, build 57). Found the view sinking 2 m in 20 s (the turret hides Jak; his undrawn neck
  sinks). Fixed in Jak 3 pc-vr.gc (build 58b): hidden in his turret states, his eyes stay at 2.4 m over where he
  stands. b68 turret3: steady, over the barrels. Item [x]; NOTES draft and whats_new have it.
- **Item 27, crosshair red:** never seen because no sim aimed at an enemy (b67's trace: the walls). Build 58 adds the
  tracked target to the enemy test (as the laser) and the trace "[vr] crosshair: on ..." (JAKVR_STATE_TRACE). New sim
  C:\JakQuest\sim\jak2_cross_dummy.sim (batch28c crossdum2): red on the gun course's gun-dummy-a in log and pictures.
  Item [x].
- LIST-0.1.3: results written for items 1, 2, 4, 5, 6, 7, 8, 15, 16, 19, 20, 21, 22, 23, 26, 27; marked [x]: 2, 16,
  19, 22, 23, 26, 27 (each with what was checked).

- **Item 15, the two Jak II games joined (mp2.sh):** b69: both games crashed (segfault) the moment the guest joined.
  b70 (C:\JakQuest\sim\mp2d.sh: mp2.sh with JAKVR_CRASH_DUMP per game) dumped both: the same place in each, rip at EE
  offset 0x1F057A0, a read at address 0. Named by C:\JakQuest\sim\tools\symfind.py (`python -I symfind.py ee.bin s7
  addr...`: the symbol table read from ee.bin, which starts at 512 KB; Jak II's s7 is r14 in crash.txt, here
  0x187e01; addr = rip or a stack word minus the ee base): our Jak II pc-vr.gc vr-merc-others! + 0x9c, called from
  vr-body-update!, vr-camera-update, update-camera. Disassembled (tools\eecut.py cuts the bytes; a .s with
  `.incbin`, clang -c --target=x86_64-pc-linux-gnu, llvm-objdump -d): handle->process of the other player's record's runtime target-handle, which is 0 (not #f)
  as he joins, before his Jak exists. Fixed (build 59, Jak II GOAL): vr-merc-others! and vr-others-pose! find the
  other Jak by the mod's own mp-player-record-target (handle->process-safe), and his Daxter pointer 0 is passed over.
  b71: joined (TCP, then UDP both ways), both to the end, exit 0; the host sees the guest's Jak 32 m off after his walk
  (mph-3), gone after he quits (mph-after). b72 (mp2_guest.sim now looks back after the walk): the guest sees the
  host's Jak. Item 15 [x]. (Jak 1's and Jak 3's lookups checked: their handles come from live processes or are
  guarded already.)

## Builds and sims this session

- Build 57 (C:\JakQuest\sim\build057.sh): Jak 3 GOAL (the turret test aid). Build 58 (build058.sh): Jak II and 3
  GOAL (crosshair). Build 58b (build058b.sh): Jak 3 GOAL (the turret eyes). gk is still build 56's (no C++ changed
  this session; kmachine.cpp only in a comment). The patch (make_patch.sh) rewritten after build 58b.
- Build 59 (build059.sh): Jak II GOAL (pc-vr.gc: the other players' Jaks by the safe lookup). Build 60
  (build060.sh): PC gk (EyeRenderer.h). make_patch.sh rewritten after build 60 (2966 files, both fixes in it).
- b66 turret3, b66r (ride trace), b66t (gkcfg-rides3p: First person in vehicles off, the game's own camera), b67
  turret3 + crossc2, b68 turret3 + crossdum2, b69 mp2 (crashed), b70 mp2d (crash dumps), b71, b72 and b73 (gk 60) mp2d (joined).
- New sim configs: C:\JakQuest\sim\gkcfg-rides3p (Jak 3 "first-person-vehicles-on off"; note the 0.1.3 key is
  first-person-vehicles-on, the old first-person-vehicles is not read).

## Next, in order

1. (done: b69 to b72 mp2, item 15 [x]; build 59)
2. make_patch.sh again if anything changes after this session's last run (see "Builds and sims").
3. (done: android_native_check.sh. It stopped in EyeRenderer.h, the Jak II mod's per-player eye textures made with
   GL_UNSIGNED_INT_8_8_8_8_REV, which GLES has not; now GL_UNSIGNED_BYTE as the game's own eye textures; libmain.so
   builds and links. PC gk rebuilt as build 60.)
4. Still open in the list: item 1 (each kind of settings row by the pointer, in pictures), 4 (Automatic's 45 s
   retry), 5-8 (the Quest's own numbers need the headset), 20 (the lesson's pass), 24 (not reproduced),
   21 (Jak 3's vehicles).
5. The cleanup list for the owner (shown, nothing deleted without their go): C:\JakQuest\sim b44 to b72 pictures
   (about 18 GB; b70's two crash dumps are 128 MB each), javatest012 to 015, mp_stage (31 MB), mp_backup (20 MB),
   the Jak II mod clone C:\JakQuest\downloads\jak2-multiplayer (196 MB), the gkcfg-* copies, the *.b63 sim backups,
   batch28c.sh.b64, build056b.sh, build057.sh, build058.sh, build058b.sh, build059.sh, mp2d.sh.
