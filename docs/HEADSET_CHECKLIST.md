# Headset checklist

What the PC tests cannot reach, to try on the Quest (and the PC VR items on a real PC headset). Each line: what to do,
and what shows it worked. The launcher's **Settings**, **Log**, **Save log** saves one text file with everything (from
0.1.4: each game's options, last crash and newest logs, the preparation's and the app's own log), and **Report a
problem** one zip of the logs in Download/JakQuestVR on the headset (PC: the Logs tab's **Gather the logs**, in
Downloads\JakQuestVR). What the simulated headset already showed
is in [LIST-0.1.0.md](LIST-0.1.0.md) and not repeated here.

## First, every session

- **The controllers.** Walk with the left stick, turn with the right. The log's `[openxr] system:` and
  `[openxr] controller bindings suggested for` lines name the headset and its controllers.
- **The view.** A new game in each of the three games: in play you see through Jak's eyes, his head and Daxter out of
  the way; the log says `[vr] view in play: first person`. Hold the left menu button a second: third person
  (`[vr-pad] the view is third person now`), held again back.
- **Frames.** A `[vr-perf]` line every 10 seconds: frames a second near the refresh rate. Note them for each game.

## Installing (list item 8)

- Add each disc image in the launcher. The headset builds the game from it (decompile, then compile); note how long
  each game takes and what the launcher's storage line says before and after. Jak 3 needed 7.3 GB on a phone
  (Precursor's issue 18). If it stops, the **Log** tab's installation log says where.
- The game panel shows the disc's version (US, PAL, Japanese, Korean) once added.

## The three games on the headset's chip (list items 5 to 7)

- **Jak 1 pickups (ARM64):** Precursor's players reported pickups that did nothing on phones: the power cell on
  Sentinel Beach, green eco, some orbs. Collect each: the counter should go up and the pickup go away.
- **Jak II crashes during play** (Precursor's README, issue 12): play from the fortress into Haven City and on; note
  where it stops, with the logs.
- **Jak 3's white screen after its first scene** (Precursor's README): a new game; after the opening scene the desert
  should draw.

## 0.1.9's changes (a tester's reports on 0.1.8, and a Pimax player's)

- **Defaults**: `[vr] options: ...` with `wheel-lean on`, `wheel-b-height on` and `fishing-first-person on`.
- **Gamepad only with no gamepad** (the Pimax player's case): turn Gamepad only on with no gamepad connected. At Jak
  1's title the VR controllers still play (A starts, the stick moves the menu): `[vr-pad] Gamepad only: no gamepad
  connected now, so the VR controllers play`. Connect a gamepad: `[vr-pad] Gamepad only: the VR controllers are left
  out of the game`, the gamepad alone plays.
- **The fisherman's game from Jak's eyes** (Jak 1, the river by the jungle; Fishing game in first person, on to
  start): talk to the fisherman and play. You stand on the bridge in Jak's eyes, the river turned ahead of you
  (`[vr-view] the fishing game from Jak's eyes: the net in your hand, the river turned ahead of you`), the net in your
  right hand, its pole where the controller points and long enough for the basket to reach where the fish come down
  (`[vr] the fishing game from Jak's eyes: the net's pole ... m (its own 3.22 m, ...)`). Point the basket under a
  falling fish: it is caught and the net's hand buzzes (`[vr-feel] a fish in the net`; harder for the big one and the
  eel), and the basket going into the water buzzes lightly. Squeeze the left grip: the net goes to the left hand
  (`[vr-pad] the fishing game: the net taken in the left hand (its grip squeezed)`), and the right grip takes it back.
  No punches or kicks while fishing. Each second of play a line says where the net is across the river.
- **The fisherman's game from the game's camera** (Fishing game in first person off, or third person): the game's
  camera; move your raised hand side to side and the net follows it, smoothly (`[vr-pad] the fishing game: the net
  follows the right hand side to side, the raised hand (the stick still moves it)`). The stick moves it as the game's
  own does; after that, the hand takes the net back once it moves 5 cm.
- **Gun turrets aimed by the hand**: Jak II's drill platform turrets; Jak 3's Spargus wall turret, the forest's
  turrets, the Scorpion's gun (the desert's beast battle) and the pickup's gun in the blow tower mission. With your
  first finger on the right trigger, the turret turns to where your right hand points and the trigger fires; lift the
  finger off and the stick aims as before. `[vr-gun] the turret aims where the right hand points`, `... the
  Scorpion's gun aims ...`, `... the pickup's gun aims ...`. The view at the Scorpion's and the pickup's gun is the
  game's camera, tipped so what it shows is ahead (`[vr-view] a view from the game's own camera ...`).
- **Daxter's maze game** (Jak 3, Vin's room): the board is ahead of you, not under your feet (`[vr-view] the view
  tipped N degrees down with the game's camera, so what it shows is ahead`); the view as before after it.
