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
| `JAKVR_TEST_WARP_OFFSET=dx,dy,dz` | Jak 1, with `JAKVR_TEST_WARP`: that many metres from the actor instead of a metre over it (the pipe game's buttons, which are not entities: `sunken-pipegame-1` with `7.5,1.3,1.25` for the right one). |
| `JAKVR_TEST_WARP_APPROACH=1` | Jak 1, with `JAKVR_TEST_WARP_OFFSET`: first to the offset, then onto the actor once the levels there have loaded, as a player walking up to it (the elevator down to the underwater city asks for the city as it starts). |
| `JAKVR_TEST_KILL_SCENE=1` | Jak 1: a scene's camera (pov-camera) dies a second into its scene, to see Jak let go (`[vr] the scene ... ended before it let Jak go`). |
| `JAKVR_TEST_GUN=1` | Jak II and 3: Jak has the gun from the start (0.1.5: Jak II also Dark Jak, his meter full and his bombs). `JAKVR_TEST_GUN=all` (0.1.5, Jak 3): every gun, Light Jak's powers, and Dark Jak's and Light Jak's meters kept full while he is neither (build 76: a new game has Dark Jak already, and the first aid left his meter empty). |
| `JAKVR_SKY_TRACE=1` | 0.1.5, PC: every 4 seconds each draw of the sky bucket (Jak 3's `sky`, Jak II's `sky-draw` since build 76): its points' depth (after the scaling to the eyes' near plane), depth test and writes, alpha test, and the near plane (`[vr-sky] frame N draw: ...`). |
| `JAKVR_TEST_BOARD=1` | Jak II and 3: Jak has the JET-Board from the start. |
| `JAKVR_STATE_TRACE=1` | Jak's state in the log as it changes (`[vr] state target-hit`). Jak II and 3 with the crosshair on: what the gun's line hits as it changes (`[vr] crosshair: on "gun-dummy-a" (mask ...), enemy 1`; a type such as `instance-tie` for the level's pieces; `#f` for nothing within 40 m or the level's own ground). |
| `JAKVR_TEST_TURRETS=1` | Jak II: the sewer's first gun turret destroyed through the game's own instant death once it is made (its die state, LIST-0.1.1 item 22). Jak 3: 2 seconds into play, Spargus' wall turret (`wascity-turret-1`, with `JAKVR_TEST_CONTINUE=wascityb-gungame`) is sent the `'pickup` its Triangle prompt sends, so Jak gets in (`[vr] test: turret: Jak put in ...`). |
| `JAKVR_TEST_COMBO=1` | Jak II, with `JAKVR_TEST_CONTINUE=gungame-start`: the gun course's yellow gun lesson opened and its manager told to start (`[vr] test: combo: ...`), for the punch-then-shoot combo. |
| `JAKVR_RIDE_TRACE=1` | Riding in first person, every 30 frames: the most the view moved in his seat in a frame against his head's own move, and its turn against his (`[vr-cam] riding, the most in a frame: ...`). |
| `QUEST_SIM_HZ=N` | The refresh rate the simulated headset tells (72 to start), for Game frame rate: Automatic. |
| `QUEST_SIM_PERIOD_BLIPS=N` | 0.1.5: every Nth frame the simulated headset tells a doubled frame period, as SteamVR does for each frame it missed; Automatic keeps the game's rate (a change of the headset's rate is followed once it has held a second: `[vr-view] the headset at ...`). |
| `JAKVR_TEST_TASK=<number>`, `JAKVR_TEST_RESTARTS=N` | 0.1.5, Jak 3: once Jak has been in play for 2 seconds, that mission played from its start (the game's play-task and its continue), then restarted N times as the pause menu's Restart mission does, a minute after and then every 20 seconds (`[vr] test: task ...`, `restart mission N`; 17 is the leaper race). |
| `JAKVR_TEST_SOUND_STALL=S` | 0.1.5, Jak 1: the sound processor's player stops answering for S seconds at its 1200th batch of commands (about 20 seconds into a run; `[vr-test] the sound player held for S s`, then `goes on`), and the game, once the player has been busy for 30 frames, sends 300 sound commands at once (`[vr] test: 300 sound commands while the sound player is held`), so the sound queue fills as in the tester's freeze. |
| `JAKVR_TEST_GAME_STALL=S` | 0.1.5: the game's thread held S seconds at its 1500th frame (`[vr-test] the game held for S s`, then `goes on`), for the stall watch's `[vr-stall]` lines. PC only. |
| `JAKVR_TEST_BAD_TEXTURE=N` | 0.1.5: the Nth texture page the game uploads has its first entry read as 0 (`[vr-test] page N's first entry read as 0`), as the page in Jak 3's crash on a restart had: the texture guard passes it over. |
| `JAKVR_TEST_TETHER=1` | 0.1.6, Jak 1, with `JAKVR_TEST_WARP` onto one of Boggy Swamp's tether rocks: 3 seconds after the warp the rock takes a hit of Jak's yellow eco (`[vr] test: yellow eco to swamp-tetherrock-...`), so its scene plays and Jak is seen let go after it (pc-vr.gc vr-test-tether!). |
| `JAKVR_PARTY_TEST=host` or `=join` | Multiplayer: host a game as soon as the game is up, or look for games and join the first of this build found. |
| `JAKVR_PARTY_TEST_SAVE=copy` | With `=join`: once the host's Jak is in the game, play the host's progress (Play which save?). |
| `JAKVR_BODY_TRACE=1` | How the posed body sits, twice a second; the merc models the eyes draw, once each; each frame Jak's bones came from the game, or were posed, with their last column off 0 0 0 1 (made affine). |
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
| `JAKVR_REPROJECT=0` | What the game had already put on the screen for its own camera is drawn as it was, not taken to each eye's view (to compare). |
| `JAKVR_CRASH_DUMP=<folder>` | The first access violation writes a dump there: crash.txt (the registers, the stack's words, rip and the game memory's base) and ee.bin (the game's memory from 512 KB on). A rip or stack word minus the base, inside the game's memory, is GOAL code: its function is found from the symbol table in ee.bin (Jak II and 3: s7 is r14, a symbol's name pointer at it + 0xff37). |
| `QUEST_SIM_ALIGN=F` | Script frames from F on count from the frame the test's set-up was done. |
| `QUEST_SIM_GRIP_TILT=D` | The grip pose turned D degrees up from the aim pose, as on the Quest's controllers. |
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
  (hands against a wall).
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
- The PC launcher's "Where the games are kept" (pc/launcher PcDataFolder, 0.1.3) the same way:
  `tests/java/dev/jakquest/vr/DataFolderTest.java`, `java -cp <classes>;<out> dev.jakquest.vr.DataFolderTest <an empty
  scratch folder> [<an empty scratch folder on another drive>]` (32 checks with the second folder, 29 without: what is
  refused, a rename on one drive, a copy to another drive and back, a stop, the old copy's removal, a missing folder).
