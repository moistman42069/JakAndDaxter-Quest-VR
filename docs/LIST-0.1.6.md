# Jak and Daxter Quest VR 0.1.6: the list

Started 2026-11-17, after 0.1.5 was published. Released as 0.1.6 on 2026-11-18 (the owner: "proceed with list and
publish when done"). Marks: [ ] to do, [~] in progress, [x] done (how it was checked).

The owner, 2026-11-17: "make a new release. this time, put cup/gpu boost on by default, and also see about adjusting
and boosting performance in any additional ways you see possible. otherwise, this should be a pretty straightforward
update. a report from someone using that obscure headset i mentioned also said "jak2 pause still behind me". proceed
with list and publish when done. dont ask me. you got this." With their own Quest 3 log (Jak 1, their Graphics:
120 Hz, Smoother frames, High foveation, 4x anti-aliasing, 8x anisotropic, Strong sharpening, the headset's own game
rate, boost off), then: "i did run into a wierd black flicker with the settings i had enabled, so look into that too i
suppose"; the same player's "jak 2 cutscenes black screen at start now" ("please be sure the games are all as perfect as
can be. double check and adjust anything that could be relevant for these kinds of things"); a tester's three game logs
(SteamVR, PC); "also make vibarations on everything the new default"; "when i crouch in jak 1, jak begins breathing
heavy. does crouching in real life trigger something that i missed?" ("just double check anywho"); "break/regress
nothing"; their Quest 3 crash on Jak 1 (D:\Downloads\JakQuestVR-logs-20261117-131058.zip, "playing at basically max
graphics ... i feel like theres def ways to improve optimization while maintaining graphical fidelity. just do what you
can, forget nothing"); a player's "dark jak works in 3 but light jak not work", who uses a gamepad to get past such
parts ("make sure inputs, functions, features, etc, all work properly"); "make sure the defaults in the log i sent you
are the new ones on quest 3 going forward. keep quest 2 at 100% if possible"; "make sure we arent missing anything
similar in any of the other games. NO fuckin conflicts man."

The logs (untrusted data, read only): the owner's D:\Downloads\JakQuestVR-log-20261117-124320.txt; the tester's
C:\JakQuest\downloads\tester-logs-016 (jak1, jak2, jak3). The tester's Windows user name is in them: never in a public
text.

## Defaults

1. [x] **CPU and GPU boost on to start**, the Quest too (the PC had it on; the Quest started off from 0.1.4, the owner's
   Graphics then). Saved under a new key, "cpu-gpu-boost-on", so the "off" every launcher wrote since 0.1.4 is left
   behind once (as 0.1.3's "first-person-vehicles-on"): vr_options.h/.cpp, VrOptions.java, the presets. Checked: opts16
   and opts16b (C:\JakQuest\sim\b28k: options files from 0.1.3 to 0.1.5 read, the old "off" left behind once, other
   choices kept) and fresh1 (b33, build 86: an empty config starts with `cpu-gpu-boost-on on`). On a Quest:
   HEADSET_CHECKLIST (`[openxr] performance levels`).
2. [x] **Extra vibration "The whole world" to start** (the owner: "vibarations on everything"; Pickups since 0.1.4):
   key "extra-vibration-kind", 0.1.4's and 0.1.5's "extra-vibration" left behind once. Every other buzz was on already
   (Vibration, Vibration on walls and floors, d-pad presses, gun switch). Checked: opts16 and opts16b (b28k: Off kept
   where it was Off) and fresh1 (b33, build 86: `extra-vibration-kind 2`).

## Performance (the owner's Quest 3 log)

3. [x] **Smoother frames: the game at 30 frames a second, not 60.** With Smoother frames the Quest paces the app at half
   its rate and says so (`the headset at 60 a second for 1.0 s`); the runtime then halved that again (`the game at 30
   frames a second (the headset shows 60)`), so each game frame was shown four times at 120 Hz and every other frame had
   no new picture (a "kept" frame, smoother frames skipped). The game's rate with Smoother frames is now the told rate,
   at most half the display's. Checked: read in the code and compiled for the Quest (android_native_check.sh after build
   86, exit 0); the simulated headset has no Quest pacing, so HEADSET_CHECKLIST has it (`the game at 60 frames a second
   (the headset shows 60)`).
