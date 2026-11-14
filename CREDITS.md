# Credits and licences

This port stands entirely on other people's open-source work. Credits name code foundations, design references and
related projects; they do not imply endorsement.

**The main base is [OpenGOAL](https://github.com/open-goal/jak-project)** ([opengoal.dev](https://opengoal.dev)): the
decompilation of the trilogy, the GOAL compiler and the native runtime the games run on.

| Project / people | Contribution or relationship |
| --- | --- |
| Naughty Dog; Sony Interactive Entertainment | The original Jak and Daxter: The Precursor Legacy (2001), Jak II (2003) and Jak 3 (2004): their games, art, audio and story. Jak and Daxter is a trademark of Sony Interactive Entertainment. The discs are supplied by the player; nothing of them is in this repository or its releases. |
| [OpenGOAL](https://github.com/open-goal/jak-project) and [its contributors](https://github.com/open-goal/jak-project/graphs/contributors) | The decompilation, the GOAL compiler (goalc), the decompiler and extractor, the native runtime (gk) and its renderer, the PC port's options, mod support and tools. ISC License, Copyright (c) 2020-2026 OpenGOAL Team. |
| [thebardockgames / Precursor](https://github.com/thebardockgames/Precursor) | OpenGOAL on Android: the ARM64 GOAL backend, GLES 3.2, the Android app and the launcher that installs the games from the player's discs. Used at a pinned commit with the changes in `patches/`. ISC License. |
| [moistman42069 / Ratchet & Clank Quest VR](https://github.com/moistman42069/RatchetAndClank-Quest-VR) | The OpenXR session for Quest and PC (`xr.h`, `xr_openxr.cpp`), the simulated headset (`xr_sim.cpp`), the controllers' pad layer, the rig, the HUD panel and pointer, the launcher's options and the release patterns, shared with this port. GPL-3.0-or-later. |
| [npiriou / Rerac-vr](https://github.com/npiriou/Rerac-vr) | The VR design Ratchet & Clank Quest VR carries and this port follows in places: first person through the hero's eyes, aiming with the hand, comfort choices. ISC License. |
| [moistman42069 / Halo CE Decomp Remix VR](https://github.com/moistman42069/HaloCE-DecompRemix-VR-PC-Android), and [astromaddie's HaloCE-VR](https://github.com/astromaddie/HaloCE-VR) before it | Quest know-how proven on the Quest 3: the OpenXR session's sequence, performance levels and refresh fallback, the launcher's patterns, its logs and its updater. |
| [JoKronk / Teamruns](https://github.com/JoKronk/teamruns-jak-project) and its contributors | Jak 1's multiplayer, built in (on to start, and it changes nothing until a player hosts or joins): the other players' Jaks, the shared cells, orbs, flies, crates and tasks, the taunts, and gk's websocket for Teamruns' own app. Its game side is merged into Jak 1; the parties (host from a save, the list of games on a network or through a party server, the host's progress for the others) are this port's own. ISC License, as OpenGOAL's. |
| Christopher M. Kohlhoff / [asio](https://think-async.com/Asio/) | Networking for the multiplayer (Teamruns' websocket, the parties). Boost Software License 1.0. |
| Peter Thorson / [WebSocket++](https://github.com/zaphoyd/websocketpp) | The websocket server Teamruns' app talks to. BSD-3-Clause. |
| David Reid / [miniaudio](https://miniaud.io) | The taunts' sounds (Teamruns). Public domain or MIT No Attribution. |
| Khronos Group | The OpenXR headers and loader. Apache-2.0 OR MIT. |
| [moistman42069](https://github.com/moistman42069) and this project's development and testing | The VR port: the VR layer, its hooks into OpenGOAL, each game's GOAL side, the launcher, packaging and device testing. |
| Players and bug reporters | Logs and reports from headsets. Private logs and identifying details are not published. |

This project is under the GNU General Public License 3.0 or later ([LICENSE](LICENSE)). It grants no rights in the
games.
