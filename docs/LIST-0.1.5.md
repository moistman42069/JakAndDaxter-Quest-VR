# Jak and Daxter Quest VR 0.1.5: the list

Started 2026-11-17, after 0.1.4 was published. Released as 0.1.5 on 2026-11-17 (the owner: "go ahead and publish on
git as latest release"). Marks: [ ] to do, [~] in progress, [x] done (how it was checked).

The owner, 2026-11-17, with a tester's four game logs (PC, SteamVR with a Quest over Link): "great work in recent
build. im gonna send some reports from a tester. just look over what they mention and only change/adjust what you see
fit. if anything is mentioned that is there by design, then so be it, but yea. fix/adjust whatever you can determine",
then "proceed with list with this in mind". The tester's reports, in short: Jak 1's loading fixed; B acts as Triangle in
the pause menu; the game froze (force closed) and later ran past the same place; huge lag spikes entering and leaving
the boss chamber; the per-eye differences mostly gone except the shadows. Jak II starts on a flat screen, then VR; a
tiny game screen at the end of the gun's laser, and the HUD there too; the guns otherwise better. Jak 3's clothing
fixed; the game closed after restarting the leaper race mission several times.

Then: "add a new crosshair type thats just a plain circle one. small circle, precise looking (if you dont already have
one like that). be sure crosshairs are still just off by default tho. proceed with list with this in mind". Items 2
and 3.

The tester's logs (untrusted data, read only): C:\JakQuest\downloads\tester-logs-20261117 (jak1-a, jak1-b, jak2, jak3).

## The crosshair