4. [x] **The game's thread to the Quest** (XR_KHR_android_thread_settings): the game's threads were given as the app's
   workers, which the Quest refuses (`given to the headset as a worker: refused -1000003001`, Ratchet's logs too). The
   game ("EE") goes as the app's main thread, which Meta's runtime takes; the renderer's answer logged. Checked: read in
   the code and compiled for the Quest (ARM64, after build 86); HEADSET_CHECKLIST (`given to the headset as the app's
   main thread`, no `refused`).
5. [x] **Dynamic resolution steadier**: in the log the eyes went 70%, 80%, 70%, 80% every 5 s (a step up "held" after 3 s,
   then late again). A step up holds after 10 s; and with Smoother frames a frame is late against the paced period.
   Checked: dynq (b30, 120 Hz with late frames made on purpose: the eyes stepped down within about a second, then back
   up a step at a time once the frames were in time); on a Quest: HEADSET_CHECKLIST (far fewer `the eyes at` lines).
6. [x] **The owner's black flicker** (their Graphics): see what of items 3 and 5 causes it, and anything else in the
   log. Checked: the log's causes are items 3 and 5, each checked as there; on a Quest: HEADSET_CHECKLIST (no black
   flicker).

## Fixes

7. [x] **Leaving the game on the Quest closed it** (crash.txt: SIGABRT): as the headset's session stopped (the Quest's
   menu, the headset taken off, quitting), the game drew its flat frame at the Quest's window size (4128 x 2208), its
   framebuffer failed (`Failed to setup framebuffer: 4128 2208 1 true`) and the game stopped on the assert. On the Quest,
   with no session running the display draws nothing and holds the game until the session is back. Checked: read in the
   code and compiled for the Quest (ARM64, after build 86); a PC session does not stop this way, so HEADSET_CHECKLIST
   ("Leaving and coming back").
