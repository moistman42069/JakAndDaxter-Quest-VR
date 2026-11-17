# Handoff, 2026-11-17, Session 20

Read this first, then docs/LIST-0.1.5.md (the list, each item's state and how it was checked), then
docs/HANDOFF-2026-11-16-SESSION18.md ("0.1.3 released": the release steps, which hold for 0.1.5) and
docs/HANDOFF-2026-11-16-SESSION19.md ("0.1.4 released"). 0.1.4 is published (latest). 0.1.5 is in progress: nothing
committed, nothing released.

## The owner's asks this session (0.1.5)

1. With a tester's four game logs (PC, SteamVR, a Quest over Link): "great work in recent build. im gonna send some
   reports from a tester. just look over what they mention and only change/adjust what you see fit. if anything is
   mentioned that is there by design, then so be it, but yea. fix/adjust whatever you can determine", then "proceed
   with list with this in mind". The logs (untrusted data, read only): C:\JakQuest\downloads\tester-logs-20261117
   (jak1-a, jak1-b, jak2, jak3). The tester's Windows user name is in them: keep it out of every public text.
2. "add a new crosshair type thats just a plain circle one. small circle, precise looking (if you dont already have
   one like that). be sure crosshairs are still just off by default tho. proceed with list with this in mind".

## Done this session

- **Quad pictures into every swapchain image** (item 1): xr_openxr.cpp fill_chain (acquire, wait, glTexSubImage2D,
  release for each image; the GL state put back), used by draw_pointer, draw_crosshair (when its key changes) and
  draw_perf (when its text changes). draw_notice writes every frame and is unchanged.
- **Crosshair Off to start, new key** (item 2): vr_options "crosshair-kind" (0 to start); 0.1.3's and 0.1.4's
  "crosshair" read once: 1 (their Dot, the start then) is Off, 2 to 6 kept. The same in VrOptions.java (CROSSHAIR,
  read(): the old key dropped after).
- **Small circle** (item 3): crosshair.h kind 7, ring(0.24, 0.045), crosshair_rim 0.045 (others 0.06);
  kCrosshairKinds 8; vr_menu.cpp kCrosshair and VrOptions' names. The line was 0.035 first: rendered offline at 14, 20
  and 25 pixels a degree (scratch tool crossview.cpp, box-filtered and bilinear), it broke up at 14; 0.045 holds.
- **Rate hold** (item 4): jak_vr.cpp RateHold / held_refresh: the headset's told rate is followed once it has held
  for 1 s (kRateHold); `[vr-view] the headset at N a second for 1.0 s: the game follows it (M shorter changes passed
  over)`. xr_sim.cpp QUEST_SIM_PERIOD_BLIPS=N tells a doubled period every Nth frame.
- **Sound queue** (item 5): rpc-h.gc of all three games: *vr-sound-dropped*, *vr-rpc-scratch*, vr-rpc-wait, and
  add-element's port-0 branch (a quarter of a second at most the first time, then commands dropped into the scratch
  element until the IOP answers; the log at the 1st, 10th, 100th and every 1000th). gsound.gc swap-sound-buffers says
  when the IOP answers again; sound-buffer-dump through format 0, one line per run of the same command, each play
  with its sound's name.
- **Stall watch** (item 6): jak_vr.cpp, end of the file: a thread that looks every 100 ms; the game's and the display's
  places (stall_where marks in opengl.cpp, jak_vr.cpp, kdgo.cpp's RpcBusy after 100000 busy asks) and frames
  (stall_frame); `[vr-stall] ...` at 1, 5, 25 and 125 s, with the IOP's threads (IOP_Kernel describe_threads,
  iop_threads_now: names and states, no lock; threads.reserve(16) holds them, 8 to 10 per game), and when it goes on.
- **Crash record** (item 7): crash_dump_win.cpp install_crash_record (kmachine.cpp, after install_crash_dump): always
  on, chained to the previous filter: `[crash] the game crashed: exception 0x... in <module+offset | the game's memory
  at ...>`, an access violation's target, the registers, up to 16 stack words in modules' code, module base names only
  (no paths); stdout flushed. main.cpp: the log flushes at info (was warn) when not verbose.
- **Jak 3 test hook** (item 7): pc-vr.gc vr-test-task! with kmachine.cpp pc-vr-test-number (JAKVR_TEST_TASK,
  JAKVR_TEST_RESTARTS; kernel-defs.gc).