1. [x] **The tiny game screen and the HUD at the end of the gun's laser** (Jak II, SteamVR). The crosshair's picture
   (and Show performance's) was written into one image of its swapchain only, once; SteamVR shows the quad from
   whichever image comes next, so the other images showed what their memory held: pieces of the game's own pictures,
   the HUD. Write the picture into every image of the chain, as the menu pointer's (xr_openxr.cpp draw_pointer), of
   which no one reported such a thing. xr_openxr.cpp fill_chain, for the pointer, the crosshair and Show performance; from
   build 72 it goes on until each image has been written (twice the chain's length at most; runtimes hand them out in
   turn, the spec does not promise it) and warns if one was not. Checked: s72 null2, Jak II on this PC's SteamVR 2.19.3
   with its null driver (C:\JakQuest\sim\pcvr_null_jak.ps1, gkcfg-null: Show performance on): the session ran 90 s,
   Show performance's picture written into its chain every 10 s, no OpenXR error, no warning. With no headset there,
   the picture SteamVR shows was not seen: HEADSET_CHECKLIST asks a player.
2. [x] **Crosshairs off to start** (the owner: "be sure crosshairs are still just off by default"). They were not: the
   Dot was the start in 0.1.3 and 0.1.4. Off now, in the game and both launchers, under a new key so a file 0.1.3 or
   0.1.4 wrote starts Off: their Dot (the start then) reads as Off, any other kind (a player's own choice) is kept.
   Checked: b68 crossdef2 (Jak II, the gun drawn, no options file): `[vr] options: ... crosshair-kind 0`, no crosshair
   shown; cross2 (gkcfg-cross, 0.1.3's `crosshair 3`): `crosshair-kind 3`, the Cross drawn. The launchers' VrOptions
   (JDK 17, a scratch test): no file 0, old 1 (the Dot) 0, old 3 kept, old 0 0, new 7 7, new 0 with old 3 0, the
   old key dropped; the label "Small circle".
3. [x] **A small circle** (the owner: "a plain circle one. small circle, precise looking"). The Circle there is wide
   (1.3 degrees across at 100%) with a thick line. New kind Small circle: a thin ring about half a degree across at
   100%, a thin dark edge. In the VR menu and both launchers. Checked: b68 ring2 and ring3 (gkcfg-ring, kind 7 at
   100%): drawn where the shot lands in Jak II and 3. Its line was 0.07 degrees wide first; rendered alone at 14, 20 and
   25 pixels a degree (box-filtered as a mipmapped compositor, and bilinear) it broke up at 14, so it is 0.09 now
   (crosshair.h ring 0.045, rim 0.045), whole at all three. Again on build 69 (ring2, the 0.045 line): `[sim]
   crosshair: kind 7 at 23.27 m ..., 0.813 m across` (its picture 2 degrees across, the ring half a degree in it), and in
   the shot.

## Tester's reports

4. [x] **The game frame rate flips between 72 and 36 a second** (all four logs: Jak 1 up to 26 times a second, Jak II
   438 changes in a run). Automatic follows the headset's rate each frame, and SteamVR reports a doubled period for
   any frame it missed; each flip changed the game's frame rate (Jak 1: "Tried to set unsupported video-mode" and the
   graphics reset each time). The headset's rate is followed only once it has held for a second (jak_vr.cpp RateHold,
   kRateHold). Checked: blips1 on builds 68 and 69 (`jak1_play`, `QUEST_SIM_PERIOD_BLIPS=7`, a doubled period told
   every 7th frame): `[sim] a doubled frame period told every 7 frames`, then one `[vr-view] the game at 72 frames a
   second (the headset shows 72)` and no other change in the run. And on SteamVR itself (s72 null2, its null driver at
   90 a second, this PC missing 300 to 580 displays every 10 s, as the tester's): one `[vr-view] the game at 90 frames
   a second` and no change in 90 s.
5. [x] **Jak 1's freeze** (jak1-a, the temple, Forbidden Jungle's inside): "WARNING: too many sound commands queued",
   then "STALL: waiting for IOP on RPC port #0" and nothing more from the game; the headset kept showing the last
   frame. The sound processor stopped answering the player's commands, and with its queue full the game waited for it
   forever (rpc-h.gc add-element's flush). A full queue never waits forever: a short wait, then the commands that do
   not fit are dropped (a sound missed, not the game stopped); the log says so and says what filled it. All three
   games' rpc-h.gc (add-element's port 0, vr-rpc-wait, *vr-rpc-scratch*), gsound.gc (when the IOP answers again, the
   dump). Checked: b71 sstall1 (`jak1_play`, `JAKVR_TEST_SOUND_STALL=5`: the IOP's sound player held 5 s some 20 s in,
   and the game sends 300 sound commands once the player has been busy 30 frames): "too many sound commands queued"
   and the queue's dump (3 plays, fire-pop, fire-pop and fly7, and 125 other commands, 124 of them one run: the
   test's volume commands), then "the IOP has not answered
   the sound player for over a quarter of a second: 1 sound commands dropped" (and at 10 and 100), the game on at 60
   frames a second through the hold (the sim's frames 1500 and 1800 five seconds apart), and once the player answered
   "the IOP answers the sound player again: 179 sound commands were dropped"; no stall. b70's sstall1 (the hold alone)
   did not fill the queue: a held player gets only the commands of a few frames. The dump wrote each command's name as
   0 (Jak 1's sound-command->string is in the debug build only): enum->string now. s72 sstall1 (build 72): the dump
   `0 to 0 "set-flava"`, `1 "play" fly7`, `2 to 127 "set-master-volume"`, then as before (177 dropped).
6. [x] **Lag spikes at the boss chamber** (jak1-b): game frames of 3.0 s (after the plant boss's music "danger"), 0.5
   s, 1.9 s and 10.8 s ("Unload soundbank jungleb", the display held 6.65 s too). Nothing in the log says where the
   time went. A stall watch: when the game or the display stops for over a second, the log says where each thread is.
   With item 4 the graphics are not reset on each rate change either. The cause of the tester's spikes is not found
   (not reproduced here): the watch is for their next log. Checked: s72 gstall1 (`jak1_play`,
   `JAKVR_TEST_GAME_STALL=7`: the game's thread held 7 s at its 1500th frame): `[vr-stall] the game has not finished a
   frame for 1.0 s: it is held by the test (JAKVR_TEST_GAME_STALL); the display is between frames (0 frames since);
   the IOP: start waits (sleep), Server waits (sleep), Player waits (sleep) ...`, the display's own line, both again
   at 5.0 s, then `the game went on after 7.0 s` and the display's. No `[vr-stall]` line in any other run of b68 to
   s72. The display waits for the game's next frame, so a game stall holds the headset's picture too (the
   compositor's own reprojection meanwhile), as in the tester's log.
7. [x] **Jak 3 closed after restarting the leaper race** (the pause menu's restart, the third time): the log ends at
   "Load soundbank wascity3", before the reset (the first restart: "ERROR: object #x187e01 is not a valid object
   (misaligned)"). The PC wrote no crash record. Try the same in a sim; the PC writes a crash record always (the
   game's log flushed, what crashed and where) for the next one (crash_dump_win.cpp install_crash_record).
   Found: b69's second task3m (jak3_menu_restart.sim, four restarts through the pause menu as a player does) crashed
   at the third restart, right after "Load soundbank wascity3" as the tester's: `[crash] the game crashed: exception
   0xc0000005 in gk.exe+0x420340`, `a read of the game's memory at 0x0`. gk.exe+0x420340 is in
   TexturePool::handle_upload_now (llvm-objdump and the rdata strings; no PDB): a texture page uploaded during the
   restart held an entry of 0, and the game read the texture there (a PS2 reads address 0 harmlessly; on the PC that
   page is protected). The guard (TexturePool.cpp): the page must lie in the game's memory, its length and name too;
   an entry that is 0, outside the game's memory or misaligned, or a texture with more than 7 mips, a place past the
   pool's 16384 or its name outside, is passed over as #f is, and the log says so (`[vr] a texture page the game
   uploads has a bad entry ...`, the first 3, then every 100th). Checked: task3m on build 70 (four restarts), task3m8
   and task3m8b on build 71 (eight each): all 20 restarts done, no crash, and the guard not reached either: the bad
   page did not come again, so these runs say only that the guard changes nothing else (before it, one run restarted
   through the menu, b69's second: b69's first pressed left before A and never restarted). So the guard itself: s72
   badtex3 (`jak3_actions`, `JAKVR_TEST_BAD_TEXTURE=40`: the 40th page's first entry read as 0, as the crash's page
   held): `[vr-test] page 40's first entry read as 0`, `[vr] a texture page the game uploads has a bad entry (a
   texture outside the game's memory: entry 0, 0x0): passed over (1 so far)`, during the desert's load; the load went
   on, the title and the run's actions played, and the game shut down as asked: no crash, no new error line.
8. [x] **Jak 1's shadows differ per eye.** The game makes them once, from between the eyes, and each eye moves them to
   its view; the rest of each eye's picture is drawn for that eye. A known limit for 0.1.5 (release notes' Known
   limits): drawing each shadow twice is the game's shadow code run per eye, not a fix of a few lines.
9. [x] **By design, no change:** B is Triangle (back) in the games' menus, as in the VR menu (0.1.3: Jak 1's menus take
   Circle as Cross, so B started a new game or wrote over a save). Jak II starts on the flat panel, as each game's
   loads and blackouts do, until the game's first picture in play.

