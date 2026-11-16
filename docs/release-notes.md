> ⚠️ **Third test release: 0.1.2 makes updating smoother, on top of 0.1.1 (out the same day, its notes below).** 0.1.0's first players sent their logs, videos and reports from SteamVR, Meta Horizon Link and the Quest; 0.1.1 fixes what they found and makes Jak's body feel more like yours, and 0.1.2 lets an update leave your games as they are when their game code has not changed. Everything here was built and tested on a PC with a simulated headset, so some things will still be off. **Please keep sending your logs** (**Send your log** below says how), whether something broke or everything worked.

**The Jak and Daxter trilogy in VR: Jak and Daxter: The Precursor Legacy, Jak II and Jak 3, standalone on Meta Quest and on PC VR.** The games run natively through [OpenGOAL](https://opengoal.dev)'s decompiled code, by way of [Precursor](https://github.com/thebardockgames/Precursor), OpenGOAL's Android port. You need your own disc images. **No game files are included.**

## Downloads

| File | For |
| --- | --- |
| `JakQuestVR-0.1.2.apk` | Meta Quest 2, 3, 3S and Pro (standalone) |
| `JakQuestVR-0.1.2-windows.zip` | PC VR on Windows 10 or 11 (SteamVR, Meta Horizon Link, Air Link, Virtual Desktop, any OpenXR runtime) |
| `SHA256SUMS.txt` | The files' SHA-256 |

## Updating

Let the launcher update itself (it offers 0.1.2 on its own), or install over your version: on the Quest, install `JakQuestVR-0.1.2.apk` over it; on PC, unzip 0.1.2 over your folder. On the Quest, do not uninstall first: your games and saves go with the app. **Your saves and options are kept** either way.

- **From 0.1.1: nothing to prepare again.** 0.1.2 does not change the game code, so your games play as they are, on the Quest and on PC.
- **From 0.1.0: each game has to be prepared again once** (0.1.1 changed the VR layer's code inside the games). **Quest:** **Prepare again** on each game's page. **PC:** press **Build it again** on each game (it shows in place of **PLAY IN VR** until then); it builds from the disc you set the game up with, so no disc image is needed, and takes a few minutes.

## Send your log

This is the most useful thing you can do with this release.

- **Quest:** in the launcher, **Report a problem** makes one zip of the logs (the launcher's, the install's, each game's, the VR options) in **Download/JakQuestVR** on the headset. Copy it from a PC (headset plugged in by cable) or the Files app.
- **PC:** the launcher's **Logs** tab, **Gather the logs**, makes one zip in **Downloads\JakQuestVR** and opens the folder.

Post it in a [**headset report**](https://github.com/moistman42069/JakAndDaxter-Quest-VR/issues/new?template=headset-report.yml) with your headset, the game, your disc's version, where you were and what happened (or that it worked). A video helps. [HEADSET_CHECKLIST.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/HEADSET_CHECKLIST.md) lists everything worth trying, with the log line that shows each worked.

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
2. Get `JakQuestVR-0.1.2.apk` onto the headset and install it: download it in the Quest's browser (or email it to yourself and download it there) and open it from the **Files** app, or sideload it with SideQuest.
3. Open **Jak and Daxter VR** from your Library's **Unknown Sources**.
4. Copy your disc images (`.iso`, PS2 discs only) to the headset's **Download** folder with a PC, and add each game in the launcher. **The headset builds each game from your disc once: this takes a while and several GB of space (Jak 3 the most). Keep the headset on charge.**
5. Choose a game and play.

**Updating:** install a new APK over this one, or let the launcher update itself. **Do not uninstall or clear the app's data:** your games and saves go with it.

## Install on PC

1. Download `JakQuestVR-0.1.2-windows.zip` and unzip it anywhere you can write (not Program Files). Keep the `JakDaxterVR` folder together. Nothing is installed into Windows.
2. Open `JakDaxterVR.exe`. Windows may warn about an app from the internet it does not know: **More info**, then **Run anyway**.
3. On the **Play** tab, **Set up from your disc image...** under each game, and pick its `.iso`. Each game is made from your disc once; it takes a few minutes.
4. Start your headset's PC VR software (SteamVR, Meta Horizon Link, Virtual Desktop, ...), then press **PLAY IN VR**.

Your set-up, saves and options are kept in `%LOCALAPPDATA%\JakQuestVR`, so updates keep them.

## What we tested

On a Windows PC with a simulated headset and controllers (scripts in `tests/`), with a PAL Jak 1, a US Jak II and a PAL Jak 3, each fix against the report it answers, then the moves, menus, guns and scenes it could reach in all three games. The Quest app's runtime was compiled for the Quest's chip and this APK built, but not run on a Quest. [LIST-0.1.1.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/LIST-0.1.1.md) says how each item was checked, and [LIST-0.1.2.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/LIST-0.1.2.md) 0.1.2's.

## Known limits

- **Some objects look different in each eye** (doors, Precursor objects, lanterns): not found on the simulated headset yet. A picture or video from your headset, with your graphics settings, would help.
- **Jak II multiplayer** is not in this release: we have not found the Jak II multiplayer mod. If you know where it is, please send a link.
- A scene in Jak 1's Lost Precursor City that "broke the game" (a report without a log): every scene we could reach there played through on the simulated headset. If it happens to you, your log would show which one.
- Big flat-coloured shapes now and then in Haven City (Jak II), and a game audio cut-out on SteamVR, are still to find: logs of sessions where they happen would help.
- Building a game on the headset needs several GB free.
- The Quest launcher's newest texts (texture packs, multiplayer, mods) are in English only.
- Multiplayer is Jak 1's only (Teamruns' is).
- On PC, OpenGOAL's code mods (the OpenGOAL mod launcher's) are their own builds of the game, without VR; texture packs are the mods this release takes.

## Thanks

To everyone who played 0.1.0 and sent their logs, videos and reports.

Everything here stands on [OpenGOAL](https://github.com/open-goal/jak-project) (the decompiled games, the native runtime and the GOAL compiler) and [Precursor](https://github.com/thebardockgames/Precursor) (OpenGOAL on Android). Multiplayer is [Teamruns](https://github.com/JoKronk/teamruns-jak-project)'. The VR layer shares its OpenXR code with Ratchet & Clank Quest VR. None of OpenGOAL, Precursor or Teamruns made, endorse or support this project: please report problems here, not to them. Full credits and licences: [CREDITS.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/CREDITS.md) and [THIRD_PARTY_NOTICES.txt](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/THIRD_PARTY_NOTICES.txt). This project is under the GPL 3.0 or later. Jak and Daxter is Naughty Dog's game and a trademark of Sony Interactive Entertainment; this project is not affiliated with either.
