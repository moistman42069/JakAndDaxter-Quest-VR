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
| `JAKVR_TEST_GUN=1` | Jak II and 3: Jak has the gun from the start. |
| `JAKVR_TEST_BOARD=1` | Jak II and 3: Jak has the JET-Board from the start. |
| `JAKVR_STATE_TRACE=1` | Jak's state in the log as it changes (`[vr] state target-hit`). Jak II and 3 with the crosshair on: what the gun's line hits as it changes (`[vr] crosshair: on "gun-dummy-a" (mask ...), enemy 1`; a type such as `instance-tie` for the level's pieces; `#f` for nothing within 40 m or the level's own ground). |
| `JAKVR_TEST_TURRETS=1` | Jak II: the sewer's first gun turret destroyed through the game's own instant death once it is made (its die state, LIST-0.1.1 item 22). Jak 3: 2 seconds into play, Spargus' wall turret (`wascity-turret-1`, with `JAKVR_TEST_CONTINUE=wascityb-gungame`) is sent the `'pickup` its Triangle prompt sends, so Jak gets in (`[vr] test: turret: Jak put in ...`). |
| `JAKVR_TEST_COMBO=1` | Jak II, with `JAKVR_TEST_CONTINUE=gungame-start`: the gun course's yellow gun lesson opened and its manager told to start (`[vr] test: combo: ...`), for the punch-then-shoot combo. |
| `JAKVR_RIDE_TRACE=1` | Riding in first person, every 30 frames: the most the view moved in his seat in a frame against his head's own move, and its turn against his (`[vr-cam] riding, the most in a frame: ...`). |
| `QUEST_SIM_HZ=N` | The refresh rate the simulated headset tells (72 to start), for Game frame rate: Automatic. |
| `JAKVR_PARTY_TEST=host` or `=join` | Multiplayer: host a game as soon as the game is up, or look for games and join the first of this build found. |
| `JAKVR_PARTY_TEST_SAVE=copy` | With `=join`: once the host's Jak is in the game, play the host's progress (Play which save?). |
| `JAKVR_BODY_TRACE=1` | How the posed body sits, twice a second; the merc models the eyes draw, once each; each frame Jak's bones came from the game, or were posed, with their last column off 0 0 0 1 (made affine). |
| `JAKVR_BODY_TRACE=2` | As 1, and each frame by its number: his body's big parts from the eyes (`frame N parts:`), his spine as the game sent it (each joint's size and how far from the one under it: a stretch of his body shows), the eyes, root, view's ease and lean, the neck and its base posed and as the game had them; as drawn, the head's shrink point in the left eye's camera; and as the frame is sent to be drawn, how far the neck base's bone and the camera moved after they were posed and placed. Jak 1 also: the head point as his skinning matrices are made (bones.gc), with the camera and neck base then. |
| `JAKVR_BODY_TRACE=3` | As 1, and each frame each hand's fingers (`[vr-body] the right fingers`: each finger's curl, how far the world moved it, how many surfaces the hand is at), to see them steady. |
| `JAKVR_CLOCK_TRACE=1` | Jak II and 3: the game's clocks every 60 of its frames (`clock:` lines), to see it keep time at half rate. |
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