- **The orb hunts' opening** (Jak 3, the desert's side missions) and any scene where the game's camera shows something
  away from Jak while it holds him: the view goes to the game's camera (`[vr-view] the game's camera shows something
  away from Jak while it holds him ...`), then back (`... is on Jak again`).
- **The Dark Maker robot** (Jak 3, the Dark Maker ship): third person while in it, whatever the view setting, and
  first person again once out.
- **What hangs on Jak's back** (Jak II and 3, first person): the JET-Board put away and Light Jak's wings are not seen
  in your view (`[vr-body] in first person the eyes leave out board-lod0 (the JET-Board on his back)`, `... (Light
  Jak's wings)`); the board ridden is seen as before, and third person shows both.
- **Steer by grabbing the wheel, B and the lean** (Jak II and 3's hover vehicles): with your right hand on the wheel,
  B is the height (`[vr-pad] B with the right hand on a hover vehicle's wheel: R2 (the height)`) and the right stick's
  click the turbo; push the wheel away to lean forward, pull it back to lean back (`[vr-pad] the wheel pushed 12 cm:
  the left stick forward (lean 1)`), without turning.
- **Jak's hands on the handlebars and the wheel** (Jak II and 3's city bikes and cars, first person, Steer by grabbing
  the wheel): squeeze a grip at the handlebars or the wheel; Jak's drawn hand goes onto the bar's end or the wheel's
  grip and closes on it (`[vr-body] the left hand onto the ride's handle: ... m from the head ...`), and off again as
  you let go. Looking down, both hands are on the bars, not floating beside them.
- **Swimming** (all three games, first person): the eyes stay over the water as Jak swims (`[vr-view] swimming: the
  eyes kept at least 12 cm over the water ...`), and in Jak II and 3's city waters over the waves round them too,
  never under the sea looking at black, also as a wave washes over him (`[vr-view] swimming: a wave round the eyes
  stands ... cm over his water's height: the eyes kept over it`).
- **The map** (Jak II and 3): the HUD's minimap and the pause menu's map are one picture, not doubled.
- **The HUD**: no see-through squares in the HUD's pictures (the health, the guns' ammo, the orbs).
- **The guns' shots** (Jak II and 3, every gun and mod): each shot leaves the gun in your hand and goes where the hand
  aims; the Peace Maker's ball too. Jak 3's Needle Lazer still picks its own targets round Jak, as on a pad.
- **The city's sections** (Jak II): every part of the city loads as you go; a log names any section the game wants
  while the view stands outside (`[vr-level] the view has stood ... s outside every level shown, ...; the game wants ...`).
- **The HUD's layer** (a Pimax or any headset whose HUD is not seen): the log's `[openxr] the HUD's layer first sent:
  ... m, at ... m in the head's space ..., its picture ... (layer N of the frame's)` says the HUD went to the headset,
  how big and where. Please send the log if the HUD or the menus' rows cannot be seen.
- **Jak II's tomb boss** (Mar's Tomb, multiplayer on or off): walking up to the Baron's widow plays her scene. A save
  made in the boss area before 0.1.9 plays it too once loaded.
- **Jak 3's Errol boss** (the war factory's end): the whole floor drawn, its panels dropping when bombed; no `!alloc`
  lines in the log at the level's start.
- **Jak 3's Dark Maker ship**: the airlock door after the first squad opens once its four troopers come and are beaten.
- **Closing the game**: no `[vr-stall]` lines after `GOAL Runtime Shutdown` unless the close takes over 5 s
  (`[vr-stall] the game is closing: ...`).
- **Windows Security** (PC): gk.exe is kept after a long play. If Windows Security removes it anyway, the launcher
  says how to restore it from Protection history.

## 0.1.8's changes (a tester's reports on 0.1.7)

- **Defaults**: `[vr] options: ...` with `ride-gun on`, `wheel-steer on` and `wheel-turn 60`.
- **A long pause** (Jak II): pause and leave it for an hour or more, then play on: no close. The log has one
  `[vr-mem] the game's global heap: ...` line at the start and none after while paused (0.1.7 grew about 100 KB a
  minute and closed after about four hours).
- **The sound device going away** (PC): with a game running, take the headset off long enough for SteamVR to switch
  its audio away (or turn the headset's audio device off and on in Windows' sound settings): `[audio] the sound device
  ...: the game's sound goes on by itself, silently ...`, then `[audio] the sound device is playing again after N s`
  once it is back, the sound playing again. A power cell's or a scene's animation never holds Jak, Daxter and the cell
  meanwhile; if a streamed animation's sound does stop, `[vr] the streamed animation ... held at ... for 4 s` and Jak
  goes on.
- **A black headset** (PC): if SteamVR does not show the game or give it the controllers, the PC's window title says
  so (`... - the headset is not showing the game (no controllers)` or the like) and the log says it every 10 s (`[openxr] the headset has
  not shown the game, or not given it the controllers, for N s: session state ...`), then `... shows the game ...
  again` when it does. `[openxr] session state: ...` lines as SteamVR moves the session along (READY, SYNCHRONIZED,
  VISIBLE, FOCUSED).
- **Hold the gun while driving** (Jak II and 3, with the gun): in a city bike or car (Jak 3: the glider and the
  hijacked pickup; the pickup has no test continue that seats Jak, so this is its only check), reach over your right
  shoulder and squeeze: the gun comes out in your right hand (`[vr-pad] Hold the gun while
  driving: this vehicle keeps his gun ...`, `[vr-holster] Hold the gun while driving: "the drawn gun is in his hand
  (not Daxter's)"`, `[vr-pad] holster: the right grip squeezed at a holster`), seen in your hand and aimed by it, not
  in Daxter's (the game's own seated poses give it to him); the right trigger fires where your hand points; let go
  over the shoulder to put it away. A hand resting at the hip in the seat draws nothing (the car's height is still the
  right grip there). In the guards' bike, the hellcat or a desert vehicle (their own guns) nothing changes. Off, or
  with Gamepad only: as 0.1.7 (Daxter holds the drawn gun in the seat).
