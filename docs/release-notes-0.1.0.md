> ⚠️ **First test release. It may not work at all, and it will be broken in some areas.** Nothing in it has run on a real headset yet: every feature below was built and tested on a PC with a simulated headset. **Its main purpose is to collect logs:** whatever happens, even if it does not start, even if everything works, please send yours (**Send your log** below says how). Each log tells us what to fix for the next one.

**The Jak and Daxter trilogy in VR: Jak and Daxter: The Precursor Legacy, Jak II and Jak 3, standalone on Meta Quest and on PC VR.** The games run natively through [OpenGOAL](https://opengoal.dev)'s decompiled code, by way of [Precursor](https://github.com/thebardockgames/Precursor), OpenGOAL's Android port. You need your own disc images. **No game files are included.**

## Downloads

| File | For |
| --- | --- |
| `JakQuestVR-0.1.0.apk` | Meta Quest 2, 3, 3S and Pro (standalone) |
| `JakQuestVR-0.1.0-windows.zip` | PC VR on Windows 10 or 11 (SteamVR, Meta Horizon Link, Air Link, Virtual Desktop, any OpenXR runtime) |
| `SHA256SUMS.txt` | The files' SHA-256 |

## Send your log

This is the most useful thing you can do with this release.

- **Quest:** in the launcher, **Report a problem** makes one zip of the logs (the launcher's, the install's, each game's, the VR options) in **Download/JakQuestVR** on the headset. Copy it from a PC (headset plugged in by cable) or the Files app.
- **PC:** the launcher's **Logs** tab, **Gather the logs**, makes one zip in **Downloads\JakQuestVR** and opens the folder.

Post it in a [**headset report**](https://github.com/moistman42069/JakAndDaxter-Quest-VR/issues/new?template=headset-report.yml) with your headset, the game, your disc's version, where you were and what happened (or that it worked). A video helps. **Most wanted:**

- Whether each game **installs and starts** on the Quest, and how long the install took.
- **How smooth it is:** the logs' `[vr-perf]` lines (every 10 seconds) carry the frame rate.
- **Jak 1 on the Quest:** whether pickups work (the power cell on Sentinel Beach, green eco, orbs). Precursor's phone players found some did nothing.
- **Jak II played into Haven City and on**, and **Jak 3 past its opening scene** on the Quest. On phones, Jak II crashed during play and Jak 3 went white after its first scene.
- **Two players in one game** (Jak 1), on the Quest and on PC.
- Anything that looks or feels wrong: the view, the hands and body, the guns, the melee, the menus.

[HEADSET_CHECKLIST.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/HEADSET_CHECKLIST.md) lists everything worth trying, with the log line that shows each worked.

## Everything in 0.1.0

The same VR layer runs all three games, on the Quest and on PC VR. **Every VR feature is on to start, and every one has its own switch** in the in-game VR menu and the launcher.

### Seeing the games in VR

- **Both eyes drawn by the game's own renderer**, each from its own place, at the headset's refresh rate (the game runs at 60 and the headset's frames are paced around it).
- **First person through Jak's eyes** (**View**): his head and Daxter kept out of your view, your eye height taken from his neck, his running lean and his jumps followed and eased, so the view never jolts.
- **Third person in VR around you** where first person does not fit: the Zoomer, the JET-Board, Jak II's cars, Jak 3's buggies, turrets, rides and scripted moves switch to it **by themselves** and back when you leave them. **Hold the left menu button one second** to switch yourself at any time.
- **Cutscenes** (**Cutscenes**): through Jak's eyes (in Jak II and 3 through the scene's own high-detail Jak), in VR around you, or on a big screen. **Talks in first person** keeps the game's conversations in your view or not.
- **Turning** (**Turning**, **Snap turn angle**, **Smooth turn speed**): smooth to start (120 degrees a second), or snap (45 degrees).
- **Seated play**, **View height**, **World scale**, **Third person world scale**.
- **Recentre** with both sticks clicked together.
- **Every scene the game draws is seen in VR**: the title, the pause menu over play, cutscenes. The flat game screen is never left in front of you.
- **The HUD and menus on a panel** in front of you (**HUD size**, **HUD distance**, **HUD height**, **HUD follows your head**).
- **Max draw distance:** whole levels drawn at full detail as far as you can see, so nothing pops in near you (checked in Geyser Rock, Sandover, Haven City and Jak 3's desert).
- Glows and light beams fade as they come close to your eyes instead of filling the view.

### Controls

- **Every button of the game's pad is on the Touch controllers**: left stick move, right stick turn (or the camera in third person), A jump, B spin kick, X punch, Y Triangle, left trigger crouch and roll, right trigger the gun, left grip Dark (and Light) Jak, menu button Start. The full table for every controller: [CONTROLS.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/CONTROLS.md), and the launchers' **Controls** tab.
- **Move direction:** where you look (to start) or where your left hand points.
- **The d-pad** (Jak 1's warp gates and totals, Jak II and 3's gun choice): rest your left thumb on its thumb rest and the right stick is the d-pad. Or (**D-pad**) hold your left hand by your head and the left stick is the d-pad (**D-pad reach**). Controllers without a thumb rest sensor use the head one by themselves.
- **Pointing at menus** with either hand: the dot shows where you point and the trigger picks (**Menu pointer hand**). The sticks, A and Y work as on a pad too.
- **PC VR controllers:** Quest Touch (all models), Valve Index, Vive wands, Vive Cosmos and Focus 3, Windows Mixed Reality and HP Reverb G2, Pico 4 and Neo 3, each laid out with the nearest buttons it has.

### Your body is Jak's

- **Full-body IK:** Jak's arms reach your controllers, his body stands under your head, his legs stay with the game's animation (**Arm length**, **Body height**, **Body forward**).
- **Hands without lag:** the hands drawn where your controllers will be when the frame shows.
- **Finger tracking:** the controllers' touch sensors curl Jak's fingers: fist, point, thumbs up, open hand.
- **Hands stop at walls and floors**, with a buzz (**Vibration on walls and floors**); his fingers rest on what the hand touches.
- **Body shown:** see his whole body, body and arms, body and legs, arms and legs, arms only, legs only, or hands only.
- **Physical crouch:** crouch for real and Jak crouches (**Crouch depth**), on the ground only.
- His shadow is your VR body's.

### Combat

- **Physical melee:** throw a fist and Jak punches, sweep it across and he spin kicks, crouch and punch up for the uppercut. On foot (vehicles keep their buttons). **Fists on the right grip** (on to start): only fists made with a grip hit, so waving your hands does nothing. **Melee speed** sets how fast a hit must be.
- **Jak 1's yellow eco** shoots from the punching hand, where it points.
- **Jak II and 3's guns:**
  - **Draw from hip or shoulder:** squeeze a grip at your hip or over your shoulder to draw the gun; bring it back there and let go to put it away.
  - **Aim with your hand:** the gun is in Jak's hand and shoots where it points; the laser sight follows (**Gun angle**).
  - **Two-hand grip:** the left hand on the gun's front steadies and aims it.
  - Each shot kicks your hand with its own vibration.

### Haptics

- The game's own rumble on the controllers (**Vibration**, **Vibration strength**), and a buzz for each VR action: a fist closing, a hit, a hand meeting a wall, drawing and holstering, every shot.

### Graphics and performance

As Ratchet & Clank Quest VR's, in the launcher's **Graphics** tab and the VR menu: **Eye resolution**, **Refresh rate**, **Smoother frames** (AppSW, Quest), **Foveated rendering** (Quest), **CPU and GPU boost** (Quest), **Max draw distance**, **Anti-aliasing**, **Anisotropic filtering**, with **Performance**, **Balanced** and **Quality** presets. Our picks:

| Headset | Preset |
| --- | --- |
| Quest 2 | **Performance**: 72 or 80 Hz, 80% eye resolution, high foveation, smoother frames and boost on, Max draw distance off (turn it on if the game stays smooth where you play) |
| Quest 3 and 3S | **Balanced** (to start): the headset's own rate, 100% eye resolution, medium foveation, smoother frames and boost on, Max draw distance on, 2× anti-aliasing |
| PC VR | **Balanced**, or **Quality** on a strong graphics card (120% eyes, 4× anti-aliasing, 16× anisotropic filtering). Smoother frames, foveation and boost are the Quest's; on PC your headset's software has its own |

Stutters? Lower **Eye resolution** first, then turn **Max draw distance** off.

### Multiplayer (Jak 1)

Built in, from [Teamruns](https://github.com/JoKronk/teamruns-jak-project)'s multiplayer: **on to start, and it changes nothing in the game until you host or join** (turn it off altogether in the launcher's **Mods**, or on the Quest Jak 1's options page).

- From the in-game VR menu's **Multiplayer** rows:
  - **Host a game:** from a new game or one of your three saves (each shown with its power cells, orbs, scout flies and percent).
  - **Join a game:** a list of the games on your network and on the party server, each with its host, level, power cells, players and whether it is on your release.
  - **Play which save?** once in a game: the **host's progress** (sent to you and loaded in place), one of your saves, or a new game.
  - **Players:** who is in the game; the host can **remove** a player.
  - **Leave the game.**
- The others' Jaks run in your world with their names over them, in their own colours, and what one player collects, everyone has.
- **Every VR feature stays on for everyone**, whoever hosts, VR or flat: melee, hand aim, yellow eco from the hand and the rest work in each player's own game. Each VR player's head, hands and fingers show on their Jak in everyone's game.
- **On your network** the games find each other by themselves. **Over the internet**, through a **party server**: anyone can run `party_server.py` (in the PC zip's `party-server` folder, with MULTIPLAYER.md; Python 3 only, nobody else has to open ports), and players put its address in the launcher's Mods. **Everyone needs this release** (the list says which games are).

### Mods

The launchers' **Mods** section, as Ratchet & Clank Quest VR's:

- **Multiplayer** (above), with your player name and the party server's address.
- **Texture packs** for all three games, on Quest and PC: OpenGOAL's own texture pack zips. Add one, turn each on or off, put them in order (the first wins where two change the same picture), remove. The game is then built again with them from your disc (PC: one button; Quest: **Prepare again**).
- **On the Quest:** Precursor's mod packages (a zip with a `mod.json`): import, enable, disable.

### The launchers

- **Quest:** the three games side by side, each set up from your own disc image on the headset itself. Each game's page: **Saves**, **Graphics**, **VR Body**, **VR Controls**, **Controls**, **Mods**, **Remove**. Settings: **Language** (English, German, Spanish, French, Italian, Portuguese), **Updates**, **Backups** (your saves copied before each update or change), **Log**, **About**. **What's new**, and **Report a problem**.
- **PC:** **Play** (each game set up from your disc image once, the disc's version shown; PLAY IN VR), **Graphics**, **VR Body**, **VR Controls**, **Controls** (every PC VR controller), **Mods**, **Logs** (Gather the logs), **Credits**.
- **Every disc version works:** US, PAL, Japanese and Korean. The launcher builds each game from source for your disc.
- **Updates itself** from this page: the Quest's launcher installs the new APK (keeping your games and saves, a copy of your saves made first), the PC's downloads the new zip, checks it against this page's SHA-256, and puts it over its folder.
- A link to [Ratchet & Clank Quest VR](https://github.com/moistman42069/RatchetAndClank-Quest-VR/releases/latest) in the corner.

### Under the hood

- **OpenGOAL v0.3.9**, merged into Precursor, with Precursor's ARM64 compiler for the Quest. Everything the OpenGOAL PC port has stays: its settings, speedrun mode, texture packs.
- The games are compiled from source on your device for your disc.

## Install on Quest

1. Put your Quest in **Developer Mode** (in the Meta Horizon app on your phone).
2. Get `JakQuestVR-0.1.0.apk` onto the headset and install it: download it in the Quest's browser (or email it to yourself and download it there) and open it from the **Files** app, or sideload it with SideQuest.
3. Open **Jak and Daxter VR** from your Library's **Unknown Sources**.
4. Copy your disc images (`.iso`, PS2 discs only) to the headset's **Download** folder with a PC, and add each game in the launcher. **The headset builds each game from your disc once: this takes a while and several GB of space (Jak 3 the most). Keep the headset on charge.**
5. Choose a game and play.

**Updating:** install a new APK over this one, or let the launcher update itself. **Do not uninstall or clear the app's data:** your games and saves go with it.

## Install on PC

1. Download `JakQuestVR-0.1.0-windows.zip` and unzip it anywhere you can write (not Program Files). Keep the `JakDaxterVR` folder together. Nothing is installed into Windows.
2. Open `JakDaxterVR.exe`. Windows may warn about an app from the internet it does not know: **More info**, then **Run anyway**.
3. On the **Play** tab, **Set up from your disc image...** under each game, and pick its `.iso`. Each game is made from your disc once; it takes a few minutes.
4. Start your headset's PC VR software (SteamVR, Meta Horizon Link, Virtual Desktop, ...), then press **PLAY IN VR**.

Your set-up, saves and options are kept in `%LOCALAPPDATA%\JakQuestVR`, so updates keep them.

## What we tested

On a Windows PC with a simulated headset and controllers (scripts in `tests/`), with a PAL Jak 1, a US Jak II and a PAL Jak 3, **nothing on a real headset**:

- **Each game from boot:** the title, a new game, the opening scenes through Jak's eyes, then play: Jak 1 on Geyser Rock, Jak II at the fortress, Jak 3 in Spargus. Walking, jumping, punching, first and third person.
- **Melee** in all three (fists, punch, spin kick, uppercut), **the gun** in Jak II and 3 (drawn, aimed by the hand, held two-handed, fired, put away), **Body shown** in every choice, the hands at a wall.
- **Vehicles:** Jak 1's Zoomer, Jak II's JET-Board and Jak 3's buggy go to third person by themselves and back.
- **Pop-in** with Max draw distance on and off, the light beams' glow near the eyes.
- **Multiplayer:** two games on one PC, on the network and over the party server: found in the list, joined, the host's progress sent and played, each player's VR body on the other's Jak, the Players page's Remove. Multiplayer stayed closed in every single-player run.
- **A texture pack** through a full build, and off again.
- The Quest app's runtime compiled for the Quest's chip (ARM64), and this APK built, but not run.

## Known limits

- **Nothing has run on a Quest or a real PC headset yet.** Expect anything from small glitches to the game not starting.
- On phones, Precursor's players saw some Jak 1 pickups do nothing, Jak II crash during play, and Jak 3 go white after its first scene. We have not been able to try these on a Quest.
- Building a game on the headset needs several GB free.
- The Quest launcher's new texts (texture packs, multiplayer) are in English only.
- Multiplayer is Jak 1's only (Teamruns' is).
- On PC, OpenGOAL's code mods (the OpenGOAL mod launcher's) are their own builds of the game, without VR; texture packs are the mods this release takes.

## Thanks

Everything here stands on [OpenGOAL](https://github.com/open-goal/jak-project) (the decompiled games, the native runtime and the GOAL compiler) and [Precursor](https://github.com/thebardockgames/Precursor) (OpenGOAL on Android). Multiplayer is [Teamruns](https://github.com/JoKronk/teamruns-jak-project)'. The VR layer shares its OpenXR code with Ratchet & Clank Quest VR. None of OpenGOAL, Precursor or Teamruns made, endorse or support this project: please report problems here, not to them. Full credits and licences: [CREDITS.md](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/CREDITS.md) and [THIRD_PARTY_NOTICES.txt](https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/THIRD_PARTY_NOTICES.txt). This project is under the GPL 3.0 or later. Jak and Daxter is Naughty Dog's game and a trademark of Sony Interactive Entertainment; this project is not affiliated with either.
