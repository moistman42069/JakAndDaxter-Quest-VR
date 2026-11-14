<h1 align="center">Jak and Daxter Quest VR</h1>

<p align="center">An unofficial VR port of the Jak and Daxter trilogy (Jak and Daxter: The Precursor Legacy, Jak II and Jak 3,
PlayStation 2): standalone on Meta Quest, and on PC VR headsets with the same launcher and options. It runs the games
natively through <a href="https://opengoal.dev"><b>OpenGOAL</b></a>, by way of <a href="https://github.com/thebardockgames/Precursor"><b>Precursor</b></a>,
OpenGOAL's Android fork. You supply your own discs; no game files are included.</p>

> **First test release: [0.1.0](https://github.com/moistman42069/JakAndDaxter-Quest-VR/releases/latest).** Nothing in
> it has run on a real headset yet: it was built and tested on a PC with a simulated headset, and its main purpose is
> to collect logs. Whatever happens, please send yours: the [release notes](docs/release-notes.md) say how.
> [docs/LIST-0.1.0.md](docs/LIST-0.1.0.md) says what works, what is still open and how each part was checked.

## Built on OpenGOAL

**[OpenGOAL](https://github.com/open-goal/jak-project) decompiled the trilogy and wrote the native runtime and GOAL
compiler that this port runs; everything here stands on that work.** [Precursor](https://github.com/thebardockgames/Precursor)
carried OpenGOAL to Android (an ARM64 GOAL backend, GLES 3.2, an Android launcher that installs the games from your
discs); this port adds the headset on top. Neither the OpenGOAL team nor Precursor's author made, endorses or supports
this project: please report problems here, not to them.

The port keeps everything OpenGOAL has (mods and custom levels, texture packs, the PC settings, speedrun mode) and is
kept current with upstream OpenGOAL's releases.

## What this port adds

One VR layer shared by all three games, so each plays start to finish as it would on a pad:

- **Seeing the games in VR:** both eyes drawn from the game's own renderer, first person through Jak's eyes (his head
  and Daxter hidden, his eye height and running lean followed), a third-person view around you where first person does
  not fit (vehicles, the Zoomer, the JET-Board, turrets), snap or smooth turning, world scale, recentring.
- **Cutscenes** through Jak's eyes, in Jak II and 3 through the scene's own high-res Jak, or in third person around you.
- **Every pad input on the Touch controllers,** with the d-pad on the right stick while the left thumb rests on its
  sensor (or the left hand at the head).
- **Body and hands:** Jak's arms reaching the controllers, his fingers following yours, hands stopping at the world,
  real punches and uppercuts, Jak 1's yellow eco from your hand, Jak II and 3's guns aimed by the hand, drawn from the
  hip or shoulder, steadied with both hands. Body shown picks what of him you see: the whole body, body and arms, body
  and legs, arms and legs, arms only, legs only or hands only.
- **Haptics** for the game's rumble and for every VR action.
- **Performance options** as Ratchet & Clank Quest VR's: Smoother frames (AppSW), foveated rendering, CPU and GPU boost,
  refresh rate, eye resolution, Max draw distance.
- **An in-game VR menu** and a launcher with every option, What's new, and an updater.
- **Mods** in both launchers: Jak 1's multiplayer built in (host from one of your saves, a list of games on your
  network and on a party server, play the host's progress; [docs/MULTIPLAYER.md](docs/MULTIPLAYER.md)), texture packs
  for all three games, and Precursor's mod packages on the Quest ([docs/MODS.md](docs/MODS.md)).

Each VR mechanic is on to start and has its own off switch in the VR menu. Which of these work today is in the list.

## What you need

- Your own discs of the games, as disc images (`.iso`). The launcher reads them and builds each game from source for
  your disc's version (US, PAL, Japanese, Korean).
- A Meta Quest 2, 3 or 3S, or a PC VR headset (OpenXR: SteamVR, Meta Quest Link, Virtual Desktop).

## Building

On Windows, with Git Bash, Visual Studio 2022 Build Tools, LLVM (clang-cl) and NASM:

```bash
git clone --recursive https://github.com/moistman42069/JakAndDaxter-Quest-VR.git
cd JakAndDaxter-Quest-VR
sh tools/setup.sh            # Precursor at its pinned commit, with this port's patches applied
tools/build_pc.bat           # gk, goalc, decompiler, extractor
```

Then extract and compile a game from your disc (the disc's files go under `third_party/Precursor/iso_data/<game>`):

```bash
cd third_party/Precursor
out/build/Release/bin/extractor.exe -g jak1 -e -d -c --proj-path "$(cygpath -w $PWD)" path/to/your/jak1.iso
```

Run it in VR with `JAKVR=1` set (an OpenXR runtime running), or on the simulated headset the tests use:

```bash
sh tools/run_sim.sh jak1 tests/jak1_play.sim out/jak1_play
```

The Quest build (the Android app, from Precursor's) and the PC release: [docs/BUILDING.md](docs/BUILDING.md).

## How it fits together

| Where | What |
| --- | --- |
| `third_party/Precursor` | Precursor, a git submodule at a pinned commit; the port's changes to it are in `patches/`. |
| `patches/0001-jak-quest-vr.patch` | The VR layer (`game/vr/`), its hooks in the renderer and runtime, and each game's GOAL side (`goal_src/jak1|jak2|jak3/pc/pc-vr.gc` and the calls into it). |
| `patches/0002-openxr-headers.patch` | Khronos's OpenXR headers (Apache-2.0). |
| `tools/` | `setup.sh` (submodule and patches), `make_patch.sh` (writes the patches back from the submodule's changes), `build_pc.bat`, `run_sim.sh` (a game on the simulated headset), `png_levels.ps1` (brightness of the test pictures). |
| `tests/` | Scripts for the simulated headset: button presses, head and hand moves, pictures, by game frame. |
| `docs/` | The release list and the session handoffs. |

The VR code is one set of C++ files for all three games (`game/vr/`): the OpenXR session, the eyes and the HUD panel,
the controllers as the game's pad, the head in the game's camera. Each game's GOAL side tells it what Jak is doing (on
foot, riding, in a scene) and hides his head in first person.

## Licence

This project is under the GNU General Public License 3.0 or later ([LICENSE](LICENSE)). OpenGOAL and Precursor are
under the ISC licence; their notices and the others are in [THIRD_PARTY_NOTICES.txt](THIRD_PARTY_NOTICES.txt), and
everyone whose work this builds on is in [CREDITS.md](CREDITS.md).

Jak and Daxter, Jak II and Jak 3 are Naughty Dog's games and trademarks of Sony Interactive Entertainment. This
project is not affiliated with Naughty Dog or Sony, contains none of their game data, and grants no rights in the games.
