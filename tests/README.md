# Tests on the simulated headset (PC)

Each `.sim` file here drives one of the games on PC with a simulated headset and controllers (game/vr/xr_sim.cpp):
a script of frame numbers and inputs, pictures of both eyes when asked, and the game's log. Nothing needs a real
headset.

```bash
sh tools/run_sim.sh jak1 tests/jak1_play.sim /c/JakQuest/sim/out
```

`tools/run_sim.sh <jak1|jak2|jak3> <script.sim> <out folder> [NAME=value ...]` runs gk with the script; the pictures
and `run.log` go to the out folder. `SIM_CFG=<folder>` picks the settings folder (default C:\JakQuest\sim\gkcfg; the
VR options are its `OpenGOAL/<game>/settings/vr-options.txt`). Saves are cleared each run unless `KEEP_SAVES=1`.

## Script lines

`<frame> <name> <value>`, `#` for a comment. `shot <name>` saves `<name>-left.png`, `-right.png`, `-panel.png` (the
flat screen) and `-motion.png`; `quit 0` ends the run. Every other name sets an input, held until set again:

- Buttons, 1 or 0: `a b x y menu left_click right_click left_first_touch right_first_touch`; `menu_unbound 1` for a
  runtime that binds no left menu button (SteamVR with Touch: the left stick's click is the menu button then)
- Sticks and triggers: `left_x left_y right_x right_y left_trigger right_trigger left_grip right_grip`
- Head (degrees, metres): `head_yaw head_pitch head_roll head_x head_y head_z` (standing eyes at 1.7 m; `head_roll`: the
  head tilted to a shoulder, the right ear up positive)
- Hands, from where a player holds them (metres): `lh_x lh_y lh_z rh_x rh_y rh_z`; their turn `hand_yaw hand_pitch
  hand_roll`; `left_gone right_gone` 1 for a controller not tracked
- A look from outside: `view_free 1` with `view_x view_y view_z view_yaw view_pitch` (the eyes alone moved)

## Test aids (PC only, from the environment)

| Name | What it does |
| --- | --- |
| `JAKVR_TEST_CONTINUE=<continue point>` | Once Jak has been in play on foot for 2 seconds, the game starts again at that continue point (each game's level-info.gc, e.g. Jak 1 `firecanyon-start`, Jak II `ctysluma-start`, Jak 3 `wasdoors-desert-turtle`). |
| `JAKVR_TEST_WARP=<actor>` | Jak 1: once Jak has been in play on foot for 3 seconds after any test continue, he is put a metre over that actor (its entity name, e.g. `sunken-elevator-2`, or its type's name for the first of that type), and the sim's `QUEST_SIM_ALIGN` frames count from then. `=list` logs each actor of the levels loaded (`[vr-test] actor <level> <name> <type> at x y z`, metres). |
| `JAKVR_TEST_WARP=<type>` (Jak II) | 0.1.7: once Jak has been in play on foot, the blackout over and any test continue or task started, and 3 seconds more, he is put 2 m from the nearest process of that type (one waiting for its race first: `vehicle-racer` for the stadium's racer), and the sim's `QUEST_SIM_ALIGN` frames count from then (`[vr-test] warp: Jak put 2 m from ...`, or `no ... in play`). 0.1.8 (LIST-0.1.8 item 14): `swingpole` puts him a metre over the nearest swing pole instead, so he takes it as he falls past (tests/jak2_pole.sim). 0.1.9 (LIST-0.1.9 item 23): `turret` puts him 1.5 m behind the nearest gun turret and 0.5 m up, where its Triangle prompt shows (tests/jak2_turret_hand.sim, with `JAKVR_TEST_TASK=37`). |
| `JAKVR_TEST_WARP=<type>` (Jak 3) | 0.1.9 (LIST-0.1.9 items 6, 7, 13 and 27): as Jak II's, after any test continue or task, also as Daxter alone (his ledge hang at `factory-indax-2` counts as standing) and in the Dark Maker robot (`precur-door-b` with `JAKVR_TEST_TASK=64`). For 30 s after the warp, every 2 s, the watch logs each actor of a level in play within 40 m (`[vr-test] watch:   <name> (<type>, <level>) N m away: <its state, or none-born>, perm #x...`), to see whether a door's squad was born, waits dormant or was killed. |
| `JAKVR_TEST_RIDE=1` | 0.1.8 (LIST-0.1.8 items 5 and 6), Jak II, with `JAKVR_TEST_WARP=<vehicle type>` (`bikea`, `cara`): for 3 seconds after the warp Jak is kept 2 m beside that vehicle each frame (a city vehicle drives off or sinks before Triangle reaches it), until he is on it (`[vr-test] ride: Jak is on it`, or `... did not get on in 3 s`). Once he stands there he is put on it as Triangle does (`[vr-test] ride: Jak beside it, standing: on it as Triangle puts him`; a script's Triangle came while he still fell from the warp). 0.1.9: Jak 3 too (`h-bike-a`, `h-car-b`). Jak 1 (LIST-0.1.9 item 20), with `JAKVR_TEST_WARP=fisher` (`JAKVR_TEST_CONTINUE=jungle-start`): 3 seconds after the warp the fisherman's game starts as his talk starts it (`[vr] test: the fishing game started`; tests/jak1_fishing.sim). |
| `JAKVR_TEST_WARP_OFFSET=dx,dy,dz` | Jak 1, with `JAKVR_TEST_WARP`: that many metres from the actor instead of a metre over it (the pipe game's buttons, which are not entities: `sunken-pipegame-1` with `7.5,1.3,1.25` for the right one). |
| `JAKVR_TEST_WARP_APPROACH=1` | Jak 1, with `JAKVR_TEST_WARP_OFFSET`: first to the offset, then onto the actor once the levels there have loaded, as a player walking up to it (the elevator down to the underwater city asks for the city as it starts). |
| `JAKVR_TEST_KILL_SCENE=1` | Jak 1: a scene's camera (pov-camera) dies a second into its scene, to see Jak let go (`[vr] the scene ... ended before it let Jak go`). |
| `JAKVR_TEST_GUN=1` | Jak II and 3: Jak has the gun from the start (0.1.5: Jak II also Dark Jak, his meter full and his bombs). `JAKVR_TEST_GUN=all` (0.1.5, Jak 3): every gun, Light Jak's powers, and Dark Jak's and Light Jak's meters kept full while he is neither (build 76: a new game has Dark Jak already, and the first aid left his meter empty). |
| `JAKVR_SKY_TRACE=1` | 0.1.5, PC: every 4 seconds each draw of the sky bucket (Jak 3's `sky`, Jak II's `sky-draw` since build 76): its points' depth (after the scaling to the eyes' near plane), depth test and writes, alpha test, and the near plane (`[vr-sky] frame N draw: ...`). |
| `JAKVR_TEST_BOARD=1` | Jak II and 3: Jak has the JET-Board from the start. |
| `JAKVR_STATE_TRACE=1` | Jak's state in the log as it changes (`[vr] state target-hit`; Jak 1 since 0.1.7 with the game's clock, `(at 1055.57 s)`, to time a state). Jak II and 3 with the crosshair on: what the gun's line hits as it changes (`[vr] crosshair: on "gun-dummy-a" (mask ...), enemy 1`; a type such as `instance-tie` for the level's pieces; `#f` for nothing within 40 m or the level's own ground). Jak 1 (0.1.6): while he may grab a ledge (falling, high enough), what the ledge search found each time it changes (`[vr] ledge search: N tris, N edges, N in reach; Jak at x y z`; 0.1.5's found no tri of a level, LIST-0.1.6 item 29). Jak 1 (0.1.8, LIST-0.1.8 item 15): each jump's rise as he lands, from his height on the ground to his highest (`[vr] test: the jump rose 2.95 m (to ..., landing at ...)`; Jak II and 3 too since build 116). |
| `JAKVR_TEST_TURRETS=1` | Jak II: the sewer's first gun turret destroyed through the game's own instant death once it is made (its die state, LIST-0.1.1 item 22). Jak 3: 2 seconds into play, Spargus' wall turret (`wascity-turret-1`, with `JAKVR_TEST_CONTINUE=wascityb-gungame`) is sent the `'pickup` its Triangle prompt sends, so Jak gets in (`[vr] test: turret: Jak put in ...`). |
| `JAKVR_TEST_COMBO=1` | Jak II, with `JAKVR_TEST_CONTINUE=gungame-start`: the gun course's yellow gun lesson opened and its manager told to start (`[vr] test: combo: ...`), for the punch-then-shoot combo. |
| `JAKVR_RIDE_TRACE=1` | Riding in first person, every 30 frames: the most the view moved in his seat in a frame against his head's own move, and its turn against his (`[vr-cam] riding, the most in a frame: ...`). |
| `QUEST_SIM_HZ=N` | The refresh rate the simulated headset tells (72 to start), for Game frame rate: Automatic. |
| `QUEST_SIM_PERIOD_BLIPS=N` | 0.1.5: every Nth frame the simulated headset tells a doubled frame period, as SteamVR does for each frame it missed; Automatic keeps the game's rate (a change of the headset's rate is followed once it has held a second: `[vr-view] the headset at ...`). |
| `JAKVR_TEST_TASK=<number>`, `JAKVR_TEST_RESTARTS=N` | 0.1.5, Jak 3 (and 0.1.7, Jak II, `JAKVR_TEST_TASK` alone; with `JAKVR_TEST_CONTINUE` it starts after the continue; 43 is the stadium's class 3 race): once Jak has been in play for 2 seconds, that mission played from its start (the game's play-task and its continue), then restarted N times as the pause menu's Restart mission does, a minute after and then every 20 seconds (`[vr] test: task ...`, `restart mission N`; 17 is the leaper race). 0.1.9, Jak 3: in two steps. First the game goes to the task's continue (or `JAKVR_TEST_CONTINUE`'s) with no step of the story changed (`[vr] test: task N, first to its continue <name>, then played there`), so the new game's arena opening is left and unloaded; once Jak has been 2 seconds in play there, the task is played without the menu's reset of the whole game (play-clean's `'debug`, which brought the arena's opening back), its steps opened as the menu opens them. Played in the arena, closing its steps made the arena's task manager ask for its scene level, which held the loader: the task's continue never loaded and Jak stayed in `target-continue` (sim ds on build 126). With no `JAKVR_TEST_WARP`, once Jak has been 2 seconds in play after the task (`[vr] test: the task's set-up done, Jak in play there`) the sim's `QUEST_SIM_ALIGN` frames count from then (tests/jak3_power_game.sim). A pause menu left open in play (a sim prelude's late Start) is closed first, once, and the aid waits for play (`[vr] test: the pause menu closed, the task aid waits for play`): a continue started behind it never loaded (sim ds on build 127). Numbers used in 0.1.9: Jak II 37 the drill platform, 51 the tomb's boss; Jak 3 51 the maze game, 54 the Scorpion's gun, 64 the Dark Maker ship, 65 the blow tower, 73 an orb hunt. |
| `JAKVR_TEST_SOUND_STALL=S` | 0.1.5, Jak 1: the sound processor's player stops answering for S seconds at its 1200th batch of commands (about 20 seconds into a run; `[vr-test] the sound player held for S s`, then `goes on`), and the game, once the player has been busy for 30 frames, sends 300 sound commands at once (`[vr] test: 300 sound commands while the sound player is held`), so the sound queue fills as in the tester's freeze. |
| `JAKVR_TEST_GAME_STALL=S` | 0.1.5: the game's thread held S seconds at its 1500th frame (`[vr-test] the game held for S s`, then `goes on`), for the stall watch's `[vr-stall]` lines. PC only. |
| `JAKVR_TEST_BAD_TEXTURE=N` | 0.1.5: the Nth texture page the game uploads has its first entry read as 0 (`[vr-test] page N's first entry read as 0`), as the page in Jak 3's crash on a restart had: the texture guard passes it over. |
| `JAKVR_TEST_TETHER=1` | 0.1.6, Jak 1, with `JAKVR_TEST_WARP` onto one of Boggy Swamp's tether rocks: 3 seconds after the warp the rock takes a hit of Jak's yellow eco (`[vr] test: yellow eco to swamp-tetherrock-...`), so its scene plays and Jak is seen let go after it (pc-vr.gc vr-test-tether!). |
| `JAKVR_PARTY_TEST=host` or `=join` | Multiplayer: host a game as soon as the game is up, or look for games and join the first of this build found. |
| `JAKVR_PARTY_TEST_SAVE=copy` | With `=join`: once the host's Jak is in the game, play the host's progress (Play which save?). |
| `JAKVR_BODY_TRACE=1` | How the posed body sits, twice a second; the merc models the eyes draw, once each; each frame Jak's bones came from the game, or were posed, with their last column off 0 0 0 1 (made affine). 0.1.8: a jump of his skeleton from his root that is not one toward the world's origin (those are set right and logged anyway) says how it lay to that line, what moved and how far his bones spread (`[vr-body] a jump of N m: ...`). |
| `JAKVR_BODY_TRACE=2` | As 1, and each frame by its number: his body's big parts from the eyes (`frame N parts:`), his spine as the game sent it (each joint's size and how far from the one under it: a stretch of his body shows), the eyes, root, view's ease and lean, the neck and its base posed and as the game had them; as drawn, the head's shrink point in the left eye's camera; and as the frame is sent to be drawn, how far the neck base's bone and the camera moved after they were posed and placed. Jak 1 also: the head point as his skinning matrices are made (bones.gc), with the camera and neck base then. |
| `JAKVR_BODY_TRACE=3` | As 1, and each frame each hand's fingers (`[vr-body] the right fingers`: each finger's curl, how far the world moved it, how many surfaces the hand is at), to see them steady. |
| `JAKVR_CLOCK_TRACE=1` | Jak II and 3: the game's clocks every 60 of its frames (`clock:` lines), to see it keep time at half rate. |
| `JAKVR_FEEL_TRACE=1` | 0.1.4's Extra vibration: every buzz it gives (`[vr-feel] step (1 0; Extra vibration: The whole world)`, a pickup's first number its pickup type), not only the first of each kind. The simulated headset's own `[sim] haptic hand` lines show what reached the hands. |
| `JAKVR_TEST_HUD=1` | The HUD kept on screen in play (each game's show-hud), for the HUD tests. |
| `JAKVR_TEST_PILLS=1` | Jak 3: five seconds into play, dark eco pills made 6 m from Jak and set to fade 4 seconds later (`[vr-test] eco pills ...`), the state the arena's crash was in (LIST-0.1.1 item 23). |
| `JAKVR_POINTER_TRACE=1` | Jak II and 3: the menu pointer's dot against each two-way line (Yes / No, On / Off) as it moves or clicks (`[vr-ptr] two-way line: ...`), its middle and the menu's transition. |
| `JAKVR_GLOW_TRACE=1` | Jak II and 3: each glow faded because its flare comes within 2 m of the eye, or drawn within 4 m of it (its depth, how much nearer the flare is drawn, its size on screen and its most, the light left), in the left eye. |
| `JAKVR_TRACE_JOINTS=14,64,...` | Those skeleton nodes' places from the eyes too, with the body trace. |
| `JAKVR_HIDE_TEST=<name>` | Every model whose name starts so left out of the eyes (which model is that?). |
| `JAKVR_HIDE_JOINTS=14,64,...` | Those nodes shrunk out of the eyes (which joint is in the way?). |
| `JAKVR_BUCKET_LOG=1` | In the left eye: each draw bucket starting and stopping, and the share of the picture the shadow pass darkens. |
| `JAKVR_SHADOW_EYES=1` | 0.1.8 (LIST-0.1.8 item 9), PC: how much of each eye's picture the shadows darken, frame by frame (Jak 1's ShadowRenderer, Jak II and 3's Shadow2), compared between the eyes: a frame where they differ by more than 0.3% of the picture and a quarter of the larger, the first ten (`[vr] shadow in each eye: left 2.31%, right 0.40% of the picture (frame N)`), and every 600 frames with a shadow, the averages, how many frames differed and the most (`[vr] shadow in each eye, the last 600 frames with one: ...`). Build 118: the first six frames the eyes part by over 2% of the picture on the eyes' own count are pictured by the simulated headset, each with its line (`[vr] shadow in each eye apart: ... (frame N; the simulated headset's pictures apart-N, of the frame after)`, `apart-N-left.png` and `-right.png` in the run's folder; a real headset takes none). |
| `JAKVR_SHADOW_ZPASS=1` | 0.1.8 (LIST-0.1.8 item 9): Jak II and 3's shadow volumes counted in the eyes as the flat game counts them (in front of what they fall on), not behind it, to compare (sim `sh2p`). |
| `JAKVR_TEST_CREDITS=1` | 0.1.8 (LIST-0.1.8 item 11), Jak 1, with `JAKVR_TEST_CONTINUE=finalboss-fight`: once the sage at the final boss has waited 5 seconds with Jak in play, the end credits start as after the last scene (`[vr] test: credits`; sage-finalboss.gc, pc-vr-test-flags bit 65536), for tests/jak1_credits.sim. |
| `JAKVR_TEST_AUDIO_LOSS=<from>,<for>` | 0.1.8 (LIST-0.1.8 items 2 and 3), PC: the sound device taken away that many seconds after the sound starts, for that many seconds (the simulated headset's `audio_loss` script line asks it at a frame instead): its asks for sound give silence and do not count, so the sound watch takes over (`[audio] the sound device has not asked for sound for 1 s ...`, the device opened again every 3 s, then `[audio] the sound device is playing again`); `[audio] test: the sound device taken away now`, `given back now`. With `old` in it (`=old` alone for the script line's, or `<from>,<for>,old`) the watch leaves the game's sound stopped too, as before 0.1.8, so every sound stream stops and a streamed animation waits on it until the stuck-stream guard ends it (989snd player.cpp and audio_loss_test.h, xr_sim.cpp). |
| `JAKVR_TEST_UNSHOWN=<from>,<for>` | 0.1.8 (LIST-0.1.8 item 4), PC VR (the real OpenXR backend: SteamVR's null driver, as C:\JakQuest\sim\pcvr_null_jak.ps1 runs it): that many seconds after the headset first shows the game, it counts as not showing it for that many seconds (`[openxr] test: ...`), so the lines every 10 s (`[openxr] the headset has not shown the game, or not given it the controllers, for N s: ...`), the window's title (`[vr-view] the window's title says: ...`, then `is the game's again`) and the line when it shows the game again are seen. |
| `JAKVR_REPROJECT=0` | What the game had already put on the screen for its own camera is drawn as it was, not taken to each eye's view (to compare). |
| `JAKVR_TEST_CRASH=av`, `abort`, `stack`, `terminate` or `invalid` | 0.1.7 (LIST-0.1.7 item 13), PC: the game made to end that way at its 1500th frame (`stack`: a stack overflow on the display's thread, since the game's own code runs on stacks in its memory), so the `[crash]` record and the launcher's exit line can be seen. |
| `JAKVR_CRASH_DUMP=<folder>` | The first access violation writes a dump there: crash.txt (the registers, the stack's words, rip and the game memory's base) and ee.bin (the game's memory from 512 KB on). A rip or stack word minus the base, inside the game's memory, is GOAL code: its function is found from the symbol table in ee.bin (Jak II and 3: s7 is r14, a symbol's name pointer at it + 0xff37). |
| `QUEST_SIM_ALIGN=F` | Script frames from F on count from the frame the test's set-up was done. 0.1.8, Jak 3: also the frame a `JAKVR_TEST_CONTINUE` seats Jak in a vehicle (`[vr] test: seated by the continue, the set-up done`; the glider's, the sled's and the desert's continues seat him at a frame that varies). With no such mark the lines from F never come, and the run goes on until it is stopped (the batch scripts' watchdog). |
| `QUEST_SIM_GRIP_TILT=D` | The grip pose turned D degrees up from the aim pose, as on the Quest's controllers. |
| `head_yaw_rate`, `head_pitch_rate` (script lines) | 0.1.8: degrees a second the simulated head keeps turning or nodding, a frame's share each frame (`head_yaw` or `head_pitch` lines set where it is; a rate of 0 stops it), for a head never still, as a player's (tests/jak1_shadow_eyes.sim). |
| `audio_loss <seconds>` (script line) | 0.1.8 (LIST-0.1.8 items 2 and 3): from that frame the sound device taken away for that many seconds (`[sim] the sound device taken away for N s`), as `JAKVR_TEST_AUDIO_LOSS` does (tests/jak1_cell_sound.sim, jak2_title_sound.sim). |
| `QUEST_SIM_SPACE_WARP=0` | No motion pictures (Smoother frames off). |
| `JAKVR_TEST_LATE=<from>-<to>` | Those display frames count as late for Dynamic resolution (the eyes step down, then back up when the frames are in time again). |
| `JAKVR_FRAME_TRACE=<ms>` | 0.1.6, Jak 1: each game frame whose own work (vsync to vsync, less its waits for the renderer) took longer than that (`[vr-frame] game frame N: its work X ms (Y ms with its waits)`). |

## The scripts

- Start and play: `boot_title`, `title_start`, `jak1_newgame`, `jak1_play`, `jak2_newgame`, `jak2_toplay`,
  `jak2_play`, `jak3_toplay`.
- The body in first person: `jak1_body`, `jak1_ahead`, `jak1_down`, `jak1_down_out`, `jak1_collar`, `jak2_body`.
- Body shown (run once for each choice, `body-shown 0` to `6` in the settings folder's vr-options.txt):
  `jak1_body_shown`, `jak2_body_shown`, `jak3_body_shown`; C:\JakQuest\sim\body_shown_all.sh runs them.
- Moving and the view: `jak1_turn`, `jak1_motion` (Smoother frames), `jak1_hud_stays`, `jak1_ocean`, `jak1_wall`
  (hands against a wall), `jak1_ledge_klaww` (0.1.6: a crouch's high jump to the ledge on the climb to Klaww, grabbed and
  climbed; with `JAKVR_TEST_CONTINUE=village2-start JAKVR_TEST_WARP=villageb-ogreboss-1`, its header says the rest).
- Melee (Fists on the right grip): `jak1_melee`, `jak2_melee`, `jak3_melee`.
- Guns (with `JAKVR_TEST_GUN=1`): `jak2_gun`, `jak2_holster`, `jak3_gun`.
- Glows at the eyes: `jak3_beam` (through Spargus' light beam, `JAKVR_GLOW_TRACE=1`).
- Pop-in (run with Max draw distance on and off): `jak1_popin`, `jak2_popin`, `jak3_popin`, and in big levels with
  `JAKVR_TEST_CONTINUE`: `jak1_popin_village` (`village1-hut`), `jak2_popin_city` (`ctysluma-start`).
- Vehicles (the view goes to third person by itself): `jak1_zoomer` (`firecanyon-start`: a new game is asked for
  power cells there), `jak1_zoomer_misty` (`misty-bike`), `jak2_board` (`ctysluma-start`, `JAKVR_TEST_BOARD=1`), `jak3_buggy`
  (`wasdoors-desert-turtle`).
- Jak 1's scenes in the underwater city (Lost Precursor City) and Rock Village's elevator down to it: `jak1_city_scene`
  with `JAKVR_TEST_CONTINUE`, `JAKVR_TEST_WARP` (the scene's trigger) and `QUEST_SIM_ALIGN=10000`, e.g.
  `village2-dock` with `sunken-elevator-2`, `sunken-start` with `square-platform-button-1`, `sunkenb-helix` with
  `helix-button`; C:\JakQuest\sim\batch28a.sh's `elev2`, `sq1`, `sq3`, `pipeR`, `pipeM`, `pipeK` (the pipe button
  with `JAKVR_TEST_KILL_SCENE`), `helix1`. Jak 1's dive (the floor buttons) is X in the air once he rises slower than
  6.5 m/s: the test jumps, then presses X twice near the top. `jak1_warp_list` with
  `JAKVR_TEST_WARP=list` lists a level's actors to pick from.
- Multiplayer, two games at once: C:\JakQuest\sim\mp_host_pose.sim and mp_guest_pose.sim with
  `JAKVR_PARTY_TEST=host` and `=join` and their own settings folders; the host's progress for the guest:
  C:\JakQuest\sim\batch11.sh (mp_host.sim and mp_guest.sim, the guest with `JAKVR_PARTY_TEST_SAVE=copy`; its log
  shows "the host's progress came" and "save 4 done").
- Multiplayer's VR menu pages: `jak1_mp_menu` (the stick down to Host a game, then each page and B back).
- Multiplayer over the party server, with Remove: C:\JakQuest\sim\batch13.sh (tools/party_server.py on port 8139,
  `server 127.0.0.1:8139` in each settings folder's party.txt; the host's mp_host_remove.sim opens the VR menu's
  Players page and removes the guest).

## The scripts added in 0.1.1

Settings folders beside the default: C:\JakQuest\sim\gkcfg-fpv (`first-person-vehicles 1`), gkcfg-spread
(`spread-spin-kick 1`), gkcfg-hide (`hide-arms-near-eyes 1`), gkcfg-half (`frame-rate 2`, `dynamic-resolution 1`),
gkcfg-warp (`smoother-frames 1`) and gkcfg-full (`frame-rate 1`, the game at the headset's own rate).

- The left stick's click as the menu button: `jak1_stick_start`. Walking straight: `jak1_walk_straight`
  (`JAKVR_BODY_TRACE=1`: the stick straight ahead, then looking down, then turned, then a little off its axis, then
  turned round, away from the cliff, with the head tilted looking down and up; Jak's heading stays with the stick's
  direction from the head: the `[vr-body] main` lines' heading against the head's).
- The HUD's own models on the HUD panel (`JAKVR_TEST_HUD=1`): `jak1_hud`, `jak2_hud`, `jak3_hud` (the icons beside
  their numbers in each `-panel.png`, none in the eyes; `[vr-view] the HUD's models in bucket N`).
- Punching by the left grip in Jak II: `jak2_left_fist` (`JAKVR_STATE_TRACE=1`: `[vr-pad] fists up: the left grip`).
- Arms by the face: `jak1_arm_near`, `jak1_arm_poses`, `jak2_gun_face` (gkcfg-hide).
- Straps and fingers on the world: `jak1_straps` (`JAKVR_BODY_TRACE=1`: `[vr-body] strap` lines), `jak1_fingers`
  (the hand at the ground, turned palm down and fingers down: `[vr-body] the right fingers`, each finger's curl and
  how far the world moved it), `jak1_wall`.
- A roll in first person: `jak1_roll` (`JAKVR_BODY_TRACE=2`, `JAKVR_STATE_TRACE=1`; the view lower through it, and
  `[vr-body] his body and legs fade out of the eyes: his roll` and `come back to the eyes` after).
- Jak's moves on foot from his eyes: `jak1_actions`, `jak2_actions`, `jak3_actions` (made by
  C:\JakQuest\sim\make_actions.sh; `JAKVR_STATE_TRACE=1 JAKVR_BODY_TRACE=1`): a picture every few frames through the
  spin kick, the punch, the double jump, the crouch and high jump, the uppercut, the long jump and the dive. Nothing of
  Jak should be open or fill the eyes in any of them: a leg near the eyes, and his body in a roll, held turned over or
  with its chest at the eyes, fades out (`[vr-body] the left leg fades out of the eyes (...)`, `his body and legs fade
  out of the eyes: <why> (...)`, then `comes back to the eyes`); what is left within 16 cm fades, dithered, gone by 7
  cm (`[vr-body] the eyes fade merc model ...`, once a model). `[vr-body] nearest before the eyes: joint N (...)` names
  the nearest joint the eyes draw within 30 cm; `[vr-body] steadiness` the most each of the elbows, hands, knees,
  chest and hips jumped in a frame (cm), once every 30 frames. With `JAKVR_BODY_TRACE=2` each frame also gives
  `[vr-body] frame N parts:` (main, chest, collars, thighs, knees and hands from the eyes, the player's metres: right,
  up, ahead; the hips', head's and game's headings; each fade). Seen from behind: C:\JakQuest\sim\jak1_actions_out.sim.
  The life-size scale is the start now (World scale 1); C:\JakQuest\sim\gkcfg-life (world-scale 0.68) is not needed.
- Jak 1's look-around (Triangle): `jak1_look_around` (`JAKVR_STATE_TRACE=1 JAKVR_BODY_TRACE=1`): `[vr-body] the game
  left N bones as posed last frame` in it and `the game makes his bones again` after, the `[vr-cam]` lines' eyes by
  Jak's root and the view by him.
- C:\JakQuest\sim\batch28a.sh runs any of these by name (`OUT=<folder> SIMS="act1 act2 melee2 ..."`) and prints each
  one's fade, steadiness or state lines.
- The gun's hold seen from outside: `jak2_gun_hold` (`JAKVR_TEST_GUN=1 JAKVR_BODY_TRACE=1`): the gun drawn, from the
  eyes, his right side and in front, then a shot; `[vr-body] the gun's nearest joint N m from the eyes` once a second.
- Scenes: `jak1_scene_view` (the left menu button held), `jak1_scene_face`, `jak1_scene_adaptive` and
  `jak3_open_adaptive` (`[vr-view] adaptive cutscenes` lines), `jak3_title`.
- The menu pointer: `jak1_ptr_slots`, `jak2_ptr_menu` (Jak II's multiplayer on, as it starts: the title's five rows;
  `jak2_ptr_menu_mpoff` with it off, gkcfg-mpoff: four), `jak3_ptr_menu` (with `JAKVR_POINTER_TRACE=1`: the dot
  against each Yes / No line, every trigger press, a click that found no row, and each auto-scroll step); the
  auto-scroll down Jak II's Options menu: C:\JakQuest\sim\jak2_ptr_scroll.sim. The stick beside a resting, shaking
  dot on Jak 1's title (LIST-0.1.1 item 42): `jak1_ptr_stick` (the highlight stays where the stick puts it, A picks it).
- Every pad button in play: `jak1_buttons`, `jak2_buttons`, `jak3_buttons` (made by C:\JakQuest\sim\make_buttons.sh;
  `JAKVR_STATE_TRACE=1`, Jak II and 3 also `JAKVR_TEST_GUN=1 JAKVR_TEST_BOARD=1 JAKVR_BODY_TRACE=1`: the right stick's
  click puts him on the JET-Board and takes him off again, then the left grip makes Dark Jak), `jak2_dpad`,
  `jak2_dpad_head`.
- Jak 3 after the opening, from C:\JakQuest\sim: `jak3_jump_short` (the jump's steadiness), `jak3_pills`
  (`JAKVR_TEST_PILLS=1`, item 23's fading pills), `jak3_arena_look` (`JAKVR_TEST_CONTINUE=wasstada-fight`). A fresh
  profile's auto-save opens the save prompt over play after the scene skip; A and Y in turn get out of it.
- Spin kick by spreading your arms: `jak1_spread_kick` (gkcfg-spread). Hit by the ground: `jak1_lava`
  (`firecanyon-start`). Each eye's picture of a door: `jak1_eyes_door` (`training-warp`).
- Performance options: any script with gkcfg-half (the log says "the game at 36 frames a second (the headset shows
  72)"; with `JAKVR_TEST_LATE` the `[vr-perf] dynamic resolution` lines) or gkcfg-warp (`[vr-view] smoother frames`
  and the simulated headset's own count of the motion it was given, marked to skip or shown again).

## The checks added in 0.1.4

- Extra vibration (C:\JakQuest\sim\batch28c.sh, `JAKVR_FEEL_TRACE=1`): `feel1`, `feel2`, `feel3` (each game's
  `jakN_actions` on gkcfg-feel, The whole world: steps, jumps, landings, rolls, attacks), `feeloff` (Off: no
  `[vr-feel]` line at all), `feelpick` (`jak1_play` on the defaults, Pickups: no step or move buzzes), `feelorb`
  (`JAKVR_TEST_WARP=money`: Jak dropped onto Geyser Rock's first orb, `small pickup (5 0; ...)`), `feelcrate`
  (`JAKVR_TEST_WARP=crate` with an offset, the actions beside a crate; its kick does not break it: Geyser Rock's
  crates are iron) and `feelflop` (`jak1_crate_flop`, `JAKVR_TEST_WARP=crate`: the dive onto the crate breaks it,
  `hit (0 0; ...)`, and its scout fly, `big pickup (8 0; ...)`).
- The Quest's driver notes (LIST-0.1.4 item 10): `ocean` (`jak1_ocean`, the sea and Jak's shadow, before and after);
  a repeated driver message is written 3 times, then at the 10th, 100th ... with `(the 10th time)`.

## The checks added in 0.1.5

- The crosshair (C:\JakQuest\sim\batch28g.sh): `crossdef2` (`jak2_gun` on the defaults: `[vr] options: ...
  crosshair-kind 0`, no `[sim] crosshair` line), `cross2` (gkcfg-cross, 0.1.3's `crosshair 3`: kept, the Cross drawn),
  `ring2` and `ring3` (gkcfg-ring, `crosshair-kind 7` at 100%: the Small circle drawn). The sim stamps the crosshair
  with nearest sampling at its eyes' 10 pixels a degree, coarser than any headset: a thin line looks broken there.
- `blips1` (`jak1_play` on gkcfg-perf with `QUEST_SIM_PERIOD_BLIPS=7`): `[sim] a doubled frame period told every 7
  frames`, then one `[vr-view] the game at 72 frames a second` and no other.
- `task3` (C:\JakQuest\sim\jak3_task_restart.sim, `JAKVR_TEST_TASK=17 JAKVR_TEST_RESTARTS=4`): the leaper race played
  and restarted four times by the game's restart-mission (each a leaper death, `resetting with ... wascitya-flut-racer`
  and the race again); `task3m` (jak3_menu_restart.sim, `JAKVR_TEST_RESTARTS=0`): the same four times through the
  pause menu (menu, up, A, A for Yes / No, left for Yes, A), as a player does, with its sound bank swap (wascity3,
  menu1, wascity3). Before 0.1.5's texture guard, task3m crashed at the third restart, as the tester's game did (`[crash]
  the game crashed: exception 0xc0000005 in gk.exe+0x420340`, a read at 0x0); with it, 20 restarts in three runs
  passed, the guard not reached: the bad page comes only now and then. `task3m8` and `task3m8b` (jak3_menu_restart8.sim): eight restarts through the pause menu, from frame 6200
  every 2000.
- `badtex3` (`jak3_actions` with `JAKVR_TEST_BAD_TEXTURE=40`): that guard, on purpose: `[vr-test] page 40's first
  entry read as 0`, then `[vr] a texture page the game uploads has a bad entry (a texture outside the game's memory:
  entry 0, 0x0): passed over (1 so far)`, and the game goes on.
- `sstall1` (`jak1_play` with `JAKVR_TEST_SOUND_STALL=5`): the sound queue never waiting forever: `[vr] test: 300 sound
  commands while the sound player is held`, `too many sound commands queued` and the queue's dump (a line for each run
  of the same command, each play by its sound's name), `the IOP has not answered the sound player for over a quarter
  of a second: 1 sound commands dropped` (and at 10 and 100), the sim's frame lines on time through the hold, then
  `the IOP answers the sound player again: N sound commands were dropped`.
- `gstall1` (`jak1_play` with `JAKVR_TEST_GAME_STALL=7`): the stall watch: `[vr-stall] the game has not finished a frame
  for 1.0 s: it is held by the test (JAKVR_TEST_GAME_STALL); the display is ...; the IOP: ...` (and at 5 s), then
  `[vr-stall] the game went on after 7.x s`.
- Every batch28g run's summary counts `[vr-stall]` lines, `the IOP has not answered the sound player` lines, `[crash]`
  lines and the texture guard's `bad entry` lines.
- The owner's and the tester's later reports (C:\JakQuest\sim\batch28h.sh and batch28i.sh, LIST-0.1.5 items 13 to 22):
  - `pturn2` (`jak2_pause_turned`): Jak II from its start through the opening's skips: each blackout `[vr-view] a
    blackout kept in the eyes, black: N frames (S s)` with the sim's `eyes 1 panel 0` and black pictures (the load into
    the fortress about 18 s), no `[vr-stall]`; then the head turned 150 degrees and Start: the pause menu on the HUD
    panel in front, the world in the eyes. Also `[vr] test: multiplayer game #f, host #f, guest #f` (alone, the mod on).
  - `pauseb1` (`jak1_pause_b`): Jak 1's pause with the head turned: the power cell page ("Circle OPTIONS") on the HUD
    panel; B opens the Options, B again goes back, Y the scout flies page.
  - `joinb2` (`jak2_join_b`): Jak II's lobby list ("Circle Manual"): B opens the Manual page, Y goes back.
  - `boardon` and `boardoff` (`jak2_board` at ctysluma-start, gkcfg and gkcfg-fpoff with `first-person-jet-board
    off`): first person on the JET-Board, and with its switch off `view in play: third person (...)` as he gets on,
    first person as he gets off. `zoomoff` (`jak1_zoomer_misty` at misty-bike, `first-person-zoomer off`): third person
    on the Zoomer (camera flags 0x10151, ride kind 1).
  - `eff3` (`jak3_effects`, `JAKVR_TEST_GUN=all JAKVR_STATE_TRACE=1 JAKVR_SKY_TRACE=1`): every gun fired at his feet and
    ahead, pictures 2 to 40 frames after: no black or colored square in either eye, the HUD panel only the HUD (its
    pictures under 5% drawn), `[vr-view] the game's zoom blur is left out of VR` (the Wave Concussor, the Plasmite RPG)
    and `slow time's trail` (Light Jak's freeze); Light Jak by the grip's squeeze again and held, then X.
  - `eff2` (`jak2_effects`, `JAKVR_TEST_GUN=1`): Dark Jak by the left grip's quick squeeze (`[vr-pad] a quick squeeze of
    the left grip: L2`, `state target-darkjak-get-on`), his lightning, punch and bomb, then each gun: no square in the
    eyes or on the HUD panel. (Its Dark Jak part starts at frame 4900 since 0.1.5: the load into the fortress, black
    since item 21, lasts to about 4660.)
  - `dark3` (`jak3_darkjak` at templea-start, `JAKVR_TEST_GUN=all`, which keeps both meters full while he is neither):
    Dark Jak by a quick click of the left stick (6 frames: `[vr-pad] a quick click of the left stick: L2 held 0.30 s in
    all`), back by a 2-frame click (inside the click's hold-back), again by a 10-frame click, back, by the left grip's
    quick squeeze; each `state target-darkjak-get-on`. Then Light Jak's shield (the grip squeezed, again and held, B).
  - `sky3` (`jak3_sky_desert` at wasdoors-desert-turtle, `JAKVR_SKY_TRACE=1`) and `sky2` (`jak2_board` at ctysluma-start,
    the same trace): the sky's `[vr-sky]` lines, its clouds, base and haze at depth 400 in third person and 204 in
    first person (the eyes' near plane, 356 game units, over the game's 1024), and the far hills whole in the pictures.
  - `hclick2` (`jak2_head_clicks` at ctysluma-start, `JAKVR_STATE_TRACE=1`): the left hand held by the head (`lh_x 0.05
    lh_y 0.35 lh_z 0.3`), the right stick's click `[vr-pad] L3: the right stick's click with the left hand by the
    head`, the left's `Select: the left stick's click ...` (Jak II alone does nothing with Select; Start opens its
    pause menu). Then a Vive wand (`no_left_click 1`, `no_thumb_rest 1`), the hand by the head: X with the left stick
    at rest `Select: the left touchpad's click ...`, X with it pushed right `d-pad right` alone.

## The checks added in 0.1.6

C:\JakQuest\sim\batch28m.sh (OUT=b30), build 83:

- `titlemp` (`jak1_title_mp`, `JAKVR_POINTER_TRACE=1 JAKVR_STATE_TRACE=1`): Jak 1's title has MULTIPLAYER under Load
  Game; A on it: `[vr-menu] open Multiplayer (the title's Multiplayer)`, the page's help in two lines above the hints;
  Host a game: `page: Host which save?` (the four slots and New Game, no "This game as it is" at the title); B:
  `page: Multiplayer`; B: `closed`, the title menu there with Multiplayer lit; New Game: the game's own save-slot
  page. No `party` line but `party: on as ...` (nothing hosted or joined).
- `pausemp` (`jak1_pause_mp`): a new game, Start, the pause's Options: MULTIPLAYER under VR OPTIONS;
  `[vr-menu] open Multiplayer (the pause menu's Multiplayer)`; B closes it back to Options with Multiplayer lit;
  Start: play.
- `load1` (`jak1_make_save` then `jak1_load_game`, gkcfg-load, `KEEP_SAVES=1`): Load Game, still the title's second
  row, loads the save (`target-continue`, `target-warp-in`, `target-stance`). With it on build 83 (b30d.out): `save1`,
  `pauseb1` (B in Jak 1's pause pages) and `act1` (the actions and their steadiness), no errors, stalls or crashes.
- `boatb` (`jak1_intro_boat_timed`, `JAKVR_FRAME_TRACE=8 JAKVR_BUCKET_LOG=1`): the opening's cut to Jak lying back on
  the boat: `Displaying level misty`, `Setting force-all-visible?[misty] to #t`, then `[vr] bucket [20] l0-shrub draws
  (25 draws, 91462 triangles)` with Misty's tfrag, tie and merc; no `[vr-frame]` line there (no game frame over
  8 ms). Misty's render data loaded at about frame 3000 (`Loader::update`), long before.
- `dynq` (`jak1_play` on gkcfg-perf, `QUEST_SIM_HZ=120 JAKVR_TEST_LATE=3700-4700`): dynamic resolution's quick step
  down: `the eyes at 90%`, 80%, 70% and 60% within about a second (0.1.5's rule, b64's auto120: one a second), then
  back up a step each 5 s once the frames are in time.
- `crouchb` and `crouchp` (C:\JakQuest\sim\b29i: `jak1_crouch_button`, the left trigger held, and `jak1_crouch`, the
  body's crouch, `JAKVR_STATE_TRACE=1`): the same `[vr] effect breath-in` / `breath-out` lines about once a second in
  both, from the game's crouch animation.
- Jak II's one-player restorations (LIST-0.1.6 item 20) compiled for the first time in build 80: four fixes (a cast
  for trajectory.gc's ignore-process1, the mod's names for the same bits: collide-spec `replica` for the original's
  `tobot`, traffic-spawn-flags `spawn-rider` / `skip-lwide-entity-hack` for `trsflags-01` / `-00`, and two
  wrappers that returned nothing where the original returns a value: fort-robotank method 26 and the hoverboard
  training manager's event handler). Then, with C:\JakQuest\sim\checks016's scripts (python -I): every solo
  wrapper's last form against its original's (check_wrappers.py, check_events.py: the event handlers return the
  original's value), every enum member the mod renamed and its uses (enum_renames.py), the VR menu's help over two
  lines (help_lines.py) and characters the games' font draws as buttons (semis.py: `;` is the Triangle).

C:\JakQuest\sim\run_b31.sh (batch28m.sh, OUT=b31), builds 84 and 85:

- `board3s` (`jak3_board_swap`, `JAKVR_TEST_CONTINUE=templea-start JAKVR_TEST_BOARD=1`): on Jak 3's board, B: `[vr-pad]
  Jak 3's JET-Board: B, Square (the grind, held)`, X: `X, Circle (the zap)`; off the board X is the punch
  (`target-running-attack`) and B the kick (`target-attack`) as before.
- `skip3` (`jak3_scene_skip`, Y every 20 frames from a scene's start): `the scene "intro-drop" skipped: Triangle, 1.1 s
  in`, never before a second; `buttons3`, `light3` the same.
- The Jak II sims (`act2 pturn2 pstick2 eyes2 boardon dark2 turret2 combo2`): each stopped at the prison's start,
  `ERROR: suspend called without enough stack in proc: warp-gate-b ... Stack: 640/512` (LIST-0.1.6 item 28, fixed in
  build 86: the mod's :code in a lambda of its own). The run's `stack:` count in batch28m.sh is that line.
- `tether1` (C:\JakQuest\sim\run_tether.sh: `jak1_tether_rock`, `JAKVR_TEST_CONTINUE=swamp-start
  JAKVR_TEST_WARP=swamp-tetherrock-13 JAKVR_TEST_TETHER=1 QUEST_SIM_ALIGN=10000 JAKVR_STATE_TRACE=1`, build 86): the
  rock's scene (`target-grab`, mode 1), then `target-stance` and Jak walking and jumping (LIST-0.1.6 item 27).

C:\JakQuest\sim\run_b35.sh (OUT=s27), build 89 (LIST-0.1.6 items 27 and 29):

- `ledge-after` (C:\JakQuest\sim\run_ledge.sh: `jak1_ledge_klaww`, `JAKVR_TEST_CONTINUE=village2-start
  JAKVR_TEST_WARP=villageb-ogreboss-1 JAKVR_TEST_WARP_OFFSET=23.53,-11.3,8.82 QUEST_SIM_ALIGN=10000
  JAKVR_STATE_TRACE=1`): the cliff up to Klaww, a crouch (the left trigger) and a high jump straight up:
  `state target-edge-grab`, `[vr] ledge search: 4 tris, 9 edges, 2 in reach; Jak at 270.10 33.62 -1753.70`, Jak
  hanging, then `target-edge-grab-jump` and the grass on top (`lk-end`). A run's or a double jump's top there is near
  31.4 m (an overhang), short of the ledge. The run's `edge grabs:` count is `state target-edge-grab`.
- `tether-swamp-tetherrock-13` to `-16` (run_tether.sh with `ROCKS=`): each rock in a run of its own, `target-grab`
  in its scene, then `target-stance` and Jak walking.
- `mp1` (Jak 1's party, host and guest on this PC, `JAKVR_PARTY_TEST=host` and `join`, the guest playing the host's
  progress) and `play1` (`jak1_play`): no errors.

## The checks added in 0.1.7

Runs in C:\JakQuest\sim\s07 (scratch), builds 87 to 98 (LIST-0.1.7). Each "the close, as the tester's" run is before
its fix, the next after it.

- `snow1`, `snow2` then `snow3` (`jak1_snow_load`, `JAKVR_TEST_CONTINUE=snow-fort JAKVR_STATE_TRACE=1`; item 12): before,
  the continue waited in black (no `Swapping in sno VIS`); after, `Swapping in sno VIS`, `snow is loaded`, Jak walking
  in first person, no stall.
- `hardland1` (`jak1_hard_land`, `JAKVR_TEST_CONTINUE=snow-fort JAKVR_TEST_WARP=eco-321 JAKVR_TEST_WARP_OFFSET=0,2,-21
  QUEST_SIM_ALIGN=10000 JAKVR_STATE_TRACE=1`; item 6): a 30 m drop at Snowy's fort: `state target-hit-ground-hard`, the
  game's camera while he is down (about 6.2 s), then his eyes as he walks. `ecofall1` to `ecofall3` (`jak1_eco_fall`,
  `JAKVR_TEST_WARP=eco-321`, blue eco by the orb caches): before, `ERROR: touching-list::get-shapes-entry() failed!`;
  after (64 pairs), none. The run's `get-shapes:` count is that line.
- `forest1`, `forest2` then `forest3` (`jak3_forest_task`, `JAKVR_TEST_TASK=43 JAKVR_TEST_CONTINUE=mhcitya-forestx
  JAKVR_STATE_TRACE=1`; item 7): before, the game stopped itself on the mission's first frame (`[crash]` with
  `finish-background` named, the game's own stop); after, `the frame's draw list: 1459 KB of 5120 KB used`, the mission
  plays on.
- `race1` (C:\JakQuest\sim\race_task.sim, `JAKVR_TEST_TASK=43`: the race's task, Jak standing at its start) then
  `race2` (`jak2_race_ride`, `JAKVR_TEST_TASK=43 JAKVR_TEST_WARP=vehicle-racer QUEST_SIM_ALIGN=10000
  JAKVR_STATE_TRACE=1`; item 11): the close as Jak got in (build 91, the record naming the racer's event handler);
  after the fix (build 92), `vehicle::event-handler: pilot-on`,
  `target-pilot-start`, `target-pilot-get-on`, the countdown and about 250 m of the race in first person.
- `crash-av`, `crash-abort`, `crash-stack`, `crash-terminate`, `crash-invalid` (`jak1_play` with `JAKVR_TEST_CRASH=`) and
  `crash-stall` (`JAKVR_TEST_GAME_STALL=12`; item 13): a full `[crash]` record for each (what, the thread, module and
  offset, the registers, the calls with the game's functions named, `the game was:`), and for the stall the
  `[vr-stall]` lines saying which thread and where.

C:\JakQuest\sim\batch29a.sh (and batch29b.sh, build 97), the options and menus; the run's `opts` line is the options
as the game read them:

- `pole1`, `pole0` (`jak1_pole`, `JAKVR_TEST_CONTINUE=swamp-start JAKVR_TEST_WARP=swingpole-81 QUEST_SIM_ALIGN=10000
  JAKVR_STATE_TRACE=1`, gkcfg and gkcfg-poleoff; item 4): on, the game's camera from the pole through the fall, `view in
  play: first person` with Jak at the ground's height; off, his eyes as he flips off, mid-air.
- `spark0` (`jak1_eco_fall`, gkcfg-sparksoff; item 2): `[vr] eco sparks off Jak's body: left out (in his eyes ...)`, then
  `shown again` in third person.
- `pad1`, `pad0` (`jak1_gamepad_only`, gkcfg-padonly and gkcfg; item 9): on, `[vr-pad] Gamepad only: ...`, the
  controllers' presses leave the title as it is and the head still turns the view; off, the same presses start the game.
- `crossdef3`, `x016a`, `x016b` (`jak3_gun`, `JAKVR_TEST_GUN=1`, gkcfg, gkcfg-x016a and gkcfg-x016b; item 5): fresh
  options, `crosshair-mark 0`, nothing drawn; a 0.1.6 file's `crosshair-shape 1`, Off; its `crosshair-shape 3`, kept and
  drawn (`[sim] crosshair: kind 3 at 10.02 m`).
- `pmp1`, `pmp0` (`jak2_pause_mp`, gkcfg and gkcfg-mpoff; item 1): the pause ring's Game Options, its second row
  Multiplayer: `[vr-mp] pause Multiplayer: "Host Game, Join Game"`; B: Game Options with Multiplayer lit (build 97); with
  the mod off, no such row.
- `shield3` (`jak3_light_shield`, `JAKVR_TEST_CONTINUE=templea-start JAKVR_TEST_GUN=all JAKVR_STATE_TRACE=1`; item 8):
  the shield up while B is held and down as it is let go (`[vr] Light Jak's shield down: "Circle let go"`), the same by
  the right stick held right.
- `pturn3`, `pstick3` (`jak2_pause_turned`, `jak2_pause_stickturn`; item 10): `[vr-view] paused: the menu on the HUD,
  following the head (1.60 m ahead ...)`, with the head turned 150 degrees and after a stick turn.
- The launchers' options files: `VrOptionsTest` (below, 30 checks).
- `mp1`, `mp2` (C:\JakQuest\sim\mp017.sh, build 97; items 1 and 3; Port mapping off, nothing sent to the router):
  Jak 1's party as in 0.1.6 (host and guest on this PC, the guest playing the host's progress): both joined, the
  progress sent, the VR bodies shown, no error, no `[crash]`, no `max number of riders`. Jak II's (C:\JakQuest\sim\mp2.sh,
  LAN host, the guest by address, its script C:\JakQuest\sim\mp2_guest_pmp.sim): connected, and the guest's pause
  Multiplayer in the session, `[vr-mp] pause Multiplayer: in a session (Players, Leave the game)`, those two rows shown.
- `null17` (C:\JakQuest\sim\pcvr_null_jak.ps1 `-Game jak2 -Name null17 -Out C:\JakQuest\sim\s07`; item 10): Jak II on
  SteamVR's OpenXR with its null driver for 90 s (`system: SteamVR/OpenXR : null`, the session begun, 90 frames/s),
  steamvr.vrsettings put back after it. Its head and eyes are together (upside down, as that driver's view is), so no
  `[openxr] the runtime's head space is N degrees from its eyes` line: the guard stays silent where they agree.

C:\JakQuest\sim\batch30.sh (build 98: Jak II's GOAL, the pause menu's ring in front of the headset's view, item 10):

- `ring1` (`jak2_pause_ring`, gkcfg): paused facing ahead (`pr-ahead`) and with the head turned 150 degrees
  (`pr-turned-pause`): the ring (skel-hud-ring, the menu's icons round it, the lit one green) centred ahead of the eyes
  both times, the HUD's words at its middle; gone once the menu closes (`pr-closed1`, `pr-closed2`). Before (build 97,
  `pturn3`'s `pt-pause`): the ring was in front of the game's own camera, seen only with the head turned toward it.
- `pstick4` (`jak2_pause_stickturn`): after a stick turn the ring ahead; the head turned 60 degrees while paused, the
  ring still centred.
- `pmp2` (`jak2_pause_mp`): `pmp1` again on build 98: the ring ahead, Game Options' Multiplayer, B back with
  Multiplayer lit.
- `rider1` (`jak1_city_scene`, as batch28a.sh's `elev2`: `JAKVR_TEST_CONTINUE=village2-dock
  JAKVR_TEST_WARP=sunken-elevator-2 JAKVR_TEST_WARP_OFFSET=-47.21,-3.77,8.24 JAKVR_TEST_WARP_APPROACH=1
  QUEST_SIM_ALIGN=10000`; item 3, Jak 1's GOAL at build 97): Jak alone rides the elevator down, 5.2 m to -142.3 m,
  `GAMEPLAY: enter sunken`, then walks off; no error and no `max number of riders`.
- `title1` (C:\JakQuest\sim\jak2_title_ring.sim, build 98; item 10): Jak II's title menu (its words on the HUD, the
  ring opened in the eyes) ahead, and with the head turned 150 degrees still ahead; it stops before Multiplayer.
- The Quest's runtime for ARM64 after the last gk change (C:\JakQuest\sim\android_native_check.sh, log
  android_native6.log): 42 files compiled, libmain.so linked, no error.

## The checks added in 0.1.8

Runs in C:\JakQuest\sim\s08 (scratch), builds 100 to 120 (LIST-0.1.8), from C:\JakQuest\sim\batch31a.sh to
batch55.sh. The new sims: `jak1_wheel`, `jak2_wheel`, `jak3_wheel`, `jak3_ride_wheel`, `jak2_ride_gun`,
`jak3_ride_gun`, `jak2_pause_soak`, `jak1_shadow_eyes`, `jak2_shadow_eyes`, `jak3_shadow_eyes`, `jak1_root_jumps`,
`jak1_shadow_walk`, `jak2_shadow_walk`, `jak3_shadow_walk`, `jak1_credits`, `jak1_view_hold`, `jak2_pole`,
`jak1_double_jump`, `jak2_double_jump`, `jak3_double_jump`, `jak1_cell_sound`, `jak2_title_sound` (each header says
how it is run).

- Steer by grabbing the wheel (item 6): `wh2b` (`jak2_wheel`, `JAKVR_TEST_CONTINUE=ctysluma-start
  JAKVR_TEST_WARP=bikea JAKVR_TEST_RIDE=1 QUEST_SIM_ALIGN=10000`), the city bike's handlebars: 27 degrees gives 0.73,
  full lock about 0.98, back to 0 at once; `wh2c` (`JAKVR_TEST_WARP=cara`), the city car's wheel: 27 degrees gives
  0.43; `wh1` (`jak1_wheel`, `firecanyon-start`), the Zoomer: 27 degrees gives 0.65; `wh3b` (`jak3_wheel`,
  `wasdoors-desert-turtle`), Jak 3's buggy: a quick squeeze gives R2, squeezed again and held R2 held, the right
  stick's click with that hand on the wheel R2, 29 degrees 0.43, grips before he is seated ignored.
- Hold the gun while driving (item 5): `rg2` to `rg2d` (`jak2_ride_gun`, as `wh2b` with `JAKVR_TEST_GUN=1`): the gun
  drawn at the shoulder, fired and put away seated, Jak's (not Daxter's, build 103), carried by the right hand with
  his on-foot hold (build 105); `gun2m` (`jak2_gun`) and `gun3m` (`jak3_gun`) on build 106 logged that hold, the same
  in both games to 1 mm (`[vr-body] the gun's hold on foot kept (0.29 m from his hand, steady) ...`), which
  `ride_gun_hold` has from build 107. `rg3b` (`jak3_ride_gun`, `ctyport-hijack-decoy`): the hijacked pickup's continue
  left Jak standing at the port (a headset check, HEADSET_CHECKLIST), so Jak 3's runs use the glider (`hanga-start`).
- Jak 3's test continue (builds 104 and 106): `[vr] test: seated by the continue, the set-up done` once a continue
  seats him (QUEST_SIM_ALIGN's mark), and a pause menu a prelude left open in play is closed first (`[vr] test: the
  pause menu closed, the continue waits for play`; `wh3c` and `rg3c` had stayed paused behind it).
- The shadows in each eye (item 9): `sh1` (`jak1_shadow_eyes`, `village1-hut`, build 106): his shadow the same in both
  eyes ahead, looking down, the head lowered and raised, turned aside and behind. `sh1b` (the same with
  `JAKVR_SHADOW_EYES=1`, build 107, the head then turning, nodding and Jak walking): every 600 frames left 3.80% and
  right 3.79% of the picture, then 5.22 and 5.22, 5.60 and 5.67, 4.98 and 5.04; no frame where the eyes differ.
- A tester's Jak 1 log (item 10): `rjc` (`jak1_root_jumps`, `JAKVR_TEST_CONTINUE=citadel-start JAKVR_BODY_TRACE=1`,
  build 106): five `the game's skeleton left his root` jumps of 3.6 to 8.7 m, each along the line to the world's
  origin within a degree. `rjc2` (build 107, the same): each jump 0.9983 to 0.9998 of the way from the origin, under
  0.01 m aside of the line, his root moving 0.00 to 0.24 m and his main joint the whole jump; some of the bones the
  game sends stay at the origin itself, so build 107's test (all his bones within 3 m of his main joint) never held.
  `rjc3` (`JAKVR_BODY_TRACE=2`, build 107): at each jump the spine as the game sent it (each joint's distance from the
  one before: 0.86, 0.17, 0.26, 0.09, 0.08 m at frame 6535) the same as the frames round it, so his whole body moved
  with his main joint; build 108 asks instead that his neck still sit where it sat on it. `rj3` (`village3-start`)
  stayed at the title (the prelude's Start came early). `rjc4` (citadel) and `rj3b` (`village3-start`), build 109:
  four jumps of 0.84 to 9.42 m and one of 3.07 m, each
  `taken back out along that line`, his rise at about the same share of his height; no `left his root` hold after
  them (the one at frame 3405 is the continue's own move across the world).
- Hold the gun while driving, seated pictures (item 5, build 109): `rg2e` (Jak II's city bike) and `rg3c` (Jak 3's
  glider, `hanga-start`): the gun in the right hand ("in hand 1"), the other hand on the handlebars or the bar,
  fired and put away at the shoulder. Steering (item 6): `wh3c`, `rw3g3` (glider 0.64 at 27 degrees), `rw3s3` (sled
  0.39), `rw3f3` (war factory's hellcat 0.38), each grab on "the game's handles seen".
- The shadows in each eye, Jak II and 3 (item 9, build 109, `JAKVR_SHADOW_EYES=1`): `sh2` apart in 38 of 600 frames
  (up to 20.6% against 8.1% of the picture), `sh3` in 32 and 10 of 600; the near plane cutting a shadow's volume in
  one eye. Build 110 counts the eye passes from behind (depth fail); `batch40` (sh2z, sh3z with
  `JAKVR_SHADOW_ZPASS=1` for the old count, and the walks `jak1_shadow_walk`, `jak2_shadow_walk`, `jak3_shadow_walk`
  in several levels each on the new) checks it: the eyes apart in 9 frames of about 58,000 with a shadow on the new
  count against 2,393 on the old (sh2z 0 against 39, sh3z 0 against 32 and 8; s1w-hut 1, his hands near one eye, the
  other Jak 1 walks 0; s2w-city 0 against 642, s2w-ruins 0 against 782, s2w-fort 8 against 885); `batch40b` (the Jak 3
  walks again, the first cut off) s3w-desert 2 against 234, s3w-spargus 0 against 360, s3w-haven 2 against 462.
  `s2wn` (build 117, s2w-fort with pictures at frames 7380 to 7410): apart in 6 elsewhere (frame 6384), none there;
  the display frame `[vr] shadow in each eye` names is not the script's (about 295 behind it, the frames before play
  not counted), so build 118 has the test aid picture the frames itself. `batch51` (build 118, into s08/b51):
  s2w-fort 18 frames apart in one block, pictured at 6888 and 7387 to 7391 (up to 38.7% against 25.6%), each Jak's
  shoulder pads a few centimetres from the eyes hiding a different part of his shadow from each eye, the shadow the
  same in both; s3w-haven 0 against 351; s3w-desert 1 frame after its last full block (16243, 4.9% against 7.0%; the
  old count 123 in the blocks before), pictured: his arms a hand's length from the eyes, the shadow by his hand alike.
- Jak 1 knocked back (item 12): `rjs` and `rjs2` (`snow_drift`, `snow-start`, build 109): after each knockback by the
  lurkers his skeleton 1.7 to 2.3 m from his root for about two seconds, his arms out of the view in rjs2's pictures;
  `rjs3` (build 111): `[vr-body] the game's skeleton N m from his root on the ground (frame F): kept by it`, and each
  drop of his health logged (`[vr] Jak's health 3.0 to 2.0; the last hit by ...`, item 11).
- Jak 1's end credits (item 11): `fbc` (`jak1_credits`, `JAKVR_TEST_CONTINUE=finalboss-fight JAKVR_TEST_CREDITS=1`,
  build 110): the panel's alpha shows the game's black behind the credits in only 4 of 23 pictures (70 to 85% of the
  panel opaque), the rest the mist alone or the text alone (80 to 89% see-through), the eyes drawing the boss's arena
  and Jak's hands behind. `fbc2` (build 113): `[vr-view] the end credits begin: the eyes kept black behind them`, both
  eyes black (mean 0.0) in every picture, the panel's text and mist as before.
- The view held in a fight (item 13): `vh` (`jak1_view_hold`, `village1-hut`, `menu_unbound 1`, build 112): the
  left stick's click held 2 s while running, `no view switch`, no Start; held at rest, third person, then first.
- Off a swing pole (item 14): `p2` (`jak2_pole`, `ruins-start JAKVR_TEST_WARP=swingpole QUEST_SIM_ALIGN=10000`, builds
  112 and 113): the pole, its flip up, his double jump and the pole again in third person, no first person between;
  A late in the swing drops him off it, the view third person through the fall until he stands, then first person
  once.
- The jump at the headset's rate (item 15): `dj72`/`dj60` (`jak1_double_jump`, `training-start`, gkcfg and gkcfg-60,
  build 113): his highest jump 3.20 m at 72 against 3.47 at sixty, a double jump at the top 5.37 against 5.92;
  `dj72b`/`dj60b` (build 114, a frame-by-frame trace since taken out): the A-hold window's last frame at 28 ticks of
  30 at 72, and the rest from the jump animation's offset (collide-shape.gc's fixed 60); `dj72c` (build 115, the
  first fix) 3.38; `dj72f`/`dj60f` (build 116, both) 3.48/3.50 and 5.87 against 3.47/3.47 and 5.91; `jdj72`/`jdj60`
  (`jak2_double_jump`, `ruins-start`) 3.50 and 5.82 against 3.48 and 5.84; `dj72g` (build 117, the trace gone, the
  build to ship): 3.48 m, 3.48, a double jump at the top 5.87, as dj72f. `j3dj72`/`j3dj60` (`jak3_double_jump`,
  `wascitya-start`, Jak 3 GOAL 119): 3.50, 3.50, 5.82 and 5.43 against 3.48, 3.48, 5.84 and 5.33.
- The regressions items 1 to 6 reach (`batch31b`, builds 113 to 118): `ps2` (`jak2_pause_soak`, three minutes paused
  in the pause menu: one `[vr-mem]` line, item 1), `pmp3` (`jak2_pause_mp`: the Multiplayer page's lines), `boat2`
  (`jak1_intro_boat`: no "streamed animation" line; this copy's Jak 1 has no stream for the opening scene, `[FAKEISO]
  failed to find SAISB STR`, so the game's own guard ends it after 4 s as ever), `hol3` (`jak2_holster`), `race3`
  (`jak2_race_ride`), `board3` (`jak2_board`), `zoom3` (`jak1_zoomer_misty`), `buggy4` (`jak3_buggy`): all exit 0,
  no errors, stalls or crashes. Every Jak II run's log had `[vr] the streamed animation title-disk-intro held at
  37717 for 4 s`: item 2's guard ending the title's scene 4 s after Start, which the game holds there on purpose (its
  skip frame pins the stream position); build 119 leaves a skip frame alone, and `pmp4` (`jak2_pause_mp`, build 119)
  has no such line, one `[vr-mem]` line and the Multiplayer page.
- Multiplayer (`mp2`: two Jak II games, host and guest, port mapping off): both exit 0, no errors, the guest found
  and joined on this PC (`UDP works both ways`), the pause menu's Multiplayer page in a session; no line about the
  router.
- SteamVR's null driver (`null18`, C:\JakQuest\sim\pcvr_null_jak.ps1, Jak II, item 4): each session state named as it
  came (`idle`, `ready`, `session begun`, `synchronized`, `visible`, `focused`, within a second); the game still
  running at the end, steamvr.vrsettings put back as it was. `null19` (build 120, the same with
  `JAKVR_TEST_UNSHOWN=20,25`): `[openxr] the headset has not shown the game, or not given it the controllers, for 10
  s: session state focused ...; frames paced by the runtime: yes (waited 3.02 ms a frame over 582 frames); ... the
  head tracked: yes; the controllers: ...`, again at 20 s, `[vr-view] the window's title says: the headset is not
  showing the game (not drawn for 10 s): restart SteamVR if it stays so`, then `the headset shows the game and gives
  it the controllers again, after 25 s` and `the window's title is the game's again`.
- The Quest's runtime compiled for ARM64 (C:\JakQuest\sim\android_native_check.sh, the NDK 27.2): libmain.so
  linked, 188 of 188 steps (`arm64`, with build 119's sound player); `arm64b` after build 120's two files
  (xr_openxr.cpp, opengl.cpp): 5 of 5, linked.
- The sound device taken away (items 2 and 3, build 119, `batch54`): `al2` (`jak2_title_sound`: the title's scene
  playing, the device away 15 s from frame 1200): `[audio] the sound device has not asked for sound for 1 s ...` a
  second in, the device opened again every 3 s (a new audio context the third time), `playing again after 14.0 s (4
  time(s) opened again)`, no "streamed animation" line, and the scene went on meanwhile (the "presents" sign at frame
  1260, the city at 1500). `al2o` (the same with `JAKVR_TEST_AUDIO_LOSS=old`, the sound stopping with the device as
  before 0.1.8): `[vr] the streamed animation title-disk-intro held at 10076 for 4 s (its sound stream stopped): it
  ends here`. Jak 1's power cell (`jak1_cell_sound`, `training-start JAKVR_TEST_WARP=fuel-cell QUEST_SIM_ALIGN=10000`;
  the cell first waits about 10 s on another camera, the game's own `fuel-cell stall` lines, the same in every run):
  `al1`/`al1o` (build 119, the device away from frame 10120, before the victory animation): al1's animation played
  through without the device (6.9 s), al1o's never started its stream and the game's own guard ended it after 4 s,
  no `[vr]` line; `al1b`/`al1ob` (build 120, from frame 10900, 4 and 5 s into the animation): al1b's played to its
  end (7.8 s), Jak in play again; al1ob `[vr] the streamed animation fuel-cell-victory-7 held at 4544 for 4 s (its
  sound stream stopped): it ends here`, Jak in play again (the tester's case: before 0.1.8 he stayed held).

## The checks added in 0.1.9

Runs in C:\JakQuest\sim\s09 to s18 (scratch), builds 121 to 137 (LIST-0.1.9), from C:\JakQuest\sim\batch60.sh to
batch77.sh. The new sims: `jak1_fishing`, `jak1_fishing_eyes`, `jak2_gun_hand`, `jak2_show_map`, `jak2_tomb_boss`,
`jak2_turret_hand`, `jak2_vehicle_hands`, `jak2_wheel_lean`, `jak3_darkship_door`, `jak3_errol_floor`,
`jak3_factory_daxter`, `jak3_gun_hand`, `jak3_orb_hunt`, `jak3_power_game`, `jak3_show_map`, `jak3_turret_hand`,
`jak3_vehicle_gun`, `jak3_vehicle_gun_hand`, `jak3_vehicle_hands`, `jak3_wheel_lean` (each header says how it is run).
Two options folders of the sims: `gkcfg-gponly` (Gamepad only on, no gamepad connected) and `gkcfg-fishtp` (Fishing
game in first person off).

- Jak II's tomb boss (item 1): `tb-on`, `tb-off` (`jak2_tomb_boss`, `JAKVR_TEST_TASK=51`, the multiplayer mod on and
  off) on the 0.1.8 build, the widow's scene never starting, as for the tester; `tb2`, `tb3` (builds 122, 123) and
  `tb4` (build 131): `[vr] the tomb boss's scene starts: Jak 53.6 m from her`, then the fight.
- The city's sections (item 2): `pf2p`, `pf2s` (`jak2_shadow_walk`, `gkcfg-perf`, `JAKVR_TEST_CONTINUE=ctyport-start`
  and `strip-start`): the `[vr-level]` watch names each level and what the game wants; none left out.
- Every gun from the hand (items 3 and 4): `gh2b` (`jak2_gun_hand`, `JAKVR_TEST_GUN=1`) and `gh3b` (`jak3_gun_hand`,
  `JAKVR_TEST_GUN=all`), build 123: every shot `starts 0.00 m from the gun's muzzle` and goes 0.0 to 0.1 degrees off
  the hand's aim, Jak 3's Needle Lazer picking its own targets as on a pad; `gh2c` (build 130), the Scatter Gun's blast
  turned by the hand.
- The guns on Jak 3's vehicles (item 5): `vgsc`, `vgbt` (`jak3_vehicle_gun`, `JAKVR_TEST_TASK=54` and `65`), the view
  from the game's camera at each gun, `[vr-gun] the Scorpion's gun aims where the right hand points`; `vgbt` on build
  131: `[vr-shadow] ... grown to 98304 points`, the mission played on.
- The hands on the handlebars and the wheel (items 6 and 7): `vh2b`, `vh2c` (`jak2_vehicle_hands`,
  `JAKVR_TEST_WARP=bikea`, `cara`, build 130): `[vr-body] the left hand onto the ride's handle: ...`, both hands closed
  on the bars' ends looking down. `vh3g` (`jak3_vehicle_hands`, `JAKVR_TEST_CONTINUE=ctygenb-start
  JAKVR_TEST_WARP=h-car-base`, build 134): the car parked by the traffic, both hands on the wheel's grips. `vh3b`
  (`JAKVR_TEST_WARP=h-bike-base`, build 136): the bike parked 4 m ahead of Jak (no parking spot within 150 m there),
  Jak on it, both grips on the handlebars, `the left hand onto the ride's handle: -0.38 -0.29 -0.22 m from the head`
  and the right `0.39 -0.31 -0.22`, both hands closed on the bars' ends looking down.
- The HUD's squares (item 9): `hud1`, `hud2`, `hud3` (each game's `_shadow_walk`, `QUEST_SIM_HEADSET=1
  JAKVR_TEST_HUD=1`): no squares in the panel pictures' alpha.
- The maps (item 11): `map2`, `map3` (`jak2_show_map`, `jak3_show_map`, build 131): one map, on the panel, `[vr-view]
  the pause menu's ring at the HUD's depth: 1.60 m`.
- Daxter's maze game (item 12): `pg` (`jak3_power_game`, `JAKVR_TEST_TASK=51`, build 130): `[vr-view] the view tipped 63
  degrees down with the game's camera, so what it shows is ahead`, the board ahead.
- The robot factory's Daxter section (item 13): `fd`, `fdz` (`jak3_factory_daxter`, the second with
  `JAKVR_SHADOW_ZPASS=1`, build 128b): fd, as players have it, the eyes' shadow shares within 0.02 points of each
  other and 0.18 of the flat game's count; fdz, the flat game's count in the headset, up to 0.26 apart (what 0.1.8
  fixed); the zappers' glow, the lights and the particles in both eyes alike.
- B for height and the lean (items 14 and 15): `wl2` (`jak2_wheel_lean`), `wl3` (`jak3_wheel_lean`): `B with the right
  hand on a hover vehicle's wheel: R2 (the height)`, `the wheel pushed 12 cm: the left stick forward (lean 1)`,
  `pulled 12 cm: ... back (lean 2)`, the turn still steering with no lean.
- Errol's floor and the port's mines (item 16): `ef` (`jak3_errol_floor`, `JAKVR_TEST_TASK=60`, build 122): no
  `!alloc`, the floor drawn ahead, down and behind; `pa`, `pa2` (`JAKVR_TEST_TASK=38`): none from build 123.
- Jak II's slow frames (item 18): `pf2s`, `pf2p`: Daxter's lines 4 in a whole run, `[vr-perf] the drawing took 8.8 to
  12.6 ms a frame` on the sim's PC; `pf1`, `pf3` (Jak 1 and 3): three such windows in all, at most 8.7 ms.
- The orb hunts' scenes (item 19): `orb` (`jak3_vehicle_gun`, `JAKVR_TEST_TASK=73`, build 128b), `orb2`
  (`jak3_orb_hunt`): `[vr-view] the game's camera shows something away from Jak while it holds him (186.5 m from him,
  146 degrees off its ahead): the view from the game's camera`, then back.
- The fishing game (item 20): `fe` (`jak1_fishing_eyes`, `JAKVR_TEST_CONTINUE=jungle-start JAKVR_TEST_WARP=fisher
  JAKVR_TEST_RIDE=1`, build 134): from Jak's eyes, the basket 6.09 m from the hand and 0.05 m off the catch line
  pointed at it, the far left end reached, `[vr-feel] a fish in the net`, `the net taken in the left hand (its grip
  squeezed)` and back, no fists or kicks; the pictures: the thin pole and its basket over the river in either hand.
  `fi` (`jak1_fishing`, `gkcfg-fishtp`): the game's camera, the net following the raised hand, the stick's hold, the
  left hand raised taking it over. `fish` (s09, s12, `jak1_fishing`): `[vr] test: the fishing game started`, `state
  target-fishing`.
- What hangs on Jak's back (item 21): `board2` (`jak2_board`, `JAKVR_TEST_BOARD=1`), `light3` (`jak3_lightjak`):
  `[vr-body] in first person the eyes leave out board-lod0 (the JET-Board on his back)`, `... jakb-wings-wings-lod0
  (Light Jak's wings)`; ridden, the board is drawn.
- The turrets (item 23): `tt2` (`jak2_turret_hand`, `JAKVR_TEST_TASK=37 JAKVR_TEST_WARP=turret`), `th3`
  (`jak3_turret_hand`, `JAKVR_TEST_TURRETS=1`) and `vgsc`: `[vr-gun] a gun turret: it turns to where the right hand
  points while its first finger is on the trigger`.
- The Dark Maker robot and the ship's door (items 24 and 27): `ds` (`jak3_darkship_door`, `JAKVR_TEST_TASK=64
  JAKVR_TEST_WARP=precur-door-b`, build 128b): `[vr-cam] mode 0 flags 0x2251 third person view` in the robot; the
  door's ten troopers each `waits to be born` until `Displaying level precurb`, then `born now`, the four fighting.
- Gamepad only with no gamepad (item 25): `gp` (`jak1_title_a`, `gkcfg-gponly`), `gp2` (`jak1_stick_start`, build
  135): `[vr-pad] gamepads connected: 0`, `Gamepad only: no gamepad connected now, so the VR controllers play`, the
  title started by the left stick's click, the VR menu opened and closed over the title's menu, its rows on the HUD.
- Swimming (item 28): `pf2p` on builds 133 to 137: `[vr-view] swimming: the eyes kept at least 12 cm over the water
  ...`, still black looking down (s18: 64% and 59% of two pictures lit). `pf2p` on build 138 (a trace, taken out
  since): the game draws the near tiles there already; the eyes went under the waves. `pf2p` and `pf3p`
  (`jak3_shadow_walk`, `gkcfg-perf`, `JAKVR_TEST_CONTINUE=ctyport-start`, the same dock in Jak 3) on build 139:
  `[vr-view] swimming: a wave round the eyes stands 43.9 cm over his water's height: the eyes kept over it` (Jak 3:
  8.5 cm), every swimming picture 95 to 100% lit (`tools/png_levels.ps1`).
- Multiplayer (C:\JakQuest\sim\mp019.sh, build 135; port mapping off, nothing sent to the router): Jak 1's party
  (`mp1` host and guest on this PC, the guest playing the host's progress) and Jak II's (`mp2`, host and guest by the
  network's list): both exit 0, no errors, no `[crash]`, no `max number of riders`, the VR bodies shown.
- The Quest's runtime compiled for ARM64 (C:\JakQuest\sim\android_native_check.sh, the NDK 27.2), after build 135:
  linked, exit 0; the release's APK (C:\JakQuest\sim\build_apk.sh, after build 139) built it again: libmain.so has
  0.1.9's lines (`the HUD's layer first sent`, `the fishing game from Jak's eyes`), no warning from game/vr.
- The launchers' options files: `VrOptionsTest` (below, 30 checks), the new option on to start.

## Without a game

- The party server: `python -I tests/party_server_test.py tools/party_server.py 8139` (24 checks).
- Texture packs (launcher/common TexturePacks.java): compile the launchers' sources, then
  `javac -cp <classes> -d <out> tests/java/dev/jakquest/vr/TexturePacksTest.java` and
  `java -cp <classes>;<out> dev.jakquest.vr.TexturePacksTest <an empty scratch folder>` (37 checks, 14 of them imports).
- Browse mods' lists (launcher/common ModList.java) the same way: `tests/java/dev/jakquest/vr/ModListTest.java`,
  `java -cp <classes>;<out> dev.jakquest.vr.ModListTest <an empty scratch folder>` (20 checks, no network).
- The PC launcher's "build it again" after an update (pc/launcher PcSetup.newSources, 0.1.2) the same way:
  `tests/java/dev/jakquest/vr/GameCodeTest.java`, `java -cp <classes>;<out> dev.jakquest.vr.GameCodeTest <an empty scratch
  folder>` (19 checks: the game code of 0.1.0, 0.1.1 and later marks against a release's game\data\game-code.txt).
- The PC launcher's "Where your files are kept" (pc/launcher PcDataFolder; the games since 0.1.3, the texture packs,
  the saves and options, and the logs since 0.1.8) the same way: `tests/java/dev/jakquest/vr/DataFolderTest.java`,
  `java -cp <classes>;<out> dev.jakquest.vr.DataFolderTest <an empty scratch folder> [<an empty scratch folder on
  another drive>]` (50 checks with the second folder, 45 without: what is refused, a rename on one drive, a copy to
  another drive and back, a stop, the old copy's removal, a missing folder; since 0.1.8 each place's default and
  folder, the packs refused in the games' folder and the saves in the logs', the saves moved and read again and back,
  the downloads following the packs, a missing packs folder waited on, the logs' missing drive falling back to the
  default place, a saves leftover finished and one naming home dropped, the Logs zip's summary line (the places kept
  elsewhere, no path), the saves copied to another drive and back).
  Run on 2026-11-19 with C:\JakQuest\sim\dft1 and a scratch folder on D:: all 50 ok; VrOptionsTest 25 ok. The launcher's own
  pictures (`PcLauncher --render <tab> <png> 1180 2600`, JAKQUESTVR_HOME, _INSTALL and _LOGS in a scratch folder, the
  packs' and then the saves' chosen folder missing): the Play tab's missing-packs card, set-ups waiting and Where your
  files are kept with a row each; Texture packs' imports off with the note; VR Body waiting on the saves; the Logs
  tab's Where things are listing every folder.
- The launchers' VR options files (launcher/common VrOptions.java, 0.1.7) the same way:
  `tests/java/dev/jakquest/vr/VrOptionsTest.java`, `java -cp <classes>;<out> dev.jakquest.vr.VrOptionsTest <an empty
  scratch folder>` (30 checks: a fresh file's starts, the crosshair Off under `crosshair-mark` and the new toggles,
  0.1.9's two wheel options on and in VR Controls, B for height for the guns only, Fishing game in first person on; each
  older crosshair key as the game's vr_options.cpp reads it, 0.1.6's Dot Off and any other shape kept; written back
  with no older key). C:\JakQuest\sim\javac_check.sh compiles the launchers' sources for it.
