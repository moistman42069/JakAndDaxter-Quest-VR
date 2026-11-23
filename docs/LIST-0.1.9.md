# Jak and Daxter Quest VR 0.1.9: the list

Started 2026-11-20, after 0.1.8 was published. Released as 0.1.9 on 2026-11-23 (the owner: "just publish it when done").
Marks: [ ] to do, [~] in progress, [x] done (how it was checked).

A tester's reports on 0.1.8, passed on by the owner. The owner: "we really shouldnt be having any rendering or gun
issues at this point. please rectify everything and make this next release as good as possible." Standing: every
crash and close explains itself in the game's own log; resolving and checking is ours (the sims first; a player's
log only helps).

Later the same day the owner passed on more of the tester's reports and four 0.1.8 logs (PC, SteamVR's OpenXR in Meta
compatibility mode 2.19.4, Touch controllers, 72 frames/s). They are kept read-only in
C:\JakQuest\downloads\tester-logs-20261120:
- game-jak2-output-6: Mar's Tomb, then a new game through the city's first missions.
- game-jak3-output-5: the desert and its vehicles, the Light Jak powers, the city.
- game-jak3-output-6: the city, then the forest.
- game-jak1-output-11: a normal game.
- game-jak3-output-7 (passed on after the others): the city, the sewers, Vin's room's maze game, the war factory with
  Daxter's four sections, and Errol's boss (item 16).

All five closed normally (exit code 0), with no `[crash]` and no `[vr-stall]`. The tomb and Light Shield notes below
come from them.

## To fix