## The logs

10. [x] **Precursor's debug lines** ("(AI-assisted) art lookup ... matched ...", "manipy-init ...") were two lines in
    five of Jak 1's log (977 and 2640 in the tester's runs of 13 and 19 minutes). Removed. Checked: b68 blips1 (Jak 1,
    jak1_play): no art lookup or manipy-init line; one "(AI-assisted) HUD manipy" line a run is left (2 in the
    tester's runs together).
11. [x] **The VR options in the game's log** at the start, so a log alone says what the player had on. vr_options.cpp:
    `[vr] options: ...`, 12 a line, when they change (load_options runs twice at a start, the headset's and the game's:
    one list). Checked: b69 ring2, b70 act1 and act3, b71 sstall1: 7 lines once in each run, `crosshair-kind` as set.

## Texts

12. [x] What's new, the release notes, CONTROLS (the crosshair kinds, B in the menus), HEADSET_CHECKLIST, tests/README.

## Before publishing: the owner's and Vixel's reports

The owner, 2026-11-17, as 0.1.5 was about to be published: "actually, good thing you didnt publish. i just got more
feedback. fix before anything:" (the owner's own play, a few missions past Jak 3's lizard race, and Vixel's), then "be
sure to update the controller section to have controls shown for each game as well. make sure theres NO control
conflicts and all controls are able to be accessed and used accurately on the vr controllers. proceed with list".

13. [x] **Effects put a colored square in front of your face** (the owner: "jak 2 and 3 have issues with certain
    affects putting a colored square in front of your face, in jak 2 lightning an certain impacts, in jak 3 1 of the
    red guns puts a black 1 out every time you shoot it"). A VR frame draws the HUD panel's pass first, then each eye.
    Three things drew a whole screen into the HUD panel, opaque where the panel is see-through: (a) the game's
    darkening of the whole screen (OpenGLRenderer's blackout quad: Dark Jak's change, 45% with his lightning; a scene's
    or a death's fade; the stopwatch), black with its alpha written: a dark rectangle ahead. On the panel it now dims
    what the HUD has, color and alpha alike (the eyes are darkened by their own passes). (b) Jak II's screen-space
    particles (Sprite3's HUD group) are also its flashes over the whole screen (an explosion's white flash and glow, the
    ecowells' flash, 6 to 15 m across on a screen of about 15 by 11): white or colored rectangles on the panel. Those of
    4 m and over are drawn in the eyes, over the whole view, the small ones (the rain's drops) stay on the panel. (c)
    BlitDisplays' zoom blur (Jak 3's Wave Concussor and Plasmite RPG, the dark gun), slow time's trail (Light Jak's
    freeze) and the copy back after a menu ran after the first pass, the panel's, drawing a copy of the screen there
    (RGB, so opaque): the black square at each red gun shot. In the eyes they would need each eye's own last picture,
    and a zoom or trail of the whole view is what VR leaves out anyway: left out in the panel's and the eyes' passes, the
    flat screen keeps them (`[vr-view] the game's zoom blur is left out of VR`). Checked, Jak 3: s75 eff3
    (tests/jak3_effects.sim, `JAKVR_TEST_GUN=all`, build 75): each red, yellow, blue and dark gun fired at his feet and
    ahead, pictures 2 to 40 frames after: no black area in either eye beyond the gun's own dark parts (under 2% of a
    picture near-black, the same in both eyes), the panel pictures only the HUD (under 5% drawn, none of it a
    rectangle); the log's `zoom blur`, `slow time's trail` and `copy back after a menu` lines; s76 dark3: Dark Jak's
    change, the panel only the meter. Jak II: s76 eff2 (tests/jak2_effects.sim in the fortress, build 76): Dark Jak by
    the grip's quick squeeze (`target-darkjak-get-on`), his lightning, his punch and his bomb (`target-darkjak-bomb0`),
    then each gun fired at his feet and ahead: the panel pictures only the health and ammo meters (under 2.3% drawn,
    in their corner), no square in either eye (under 4.0% near-black, scattered: his arms, the gun's dark parts), his
    claws and the bomb's light in the pictures. (Its Dark Jak part was 400 frames later than first written: s75 eff2's
    squeeze came during the load into the fortress, before play.)
14. [x] **Jak 3: Dark Jak's button** (Vixel: "the 1 that makes you dark jak seems not to work, in version 1 it was
    left grip, now it doesn't seem to work"). Since 0.1.3 L2 (Dark Jak) was the left stick's click on foot, the left
    grip being the left fist; on SteamVR with Touch that click is the menu button, and L2 needed the click with the
    grip squeezed, which nothing showed. 0.1.5 gives the left grip L2 again: a quick squeeze let go with nothing else
    pressed is L2 for 0.3 s (Jak II's Dark Jak on the press; Jak 3's on the let-go of an L2 held a tenth of a second,
    target-lightjak.gc target-powerjak-get-on); squeezed again within that time and held, L2 held, no fist (Light Jak's
    powers with a face button or the right stick). Not with the gun in the hand (a squeeze missing the gun's front would
    put it away): the click is L2 there. And the click's own L2 (jak_vr.cpp fill_pad): it began after the click's
    three-frame hold-back (to see a recentre coming) and ended with the click, so a quick click gave Jak 3 less than
    its tenth of a second (he stood again, `target-powerjak-get-on` then `target-stance`), and a click let go within
    the hold-back gave nothing: now the click's L2 lasts 0.3 s at least (`[vr-pad] a quick click of the left stick: L2
    held 0.30 s`). The Jak 3 test aid kept Dark Jak's meter empty in a new game (it filled it only when giving Dark Jak,
    which a new game has), so the sims could not turn him at all (s75b dark3, build 75: every L2 `target-powerjak-get-on`
    then `target-stance`, the grip's too); `JAKVR_TEST_GUN=all` now keeps both meters full while he is neither.
    Checked: s76 dark3 (tests/jak3_darkjak.sim at templea-start, build 76): a 6-frame click `L2 held 0.30 s in all`,
    `state target-darkjak-get-on`, his claws and their lightning in the pictures; a 2-frame click (inside the
    hold-back) `target-darkjak-get-off`; a 10-frame click Dark Jak again; back; the grip's quick squeeze
    `target-darkjak-get-on`; back; the grip squeezed, again and held, B: `target-lightjak-get-on`, his blue hands. The
    HUD panel's pictures: only the meter, no dark rectangle while he is Dark Jak (item 13). s75 eff3 (build 75): Light
    Jak by the grip, again and held, X: `target-lightjak-get-on`, `target-lightjak-freeze`.
15. [x] **Jak 3's sky z-fights** (the owner: "its hardly notice able only really happens if you look at it just
    right"). Jak 3's and Jak II's skies are drawn first with a set depth: their clouds and the sky's base at 400 in
    the game's 24-bit units (sky-tng.gc, the far plane at 100, more is nearer), so that the farthest scenery, drawn
    after with depth tested, lands above 400 and hides it. In first person each eye's near plane is 356 (jak_vr.cpp
    frustum, for hands and guns close to the eyes), not the game's 1024, and a scene's depths above the far plane shrink
    in the same ratio, about a third: the farthest hills came out at or under the sky's 400 and fought with it, more at
    some angles (the eyes see them a little apart). DirectRenderer.cpp: the sky's set depths (above the far plane,
    under 4096) are scaled by the eyes' near plane over 1024 when it is nearer, 400 to 204 at 356; third person and the
    flat game keep 400. Jak 1's sky has its own renderer, not touched. Checked, build 76 (`JAKVR_SKY_TRACE=1`,
    which now also traces Jak II's sky-draw and says the depth after scaling): s76 sky3 (tests/jak3_sky_desert.sim at
    wasdoors-desert-turtle, the head turned round in steps and up) `depth 204 to 204 ... near 356 (the eyes)` for the
    clouds, the base and the fog in first person, `depth 400 ... near 1024` in third person; s76 sky2 (Jak II on the
    JET-Board at ctysluma-start) the same, 204 in first person and 400 while riding in third person. The pictures
    before and after (s75b, build 75) differ in under 1.1% of each, at edges and moving things (vehicles, sparks):
    the sim's views, the garage at night looking out its gate, did not catch the fighting itself, as the owner says it
    shows only at some angles.
16. [x] **A Jak II boss does not shoot at you** (the owner: "its supposed to shoot in your general direction but it
    felt like it ignored me"), widened by "fix all jak multiplayer conflicts in single player". Found: Jak II's
    multiplayer mod (on to start) lets only a game's host decide some things, and out of a multiplayer game every
    player slot is empty, so "the host?", "a guest?" and "in a game?" are all false: each "only the host" check failed
    in one player's game. The squid boss's aim (mp-palace-squid-sync.gc: every clause wanted a host or a guest, so it
    aimed at its arena's middle) and its ending scene (squid-states.gc); the widow's spydroids, bombs and ending
    (widow.gc); the burning bushes' mission prompt (ctywide-obs.gc); the guards taking the Kid and Kor (kid.gc, kor.gc),
    the Kid's escort course and the guards' transports (hal3-course.gc, transport.gc); enemy authority (the hoppers and
    others held back: mp-enemy-authority.gc) and vehicles (vehicle-replica.gc). Each now asks "a guest?" (false alone)
    or the new mp-session-solo? (player.gc). The other host checks (castle-baron, vehicle.gc, dig-digger, the gun
    course, metalkor, under-shoot-block, scene.gc, task-control, the races, strip-obs, mountain-obs) send to the network
    or are inside a multiplayer game only: left. Jak 1 (Teamruns): its per-checkpoint hook and the crate smack skip ran
    with multiplayer merely on; now only in a game (*vr-party-on?*, mod-custom-code.gc, effect-control.gc). Checked:
    s74 pturn2 (Jak II, multiplayer on): `[vr] test: multiplayer game #f, host #f, guest #f` (pc-vr.gc's state trace),
    so the solo paths are the ones taken alone.
17. [x] **The controls for each game, no conflicts, every one reachable** (the owner, quoted above): CONTROLS.md (both
    launchers show it) per game, and every game button the games read in play reachable on every controller.
    CONTROLS.md's "Each game": a table for Jak and Daxter, Jak II and Jak 3, each button on foot and in each ride, from
    each game's code (the launchers bundle the file at the release build: build_pc_release.py). Found and fixed: (a)
    Select and L3 had no button on foot in Jak II and 3 with the fists on the grips (the stick clicks are L2 and R2
    there; Jak II's multiplayer player list is Select, its names and HUD while held L3): the left hand held by the
    head makes the clicks Select and L3, everywhere (jak_vr.cpp fill_pad, head_clicks). (b) The Vive wand (no left
    stick click, no thumb rest) had no Select anywhere: with the hand by the head its left touchpad's click with no
    direction in is Select, on a direction only that direction (no X or Y meanwhile); the Quest's controllers and the
    other families have the stick click (pad.h `left_click_sensed`): unchanged. (c) Jak 1's pause pages' Circle (the
    Options) and Jak II's multiplayer menus' Circle: item 22. (d) Dark Jak's L2 hidden behind the click with the grip
    squeezed on SteamVR with Touch, and too short for Jak 3 by a quick click: item 14. Each controller button with two
    uses is told apart by how it is used (a quick squeeze or a held one, on foot or riding, the hand by the head or
    not). CONTROLS corrected: Jak II does nothing with Select alone (main.gc toggle-pause turns it away without the
    debug segment; Jak 1 and 3 pause without the menu), Jak 1's warp gates read the d-pad (not Select), and L3 is not
    "needed by none" (Jak II's multiplayer). Checked: s77 hclick2 (tests/jak2_head_clicks.sim at ctysluma-start,
    build 76): the hand by the head, `[vr-pad] L3: the right stick's click with the left hand by the head`, `Select:
    the left stick's click ...`, no pause from it (Jak II), Start the pause menu; s78 hclick2 (build 77, the wand
    part: `no_left_click 1`, `no_thumb_rest 1`): X in the touchpad's middle `Select: the left touchpad's click with
    the left hand by the head`, X pushed right `d-pad right (the left stick)` alone, no punch from either.

Then, the same day: "fix all jak multiplayer conflicts in single player. this is for jak 1 and jak 2 since both have
multi currently" and "I don't want multiplayer to conflict with single player at all ... Single player works fine,
multiplayer works fine"; a player's "can you make vehicle types seperate 1st person toggles, for example hover car,
hover bike, lizard, hover board?" with the owner's "keep the first person everything option ... see what you see fit
to adjust/add"; "see about getting cross platform/multiplayer compatibility with PC/flat players who use the multiplayer
mod as well ... Research this and determine how plausible it even is before doing anything" and "dont publish anything
yet, when your done with everything on the list, gve me a breakdown on what you discovered for crossplay and whether it
makes sense to implement it or not"; and a report: "jak2 i failed a mission and now every time i approach it acts like
i trigger the cutscene but it don't trigger and the mission don't start".

18. [x] **First person for each kind of ride** (the player's ask; First person in vehicles kept as the switch for them
    all). Zoomer, Flut Flut, JET-Board, hover cars, hover bikes, Titan suit, leaper lizards, desert vehicles and other
    rides (slides, turrets, the glider), each its own switch, on to start, shown in the games that have it (VR menu and
    both launchers). Each game's pc-vr.gc vr-ride-kind puts the kind in the camera flags' bits 16 to 19 (jak_vr.h
    RideKind); jak_vr.cpp ride_first_person. (Jak II and 3's vr-ride-kind reads what Jak pilots through
    vr-pilot-vehicle in pilot-h.gc, built after pc-vr.gc.) Checked, build 75: s75 boardon (tests/jak2_board.sim at
    ctysluma-start, all on): first person on the JET-Board; boardoff (gkcfg-fpoff, `first-person-jet-board off`): `view
    in play: third person (the game's state: a vehicle, a ride or a scripted move)` as the board starts, first person
    again as he gets off; zoomoff (Jak 1 at misty-bike, `first-person-zoomer off`): third person on the Zoomer, its
    camera flags 0x10151 (ride kind 1, the Zoomer). Each option is listed at the game's start (`[vr] options: ...
    first-person-jet-board off`).
19. [x] **Jak II: a failed mission's scene never played again** (the report above). The multiplayer mod passes over a
    scene of the same name as the last one played (a guest's copy of one it already had); that name is let go only by
    the mod's per-frame scene hook, which runs in a multiplayer game only. In one player's game, the mod on or off, the
    name stayed, so after a mission's intro had played, failing and walking back started the same scene, which was
    passed over at once: Jak held a moment, no scene, no mission. scene.gc: in a multiplayer game only. Checked by
    the code (a mission failed and walked back to is not scripted in a sim): with mp-session-in-game? false alone, the
    scene player's aborted? is #f, as the original game's.
20. [x] **Cross-play with flat players of the original multiplayer mods** (research only, as asked; the breakdown goes
    to the owner at the end of the list). Found: Jak 1's original multiplayer is Teamruns: its own desktop app
    (Electron) joins players through Teamruns' online service (WebRTC, signalled through their hosted database) and
    feeds the game; this port's Jak 1 parties speak their own lines of JSON straight between players (TCP 8120, UDP
    8121, tools/party_server.py), with no account or service. Jak II's original mod finds and joins games through
    Steam (Valve's GameNetworkingSockets and Steam lobbies); this port's Jak II plays over plain TCP and UDP (26210,
    LAN finding on 26211), so the Quest, which has no Steam, can join. To play with the originals each would need
    their transport: Teamruns' service (theirs to allow; not ours to sign in to) and Steam's (the Steamworks SDK, a
    Steam account on each PC, none on the Quest), and their message formats kept in step with each of their releases.
    Neither format has a place for a VR player's head and hands, so the flat players would see VR players as plain
    Jaks at best. What already works: this port's PC build plays flat without a headset (`[vr] no headset`, the game
    on the monitor with a pad or keyboard), joins the same parties as the VR players and sees their heads and hands
    (docs/MULTIPLAYER.md "Friends without a headset", 0.1.5). The recommendation: not to build cross-play with the
    original mods; flat friends use this port's PC build. No code changed for this item.

Then a tester's answer to item 9's two "by design" points: "starting non vr and pause menu behind me are still issue",
"the non vr issue also happens every time a cutscene starts and ends".

21. [x] **The flat panel at the start and around scenes** (Jak II above all). A blackout (the game's mode 4: a scene
    starting or ending, a mission retried, a warp, a load) kept the eyes black for 1.5 s, then went to the flat panel
    until it ended: the tester's Jak II log has ten "a blackout past 1.5 s (a load): the flat panel" lines, each 3 to 6
    s (a scene's start, a death in the palace cable mission). The game's start (before its first camera) and a blackout
    from the title went to the flat panel at once. Now the eyes stay black for the whole blackout and at the start, as
    the flat game shows black then; the flat panel only with the VR menu open (it is drawn there) or after a scene the
    player put On a screen. jak_vr.cpp blackout_in_eyes; the log says each blackout's frames and seconds. The game's
    frame is still drawn during it, flat into the panel's picture and not shown, so its work is done under the black
    as 0.1.4 did on the flat panel (build 74 drew nothing: s74 pturn2's first frame in play after Jak II's title held
    the game 1.5 s, `[vr-stall] ... the display is drawing the eyes`, the new level's first draws).
    Checked: s74 and s75 pturn2 (tests/jak2_pause_turned.sim: Jak II from its start through the opening's skips):
    pictures at frames 1200 and 1800 the title's intro in the eyes; `a blackout kept in the eyes, black: 4 frames
    (0.1 s)`, `2 frames`, then the load into the fortress `959 frames (17.9 s)`, all black in the eyes with no panel
    (the sim's `eyes 1 panel 0`, the pictures black), then play with the fortress textured as ever; s75 (build 75, the
    hidden draw): no `[vr-stall]` line. s74 pauseb1 (Jak 1, new game to Geyser Rock): four blackouts of 1 to 10 frames,
    black.
22. [x] **B in the pause menu** (the tester's first report: "the button that should be circle is acting like triangle
    in pause menu"; item 9 had it by design). Found: Jak 1's pause opens on its power cell page, where Circle opens
    the Options (progress.gc respond-progress; X the orbs page, Triangle the scout flies page): with B as Triangle in
    every menu since 0.1.3, the Options could not be reached by a button there. And Jak II's multiplayer menus read
    Circle (the lobby's options and address, the lobby list's Join by address, the look editor's other character):
    out of reach too (item 17). B is now Circle where a menu has a Circle of its own (each game's pc-vr.gc
    vr-menu-circle?, the camera flags' kFlagMenuCircle), Triangle elsewhere (Jak 1's options and save menus take Circle
    as Cross). "pause menu behind me": the pause menu is on the HUD, which follows the head (on in all four of the
    tester's logs: none says "the HUD stays put"); the sims pause with the head turned 150 degrees to be sure.
    Checked: s74 pauseb1 (tests/jak1_pause_b.sim, build 74): the head turned 150 degrees, Start: the power cell page
    (its "Circle OPTIONS") on the HUD panel, the eyes on the world behind; B: the Options (Game, Graphic, Sound, VR
    Options, Load, Save); B again: back to the page; Y: the scout flies page; Start: play. s74 pturn2 (Jak II): the head
    turned 150 degrees, Start: the pause menu (Missions) on the HUD panel, the world in the eyes. s75 joinb2
    (tests/jak2_join_b.sim): the title's Multiplayer, Join Game: the lobby list ("No games found", its prompts "X Join,
    Square Refresh, Circle Manual"); B: the Manual page (Join with Invite, Direct Connect), which no button reached
    before; Y: back.
