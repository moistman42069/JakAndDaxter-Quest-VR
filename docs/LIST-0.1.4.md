# Jak and Daxter Quest VR 0.1.4: the list

Started 2026-11-16, after 0.1.3 was published. Marks: [ ] to do, [~] in progress, [x] done (how it was checked).

The owner, 2026-11-16: "alright, this release is awesome. regress nothing. this next release should be a quick haptics
update. basically, add options to add extra haptics (in the launcher and in game vr menu) haptics can be either as is,
also given when collecting items (eggs, etc), or for the whole world (when taking steps, any actions really, etc).
proceed with list with this in mind".

**Released as 0.1.4, 2026-11-17** (the owner: "Publish 0.1.4"). Everything here that a PC with the simulated headset
can do and check is done. Left for the headset or a player's log: the Quest's own check of Save log (item 9) and of
the game log without the driver's flood (item 10), and items 6 and 7, carried to the next list and named in the
release notes' Known limits.

## Haptics

1. [x] **Extra vibration**, one option in the in-game VR menu and both launchers (VrOptions), saved as
   "extra-vibration": **Off** (the haptics as in 0.1.3), **Pickups** (also a buzz for each thing collected), **The whole
   world** (pickups, and the world: steps, actions and the like). New and the port's own, so on to start: Pickups
   (The whole world buzzes at every step, so it is the player's to choose). Done: vr_menu.cpp (Controls),
   VrOptions.EXTRA_VIBRATION (VR Controls, both launchers), default 1 (Pickups, the owner's choice). Checked: builds 66
   and 67; the sims on gkcfg (the defaults) say "Extra vibration: Pickups", on gkcfg-feel "The whole world", on
   gkcfg-feeloff nothing new.
2. [x] **Pickups**, all three games: orbs ("eggs"), eco, power cells, scout flies, Precursor orbs and skull gems, ammo,
   health, guns, passes and the like, each as it is taken (the pickup event in Jak's handler,
   target-generic-event-handler 'get-pickup, only when it was taken, not when he is full). Small things a light tick,
   eco a longer one, big things (a power cell, a scout fly, a gun, a crystal) a strong one. Checked: b83 feelorb (Jak
   dropped onto Geyser Rock's first orb, on the defaults): `small pickup (5 0; Extra vibration: Pickups)`, 0.25 for
   0.03 s in each hand; b84 feelflop (the dive breaks an iron crate): its scout fly, `big pickup (8 0; ...)`, 0.85 for
   0.25 s; b82 feel3: Jak 3's big pickup as its opening scene hands Jak an item. The pickup type is the log's first
   number (Jak 1's orb 5, scout fly 8). Not reached in a sim: eco (the same call, its own strength).
3. [x] **The whole world**, all three games: each step (the foot's side in that hand; running harder), each jump,
   landing, roll and slide, each punch, kick, dive and flip from the buttons as from the hands (the game's attack start),
   each hit that lands, and the world's shakes (the game's camera shake: explosions, quakes, bosses), by how big.
   Checked: b82 feel1, feel2, feel3 (each game's jakN_actions on The whole world): steps (each foot, walk and run),
   jumps, landings, rolls and attacks in all three; b83 feel1 the same after build 67; b84 feelflop: 3 jumps, 12
   landings, 3 attacks (the dives and the uppercut) and the dive's `hit (0 0; ...)`, 0.5 for 0.07 s. Two landing buzzes
   where the animation has two land effects (as its sounds): kept. Not reached in a sim: a camera shake (the game's
   *camera-smush-control*, the same call).
4. [x] **Regress nothing:** Off is exactly 0.1.3's haptics; the game's own rumble, the guns, the walls, the d-pad and
   the gun switch are as they were; **Vibration** off stops every buzz and **Vibration strength** scales the new ones
   too; the other players' Jaks (Jak 1's and Jak II's multiplayer) never buzz your hands; no buzz in scenes, menus or
   loads for the world's steps and actions. Checked: Off returns before anything (jak_vr.cpp feel); b82 feeloff: 0
   `[vr-feel]` lines and only the game's own buzzes, the same as 0.1.3's run (b57); b82 and b83 feelpick (jak1_play on
   Pickups): 0 `[vr-feel]` lines, no step or move buzz; every buzz goes through buzz() (Vibration, Vibration strength);
   every hook checks `(= self *target*)` or the effect's process, so the other players' Jaks never buzz; the world's
   steps and actions only in play, not while a scene holds Jak.
5. [x] **Texts:** What's new, the release notes, HEADSET_CHECKLIST (what to try, the log line for each), the VR menu's
   and launchers' help. Done: whats_new.txt's 0.1.4 section (with items 8 to 10), docs/release-notes.md for 0.1.4,
   HEADSET_CHECKLIST's Extra vibration and Save log, tests/README (JAKVR_FEEL_TRACE, the 0.1.4 checks), the help line
   in vr_menu.cpp and VrOptions.

The owner, later the same day: "set the middle haptics option as the default (where you also feel vibrations for
collectibles, eggs, etc) and also make the graphics options i set in the launcher (not sure if you can see those,
disregard if not) the new default too", then "i play on quest... let me send a log". Item 1's default is Pickups (the
middle one). Item 8:

8. [x] **The owner's graphics options as the new defaults** (the Quest launcher's Graphics). Their first file,
   precursor-install.log, is the installer's log only (Jak 1 prepared on their Quest 3 with 0.1.3, versionCode 10399:
   1334 targets built, no error), without the VR options; their second, Report a problem's zip
   (JakQuestVR-logs-20261116-231621.zip), has jak1/vr-options.txt. Its Graphics: Eye resolution 100%, Refresh rate
   **120 Hz**, Smoother frames **on**, Foveated rendering **High**, CPU and GPU boost **off**, Max draw distance on,
   Anti-aliasing **Off**, Anisotropic filtering **Off**, Game frame rate Automatic, Dynamic resolution on, Share work
   between eyes on, Show performance off, PS2 detail off, Sharpening **Normal** (bold: not 0.1.3's start). Done: those
   seven are the Quest's start now, in the game (vr_options.h, `#if defined(__ANDROID__)`) and the launcher
   (VrOptions.QUEST, Android's virtual machine; onQuest), so the VR menu, the launcher, Balanced ("the defaults") and a
   new file agree. The PC keeps 0.1.3's start: anti-aliasing, anisotropic filtering and the refresh rate do work there
   and its chips are not the Quest's (regress nothing); the other four do nothing on a PC. Performance's anisotropic
   filtering is Off on the Quest (it was 4x, more than the new start). A player's own file keeps its values (each
   file is written whole), so this is the start for a new player or a game with no file yet. 120 Hz is safe on any
   Quest: set_refresh takes the most the headset offers up to it. Smoother frames was off to start for the title's
   flicker (0.1.0); from 0.1.1 it is off on the title and its menus.

The owner, then: "i mean, the report a problem thing is nice, but the default logs themselves should also be alot more
detailed and basically provide everything you need as well. so when i go into the logs section and save one, it
shouldnt be fuckin useless lol. proceed". Items 9 and 10 (10 found in their zip's game log):

9. [x] **Detailed logs.** The Quest launcher's Logs (Settings, Log) showed and saved the preparation's log alone
   (precursor-install.log). Its **Save log** writes one text file now, JakQuestVR-log-<date>-<time>.txt
   (ReportBuilder.writeFullLog): the app and the headset (build, device, Android, RAM, storage, memory now), each
   game prepared or not, how the app's last 10 runs ended (Android's record: crash, out of memory and the like), then
   each game's VR options, settings, mods, manifest, last crash, buildinfo, the start (96 KB: the options read, the
   headset, its rates) and the end (2 MB) of its newest run's log and the end (256 KB) of the run before, what was kept
   from an abnormal exit, the preparation's state and log (its start and its last 512 KB), and the app's Android log.
   No saves, disc images or game files. The section shows the same shortened (each log's end, no Android log), with
   a new line saying so in all six languages; the preparation screen's View log keeps showing the preparation's log,
   and its Save log writes the full one too. The device lines (Spanish, from Precursor) are in English now, in the
   preparation's log and Report a problem's summary too. The PC: its Logs button already zipped every game's and set-up's
   log and each game's options and settings; its summary adds the date and the PC's memory. Checked: the APK built
   (apk014, BUILD SUCCESSFUL, no new compiler notes: 0.1.3's build had the same deprecation note), the new text is in
   its classes.dex; the full log reads the same files at the same paths as Report a problem's zip, which gave the
   owner's files; the save runs off the screen's thread and stops with a message if the file cannot be opened. Left:
   the headset's own check, the next Save log on a Quest.
10. [x] **The Quest's driver filled the game's log.** The owner's game log (4 MB kept of a 13-minute run) was 26,000
   lines of the Adreno 740 driver's notes, reported as errors: "unable to generate a triangle primitive because there
   are less than 3 vertices" from the ocean (about 180 a second by the sea) and Jak's shadow, and "Packing allocations
   for resource %p" at each load. Every other line was lost among them, and each was written in play. Fixed both ways:
   the ocean's and the shadow's draws with fewer than 3 indices are not sent (they draw nothing; CommonOceanRenderer.cpp,
   ShadowRenderer.cpp), and the same driver message is written 3 times, then at the 10th, 100th, 1,000th ... with its
   count (OpenGLRenderer.cpp opengl_error_callback, all three games, the PC too). Checked: b83 ocean against b82o
   (before build 67): the sea and his shadow in each eye the same (the pictures differ only where Jak stood and the
   waves moved); the PC driver's repeated warning written at its 1st, 2nd, 3rd (with "from here written at the 10th,
   100th, 1,000th ...") and "(the 10th time)", the other warnings once each; no error. The Quest's own lines: in the
   next game log from a Quest.

## Carried from 0.1.3 (the headset or a player's log)

6. [~] LIST-0.1.3 items 5 to 8: the Quest's own numbers for Show performance, Dynamic resolution, Share work between
   eyes and Max draw distance. The owner's log (Quest 3, 0.1.3, their Graphics above: 120 Hz offered and taken,
   Smoother frames on, Max draw distance on, Share work between eyes on, Automatic): Jak 1 in play at **60 frames a
   second** for 39 samples of 10 s (59.0 the least), the headset at 120 with Smoother frames making every other one
   (on for 96 to 100% of them), each frame's work 9.4 ms on average (4 to 12 ms a sample; most 16 to 23 ms) of the 16.7
   ms the frame has, 0 to 4 displays missed a sample in play (22 at the first, a load). Dynamic resolution worked: down
   to 60 and 70% through a load, back up to 100% in steps while the headset kept up. On the title (no Smoother frames)
   the game drew 119.6 a second, 2.3 ms a frame. Show performance was off; the [vr-perf] lines give its numbers.
   Left: the same from a Quest 2 or 3S, and a run with Smoother frames off.
7. [ ] LIST-0.1.3 item 24: Jak 3's opening upside down (not reproduced; the log says when the view is upside down and
   still).