1. [x] **Jak II: the boss's scene in Mar's Tomb does not start, and the player is stuck** (the tester: "started new
   jak 2 game because boss cutscene in mar's tomb wont start", then "jak 2 daxter section just ended 1 of the times i
   died in the spider chase... but i am pretty sure that is a base game bug", and "when i caught back up with daxter
   at the boss fight area of mars tomb cutscene did not happen, so i am just stuck in mar's tomb"). The tomb's boss is
   the Baron's Widow (goal_src/jak2/levels/tomb/widow.gc, widow-baron.gc, tomb-scenes.gc). Their 0.1.7 Jak II log died
   again and again at this boss (LIST-0.1.8 item 11), so 0.1.7 reached the fight; this is 0.1.8.

   **What game-jak2-output-6 shows** (the tomb from a save at 30:44 to 51:35, multiplayer on in the options):
   - **Daxter's spider chase** (level tombe, task `tomb-poles-boulder`): 41 deaths to the big spider and the small
     ones; those looked at restarted at the chase's start. The last, at 40:35 (Daxter's health 4 to 0 by
     `tomb-spider`, near the top of the climb), restarted further on, and the chase ended there:
     - an auto-save;
     - tombe dropped and tombb entered;
     - the chase's last scene, `tomb-spider-chase-2`, unlinked (ten "could not find a master slot to unlink" errors);
     - Jak in play, first person.
   - **Jak's poles** (task `tomb-poles-poles2`): six falls.
   - **The boss area:** tombboss and tombext were loaded (47:47) and the task `tomb-boss-introduction` was started
     ("set task start time"). Jak fell (endlessfall) five times there, and `tomb-boss-intro` never played. The
     tester saved and quit to the title at 51:35; as it quit, the boss door's scene `tomb-boss-open` was unlinked.
   - **Not our 0.1.8 guard:** there is no "streamed animation" line in the whole log (0.1.8's stuck-stream guard,
     LIST-0.1.8 item 2), no `[crash]` and no stall.

   **Lead:** the Jak II multiplayer mod rewrote the scene's trigger (widow.gc, the `:trans` of the widow's waiting
   state).
   - It plays `tomb-boss-intro` only when the nearest player record whose character is Jak
     (`mp-nearest-player-character-record *target* (mp-player-character jak) MP_INVALID_PLAYER_SLOT`,
     multiplayer/player/player.gc) is within 376832 units (92 m) of the widow, and only with no `*scene-player*` and
     `tomb-boss-door` closed.
   - The original asks Jak's own distance.
   - If a one-player game's local record is not Jak at that moment (no record, an unknown character, or Daxter still
     after the chase ended by a death), the scene never plays. That is a one-player game changed by the mod, against
     the standing rule.
   - The mod's door data for the tomb (engine/mods/airlock-customizable/airlock-data/tomb/tomba-airlock-data.gc,
     `tomb-boss-door-1`) also decides the door's scene (`tomb-boss-open`) and the boss's loads by the tasks.

   **To check by sim:**
   - The continue `tombboss-start` (engine/level/level-info.gc) with multiplayer on and off: does the scene play, and
     what is the local player record's character?
   - The chase ended by a death at its end (the continue `tomb-boulder-end`), then the walk to the boss: the
     tester's case.
   - Whether the chase ending on a death happens in the original too (the tester thinks so), with the mod off.

   **Fix:** in a one-player game, the trigger and every such mod rewrite ask Jak himself (`*target*`), as the
   original does. Also search the mod's other rewrites of the original's `*target*` checks
   (`mp-nearest-player-character-record`, `mp-jak-target-for`) for the same.

   **A stuck save:** the tester's save is in the boss area with `tomb-boss-introduction` open. After the fix, loading
   that save must play the scene.

   **Found:** outside a multiplayer game there is no player record at all (the mod makes one only on hosting or
   joining), so the mod's nearest record playing Jak was never found and the scene never started: every one-player
   game that reached the boss after the chase, not only the tester's.

   **Fix (build 122):** widow.gc's trigger asks Jak himself (`*target*`) when `(mp-session-solo?)`, the mod's record
   only in a multiplayer game, and logs `[vr] the tomb boss's scene starts: Jak ... m from her`. The mod's other
   rewrites keep the one-player behaviour already: the talkers' voice place (ambient.gc: moved only when a record is
   found), the vehicle camera (vehicle.gc: `*target*` first), the board trainer (skatea-obs.gc: only in a
   multiplayer game) and the gun's Daxter ride check (target-gun.gc: R1 as the original first).

   **Checked:** sims s09 tb2 and tb3 (Mar's Tomb's boss continue, the task the tester's save had open):
   `[vr] the tomb boss's scene starts: Jak 53.6 m from her`, then the fight (builds 122 and 123). On the 0.1.8 build
   (s09 tb-on and tb-off, multiplayer on and off in the options) the scene never started, as for the tester. A
   loaded save there plays it, as the continue does (the scene is the task's, started from the widow's waiting state).
2. [x] **Jak II: a section of the city not loading** (the tester: "yup section of the city just not loading. a section
   of the city didn't load so i explored it a little and closed game"). Where is not said. Look at what decides which
   city levels load and which of their parts swap in (LIST-0.1.7 item 12, Jak 1 at Snowy: a level's last part, its
   VIS, swaps in only once the camera is inside it, and in first person the camera is Jak's eyes; the levels now load
   from the game's own camera; check that Jak II and 3 have the same fix and that nothing in the city still asks from
   the eyes; Max draw distance; the multiplayer mod's level changes), and add a log line when Jak stands where no
   level is loaded (which level should be, which are). Sim: drive and walk round the city's sections (the bike
   continues of LIST-0.1.8's wh2b) watching the loader.

   Later the tester: "the area that wasn't loading 2 times that launched the game is loaded now". So the section
   failed on two launches of the game and loaded on a later one: it comes and goes. Look for what differs between
   launches (the save's place on loading, the order the city's levels load in, the memory free for them) rather than
   for a part that never loads.
   - Found (sim s10 pf2p, the port): in first person the eyes, in Jak's head, stood outside every one of the city's
     sections for 1.2 to 1.5 s at a time while the game's camera, a few metres behind, stood inside one. Jak II and 3
     asked which level they stand in from the eyes (`*math-camera*`), and that decides the view's level and the
     continue whose list loads the city's sections. Jak 1 had this fixed in 0.1.7; Jak II and 3 had not.
   - Fix (Jak II and 3's level.gc, pc-vr.gc `vr-level-inside?`): the test is from the game's own camera, as in the
     original; the eyes still draw from the eyes. A log line too: when the view stands outside every level shown for
     over a second, `[vr-level] the view has stood 1.0 s outside every level shown, at ... m; the game wants ...`,
     with Jak's place, each level and its state, and the levels the game wants (again every 10 s while it lasts).
   - Checked: sims s12 and s14 pf2p (the port): the watch names each level and what the game wants; with the eyes
     under the water (item 28) every level the game wanted was up and shown, the game's camera inside them. The
     tester's case came and went between launches and was not seen again; a next log will name the section.
3. [x] **Jak 3: the Peace Maker's shot does not start at the gun in your hand** (the tester: "just got my favorite gun in
   jak 3 the peace maker, projectile source not attatched to gun so cant aim peace maker"). The dark gun's shots
   (goal_src/jak3/engine/target/gun/gun-dark-shot.gc) start from the gun's fire-point in places (lines 149, 1391,
   3552 to 3562), but the Peace Maker's ball, its aim or its target search may take another point or direction (the
   game's own aim, Jak's facing, the camera). Check its start and its direction against the hand's aim, then the same
   for Jak II's Peace Maker (Jak II has it too).

   game-jak3-output-6 has it: at 24:55 the d-pad chose the game's gun 35 (`gun-dark-1`, the Peace Maker,
   game-info-h.gc). At 24:57 it fired (its blast's sound stream `pmkrxplo`). Nothing in the log says where the shot
   started or where it went. Add a log line for each kind of shot's first firing, giving its start against the muzzle
   in the hand and its direction against the hand's aim, so the next log tells.
   - Found (session 7831386a, 2026-11-20): aimed by the hand, the gun's fire-point and fire-dir-out are the muzzle in
     the hand and its aim, but the Peace Maker's ball (gun-dark-shot.gc, both games) grew where the game's animation
     holds the gun and flew by the game's gun pose or the camera, homing on whatever the game's own aim had locked.
   - Fix (builds 122 to 124; target-gun.gc `vr-gun-hand-aimed?`, gun-util.gc, gun-dark-shot.gc, projectile.gc):
     while the hand aims our own Jak's gun, the ball grows at the gun's muzzle, leaves along the hand's aim and homes
     only on what the hand points at (within 30 degrees), else flies straight; what the gun locks onto is found along
     the hand's aim. Another player's gun stays the game's. The first firing of each kind of shot logs
     `[vr-gun] "<shot>" (gun N) by the hand: starts N m from the gun's muzzle, goes N degrees off the hand's aim`.
   - Checked (sims s09 gh2b on Jak II, gh3b on Jak 3, build 123): the Peace Maker's ball `starts 0.00 m from the gun's
     muzzle, goes 0.0 degrees off the hand's aim` in both games.
4. [x] **Every gun, every mod, aimed by the hand** (the owner: no gun issues "at this point"). Fire each of Jak II's
   four guns and each of Jak 3's twelve (red, yellow, blue and dark, three each) in first person, on foot and seated
   (Hold the gun while driving), and check for each that the shot, its flash and its laser start at the muzzle in the
   hand and go where the hand points (LIST-0.1.3's muzzle work: fire-point and laser-point from the right
   controller's aim). Make a sim that fires each in turn at a target off Jak's facing, so a shot that follows his body
   or the camera instead shows.
   - Sims: tests/jak2_gun_hand.sim and tests/jak3_gun_hand.sim (`JAKVR_TEST_GUN=all` gives every gun; each fired in
     turn with the hand turned away from Jak's facing).
   - Found: besides item 3's ball, Jak 3's Wave Concussor gathered and burst its shockwave where the animation holds
     the gun, the Plasmite RPG's grenade and the Gyro Burster's saucer went nearly straight up with the hand pointing
     down (the game raises the aim for a throw), and the Scatter Gun's muzzle blast (Jak II and 3) was turned by his
     facing, not the hand.
   - Fix (builds 122 to 124; gun-red-shot.gc both games, gun-blue-shot.gc Jak II, target-gun.gc): the shockwave at the
     muzzle in the hand; the grenade and the saucer thrown the hand's way (pointing down, as if level; up, as the
     game's, `vr-gun-lob-level!`); the Scatter Gun's blast and its cone turned by the hand's aim (and kept from a
     camera inside it), and logged as the other shots; Jak II's Vulcan Fury's flash and shells from the gun in the
     hand.
   - Checked (sims s09 gh2b and gh3b, build 123): every shot `starts 0.00 m from the gun's muzzle` and goes 0.0 to
     0.1 degrees off the hand's aim, but Jak 3's Needle Lazer (gun 34, `gun-blue-shot-3`) at 27.6: the game picks its
     needles' target among those round Jak (gun-fire-blue-3, 40 m, enemies first), as in the original, so it is left
     as the game's. The Scatter Gun's blast turned by the hand: sim s12 gh2c (build 130).
5. [x] **Jak 3: a vehicle's turret, nothing to see in first person** (the tester: "vehicle turret section if 1st person
   you cant see anything", "still in jak 3"). Which section is not said; a turret on a vehicle, not Spargus' wall
   turret (C:\JakQuest\sim\jak3_turret.sim, LIST-0.1.3 item 16). Find each of Jak 3's turrets on vehicles (the
   desert's gunner seats, the missions where another drives), see each in first person (the eyes inside the
   vehicle's or the turret's model, or the view cut by the near plane), and fix the view there.

   Neither Jak 3 log names a turret. game-jak3-output-5 has the desert's marauder chase
   (`desert-chase-marauders`, levels desertg and desertf), driven with the wheel held and R2 and L2 by quick
   squeezes; the turret section may be another mission.
   - Found: Jak 3's gun seats on vehicles that another drives (the blow tower mission's gun, `cam-bt-gun`; the
     Scorpion's and the pickup's guns, `cam-scorpion-gun`) put the view in Jak's head, inside the vehicle, where only
     its inside is seen.
   - Fix (Jak 3's pc-vr.gc, jak_vr.cpp kFlagGameView): those seats are seen from the game's own camera, as in third
     person, whatever the view setting; the view tipped with that camera so what it shows is ahead, and the hand still
     aims the gun (item 23). The log: `[vr-view] a view from the game's own camera (a minigame's or a vehicle
     gun's)`.
   - Checked: sims s12 vgsc and s13 vgbt (builds 130, 131): the view from the game's camera at each gun, `[vr-gun] the
     Scorpion's gun aims where the right hand points`. vgbt first stopped the game on too many shadow volumes in one
     frame (item 10); since build 131 the shadow buffers grow (`grown to 98304 points`) and the mission plays on.
6. [x] **Bikes: Jak's hands do not take the handlebars** (the tester: "jak's hands dont grab handdle bars of bike style
   zoomers"; the game is not said, Jak 3 likely as it follows "still in jak 3"). Steer by grabbing the wheel draws his
   hands on the game's own handles while held (LIST-0.1.8 item 6, "the game's handles seen"). Check Jak II's and Jak
   3's bikes (the city bikes, Jak II's race bikes, the guards' bike) in first person with pictures, held and not held,
   and put his hands on the bars.
   - Found (sims s10 and s11 vh2b, Jak II's city bike): getting on, the game's stretch of his arms was undone (his
     limbs set to his own size) and the view's ease moved his hands with the view, not the ride, so the hands were
     placed off the bars' grips (0.52 m left and 0.27 m right of the head); turned as the controller, the palm hung a
     hand's length off the bar's end, the fingers open in pose_arm's even curl.
   - Fix (jak_vr.cpp, Steer by grabbing the wheel's hold): a ride's handles are taken from the game's pose as it sent
     it (before his limbs are resized or eased), the hand turned as the game's own pose holds the handle (eased in
     from the controller's turn as the grip closes), and the fingers closed round the bar as the game's animation
     closes them (`fingers_as_game`). The log says where each hand is put, once a hold: `[vr-body] the left hand onto
     the ride's handle: ... m from the head (the controller ...)`.
   - Checked: sim s12 vh2b (Jak II's city bike, build 130): `the wheel held by the left hand (grab 1; handlebars or a
     bar, the game's handles seen)`, `the left hand onto the ride's handle: -0.37 -0.53 -0.26 m from the head`; looking
     down, both hands closed on the bars' ends. Sim s17 vh3b (Jak 3's city bike, build 136, the bike parked by the
     traffic 4 m ahead of Jak, no parking spot within 150 m there): both grips `handlebars or a bar, the game's
     handles seen`, `the left hand onto the ride's handle: -0.38 -0.29 -0.22 m from the head` and the right `0.39
     -0.31 -0.22`; looking down, both gloved hands closed on the bars' ends.
7. [x] **The cars' hand holds, a little off** (the tester: "hover car hand holds close enough but adjustments could
   improve them ... i think car cars had the hand hold exactly where need be"; the desert's vehicles right, the city's
   hover cars close). Measure, for each of Jak II's and Jak 3's city cars, where the drawn hands sit against the wheel
   the game draws, and move them onto it (as the desert's).
   - Fix: item 6's, for every ride held by its controls: the wheel's handles where the game's pose has his hands, each
     hand turned and its fingers closed as the game's animation holds the wheel.
   - Checked: sim s12 vh2c (Jak II's city car, build 130): `the wheel held by the left hand (grab 1; a wheel, the
     game's handles seen)`, `the left hand onto the ride's handle: -0.21 -0.31 -0.53 m from the head`. Sim s16 vh3g
     (Jak 3's city car, build 134; no car came by in 20 s on sims s11 to s14, so the test aid now has the traffic park
     one, as the city's parking spots do): in the car in first person, both hands on the wheel's two grips in the
     picture, and the wheel turned 27 degrees each way steering it (`steering -0.38`, `0.43`).
8. [x] **Jak 3: the Light Shield still does not work for the tester** (the tester: "jak3 light heal absolutely works but
   light shield don't"). 0.1.7 item 8 made B held keep Circle held (the game keeps the shield only while Circle is
   held) and shield3 showed it up while held on the sim. On their headset it still falls: check the chord on SteamVR
   with Touch controllers (their set-up: a Quest through SteamVR) and on the Quest, the time between Y and B, B held
   through the chord's end, the light eco the shield needs (3.0) against what they had, and the help line's words.
   Make the log say each step (asked, why refused, why down) if any step is still silent.

   **The owner:** "for light shield, theres a chance he just didnt do the right input for it, so keep that in mind.
   only fix if you see any actual faults lol." So change nothing unless a real fault is found.

   **game-jak3-output-5 agrees:** Light Jak by the left grip and Y five times (39:43 to 41:19). Each time Y was let go
   with nothing else pressed, which is the heal (`Y let go, Triangle (Regeneration)`). Never B or the right stick
   after Y, and no `Light Jak's shield asked` line, so the shield was never asked for in this log.
   game-jak3-output-6 has no Light Jak.

   **The shield's input:** hold the left grip, press Y, then press B and keep it held (the right stick pushed right
   also works). Letting Y go first heals instead. If the sim and the code both hold up, nothing changes. At most,
   check that CONTROLS.md and the help line say "keep Y held, then hold B".

   **Later the tester found it:** "oh i gotta hold Y and grip when pressing other power". That was the input, as the
   owner thought.
   - Checked (2026-11-22): the VR menu's help line says it ("Left grip and Y held: Light Jak, then B held too: the
     shield"), and so does CONTROLS.md's left grip row (Y, then B kept held; letting Y go alone is the heal), and the
     log names each step (`[vr] Light Jak's shield asked (light eco ...)`, `[vr] Light Jak's shield down: ...`).
     No fault found, no change.
9. [x] **See-through squares in the HUD** (the tester: "ui transparency squares are notice able thats not a major problem
   but if you can fix it some people will appreciate it"; the game is not said). The HUD's see-through parts seem to
   show as squares in the headset. Check the HUD panel's alpha in all three games (the sim's panel PNGs have alpha:
   look at it, not the RGB), the blend of the panel over the eyes, and the edges of each HUD sprite.
   - Found (sim gh3, Jak 3's gun HUD): the HUD panel's alpha is how much of the eyes' picture each of its pixels
     hides. The game's blends write the drawn pixel's own alpha over what was there, which the flat screen never
     shows; a sprite drawn over another part of the HUD left its whole square's alpha, so the part under it went
     see-through in that square (the chevrons, the ring and the bars cut squares out of the gun's picture).
   - Fix (DirectRenderer.cpp): on the panel each blend adds its cover to what is there, additive and subtractive draws
     hide nothing, and an unblended draw hides everything under it, as each looks on the flat screen. Only the
     panel's pass and only draws that write colour: masks and the minimap's own texture are drawn as the game asks.
     The log: `[vr-hud] the HUD panel's alpha adds each HUD sprite's cover to what is under it`.
   - Checked: sims s12 hud1, hud2 and hud3 (one for each game): no squares in the panel pictures' alpha.
10. [x] **Rendering, all three games** (the owner: no rendering issues "at this point"). With items 5 and 9, a sweep in
   first person of what a player sees most (each game's opening hours, its vehicles and turrets, the HUD and the
   menus), both eyes compared, for anything drawn in one eye only, cut by the near plane, see-through or missing.
   - Done with the others: items 5 (vehicle guns), 9 (HUD squares), 11 (maps), 13 (the factory's effect, no fault),
     16 (Errol's floor), 21 and 22 (what blocks the eyes) and 28 (under the water), with pictures of each game's places
     in both eyes (sims s12 pf1, pf2s, pf2p, pf3, light3, board2, gh2c, hud1 to hud3, map2, map3).
   - Found on the way (sim s12 vgbt, Jak 3's blow tower mission): the game stopped on more shadow volumes in one frame
     than the shadow buffers held (49,152 points); and an overflowing shadow bucket was skipped wrongly (`==` for
     `!=`), so the renderer's check that each bucket ends at the next stopped it.
   - Fix (Shadow2.cpp, build 131): the buffers grow when short, up to 16 times; past that the rest of the frame's faces
     are left out (logged once a run), and the game never stops there. The overflow skips its bucket correctly.
   - Checked: sim s13 vgbt (build 131): `[vr-shadow] ... grown to 98304 points`, no crash, the mission went on.

## More of the tester's reports (2026-11-20, later)

11. [x] **Jak II and 3: the map shows a double image** (the tester: "i keep forgetting to mention jak 2 and 3 maps have
    double image issue"). Most likely the HUD's minimap. Look at the HUD with the map up, in both games, in the
    headset's picture, the HUD panel and each eye. The map may be drawn twice (in the eyes and on the HUD), drawn
    for each eye at a different place, or drawn with a texture both eyes read at different times. Picture it, then fix
    it so there is one map, steady. With item 9.
    - Found (sims s10 map2, map3): two causes.
      - The map's bucket (and Jak II's subtitles and multiplayer lists) was drawn on the HUD panel and also flat into
        each eye at the same place on its picture. The two eyes cannot join those, so a second picture showed behind
        the panel's.
      - The pause menu's ring stood in the world under a metre away, while the map and the menu's words were on the
        panel 1.6 m away. Eyes meeting on one saw the other double.
    - Fix (TextureUploadHandler.cpp, DirectRenderer.cpp; Jak II and 3's progress.gc `vr-ring-depth-scale`, Jak II's
      main.gc):
      - Those buckets draw only in their own pass (the HUD's on the panel).
      - The ring stands at the HUD's depth, as many times bigger as it is farther, so it looks the same size.
      - Three white flashes the game put in the map's bucket (the bomb bot's end, the drill platform's and the dump's
        explosions) are drawn in the screen filter's, where the eyes see them.
    - Checked: sims s13 map2 and map3 (build 131): one map, on the panel; `[vr-view] the pause menu's ring at the
      HUD's depth: 1.60 m`.
12. [x] **Jak 3: Daxter's maze minigame cannot be played well in the headset** (the tester: "in jak 3 the daxter
    pacman like minigame cant be seen if your set to 1st person, and you have to look down to play it in 3rd person.
    looking down kinda pain in the neck can you fix it to where camera orientation i better durring this jak 3
    minigame? pac dax is replayable").
    - The game is the power-grid maze in Vin's room (goal_src/jak3/levels/city/vinroom/power-game.gc), seen from the
      game's own camera above the board.
    - In first person the view is Daxter's eyes, so the board is not seen. In third person the game's camera looks
      straight down, so the player has to look down.
    - Fix: while it plays, use the game's camera whatever the view setting (as a minigame with its own camera), and
      turn that view so the board sits ahead of you at a comfortable height, not under your feet. For example, the
      board's "up" toward the top of your view, or the top-down picture shown on a screen in front.
    - Test continue: it is replayable, so start the sim from its replay or its task's continue, and picture it at
      the start and while playing.
    - Fix (Jak 3's pc-vr.gc, jak_vr.cpp kFlagGameView): while the maze game plays it is seen from the game's camera
      whatever the view setting. That view is tipped down by the camera's own pitch (40 to 75 degrees), so looking
      straight ahead sees what the game's camera sees: the board in front of you, its far side up. The hands go
      through the same tilt.
    - Checked: sim s12 pg (build 130, the maze's continue): `[vr-view] the view tipped 63 degrees down with the game's
      camera, so what it shows is ahead`, and the board ahead in the pictures.
13. [x] **Jak 3: an effect missing in the robot factory, Daxter's second section there** (the tester: "i think there is
    a effect missing at crimson bot factory jak3 daxter section 2nd time you play as daxter in robot facility").
    Which effect is not said. Daxter's sections in the factory are the continues `factory-indax-1` to `-4`
    (engine/level/level-info.gc; "indax" is Jak 3's name for Daxter alone), so the second is likely
    `factory-indax-2` (with `factory-pre-indax-2` and `factory-mid-indax-2`). game-jak3-output-7 has all four of
    Daxter's factory sections (their scenes `factory-indax-1-intro` to `-4-intro`), so the tester's second one is in
    it. Play it on the sim and compare each eye
    with the flat game's picture for what is missing (a particle, a light, a screen effect, a part of the level drawn
    in neither eye). With item 10.

    **Found:** the section (`factory-indax-2`, level factoryc) has, besides its particles and lights, the zappers'
    red glow (`fac-zapper`) and spot shadow effects (`generic-spot-shadow-effect`): effects the shadow renderer draws,
    whose eye pass VR counts differently from the flat game (LIST-0.1.8 item 9: behind what they fall on, not in
    front). Sims s10 fd and fdz (build 128b, `JAKVR_TEST_CONTINUE=factory-indax-2`, Daxter put at a spot shadow
    effect, `JAKVR_SHADOW_EYES=1`: each eye's shadow share every 600 frames, pictures turning about and looking down):
    - fd, as players have it: 17 windows, up to 0.98% of the picture; the eyes within 0.02 points of each other and
      within 0.18 of the flat game's own count of the same volumes.
    - fdz, the flat game's count in the headset: the eyes up to 0.26 points apart (what 0.1.8 fixed).
    - In the pictures the zappers' glow, the lights and the particles are in both eyes alike.

    **No fault found, no change.** The tester did not name the effect and none is missing in the sims. To ask the
    tester: which effect, and where in the section (a picture would tell).
14. [x] **Steer by grabbing the wheel: B for the hover height** (the tester: "when gripping steering on hover vehicles you
    made thumbstick press swap hover zones i personally think B would be more convenient for me. unless i read it
    wrong it says to do exactly what i been doing").
    - Today, while a hand holds the wheel, the right stick's click is the right grip's button: R2, the height in Jak
      II and 3's hover vehicles (CONTROLS.md, LIST-0.1.8 item 6). The tester has been doing that.
    - In the city's cars and bikes, B is already the turbo (CONTROLS.md, "B the turbo"), so B cannot simply become
      the height without losing the turbo. Every button must stay reachable, with no conflicts.
    - Weigh, while the wheel is held: B the height and the stick's click the turbo; or B the height only in vehicles
      with no turbo; or a choice in the options. Keep what a player already knows working (the quick squeeze, the
      stick click), and say it plainly in the help line and CONTROLS.md.
15. [x] **Steer by grabbing the wheel: push and pull to lean** (the tester: "in addition to your manual drive you should
    have pushing forward while gripping steering do forward joystick's action of leaning and pulling back should do
    joystick back's action, in jak 2 its just kinda neat, in jak 3 its important").
    - While the wheel is held, the wheel pushed forward (away from you) works as the left stick forward, and pulled
      back as the stick back: the lean in the air (Jak 3's desert vehicles and jumps, where it matters) and whatever
      the stick's forward and back do in each vehicle. It is added to the stick, as the steering is.
    - Keep it apart from turning. Handlebars turn by pushing one hand and pulling the other; a lean is both hands
      moving forward or back together (one hand: that hand's move from where it took the wheel). Use a dead zone and
      the same easing as the steering, so turning never leans and a lean never turns.
    - Its own toggle, plainly named (for example "Lean by pushing the wheel"), on to start, in the VR menu and both
      launchers. Check it by sim in a desert vehicle's jump and on Jak II's bike, with the stick's own lean
      unchanged.

    **Items 14 and 15, fix (build 123):** two options, on to start, in the VR menu and both launchers: **B for height
    while holding the wheel** (with the right hand on a hover vehicle's wheel B is R2, the height, and the right
    stick's click Circle, the turbo; off, as 0.1.8) and **Lean by pushing the wheel** (both hands 12 cm away or back:
    the left stick forward or back, apart from turning). CONTROLS.md says both.

    **Checked:** sims s09 wl2 (Jak II's city bike) and wl3 (Jak 3's desert buggy): `B with the right hand on a hover
    vehicle's wheel: R2 (the height)`, `the right stick's click with that hand on the wheel: Circle (the turbo; B is
    the height)`, `the wheel pushed 12 cm: the left stick forward (lean 1)`, `pulled 12 cm: ... back (lean 2)`, and
    the turn of 27 degrees still steering with no lean (-0.69 on the bike, -0.39 in the buggy). VrOptionsTest passes.
16. [x] **Jak 3: the floor not drawn in a boss fight, so you cannot tell when it is destroyed** (the tester: "jak 3 erol
    boss? either way floor not rendering so you can't tell when floor gets destroyed").
    - Most likely Errol's boss at the end of the war factory (goal_src/jak3/levels/factory/factory-boss-*.gc). Its
      floor is made of platforms that drop (`fac-drop-plat` and `fac-drop-plat-nocollide`, factory-boss-setup.gc)
      under its floorbuster bombs.
    - **A lead in the code:** `fac-drop-plat-nocollide`'s idle state switches the platform's renderer by the distance
      from `(math-camera-pos)` to the platform. Within the platform's own bounding radius it clears `force-vu1`;
      outside it sets it.
    - In first person the camera is Jak's eyes, standing on the floor and so inside that radius. The platform then
      takes the path the flat game uses only when its camera is that close, and that path may not draw in the eyes.
      Check which renderer each takes in the eye passes, both eyes and the flat picture.
    - Other floors in the war factory switch the same way (`fac-break-floor`, factoryc-obs2.gc). So do any other
      game objects asking `math-camera-pos` for their renderer, in all three games: search for the pattern.
    - Sim: the boss's continue (`factoryd-start` or `factoryd-pre`, engine/level/level-info.gc), pictures of the floor
      before and after a floorbuster in first and third person.
    - If it is not this boss, the tester's next log will say where (the level is in its `GAMEPLAY: enter` lines).
    - With item 10.

    **Later the tester, with a log:** "erol's boss floor has a few pannels of floor but 90% of it is missing i had use
    where amo drops and gun's lazer to figure out where was safe". So it is Errol's boss. The floor's collision is
    there (the ammo lands on it, the laser stops on it) but most of its drawn panels are not.

    **game-jak3-output-7** (C:\JakQuest\downloads\tester-logs-20261120, read-only): the city, the sewers, the maze
    game in Vin's room, the war factory with Daxter's four sections, then the boss (level factoryd, task
    `factory-boss-resolution`). Closed normally, exit code 0, no `[crash]` and no stall.
    - **The strongest lead:** 221 lines of `kmalloc: !alloc mem in heap for #<process @ ...> (336 bytes)` at
      factoryd's birth, just after `Displaying level factoryd` and before `GAMEPLAY: enter factoryd`.
      - The same 221 again at each restart after a death there: six blocks, 1,326 lines in all.
      - Nowhere else in this log, and in none of the tester's other logs.
      - The processes sit 0x6A0 bytes apart, just above factoryd's loaded data, so they are the level's own objects
        born together. Each fails to get 336 bytes of its own heap.
      - 221 failing objects at the one fight whose floor is 90% missing: likely the floor's drawn panels
        (`fac-drop-plat-nocollide`, with no collision of their own, so the collision stays while the panels are
        never made). That fits the ammo and the laser stopping on the unseen floor.
    - **To find:**
      - Which type fails: the line names no type, because the process is not set up yet. Add the entity's name and
        type to that line, or log it at birth, so the log says.
      - What the 336 bytes are (the skeleton or draw-control set-up of `initialize-skeleton`?).
      - Why the object's heap is short. The port's changes to Jak 3 do not touch draw-control or process-drawable
        (only pilot-h.gc among the headers), so look at what our code allocates in each drawn object's set-up.
      - Whether the level's heap is short because of what else is loaded (Max draw distance, the levels kept while
        moving through lfacrm2 and lfaccar).
      - Whether the unchanged OpenGOAL Jak 3 does the same (flat, no VR): if so it is the original port's fault,
        and is still ours to fix here.
    - **The renderer switch above** is the second lead, if the panels do get made.
    - **Sim:** the continue `factoryd-start` (or the boss's), counting `!alloc` lines and picturing the floor in
      first and third person. Done when no allocation fails and every panel is drawn and drops when bombed.
    - The same check after a death there, since each restart failed again.

    **Found:** the boss level's dead pool (factoryd-login, factory-boss-setup.gc) gives each object a 1560-byte heap,
    and the port's kernel keeps a backup stack each process must leave free (gkernel.gc `stack-size-set!`), so the
    floor's drawn panels (`fac-drop-plat-nocollide`, which ask 16) found their heap 336 bytes short and were never
    made; their collision is the level's, so it stood. The pool and the kernel are the port's, VR or not. Another
    pool did the same: Jak 3's port attack (`*port-attack-dead-pool*`, its mines).

    **Fix (builds 122, 123):** 512 bytes more for each in those pools (factoryd-login, lctypatk-login and the port
    attack's two own pools in ctyport-attack.gc and ctyport-attack-bbush.gc); about 111 KB more of the boss level's
    heap, which it has. And `!alloc mem` now names the process, its heap's size and what was left (kscheme.cpp,
    Jak II and 3), so a next such line says which object.

    **Checked:** sim s09 ef (Errol's boss, task 60, build 122): no `!alloc`, the floor drawn ahead, down and behind.
    pa (the port attack, task 38, build 122) still had 40 `!alloc` lines, now naming `ctyport-mine` (heap 1952, 320
    left); pa2 (build 123, the first 40 mines' pool too): none.

## More reports (2026-11-22)

The owner passed on more of the same tester's reports, a Jak II log, and a second player's Jak 1 log and report. The
owner: "take everything they said into account and fix. add proper flawless working vr sections, auto switch sections
like the dark maker robot to 3rd person, hide objects that could obstruct 1st person (only when in 1st person of
course), verify the nicest/smoothest vr implementation for the fishing game, see about fixing whatever flagged the
virus thingy, and yea basically fix/implement/adjust anything else you see fit based on that report." Both logs are
kept read-only in C:\JakQuest\downloads\tester-logs-20261122:
- game-jak2-output-7: the tester's second Jak II save (the city, the port, the Strip Mine, the slums, the palace),
  0.1.8, SteamVR in Meta compatibility mode at 72, an RTX 2080. Closed normally.
- game-jak1-output-12: the second player, a Pimax Dream Air on SteamVR (its driver `aapvr`), 0.1.8, Jak 1's title
  only. Closed normally.

17. [x] **Windows Security took gk.exe for a virus** (the tester: "can no longer play jak vr ... my theory is windows
    mistook as virus and deleted something vital. before it said this the message said something about jak 2 being a
    virus", then "protection history was correct solution"). The launcher said "The release's game\gk.exe is missing:
    unzip the release again". Microsoft Defender quarantined gk.exe while Jak II ran; restoring it from Protection
    history fixed it.
    - Find what in gk.exe looks like malware to Defender's heuristics, and change it where that costs nothing. Candidates
      that 0.1.7 and 0.1.8 added: the stall watch suspends the game's thread and reads its stack (`[vr-stall] the game's
      thread is at ...`); the crash record takes the process's crash filter back each time another part takes it
      (`another part of the game had taken the crash filter`); and the exe has no version information (no product,
      company or description).
    - Give gk.exe and the other exes Windows version information and a manifest.
    - When gk.exe is missing, the launcher says plainly that Windows Security may have quarantined it, and how to
      restore it (Windows Security, Virus and threat protection, Protection history, Restore), before "unzip again".
    - The release notes and the README say the same, in a short "If Windows Security removes gk.exe" section.
    - For the owner (their call, not ours): report the file to Microsoft as a false positive, and code signing.
    - Fix:
      - gk.exe and extractor.exe carry Windows version information: product, company, description and the
        release's version (cmake/JakVrVersion.cmake).
      - When gk.exe or extractor.exe is missing, the launcher says Windows Security may have removed it and how to
        restore it. A missing DLL (the exit code STATUS_DLL_NOT_FOUND) is explained the same way.
      - The README and the release notes have an "If Windows Security removes gk.exe" section.
    - Not changed: the stall watch and the crash record stay. They explain hangs and crashes in the game's own log,
      a standing rule. Code signing and a false-positive report to Microsoft are the owner's call.
18. [x] **Jak II: very laggy in a second save, cutscenes too** (the tester: "exploring my 2nd save of jak 2 and its
    currently very laggy it even affects cutscenes"). game-jak2-output-7 shows:
    - The same PC and options as the tester's earlier logs, which held 72 (game-jak2-output-6: 641 of 645 ten-second
      windows at 70 or more). This one has 659 windows: 425 at 70 or more, the rest at 30 to 69.
    - The slow windows are in the Strip Mine (`strip`) and the city (the port, the slums, the palace), in first person,
      on foot and in vehicles. The render thread's work, xrBeginFrame to xrEndFrame, rose from about 8 ms to 12 to 20
      ms. That is drawing, not the GPU's resolution: the dynamic resolution fell to 60% and it did not help.
    - **56,000 lines of `[vr-body] the eyes leave out merc model daxter-lod0` and `daxter-highres-lod0`**, two a frame,
      from 13:10 to the end. Two of Daxter's models are drawn each frame there, and the log's "told once" memory holds
      only one name. That fills the log; fix it.
    - A 7 s stall at 59:26, the game's thread in a file write (`pc settings file write` and an auto-save).
    - Fix: find what costs the render thread the time there (the sim in the Strip Mine and the port, first person,
      timing the draw per bucket), cut what our VR path adds, and make the log name what took a slow frame's time.
    - Fix:
      - The told-once memory for the models the eyes leave out holds every name, so Daxter's two models are logged
        once (`[vr-body] the eyes leave out merc model daxter-lod0`), not two lines a frame.
      - The world's sprites (the particles bucket, the costliest at the port and in the Strip Mine) are worked out in
        the frame's first eye and kept for the other (Sprite3.cpp).
      - `[vr-perf]`: over each 10 s in which the drawing took over 8 ms a frame, each pass's time and the eyes' six
        costliest buckets, so a player's log names what made a place slow.
    - Checked: sims s11 and s12 pf2s and pf2p (the Strip Mine and the port, first person): Daxter's lines 4 in a whole
      run; `[vr-perf] the drawing took 8.8 to 12.6 ms a frame` on the sim's PC, merc and the particles the costliest.
19. [x] **Jak 3: the orb hunt side missions' scenes do not show in first person** (the tester: "did i report that jak 3
    sidequest orb hunt cutscenes don't work if your playing 1st person?"). The orb hunts open with the game's camera
    showing where the orbs are. In first person the view stays in Jak's eyes, so it is not seen. Show the game's camera
    while the game's camera is not following Jak (as item 12's maze game), in all three games.
    - Fix (jak_vr.cpp `s_camera_elsewhere`, all three games): the view becomes the game's camera's, as in third person,
      while the game holds Jak in play and its camera has stood, for a quarter second, either over 20 m from his
      head or over 4 m from it and more than 75 degrees off from him. After a second without that, the view goes back.
      A talk, whose camera frames Jak, stays in his eyes (First person in talks).
    - Checked: sim s10 orb (Jak 3's orb hunt, build 128b): `[vr-view] the game's camera shows something away from Jak
      while it holds him (186.5 m from him, 146 degrees off its ahead): the view from the game's camera`, then `the
      game holds Jak no more: the view as before`.
20. [x] **Jak 1: the fishing game in VR** (the tester: "will vr fishing be in next update or do you need to try the
    fishing to know how to apply it?"). The owner: "verify the nicest/smoothest vr implementation for the fishing game".
    Jak 1's fisherman's game at the river: the net moved left and right under falling fish. In VR: see it from the
    game's camera, and move the net with the hand (where the hand points along the river, or its side-to-side
    position), smoothed, with the stick still working. Then the owner: "more so making the fishing minigame work in
    first person vr ... motion controls, proper implementation, allat".
    - First person (new option **Fishing game in first person**, on to start, Jak 1; the VR menu and both launchers,
      `fishing-first-person`): you stand on the bridge in Jak's eyes, the river turned ahead of you, the net in the
      right hand (jak_vr.cpp `fishing_net`). Its pole goes where the controller points, lengthened so its basket reaches
      the catch line (its own 3.2 m made about 6.1 m, no thicker: Jak 1's fisher.gc `vr-fish-net!`, pc-vr.gc
      `vr-fish-net-shape!`), and the game's paddle (what catches, within 0.7 m of a fish plus its size) follows where
      the basket is across the river. A catch buzzes the net's hand (a fish lightly, the big one and the eel harder),
      and so does the basket going into the water. The other grip squeezed takes the net in that hand. While fishing,
      the grips and quick hand swings are not punches or kicks.
    - Off, or in third person: the game's camera, the net following your raised hand side to side, smoothed; the
      stick still moves it as the game's own does, and after that the hand takes the net back once it moves 5 cm.
    - The log: `[vr-view] the fishing game from Jak's eyes: the net in your hand, the river turned ahead of you`,
      `[vr-pad] ... the net in the right hand, its pole where the controller points`, `[vr] the fishing game from
      Jak's eyes: the net's pole 6.85 m (its own 3.22 m, 2.13 times, as thick), the catch line 6.59 m across, its ends
      6.09 and 5.92 m from the hand`, then a line each second of play: where the net is across the river, its basket
      against the catch line and the water.
    - Checked (sim s16 fe, build 134): the basket 6.09 m from the hand, 0.05 m off the catch line pointed at it, the
      far left end reached; `[vr-feel] a fish in the net`; `the net taken in the left hand (its grip squeezed)` and
      back to the right; no fists or kicks. The pictures: the thin pole and its basket ahead over the river, in
      either hand. Sim s16 fi (the option off, the game's camera): the net following the raised hand, the stick's
      hold, the left hand raised taking it over. VrOptionsTest 30 of 30 (the new option on to start).
21. [x] **The JET-Board on Jak's back and Light Jak's wings, at the third-person body in first person** (the tester: "the
    hoverboard when put away on your back is attached to 3rd person model when set to 1st person", then "just got light
    wings. they have same problem as put away hover board"). In first person, hide what hangs on Jak's back and would
    float in view (the stowed board, the wings while not flying). With item 22.
    - Fix (jak_vr.cpp, the models the eyes leave out): in first person the eyes leave out the JET-Board while it is on
      his back (Jak II and 3's `board-lod0` when he does not ride it) and Light Jak's wings while he is not flying
      (`jakb-wings-wings-lod0`). Flat and in third person nothing changes.
    - Checked: sims s12 board2 and light3 (build 130): `[vr-body] in first person the eyes leave out board-lod0 (the
      JET-Board on his back)` and `... jakb-wings-wings-lod0 (Light Jak's wings)`, the pictures clean; ridden, the
      board is drawn.
22. [x] **Hide what blocks the first-person view, in first person only** (the owner). Look for each such object in all
    three games (what Jak carries on his back or head, what the game attaches near his eyes) and hide it in the eyes
    while in first person.
    - Fix (Merc2.cpp, jak_vr.h `merc_near_fade`): in the eyes, in first person only, every model that comes within
      16 cm of an eye fades out, dithered, as Jak's own body has since 0.1.1: a vehicle's or the Titan suit's parts
      at the head, a ride's neck, an enemy, a door, a scene's actor. Before, the near plane cut them open to show
      their inside. Another player's Jak, the HUD and third person are as before. With items 21 (what hangs on his
      back) and 24 (the Dark Maker robot to third person).
    - Checked: the first-person pictures of this list's sims (vehicles, turrets, the robot, the board) show no model
      cut open at the eyes.
23. [x] **VR controls for Jak II and 3's turret sections** (the tester: "am looking forward to the update that adds vr
    controlls to the turret sections of jak 2 and 3"). The owner: "add proper flawless working vr sections". Aim each
    turret with the hand (as the guns), fire with the trigger, from the turret's own view; the stick still works.
    - Fix (Jak II and 3's target-turret.gc, Jak 3's cty-blow-tower.gc, jak_vr.cpp `turret_aim`): with Aim with your
      hand on, a gun turret turns to where the right controller points while its first finger is on the trigger, and
      the trigger fires. Moving the stick takes the turret back, as in the original.
    - Checked: sims s12 tt2 (Jak II's drill platform turret) and vgsc (Jak 3): `[vr-gun] a gun turret: it turns to
      where the right hand points while its first finger is on the trigger`.
24. [x] **Jak 3: the Dark Maker robot in first person shows only its inside** (the tester: "dark maker robot 1st person
    can't see anything because all you see is the insides of it"). Switch to third person there by itself (the owner:
    "auto switch sections like the dark maker robot to 3rd person"), and look for other such sections in all three
    games.
    - Fix (Jak 3's pc-vr.gc `vr-first-person-ok?`): never in the mech, Jak 3's Dark Maker robot (lmech), whatever the
      view setting; first person again once out. The other sections: Jak II's Titan suit keeps its own switch
      (**Titan suit in first person**); the gun seats with only an inside to see are the game's camera (item 5:
      the Scorpion's gun, the pickup's gun, cam-power-game, cam-bt-gun, cam-scorpion-gun); Jak 1 has none.
    - Checked (sim s10 ds, build 128b): in the robot through the ship, `[vr-cam] mode 0 flags 0x2251 third person
      view` from the restart at `precura-mech` to the door, the view the game's camera.
25. [x] **The Pimax Dream Air: stuck in Jak 1's title menu, the menu's rows unseen** (a second player: "When I'm in the menu
    in Jak 1 Im kinda stuck in it? Only thing I can do is rexenter using both sticks. 1. if I click on the left stick
    the Jak Logo disappears and thats it 2. cant see the menu options."). game-jak1-output-12 shows **Gamepad only on**
    (`[vr-pad] Gamepad only: the VR controllers are left out of the game`), so the Touch-style controllers never
    reached the game and the title could not be passed. The left stick's click opened the VR menu (the logo went), but
    they did not see its rows either. Their eyes were 5784 x 5298 each (150% of SteamVR's recommended 3856 x 3532) at
    90 Hz.
    - Gamepad only stands aside while no gamepad is connected: the VR controllers play, and the log says so.
    - The VR menu over Jak 1's title: picture it on the sim, at the player's eye size too.
    - The launcher's text for Gamepad only says it needs a gamepad.
    - Fix (jak_vr.cpp): Gamepad only stands aside while no gamepad is connected (the input manager's count): the VR
      controllers play, and the log says which (`[vr-pad] Gamepad only: no gamepad connected now, so the VR
      controllers play`, and the VR controllers left out again once one is). The launchers' text: "With no gamepad
      connected the VR controllers play".
    - Checked (sim s17 gp2, build 135; Gamepad only on and no gamepad, as their file): `[vr-pad] gamepads connected:
      0`, `Gamepad only: no gamepad connected now, so the VR controllers play`, `Start: the left stick's click pressed`
      at the title, then the VR menu opened by the left stick's click held over the title's menu and closed by the
      next click. The HUD's picture has the VR menu's rows (View, Cutscenes, ... Fishing game in first person ...).
    - Not found: why they saw no rows. Both menus are on the HUD, a layer of its own in the headset; their log has no
      error from the runtime (SteamVR/OpenXR 2.18.4 with the Pimax's driver) and its head space agreed with its eyes
      (no `the runtime's head space is ... degrees from its eyes`). The log now says where the HUD's layer is first
      sent (`[openxr] the HUD's layer first sent: <size>, at <place> in the head's space ...`), so their next log
      shows it. Their eyes, 5784 x 5298 each at MSAA 4, are beyond what the sim draws.
26. [x] **Jak 3's turret walkers seem to take too little damage** (the tester: "the health of the walkers in jak 3's
    turret section seem to subtle i'd recommend at least getting a second opinion before doing anything about it").
    The game's own numbers; nothing changes unless the VR aim is found to hit less than the game's own aim. Checked
    with item 23.
    - No change: the walkers' health is the game's own. With item 23 the turret turns to where the hand points and
      fires from its own gun, so the VR aim does not hit less than the stick's.
27. [x] **Jak 3: the Dark Maker ship, a door's troopers never come** (the tester, 2026-11-22: "a squad of enemies that
    must be killed in order for a door to open seemingly don't spawn ... i cant progress"; game-jak3-output-8). The
    tour (task 64, Jak in the Dark Maker robot) in precura: the first door's five troopers (dp-bipedal-109 to 111,
    176, 177) came and were beaten; the airlock door precur-door-b-3 at (-165, 540, -51) opens only once
    dp-bipedal-123 to 126 are dead, and those four never ambushed though Jak stood 9 to 20 m from them for minutes
    (they wake when he comes within 20 m, or 120 m in sight). The log has no birth failure or pool warning. The
    "entity missing actor-group" lines are the laser beams' (precur-laser-beam), as the original prints them.
    - A sim: task 64, warped to the door (JAKVR_TEST_WARP=precur-door-b; the warp now works in the robot too), the
      new watch logging every actor within 40 m (its process's state, or none born, and its perm status) every
      2 s for 30 s.
    - Found (sim s09 ds, build 127c): `"dp-bipedal-123" ... process-drawable-art-error, perm #x2`, 124 the same. The
      four troopers' nav mesh (their `nav-mesh-actor`, 58642) is in precurb, not precura, and a nav enemy born while
      its mesh's level is not active finds none (`get-nav-control`: "no nav-mesh") and stands in the art error for
      good, never fighting or dying, so the door stays shut. Max draw distance (on to start) turns the game's
      `ps2-actor-vis?` off, so a level's actors are all born the moment it is active; precurb is shown a moment after
      precura, and turned off and on again at every restart (the tester's log: "Turning level precurb off" at each
      death, "Displaying level precurb" after). The game said nothing: the art error prints only when entered from
      another state. Troopers 115 to 121 and dp-bipedal-94 share the case (their meshes are in precurb too).
    - Fix (build 128, Jak II and 3's entity.gc `vr-birth-wait?`): an actor with a `nav-mesh-actor` that is in no
      active level waits to be born while a level the game wants is not up yet (then it is born as the game always
      did), logging `[vr] dp-bipedal-123 waits to be born: its nav mesh is in a level not up yet` and
      `... born now, its nav mesh's level up`. process-drawable-art-error now always logs its reason
      (`[vr] <name> stands in process-drawable-art-error: no nav-mesh`), both games. The test warp holds Jak
      at the door until every level the game wants is up (5 s at most): on build 127c he fell through precurb's
      floor before it was shown.
    - Checked (sim s10 ds, build 128b): as precura came up, `dp-bipedal-116` to `121` and `123` to `126` each
      `waits to be born`; the warp held Jak at the door 195 frames until `Displaying level precurb`, then all ten
      `born now`. The watch: 126 `ambush`, 125 `hostile`, 123 and 124 `dormant-aware`, then all four `shield-out` and
      `shield-idle` (fighting the robot), perm #x0, none in the art error. The robot seen in third person (item 24:
      `[vr-cam] ... third person view`). The art error's log, the effects' own: `[vr] ceilingglow-part-6 stands in
      process-drawable-art-error` with no reason, as the original leaves them (worded "no reason given" since).

28. [x] **Swimming in first person: the eyes under the water** (found by our sims: s10 to s14 pf2p, Jak II's port,
    where Jak walks off the dock and swims). Looking down while swimming showed only black.
    - Found: Jak's head sits in the water as he swims, and the eyes are in his head: at the port 3 to 4 cm over his
      water's height, or under it. Two more causes, from sim s19's trace of the eyes' height (build 138): his water's
      height is taken at his feet, but the eyes are ahead of them, where a wave of the city's sea (about half a metre
      up and down) can stand higher; and a wave over his feet sets his under-water flag while he still swims at the
      surface, which let the eyes go (once 1 m under the sea). Under the surface the eyes see the sea's underside,
      which is not drawn, and the dark beneath. A first try (builds 136, 137) drew the sea's near tiles, taking them
      for left out there; the trace showed the game draws them already, and that try was taken out.
    - Fix: while he swims at the surface in first person (his swim states, kFlagSwimming), the eyes are kept at least
      12 cm over the water (jak_vr.cpp, build 133: `[vr-view] swimming: the eyes kept at least 12 cm over the water
      ...`), in all three games. In Jak II and 3 (pc-vr.gc `vr-sea-top`, build 139) the water's height given is the
      highest of the sea's surface at the head and 35 cm round it, at the waves' full height as drawn by the eyes,
      where that sea is the water he swims in (within 1 m of his water's height: a pool elsewhere keeps its own), and
      his under-water flag no longer lets the eyes go (`[vr-view] swimming: a wave round the eyes stands N cm over his
      water's height: the eyes kept over it`). Jak 1 keeps the 12 cm alone (its sea's code differs; no report of it).
    - Checked (sims s20 pf2p, Jak II, and pf3p, Jak 3, at the port's dock, `JAKVR_TEST_CONTINUE=ctyport-start`, build
      139): `[vr-view] swimming: a wave round the eyes stands 43.9 cm over his water's height: the eyes kept over it`
      (Jak 3: 8.5 cm). The swimming pictures, ahead and at the feet in four directions: the sea's surface ahead and
      the arms tinted under it looking down, no black; lit share of each picture 98 to 100% (Jak 3: 95 to 100%), where
      s18's (build 137) had 64% and 59% at the feet and ahead. Both exit 0, no `[crash]`.

## Reports with no change (thank you)

- Jak II: first person again once the feet touch the ground after a swing pole, "much better now" (LIST-0.1.8 item 14).
- Jak II: "holding gun in hover car works great" (LIST-0.1.8 item 5).
- Jak 1: "i got the trouble powercell its fixed" (LIST-0.1.8 item 2), "shadows look fixed" (item 9), "1st person
  steering on jak 1's zoomer is good" (item 6), and "jak 1 looks 99% finished to me".
- Jak 3: Light Jak's heal "absolutely works".
- All three games: "shadows appear to be fixed accross all 3 games" (LIST-0.1.8 item 9).
- game-jak1-output-11: a normal game (from the final boss and the citadel back through the lava tube, Volcanic
  Crater, Snowy Mountain, Sandover and the jungle), exit code 0. Its 68 lines from the skeleton's guard are LIST-0.1.8
  item 10's fix at work; nothing else of note.