- **The Jak 3 crash's guard** (item 7, found in b69): TexturePool.cpp handle_upload_now checks the page, its length
  and name, each entry (0, outside the game's memory or misaligned: passed over as #f), and each texture's mips (7 at
  most), places (under m_textures' 16384) and name, before anything is read or written; `[vr] a texture page the game
  uploads has a bad entry (...): passed over (N so far)`, the first 3, then every 100th.
- **Test aids** (tests/README's table): JAKVR_TEST_SOUND_STALL=S (srpc.cpp RPC_Player holds S s at its 1200th batch;
  Jak 1's pc-vr.gc vr-test-sound-flood!, kmachine.cpp pc-vr-test-flags bit 4096, sends 300 commands once the player
  has been busy 30 frames), JAKVR_TEST_GAME_STALL=S (jak_vr.cpp stall_frame, the game's 1500th frame),
  JAKVR_TEST_BAD_TEXTURE=N (TexturePool.cpp: the Nth page's first entry read as 0).
- **Log** (items 10 and 11): loader.gc's "(AI-assisted) art lookup ... matched" and generic-obs.gc's manipy prints
  removed; jak_vr.cpp tell_often for the two "[vr-body] the game's stretch" lines; vr_options.cpp logs every option
  (`[vr] options: ...`, 12 a line) when they change (load_options runs twice at a start: the headset's, set_game's).
- **Texts**: whats_new.txt 0.1.5, docs/release-notes.md for 0.1.5 (banner, downloads, updating, What's new in 0.1.5),
  CONTROLS (the crosshair Off to start, Small circle, B in the menus), HEADSET_CHECKLIST (0.1.5's fixes section),
  tests/README (the test aids' rows, "The checks added in 0.1.5"); then the Jak 3 fix in What's new and the release
  notes (banner, What's new, What we tested for 0.1.5, Known limits: Jak 1's shadows, the lag spikes and the temple's
  sound stop still to find, Thanks up to 0.1.4). The texts were scanned: no player's name, no user name, no email.

## By design, not changed

- B is the game's Triangle (back) in the games' menus (0.1.3: Jak 1's menus take Circle as Cross, so B picked).
- Jak II starts on the flat panel, as each game's loads and blackouts do, until its first picture in play.
- Jak 1's shadows per eye (item 8): made once from between the eyes and moved to each eye's view; a known limit.

## Builds and sims this session

Builds (C:\JakQuest\sim\build068.sh to build072.sh, one at a time; each "BUILD OK", all three games):

- **68**: items 1 to 7, 10 and 11 as above. Its first try stopped at crash_dump_win.cpp's `'\\'` (sed quoting): fixed
  with the Edit tool.
- **69**: the Small circle's line 0.045 (from 0.035); the options logged once (load_options runs twice at a start);
  the sim's `[sim] a doubled frame period told every N frames`.
- **70**: the Jak 3 crash's guard (TexturePool.cpp handle_upload_now, below); srpc.cpp's JAKVR_TEST_SOUND_STALL.
- **71**: the sound flood for that test (kmachine.cpp pc-vr-test-flags bit 4096, pc-vr.gc vr-test-sound-flood!).
- **72**: Jak 1's queue dump names its commands (enum->string: sound-command->string is in the debug build only, so
  it printed 0); JAKVR_TEST_GAME_STALL (jak_vr.cpp stall_frame); fill_chain goes on until every image of the chain
  is written (twice the chain's length at most), not one acquire per image.

Sims (C:\JakQuest\sim\batch28g.sh, `OUT=<folder> SIMS="..." sh batch28g.sh`; 0.1.5's folders are b68 to b71 and s72:
b71's mp folders and b72 to b79 are from the session before):

- **b68**: crossdef2, cross2, ring2, ring3 (item 2 and 3), blips1 (item 4), task3 (four restarts by restart-mission: no
  crash), act1, act2, gunhold, ocean (as before): every run 0 errors, 0 stalls, 0 crashes.
- **b69**: ring2, crossdef2, blips1 (as item 3, 2 and 4 say), task3m (the pause menu's restart, four times; the first
  try pressed left before A, so nothing restarted: the sim now presses menu, up, A, A, left, A). The second task3m
  (b69-task3m2.log) **crashed at the third restart**: `[crash] the game crashed: exception 0xc0000005 in
  gk.exe+0x420340`, `a read of the game's memory at 0x0`, after "Load soundbank wascity3", the tester's log's last
  line. No PDB (Release, -O3): llvm-objdump and the rdata strings put gk.exe+0x420340 in
  TexturePool::handle_upload_now, reading a texture page's entry that was 0 (a page uploaded during the restart, the
  menu's sound bank swap with it). The PS2 reads EE address 0 harmlessly; OpenGOAL protects that page.
- **b70** (the guard): task3m (four restarts, no crash, the guard not reached: the timing differs run to run), sstall1
  (the sound player held 5 s: the queue did not fill), act1, act2, act3: 0 errors, 0 stalls, 0 crashes.
- **b71**: sstall1 with the flood (item 5: the drop path, the game on through the hold; its dump printed 0 for each
  command's name: build 72), task3m8 and task3m8b (C:\JakQuest\sim\jak3_menu_restart8.sim: eight restarts through the
  pause menu, from frame 6200 every 2000): all 16 restarts done, no crash, the guard not reached, the game shut down
  as asked at the end.
- **s72** (build 72): gstall1 (item 6: `[vr-stall]` for the game and the display at 1.0 and 5.0 s, the IOP's eight
  threads named with their states, both "went on after 7.0 s"), badtex3 (item 7: the guard passed the made bad entry
  over during the desert's load, the run's actions all played, a normal shutdown, the same 28 distinct error lines as
  b70's act3), sstall1 (item 5: the dump names its commands). Each 0 crashes.
- **s72 null2**: Jak II on this PC's SteamVR 2.19.3 with its null driver, 90 s (C:\JakQuest\sim\pcvr_null_jak.ps1;
  the PC's active OpenXR runtime left as it is, XR_RUNTIME_JSON for the run, steamvr.vrsettings put back after: hash
  the same). Its first try ran flat: the script did not set JAKVR (the PC game's VR is on only with JAKVR,
  QUEST_SIM or QUEST_SIM_SCRIPT set; the launcher sets JAKVR); it does now. Then: the session at 90 a second, Show
  performance's picture written into its chain every 10 s, no OpenXR error or warning; one `[vr-view] the game at 90`
  with this PC missing 300 to 580 displays every 10 s (item 4 on SteamVR itself); the null driver's upside-down head
  warned of, as known.

No large frame dumps were made (the sims' PNG shots stay in their folders, about 3 MB each).

## Next

- The list is done: every item [x] (LIST-0.1.5.md says how each was checked). Nothing committed, nothing released:
  the owner says when. Release steps: SESSION18's "0.1.3 released" (bump VERSION, PcBuild.java and the jak2
  mod-settings.gc *mod-version-text* to 0.1.5; build_apk.sh; `python -I tools/build_pc_release.py`;
  out/release-0.1.5 with SHA256SUMS; tools/make_patch.sh; commit; `gh release create`). patches/ was made again at
  the end of this session (make_patch.sh).
- For a player (HEADSET_CHECKLIST, "0.1.5's fixes"): the picture at the end of the gun's laser on SteamVR (only the
  crosshair or the performance text), Jak 1's temple and the plant boss's chamber (any `[vr-stall]` or `the IOP has
  not answered the sound player` lines in the log), Jak 3's leaper race restarted from the pause menu.
- Still to find, from a player's log: what stops Jak 1's sound processor in the temple, and the lag spikes at the
  plant boss (the stall watch says where the game was). Jak 1's shadows per eye would take the game's shadow code run
  for each eye.

## Publishing 0.1.5 (started 2026-11-17, stopped on the usage limit)

The owner: "Publish 0.1.5". Done: VERSION, PcBuild.java and Jak II's *mod-version-text* ("0.3.1-jakvr.0.1.5") bumped.
Left, in order (as SESSION19's "0.1.4 released"): Jak II's GOAL built again for the version text (build072.sh with
jak2, or build_pc_release.py's own build), README's banner and LIST-0.1.5's top ("Released as 0.1.5"); build_apk.sh;
`python -I tools/build_pc_release.py`; out/release-0.1.5 with SHA256SUMS.txt; make_patch.sh; the private-details scan;
commit, push, `gh release create v0.1.5` with docs/release-notes.md; give the APK's path and SHA-256.