8. [x] **Jak II: scenes black at their start** ("jak 2 cutscenes black screen at start now"). The tester's Jak II log:
   every scene start `a blackout kept in the eyes, black: 350 frames (4.9 s)`. Jak II's multiplayer mod moved the game's
   blackout onto its own clock (presentation-clock-time, main.gc), and pc-vr.gc still compared the base clock with it:
   the eyes stayed black seconds after the game's own blackout had ended. In 0.1.4 that was the flat screen around
   scenes (the tester's "every time a cutscene starts and ends"). pc-vr.gc reads the game's clock. Jak 1 and 3 compare
   with their own clocks (checked). Checked: act2 and eyes2 (b32, build 86): each `a blackout kept in the eyes` 0.0 to
   1.0 s (0.1.5: 4.9 s at every scene).
9. [x] **Jak II's pause behind the player** (SteamVR). The pause menu is on the HUD, which follows the head (a layer in
   the head's own space); in a menu the pointer's beam and dot come with it, layers in the room's space. On a PC each
   frame's layers now share the room's space: the HUD put where the head is for the frame. Checked: pturn2 and pstick2
   (b28k, and again in b32 on build 86): paused after the head's and the stick's turn, the menu ahead.
10. [x] **Jak 1: Jak breathes hard while you crouch** (Physical crouch, on to start, holds the game's crouch): find the
   sound. The owner then: "if the crouch breathing is a normal game function, then so be it, but yea just be thorough
   and check". It is: Jak's crouch animation (target-duck-stance) plays the art's own breath-in and breath-out
   effects, about one a second, and his standing stance breath-in-loud and breath-out-loud. Nothing of ours plays
   them (effect-control.gc: our only changes there are Extra vibration's hook and the party's sparks). Checked: crouch1
   (b28k), crouchb and crouchp (b29i): the same breath effects about once a second by the body's crouch and by the
   button, from the game's own crouch animation; no change.
11. [x] **Jak 1 closed on the owner's Quest 3 at Geyser Rock's scarecrows** (their zip: crash.txt SIGSEGV, the game's
   own code at the scarecrow's break). Matched against Jak 1's code compiled for ARM64 (goalc --arch arm64,
   C:\JakQuest\sim\arm64proj): joint-exploder's method 21, which calls bounding-box's add-point! through the
   bounding-box type's symbol, and that symbol's value was 0: something had written over the symbol table. Not the
   graphics (their Max graphics draw on the display thread; the symbol table is the game's). The crash handler then
   faulted a second time naming the function (a half-made type's symbol), so the report stopped short. Now: the kernel
   checks the symbol table once a frame (kscheme.cpp symbol_guard, after the dispatch on the game's thread): a type's
   symbol found 0 gets its type back and the log says which (`[vr-guard] ...`), a function's is logged; and the crash
   handler skips what is not a symbol or a string, so the next report names the function. Checked: scare1 (b28k): Geyser
   Rock's scarecrows broken, no `[vr-guard]` line, no crash; on a Quest: HEADSET_CHECKLIST.
12. [x] **Jak 3: Light Jak by the left grip and Y** ("dark jak works in 3 but light jak not work"; a gamepad to get
   past such parts). The game's lesson: "Hold L2 then Press Triangle". The left grip, which players take for L2 (its
   quick squeeze is Dark Jak), held is the left fist, so holding it and pressing Y did nothing; only 0.1.5's second
   squeeze held, or the left stick's click held, was L2 held, which nothing showed. Now, once the game has given Jak
   Light Jak (pc-vr.gc: the game's feature and setting, and not as Dark Jak), the left grip held and Y pressed is L2
   held (no fist, a light buzz); Y let go: Triangle (heals); the thumb slid to X: Square (time slows); A: Cross (the
   wings); B: Circle (the shield); or the right stick. The grip let go before a choice: L2 alone, Dark Jak, as the
   game's own pad. Before Light Jak, and as Dark Jak (Y his invisibility), Y with the grip held is Triangle as ever.
   Checked: light3 (b28k, and b31 on build 85) and light3b (b28l): each power, the wings, Dark Jak by the grip let go
   first.
13. [x] **The Quest 3 and 3S start with the owner's Graphics** (their vr-options.txt from the zip): Strong sharpening
   (their 4x anti-aliasing and 16x anisotropic filtering not: the owner then, "anti aliasing/anisotropic filtering
   should be off by default", on every headset and the PC), with what was already the start (120 Hz, Smoother frames, High
   foveation, Max draw distance, Automatic game rate, Dynamic resolution, boost now on); and 150% eyes (the owner then:
   "default quest 3 res is now 150% right?", "so yea, for quest 3/3s, res in launcher should be 150%"; their logs had
   `resolution 100`, the eyes 1680 x 1760, the Quest 3's recommended size; 150% is 2520 x 2640, within the headset's
   largest, and Dynamic resolution draws smaller while it does not keep up). Told apart by Android's device name
   (eureka, panther) in both the launcher and the game. The Quest 2 and the others keep 100% eyes and Normal
   sharpening; the PC 100% and no sharpening. New keys ("anti-aliasing",
   "anisotropic-filtering", "sharpening-strength", "eye-resolution"), so a Quest 3's old start is left behind once
   and any other choice is kept; a PC keeps its own. Checked: opts16 and opts16b (b28k: old starts left behind once,
   other choices kept) and fresh1 (b33: a PC's 100% eyes, no sharpening, anti-aliasing and anisotropic filtering Off);
   the Quest 3's own start (Android's device name): read in the code and compiled for the Quest, HEADSET_CHECKLIST.
14. [x] **Every game's prompts against the controls** ("make sure we arent missing anything similar"): each "Hold X
   then press Y" and two-button prompt in the three games' texts read against fill_pad. Jak 3: Light Jak's (item 12),
   Dark Jak's (the left grip's quick squeeze), Dark Jak's invisibility (Y as Dark Jak, kept out of item 12), the
   JET-Board's and the gun's. Jak II: Dark Jak, the Dark Giant (L2 as Dark Jak), Dark Bomb and Blast, the board's R2.
   Jak 1: few (Circle to talk, the d-pad): reachable as they were. No button does two things at once. Checked: each
   prompt read against fill_pad; every button pressed in act1 (b30d), act2 (b32) and buttons3 (b31).
15. [x] **Jak 3's "Exceeded max number of collide-cache prims"** (the tester's log, 10 lines): at the temple's load,
   in its blackout, before play; the hands' probes only run in first person play (hand_probes). The game's own. Checked:
   the tester's log read (the game's own message in the temple's load, before play); no change.

16. [x] **The Dot crosshair on to start again** (the owner, 2026-11-17: "lets turn the dot crosshair on by default
   again for this release"; 0.1.5 started Off). Under a new key, "crosshair-shape", in the game (vr_options) and both
   launchers (VrOptions): 0.1.5's "crosshair-kind" 0 (its Off start, written into every file by its launchers) is the
   Dot; any other kind a player chose in 0.1.5 is kept, and 0.1.3's and 0.1.4's "crosshair" as the player had it (their
   start was the Dot, so an Off there was the player's own). Checked: opts16x (0.1.5's kind 0 read as the Dot) and
   opts13 (0.1.3's own Off kept) (b28l), and fresh1 (b33: `crosshair-shape 1`).
17. [x] **The launchers' Controls page** (the owner: "make sure to update the controls section of the launcher
   accordingly"): both launchers show docs/CONTROLS.md (the APK bundles it as controls.md, the PC release copies it),
   which has item 12's Light Jak and item 16's crosshair. The option help that named the grips' old buttons (the
   launcher's "Fists on the right grip", the in-game VR menu's same row, two lines at most) says the quick squeezes
   and Light Jak by the left grip and Y. Checked: CONTROLS.md read against jak_vr.cpp's buttons (items 12, 16, 23); the
   APK bundles it and the PC release copies it (0.1.6's builds).

## Added (the owner, 2026-11-17)

20. [x] **No conflicts with multiplayer: one player's game is the original** (the owner: "i thought i already said to be
   sure theres no conflicts, with multi, etc."). The multiplayer mods run their game code with nobody hosted or joined
   (Jak II's mod is compiled into the game and runs; its launcher switch only stops its network side). Every change
   they make to the game's own code, read against the original one by one (C:\JakQuest\sim\audit-0.1.6.txt: the
   method, the helpers' one-player answers, each finding), and each change found in one player's game put back to the
   original's alone (`mp-session-solo?`), the mod's kept for a session. Among them: Jak II's pause (item 9's cause: the
   mod held nothing in the pause), the task graph (Daxter's sidekick steps, race continues, the tomb boulder, the
   mountain and krew collections, Keira's delivery), enemies' hit reactions and knock-backs, the widow's random seed,
   gems and orbs, crates, elevators, swing poles, the sewer escort's and Sig's Atoll "too far" checks, the mod's added
   actors and trigger zones, the city's traffic levels and Erol's race sounds, the first auto-save's notice, the city
   mirror, the player-data file failing a save. Then Jak 1's Teamruns changes the same way. Checked:
   C:\JakQuest\sim\audit-0.1.6.txt (each change, its one-player answer, each fix). Jak II alone on build 86 (b32): act2,
   pturn2, pstick2, eyes2, boardon, dark2, turret2 and combo2, no stack, error, stall or crash. Jak 1 alone: act1,
   save1, load1 and pauseb1 (b30d), the tether rock (b32 and b33) and fresh1 (b33). The sessions on build 86 (b33): Jak
   1's mp1 (the guest joined and played the host's progress) and Jak II's mphost and mpguest (connected, UDP both ways,
   no errors).
21. [x] **Jak 1's first scene: a stutter on the boat** (the owner, Quest 3: "only seems to happen for that short shot of
   him laying back on the boat"): the intro's shots timed in a sim, the view switches and loads at that cut checked.
   That cut is the intro's first look at Misty Island: `Displaying level misty`, `Setting force-all-visible?[misty] to
   #t` (the game draws the island whole for the shot). The renderer had Misty's data from about frame 3000, long
   before; no game frame over 8 ms there (JAKVR_FRAME_TRACE=8); no actor born there (the vis-actors branch skips a
   level set all visible: a first guess, far births a few a frame in a scene, was tried, never acted in this scene, and
   was taken out again: entity.gc is 0.1.5's). JAKVR_BUCKET_LOG: the cut adds Misty's l0-shrub (about 91,000
   triangles, every plant with Max draw distance), its tfrag, tie and merc, on top of Sandover's ~240,000. The owner's
   Quest 3 log: the intro at 13 to 17 ms of display work a frame against 8.3 at 120 Hz, and Dynamic resolution one
   10% step a second, so a sudden heavier shot of a few seconds stuttered through. Fix: dynamic resolution's quarter
   second (jak_vr.cpp kScaleQuick): most of its frames late steps down at once (sim dynq, JAKVR_TEST_LATE=3700-4700 at
   120 Hz: 100% to 60% within about a second, 0.1.5/build 64's auto120 took over three; step-ups unchanged). With the
   defaults' AA and anisotropic filtering off. Checked: boatb and dynq (b30).
22. [x] **Multiplayer in each game's main menu** (the owner: "im not seeing a section in the in game menu for
   multiplayer. it should be simlar to the openrac multiplayer mod for both games", "it should show in the main menu of
   the games of course"): a Multiplayer section with hosting, joining and the games to join, as Ratchet's. Jak II's
   title had it (Host Game, Join Game: its lobby browser and direct connect); Jak 1's hosting and joining were only in
   the VR menu. Single player stays apart: nothing of it runs until the player hosts or joins. Checked: titlemp and
   pausemp (b30), then titlemp with save1, load1, pauseb1 and act1 (b30d); a Jak 1 session hosted and joined (mp1, b33).
   Jak II's title Multiplayer unchanged.
23. [x] **Jak 3's JET-Board: X and B swapped** (a tester: "jak3 jet board grind control inconvenient for vr i recommend
   specifically on jack 3's jet board x and b's button actions switch"): Jak 3's board grinds with Square held
   (target-board.gc: the rail check while it is held) and zaps with Circle. X, Square, is the left thumb, the one on the
   stick steering. On Jak 3's board only (play, kFlagBoard), B is Square and X Circle (jak_vr.cpp board_swap); Jak II's
   board and Jak 3 off the board unchanged. A, the jump, and B, the grind, are side by side on the right thumb, as the
   pad's Cross and Square. Checked: board3s (b31).
24. [x] **Jak 3: a scene that did not play** (a tester: "jak 3 a cutscene didn't play ... another jak 3 cutscene not
   play"; their 0.1.5 log, PC with SteamVR): Torn's briefing in the Hip Hog (city-sniper-fight-intro), twice. Talked to
   with Triangle (Y), Jak held 15 s while the scene loaded, then it ended in its first frames, black, and the task went
   on to its resolution; talked to again to see it, it ended again in about 3 s. The first end came on the frame SteamVR
   gave the controllers back to the game (the log's controller lines, focus back), and Triangle, the controllers' Y, is
   Jak 3's scene skip. The mine boss's end and the sewer scene played (the "could not find a master slot" errors are in
   every scene, played or not: harmless). Fixes: a button held as focus comes back counts once it is let go
   (xr_openxr.cpp, every game, PC and Quest); Jak 3's Triangle skips a scene once it has run a second (scene.gc); the
   log says "[openxr] focus lost", "[openxr] focus back" and "[vr] the scene ... skipped: Triangle", so a report
   like it says which. Checked: skip3, buttons3 and light3 (b31): no skip before a second; a button held as focus comes
   back read in the code (HEADSET_CHECKLIST).
25. [x] **Jak II: the dark lingers after a mission's load** (a tester: "mission seems to be have screen dark even after
   gameplay has loaded, evenetually i can see but seems to linger longer than it needs to"; their 0.1.5 log: blackouts
   kept 21.8 s, 56 s and 77 s after loads, the strip mine's "coming out of blackout" with the eyes black 23 s more):
   item 8's cause, the mod's blackout on the presentation clock read on the base clock (fixed in pc-vr.gc), and one more
   such read found in main.gc's vr-solo-toggle-pause (item 20's put-back pause), now on the presentation clock too.
   Checked: the eight Jak II sims on build 86 (b32): every blackout kept 1.0 s or less.
26. [x] **Reports with no change** (the same tester): Jak 1's big button challenge works ("i got all the loot"). "Holding
   gun in hand while driving is currently just jak 2 thing": Jak II's cars leave the game's gun out (the holsters count
   there), Jak 3's own vehicle code puts it away as he gets in (target-pilot.gc: its gun only for the vehicles that
   have one); each game kept as its own. No change.
27. [x] **Jak 1: held in place after a Boggy Swamp tether rock's scene** (a tester: "cutscene after blasting rock in boggy
   swamp glues your feet to ground", then "i tried a different rock that does the same thing"; their two 0.1.5 logs, PC
   with SteamVR: the rock's scene, then Jak held, target-grab, where he stood until they quit, 21 and 41 s, a pause
   between no help). Cause: our 0.1.5 merge of Teamruns' swamp-blimp.gc put the rock's `(process-release? *target*)`
   inside `(when (zero? s5-2) ...)`, the part only the last rock runs (its look at the precursor arm), so the first three
   rocks never let him go; the merge also ran the break twice. Item 20 put the original back alone: 0.1.6's alone branch
   of swamp-tetherrock-break is token for token the original game's (comments and spacing aside), the release where the
   original has it. Checked in play: tests/jak1_tether_rock.sim (Jak put on a rock, JAKVR_TEST_TETHER=1 breaks it as his
   yellow eco would, kmachine.cpp bit 16384 and pc-vr.gc vr-test-tether!, PC only), Jak let go after its scene. Checked
   again on build 86 (b32, b33): `target-grab` in the scene, then `target-stance`, walking and jumping.
28. [x] **Jak II stopped at the prison's warp gate in every Jak II sim** (found by the b31 sims, never released: item 20's
   Jak II put-backs): "suspend called without enough stack", warp-gate-b's idle at 640 of its 512 bytes, then the game
   stopped. Item 20 ran the original's :code as a function (vr-orig-...) called from a :code that also held the mod's
   code inline, so a frame's wait kept both frames, past the stacks the game cuts small. Every such wrapper whose
   original suspends was read for its process's stack: the warp gate (512 bytes: idle, use), the airlocks (1024: close,
   open), the race manager (1024: idle, win, lose), the board training (1024: ten states), the task manager's hooks
   (3072: Krew's collection and delivery, the lurkers' rescue); the others have the full 16 KB. In each, the mod's code
   is now a lambda called on its own (this compiler makes a lambda in a call's head a function of its own, only
   :immediate ones are inlined), so a wait holds the small dispatch and one of the two, as the original held its own.
   Jak 1's put-backs are tests of a symbol in the same function, no new stack objects; Jak 3 has none. Checked: the
   eight Jak II sims on build 86 (b32): no stack line, no error, stall or crash.

## Checks

18. [x] Builds (gk, Jak 1, II and 3 GOAL), the Quest's ARM64 check, targeted sims: Jak II's scenes from the start
    (tests/jak2_pause_turned.sim, the opening and its cuts), a pause with the stick turned (tests/jak2_pause_stickturn.sim),
    Jak 1's crouch, Light Jak by the left grip and Y and Dark Jak's quick squeeze, Jak 1's scarecrows broken, the
    options read (old keys left behind). Done: builds 80 to 86 (gk, Jak 1, II and 3 GOAL), each clean (Jak II 16
    warnings as before); the Quest's ARM64 runtime compiled after build 86 (android_native_check.sh, exit 0); the sims
    above (b28k to b33).
19. [x] Texts: whats_new.txt, release-notes.md, CONTROLS.md, HEADSET_CHECKLIST.md, README; then publish 0.1.6. Done:
    whats_new.txt, release-notes.md, CONTROLS.md, HEADSET_CHECKLIST.md, MULTIPLAYER.md, README and tests/README.md,
    scanned (no user name, no email but the upstream libraries' copyright lines, as 0.1.5); the APK (versionCode 10699,
    0.1.0's key) and the Windows zip (self-test OK) in out/release-0.1.6 with SHA256SUMS.txt; published as v0.1.6.