- **Steer by grabbing the wheel**, in each vehicle: Jak 1's Zoomer; Jak II's city bike, city car, the guards' bike,
  the hellcat, a race bike (the stadium's and the port's); Jak 3's city bike and car, a desert vehicle, the glider,
  the catacombs' sled and the war factory's hellcat. Reach for the controls where Jak's hands are and squeeze both
  grips (a small buzz each; `[vr-pad] the wheel held by the left hand (grab 1; a wheel, the game's handles seen)` or
  `handlebars or a bar`), then turn your hands as a wheel, as a wheel tilted back like a car's, and as handlebars
  (push one, pull the other): it steers smoothly from the least turn, the same for each way of holding it, Jak's
  hands drawn on the handles. Turned past full lock and back a little, it steers back at once. One hand alone steers
  too. Let go: it straightens, and the stick steers as ever (both together add up). **Wheel turn**: smaller turns
  sharper. Buttons with both hands on: the right stick's click is R2 (Jak II's height, Jak 3's turbo while held;
  `[vr-pad] the right stick's click with that hand on the wheel: R2`), the left's L2 (Jak 3's camera view); a quick
  squeeze of a grip on the wheel is its button too (`[vr-pad] a quick squeeze on the wheel: R2`). Get off with the
  grips still squeezed: nothing happens on foot until you let go. Off: the grips are R2 and L2 as in 0.1.7.
