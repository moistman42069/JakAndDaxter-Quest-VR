# Jak and Daxter Quest VR 0.1.7: the list

Started 2026-11-18, after 0.1.6 was published. Marks: [ ] to do, [~] in progress, [x] done (how it was checked).

The owner gathered players' reports and logs on 0.1.6 first ("i wanna gather more logs and reports before anything
else"), then: "lets get to work now. proceed with list". And, standing from now on: "the logs should just indicate a
crash and be as detailed as can be going forward" (item 13). A tester's logs are kept read-only in
C:\JakQuest\downloads (tester-log-20261118-jak1, tester-log-20261118-jak3, tester-logs-20261118-b); all PC, SteamVR's
OpenXR in Meta compatibility mode 2.19.3 (a Quest through SteamVR), NVIDIA 617.42, 72 frames/s.

1. [x] **A Multiplayer page in Jak II's pause menu.** Jak 1 got Multiplayer in its title screen and pause menu in 0.1.6
   (LIST-0.1.6 item 22). Jak II's title has had one since 0.1.3, but its pause menu only has the mod's settings, so
   there is no host, join, players or leave while playing, and the VR menu's party rows are Jak 1's only. Reuse the
   mod's Host Game, Join Game and lobby pages plus Players and Leave the game; nothing when "Jak II multiplayer" is off
   in the launcher's Mods. A one-player game stays the original until a game is hosted or joined.
   Done: the pause ring's Game Options has **Multiplayer** as its second row (progress-static-pc.gc; the ring is the
   game's 10-slot model, so it takes no more). Outside a session: Host Game (the mod's Load Game, host settings and
   lobby, hosting one of the player's saves) and Join Game (the mod's lobby browser); in a session: Players (the mod's
   player list) and Leave the game (Yes / No, then the mod's disconnect and title). No row with "Jak II multiplayer"
   off. B from it goes back to Game Options with Multiplayer still lit, not its first row (progress-generic-pc.gc,
   build 97). The log: `[vr-mp] pause Multiplayer: Host Game, Join Game` / `in a session (Players, Leave the game)`.
   Checked: pmp1 (jak2_pause_mp: the row, its page, B back with Multiplayer lit), pmp0 (the mod off: no row) and mp2
   (Jak II's host and guest on this PC: the guest's pause Multiplayer in the session, Players and Leave the game).
2. [x] **An option to turn off eco effects in first person** (the owner: "option to turn off eco effects in 1st
   person"). While Jak holds eco, Jak 1's target-powerup-process (engine/game/powerups.gc) launches sparks on random
   joints of his body every frame, the head and neck too, so in first person they burst at the eyes. The game skips them
   only in its own first-person look-around. Plain name, VR menu and both launchers; Jak II's and 3's Dark and Light Jak
   sparks the same.
   Done: **Eco sparks in first person** (VR menu, both launchers; on to start). Off, while the view is Jak's eyes the
   camera tells the game so (jak_vr.cpp, pc-vr.gc `*vr-eco-sparks-off?*`, set each frame) and the sparks off this
   player's Jak are left out: Jak 1's eco (powerups.gc), Jak II's Dark Jak (target-darkjak.gc), Jak 3's eco, Dark and
   Light Jak (powerups.gc, target-darkjak.gc); the eco's sound, its touch and the rest stay, and the game's camera shows
   the sparks. The log says each change (`[vr] eco sparks off Jak's body: left out ...` / `shown again`). Checked: spark0
   (jak1_eco_fall with it off: left out in his eyes, shown again in third person, no error).
3. [x] **Jak 1: platform riders follow Teamruns' rule only in a party.** `add-rider!`
   (engine/collide/collide-shape-rider.gc) keeps the last rider slot for Jak and lets him on when every slot is taken,
   writing past the platform's rider list. Alone: the original's `(< num-riders allocated-riders)`; Teamruns' rule only
   with `*vr-party-on?*` (LIST-0.1.6 item 20's pattern).
   Done (build 97): alone, the original's rule; in a party, the last slot still kept for Jak, but never past the
   list's end (`(< num-riders allocated-riders)` first). Checked: rider1 (Jak 1 alone on Rock Village's elevator down:
   Jak from 5.2 m to -142.3 m on it, `GAMEPLAY: enter sunken`, then walking off; no error, no `[crash]`, no `max
   number of riders`) and mp1 (Jak 1's party, host and guest: joined, progress sent, no error, no `max number of
   riders`).
4. [x] **Pole swings: third person until Jak lands** (a tester: "when pole swinging there should be a on by default
   option that return to 1st person only after feet touch ground ... it kinda throws off where i am aiming when
   perspective swaps back to 1st before i land"; the owner: on by default). First person is refused only in the five
   pole states, so it comes back mid-air after the jump off. Hold third person until his feet are on the ground
   (swimming, a death, a ledge, another pole, a scene or a time limit end it too). Option in the VR menu and both
   launchers, on to start; off keeps 0.1.6's behaviour. All three games.
   Done: **Third person after a pole until you land** (VR menu, both launchers; on to start). Each game's
   vr-pole-hold? (pc-vr.gc) holds from a pole state until his feet are on something, or water, a ledge, a death, a
   continue, a grab, a scene or 6 s; jak_vr.cpp first_person_now keeps the game's camera while it holds. Checked: pole1
   (on: A late in the swing threw Jak off backwards, the game's camera through the fall, his eyes again as his feet
   touched, Jak at the ground's height) and pole0 (off: his eyes as he flipped off, mid-air, then a ledge grabbed).
5. [x] **The crosshair Off to start again, and Jak 3's HUD at the gun's aim point** (a tester: "jak 3 issue hud at end of
   gun's aiming point again"; turning the crosshair off fixed it; the owner: "make it so crosshair is off by default
   again ... fix it anyway however you can"). 0.1.6 started the Dot under "crosshair-shape", and its launchers wrote
   `crosshair-shape 1` into every options file: a new key again, 0.1.6's Dot read as Off, other choices kept. And find
   what still lets the crosshair's layer show stale pictures (0.1.5 item 1's fill_chain), in Jak 3 above all.
   Done: the key is `crosshair-mark` (vr_options.cpp and VrOptions.java): it wins; else 0.1.6's `crosshair-shape` 2 to
   7 kept (its 1, the Dot it wrote for everyone, and 0 read as Off), else 0.1.5's and 0.1.3's keys as before. The
   stale pictures: 0.1.5 wrote every image of the crosshair's chain once at each change, and a runtime with copies of
   its own behind a GL chain (SteamVR's) may keep more than it lists, so the crosshair's and the menu pointer's
   pictures now go into the next image every frame they show (xr_openxr.cpp fill_chain one_image), as an app that
   draws each frame does. Checked: VrOptionsTest (21 checks: fresh files and each older key); crossdef3 (Jak 3's gun
   on fresh options: `crosshair-mark 0`, no crosshair drawn), x016a (a 0.1.6 file's `crosshair-shape 1`: Off) and
   x016b (`crosshair-shape 3`: kept and drawn, `kind 3 at 10.02 m`). The layer's pictures on SteamVR are for a
   player's headset to confirm (HEADSET_CHECKLIST).
6. [x] **Jak 1: stuck after a high fall with blue eco until an enemy hit him** (a tester: "i fell from high area fueled
   by blue eco was unable to move until a enemy hit me, could be normal, but might be worth mentioning"). The log
   (tester-log-20261118-jak1): 2,737 `ERROR: touching-list::get-shapes-entry() failed!` from about [35:4x] on, after
   repeated `[vr-body] the game's skeleton left his root by 1-5 m` with Jak at about 836, 251, -3241 (Snowy Mountain).
   Find the states, who fills the touching list, compare with the original, fix. Worked with item 12 (same place).
   Done: the "stuck" is the original's painful landing (`target-hit-ground-hard`, target2.gc): a fall of over 30 m
   costs a pip and takes control for about 6.2 s while Jak lies there and gets up, with or without eco. In first person
   that looked like being stuck, so it is a third-person state now in all three games (vr-first-person-ok?). The errors:
   blue eco's touch-tracker (generic-obs.gc) touches every orb an orb cache lets out (30 and more), which filled Jak
   1's touching list (32 pairs); it is 64 pairs now (collide-touch-h.gc, its pool 128, the PC's handle array 128), and
   when it is full anyway the log says how often and who fills it. Checked: hardland1 (jak1_hard_land, a 30 m drop at
   Snowy's fort: `state target-hit-ground-hard`, third person while he is down, then his eyes as he walks) and ecofall3
   (jak1_eco_fall, blue eco by the orb caches there: no `get-shapes-entry` line, no error). Seen, not this item: after an
   enemy's hit at the fort's gate his root moves 10-19 m in a frame (the tester's log and every run, before and after);
   the body's guard holds his model there.
7. [x] **Jak 3: the game closes entering the forest** (a tester: "jak 3 crashed entering the forest", then "jak 3 forest
   crash seems consistant"; two logs). Both stop about 2 seconds after `GAMEPLAY: enter foresta`, at the start of the
   same mission (its music forplkil, level lforplnt), just as the trewnd2L sound stream starts. No `[crash]` line and no
   assert in either (item 13). The first log's 154 `MERC2 out of bones` lines are not in the second, so not the cause.
   Done: the mission's first frame draws 1,459 KB into the game's buffer for a frame's drawing, which held 1,280 KB, and
   the game stops itself on purpose then (no exception, so nothing was logged). The buffer is 5 MB now, with Jak 3's
   global heap 16 MB bigger for it; the log says the most a frame has used. MERC2's bone lines are written the first
   time and each 1,000th after. Checked: forest1/forest2 (the close, as the tester's) then forest3 (`the frame's draw
   list: 1459 KB of 5120 KB used`, the mission plays on, no crash or stall).
8. [x] **Jak 3: Light Shield may not work** (the same tester: "light heal worked ... seemed like light shield didn't
   work but could be user error"). Our chord (0.1.6 item 12): the left grip held, Y pressed, then B (Circle) or the
   right stick for the shield. Check Jak 3's own rule for the shield (button, meter), that B reaches the game in time,
   and that the help line and CONTROLS.md say it plainly.
   Done: Jak 3 keeps Light Jak's shield up only while its button (Circle) stays held (target-lightjak.gc), and our
   chord sent B as one press, so the shield fell at once. Now B held keeps Circle held, and the right stick pushed
   right keeps it held while it stays there (jak_vr.cpp); the log says when the shield is asked for and why it falls
   (`[vr] Light Jak's shield asked (light eco N, it needs 3.0)`, `... down: "Circle let go"`). CONTROLS.md and the
   help line say "hold". Checked: shield3 (jak3_light_shield: by B, up while held, down as B was let go, light eco
   100 to 86; by the stick the same, 86 to 71).
9. [x] **A Gamepad mode: only the gamepad plays, the VR controllers left out** (the owner, from a tester: "is there a way
   to implement just the gamepad... either my vive wands are broken or its just difficult to execute a roll with
   them"). Today fill_pad (game/vr/jak_vr.cpp) adds our buttons to a real pad's and our sticks take its sticks' place
   off their middle (a Vive wand's touchpad touched counts), and gestures still fire. An option in both launchers and
   the VR menu, off to start: on, the controllers' buttons, sticks and gestures are left out, the head still tracked, the
   game's rumble to the gamepad. Also the roll on a Vive wand.
   Done: **Gamepad only (no VR controllers)** (VR menu's Controls, both launchers; off to start). On, fill_pad leaves
   the controllers' buttons, sticks, gestures and hands out of the game (jak_vr.cpp); the headset still turns the view,
   the gamepad's right stick turns in first person, the game's rumble goes to the gamepad alone, and the VR menu takes
   the gamepad too (and the controllers while it is open). The log: `[vr-pad] Gamepad only: the VR controllers are
   left out of the game`. The roll on a Vive wand is the game's own rule, L1 while the stick is off its middle: the
   left trigger with the thumb on the left touchpad away from its middle (CONTROLS.md). Checked: pad1 (on: the
   controllers' presses never started the game, the head still turned the view) and pad0 (off: the same presses
   started it).
10. [x] **Jak II: the pause menu still behind the player** (a third report, on 0.1.6: "pause still behind me in jak 2").
    0.1.5 item 22 called it by design, 0.1.6 item 9 put each frame's HUD where the head is on PC. Find what still puts
    it behind (a HUD left in the room, a pose the runtime shows differently, the Quest build, the flat panel), fix,
    and check with the head and the stick turned, on PC and the Quest build.
    Done: it was Jak II's own. Its pause menu is the HUD's words plus a ring, a 3D model (skel-hud-ring, its ten icons
    around it, the option lit in green) the game draws in the world. The multiplayer mod's set-ring-position (in
    one-player games too) put it in front of the game's own camera (`camera-pos`, `camera-matrix`), and in first
    person that camera stands metres behind Jak looking the way the player faces (the tester's log: the eyes at
    755.9, 11.7, -715.7, the game's camera at 758.1, 14.3, -727.1), so the ring was behind them while the words stayed
    ahead. The tester's 15 pauses in Jak 1 and 5 in Jak 3 on the same SteamVR said nothing: Jak 3's ring is put in
    front of the view (`*math-camera*`, in VR the head) and Jak 1 has none. With a headset, Jak II's ring is now put and
    turned from the view as Jak 3's (progress.gc set-ring-position and update-progress-camera-facing-quat!, build 98);
    without one, as before. The same GOAL runs on the Quest. Also kept, as a safeguard: should a runtime's head space
    turn more than 30 degrees from its eyes, the head-following layers are put from the eyes and the log says so once
    (`[openxr] the runtime's head space is N degrees from its eyes ...`; xr_openxr.cpp). Checked: pturn3 (build 97,
    before: the ring in the eyes only with the head turned 150 degrees, toward the game's camera), ring1
    (jak2_pause_ring, build 98: the ring centred ahead of the eyes paused ahead and paused with the head turned 150
    degrees, the HUD's "Missions" at its middle, gone once closed), pstick4 (after a stick turn: ahead; the head
    turned 60 degrees while paused: still centred) and null17 (SteamVR's null driver: its head and eyes together, the
    guard silent).
11. [x] **Jak II: the stadium race closes the game as Jak gets into the racer** (a tester: "jak 2 race mission crashed
    game as soon as i got in vehicle ... jak 2 race crash seems consistant"; two logs). Both: the race loads (stadiumb,
    lracebf, `starting race-manager`), Jak walks to the racer in first person, and the log stops as he gets in (the
    body's `stretch of his limbs undone (his hips' scale 1.40)`). No `[crash]` line, no assert (item 13).
    Done: the multiplayer mod's pilot-on code read the racer's minimap icon, which a one-player race never makes; it
    checks for it now, as the mod does everywhere else. Checked: race1 (the close, as the tester's) then race2
    (`vehicle::event-handler: pilot-on`, `target-pilot-start`, `target-pilot-get-on`, then driving the race, no crash).
12. [x] **Jak 1: black screen after loading a save at Snowy Mountain** (the same tester: "after loading screen still
    black"). The log: Load Game, snow and village3 loaded, `GAMEPLAY: enter snow`, then 42 minutes with Jak at
    837.62, 219.03, -3270.89 never moving, 1-2 ms of work a frame, the eyes' place still in the room's own numbers, and
    `Swapping in sno VIS` only at shutdown. Snowy's load never finished.
    Done: the game swaps in a level's last part (its VIS) only once its camera is inside the level, and in first person
    the camera is Jak's eyes, which stood outside it at that save's place. The levels now load from where the game's own
    camera is. Checked: snow1/snow2 (the black screen, as the tester's) then snow3 (Continue at snow-fort: `Swapping in
    sno VIS`, `snow is loaded`, Jak walking in the pictures, no stall).
13. [x] **Every crash and close shows in the game's own log, as detailed as can be** (the owner: "the logs should just
    indicate a crash and be as detailed as can be going forward"). Three closings (items 7 and 11) left no `[crash]`
    line: game/vr/crash_dump_win.cpp catches the exceptions a vectored handler sees, and an assert writes before its
    abort, but abort and terminate, the C runtime's fast fails (invalid parameter, pure call, stack cookie), heap
    corruption, a stack overflow with no stack left, a hang, and a GPU or VR runtime loss leave nothing. Make each leave
    a record: what, where (module and offset, the GOAL function when in game code), the game's state (level, Jak's
    state and place, the last few things done), and on a hang which thread is stuck. The Quest's report too.
    Done (game/vr/crash_dump_win.cpp, jak_vr.cpp/.h, main.cpp): abort, std::terminate, the C runtime's invalid parameter
    and pure call, a stack overflow (written from a thread of its own) and the exceptions each end the log with a
    `[crash]` record: what, the thread, the module and offset, the registers, the calls innermost first with the game's
    own functions named (`function 'display-loop' +0x16ef`), and `the game was:` (the game, each thread's place and
    frames, the view, Jak's place). The crash filter is taken back each second if another part takes it. A hang: from 1
    s, `[vr-stall]` says which thread and where it is stuck, the calls named. The Quest's crash.txt gets the same `the
    game was:` line. Checked with JAKVR_TEST_CRASH=av, abort, stack, terminate, invalid and JAKVR_TEST_GAME_STALL
    (crash-av ... crash-stall): each a full record or the stall lines; the Quest's compiled (android_native_check.sh).
