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

- Buttons, 1 or 0: `a b x y menu left_click right_click left_first_touch right_first_touch`
- Sticks and triggers: `left_x left_y right_x right_y left_trigger right_trigger left_grip right_grip`
- Head (degrees, metres): `head_yaw head_pitch head_x head_y head_z` (standing eyes at 1.7 m)
- Hands, from where a player holds them (metres): `lh_x lh_y lh_z rh_x rh_y rh_z`; their turn `hand_yaw hand_pitch
  hand_roll`; `left_gone right_gone` 1 for a controller not tracked
- A look from outside: `view_free 1` with `view_x view_y view_z view_yaw view_pitch` (the eyes alone moved)

## Test aids (PC only, from the environment)

| Name | What it does |
| --- | --- |
| `JAKVR_TEST_CONTINUE=<continue point>` | Once Jak has been in play on foot for 2 seconds, the game starts again at that continue point (each game's level-info.gc, e.g. Jak 1 `firecanyon-start`, Jak II `ctysluma-start`, Jak 3 `wasdoors-desert-turtle`). |
| `JAKVR_TEST_GUN=1` | Jak II and 3: Jak has the gun from the start. |
| `JAKVR_TEST_BOARD=1` | Jak II and 3: Jak has the JET-Board from the start. |
| `JAKVR_STATE_TRACE=1` | Jak's state in the log as it changes (`[vr] state target-hit`). |
| `JAKVR_PARTY_TEST=host` or `=join` | Multiplayer: host a game as soon as the game is up, or look for games and join the first of this build found. |
| `JAKVR_PARTY_TEST_SAVE=copy` | With `=join`: once the host's Jak is in the game, play the host's progress (Play which save?). |
| `JAKVR_BODY_TRACE=1` | How the posed body sits, twice a second; the merc models the eyes draw, once each; each frame Jak's bones came from the game, or were posed, with their last column off 0 0 0 1 (made affine). |
| `JAKVR_BODY_TRACE=2` | As 1, and each frame by its number: the eyes, root, view's ease and lean, the neck and its base posed and as the game had them; as drawn, the head's shrink point in the left eye's camera; and as the frame is sent to be drawn, how far the neck base's bone and the camera moved after they were posed and placed. Jak 1 also: the head point as his skinning matrices are made (bones.gc), with the camera and neck base then. |
| `JAKVR_GLOW_TRACE=1` | Jak II and 3: each glow faded because its flare comes within 2 m of the eye, or drawn within 4 m of it (its depth, how much nearer the flare is drawn, its size on screen and its most, the light left), in the left eye. |
| `JAKVR_TRACE_JOINTS=14,64,...` | Those skeleton nodes' places from the eyes too, with the body trace. |
| `JAKVR_HIDE_TEST=<name>` | Every model whose name starts so left out of the eyes (which model is that?). |
| `JAKVR_HIDE_JOINTS=14,64,...` | Those nodes shrunk out of the eyes (which joint is in the way?). |
| `JAKVR_BUCKET_LOG=1` | In the left eye: each draw bucket starting and stopping, and the share of the picture the shadow pass darkens. |
| `JAKVR_REPROJECT=0` | What the game had already put on the screen for its own camera is drawn as it was, not taken to each eye's view (to compare). |
| `JAKVR_CRASH_DUMP=<folder>` | The first access violation writes a dump there. |
| `QUEST_SIM_ALIGN=F` | Script frames from F on count from the frame the test's set-up was done. |
| `QUEST_SIM_GRIP_TILT=D` | The grip pose turned D degrees up from the aim pose, as on the Quest's controllers. |
| `QUEST_SIM_SPACE_WARP=0` | No motion pictures (Smoother frames off). |

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
- Multiplayer, two games at once: C:\JakQuest\sim\mp_host_pose.sim and mp_guest_pose.sim with
  `JAKVR_PARTY_TEST=host` and `=join` and their own settings folders; the host's progress for the guest:
  C:\JakQuest\sim\batch11.sh (mp_host.sim and mp_guest.sim, the guest with `JAKVR_PARTY_TEST_SAVE=copy`; its log
  shows "the host's progress came" and "save 4 done").
- Multiplayer's VR menu pages: `jak1_mp_menu` (the stick down to Host a game, then each page and B back).
- Multiplayer over the party server, with Remove: C:\JakQuest\sim\batch13.sh (tools/party_server.py on port 8139,
  `server 127.0.0.1:8139` in each settings folder's party.txt; the host's mp_host_remove.sim opens the VR menu's
  Players page and removes the guest).

## Without a game

- The party server: `python -I tests/party_server_test.py tools/party_server.py 8139` (24 checks).
- Texture packs (launcher/common TexturePacks.java): compile the launchers' sources, then
  `javac -cp <classes> -d <out> tests/java/dev/jakquest/vr/TexturePacksTest.java` and
  `java -cp <classes>;<out> dev.jakquest.vr.TexturePacksTest <an empty scratch folder>` (23 checks).