- **Shadows in each eye** (all three games, by day): stand in first person and look down at Jak's shadow, then
  around at others' (Sandover's villagers, Haven's citizens, the desert), moving your head all the time and walking a
  little: each eye shows the same shadow (closing one eye, then the other, it only shifts as the ground does; his
  shoulders and arms, a hand's width from your eyes, shift far more and hide a different part of it from each eye),
  with no shadow in one eye alone, no dark streak at an eye's outer edge and no flicker as the head turns. Look down
  at your feet while walking and turning (where 0.1.7 could darken one eye more than the other, Jak's own shadow round
  the eyes); each game's log says `[vr] the shadows in each eye: counted behind what they fall on` once. If one
  differs, send the log with where (the level, looking which way).
- **Jak 1's body far from the world's middle** (the citadel, the final boss, the crater, Snowy Mountain): walk, roll
  and jump in first person: his body never pops a few metres aside for a frame and never drifts off the view. The log
  may have up to four `[vr-body] the game's skeleton sent at 0.99... of its place from the world's origin (... m toward
  it ...): taken back out along that line`, then a count a minute; no `the game's skeleton left his root by` bursts.
- **Jak 1 knocked back** (any enemy, Snowy Mountain's lurkers by the start among them): let one hit you, then stand
  and look down: his arms and body are under you from the moment he lands, not gone for two seconds. The log may
  say `[vr-body] the game's skeleton 1.9 m from his root on the ground (frame ...): kept by it` (up to four, then a
  count a minute).
- **Each hit in the log** (all three games): take a hit or two and fall once; the log says
  `[vr] Jak's health 3.0 to 2.0; the last hit by ... (...)` each time (in Jak II and 3 with `, as Dark Jak` when he
  was), nothing when it fills again.
- **The end credits** (Jak 1 after the final boss; Jak II after the last scene; Jak 3 from the title's Secrets or
  after the end): watch them for a minute, looking ahead and around: no flicker of the text, the picture or the
  black. Jak 1's are over black all round, the purple mist behind the text, never the boss's arena or Jak's hands
  showing through (`[vr-view] the end credits begin: the eyes kept black behind them, as on a screen`, then `... are
  over` when play comes back).
- **The view held in a fight** (SteamVR with Touch, where the left stick's click is the menu button): in first
  person, run with the left stick pushed and its click held two seconds: the view stays first person, no pause
  (`[vr-pad] the left stick's click held a second while the stick moved Jak: no view switch`). Let the stick rest and
  hold the click a second: third person; again: first person (`[vr] view in play: ...`). A short click is Start.
- **Off a swing pole** (Jak II's ruins and Haven, Jak 3's temple and the like; Jak 1 as before), in first person:
  swing, flip and jump off: the view stays third person from the swing through the flip and the jump and goes to first
  person once, as his feet touch the ground, never first then third then first. The log has one `[vr] view in play:
  first person` after the landing.
- **Jumps at the headset's rate** (Game frame rate Automatic, the log's `[vr-view] the game at 72 frames a second`, or
  80, 90, 120): on Jak 1's Geyser Rock, hold A for a jump, then a double jump with the second A held near the top of
  the first, pushing toward the ledge the training sign points to: he gets up onto it. The jumps feel as high as with
  Game frame rate at 60. In Jak II and 3, a double jump onto a ledge you know reaches it as before.
- **Where your files are kept** (the PC launcher's Play tab, no headset needed): one row each for the games, your
  texture packs, your saves and options, and the logs. For each, **Move to another drive or folder...** onto another
  drive: it is copied with a percentage, then the row says where it is kept now (the old copy removed). Then play a
  game: your saves load (saves), a texture pack is used (packs; set one up again), **Gather the logs** puts the zip in
  the new folder (logs), and the Logs tab's Where things are lists every folder. **Back to the default place** brings
  each back. With a moved folder's drive unplugged (or the folder renamed): the Play tab says it is not there; the
  packs: set-ups and the Texture packs tab's imports wait, with a note; the saves: PLAY and the option tabs wait (so
  no save is started over); the logs: they go to Downloads\JakQuestVR meanwhile. Close the launcher while an old copy
  is being removed: the next start finishes it.

## 0.1.7's changes (players' reports on 0.1.6)

- **Defaults** (a game with no options saved, or one 0.1.6 wrote): `[vr] options: ...` with `crosshair-mark 0` (the
  crosshair Off, Jak II and 3: 0.1.6's Dot is read as Off, any other shape picked is kept), `pole-third-person on`,
  `eco-sparks-first-person on` and `gamepad-only off`.
- **Pole swings** (all three games): swing round a pole and jump off: the view stays the game's camera until his feet
  are on the ground, then goes back in his eyes. With **Third person after a pole until you land** off: back in his
  eyes as he lets go, as in 0.1.6.
- **A long fall** (over 30 m, e.g. off the ledge with blue eco above Snowy Mountain's fort gate): Jak lands hard (a
  health pip lost) and the view goes to the game's camera while he is down (about 6 seconds), then back in his eyes,
  and he walks at once. No `ERROR: touching-list::get-shapes-entry() failed!` lines with blue eco by an orb cache.
- **Eco sparks in first person** off: take eco in first person (blue eco at Sentinel Beach): no sparks bursting at
  your eyes (`[vr] eco sparks off Jak's body: left out ...`); the game's camera still shows them. Jak II and 3: Dark
  Jak's and Light Jak's sparks the same.
- **Gamepad only** on, with a gamepad connected: the gamepad plays and the controllers do nothing in the game
  (`[vr-pad] Gamepad only: the VR controllers are left out of the game ...`); the headset turns the view, the
  gamepad's right stick turns you in first person, and the rumble is the gamepad's. The game's pause menu, VR Options:
  the VR menu, worked by the gamepad (its left stick or d-pad, Cross, Circle back) or the controllers; turn it off
  there (`Gamepad only off: the VR controllers play again`). A **Vive wand**: the roll is the left trigger with the
  thumb on the left touchpad away from its middle.
- **Jak 3's Light Shield**: hold the left grip, press Y, then press **B and keep it held**: the shield stays up while
  B is held (`[vr] Light Jak's shield asked (...): up while Circle stays held`, then `Light Jak's shield down: Circle
  let go`); or push the right stick right and keep it there. If the game refuses it, the line says why (not given
  yet, not enough light eco).
- **Jak 3's forest** (the mission from the city's port into the forest): it plays on (`[vr] the frame's draw list: N
  KB of 5120 KB used ...`), no closing.
- **Jak II's stadium race** (class 3): Jak gets into the racer and the race starts.
- **Jak 1, Load Game at Snowy Mountain**: the level appears and Jak walks.
- **Jak II's pause menu**: pause, **Game Options**, **Multiplayer** (`[vr-mp] pause Multiplayer: Host Game, Join
  Game`); in a game together, Players and Leave the game. With Jak II multiplayer off in the launcher's Mods: no
  Multiplayer row there; B from it: back to Game Options with Multiplayer still lit. Pause facing ahead, after turning
  round and after a stick turn: the ring (the round model with the menu's icons) in front of you with the words in its
  middle, never behind; turn your head while paused and it comes with you. A runtime whose head is far off its eyes
  says so once (`[openxr] the runtime's head space is N degrees from its eyes ...`).
- **The crosshair** (Jak II and 3, once you pick one): at the end of the laser only the crosshair, never a picture of
  the game or the HUD (Jak 3 above all, SteamVR).
- **Any closing**: the log ends with a `[crash]` record saying what ended it and where (an abort, a C runtime check
  and a stack overflow too; a hang: `[vr-stall]` lines). Please send that log.

## 0.1.6's changes (the owner's Quest 3 logs and players' reports on 0.1.5)

- **Light Jak** (Jak 3, once the game has given him to you): hold the **left grip** and press **Y**: Jak stops for his
  power (`[vr-pad] the left grip held and Y: L2 held`). Let **Y** go: he heals if hurt (`Light Jak by the left grip:
  Y let go, Triangle (Regeneration)`); or slide the thumb to **X** (time slows), press **A** (the wings) or **B** (the
  shield), or push the right stick. Y tapped and everything let go at once still heals. The grip let go before a
  choice with Y held: Dark Jak, and no invisibility from the Y still held. Dark Jak's quick squeeze of the left grip
  as before; as Dark Jak, Y with the grip held is his invisibility.
- **Defaults** (a game with no options saved, or one an older release wrote): `[vr] options: ...` with
  `cpu-gpu-boost-on on` (the Quest too: `[openxr] performance levels: CPU and GPU boost`), `extra-vibration-kind 2`
  (Off kept where it was Off) and `crosshair-shape 1` (the Dot, Jak II and 3; a kind picked in 0.1.5 kept). A **Quest 3 or 3S**: `eye-resolution 150` (`[openxr] eye 0: 2520 x 2640 (recommended 1680 x 1760)` on
  a Quest 3), `sharpening-strength 2`; a **Quest 2** or Pro: `eye-resolution 100`, `sharpening-strength 1`; every
  headset and the PC: `anti-aliasing 1` and `anisotropic-filtering 1` (both Off). A choice made in 0.1.5
  other than its start is kept. Say how the Quest 3 runs at 150%: Dynamic resolution's lines (`the eyes at N%`) show
  how often it draws smaller.
- **Smoother frames** at 120 Hz (Quest): `[vr-view] the game at 60 frames a second (the headset shows 60)`, not 30;
  no black flicker. **Dynamic resolution**: `[vr-perf] dynamic resolution: the eyes at ...` lines far fewer than
  0.1.5's one every five seconds.
- **The game's thread** (Quest): `[openxr] thread ... (EE) given to the headset as the app's main thread` with no
  `refused`; `[openxr] the display's thread given to the headset as the renderer`.
- **Leaving and coming back** (Quest): open the Quest's menu or take the headset off for a while, then come back: the
  game is where you left it, not closed (`[vr] the headset's session stopped: nothing drawn, the game held until it
  runs again`, then `... runs again: the game goes on`).
- **Jak II's scenes**: no black at a scene's start beyond the game's own (`[vr-view] a blackout kept in the eyes,
  black: N frames` a fraction of a second, not 0.1.5's 4.9 s).
- **Jak II's pause** (PC, SteamVR): pause after turning around by the stick or the head: the menu ahead of you
  (`[vr-view] paused: the menu on the HUD, following the head ...`).
- **Jak 1 at Geyser Rock's scarecrows** (where a Quest 3 closed): break them all. A `[vr-guard]` line in the log means
  the game lost one of its types and it was put back: please send that log.
- **Multiplayer on Jak 1's title and pause menu**: **Multiplayer** (under Load Game on the title, under VR Options in
  the pause menu's Options) opens the VR menu on its Multiplayer page (`[vr-menu] open Multiplayer (the title's
  Multiplayer)`); **Host a game** at the title offers your saves and a new game, no "This game as it is"; **B** goes
  back a page and closes it from Multiplayer, the game's menu still there. New Game and Load Game as before.
- **One player with multiplayer on** (Jak 1 and Jak II, as they start): play as you would alone. Nothing should
  differ from the original game (Jak 1: the swamp's tether rocks let Jak go, Load Game on an empty slot does
  nothing; Jak II: the pause holds the game). After leaving a Jak 1 multiplayer game, the others' Jaks are gone and
  the Zoomer has its own speed.
- **Jak 1's Boggy Swamp tether rocks**: break one with yellow eco (not the last): after its scene Jak walks and jumps
  at once (0.1.5 held him in place after any of the first three). The last rock's scene shows the Precursor arm.
- **Jak 1's ledges** (0.1.6: none of a level's was grabbed before): jump at a ledge, Jak hangs from it and climbs
  up; the climb to Klaww in Rock Village needs one (a high jump: crouch, then jump). With Jak 1's multiplayer, the
  same in a hosted or joined game. While he hangs, first person puts your eyes where his head is, just under the
  ledge's top (its grass seen from under it): say how that feels.
- **Jak II from a new game** (the prison, with its warp gate): the escape plays on into the city; Jak II's other
  warp gates, airlocks, races and the stadium's JET-Board lessons each work as before.
- **Jak 1's opening, the shot of Jak lying back on the boat** (Quest, Dynamic resolution on): at the cut to that shot
  the eyes may get smaller at once (`[vr-perf] dynamic resolution: the eyes at ...% (frames were shown late)`, several
  within a second), and the shot plays without a stutter of more than a moment. Tell us if it still stutters, with
  your Graphics settings.
- **Jak 1's crouch**: crouch by your body or with the left trigger: Jak breathes in and out about once a second, as
  the game's crouch does with the button. That is the game's own sound.
- **Jak 3's JET-Board**: on the board, hold **B** near a rail to grind it, and **X** zaps; A jumps. Off the board X and
  B are as before, and Jak II's board is unchanged (X Square, B Circle).
- **Controllers back** (PC: open and close the SteamVR dashboard; Quest: the Meta button's menu, or the headset off and
  on): hold A while you come back. Jak doesn't jump until you press A again (`[openxr] focus lost`, `[openxr] focus
  back`, `[openxr] focus back with buttons held: ...`).
- **Jak 3's scenes**: talk to Torn in the Hip Hog (or any scene started with Y): the scene plays; Y in its first second
  doesn't skip it, Y after that does (`[vr] the scene ... skipped: Triangle`).
- **Jak II after a mission's load** (the strip mine, the city's missions): the headset comes out of black with the
  game, not seconds later.

## 0.1.5's fixes (a tester's reports on 0.1.4: PC, SteamVR with a Quest over Link)

- **The gun's laser** (Jak II and 3, SteamVR): turn on a crosshair, draw the gun and point it around: at the end of
  the laser only the crosshair, never a small picture of the game or of the HUD. The same with **Show performance**
  on: only its text.
- **The crosshair**: off to start (a game with no options saved yet, or one whose 0.1.3 or 0.1.4 file had the Dot:
  the log's `[vr] options: ... crosshair-kind 0`). **Small circle**: a thin ring, about half a degree across at 100%,
  sharp at any distance; red on an enemy the gun can reach.
- **Game frame rate: Automatic** on SteamVR: the log's `[vr-view] the game at ...` lines no more than a few in a run
  (0.1.4 gave up to 26 changes a second), and Jak 1's `Tried to set unsupported video-mode` gone. A change of the
  headset's own rate shows as `[vr-view] the headset at N a second for 1.0 s: the game follows it`.
- **Jak 1's temple** (Forbidden Jungle, inside): where 0.1.4 froze (`too many sound commands queued`, then `STALL:
  waiting for IOP on RPC port #0`), the game goes on. If the sound stops answering, the log says `the IOP has not
  answered the sound player for over a quarter of a second` (a sound or two missed) and later `the IOP answers the
  sound player again`; say whether the sound came back.
- **Lag spikes** (Jak 1's plant boss, going in and out): say whether they still happen. A frame of over a second
  writes `[vr-stall] the game has not finished a frame for 1.0 s: it is ...` with where the display and the sound
  processor's threads were; send that log.
- **Jak 3's leaper race restarted** several times from the pause menu: say whether it still closes. If the PC game
  closes on its own, its log ends with `[crash] the game crashed: ...` lines; send that log.
- **No flat screen** at the game's start, between scenes, after a mission failed or on a long load: black in the
  headset, then the game around you again (the log's `[vr-view] a blackout kept in the eyes, black: N frames (S s)`
  for each). A scene put **On a screen** in the VR menu's Cutscenes still shows on the screen.
- **B in the pause menu**: Jak 1's pause opens on the power cell page; **B** opens the Options (its "Circle Options"),
  **B** again goes back, **Y** shows the scout flies. In the Options and save menus **B** goes back. Jak II's lobby
  list: **B** opens Manual (Join with Invite, Direct Connect). Turn round before pausing: the menu is in front of you.
- **Dark Jak's button** (Jak II and 3): a quick squeeze of the left grip (or the left stick's click) makes Dark Jak
  (`[vr-pad] a quick squeeze of the left grip: L2`; a quick click, `[vr-pad] a quick click of the left stick: L2 held
  0.30 s`), once his meter has some dark eco. Jak 3: squeeze, let go and squeeze again and hold, then the right
  stick or A, B, X, Y for Light Jak's powers. On SteamVR with Touch (the left click is the menu button) the grip
  works the same.
- **The sky** (Jak 3's desert, Jak II's city), in first person: the farthest hills whole against the sky, clouds and
  the haze behind them, nothing flickering at their edges as you turn your head.
- **First person for each ride**: the VR menu's Body, with First person in vehicles on: turn one ride's switch off
  (the JET-Board, say) and ride it: third person on it, first person on the others.
- **Jak II alone, multiplayer on** (as it starts): the burning bushes offer their missions, the Baron's squid boss
  shoots at you and its ending scene plays, the tomb's widow throws her bombs and her ending plays, and after a
  failed mission walking back to it starts its scene and the mission again.
- **Effects in Jak II and 3**: Dark Jak's lightning and change, the guns' impacts, Jak 3's red guns' shockwave: no
  colored or black square in front of your face; Dark Jak's darkening dims the HUD only.

## Performance options (launcher's Graphics tab, as Ratchet & Clank Quest VR's)

- **0.1.4's Quest start** (a tested Quest 3's Graphics): a game with no options saved yet starts at 120 Hz (`[openxr]
  refresh rate 120 Hz`, or the most a headset offers up to it), Smoother frames on, High foveation, `[openxr]
  performance levels: CPU and GPU sustained high`, anti-aliasing and anisotropic filtering off, `[openxr] sharpening:
  normal`. On a Quest 2 or 3S, note the `[vr-perf]` lines with it, and say whether Balanced or Performance plays better.
- **Smoother frames (AppSW)**, off to start from 0.1.1 to 0.1.3 (the title screen flickered with it), on to start on the
  Quest from 0.1.4: `[openxr] smoother frames (Application SpaceWarp): on, motion ...`; turn your head and run,
  and say whether anything flickers or tears. 0.1.1 changed how it is given (the PC cannot show it: this is the check
  that matters most). The log should say `[vr-view] the game at 36 frames a second (the headset shows 72)`, then
  `[vr-view] smoother frames: the headset makes the frames between from here` once in play and `none made here` on
  the title, in a menu and through a load. Look for: the title and the menus steady (none there); no flicker as a
  scene cuts or the game hitches; the game at its right speed (0.1.0 ran it slow with this on); things that move by
  themselves (enemies, Jak in third person) may still show a double edge, since only the view's own motion is given.
- **Game frame rate** (0.1.1, 60 to start): **Half the headset's**: `the game at 36 frames a second`, half the work,
  the head still turns smoothly, moving things step a little; **The headset's**: `the game at 72 frames a second`
  (90 or 120 at those refresh rates), the smoothest. In each, Jak runs and jumps as far as at 60, and scenes keep
  their sound in step.
- **Dynamic resolution** (0.1.1, off to start): in a heavy place (Haven City, Spargus) `[vr-perf] dynamic resolution:
  the eyes at 90% (frames were shown late)` and lower, down to 60%, and back up a step every few seconds where it is
  light. Say whether the changes of sharpness are seen, and whether it ever goes up and down over and over.
- **PS2 detail** (0.1.1, off to start): far-off things and effects drawn as the PS2 drew them; note the `[vr-perf]`
  frames with it on and off in the same place, and how much pops in.
- **Sharpening** (0.1.1, off to start; next start): `[openxr] sharpening: normal` or `strong`; with a lower Eye
  resolution the edges should stay crisp. If the log says the headset does not offer it, nothing changes.
- **Foveated rendering**: `[openxr] foveated rendering:` names the level. The edges of the view a little softer at
  High, sharp at Off; **Highest** (0.1.1) softer again, for the most frames.
- **CPU and GPU boost**: `[openxr] performance levels: CPU and GPU ...`.
- **Refresh rate** and **Eye resolution**: `[openxr] refresh rate ...` and the eye size in `[openxr] eye ...`.
- **Max draw distance**, on: across Sandover, Haven City and Jak 3's desert nothing should pop in near you. Off for
  comparison, with the `[vr-perf]` lines of both.

## Hands, body and haptics

- **The d-pad**: the left thumb resting on its rest makes the right stick the d-pad (Jak 1's warp gate, Jak II and 3's
  gun choice). Controllers without a thumb rest sensor: the left hand by the head instead. Since 0.1.1 each direction
  pulses in that hand (`[vr-pad] d-pad up (the right stick)` and so on), the thumb rest a light one as it takes the
  stick, and Jak II and 3's gun change pulses in the gun's hand (`[vr-gun] the gun switched`): say whether each is
  felt, and whether the d-pad ever fails to go in. Off with **Buzz on d-pad presses** and **Buzz on gun switch**.
- **Select and L3** (0.1.5): the left hand held by the head, click the left stick (`[vr-pad] Select: the left stick's
  click with the left hand by the head`; Jak 3 pauses without the menu, Jak II multiplayer shows the player list) and
  the right (`L3: the right stick's click ...`; Jak II multiplayer, the names and HUD while held). On foot in Jak II
  and 3 the same clicks with the hand down stay Dark Jak and the JET-Board. Vive wands: the left touchpad's click in
  its middle, the hand by the head (`Select: the left touchpad's click ...`); on a direction, that direction only.
- **Extra vibration** (0.1.4; the VR menu's Controls, the launchers' VR Controls). **Pickups** (to start): collect an
  orb, eco, a power cell or a scout fly (Jak II and 3: ammo, a gun, a skull gem): a light tick, a longer buzz, a
  strong one. **The whole world**: walk and run (each step in that foot's hand, firmer running), jump, land, roll,
  punch and kick, hit an enemy or a crate (the punching hand), and stand near an explosion or a boss's stomp (a shake
  by how big). The log names the first of each kind in a run: `[vr-feel] small pickup (6 0; Extra vibration:
  Pickups)` (the number is the game's pickup type), `eco`, `big pickup`, `step`, `jump`, `landing`, `roll`, `attack`,
  `hit`, `shake (<cm> <ticks>; ...)`. Say whether any is too strong, too weak or too often (a step on every frame, a
  buzz that never stops), and that **Off** feels as 0.1.3, **Vibration off** stops them all and **Vibration strength**
  scales them. In multiplayer the other players' steps and pickups should not buzz your hands.
- **The menu pointer** (0.1.1): Jak 1's save and load files and its yes / no questions, each game's On / Off rows and
  Yes / No questions (point at a side, then the trigger), Jak 1's sliders, and a long list (Jak 1's options, Jak II and
  3's game options) scrolling while the dot rests on its top or bottom row.
- **The stick in Jak 1's menus** (0.1.1): on the title, the left stick down to Load Game and A: the load screen, not a
  new game (`[vr-pad] d-pad down (a stick in Jak 1's menu)`), with the hand pointing at the menu or not. The same with
  the stick in Jak II and 3's menus (they read it themselves).
- **A scene's view** (0.1.1): hold the menu button a second in a scene: through Jak's eyes, then around you, and back
  (`[vr-pad] scenes are seen ...`).
- **Spin kick by spreading your arms** (off to start, 0.1.1): turn it on, hold both arms out as a T: one spin kick.
- **First person in vehicles** (on to start since 0.1.3): get on the Zoomer, the board or in a car: the view is
  from Jak's seat and turns with the vehicle. Off: third person there. Vehicle view height, forward and tilt (0.1.3)
  move and tip that view; the hands and the gun stay where yours are.
- **Fingers**: touch and lift the buttons, trigger and grip: Jak's fingers curl and point with yours.
- **Hands at the world**: put a hand into a wall or the floor: it stops there with a short buzz. 0.1.1: lay the palm
  on a wall or the ground and the fingers settle on to it; point the fingers into it and they fold; draw the hand
  back and they open again, each finger by itself and without a snap.
- **Jak 1's wrist straps** (0.1.1): the loose ends at his wrists hang down whichever way the hand is turned, swing as
  you shake it and come to rest, and lie on the ground or a wall instead of going through.
- **Walking straight** (0.1.1): push the left stick forward: Jak goes where you look, with the head level, looking
  down and tilted. A thumb a little off straight goes that little off, not a wide diagonal.
- **Adaptive cutscenes** (0.1.1, on to start): Jak 1's opening is around you until Jak stands at the silo, then through
  his eyes (`[vr-cam] adaptive cutscenes: ...`); a scene never flips back and forth. Off: every scene as Cutscene view
  says.
- **A roll** (the left trigger while running): say how it looks and feels in first person.
- **Melee** (Fists on the right grip): hold the right grip and punch (`[vr-pad] punch: ...`), sweep across for the spin
  kick (`[vr-pad] spin kick: ...`), crouch for real and punch up for the uppercut (`[vr-pad] uppercut: ...`). Jak 1
  with yellow eco: the shot leaves the punching hand. 0.1.1: the left grip makes the left fist in Jak II and 3 too
  (`[vr-pad] fists up: the left grip`), and before Jak has a gun a fist made low at the hip is a fist, not a holster.
- **First person in vehicles**, also on Jak 1's and II's slides and at the turrets (0.1.1, with the switch on).
- **Jak II and 3's gun**: grip at the hip or over the shoulder draws it (`[vr-pad] holster: ...`), let go there puts it
  away; it aims where the hand points; the left hand on it steadies it. Each gun's shot kicks the hand. The VR menu's
  **Holster size** (130% to start since 0.1.1): at 100% the places of 0.1.0, at 200% easy to find, at 50% only right
  at the hip or behind the shoulder; say whether 130% draws the gun when you did not mean it (a fist low at your side).
- **Body shown**: try each choice in the VR menu (Whole body to Hands only): what you see of Jak changes, nothing else.
- **Arms by the face** (0.1.1): bring a hand up close in front of your eyes, then over your head and across your face:
  Jak's hand bends at the wrist and his forearm comes up from below (or beside the head), never through your eyes, and
  his arm never vanishes. Move fast and roll the wrist: the elbow follows without flicking side to side. **Hide arms
  near your eyes** (off to start) fades out an arm that still comes within 16 cm of the eyes.
- **Life size** (0.1.1): World scale 100% (the new start): Jak is as tall as you, his hands and the gun a person's
  size, the world as big as a real one around you. 145% is how 0.1.0 looked (you a giant, the hands twice as big):
  say which feels right, and whether 100% makes jumps and ledges read well.
- **His body under you** (0.1.1, **Keep your body upright** and **Fade body near your eyes**, both on to start): punch,
  spin kick, uppercut (button and for real), jump, high jump, long jump, dive, roll, crouch. Look down in each: his
  body stays upright under your head, nothing of him in front of your eyes; a leg kicked up by your face, and his body
  in a roll, a flip or a dive, fades out and back smoothly, never a pop (`[vr-body] ... fades out of the eyes` /
  `comes back to the eyes`). Crouched (the button, and crouching for real), look down: his knees in front of you over
  his feet and his body leaning forward as a person's in a squat, never his legs under the ground or his belt in your
  face. Turn your head without moving: his hips stay, then follow past about 35 degrees. Run and
  turn: his hips face where he runs, but never more than about 60 degrees from where you look. Each switch off: as
  0.1.0 (the body as the game's animation has it; nothing faded).
- **Jak 1's look-around** (Triangle in play): the view stays where Jak is (not ahead of him), his body still; Triangle
  again leaves it.
- **Jak 1's Lost Precursor City** (0.1.1): Rock Village's elevator down (the ride's scene, then the city drawn round
  you at the bottom), the blue eco launchers in the helix room (Sunken B: no crash on entering), the floor buttons that
  raise the square platforms (dive on them: the scene, then play), the pipe game's three buttons at the back of the
  room (dive on one: the pipes' scene, then Jak free to move), the helix button and the room's way out. A crash names
  the process in the log (`kmalloc: !alloc mem in heap for #<process ...>`).
- **Jak 1's scenes through his eyes** (0.1.1): the opening and a talk to the Sage: you face where Jak faces in the scene,
  what happens in front of you.
- **Jak 3's gun magazine** (0.1.1): draw the gun: its ammo stays on the gun as you move it.
- **Vibration**: the game's own rumble (a hit, a Zoomer crash) and the VR actions' buzzes, at Vibration strength 100
  and 50.

## Multiplayer (Jak 1)

- Two Quests on one Wi-Fi: one hosts (VR menu, Multiplayer, Host a game, a save), the other sees it under **Join a
  game** with its level and power cells, and joins. Each sees the other's Jak, with their VR head and hands. A Quest
  and a PC on the same network the same way.
- **Play which save?** on the guest: the host's progress (`party: the host's progress came (... bytes)`), then the
  guest plays at the host's place.
- The host's **Players** page: Remove takes the guest out (the guest's menu says the host took them out).
- Over the internet: a PC runs `party_server.py` (docs/MULTIPLAYER.md), its address in both launchers' Mods section;
  host and join there as above.
- **Multiplayer: Off** in the launcher: nothing of it in the game, and the VR menu's rows say "Turn it on first".

## Mods (Quest; the PC launcher's Mods tab the same)

- The Mods section starts by saying what works here (Multiplayer, Quest mod packages, texture packs on their own tab)
  and that OpenGOAL's own mods are flat screen only.
- **Browse mods**: both lists read (this project's and jakmods.dev's), "Read at ..." under them; Jak 1 / Jak II / Jak 3
  each show their mods (no texture packs: those are on Texture packs); **Show them** lists OpenGOAL's own mods as Flat
  screen only, each with **Its page**. Without a network: a plain line saying a list could not be read, and the copy
  from before when there is one.
- **Mod packages** (Precursor's): import one, enable it with the game closed, play, disable it. A texture pack's .zip
  given to **Import a mod**: "It is a texture pack: imported to Texture packs".

## Texture packs (Quest: a game's options page, Texture packs; the PC launcher's Texture packs tab the same)

- The tab is beside Mods, between Controls and Mods, and its name reads whole.
- **Import a .zip**: a pack downloaded in the headset's browser (Downloads), an OpenGOAL launcher pack. It shows in the
  game's list, **Imported**, on. Then **Prepare again** and the disc image: the game's pictures changed. Turn it off and
  prepare again: the game's own.
- **Import a folder**: a pack's folder copied to the headset (the folder picker asks to allow it): the same.
- A Jak 1 pack imported from Jak II's page: the message says "for Jak 1 (on that game's Texture packs)", and it is in
  Jak 1's list, not Jak II's.
- A .7z, a PCSX2 pack, a zip of photos: a plain message, nothing added.
- **Find texture packs**: the lists' packs; **Add** one: it shows in the list, **From a mod list**, and the button says
  **Added**. A newer version in the list: the pack's card and the list both offer **Update to ...**, and it keeps its
  place and its On / Off.

## The launcher

- **Check for updates now** on a later release: the launcher updates, and the games start after it with their set-up,
  saves and options kept.
- What's new, the Controls section for your controllers, and the link to Ratchet & Clank Quest VR in the corner.
