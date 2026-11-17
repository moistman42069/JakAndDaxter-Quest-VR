> ⚠️ **Fifth test release: 0.1.4 is a quick vibration update.** **Extra vibration** lets you feel what you collect, or the whole world (each step, jump, landing, punch, hit and quake); the Quest's graphics start as a tested Quest 3 runs them; and the launcher's **Save log** now saves everything a bug report needs. 0.1.3's notes are below. Everything here was built and tested on a PC with a simulated headset, so some things will still be off. **Please keep sending your logs** (**Send your log** below says how), whether something broke or everything worked.

**The Jak and Daxter trilogy in VR: Jak and Daxter: The Precursor Legacy, Jak II and Jak 3, standalone on Meta Quest and on PC VR.** The games run natively through [OpenGOAL](https://opengoal.dev)'s decompiled code, by way of [Precursor](https://github.com/thebardockgames/Precursor), OpenGOAL's Android port. You need your own disc images. **No game files are included.**

## Downloads

| File | For |
| --- | --- |
| `JakQuestVR-0.1.4.apk` | Meta Quest 2, 3, 3S and Pro (standalone) |
| `JakQuestVR-0.1.4-windows.zip` | PC VR on Windows 10 or 11 (SteamVR, Meta Horizon Link, Air Link, Virtual Desktop, any OpenXR runtime) |
| `SHA256SUMS.txt` | The files' SHA-256 |

## Updating

Let the launcher update itself (it offers 0.1.4 on its own), or install over your version: on the Quest, install `JakQuestVR-0.1.4.apk` over it; on PC, unzip 0.1.4 over your folder. On the Quest, do not uninstall first: your games and saves go with the app. **Your saves and options are kept** either way.

- **Each game has to be prepared again once** (0.1.4 changes the game code), whichever version you come from. **Quest:** **Prepare again** on each game's page. **PC:** press **Build it again** on each game (it shows in place of **PLAY IN VR** until then); it builds from the disc you set the game up with, so no disc image is needed. Coming from 0.1.3, the PC builds again only what changed; from an older version, a few minutes.
- **Multiplayer** (Jak 1's and Jak II's) joins only games of the same release: everyone in a game needs 0.1.4.

## Send your log

This is the most useful thing you can do with this release.

- **Quest:** the launcher's **Settings**, **Log**, **Save log** saves one text file with everything a bug report needs (new in 0.1.4: the app and the headset, each game's options, its last crash, the start and the end of its newest log and the run before, the preparation's messages and the app's own log; before, it was the preparation's log alone). Or **Report a problem**: one zip of the same files in **Download/JakQuestVR** on the headset. Copy either from a PC (headset plugged in by cable) or the Files app.
- **PC:** the launcher's **Logs** tab, **Gather the logs**, makes one zip in **Downloads\JakQuestVR** and opens the folder.

Post it in a [**headset report**](https://github.com/moistman42069/JakAndDaxter-Quest-VR/issues/new?template=headset-report.yml) with your headset, the game, your disc's version, where you were and what happened (or that it worked). A video helps. [HEADSET_CHECKLIST.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/HEADSET_CHECKLIST.md) lists everything worth trying, with the log line that shows each worked.

## What's new in 0.1.4

- **Extra vibration**, in the VR menu's **Controls** and the launchers' **VR Controls**, all three games:
  - **Off:** the vibration as in 0.1.3 (the game's own rumble, the guns, walls and floors, the d-pad and the gun switch).
  - **Pickups** (to start): also a buzz for each thing you collect: a light tick for a Precursor orb, an eco pill, ammo or a skull gem, a longer one for eco, a strong one for a power cell, a scout fly, a gun, a pass or a crystal.
  - **The whole world:** pickups, and each step (in the hand of the foot that lands, firmer running and in Jak 3's Titan suit), each jump, landing, roll and slide, each punch, kick, dive and flip (from the buttons or your hands), each hit that lands, and the world's shakes (explosions, quakes, bosses), by how big.

  **Vibration strength** sets how hard these are too, and **Vibration off** turns them off with the rest. Only your own Jak buzzes your hands: the other players' in multiplayer do not, nor Jak's moves in scenes, menus and loads.
- **The Quest's Graphics start as a tested Quest 3 runs them:** **Refresh rate** 120 Hz (or the most your headset has up to that), **Smoother frames** on, **Foveated rendering** High, **CPU and GPU boost** off, **Anti-aliasing** and **Anisotropic filtering** off, **Sharpening** Normal; the rest as before. On that Quest 3, Jak 1 ran at 60 frames a second with the headset at 120 (Smoother frames making every other one), each frame's work about 9 ms of its 16.7. This is the start for a game with no options saved yet: options you set are kept. **Balanced** sets these on the Quest. The PC's start is as before.
- **Save log saves everything** (Quest: **Settings**, **Log**): see **Send your log** above. The **Log** page shows the same, shortened. The device lines in the logs are in English now.
- **Quest: the game's log no longer fills with the graphics chip's notes.** The Quest's driver reported each empty draw of the sea and of Jak's shadow, and its own memory moves at each load, as errors: about 180 lines a second by the sea, so a log kept only its last few minutes and the lines that mattered were lost. The sea and the shadow no longer send their empty draws (nothing is drawn differently), and any message the driver repeats is written three times, then at the 10th, 100th, 1,000th time with its count. Less is written in play too.

## What's new in 0.1.3

- **Jak II multiplayer.** The [Jak and Daxter II Multiplayer](https://github.com/himstan/Jak-And-Daxter-2-Multiplayer) mod by himstan (co-op for up to 8 players, Jak or Daxter, late joining, the city, its traffic and enemies kept the same for everyone, a lobby, skins and name tags) is built into Jak II: the title's **Multiplayer** hosts a game or joins one on your network or by address, with every VR feature for everyone and the others shown with their VR head and hands. Games are between players of this port only (not the mod's own build). Each game's multiplayer can be turned off in the launcher's **Mods**: off, the game is as it was, with nothing of the mod started.
- **Jak 1's Load Game loads your save.** Teamruns' run rules for saves were on all the time and asked to write over the file (and **Yes** started a new game there); they now only apply in a Teamruns run.
- **Menus:** the pointer and the sticks work in every menu of all three games, the trigger clicks with the pointer, and the stick no longer slips past a row; the row the stick chose stays chosen until you point at another. **B** goes back in the games' menus (in Jak 1's it picked the row). Jak II's title: the stick and the pointer no longer land on the hidden Secrets row (it skipped Options, and a click there showed a memory card notice). Each kind of setting takes the pointer: a click on an On / Off, a list or a slider to change it, then a click on its left or right side to step it (hold to keep stepping; Jak 1's sliders follow the pointer while you hold), a click in its middle when done. A long settings page no longer scrolls on by itself when it opens with your hand resting low, and Jak 1's lists (language, frame rate, resolution and the like) now take the pointer while you change them.
- **Both eyes see the same:** objects that looked a little different in each eye (shiny and environment-mapped models, cloth, trails and lasers) are drawn alike in both. Jak 3's clothing no longer sticks to the screen or the HUD. Depth is finer, so posters and signs on walls fight less with them (Jak II's city; its lit signs flicker by design, as in the game).
- **Texture packs have their own tab** in both launchers (**Texture packs**, beside **Mods**: bundled with the mods they were confusing), with **Import** for a pack you downloaded: its .zip (a pack inside a .zip is found too) or the folder you unpacked it to; on PC a .zip or a folder can also be dropped onto the launcher. It goes to the game it is for (its folders or its metadata.json say; else the game whose page imports it, or on PC you are asked), on, with On / Off, Up / Down and Remove there. A pack zipped from inside its texture_replacements is taken too. What cannot work says so plainly: a .7z or .rar (unpack it first), a pack for the PCSX2 emulator (pictures named by number), or pictures in no texture page. On the Quest, a texture pack given to **Import a mod** goes in as a texture pack. **Find texture packs** lists the mod lists' packs; **Browse mods** (Mods) the rest. [MODS.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/MODS.md#texture-packs-all-three-games-pc-and-quest) has the details.
- **A crosshair for the guns** (Jak II and 3; a small dot to start): a mark where the shot would land, on the wall, the ground or the enemy the gun points at, the same size to your eye near or far, red on an enemy the gun can reach. In the VR menu and the launcher: **Crosshair** (Off, Dot, Cross, Cross and dot, Circle, Circle and dot, Plus), **Crosshair size**, **color**, **opacity** and **Crosshair red on enemies**.
- **Guns:** each shot, its flash, its light and the laser start at the muzzle of the gun in your hand. Jak II and 3's punch-then-shoot combo works (the gun course's lesson): the shot goes to what the punch hit. On foot the left fist is only a fist now; Dark Jak (and Jak 3's Light Jak) is the left stick's click there ([CONTROLS.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/CONTROLS.md) has each controller's).
- **The body:** Dark Jak is as solid as Jak (his widened chest no longer squeezes his arms); pieces of gear on his limbs fade with them near your eyes.
- **Riding:** **First person in vehicles** is on to start, in all three games, with **Vehicle view height**, **forward** and **tilt** to put the view where you like it, and **Steady view when riding** (on to start) keeps the view in his seat without each bounce and shake (Jak 3's leaper lizards). Jak 1's Zoomer in first person: the view no longer drops into the Zoomer at each bump. Jak 3's gun turrets (Spargus' wall, a game you must play): you stand at the gun at Jak's own height, looking over its barrels; the view no longer sinks into the turret as you play.
- **The JET-Board:** a quick squeeze of the right grip (no punch) takes it out and puts it away, as well as the right stick's click; a quick click is no longer lost, and the click no longer pauses Jak II and 3 with the left thumb on its rest. CONTROLS.md says how to get back on.
- **Adaptive cutscenes** go back to Jak's eyes when a long scene comes back to him, without going back and forth.
- **Typing with the sticks** in any text field (a player's name, an address): up or down changes the letter, right keeps it, left erases one, A enters it and Y leaves.
- **Performance** (each in both launchers' Graphics): **Game frame rate: Automatic** (to start: the headset's rate while the game keeps up, else 60, trying the headset's again a little later), **Dynamic resolution** on, **Share work between eyes** (on: the second eye skips what is the same for both), **Show performance** (off: a small line low in your view with the frame rate and each frame's work); less log writing in play.
- **PC: the games on another drive.** The Play tab's **Where the games are kept** moves the games' set-up (each game's extracted disc and the game built from it: about 3 GB for Jak 1, up to about 10 GB for Jak II or 3) to another drive or folder: there it is copied, checked, then removed from where it was (C: to start). Saves and options stay in `%LOCALAPPDATA%\JakQuestVR`. The launcher's buttons that cannot be used for the moment are now dimmed.
- If the headset's view is upside down and still for 5 seconds, the log says so (SteamVR without a headset found shows its stand-in's view upside down).

## What's new in 0.1.2

- **Smoother updates on PC.** From now on, the PC launcher asks you to build a game again only when an update changes the game code (or how the games must be built), as the Quest's launcher already does. Before, every new version asked for it, even when nothing in the games had changed. The launcher's log zip says which version built each game.
- **Corrected:** 0.1.1's notes said the PC launcher builds each game again by itself the first time you press **PLAY IN VR**. It does not: after an update that needs it, each game shows **Build it again** in place of **PLAY IN VR** until you press it.

Nothing else changed from 0.1.1: the games, the VR layer and the Quest app are the same as in 0.1.1.

## What's new in 0.1.1

### Fixed (from your reports)

- **Jak 1's menus:** the stick moves through them again, so **Load Game** loads. In 0.1.0 Jak 1's menus took only the d-pad, the highlight stayed on New Game and A started a new game.
- **The menu pointer**, in all three games: a hand held still no longer takes back the row the stick chose. The dot moves the highlight only when your hand really moves to another row; the trigger still picks the row under the dot.
- **Crashes:** Jak II as the sewer's gun turret is destroyed, Jak 3 in the arena (`suspend called without enough stack`) and at the end of its tutorial, every time.
- **A scene that ends early lets Jak go**, in all three games. In 0.1.0, Jak 1's pipe game in Lost Precursor City (the three buttons at the back of the room) could end its scene at once and leave Jak unable to move, with a broken picture after. Now Jak is let go, and the log names the scene (`ended before it let Jak go`) if it happens again.
- **The Quest running out of memory preparing a game** ("Couldn't prepare Jak 3"): each game's preparation needs far less memory (Jak 3's most 5.8 to 4.0 GB), the Quest's installer counts each game's own needs, and after Android closes a preparation for memory the next one goes one level at a time.
- **Start on SteamVR with Touch controllers:** the left stick's click is the menu button there (**Stick click as menu button**, on).
- **Jak 3's title screen** shows its text again, and **its opening** no longer flickers between Jak's eyes and the game's camera.
- **Jak 1's scenes through Jak's eyes** face what happens instead of away from it.
- **The HUD** in Jak 1 and Jak 3 sits right on its panel, and **Jak 3's ammo** is on the gun again.
- **Jak II's car:** its height button works every time (the holsters count only on foot, so a hand resting by your hip in the car no longer reaches for the gun).
- **Walking straight** walks straight (it looked diagonal), **rolling** follows Jak down, and **punching for real in Jak II and 3** answers as readily as in Jak 1.
- **The options set at start reach the headset at once** (some waited for the VR menu to be opened).

### Your body

- **Life size** (World scale 100%, on to start): Jak is as tall as you, and his hands the size of yours.
- **Keep your body upright** (on): his body stands under your head through kicks, flips and landings, instead of tipping into your view.
- **Fade body near your eyes** (on): a part of him that comes too close (a leg in a kick, his body in a roll) fades smoothly instead of filling the view.
- Smoother arms, wrists, knees and fingers; fingers bend against walls and floors; Jak 1's arm wraps follow your hands and the world.

### New options (each in the VR menu and the launcher)

- **Adaptive cutscenes** (on): a scene goes around you by itself while Jak is not properly in it, and back to his eyes when he is.
- **Hold the left menu button one second in a scene** to switch between his eyes and around you.
- **First person in vehicles** (off): the Zoomer, the JET-Board, the cars and the like from Jak's seat.
- **Spin kick by spreading your arms** (off).
- **Buzz on d-pad presses** and **Buzz on gun switch** (on).
- **Holster size** (130% to start: easier to put the gun away).
- **Performance** (all off or as before to start): **Game frame rate** (half rate: the game at 36 a second on a 72 Hz headset), **Dynamic resolution**, **PS2 detail**, **Sharpening** and **Highest** foveation.

### Graphics

- **Smoother frames (AppSW) is off to start:** it made the title screen flicker. It was reworked too (motion only in play, never on the title, menus or loads); turn it on to try it.
- **Max draw distance draws everything, shadows too**, in all three games.

### Mods

- The **Mods** section says plainly what works in VR, with **Browse mods**: install, update, turn off and remove texture packs.

Everything 0.1.0 brought (the VR view, the controls, the body, combat, multiplayer, the launchers) is still here: [0.1.0's notes](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/release-notes-0.1.0.md) describe it all.

## Install on Quest

1. Put your Quest in **Developer Mode** (in the Meta Horizon app on your phone).
2. Get `JakQuestVR-0.1.4.apk` onto the headset and install it: download it in the Quest's browser (or email it to yourself and download it there) and open it from the **Files** app, or sideload it with SideQuest.
3. Open **Jak and Daxter VR** from your Library's **Unknown Sources**.
4. Copy your disc images (`.iso`, PS2 discs only) to the headset's **Download** folder with a PC, and add each game in the launcher. **The headset builds each game from your disc once: this takes a while and several GB of space (Jak 3 the most). Keep the headset on charge.**
5. Choose a game and play.

**Updating:** install a new APK over this one, or let the launcher update itself. **Do not uninstall or clear the app's data:** your games and saves go with it.

## Install on PC

1. Download `JakQuestVR-0.1.4-windows.zip` and unzip it anywhere you can write (not Program Files). Keep the `JakDaxterVR` folder together. Nothing is installed into Windows.
2. Open `JakDaxterVR.exe`. Windows may warn about an app from the internet it does not know: **More info**, then **Run anyway**.
3. On the **Play** tab, **Set up from your disc image...** under each game, and pick its `.iso`. Each game is made from your disc once; it takes a few minutes.
4. Start your headset's PC VR software (SteamVR, Meta Horizon Link, Virtual Desktop, ...), then press **PLAY IN VR**.

Your set-up, saves and options are kept in `%LOCALAPPDATA%\JakQuestVR`, so updates keep them.

## What we tested

On a Windows PC with a simulated headset and controllers (scripts in `tests/`), with a PAL Jak 1, a US Jak II and a PAL Jak 3: for 0.1.4, **The whole world**'s steps, jumps, landings, rolls and attacks in all three games, pickups in Jak 1 (an orb, a scout fly) and Jak 3, and a hit in Jak 1 (a camera shake and eco were not reached: they take the same path), no new buzz with **Off**, pickups only with **Pickups**, and the sea and Jak's shadow drawn as before. The launchers' own tests pass, and the Quest's new start values were checked against the tested Quest 3's file. The Quest app was built, but its new **Save log** not run on a Quest. [LIST-0.1.4.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/LIST-0.1.4.md) says how each item was checked ([LIST-0.1.3.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/LIST-0.1.3.md) and the lists before it for the releases before).

## Known limits

- **The performance options on the Quest:** one Quest 3's log (Jak 1, the Graphics the Quest starts with now) gave 60 frames a second with the headset at 120 and Smoother frames on, about 9 ms of work in each frame's 16.7, and Dynamic resolution stepping down through a load and back up. A log from a Quest 2 or 3S, from Jak II or 3, or with Smoother frames off, would tell us the rest.
- **Jak 3's opening seen upside down** (one player, not every time) was not seen on the simulated headset. If it happens, the log now says so. One cause we know of: SteamVR started without finding the headset shows its stand-in's view upside down; start SteamVR again with the headset connected.
- **Jak II multiplayer** is between players of this port's same release, on your network or by address; it does not join the mod's own (flat screen) build.
- A scene in Jak 1's Lost Precursor City that "broke the game" (a report without a log): every scene we could reach there played through on the simulated headset. If it happens to you, your log would show which one.
- Big flat-coloured shapes now and then in Haven City (Jak II), and a game audio cut-out on SteamVR, are still to find: logs of sessions where they happen would help.
- Building a game on the headset needs several GB free.
- The Quest launcher's newest texts (texture pack import, multiplayer, mods) are in English only.
- On PC, OpenGOAL's code mods (the OpenGOAL mod launcher's) are their own builds of the game, without VR; texture packs are the mods this release takes.

## Thanks

To everyone who played 0.1.0 to 0.1.3 and sent their logs, videos and reports.

Everything here stands on [OpenGOAL](https://github.com/open-goal/jak-project) (the decompiled games, the native runtime and the GOAL compiler) and [Precursor](https://github.com/thebardockgames/Precursor) (OpenGOAL on Android). Jak 1's multiplayer is [Teamruns](https://github.com/JoKronk/teamruns-jak-project)', Jak II's is himstan's [Jak and Daxter II Multiplayer](https://github.com/himstan/Jak-And-Daxter-2-Multiplayer). The VR layer shares its OpenXR code with Ratchet & Clank Quest VR. None of OpenGOAL, Precursor, Teamruns or the Jak II multiplayer mod's authors made, endorse or support this project: please report problems here, not to them. Full credits and licences: [CREDITS.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/CREDITS.md) and [THIRD_PARTY_NOTICES.txt](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/THIRD_PARTY_NOTICES.txt). This project is under the GPL 3.0 or later. Jak and Daxter is Naughty Dog's game and a trademark of Sony Interactive Entertainment; this project is not affiliated with either.
