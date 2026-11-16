# Jak and Daxter Quest VR 0.1.1: the list

From the first reports on 0.1.0 (2026-11-14): Vixel (PC VR: SteamVR, a Quest through it, Touch controllers; his logs
in C:\JakQuest\reports\2026-11-14\vixel), titan on the Flat2VR thread (Quest 3, the Jak 3 install; his log in
C:\JakQuest\reports\2026-11-14\titan), Willthethrill's three PC VR videos (Jak II's Haven City, Jak 3's Spargus and
arena), smalluglyrabbit (Vive wands), and the owner. Marks: [ ] to do, [~] in progress, [x] done (how it was checked).
0.1.0's open items 5 to 8 (the Quest's own) stay open in LIST-0.1.0.md; items 2 and 3 here are two of them.
The owner: "some of these may be pcvr specific, quest may work fine, so dont break either one with certain fixes.
regress nothing". So a fix for something seen only on PC VR (SteamVR, Touch through it, wands) is gated to where it
happens (the runtime, a binding that is missing), and each fix is checked on both the PC's simulated headset and the
Quest's paths it reaches.

1. [x] **Smoother frames (AppSW) off to start** (owner: "auto disabled appsw. it seems to cause visual issues for now. the
   title screen flickers with it on"). The game's default, the launchers' default, and 0.1.0's saved "on" turned off
   once; the Performance preset no longer turns it on; What's new, the release notes and the checklist say so.
   [~] Done in the code (checked 2026-11-15): vr_options.h `space_warp = false`, saved as "smoother-frames" (0.1.0's
   "space-warp" no longer read), the VR menu's row says off to start, VrOptions.java the same key and default, and
   the Performance preset leaves it alone; HEADSET_CHECKLIST.md says so. Done since: whats_new.txt and release-notes.md
   say so for 0.1.1 (both: "off to start: it made the title screen flicker").
2. [x] **The Quest's install runs out of memory** (titan: "Couldn't prepare Jak 3" every time, after restarting; the owner:
   Jak 1 too until other apps were closed). titan's log: the installer process is at 4.1 GB when the compile ends, then
   the level extraction loads all 274 DGOs on top and Android closes it at 4.6 GB (LOW_MEMORY, 7.7 GB headset). Bring
   each game's install well under the Quest's memory (0.1.0 item 8). The owner: "find a method to make sure ALL the
   games can properly be prepped".
   Done, first the extractor and compiler (Precursor's; peaks measured on the PC with 6 level workers,
   C:\JakQuest\sim\mem011): (a) LinkedWord is 8 bytes and each symbol name is kept once (was 16 bytes a word plus a
   string each); (b) an object's raw bytes are freed after linking when its code is not decompiled (not NO-XGO or
   ALLSPOOL); (c) the extractor's compile drops each file's debug info and source text once its .o is written (no
   listener); (d) the test zone takes its art from one DGO (`art_dgos` in test-zone.jsonc: VI1.DGO, CFB.DGO, CFB.DGO)
   instead of loading every DGO. Jak 3: decompile and extraction 5848 to 4044 MB at most, compile 7813 to 1944 MB.
   Jak II after: 3185 and 1535 MB; Jak 1 after: 1703 and 806 MB. Checked (all three games): the decompiler's output is the
   same, and the obj and iso files hash the same before and after except the test zone's own three (TSZ.DGO,
   test-zone.go, test-actor-ag.go). Level extraction (.fr3) differs run to run with the same binary (threads), so it
   was not compared. Then the Quest's installer (InstallService.java): how many levels it extracts at once now counts
   each game's own memory before its levels (2.0 GB Jak 1, 2.9 GB Jak II, 3.6 GB Jak 3, with room over the PC's
   numbers; 0.1.0 counted Jak 1's 2 GB for all three, so Jak 3 started too many), and when Android closed the last
   preparation for memory (the `:installer` process's last exit, LOW_MEMORY) the next one extracts one level at a
   time. The install log says which: `Memory: jak3 needs about 3600 MB before its levels, ... level(s) at a time`.
   The launcher's message after such a close says the next preparation goes one level at a time (six languages).
   Checked: the Quest launcher's Java compiles (javac with Android 34). Headset: prepare Jak 3, then Jak II and Jak 1,
   with other apps open as usual.
3. [x] **Jak 3 crashed in the arena** (Vixel, PC): "suspend called without enough stack in proc: eco-pill ... Stack:
   144/128". Precursor's compiler makes bigger frames than upstream's, also on x86, and Jak II and 3 shrink small
   processes' backup stacks to as little as 16 bytes; Precursor's own fix (never below 512) is in Jak 1's kernel and only
   for ARM64. Likely also 0.1.0 item 6 (Jak II's crashes on the phones).
   Done: `stack-size-set!` never goes below 512 bytes in all three kernels, on every chip (Jak 1's ARM64-only rule made
   general). The processes that shrink their stacks come from the shared process heap (16 KB each at spawn), where
   512 fits; in the fixed 8 KB pools only the flut's and mech's manipy shrink (128 to 512, 384 bytes more of 10 KB).
   Checked: the three games compile; Jak 3 on the simulated headset from boot to the desert with the gun drawn, aimed
   and steadied (tests/jak3_gun.sim): no "suspend called without enough stack", no process heap errors. Headset: the
   Jak 3 arena (Vixel's place) and the flut and mech.
4. [x] **Start with Touch controllers on SteamVR** (Vixel: "vr itself cant start button"; the owner heard it twice): SteamVR
   keeps the left menu button for its dashboard ("menu button no" in the log), so nothing is Start. Without a bound left
   menu button, the left stick's click is the menu button (a press Start, held a second the view or the VR menu).
   Done (xr_openxr.cpp, pad.h, jak_vr.cpp): with no left menu button bound, the left stick's lone click is the menu
   button everywhere it is (Start, held a second the view in play or the VR menu over a menu, a click closes the VR
   menu), and no L3 then; with the left hand at the head it stays the d-pad's Select, and both sticks clicked together
   stay the recentre with no Start. The log says `menu button no (the left stick's click is the menu button)`.
   CONTROLS.md (the launchers' Controls) has a row for Quest Touch through SteamVR. Checked on the simulated headset
   (tests/jak1_stick_start.sim, `menu_unbound 1`): one Start at PRESS START, the VR menu opened by the held click and
   closed by the next, no Start from the two-stick recentre. Headset: Vixel's SteamVR with Touch.
5. [x] **Jak 3's title screen has no text** in VR (Vixel: "higher priority"; he started by aiming where he remembered).
   Found: the text is on the HUD panel, but Jak 3 draws it without writing alpha (sim b24 menu3, km-p10-panel.png:
   18126 pixels with colour, none with alpha), and the panel is laid over the eyes by its alpha, so none of it showed.
   Jak 1's orb icon on the HUD is the same (sim b25 hud1: 13081 coloured, 312 with alpha). Done in the code: the HUD
   panel's alpha is its own or its colour's brightness, whichever is more (post_processing.frag `keep_alpha` 2,
   OpenGLRenderer.cpp), in all three games; what had alpha is as before. Checked (batch 25, build 24, Jak 3 first):
   tests/jak3_ptr_menu.sim's km-p10-panel.png now 26960 pixels with alpha against b24's 8834, the 18126 coloured ones
   among them (New Game, Load Game, Options, Quit). Jak 1's orb icon (tests/jak1_hud.sim, batch 26): 12990 pixels
   with alpha on the panel (b25: 312). Headset: Jak 3's title.
6. [x] **Jak 3's opening flickers** (Vixel: "view fight between 1st person view and where camera should be ... alot of
   flickering").
   Found: the game's blackouts (mode 4: between a scene's shots, a warp, a load) put the view on the flat panel at once,
   showing the game's own camera there, then Jak's eyes again a few frames later; Jak 3's opening has several (sim b20
   open3: mode 4 frames in the middle of its scenes, each a switch out of the eyes and back). Done in the code
   (jak_vr.cpp `blackout_in_eyes`): a blackout begun while the eyes saw play or a scene keeps the eyes, black, for up to
   1.5 s (`[vr-view] a blackout kept in the eyes, black: N frames`); a longer one (a load) goes to the flat panel as
   before. All three games. Checked (batch 26, tests/jak3_open_adaptive.sim): the opening's two blackouts in its
   scenes kept black in the eyes (18 and 28 frames: "a blackout kept in the eyes"), the loads on the flat panel as
   before, Adaptive cutscenes' switches as in item 32. Headset: the opening (Vixel's place).
7. [ ] **Some objects look different in each eye** (Vixel, Jak 1: "bad on doors, left eye seems bad version while right
   eye looks fine ... mostly precursor objects but a few lanterns also", the door on Geyser Rock; Jak 3 the same; the
   owner: Jak II too). Vixel, 23:05: "all 3 have single objects rendered differently per eye, jak 2 took me longest to
   spot some, but 1 and 3 have them at start".
8. [x] **Jak 1's scenes through Jak's eyes: you face away from what happens** (Vixel: "your mostly looking away from what is
   happening in the story"). Turn the view to the scene as it starts.
   Found: Jak II and 3 take a scene's facing from the scene Jak's main joint (pc-vr.gc `vr-scene-jak-head!`); Jak 1
   took it from his control's facing, which its scenes leave as it was in play while they turn his whole body by its
   joints, so the view (and the eyes' place 8 cm ahead of his neck) faced where he stood before the scene. Done in the
   code: Jak 1's pc-vr.gc `vr-jak-ahead!`: in a scene, his main joint's z row, as the other two. To check:
   tests/jak1_scene_face.sim (the opening scene's shots, the eyes against the game's camera).
   Checked on the PC sim (batch 35, build 33, tests/jak1_scene_face.sim with scenes through his eyes,
   C:\JakQuest\sim\gkcfg-noadapt): through his eyes the opening scene faces the way the boat goes (its prow, Daxter
   before him, then the island), and on Misty Island it looks out over the meeting below. Headset: the other scenes.
9. [x] **The HUD** (Vixel: Jak 1's "worse", Jak 3 "minor hud issues", Jak II's menu fine but its "text follows head
   weird"). Vixel, 23:05: "jak 1 hud might be unreadable", "jak 2 hud collectable colors off to the side of their
   values", "jak 3 hud broken but readable". Vixel, 2026-11-15 20:12 UTC: "if hud is still fucked in next build i'll
   try to screenshot hud and original hud".
   Found: the HUD's icons (orbs, cells, flies, metal heads' gems, skulls and the like) are little 3D models the games
   draw through Generic2 into a level's foreground buckets with the HUD's own matrices; only the 2D (numbers, bars) went
   to the HUD panel, so in VR the icons were drawn in the eyes, each at its place in the eye's whole view, off to the
   side of their numbers on the panel (Vixel's "collectable colors off to the side of their values"). Done in the code:
   Generic2 tells such fragments apart (their matrix), and a bucket seen holding them is drawn in the HUD pass for them
   alone, the eyes leaving them out (jak_vr.cpp `hud_models_drawn`, `hud_models_only`, Generic2_OpenGL.cpp do_draws);
   the first frame they appear they are still drawn in the eyes, so nothing goes missing. With item 5's alpha, icons
   drawn without alpha show too. Checked on the simulated headset (batch 25, tests/jak1_hud.sim, jak2_hud.sim,
   jak3_hud.sim with JAKVR_TEST_HUD=1): each icon on the panel beside its number in all three games, none in the eyes
   (`[vr-view] the HUD's models in bucket N: drawn on the HUD`), and Jak 1's orb icon with item 5's alpha (batch 26).
   Not checked: the HUD's own look against a flat screen's (Vixel offered pictures; to compare when he sends them).
10. [x] **Jak 3's gun: the ammo on the gun** (Vixel: "shotgun amo not connected to gun, but aim worked good").
    Found: the runtime carries the gun's bones with Jak's posed hand, but only its first 32 (vr-body-info's gun-bone,
    `min 32` in gun-util.gc's `vr-gun-fill!`), and Jak 3's gun has its magazines on joints 36 to 39 (gun-init's `mag`
    joint-mods), so they stayed where the game's own animation held the gun. Done in the code: 64 bones (all three
    games' vr-body-info, Jak II and 3's `vr-gun-fill!`, jak_vr.h kGunBonesMost and the struct's size, 2208 bytes); the
    body trace says the gun's own count ("gun bones N (of its M)"). To check: tests/jak3_gun.sim with
    JAKVR_BODY_TRACE=1, the magazine on the gun in the pictures.
    Vixel, 2026-11-15 04:37 UTC, Jak II too: "i finally got gun in jak 2 same problem as 3 where amo clip is attatched
    to where gun would have been in game instead of the gun" (Jak II's gun has 36 nodes, so its last four were left
    behind the same way; the 64 bones cover both).
    Checked on the PC sim (batch 32, build 30: tests/jak3_gun.sim and jak2_gun.sim): drawn, aimed left, held in both
    hands and fired, the magazine (the red-lit top) sits on the gun in the hand in every picture, in both games.
11. [x] **The holster places a little bigger to start** (Vixel: easier to put the gun away for those unsure where), and
    the owner: "add a holster size option regardless in the in game vr menu". Done: **Holster size** in the VR menu
    (guns only) and the launchers, 50 to 200%, 130% to start (vr_options holster_size, at_holster() in jak_vr.cpp
    grows each limit by 0.15 m per 100%). Checked on the simulated headset: tests/jak2_holster.sim and
    tests/jak2_gun.sim (JAKVR_TEST_GUN=1) draw and put away the gun at 130%.
12. [x] **Spin kick by spreading your arms** (Vixel's idea: "T-pose to trigger spin move"). The owner, 2026-11-15:
    "t pose to spin kick is off by default". Done: **Spin kick by spreading your arms** in the VR menu and the
    launchers, off to start: on foot with both arms held out wide at the sides (a T) for 0.2 s, one spin kick
    (Circle); the arms brought in, the next. The log: `[vr-pad] spin kick: the arms spread`. Checked on the simulated
    headset (tests/jak1_spread_kick.sim with `spread-spin-kick 1`): two kicks for two spreads; tests/jak1_melee.sim
    as before.
13. [ ] **Jak II multiplayer** (the owner to Vixel: "next release will add it"; Vixel: "jak2's multiplayer mod is on open
    goal"). Find the mod, its licence (ask before using anything unlicensed), and build it in as Jak 1's.
    2026-11-15: a web search finds no public Jak II multiplayer mod (Teamruns, the one built in, is Jak 1's alone; an
    early "Multiplayer Hide'n'Seek" in OpenGOAL's 2023 progress report names no game). Ask Vixel for its name or link.
14. [ ] **Willthethrill's videos** (PC VR): Jak II in Haven City (big flat-coloured shapes filling the view now and then),
    Jak 3 in Spargus and the arena (the gun close to the eye, a dark patch over the lava). Look at each; no log yet.
    On the PC sim (batch 37, C:\JakQuest\sim\jak3_arena_look.sim; the arena's fight put him back on the walkway over
    the lava at wasstada-jump-training): dark oval patches on the lava beside the walkway, the same in both eyes (in
    the world, not one eye's picture), where Jak's own shadow would fall off the walkway's edge onto the lava below.
    The gun drawn sits low at the right hand, not at the eye, with the hands at rest. Next: the same place from the
    game's own camera (third person), to tell the game's shadow from anything of the VR's.
    Batch 38 (build 37): the same dark ovals are in the game's own third-person view (picture ar3-ahead): the game's
    shadows on its lava, not the VR's. Haven City: C:\JakQuest\sim\jak2_city_look.sim (JAKVR_TEST_CONTINUE=ctysluma-start)
    looks round, down and up standing, walks and turns with a picture each half second, then the same from the game's
    camera, to find the flat shapes. To run: batch 39.
    Batch 39 (build 38, 20 pictures): Haven City from his eyes looking ahead, left, back, right, down and up, walking
    and turning among the citizens and guards, then from the game's camera: no flat-coloured shape anywhere. The one
    view filled by one thing: walking on into a wall (hc-walk-6 to 8), its own textured face close before the eyes, as
    in any first-person game. Needs Willthethrill's video (where and when the shapes come) through the owner.
15. [~] **Vive wands' d-pad** (smalluglyrabbit: cycling the guns): the left hand at the head is the d-pad there (no thumb
    rest); check it works with wands. The simulated headset now has `no_thumb_rest 1` (a controller with no thumb rest
    sensor, as wands, Index and WMR); tests/jak2_dpad_head.sim holds the left hand by the head and works the left stick.
    Checked on the PC sim (batch 17): right, down, left and up on the left stick by the head, the guns changing three
    times. Left: smalluglyrabbit's word on real wands.
16. [x] **Jak II's car: the height button works only now and then** (Vixel, 22:51: "button that makes cars go up and
    down randomly doesn't work then randomly starts working again", his Jak II log at 23:00, notes in
    C:\JakQuest\reports\2026-11-14\vixel\game-jak2-output.notes.txt). Sitting in the car his right hand rested at the
    hip, inside the holster place: 163 right grips went to the holster, 53 times "did not draw the gun (state
    target-pilot-stance)", and no R2 for the car's height. Done: the holsters count only on foot (the games' first
    person allowed: not in a vehicle, on the board, a turret or a ride), where the gun can come out.
    Vixel found the same himself, 2026-11-15 04:37 UTC: "so vehicle up down thing seems to be tied to putting gun away
    and pulling gun back out witch explains why sometimes i cant go up down, basically 2 separate actions seem to be
    on same button".
17. [x] **A switch for each new feature** (the owner: "add toggles for all these new features like t pose to spin kick,
    etc.", then "all those little vr opotions should be toggles in the in game vr menu ... t pose to spin kick is off
    by default. all other defaults stay the same"). Done, each in the VR menu and both launchers (vr_options.h/.cpp,
    vr_menu.cpp, VrOptions.java), plain names: **Spin kick by spreading your arms** (item 12, off), **Stick click as
    menu button** (item 4, on; off: no Start from the stick, L3 as before), **Holsters only on foot** (item 16, on),
    **Holster size** (item 11, 130%), **First person in vehicles** (item 25, off: the owner's), **Buzz on d-pad
    presses** and **Buzz on gun switch** (items 28 and 29, on), **Hide arms near your eyes** (item 20, off: the
    owner's), **Adaptive cutscenes** (item 32, on), **Fade body near your eyes** and **Keep your body upright** (item
    40, on: the owner's), the performance options (item 36, off). Every option has its VR menu row (checked against
    vr_options.cpp's keys) and its launcher row (VrOptions.java; the PC launcher compiles with -Werror).
18. [ ] **Game audio cut out** (Vixel, 23:05, Jak II on SteamVR: "game audio cut out but that could be glitch on my vr
    headset's end"). Ask for the log of a session where it happens; look for the sound thread's lines. His last message
    that night (06:09, "vr headset started screaming at me") may be the headset itself or the game's sound: ask.
    Vixel, 2026-11-15 20:01 UTC, on that: "its like a fan screaching". The headset's own fan then, not the game's
    sound; nothing to change for it here. The 23:05 cut-out still needs a log of a session where it happens.
19. [x] **The pointer works on every menu** (the owner, 2026-11-15: "some options dont seem to be triggered by the
    pointer, for example, the save select screen on jak 1, etc. i want the pointer to be able to interact with all
    menus, and auto scroll when pointed at the bottom of a menu when scrolling. basically, matched interactivity as the
    openrac mod"). Every game menu the pad can work (Jak 1's save select, the other games' too), the pointer works the
    same way, and pointing at the top or bottom row of a list that scrolls keeps scrolling it, as Ratchet & Clank
    Quest VR's pointer does (C:\RACQuest\RatchetQuestVR, read only).
    Done in the code: Jak 1, the save and load screens' files (draw-memcard-file-select drew them outside the options'
    rows) and the yes/no notices (drawn with a line height of 0, so no row was ever under the pointer); a row being
    changed follows the pointer (On / Off and the like: the half pointed at is left or right on the d-pad once,
    through the new `pc-vr-pointer-side!`; a slider's bar follows it) and a click on it is Cross. Jak II, the save
    slots (their own layout) and every Yes / No, On / Off and Continue / Retry line (left half the first, right half
    the second). Jak 3, the save slots (their own heights) and every Yes / No footer and On / Off row (through
    `pc-vr-pointer-side!`, as its rows change their setting from the d-pad). Auto-scroll: the pointer held on the top
    or bottom row shown of a list with more past it moves a row each 0.3 s (Jak 1's progress lists, Jak II and 3's
    scrolling option pages). To check: build, then each on the simulated headset.
    Checked on the PC sim (batch 17, tests/jak1_ptr_slots.sim): Jak 1's save slot under the dot, the trigger a Cross,
    the new game started. Left to check: Jak II and 3's Yes / No and On / Off rows, the auto-scroll.
    Found on the PC sim (batches 33 and 35, tests/jak2_ptr_menu.sim, jak3_ptr_menu.sim): Jak II's title menu took the
    click on Quit Game and showed its Yes / No, but the dot on either half changed nothing: that line (and the pause
    menu's Restart / Quit ones) is drawn inside its option by `menu-restart-mission-qr-option` and
    `menu-quit-qr-option`, which never asked the pointer. Done: both do now (progress-draw.gc `vr-ptr-yes-no!`). Jak
    3's title menu ran at 7 or 8 frames a second on the simulated headset, and the trigger, read once a game frame, was
    pulled and let go between two reads: no click on Quit. Done: each trigger's most since the game last read the pad
    counts (jak_vr.cpp `Shared::trigger_most`), and a click waits until the game has read it twice, or 1.5 s. The
    pointer test trace (JAKVR_POINTER_TRACE=1) logs the dot against each two-way line and each row the auto-scroll
    moves on (`[vr-ptr] ...`); C:\JakQuest\sim\jak2_ptr_scroll.sim holds the dot on a long page's lowest row.
    Checked on the PC sim (batch 37, build 36): Jak II's title menu end to end: the click on Quit Game was Cross, its
    Yes / No followed the dot (Yes on the left half, No on the right; the line is 36 high round y 301.5, the dot in it
    at 4 and 2 degrees, just under at 0), and the click on No closed the question. The press trace (each pull logged)
    showed Jak 3's title click and the scroll test's second click both reached the game, which looked 13 times, with
    the dot under Jak 3's menu (v 0.78, where Quit's bar ends at 0.68: Quit was still picked from the step before) and
    above Jak II's Options menu (v 0.18, its first row at 0.38): the sims aimed off the rows, and the game rightly took
    no click. The sims aim at the rows now (Jak 3's Quit at 7 degrees, Game Options at 20); left to check: Jak 3's
    Yes / No and the auto-scroll.
    Checked on the PC sim (batch 38, build 37): Jak 3's Quit click is Cross, and its Yes / No (the line's middle at y
    290.8) follows the dot at 4 degrees, Yes on the left half and No on the right (pictures km-exit-4-yes / no); the
    sim's last click was at 0, under that line, and rightly found no row (tests/jak3_ptr_menu.sim clicks No at 4 now).
    Jak II's Game Options opened by the pointer; on it the bottom row shown (Subtitle Language) is near 5 degrees and
    from 0 down the dot is under the page, where the held dot moved nothing. Done: the dot just under the rows shown or
    just over them (a row's height, where the page's arrows are) moves them on as the edge row does, in all three games
    (`vr-ptr-generic-past` in Jak II and 3's progress-generic-draw-pc.gc, `vr-progress-pointer-past` in Jak 1's
    progress-pc.gc). The scroll sim now holds the dot 3 s each on the bottom row, under the page and over it. To check:
    build 38, then ptr3 and scroll2.
    Checked on the PC sim (batch 39, build 38): Jak II's Game Options page (10 rows, 5 shown) moved on a row each 0.3 s
    with the dot on its bottom row and under it, to the last row (Miscellaneous), and back up to the first (VR
    Options) with the dot over it (pictures js-held-0, js-held-30; "[vr-ptr] the page moves on a row: 9 of 10" down to
    "0 of 10"); Jak 3's Quit question closed by the click on No at 4 degrees (km-exit-closed: the title menu again).
    Left: Jak 1's auto-scroll. Its pointer trace now logs each row the auto-scroll moves on too (progress-pc.gc, the
    same "[vr-ptr] the page moves on a row" line), and C:\JakQuest\sim\jak1_ptr_scroll.sim opens Options, then Game
    Options (10 rows, 6 shown), and holds the dot 3 s each on the bottom row, under the rows and over them.
    Checked on the PC sim (batch 40, build 39): Jak 1's Game Options, opened by two pointer clicks, moved on a row
    each 0.3 s with the dot on its bottom row ("6 of 10" to "9 of 10", Back highlighted: jp-held-bottom) and back up
    with the dot over the rows ("3 of 10" to "0 of 10"). The pointer now works and auto-scrolls on every menu checked
    in all three games; left: the headset.
20. [x] **The body gets in the way** (the owner: "the body also seems to get in the way a bit"). Less of Jak in front of
    the view and the hands, as Ratchet's. The owner, 2026-11-15, on the arm cut: "Without making this weird effect where
    the hands lose arms every couple seconds? ... keep that hide arms near your eyes thing as a toggle that defaults to
    off, but cmon man, full body ik HAS to be made better". So the fix is the IK itself (item 24: the wrist bends out
    of the eyes, the forearm comes up from below), and **Hide arms near your eyes** (VR menu and launchers) is **off** to
    start: turned on, while an arm's elbow or the middle of its forearm is within 16 cm of the eyes (back past 20 cm)
    its upper arm and forearm fade out of the eyes (eased, item 40), the hand staying, the shadow keeping the arm; the
    log says `[vr-body] the right arm fades out of the eyes` / `comes back to the eyes`. (Ratchet's own: whole body to
    start, no such cut.) Checked (batch 28a, tests/jak1_arm_near.sim with the option on, C:\JakQuest\sim\gkcfg-hide):
    the wrist bend (item 24) keeps the elbow and forearm farther than 16 cm, so it never had to fade them. The rest of
    him in the way (his legs in a kick, his body in a roll, a flip, a dive, an uppercut): item 40, which also brings
    the chest and shoulders under the view when looking down (batch 28a, tests/jak1_down.sim: forearms and legs below,
    no chest in the face).
21. [x] **Fingers against the world, smoother** (the owner: "finger bending against environments isnt as smooth or
    responsive as i feel it could be in comparison to our openrac work"). Match Ratchet's finger bending at walls and
    floors: how fast the fingers follow and how they ease.
    Done in the code: the curl eases toward what the probes allow at a rate (18 a second closing on a wall, 7 opening
    again) by the frame's time, instead of a fixed share a frame, so it follows as fast at any frame rate and lets go
    smoothly; each finger goes from where it is drawn toward the curl asked and stops where the world is (item 35).
    Checked on the PC sim (batches 22 to 24, tests/jak1_fingers.sim, JAKVR_BODY_TRACE=3, every frame): the fingers
    ease on to the ground and off it a step a frame, and rest still once the hand is turned flat to it or away (no
    flicking between two answers as b22's did). Found there: with the hand edge on in the ground, held still, the
    little finger curled on to the ground and opened again about once a second (0.12 to 0.62 and back): the palm sat
    near the 60 degrees that decide "a surface the palm faces", going between the two. Done: that test has give now
    (taken within 60 degrees, let go past 70), and the trace says "(the palm faces one)". Checked (batch 28a, every
    frame): the hand held still edge on in the ground, its fingers changed twice in the whole hold (b24: about once a
    second). Headset: against walls and floors, as Ratchet's.
22. [x] **Jak II crashed when the sewer's gun turret was destroyed** (Vixel, 2026-11-15 05:02 to 05:11, 0.1.0 on PC:
    "game crash after a turret shot me", "i think its actually destroying it that causes crash ... a required part of
    the game causes crash"; his logs game-jak2-output_2.log and _3.log, both ending "suspend called without enough stack
    in proc: #<sew-gunturret sew-gunturret-13 running :state die ... :stack 0/256 ...> Stack: 288/256"). The turret
    is an entity; its process got the kernel's own 256-byte backup stack when it was made, which item 3's fix (in
    `stack-size-set!`) never sees. Done: every kernel's `activate` gives a process with a big heap (12 KB or more: the
    entities' 16 KB, before they shrink) 512 bytes, as `stack-size-set!` does; the small fixed pools keep 256.
    Checked on the PC sim (batch 38, build 37, tests/jak2_turret.sim with JAKVR_TEST_CONTINUE=sewer-start and
    JAKVR_TEST_TURRETS=1: into the sewer, sew-gunturret-13 found and destroyed): "in state active, stack 512", then
    "destroyed (instant-death)", "in state die, stack 512" and "gone, its death ran through"; no "without enough stack"
    and no error in the log. Headset: the sewer on Vixel's PC.
23. [x] **Jak 3 crashed at the end of the tutorial, every time** (Vixel, 05:18 to 05:25, 0.1.0 on PC: "every time i finish
    tutorial game crashes on jak 3"; game-jak3-output_2.log and _3.log end "suspend called without enough stack in
    proc: #<eco-pill eco-pill running :state fade ...> Stack: 144/128"): item 3's own crash, fixed there (0.1.0 did
    not have it). Check: the arena's end on the PC.
    Checked on the PC sim (batch 37, build 36, C:\JakQuest\sim\jak3_pills.sim with JAKVR_TEST_PILLS=1: dark eco pills
    made 6 m from Jak in the arena as its enemies drop them, set to fade after 4 seconds): two pills went through their
    fade and were gone, and the log has no "without enough stack". (Batch 36's run had sat in the game's save prompt
    over play the whole time, so made no pills.) Headset: the arena's end on Vixel's PC.
24. [~] **Full-body IK, smooth and proper in all three games** (the owner, 2026-11-15: "make full body ik in all games
    as smooth and proper as possible. as minimum jank as can be", with Halo's and Ratchet's full body work and other
    references to draw on). His video (dev.jakquest.vr-20261114-185418-0.mp4, Jak 1 on Geyser Rock): the sleeve and the
    hand fill the view up close, bits of Jak near the eyes. With items 20 and 21.
    Found (sim, JAKVR_BODY_TRACE=1's new "right arm from the eyes" line): Jak's hand is near twice a person's, his
    wrist 15 cm behind the palm, so a controller held 12 cm before the eyes put his wrist 3 cm behind them and the
    forearm ran straight up through the eyes (the glove's cuff and the sleeve filling the view).
    Done in the code (jak_vr.cpp pose_arm): **the wrist bends out of the eyes**: as a real wrist does, the hand pitches
    about its palm (which stays on the controller) just enough to keep the wrist 14 cm from the eyes, so the forearm
    comes up from below; pitched down instead only for a hand above the head (the wrist over the eyes, the elbow then
    out to the side); eased when it lets go, at once when the hand comes closer. Not the gun's hand (the gun stays where
    the controller points). **The elbow** is eased in the chest's own axes (about 40 ms: a one-frame jump or a quick
    roll of the wrist no longer flicks it to the other side), the hand's roll moves it less (less again high or by the
    face, where a palm turned down swung it up to the shoulder's height), and by the face it hangs down and a little
    forward; **the shoulder** no longer rises for a hand brought to the face (it lifted the upper arm into the view).
    Checked on the PC sim (tests/jak1_arm_poses.sim, five poses): the wrist 14 cm below the eyes with the hand 12 cm
    before them (was 3 cm behind), the hand 25 cm away clear with its forearm from below, a hand over the head reaching
    down from above, the rest pose unchanged. Up close (12 cm) his big hand itself still fills much of the view, as a
    hand that size would: item 40's life size makes it a person's hand (World scale 100%). The refinement pass on
    the rest of the body (upright under the head, the game's limb stretch undone, the fades): item 40. To check on
    the headset.
25. [~] **First person in vehicles, as an option** (the owner: "add an option for 1st person vehicles as well", then
    "right now i think the game auto switches to third person for things like vehicles ... keep that as the default.
    just add this first person vehicle/whatever else option as a toggle. off by default"): the view from Jak's seat on
    the Zoomer, the JET-Board, the cars and the like, its own switch. Done in the code: **First person in vehicles**
    (off), the camera flag 256 (kFlagVehicle) from each game's `vr-vehicle-first-person-ok?` (Jak 1: the Zoomer and the
    flut-flut; Jak II and 3: piloting, the board, the mech, the flut), and the view turns with the vehicle.
    Checked on the PC sim (batch 17, tests/jak2_board.sim, JAKVR_TEST_BOARD=1 JAKVR_TEST_CONTINUE=ctysluma-start):
    with the switch on (C:\JakQuest\sim\gkcfg-fpv) no switch to third person on the JET-Board, the pictures from Jak's
    eyes on the board in Haven City; off (the default) third person as the board starts and first person again as he
    gets off, as 0.1.0. (tests/jak1_zoomer.sim never put Jak on the Zoomer: the continue point comes after it.)
    Batch 39 (build 38, tests/jak1_zoomer_misty.sim from the misty-bike continue point, the switch on): first person on
    the Zoomer (camera flags 0x151, the vehicle flag set), the view from his seat with his hands on the handlebars and
    the Zoomer's nose ahead (pictures zm-on, zm-drive1, zm-drive2); his chest and legs faded out of the eyes, no part of
    him in the view. Left: the cars and the Titan suit on the headset.
26. [x] **Switching to third person in a scene** (the owner: "i also couldnt find the button to switch to third person
    in the middle of cutscenes. didnt seem to be mapped properly"; his video dev.jakquest.vr-20261114-185754-0.mp4 shows
    Jak 1's opening scene and "SUBTITLES DISABLED"): the left menu button held a second switches the view in scenes
    too, as in play, and say so in Controls. Done in the code (fill_pad: the hold in a scene switches between through
    Jak's eyes and around you, until the VR menu's Cutscenes changes; CONTROLS.md). To check: tests/jak1_scene_view.sim.
    Checked on the PC sim (batch 35, build 33, tests/jak1_scene_view.sim): in the opening scene the hold switched to
    through his eyes (`[vr-pad] scenes are seen through his eyes now: the left menu button held`, the view to his eyes)
    and back (`... around you now`, the view to the game's camera).
27. [~] **Every mapping and function in all three games, and a clean build** (the owner: "make sure all mappings and
    functions work in all games, and just be sure this next build is as cleaned up as can be"). Go through the
    controls table (CONTROLS.md) in each game on the simulated headset before the release builds.
    Checked on the PC sim (batch 35, build 33, tests/jak1_buttons.sim, jak2_buttons.sim, jak3_buttons.sim): in all
    three, A jumped, B spin-kicked, X punched (standing and running), the left trigger crouched, A then X dived, the
    left trigger and X uppercut, the menu button paused and played again (`[vr-pad] Start`), and Jak 1's Triangle
    looked around and out; in Jak II and 3 the right grip at the hip drew the gun (`[vr-pad] holster`,
    target-gun-stance) and the right stick's click got on the JET-Board (target-board-start); Jak 3's left grip
    (L2) made him change (target-powerjak-get-on). Found: the runs could not show a round fired (the line is logged
    only with JAKVR_BODY_TRACE, which the batch did not set) nor Jak II's Dark Jak (the test gave no dark eco). Done:
    the batch sets it, and Jak II's JAKVR_TEST_GUN aid also gives Dark Jak with his meter full (gun-util.gc).
    Batch 36 (build 35; the tests now click the board on, then off, then the left grip): Jak 3 fired a round, got on the
    board and stayed on it: on the board the right stick's click was R3 (the game's look around, unused in VR) and only
    the grip was R2, so the click that put him on never took him off, and the left grip that came after found him still
    on it (no Dark Jak on the board). Done: Jak II and 3 tell the runtime when he is on the board (kFlagBoard, pc-vr.gc),
    and there, with Fists on the right grip (the option that moved the board to the click), the click is R2 as on foot
    (the grip still is too; with the option off nothing changes: the grip on and off, the click R3); CONTROLS.md says
    so. Jak 3's test aid gives Dark Jak as Jak II's does. (Jak II's buttons run in batch 36 still used the old test
    file.)
    Checked on the PC sim (batch 37, build 36): in Jak II and 3 every button again, and now the right grip's gun fired a
    round (`[vr-gun] a round fired`), the right stick's click put him on the board and the same click took him off
    (target-board-get-on, target-board-get-off), and the left grip then made Jak II's Dark Jak (target-darkjak-get-on)
    and Jak 3's change (target-powerjak-get-on). Every mapping of CONTROLS.md's play rows has now been seen working in
    all three games on the sim. The clean build is the release builds' own, once every item is done.
28. [x] **The d-pad: make sure it works, and feel it** (the owner, 2026-11-15: "i also couldnt tell if dpad was
    activating. maybe make it so when i use dpad inputs, the controller stick vibrates when moved in a direction ...
    just make sure d pad actually works"). Done in the code: a quick pulse in that stick's hand as a direction goes in
    (and a lighter one as the left thumb's rest makes the right stick the d-pad), with **Buzz on d-pad presses** (on);
    a direction now goes in past half way and stays in until under a third (no flicker at the edge); the log says
    `[vr-pad] d-pad <way> (the <left|right> stick)`. Checked on the PC sim (batch 32, build 30, tests/jak2_dpad.sim
    with JAKVR_TEST_GUN=1): `d-pad right`, `down`, `left` and `up (the right stick)`, the gun changing after down, left
    and up (Jak II gives right nothing there). How strong the buzz feels: the headset.
29. [x] **A pulse as the gun changes** (the owner: "could also add immersion for switching weapons in other games").
    Done in the code: Jak II and 3's gun-util.gc `vr-gun-switch-check!` (in play each frame, first or third person)
    tells the runtime when the game's chosen gun changes (`pc-vr-gun-switched!`), a short click in the gun's hand with
    **Buzz on gun switch** (on, guns only); the log says `[vr-gun] the gun switched`. Checked on the PC sim with item
    28's test (batch 32): `[vr-gun] the gun switched (the game's gun 1)`, then 3, then 2, once each change; the button
    tests' left grip in Jak II and 3 the same (batch 31). The click's feel: the headset.

30. [x] **Jak 1's arm wraps follow the hands and the world** (the owner, 2026-11-15: "in Jak 1, seems like there's these
    wraps attached to his arms/hands. Make sure the wrap physics actually react properly to hand movements and the
    world around them, since it doesn't seem to be synced right. If there's any similar body meshes like that in the
    other games, make sure they do the same"). The loose cloth on Jak's arms (and whatever hangs off him in Jak II and
    3: the scarf, the goggles' straps, Daxter's ears and tail) swings with the VR hands and stops at walls.
    Done: Jak 1's wrist straps (learn_shape finds them in his skeleton: two loose ends a wrist, 18>19 and 50>51 on the
    left hand's 17, 24>25 and 62>63 on the right's 23) are swung by the runtime each frame from the posed hand
    (jak_vr.cpp, a Verlet chain: a spring back to their made shape, kStrapStiff 12, gravity, damping), stopped by what
    the hand is at (the game's answers to a probe along each strap), and started again when the hand jumps. Jak II's
    and 3's arms have nothing loose (their bands and guards are skinned to the forearm, so they go with the posed arm);
    Jak 3's cape and scarf are the game's own cloth hanging from his back and neck, cut with his body by Body shown;
    Daxter is left out of the eyes in first person. Checked on the simulated headset (tests/jak1_straps.sim,
    JAKVR_BODY_TRACE=1, batch 21): the straps hang about 33 cm whichever way the hand is turned, swing as it moves and
    settle (a sway of about 5 mm left).
31. [x] **The JET-Board in first person too** (the owner: "1st person vehicles/everything toggle should include the
    jetboard too of course"). The one switch covers everything the games move to third person for: the Zoomer, the
    flut-flut, the cars, the JET-Board, the Titan suit, the turrets and the like.
    Done: First person in vehicles (item 25, off to start) takes the JET-Board with the rest, and now slides and turrets
    too (each game's `vr-vehicle-first-person-ok?`; its help in the VR menu and the launchers: "On the Zoomer, the
    JET-Board, in cars, turrets, slides and the Titan suit, seen from his eyes too, turning with it"). Checked on the
    simulated headset (tests/jak2_board.sim with C:\JakQuest\sim\gkcfg-fpv, batch 17): no switch to third person on the
    JET-Board, the pictures from Jak's eyes on it in Haven City.
32. [x] **Adaptive cutscenes** (the owner: "is it possible to auto switch to 3rd person in cutscenes where jak/daxter
    aren't really in the scene properly, and then switch back when they are? Like for the intro cutscenes of jak 1 for
    example, he seems to be in the ship, etc. Have this as a toggle like 'adaptive cutscenes' ... On by default").
    A scene seen through Jak's eyes goes to the around-you view by itself while Jak is not properly in it (not drawn,
    far from what the game's camera shows, riding something), and back through his eyes when he is; its own switch
    **Adaptive cutscenes**, on to start.
    Done: jak_vr.cpp `update_scene_fit` (the VR menu's and the launchers' Adaptive cutscenes, on to start; held menu
    button in a scene: your own choice for the rest of that scene). Through his eyes needs Jak drawn, his body at his
    own place (not carried by a boat or a lift: his joints away from his root) and near what the game's camera shows;
    a cut in the game's camera (0.5 m in a frame) asks again at once, anything else must hold for a moment, so the view
    does not flap. Checked on the simulated headset: Jak 1's opening (tests/jak1_scene_adaptive.sim) is around you
    for the boat and Misty Island ("Jak's body is not at his own place") and through his eyes from the silo on, one
    switch each way; Jak 3's opening (tests/jak3_open_adaptive.sim) two switches; Jak II's new game one.
33. [x] **Punching for real in Jak II and 3 answers less than in Jak 1** (Vixel, 2026-11-14 23:08 UTC: "i feel like
    physical punch in 2 and 3 is less responsive than in 1 but i'd have to test more").
    Found: with Fists on the right grip (on to start) only a fist punches. In Jak 1 both grips make fists (the game
    has no use for the left one); in Jak II and 3 the left grip is the game's L2 (Dark Jak, Light Jak), so only the
    right hand made a fist there and every left-hand punch went unheard. Done (jak_vr.cpp fill_pad): in Jak II and 3
    the left grip makes the left fist too, and stays the game's L2 as before; not at a holster or on the drawn gun's
    front, where the grip is the gun's. Checked on the simulated headset (batch 25, tests/jak2_left_fist.sim): "fists
    up: the left grip", "punch: the left hand ahead at 22.1 m/s" (target-running-attack), no punch from the right hand
    with only the left grip held; Jak II's punch, spin kick and uppercut by the right hand at life size (batch 30:
    16.7, 19.8 and 23.3 m/s). Headset: Vixel's feel of it.
34. [x] **Rolling feels off** (Vixel, 23:20 UTC: "rolling feels off but that could be just user error, might be worth
    looking into").
    Found (sim b22, tests/jak1_roll.sim): the view stayed at his standing height through the roll while his body went
    1.5 m and more under it, so his arms let go of the player's hands until he stood again, and what was left of him
    tumbled below the view. Done: in his roll (each game's pc-vr.gc: Jak 1 target-wheel, Jak II and 3 target-roll, the
    camera flag 1024) the view goes down with him as in a crouch (jak_vr.cpp first_person_anchor), and his body and
    legs fade out of the eyes for it and back after (item 40), the hands staying on the controllers. Checked (batches
    28a and 29a): the roll shows the two hands only, the view low through it and back up after. Headset: Vixel's feel
    of it.

35. [ ] **A polish pass over everything** (the owner, 2026-11-15: "refine whatever else you see fit, even double
    checking certain work that may have already been 'done' just incase you spot room for refinement. this should be
    as refinded, vr native feeling, and proper as possible ... proper working IK, satisfying world collision with
    finger bending to the world, accurate body ik physics with the wraps on jaks arms in jak 1 for example working
    well and reacting to the world accurately, proper gun handling without weird gun effects or mismapped animations,
    etc etc. as polished as can be with everything established in mind"). Each of the body's parts looked at again on
    the simulated headset in all three games (arms, fingers at walls and floors, the wraps, the gun in the hand and
    its animations), with whatever looks off fixed, and the items marked done above looked at once more.
    [~] Looked at again on the simulated headset (builds 30 to 42), what was off fixed under item 40 and 42:
    - The moves in all three games, from the eyes (batches 32 and 41: kick, punch, jumps, crouch, uppercut, long jump,
      dive; Jak II's uppercut no longer swaps his shoulders across the view; Jak 3's knees at most 16 cm a frame).
    - Fingers at the floor in all three (batch 41: fingers, fingers2, fingers3, the hand pushed 2.2 m into the ground at
      four turns of the wrist: held at the floor, the fingers bent up against it); the hand at a wall (wall: held, let
      go); the wrist straps (batch 38: clean); the arm poses (poses: steady, 0.1 to 0.3 cm a frame standing).
    - The guns (batches 32 and 41: gun2, gun3: drawn, aimed, two hands, fired, the magazines on the gun; the gun's
      nearest joint 23 cm from the eyes at rest, 33 aimed); the d-pad's gun change (item 28, 29).
    - The menus (items 19 and 42): the pointer and the sticks in all three games' menus; Jak 1's menus did not take the
      stick at all (fixed, item 42).
    Left: the headset itself (HEADSET_CHECKLIST.md), and anything the owner or players see there.

36. [x] **More performance options** (the owner, 2026-11-15: "see whatever can be done to boost performance on the games
    too. optional and off by default of course, but see about refining appsw, adding any other possible performance
    boosting options. i know get rich added proper asw and things on his standalone rdr1 port, but yea, just check other
    points of reference if possible to be sure all the options are there"). Smoother frames (AppSW) made right (no
    flicker on the title and menus), and whatever else other standalone ports offer that fits here (a resolution that
    follows the load, a lower refresh rate, stronger foveation and the like), each its own switch, off to start.
    The reference the owner gave (2026-11-15): https://github.com/dubrovskiy-yevhen-stakelogic/rdr1-quest (read for
    what it offers and how; no licence stated, so none of its code is used).
    Done, each in the VR menu or the launchers and off (or as before) to start: **Smoother frames (AppSW)** reworked:
    motion only in play, scenes and a pause over them (never the title, its menus or a load, where 0.1.0 flickered),
    begun only after 20 drawn frames in a row with both eyes' motion, then given every frame (a cut marked to be
    skipped, not dropped), so the headset no longer flips between half and full rate; **Game frame rate** (60, the
    headset's, or half the headset's: the game paced from the display, 36 a second at 72 Hz, the headset turning each
    picture to the head in between); **Dynamic resolution** (the eyes drawn smaller while frames come late, back up when
    they are in time); **PS2 detail** (far-off things, effects and shadows as the PS2 drew them); **Sharpening** (the
    Quest's own, normal or strong); **Foveated rendering** gains **Highest**. The Refresh rate choice was there already.
    Checked on the simulated headset (batches 21 to 23): half rate "the game at 36 frames a second (the headset shows
    72)" in all three games, Jak 1 walking to the same place at 36 as at 60, Jak 3's clocks at 36 (time-factor 8.33,
    JAKVR_CLOCK_TRACE) keeping the game's time; the headset's own rate "72 frames a second"; Dynamic resolution stepping
    90 to 60% while frames are marked late (JAKVR_TEST_LATE) and back up after. Smoother frames (batch 25, warp1, Jak 1
    at half rate): none on the title, its menus and the loads ("none made here (a menu, the title, a load or a flat
    screen)"; batch 23's 0 was the title), then in play "the headset makes the frames between from here", "motion on
    168 of the last 300 frames, drawn anew on 84, marked to skip on 84". How it looks is the Quest's to show.

37. [x] **Walking straight looks diagonal** (a player, passed on by the owner 2026-11-15: "Also, I have a problem with
    walking straight. I allways look like I am walking diagonal when I press forward ... In all 3 games").
    Found: the runtime gave the games' pad each axis of the stick with the games' dead zone added to it on its own
    (jak_vr.cpp `jak_stick`: 49 + 61 for each axis), so a thumb a hair off straight (x 0.05, y 1) went to the game as
    x 52, y 110: a walk 25 degrees off, in all three games. The sims never showed it: their stick's x was exactly 0.
    Done: the dead zone is taken off the stick's length, its direction kept (`jak_stick(x, y, byte_x, byte_y)`), with
    the pad's square corners still reached on a full diagonal. And a second, smaller cause: the games turn the stick by
    the camera, then by the shortest turn that takes the camera's up onto the ground's (read-pad and
    warp-vector-into-surface!, the same in all three), so a head tilted to a shoulder while looking down or up walks
    him to one side (12 degrees of tilt looking 40 degrees down: 4.4 degrees); fill_pad turns the stick back by the
    difference between that flat ahead (ahead less k times up, k = ahead.y / (1 + up.y)) and the head's own. Checked on
    the simulated headset (tests/jak1_walk_straight.sim, JAKVR_BODY_TRACE=1): straight ahead, looking down and with the
    head turned 60 degrees, Jak's heading is the head's; the stick full right, 90 degrees right of it; the stick at
    x 0.1, y 1, 0.10 rad (6 degrees) off the head, not 25; the head tilted 12 degrees looking 40 down and 35 up, his
    heading 0.30 to 0.32 rad against the head's 0.31 (batch 24).

38. [x] **A clear Mods section in the launchers, with a mod browser** (a player, passed on by the owner 2026-11-15: "your
    launcher says something about mods but i only see a thing saying texture packs, witch i couldn't find texture packs
    for regular open goal way back when i looked a long time ago"; the owner: "maybe make it more clear. add a browse
    mods section similar to our openrac mod with a mod updater, disabler, etc. just make it so poeple arent confused").
    The launchers' Mods (PC and Quest) say plainly what can be added and where to find it, browse the mods there are
    for each game, install, update, turn off and remove them, as Ratchet & Clank Quest VR's launcher does
    (C:\RACQuest\RatchetQuestVR, read only).
    Found: there are no texture packs to find for the plain games. jakmods.dev's list (the one the OpenGOAL launcher's
    Mods reads; 63 mods on 2026-11-15: 45 for Jak 1, 17 for Jak II, 7 for Jak 3) has none, and every mod on it is a
    whole game build of its own (its own gk and game code) made for a flat screen, which cannot run in VR here.
    Done: both launchers' Mods start with "What works in Jak and Daxter Quest VR" (Multiplayer; texture packs, from
    Browse mods or a .zip; on the Quest, mod packages; OpenGOAL's own mods are flat screen only and why) and end with
    **Browse mods**: this project's own list (`mods/mods.json`, OpenGOAL's mod list format, empty to start) and
    jakmods.dev's, read on the button and as the launcher starts while automatic update checks are on, the copy from
    before kept for when a list cannot be read. Per game: Add, **Update to ...** (also on the added pack's card: the
    new one takes the old one's place and On / Off), Added, Its page; OpenGOAL's own mods behind Show them, marked
    Flat screen only, each with its page and **Open jakmods.dev**. Downloads only from GitHub over HTTPS, checked
    against the list's size and SHA-256 when it gives them, then added as a chosen file would be. On / Off, order and
    Remove are the Texture packs' (and the Quest's mod packages') own, as before. Shared code:
    `launcher/common/src/dev/jakquest/vr/ModList.java` (and `Json.java`, moved there from the PC launcher); how to
    list a pack: docs/MODS.md. Checked: an offline test of the list reading, version order, the allowed hosts and
    Add / Update of a pack (20 checks); the PC launcher builds with `-Werror`, the Quest's code compiles against
    android.jar. Not checked here: reading the lists over the network and a real download (the headset checklist's
    Mods rows). `mods/mods.json`'s address answers only once it is pushed to the repository's main branch; until then
    Browse mods says this project's list could not be read.

39. [x] **The options read at start never reached the headset** (found 2026-11-15 on the simulated headset, Smoother
    frames' run; Vixel's 0.1.0 log shows it too: "the headset started" at 13:58, the options read at 14:00). The headset
    starts on the display's first frame, seconds before the game reads its options (set_game, then load_options on
    the kernel's thread), so everything the headset takes as it starts ran at its defaults whatever was chosen: Eye
    resolution, Refresh rate, Smoother frames, Foveated rendering, CPU/GPU boost (and now Sharpening). Done:
    start_headset reads the options first when they have not been read yet (vr_options `options_loaded()`). Checked on
    the simulated headset (batch 23, C:\JakQuest\sim\gkcfg-warp): "options from ...gkcfg-warp..." before "the headset
    started", and Smoother frames' half rate from the start ("the game at 36 frames a second (the headset shows 72)").

40. [~] **The body's refinement pass: IK, how he is shown, parts fading out of the view** (the owner, 2026-11-15:
    "certain actions seem to clip in the eyes ... refine ik and body representation, to be sure its all as smooth as
    possible", then "move ahead the refinement pass on body ik, representation, body part fading (so it doesnt obscure
    view, basically make it as smooth as possible though and not too jarring)"; "have the body fade toggle on by
    default"; "if upright body helps with stability and ik too, then also enable it by default"; on life size: "if you
    think itll work in vr for most players to help with immersion ... have it on by default as well").
    Found on the simulated headset (batches 25 to 29): (a) 0.1.0 showed a game metre as a real one, and Jak's eyes are
    2.3 m up, so the player was a giant 1.46 times a person, his arms 1.85 and his hands and the gun twice a real hand;
    (b) his hips, chest and all above them turned and tipped as the game's animation had them (a spin kick spins them
    right round, a punch twists them a quarter turn, a dive lays them flat), and with his neck held at the player's
    that swung his shoulders, chest and thighs through the eyes; (c) the games scale his limbs up in attacks (Jak 1's
    punch: both arms about 1.9 times, the wrist straps' tips 0.56 m from the wrist against 0.30), so an arm posed to
    the controller was a giant's; (d) the uppercut spins the game's facing in the air, and the hips snapped to it (the
    body 150 degrees from the head, a shoulder 16 cm before the eyes, its upper arm half the view); (e) the body was
    held at most 0.8 m under the player's neck, so in a button crouch or the uppercut the chest stayed at the eyes,
    and 0.3 m over it, so a hard landing sagged the shoulders; (f) 0.1.0's near-eye cuts never fired (the eyes' place
    in world space against the bones in model space); (g) Jak 1's look-around (Triangle) stops the game working out
    his joints, and the pose then built on its own output each frame (his neck read back 10^18 m off, the view 0.8 m
    ahead of him: sim b26 buttons1).
    Done (jak_vr.cpp body(), vr_options, vr_menu.cpp, VrOptions.java):
    - **Life size**: World scale 100% (the start) is life size, Jak as tall as the player (the game shown 1.45 times
      smaller: kLifeSize); 145% is 0.1.0's look; a saved "world-scale 1" is now life size. The hands, the HUD, the near
      fade and every distance in the player's metres follow it.
    - **Keep your body upright** (on): his hips stand upright as made, facing the hips' own heading; the hips stay
      while the head turns, then follow past 35 degrees (kBodyTurnDeadZone); moving or in the air they go the game's
      way, but at most 57 degrees from where the head looks (kHipsFromHead) and back to the head's way when he runs
      back toward the view, never at once. The chest, neck and head as before on top. Off: 0.1.0's.
    - **His limbs at his own size**: the game's stretch of an arm (from the collar) or a leg in an attack undone each
      frame before the pose (`unstretch`: each joint's rows the size of his hips', every joint under re-seated), the
      log saying `[vr-body] the game's stretch of his limbs undone` and `over`.
    - **Held under the player's neck** up to 0.8 m aside, 0.9 m up and 2.0 m down (the legs fold), so a crouch or an
      uppercut no longer leaves his chest at the eyes, nor a landing his shoulders low.
    - **Fade body near your eyes** (on): a leg within 0.60 m of the eyes, or up by the face (within 0.55 m under their
      height and 0.90 m of them), fades out of the eyes; his body and legs fade in his roll, held turned over past 70
      degrees, or with his chest within 28 cm; an arm only with Hide arms near your eyes (item 20). Each comes back past
      a little more (0.72, 0.68 and 1.00 m, 0.33 m, 60 degrees). Eased: out in 0.08 s, back in 0.28 s, held 0.35 s after
      the last frame that asked, a smooth step, dithered in the shaders through the bones (merc2/emerc `vr_fade`), the
      same frame the pose asks it; his shadow keeps all of him. What is left of him and the gun fade within 16 cm of
      the eyes, gone by 7 cm. The log: `[vr-body] the left leg fades out of the eyes (N m from them, its highest ...)`,
      `[vr-body] his body and legs fade out of the eyes: his roll | the game holds him turned over | his chest at the
      eyes (...)`, and `comes back to the eyes`.
    - **Never posed twice**: a bone found just as the pose wrote it last frame (the game did not make it again) is
      taken as the game last made it, for his skeleton, his gun and Daxter (`[vr-body] the game left N bones as posed
      last frame` / `the game makes his bones again`); a head place far off his root no longer moves the view's lean.
    - **The hips through a spin**: a turn of the game's facing in the air faster than 15 radians a second (Jak II's
      uppercut spins him right round twice) is not followed for 0.25 s after it, so the hips and legs no longer swing
      40 degrees to and fro under the view (b32 act2t; checked b33 and b35 act2t).
    - **His spine at its own size**: Jak 3's jump launch moves his spine joints apart (one 0.19 to 0.48 m from the one
      under it), which stretched his chest up through the view; each spine joint from his main joint to his neck is put
      back where it sits as made, the feet kept (`[vr-body] the game's stretch of his body undone`, past 5 cm). Jak 3's
      jump: hips 28 cm in a frame to 0.2, chest 5 to 0.1 (b34 against b35 jump3); Jak 1 and II's chest and hips 0.1 cm
      a frame through every move where they were 6 to 19 (b35 act1, act2t).
    - **Each limb bone at its own length** (collar to shoulder, upper arm, forearm, thigh, shin): the same launch moves
      the arm and leg joints apart too, and an arm a few cm longer for three frames swung its elbow 15 cm. Jak 3's
      jumps: the elbows 14 to 16 cm in a frame to 2 or 3 (b35 against b36 act3).
    - **The knee's side eased** in his hips' own axes (25 a second, 3 while the leg is near straight, where its side
      means little): Jak II's spin kick no longer pops the standing knee 27 cm (b35 act2t). And **a leg's reach given
      softly** past 95% of it, so a leg near straight never snaps its knee (Jak 3's jump launch: 22 cm in a frame, b36).
    - **The feet go round with the hips** where the game's animation turns his hips far from where they face: Jak 1's
      spin kick spins his main joint right round while his facing stays, and the legs reached for feet circling 0.8 m
      round the upright hips, half a turn every two frames (b36 act1t, frames 3373-3385; a knee 1.7 m in a frame). Only
      the turn about the up axis, from 35 degrees off (under it, a walk, a run: the feet where the game plants them).
      Batch 37 (build 36): through the kick the worst knee in a frame went from 0.85 to 0.43 m, but the carry came and
      went frame to frame (his heading read from the way his front points, which the kick's tilt shakes), and in the
      long jump, held tipped past 90 degrees, his front pointed back and the feet were carried half a turn, an ankle 3
      cm from the eyes (frames 3993-3995). Done (build 37): the heading is the twist of his hips about the up axis; the
      carry starts past 35 degrees and stays until back under 17, eased in over 0.06 s and out over 0.2, and only while
      he is held within 53 degrees of upright (all of it within 32).
    - **Riding** (the JET-Board, a vehicle with First person in vehicles): batch 37's board (Jak II) had the knees
      jumping 23 to 110 cm a frame. He rides the board sideways; the upright hips faced the board's way over feet the
      game keeps sideways on it (a quarter-turn twist in each leg), and the feet carry turned the feet toward the hips,
      off the board. Done (build 37): riding (kFlagBoard, kFlagVehicle), his hips go with the game's facing, stand
      upright but keep the game's own turn about the up axis (sideways), the chest turns from there toward the head
      (up to 69 degrees), the knees and neck from that, and no feet carry. To check: batch 38's board.
      Batch 38 (build 37): first person stays on the board, but the knees still jumped 23 to 94 cm while riding: the
      view sat at his standing eye height over his root, and the board's root is not under his feet, so his neck was up
      to 1 m over the view, the body was pulled down to it and the legs folded flat against the board. Done (build 38):
      riding, the view follows his eyes' own place from his root (eased over 0.15 s); a car's view sits at his eyes too.
    - **The game's skeleton leaving his root for a frame or three** (batch 38 act1t, Jak 1: frames 3963-3965 in the walk
      and 3987 at the roll's start, the same frames in batches 36 and 37): his whole skeleton drawn 0.9 to 2.6 m off his
      root while the root goes on, beside the view. Done (build 38): a move of his main joint from his root of more than
      0.6 m in a frame is held where it was for up to 8 frames, then followed eased (not while riding); logged as "the
      game's skeleton left his root by ...". Batch 39 (build 38): Jak 1's pops gone (held at frames 3969 and 3978, back
      after 3 and 5 frames); but it also held Jak II's own moves: the uppercut's launch (1.14 m up, frame 4223: held
      down, his legs stretched to the ground) and the dive (0.62 m, frame 4500). And the board (Jak II) still popped for
      a frame every second or so (frames 4900, 4949, 4999, 5016: his neck 0.4 to 0.74 m aside, the feet 0.5 to 1 m off).
      Done (build 39): only the move across is measured and held (a lift or drop goes through as the game has it), from
      0.8 m on foot; riding too, from 0.35 m (getting on or off starts it again). To check: batch 40.
    - **The legs in the air** (batch 38 act2t, Jak II's uppercut: a knee 0.87 and 0.96 m in a frame, frames 4234-4235):
      his tucked feet circled his upright hips, as the stand-up turned the hips but left the feet where the tipped,
      spinning skeleton had them. Done (build 38): off the ground three frames running (not riding), the feet ease over
      0.1 s into turning and moving with the hips (out over 0.12 s on landing), so his legs keep the game's pose from his
      hips in a jump or flip; the feet carry gives way to it. Batch 39 (build 38): the uppercut's knees at most 0.17 m
      in a frame (0.96 in batch 38); act3, jump3, roll, down, poses and melee2 as before.
    - **The feet carry steadier** (batch 38 act1t, Jak 1's running punch and spin kick: the carry 0.03, 0.33, 0.28,
      0.53, 0.64, 0.46 frame to frame, a knee 0.86 m in a frame): the eased carry only rose; the swing was how upright
      the game holds him, by which it is multiplied, read fresh each frame as the moves shake his hips' tip. Build 38
      followed that over 0.06 s; batch 39 showed the real cause: the spin kick's knee still 0.92 m in a frame (3383), as
      the carry stopped mid-spin when his hips passed his facing (the turn followed to 316 degrees, then -2, then 111)
      and its share swung the feet half a turn. Done (build 39), the carry reworked: while carrying, the feet turn all
      the way with the hips (no share, no "how upright" multiplier, the 0.06 s smoothing gone); only the feet's jump as
      the carry starts or stops eases away (0.03 s in, 0.08 s out); it stops only once the turn is under 17 degrees and
      has swung less than that since the frame before, or he is tipped past 53 degrees. Batch 40 (build 39): the
      carry follows the whole kick smoothly (the turn followed 80, 118, 279 ... 811 degrees, the feet with it); the worst
      knee in a frame 0.46 m in the kick (0.92 in batch 39) and 0.56 in the running punch (0.86), now where the game's
      own spin turns him up to 100 degrees a frame; the long jump no carry. The guard: Jak 1's pops held (3967, 3976,
      3980, back after 3 to 9 frames), none in Jak II (the uppercut's lift and the dive go through), and the board's
      one-frame pops held while riding (4945, 4995, 5012, each back the next frame); no pop left in the ride.
    - **The body's eases by the game's frames** (batch 40 act1t, frame 3397: after the kick both knees crossed over in
      one frame, 0.5 m, the ankles still): the knee's side, the elbow's hint and the wrist's pitch were eased by the
      wall clock, so a slow frame (a hitch; the simulated headset with the body trace runs under one game frame a second)
      let each go all the way at once, or started it again. Done (build 40): by the game's frames (body_ease_dt), started
      again only when one did not run in the last 30 frames. On the headset at full rate nothing changes; in the sims
      the knees and elbows now ease as on the headset.
    - **The crouch** (batch 40 duck1, build 39: crouched, his hips 0.11 m over his ankles, both knees 0.13 to 0.3 m under
      the ground; looking down, his belt right under the eyes and no legs, picture dd-duck-70): his spine upright, the
      lowered view brought his hips down to his feet. Done (build 40): on the ground (not riding, not in the air or a
      roll), his hips are kept at least 0.4 of his leg's reach over his lower ankle and his trunk leans forward from its
      base (up to 57 degrees, eased over 0.08 s) to bring his neck down to the player's, the body moved back by as much
      as the lean brought it forward, as a person crouches. The same for the player's own crouch. The parts trace ends
      "his trunk leans N". To check: batch 41 (duck1, duck2, duck3).
    - **The knee as a crouch starts** (batch 41 act1t, build 40: the game's ankles jump 0.58 m up toward the hips as
      he enters duck stance, and the eased knee side swung a knee from crossed past his middle to far outside, 0.74 m
      in a frame). Done (builds 41 and 42, pose_leg): a folded leg's knee goes forward, up and a little out, as in a
      person's squat (from 0.8 of the leg's reach, all of it at 0.45), as far as that way stands off the line from hip
      to foot (along it, a roll's tuck, it gives no side: the game's own bend there); and a knee's side never more than
      0.2 toward the other leg. Batch 43 (build 42) against batch 41, the worst knee jump in a frame: crouching 0.50 m
      (0.75), the roll 0.66 (0.93, his body faded there), the rest the same; the duck pictures as before.
    - **Knees in Jak 3** (batch 37, build 36): with the soft reach, the moves' worst knee in a frame 35 to 23.5 cm (act3);
      the jump's 13 to 23 cm come at take-off and landing with the chest and hips steady (0.1 cm): the legs reaching
      for the game's feet as the eased view lands, under the view looking ahead.
    - **The stale bones** (Jak 1, `the game left N bones as posed last frame`): body() runs once a game frame; of
      Jak's own bones only nodes 82 and 83, which the game never writes (their last column all zero, made affine every
      frame), the rest of the 131 being Daxter's, who is out of the eyes in first person. Nothing of Jak shows frozen.
    Checked on the simulated headset: life size against 0.1.0's scale in the same poses (batch 27: the gun and limbs
    clearly smaller; the gun's nearest joint 23 cm from the eyes at rest, 33 cm aimed); the moves on foot in Jak 1 and
    Jak II (batches 29a and 30): punch, spin kick, jump, crouch, high jump, uppercut, long jump and dive show the hands
    and, looking down, an upright body; the legs and body fade only in the roll, a flip or dive, and a kick by the
    face; standing still the elbows move 0.1 to 0.3 cm a frame, the hands 0.0, the knees at most 1.5; physical melee
    at life size (batch 28a: punch 23.3 m/s, spin kick 29.5, the left punch 12.8); the roll; the look down (forearms and
    legs, no chest in the face); the arm poses and the fingers (items 20, 21, 24).
    The last three looked at again on build 48 (batch 52): Jak II's uppercut (b30 upper-20 had a part of him within
    16 cm of the right eye): the nearest joint now 17 cm at most, before the fade's 16, and upper-10 to upper-40 clean
    (his hands at the bottom, nothing of him in the eye); Jak 3's moves (act3): no error, the knees at most 16 cm in 30
    frames, the chest and hips 0.1 to 0.2, the legs and body fading only in the roll and the flips; Jak 1's look-around
    (look1) through his eyes clean. Left: the headset itself, the checklist's "Life size" and "His body under you".
41. [~] **Max draw distance draws everything, shadows too** (the owner, 2026-11-16: "max draw distance means EVERYTHING
    should be rendered, including shadows. fix that (as long as it wont cause issues) and proceed with list with this in
    mind"). Found: with it on, Jak II and 3 already draw every shadow at any distance (foreground.gc skips the fade
    when the PC port's ps2-shadow? is off, which Max draw distance sets), and every actor at its highest detail
    however far; Jak 1 has the same setting but never read it, so its shadows still went past their fade distance
    (100 m; an enemy's 60 m). Done: Jak 1's bones.gc skips that fade in VR with ps2-shadow? off, as the other two do
    (PS2 detail turns it on, and the fade with it; out of VR as before). Left as they are: the shadows the games turn
    off themselves for a state (an enemy's ragdoll, an NPC out of a talk), which are not distance. Built (38): Jak 1's
    runs with Max draw distance on (the sims' gkcfg: ps2-shadow? off; batch 39 act1t, poses, roll, down, zoomfp) ran
    with no error of their own (only the game's own fake-disc and sage-animation lines, as before). The far shadows
    themselves (past 100 m) to see on the headset.
42. [x] **Jak 1's Load Game started a new game** (a player's report, passed on by the owner 2026-11-16: "load game is new
    game ... so if you don't beat in 1 go you don't beat it ... only jak 1 has this problem"; the owner: "they may be
    confusing it with the input just clicking something else by accident, not that load game itself is broken").
    Found: the menu pointer chose the row under the dot whenever the dot moved 1.5 of the screen's 512 units (a fifth
    of a degree of the hand), which a hand held still on a headset does all the time. So with the dot resting on
    New Game, the stick's move down to Load Game was taken back at once and A picked New Game. All three games' menus
    work that way (Jak 1's title has New Game on top, where a resting hand points). Ratchet's pointer takes the cursor
    only as the dot reaches another row. Done (jak_vr.cpp menu_pointer_at, for all three games): the dot counts as
    moved only 8 units (about 2.6 cm on the menu, near a degree) from where it last did, and either stick pushed in a
    menu hands the choice to the stick until the hand really moves again. A trigger click still picks the row under the
    dot. To check: tests/jak1_ptr_stick.sim (batch entry stick1: the dot on a row shaking 0.3 degrees, the stick down
    twice, A), then the pointer sims (ptr2, ptr3, scroll1, scroll2) unchanged.
    **The main cause, found by that test (batch 42, build 41):** the stick down twice moved nothing at all, dot or no
    dot. Jak II and 3's menus read the left stick themselves (`cpad-pressed? 0 down l-analog-down`); Jak 1's read the
    d-pad alone (progress.gc, progress-pc.gc: `cpad-pressed? 0 down`), and in menus the VR controls gave the game the
    stick as a stick and no d-pad. So in Jak 1's menus the stick did nothing: the highlight stayed on New Game (the top
    row, chosen to start) and A started a new game, which is the report ("only jak 1"). Done (fill_pad): in Jak 1's
    menus and title both sticks are its d-pad (a direction in past half way, out under a third, as the d-pad's own),
    without the d-pad's buzz; the log says `[vr-pad] d-pad down (a stick in Jak 1's menu)`. Checked on the PC sim
    (batch 43, build 42, stick1): with the dot resting and shaking on Options, the stick down twice moved the highlight
    to Quit Game and it stayed there; A opened Quit Game's "Are you sure you want to quit?" (batch 42 on build 41: the
    highlight never moved and A opened Options). So the stick down to Load Game and A now loads. The headset check:
    HEADSET_CHECKLIST.md's "The stick in Jak 1's menus".
43. [~] **The underwater city: "a certain cutscene seemingly broke the game"** (a player, passed on by the owner
    2026-11-16: "got to under water city and a certain cutscene seemingly broke the game"; no log with it). Jak 1's
    Lost Precursor City (sunken, sunkenb) and Rock Village's elevator down to it, each scene there played on the PC sim.
    New test aid: `JAKVR_TEST_WARP=<actor>` (pc-vr.gc vr-test-warp!, kmachine.cpp pc-vr-test-warp!): once Jak has been
    in play on foot for 3 seconds after the test continue, he is put a metre over that actor (its entity name, or the
    first of its type), the game's camera restarted beside him; `list` logs every actor of the levels loaded, and
    `JAKVR_TEST_WARP_OFFSET=dx,dy,dz` puts him that far from it instead (the pipe game's buttons, which are not
    entities). The moment is the sim's aligned frame (`QUEST_SIM_ALIGN`, kmachine.cpp pc-vr-test-ready!). Tests:
    tests/jak1_warp_list.sim, tests/jak1_city_scene.sim (batch entries wl1 to wl4, elev1, sq1, sq3, pipe1, pipeR, pipeM,
    helix1, chamber1).
    **Found (batch 47, build 45, helix1 and wl2):** entering Sunken B (the helix room) crashed the game: `kmalloc:
    !alloc mem in heap for #<process launcher-115 ...> (80 bytes; its heap 896, 64 left)` (a blue eco launcher). The
    launcher's heap is #x400, and since 0.1.1 the 512-byte stack least of stack-size-set! (item 3) holds on PC too,
    while entity birth added 512 to an entity's heap only on the Quest. So on PC 0.1.1 every Jak 1 blue eco launcher
    would have crashed (0.1.0 had neither the least nor the crash), and Jak II and 3's smallest entities could on both.
    Done: entity birth adds 512 to the heap in all three games on both (entity.gc birth!; the heap is shrunk to what
    the process used after its set-up, so it costs nothing after). The process-heap message now names the process and
    its heap's size and room (kscheme.cpp), for the next such report.
    Checked on the PC sim: batch 48 (build 47) helix1: no crash entering Sunken B; the helix button's scene took Jak
    and let him go. elev1: the ride and its camera worked, but the city was not drawn at the bottom, because the warp
    put Jak on the elevator before the city had loaded and the elevator's request to show it was refused ("can't
    display sunken because it isn't loaded"): the test's doing (a player who walks to it has it loaded). So the warp
    aid got `JAKVR_TEST_WARP_APPROACH=1`: first beside it at the offset, then onto it once the levels there have loaded
    (entry elev2). Batch 52 (build 48): elev2: the elevator's scene, the city shown, the ride down, Jak in the glass
    room at the bottom with the city drawn round him; pipeR: the pipe button's scene, Jak let go; sq1: the dive on the
    floor button raised the square platforms (its scene, then Jak let go). Every scene reached in the city played
    through; the player's "a certain cutscene" was not found (no log with the report), so [~] until a log names it.
    The release notes ask for one.
44. [x] **The pipe game's buttons left Jak unable to move** (Vixel, passed on by the owner 2026-11-16 with his log,
    0.1.0 on a PC: "3 glass tubes at back of room, i press blue 1s button screen got glitchy and i couldn't move";
    Lost Precursor City's pipe game, its three buttons at the back of the room). His log: a real punch in the air (the
    dive) pressed the button and the pipes' scene began (the view in scene mode, Jak held: target-grab), and within a
    second the view was back in play mode with Jak still held, the game's camera behind him and still: the scene's
    view (othercam, a child of the scene's camera) was gone, so the scene's camera (pov-camera) had died before its end,
    which is what lets him go (Jak 1's target-grab always goes back to target-stance on it). He never moved again, and
    five seconds on the eyes showed garbage. Nothing in the log says why it died (no error, no heap or stack message).
    Played on the PC sim on 0.1.1 (tests/jak1_city_scene.sim with the warp aid's offset onto the right and the middle
    button, entries pipeR and pipeM): batch 49 (build 47, Max draw distance on), batch 50 (the same, Max draw distance
    off and the game's own actor culling on, C:\JakQuest\sim\gkcfg-mdoff): each time the dive pressed the button, the
    pipes' scene played (about 6 s), Jak was let go (target-stance) and walked away. Not seen on 0.1.1.
    One cause looked at: 0.1.0's PC kernel gave every process 256 bytes of backup stack and let stack-size-set! go
    below that (the least of 512 was the Quest's only), and Jak 1's release kernel does not check the stack as a
    process suspends, so a frame past it would write over the thread's saved registers without a word. Batch 51: Jak 1
    compiled with exactly 0.1.0's two stack lines, pipeR and pipeM: both played the scene and let Jak go, as on 0.1.1
    (then 0.1.1's kernel put back). So not the stacks, at least not on the sim. The scene's camera belongs to the pipe
    game (spawned `:to` it), which never kills it itself; only the pipe game dying, or the camera itself, could.
    Done, a net in all three games: a scene's camera (pov-camera) that dies while it holds Jak (with what it belongs
    to, or its level left, or anything else) lets him go as its end does (pov-camera.gc vr-pov-camera-let-go, logged
    `[vr] the scene <name> ended before it let Jak go: he is let go`); before, nothing would ever have let him go. Only
    a death in the scene's play or its skip calls it (the camera's normal end goes through done-playing first). The
    owners in Jak 1 that end a camera early themselves (taskables, the plant boss, the pelican, the fisher, kermit, the
    title) end othercams, not scene cameras, so nothing they do changes. The garbage in the eyes was the body's pose
    built on its own output while the game stopped working out his joints, fixed in 0.1.1 (item 40, the bones kept as
    the game last made them). If it comes back, the log line says which scene ended early.
    Checked on the PC sim, batch 53 (build 49): a new test aid, `JAKVR_TEST_KILL_SCENE=1` (kmachine.cpp
    pc-vr-test-flags 1024, PC only; Jak 1's pov-camera.gc), kills a scene's camera a second into its scene, as in
    Vixel's log. Entry pipeK (pipeR with it): the dive pressed the right button, the scene took Jak (target-grab), its
    camera died a second in, `[vr] the scene "pipegame-right" ended before it let Jak go: he is let go`, then
    target-stance, and he walked off as after the scene's normal end (the same pictures as pipeR's, no garbage). The
    normal end (batches 49 to 52: pipeR, pipeM, elev2, sq1, helix1) never logs it. Done: the fix is in. If a player
    still sees it, the log line names the scene.

Later, not this release (the owner: "won't rlly b priority till l8r l8r"): building the games from an OpenGOAL install
the player already has, without the disc image (Vixel).
