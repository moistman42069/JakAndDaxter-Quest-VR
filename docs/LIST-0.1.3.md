# Jak and Daxter Quest VR 0.1.3: the list

Started 2026-11-16, after 0.1.2 was published. Marks: [ ] to do, [~] in progress, [x] done (how it was checked).

The owner, 2026-11-16, after the performance pitch (LIST-0.1.2 item 3): "just do everything on that list. they all seem
to be beneficial. then just leave on the ones you see best for performance by default. do what you see best man, but
do EVERYTHING that can boost performance and have them as options in the launcher. also, new reports from recent
release: \"jak 1 hud is fixed / but render issue of certain objects being rendered slightly different still a issue\"
\"load still seems like it starts new game in jak 1 / but body size more comfortable to wear\". proceed with list with
this in mind. if anything, make sure on menus that the stick can properly navigate anything needed and avoid misinputs
n such. just see what works best. proceed with list". Then: "just make the fuckin pointer as accurate and responsive as
possible and interact with every settings/etc options properly. no clue why dat woudl b fohkin hard lol. proceed with
list". Then: "someone reported things looking better in jak 1, mostly due to being able to see more due to the better
body. im not sure how accurate that is, they seem equal to me, but double check and be sure body ik, smoothness,
function, etc, all works good and equal. regress nothing, proceed with list." Then (a player: "jak 3 clothing that used to be double rendered now attaced to head",
"but guns are fixed / yellow gun projectile source not gun but goes where i aim gun"): "make sure all body/clothing
details work well and dont obstruct view/etc. make sure guns look accurate in regards to muzzle flashes, lighting etc.
fix whatever else you can piece together from those reports, etc. proceed with list". Then (the same player): "clothing
attached to screen instead of character". Then: "there IS a jak 2 multiplayer mod by the way. basically implement it
similar to our openrac/jak 1 work already done and refine whatever else you see fit for compatibility. heres the repo
https://github.com/himstan/Jak-And-Daxter-2-Multiplayer proceed with list with this in mind". Then (a player): "there is a
manditory minigame in jak 3 i am not as close as i should be to it, its not a problematic distance just feels wrong";
the owner: "not sure what they are referring to, but just check incase".

Players' reports on 0.1.1 (relayed 2026-11-16): Jak 1's HUD fixed (LIST-0.1.1 item 9); the body's size more comfortable
(item 39, Life size). Still: objects drawn slightly differently in each eye (item 2 here), and Load Game in Jak 1
(item 1 here).

Confirmed fixed by players (relayed 2026-11-16; keep them so, "dont regress there"): going up and down on vehicles
("being able to go up and down on vehicles is fixed"), the HUD ("1. and hud"), Jak II's destroyed turret no longer
crashing ("destroyed turret game still on"), Jak 3 no longer crashing at the tutorial ("jak 3 doesnt crash at tutorial
anymore it seems").

The owner, later the same day: "make sure any other meshes on the bodys similar to jak 1s hand straps have proper
physics applied. make sure they also fade similar to other parts of the body so it doesnt obstruct view tho" (item 17);
a player: "jak 2 has z fighting on some billboards, / in both 3 and 2 dark jak body squishy" (items 18, 19). And: "be
sure everything can work properly, do things in a way that avoids conflicts/regressions, and just make it all work as
best as possible" (reviewing and resolving the multiplayer merge is this port's own job, not the owner's).

**Released as 0.1.3, 2026-11-16** (the owner: "i want you to work through EVERYTHING and just publish it as a new
release"). Everything here that a PC with the simulated headset can do and check is done. Left for the headset or a
player's log, carried to the next list and named in the release notes' Known limits: the Quest's own numbers for
items 5 to 8 (Show performance on in a log), and item 24 (not reproduced; the log now says when the view is upside
down and still). NOTES-0.1.3-DRAFT.md, named below, is merged into docs/release-notes.md.

## Menus

1. [x] **Jak 1's Load Game still seems to start a new game; the stick and the pointer in every menu** (the reports and
   the owner's words above).
   Found (2026-11-16):
   - **B was Circle in the games' menus, and Jak 1's menus take Circle as Cross** (progress-pc.gc: "pressed x or
     circle. advance!"). So B, the Quest's "back", picked the highlighted row: New Game on the title, a save on Load
     Game, Yes on "write over this save?". Jak II and 3 take Triangle for back and do nothing with Circle.
   - **The dot took the row back from the stick on any move of about a degree** (0.1.1's fix, kPointerMoveUnits 8):
     the stick down to Load Game, then the thumb going to A turned the hand a degree, the dot (still on New Game, where
     a resting hand points) chose New Game again, and A started a new game.
   - A push down a little to one side also went left or right (changing a setting) in Jak 1's menus, which take the
     stick as their d-pad.
   - The pull of the trigger turned the hand, so a click could land on the row next to the one aimed at.
   - **The root of "Load Game starts a new game" (sim b56 load1, build 51, after every fix above): Teamruns' run rules
     for the saves were always on** (mod-game-handler.gc *enable-multiplayer-save-and-load* #t since the merge in
     0.1.0). A run shows one file, and a file loads only once saved in that run (*player-can-load*); otherwise choosing
     it asks "A Jak and Daxter save game already exists ... overwrite?", and Yes writes the game as it is (on the
     title, a new game) over it. In b56 the stick and A reached Load Game and the file, and the game asked to write
     over it.
   Done in the code (not built yet):
   - Teamruns' save rules only in a Teamruns run: *enable-multiplayer-save-and-load* #f, set #t by its app's
     mark-connected (mod-custom-code.gc). Outside a run the game's own four files load, as the original game's.
   - jak_vr.cpp fill_pad: in the games' menus (title and pause menus) B is Triangle, back, in all three games (and what
     closes the VR menu over them is kept from the game as Triangle). Jak II and 3 never read Circle there.
   - Each game's menus choose a row by the dot only as it **comes onto** that row, well inside it (4 units off its
     edges), never while it stays there: Jak 1 progress-pc.gc vr-ptr-enters? (rows of each screen, the save files,
     Yes / No sides, On / Off sides), Jak II and 3 pc-vr.gc vr-ptr-enters? and vr-ptr-moves-row? (progress.gc rows,
     the PC pages' rows, every two-way line). So the stick's row stays the stick's until the hand points at another.
   - The dot is smoothed (a "1 euro" filter: still when the hand is, at once when it moves; kDotCutoff 1.6 Hz,
     kDotBeta 12) and the click goes where the dot was as the trigger began to move (kPullBegins 0.12), what is drawn
     and what the game gets alike. "Moved" is 2 units again (it only drives what follows the dot).
   - Every kind of setting takes the pointer (the row being changed): On / Off and the like, the side the dot comes
     onto, a click on the other side sets it, a click on its own side is Cross; Jak 1's sliders, a click on the bar
     takes it and it follows the dot while the trigger is held (flag 8, new), a click off the bar is Cross; any other
     (language, frame rate, resolution, display mode, Jak II and 3's lists and sliders): a click on the left or right
     third is a step that way, again every 0.09 s after 0.4 s while held (pc-vr-pointer-side! -3 / 3, kSideRepeat), the
     middle is Cross. A click taken as a step is no Cross (s_pointer_click_spent), and a click is read once.
   - Menus: a push mostly one way is that way alone (Jak 1: only the stronger way; Jak II and 3: the weaker dropped
     under 0.45 of the stronger, so a map's diagonal still works).
   To check (sim): Jak 1 title, the dot resting and shaking on New Game, the stick down to Load Game, A: the files
   screen; a file, A: loads (and B there goes back); B on the title does nothing; Jak 1, II and 3 settings: each kind
   of row by the pointer; the pointer sims (ptr2, ptr3, scroll1, scroll2, stick1) as before.
   Checked: b57 and b62 load1 (Jak 1's title, Load Game, the file, A: the save loads, Jak at its place, no question),
   b62 title1 (Jak 1's title by the left menu button and A), b63 ptr3, scroll1, scroll2, stick1 and ptr1slots (no
   errors; the held stick moves a page row by row, the pointer's click reaches the game), b65 ptr2 and ptr2off (Jak
   II's title with Multiplayer on and off, item 15). Still to see: each kind of settings row by the pointer (On / Off
   sides, Jak 1's sliders, the thirds' steps) in pictures.
   Found more (Session 18):
   - **A long page opened with the hand at rest moved on by itself** (b75 ptrcal1, tests/jak1_ptr_settings_cal.sim:
     Jak 1's Game Options, 10 rows, 6 shown; the resting hand's dot lies on the bottom row shown, and the edge's
     auto-scroll, called for whatever row the dot was on, moved the page on to Back: "[vr-ptr] the page moves on a
     row: 6 ... 9 of 10" with no hand move; A there went back out). Jak II's and 3's long settings pages did alike.
     Fixed (build 61): the edge's scroll only for the row the dot chose on that page (a move onto it, a click, or a
     move onto the arrows past the rows) and the rows the scroll moves on to; never the stick's row or a resting dot's
     on a page just opened (Jak 1 progress-pc.gc vr-ptr-edge-take! / vr-ptr-edge-mine?, Jak II and 3
     progress-generic-draw-pc.gc vr-ptr-generic-take! / vr-ptr-generic-mine?). b76 ptrset1: Game Options opened at
     rest, its first row (Input Options) chosen, no page move in 3 s; b76 scroll1 and scroll2: the dot moved onto the
     bottom row, under or over the rows still moves the page as before (Jak 1 and II).
   - **Jak 1's list rows took no pointer while being changed** (b76 ptrset1, tests/jak1_ptr_settings.sim: Language
     being changed, the clicks on its right third, left third and middle each "found no row of the game's menu"). Such
     a row (language, frame rate, display mode, resolution and the like) draws its own carousel and leaves the row's
     text empty, and draw-options offered the pointer only rows with text. Fixed (build 62): the row being changed is
     always offered. (On / Off rows and sliders keep their text and were not affected.)
   - Seen working (b76 ptrset1): Jak 1's Play hints by the pointer: a click (its ON OFF chooser), the dot onto the
     right half (OFF, no click), a click (done: "Play hints: Off"); a click, the dot onto the left half (ON), a click
     (done: "Play hints: On").
   - **Each kind of settings row by the pointer, all three games, in pictures** (b77, build 62/63, on the settings
     copy C:\JakQuest\sim\gkcfg-ptrset; batch28c ptrset1, ptrset2, ptrset3):
     - Jak 1 (tests/jak1_ptr_settings.sim): Game Options opened at rest stays on Input Options; Play hints as above;
       Language: a click (its carousel), the right third a step (English to Francais), the left third back (English),
       the middle done ("Language: English", the settings written); B back, the stick to Sound Options, SFX volume: a
       click (50), the trigger held on the bar where the dot is (49), the hand turned right while held (88), let go
       (88), a click off the bar (done, written).
     - Jak II (tests/jak2_ptr_settings.sim): Subtitles (On / Off) a click, the right third (On), the left (Off), the
       middle (done, Off); the stick down three rows (the page moves on one) and the dot onto Subtitle Language: a
       click, right (Francais), left (English), middle (done); Sound Options' Music Volume: a click, the trigger held
       70 frames on the right third (0.40 to 0.59, the steps repeating), 70 on the left (0.42), the middle (done).
     - Jak 3 (tests/jak3_ptr_settings.sim, the stick to Options): the same rows, Subtitles toggled and back, Subtitle
       Language English to Suomi and back, Music Volume 0.40 to 0.57 and back to 0.40, each done by the middle.
     Every click logged where it went ("a step that way" or "Cross"), none "found no row".
   The aims (b75 calibration, the dot's place logged as u v of the panel): Jak 1's rows about 5 degrees of hand tilt
   apart; pitch atan((0.35 + (0.5 - v) * 1.26) / 1.3), yaw atan((0.2 - (u - 0.5) * 1.68) / 1.3).
   Note for the owner: a save written over in 0.1.0 (Load Game then started a new game in that slot) loads as that new
   game; a player whose save was lost then would see "a new game" from Load Game in any version.

## Seeing the games

2. [x] **Some objects look slightly different in each eye** (still, after 0.1.1; LIST-0.1.1 item 7: Vixel, "left eye
   seems bad version while right eye looks fine ... mostly precursor objects but a few lanterns also", all three games).
   Found (2026-11-16): the shiny background (envmapped TIE: Precursor walls and doors, lanterns, in all three games)
   takes the camera's place from its own matrix (cam_no_persp), which the eye shift did not move: each eye drew it
   from between the eyes, off from everything around it. The other background renderers (tfrag, shrub, hfrag) take
   the shifted camera. Also (item 13): Jak 3's generic models after a prim bucket had no eye shift at all.
   Done in the code (not built yet): Tie3.cpp init_etie_cam_uniforms shifts that matrix for the eye.
   To check (sim): Jak 1's Geyser Rock door (tests/jak1_eyes_door.sim), Jak II and 3 Precursor rooms: both eyes alike
   but for the eyes' shift.
   Checked: Jak 1 b56 and b62 eyes1 (the door and the shiny lantern alike in both eyes), Jak II b63 eyes2 (the tomb),
   Jak 3 b64 eyes3 (the temple's carved doorway, ahead and looking up: alike but for the eyes' shift). In the notes.
3. [x] **The body in Jak II and 3 as good as in Jak 1** (a player: Jak 1 looks better, "being able to see more due to
   the better body"; the owner: "be sure body ik, smoothness, function, etc, all works good and equal. regress
   nothing"). Compare the three games' body settings, fades and what blocks the view, on the simulated headset.
   Checked (2026-11-16, build 54: sims b57 act1, b61 act2 and act3, the same moves in each game: kicks, punches,
   jumps, a duck, the high jump, the uppercut, the long jump and the dive): equal.
   - The same code poses all three (jak_vr.cpp body() is driven by each game's named joints); what differs by game is
     his eye height (2.3 game metres in Jak 1, 2.4 in II and 3, his own) and Jak 1's wrist straps (only his have them).
     The sims' options are the defaults in all three.
   - Fades: the same ones in each (his body and legs at the roll and where the game turns him over, each leg at the
     dive), coming back as he rights himself.
   - What blocks the view: nothing of him but the hands and forearms in any game's pictures. The log's nearest joint
     before the eyes in II and 3 is the JET-Board's mount (joint "board": Jak II 25, Jak 3 37, 15 to 30 cm under the
     eyes), which carries none of his mesh (the board is drawn only while ridden); in Jak 1 only an ankle once.
   - Steadiness (the most a joint jumped in a frame against the frames either side): the knees 80 cm in Jak 1's dive,
     79 in Jak II's, 78 in Jak 3's (the game's own dive animation in each); elbows 16, 15 and 10 cm at most, the
     hands under 0.2 cm except one 5.7 in Jak 3, the chest 7.7, 21 (once, Jak II's dive) and 2.7. On a par.

13. [x] **Jak 3: clothing "that used to be double rendered now attached to head"** (a player's report on 0.1.1, relayed
    2026-11-16, with "but guns are fixed", then "clothing attached to screen instead of character"; the owner: "make
    sure all body/clothing details work well and dont obstruct view/etc"). Jak 3's scarf, shoulder cloth and the like:
    follow his body, never the view, never in the way.
    Found (2026-11-16): Jak 3's cloth (cloth.gc) is drawn as prim strips, through the generic renderer's prim buckets,
    whose matrix takes world points straight to the screen (full_matrix). The eye shift in generic.vert was made of
    the projection's numbers, which a prim bucket leaves 0: the cloth was drawn alike in both eyes, so it sat at the
    screen's depth, on the view ("attached to screen"). Worse, the "full matrix" flag a prim bucket set was never
    cleared (upstream OpenGOAL's too), and the generic renderer is shared by both kinds of bucket: every Jak 3 generic
    model after a prim bucket was drawn without the eye shift too.
    Done in the code (not built yet): generic.vert takes the world's shift through the full matrix (vr_shift_world,
    Generic2_OpenGL.cpp); Generic2_DMA.cpp clears the flag for the other kinds of bucket. Light trails, vehicles'
    trails and the sewer's laser turret (prims too) are fixed with it.
    To check (sim): Jak 3 in Spargus in first person and third: the scarf on him in both eyes, left and right pictures
    apart by the eyes' shift; in first person, whether it comes in the way (then a near fade, as his body's).
    Found more (2026-11-16, sim b60 introa on build 52, and Vixel's 0.1.2 Jak 3 log: "the HUD's models in bucket 420 /
    405 / 425 / 370: drawn on the HUD"): the HUD check (Generic2_Build.cpp process_matrices) took every fragment whose
    matrix's last number is not 0 for a HUD model, and a prim bucket's full matrix never has 0 there, so the VR HUD
    pass took Jak 3's cloth (Veger's coat in the intro, Jak's own in play) and drew it on the HUD panel: stuck to the
    view, without the eyes' shift. Done in the code (not built yet): a full-matrix bucket's fragments are never the
    HUD's. To check (sim): introa again (no cloth on the panel pictures, Veger's coat on him in both eyes), and Jak 3 in
    play (no "HUD's models" lines for 370, 405, 420, 425).
    Checked (build 54): the HUD panel's pictures, lit pixels counted in each. Jak 3's intro seen around you (b62
    introa against b60 on build 52, 71 pictures each): build 52 had Veger's coat on the panel in the last 23 (from the
    scene's 49th picture on), build 54 none, the subtitles and the rest the same; his coat on him in the eyes' pictures,
    upright. In play (b62 gun3 against b54, before the fix): Jak's own collar cloth was on the panel looking down; now
    the panel holds the game's HUD alone (b61 act3 too). No "HUD's models" line in any Jak 3 run of build 54 (b54's
    gun3 had buckets 430 and 405, b60's intro one).
14. [x] **The yellow gun's shots do not start at the gun** (the same player: "yellow gun projectile source not gun but
    goes where i aim gun"; the owner: "make sure guns look accurate in regards to muzzle flashes, lighting etc. fix
    whatever else you can piece together from those reports"). Each gun's shot, flash and light from the gun in the
    hand, in Jak II and 3.
    Done in the code (not built yet): the game reads its muzzle (gun joint 13) in the gun's :post, before the body
    poses the gun in the hand, so the shot started where the game's own animation holds the gun. jak_vr.cpp keeps the
    muzzle's place from the right controller's aim pose each time the body poses the gun (s_gun_muzzle_local), and
    hand_shot answers 3 with that point (posed in the last 0.25 s); gun-util.gc (Jak II and 3) vr-gun-aim! then puts
    fire-point and laser-point there, so the shot, its flash, its light and the laser start at the muzzle in the hand.
    To check on the sim: gun3 pictures (the flash at the barrel), both games.
    Seen (b62 gun3, build 54, Jak 3): the flash at the barrel's mouth of the gun in the hand as it is drawn and fired,
    the shot's blast going out ahead of it and its light on the floor under the muzzle; two rounds fired. Jak II's
    (gun2) is the same code (gun-util.gc vr-gun-aim! in each game) and runs in the next batch.
15. [x] **Jak II multiplayer** (the owner, 2026-11-16: "there IS a jak 2 multiplayer mod by the way. basically implement
    it similar to our openrac/jak 1 work already done and refine whatever else you see fit for compatibility";
    LIST-0.1.1 item 13). The mod: [Jak and Daxter II Multiplayer](https://github.com/himstan/Jak-And-Daxter-2-Multiplayer)
    by himstan, ISC licence (as OpenGOAL), v0.3.1 of 2026-11-14: co-op, up to 8 players (Jak and Daxter as characters),
    late joining, races, vehicles, traffic and enemies kept the same for everyone, a lobby, skins and name tags.
    Found (2026-11-16, a partial clone in C:\JakQuest\downloads\jak2-multiplayer): next to its base (OG-Mod-Base) it
    changes 548 of Jak II's GOAL files (+70,000 / -20,000 lines; a new goal_src/jak2/multiplayer), adds a native
    multiplayer layer (game/multiplayer, 17,000 lines) and, under it, Valve's GameNetworkingSockets with protobuf, UPnP
    (miniupnpc, libnatpmp) and Steam's lobbies. Its packets are its own (a bit-packed codec), carried in GNS's lanes.
    Plan, as Jak 1's (Teamruns' game side, this port's own parties): its game side (GOAL and the native game layer)
    built into Jak II on this port's own games list, hosting, joining, party server and LAN (party.cpp), its packets
    carried there instead of GNS (so nothing new to build for the Quest), every VR feature for everyone and the others
    shown with their VR head and hands; on to start, changing nothing until a game is hosted or joined. Games are
    between players of this port (as Jak 1's with Teamruns): its own release's players only.
    Staged (2026-11-16, not in Precursor yet): C:\JakQuest\sim\edits013\mp_integrate.py writes C:\JakQuest\sim\mp_stage
    (files/, delete.txt, report.tsv): 549 new, 346 taken, 26 merged clean, 19 with conflicts, 2 deletions; the
    developer tools, Steam, GNS, tests and the merc format change left out (see HANDOFF-2026-11-16 Session 11).
    The mod's own transport (session_platform.cpp) is GNS: to be replaced by the port's own TCP (reliable lanes) and
    UDP (realtime lanes) one, same interface; steam_client and steam_lobby as stubs (no Steam); its LAN discovery
    (UDP 26211), direct connect, invites and UPnP (miniupnpc, libnatpmp: plain C) kept.
    Applied to Precursor (Session 12), the transport written. **Each multiplayer off in the launchers** (the owner,
    2026-11-16: "make sure the multiplayer mods can be disabled in the launcher. just to make it simple incase of
    conflicts"; Session 13): both launchers' Mods have "Jak 1 multiplayer" and "Jak II multiplayer", on to start, each
    the "multiplayer" key in its own game's vr-options.txt (VrOptions.MULTIPLAYER_SWITCHES; the Quest's per game). Jak II
    off: runtime.cpp and jak2 kmachine.cpp start no adapter, profile or network (the bridge's calls are still given, and
    answer as with no session: pc_multi_exchange_state and the game's saves' pc_multi_player_data_save guarded), the
    manager is not spawned (mp-main.gc), the LED sync idles, the title is the game's own (New Game, Load Game, no invites
    looked for), Options has no Multiplayer row, Game Options' greyed, no beta notice; the VR menu says which
    (pc-vr-multiplayer?, kmachine.cpp). Jak 1 off: as before (party::update(false); Teamruns' socket only with its app's
    --socketport). The mod's Steam row greyed (no Steam here). Both launchers' Java compile. Built in build 52.
    **The title's hidden Secrets row** (sim b63 ptr2, build 55; Session 16): the Multiplayer row moved Secrets from 3 to
    4, and upstream's skip over it while hidden (secrets locked) had its index written in: down from Multiplayer skipped
    Options onto the hidden row, up from it went to Multiplayer, and the pointer at Quit Game chose the hidden row, whose
    click opened the memory card notice. Fixed in code (build 56): its index by name (progress-static-pc.gc
    vr-title-secrets-index) in progress-generic-pc.gc (up and down), progress-pc.gc (off the hidden row) and
    progress-draw-pc.gc (no highlight on it), right with Multiplayer on and off; the pointer leaves the hidden row out and
    its rows' boxes are where they are drawn (vr-title-row-hidden?, vr-title-row-shift: Quit Game 45 up with the secrets
    locked, 35 open; the other title rows 30 up open). At the old Quit Game aim the dot is now on Quit Game with the mod
    on and off (the boxes 289-336 and 257-304 against the dot's 298).
    **Which games join each other** (Session 16): the mod's compatibility identity was its placeholder resolved to "dev-"
    and Precursor's git commit, which the Quest's build has none of (revision.h written only with a .git: no identity,
    so its LAN announce and session gate fail), and a PC build's commit could differ from the Quest's. Now
    mod-settings.gc *mod-version-text* "0.3.1-jakvr.0.1.3" (the mod's version, then this project's release; shown
    "v0.3.1-jakvr.0.1.3" at the title's bottom right and in the speedrun display): the PC's and the Quest's the same,
    another each release (build_pc_release.py stops a release whose last part is not VERSION).
    Checked (build 56, Session 17): b65 ptr2 (tests/jak2_ptr_menu.sim, Multiplayer on, five rows) and b65b ptr2off
    (tests/jak2_ptr_menu_mpoff.sim, gkcfg-mpoff, four rows): the dot picks every drawn row and never the hidden Secrets
    row, Quit Game asks Yes / No under it, the dot on each half picks it, the click on No closes the question, no
    memory card notice; "v0.3.1-jakvr.0.1.3" at the title's bottom right. The sims were retuned to where the rows are
    drawn now (their headers say how).
    **The two games joined** (C:\JakQuest\sim\mp2.sh: two Jak II games on this PC, the host `-mp-host lan`, the guest
    `-mp-client 127.0.0.1` 50 s later; Session 17): b69, both games crashed the moment the guest joined. b70 with
    JAKVR_CRASH_DUMP per game (mp2d.sh): both at the same place, our pc-vr.gc vr-merc-others! (the other players'
    Jaks for the eyes, each frame), reading address 0: a joining player's record is there before his Jak, its
    target-handle still 0 (not #f), and handle->process read through it. Fixed (build 59): vr-merc-others! and
    vr-others-pose! find the other Jak by the mod's own safe lookup (mp-player-record-target), and a Daxter pointer of 0
    is passed over. b71: the guest connected ("connected to 127.0.0.1:26210", UDP both ways), both games played to the
    end and quit (exit 0, no dump); both Jaks start on the same spot, where the other is left out of the eyes (standing
    in the player's body); after the guest walked 32 m off, the host's picture (mph-3) shows his Jak there, whole, and
    after the guest quit (mph-after) he is gone. b72 (the guest sim now looks back after its walk, mpg-2back): the
    guest sees the host's Jak standing at the start, whole; again both to the end, exit 0, no dump. Not tried here: a
    Quest in the game, two machines on a real LAN, the internet.
    **The Quest's build** (android_native_check.sh, Session 17): stopped in EyeRenderer.h, where the mod's per-player
    eye textures were made with GL_UNSIGNED_INT_8_8_8_8_REV, which GLES has not. Now GL_UNSIGNED_BYTE, as the game's
    own eye textures beside them (no data at creation: the same RGBA8). The ARM64 runtime (libmain.so) builds and
    links; PC gk rebuilt with it (build 60), and b73 on it: joined, each sees the other as in b72, exit 0.
16. [x] **Jak 3: "a mandatory minigame ... not as close as I should be to it"** (a player, relayed 2026-11-16; "not a
    problematic distance just feels wrong"). Jak 3's mandatory minigames with their own camera (the turrets, the gun
    course and the like): where the view is put in each, as against Jak II's.
    Found: third person anchors the view at the game's camera at kUnitsPerMetre / third_person_scale, with no Life
    size (kLifeSize) as the body's views have; the likely minigames are the turrets (third person to start; First
    person in vehicles off). To check on the sim against Jak II's turret, then give third person the same scale.
    0.1.3: First person in vehicles is on to start (item 21), and the turrets are among its rides (each game's
    vr-vehicle-first-person-ok?: focus turret), so a turret minigame is now seen from Jak's own eyes at life size, as on
    foot; the third-person distance the player felt (0.1.2: the game's camera, a game metre a real one) is only there
    with First person in vehicles off. No Jak 3 turret sim exists (Jak II's turret2 is in batch 61).
    Sim (Session 17): C:\JakQuest\sim\jak3_turret.sim (turret3: continue wascityb-gungame, Spargus' wall turret,
    wascity-turret-1). Jak lands on foot 82 m from it, so the test aid JAKVR_TEST_TURRETS=1 (pc-vr-test-flags bit 256,
    Jak 3's pc-vr.gc vr-test-turret!, build 57) sends the turret the 'pickup its Triangle prompt sends, 2 s into play:
    target-turret-get-on, -stance, -stance-fire.
    Found (b66, b66r with JAKVR_RIDE_TRACE): first person in the turret, but the view sank 2 m in 20 s, 0.1 cm a frame,
    from his standing eyes to his feet. The turret hides Jak once he climbs in (no-draw from the get-on's frame 34),
    and his undrawn neck, which the riding view follows, sinks into it. Jak 3 marks the turret with focus turret, not
    him (he gets grabbed), so the first fix by focus-test? never took (b67).
    Fixed (Jak 3 pc-vr.gc, build 58b): while he is hidden in his turret states (get-on, stance, stance-fire, get-off)
    his eyes are where he stands at the gun, at his standing eye height over it (2.4 game metres, as on foot). Jak II's
    turrets keep him drawn (unchanged).
    Checked (b68 turret3): the eyes at 2.4 m over his place for the whole ride, not moving; the pictures from there:
    over the twin barrels to the sea, the shield plates below, both eyes alike; the head looks round freely, the stick
    turns the turret (its heading follows), the trigger fires (the turret's fire state, the controllers' buzz). The
    game's own camera (b66t, First person in vehicles off) frames it the same way from just above the barrels.
17. [x] **Every strap and loose piece on the bodies swings and fades** (the owner, 2026-11-16: "make sure any other
    meshes on the bodys similar to jak 1s hand straps have proper physics applied. make sure they also fade similar to
    other parts of the body so it doesnt obstruct view tho"). Jak 1's hand straps have their swing; find the others in
    all three games (Jak II and 3's scarves, belts, straps, Daxter on the shoulder, hair and goatee joints and the
    like), give each the same swing from the body's motion and the body fade's same treatment near the eyes.
    Found (2026-11-16, the skeletons in joint-nodes.gc against the body's log): Jak II's and 3's Jak have no loose
    leather like Jak 1's wrist straps (two joints hanging from the wrist, swung by body() step 8). Their pieces are
    stiff gear: the left shoulder pad (Jak II node 23, Jak 3 22) and the right thigh's pants piece (Jak II 63) go with
    their limb joint (the body's "joints carried by the limbs": 23>15, 63>34 in Jak II, 22>13 in Jak 3), the knee
    straps hang under the knees and go with the legs, Jak 3's pack strap sits on his chest and goes with it. The legs'
    and arms' pieces fade with them near the eyes (build 52's fix). His hair (MhairA, MhairB) and ears are left out of
    the eyes with his head and keep the game's own animation (they show in his shadow and to others in a party).
    Nothing else hangs from Jak, Dark Jak or Daxter's riding pose in either game. To see again on build 54's sims:
    act2, act3 (the pads with the arms, nothing in the view).
    Seen (b61 act2 and act3, build 54): no pad, strap or piece of gear in the eyes' pictures through every move, the
    pads carried with the arms (the log's "joints carried by the limbs": 23>15 63>34 in Jak II, 22>13 in Jak 3).
18. [x] **Jak II: z-fighting on some billboards** (a player, 2026-11-16). Find which (the city's signs and posters: tie
    or generic decals close to their wall); per-eye depth (the eye shift of item 2, the projection's near plane in
    VR) as against the game's own.
    Done in the code (build 54): the eyes' near plane per view (jak_vr.cpp frustum, each game's math-camera.gc reads it
    from pc-vr-frustum!'s z). In his eyes 6 cm of the player's (kEyeNearMetres, inside the 7 cm where his body and the
    gun have faded away, a static_assert keeps it so), 1.7 times finer depth than 0.1.2's 3.4 cm; seen from the game's
    camera (third person, around you) the game's own 25 game cm, 5 times finer. To see on the sim: city2's posters.
    Seen (b61 city2, build 54): the slums' hanging sign and the posters on the walls whole in both eyes, standing,
    turning and walking; nothing of the body or the hands cut by the nearer plane.
    The owner, later: "i didnt know the jak 2 light billboards actually just have that flicker effect on them natively".
    Nothing to undo: the change made depth finer and touched no sign's own animation or light; the texts now say the
    city's lit signs flicker by design.
19. [x] **Dark Jak's body "squishy" in Jak II and 3** (a player, 2026-11-16). Dark Jak's own skeleton and its scale
    (the game grows him): the body's IK and life size against it; make him as solid as Jak.
    Found: both games' target-darkjak.gc widen his upper_body joint 1.2 times across and front to back, not up
    (trs-set! upper-body), so the arms' joints under it are sheared, at a slant that changes as the game animates them;
    unstretch made each row the right length but kept the shear (arms bulging and thinning with the hands still).
    Fixed (jak_vr.cpp nearest_turn): a skewed joint under the collar or thigh is made the nearest plain turn at the
    body's scale; the wide chest stays. To check on the sim: Dark Jak's arms in first person (buttons2 has him).
    Checked (build 56): b64 dark2, Dark Jak's arms in first person (on, looking down, hands down and to the left, a
    fist, a punch) solid in every picture, the claws in place, no bulge or thinning. In the notes.
20. [x] **Punch and shoot (a player, 2026-11-16):** "there is a move that seems hard to do in vr so picked up my
    controller. your supposed to punch and shoot at same time, your punch seemingly prevents me from shooting even if
    i use button on vr controller", and "you only need that move for a tutorial on how to do it"; the owner: "fix this
    issue in a way that avoids any other similar conflicts".
    Found: the move is Jak II and 3's gun combo (Jak II's gun course teaches it with the yellow gun:
    gungame-obs.gc yellow-training-intro, "gungame-tutorial-kick-combo"). A punch or kick that hits opens a window of
    0.7 s (combo-window-start); the trigger in it (target-gun-joint-pre0) draws the gun if it is away, turns him to
    what was hit and fires at it while track-target-hold-time lasts (0.4 s); the dummy counts it only if a shot hits it
    within a second. Two things of the port's stood in the way:
    - **The hand's aim** (Aim with your hand, on to start) put every shot where the right hand pointed (gun-util.gc
      vr-gun-aim!), the combo's too, so it missed what the punch hit and the lesson never passed.
    - **The left fist was also L2** (0.1.1 on): Dark Jak in Jak II, the Dark and Light Jak chooser in Jak 3 (any dark
      eco), and both put the gun away and keep it off. With the gun in the right hand the left fist is the only one, so
      a punch with it could turn him and leave the trigger nothing to fire.
    Done in the code (not built yet):
    - gun-util.gc (Jak II and 3) vr-gun-combo-aim?: while the game holds its combo aim on what was hit, the shot goes
      from the muzzle in the hand to it; otherwise the hand's aim as before.
    - jak_vr.cpp fill_pad: on foot with Fists on the right grip (on to start), Jak II and 3's left grip is the left fist
      and not L2. L2 is the left stick's click there (as the right grip's R2 is the right stick's since 0.1.0); where
      that click is the menu button (SteamVR with Touch) the click with the left grip squeezed; a Vive wand, with no left
      stick click (pad.h left_click_sensed, xr_openxr.cpp logs "stick click"), keeps L2 on the grip. In Jak 3, while
      the click holds L2, the right stick picks Light Jak's powers (up Triangle, down Cross, left Square, right Circle:
      X and Y are under the same thumb) and turns nothing. In vehicles and on the board the left grip is L2 as before.
    - Texts: the VR menu's and the launcher's Fists on the right grip, docs/CONTROLS.md (the launchers' Controls).
    To check (sim): Jak II's gun course combo if a continue point reaches it (else a punch and the trigger near an
    enemy with JAKVR_TEST_GUN=1: the shots at it); the left grip in Jak II and 3 never L2 on foot, the left click L2.
    Seen (build 56, b64 combo2: the gun course's yellow gun lesson, "Kick a target then press R1 for a rapid fire
    combo", by the test aid JAKVR_TEST_COMBO): the first punch on the first dummy and the trigger 10 frames later
    fired (the yellow gun 100 to 99) and the dummy was blown apart (c-combo1 to c-combo2); the later punches hit
    nothing and, as in the game, fired nothing. The lesson's own "passed" was not reached (the sim punches on toward the
    wall); buttons2 and buttons3 (b61, b57) clean.
    The lesson's pass (Session 18, C:\JakQuest\sim\jak2_combo_pass.sim, batch28c combopass; test aids under
    JAKVR_TEST_COMBO, pc-vr-test-flags 2048: target-gun.gc logs each R1 against the window and the combo's target,
    gun-dummy.gc each dummy's hits and where it stands against Jak, gungame-obs.gc passed or "Not quite"). b75 to b78:
    the trigger must be pressed inside the 0.7 s after the punch's hit (the sim now taps it six times over half a
    second); the dummies stand 20 and 27 m from the start, so the sims' punches hit air and their plain shots broke a
    dummy ("Not quite"). b79 and b80: walks overshot (7 m past) or stopped short (6.6 m), the lunges never reached.
    Build 65 (gun-dummy.gc, the same flag): Jak set 1.4 m before dummy 0 as the lesson opens. **b81: passed** — the
    punch broke dummy 0, R1 50 ticks after the hit found dummy 1 as the combo's target (about 10 m away, with the hand
    turned 60 degrees off, hand_yaw 60), its shot broke it 46 ticks after the mark, "a dummy broken by a combo (the
    other one gone: passed)", "the lesson passed (sigc080)". jak2_combo.sim (b64) shares the flag, so it now starts by
    the dummy too.

## Asked later the same day (2026-11-16)

21. [x] **First person in vehicles on to start, with the view's place to set** (the owner: "i know we have a a setting
    for 1st person vehicles/everything really. make that the new default. add options to adjust angle/positioning as
    needed"). Done in the code (built in build 56):
    - First person in vehicles on to start, under a new key ("first-person-vehicles-on"; 0.1.1's "first-person-vehicles"
      was off to start and is read no more), in the VR menu and both launchers.
    - **Vehicle view height** and **Vehicle view forward** (centimetres, -50 to 50) and **Vehicle view tilt** (degrees,
      -30 to 30, down above 0), VR menu and both launchers' VR Body, first person in a vehicle only (kFlagVehicle and
      not on foot). The view alone moves: the body's fit takes the move back off (map_view_shift, as View height's
      lift was), so he stays in his seat. The tilt is in to_world_dir itself (s_view_tilt), so the view, the hands, the
      gun and the body all tip together about the seat's level line; 0 is as before.
    To check (sim): zoomer, board, turret2 with the defaults (first person now), then with each set (gkcfg-vview).
    Checked (build 56): b64 zoomst (Jak 1's Zoomer, the defaults): first person in the seat, no "a jump of" line while
    riding (b63's drop of 1.5 m into the Zoomer at each bob, picture zm-drive1 black: kRideHeadMost is 8 m now), the
    view in his seat 0.2 to 1.6 cm a frame against his head's 0.7 to 13 (Steady view when riding). b64 zoomfpv
    (gkcfg-vview: height 20, forward 20, tilt 15): the view in the seat tipped down, the hands in place, every picture
    drawn. b61 board (build 54): Jak II's board in first person. Jak 3's turret: item 16.
    Jak 3's vehicles (gk 60): new sim C:\JakQuest\sim\jak3_vehicle.sim (batch28c veh3: the continue point
    wasdoors-desert-turtle, the Tough Puppy, A to drive, the stick to turn). b74 (the defaults): first person in the seat
    (target-pilot-start, then target-pilot-stance; "view in play: first person"), no "a jump of" line, the view in his
    seat 0.0 to 0.2 cm and 0.0 to 0.5 degrees a frame (Steady view when riding), the horizon level while the car tips;
    pictures seat, look left, drive, turn, stop and look down all drawn, no errors. b74v (gkcfg-vview): the view 0.29
    game metres higher and the same further forward than b74 (20 cm at life size), tipped 15 degrees down, the hands on
    the bars, every picture drawn, no errors.
22. [x] **Adaptive cutscenes looked over** (the owner: "be sure it works well and goes to third person automatically
    during moments that make sense and switch to first person view accordingly ... still have it on by default"; then
    "the other tester may not have really tried adaptive cutscenes ... if it looks fine and has no room for
    improvement, then leave it as is"). One gap found: a scene that had left his eyes three times stayed around you to
    its end, so a long scene that came back to Jak was not seen from his eyes again. Done (not built): the wait to
    come back grows instead (1 s the first two times, then 2 s, then 4 s), and coming back the distances are 85% of
    those that leave his eyes, so a Jak at the edge of one does not go back and forth. Still on to start.
    To check (sim): adapt1, open2, open3 ("[vr-cam] adaptive cutscenes" lines; no switch within a second of the last).
    Checked (build 54, b62 open2, open3, adapt1): open2's long scene comes back to his eyes each time, the waits 1, 1,
    2 and 4 s, no back and forth; open3 and adapt1 clean.
23. [x] **The JET-Board in Jak II (and 3)** (a player, on a DPVR E4 through SteamVR: "cant use the hoverboard in jak
    2", "i pulled both trigers both grabs tried random buttons and putting hands by head"; the owner: "see if its just
    the headset ... may be an actual conflict"). Their log (0.1.2): SteamVR in Meta compatibility mode, Touch bindings
    (no left menu button, no thumb rest), the board's task begun (stadium-board1-board), the grips and the holsters
    squeezed, the right stick's click (the board's R2 with Fists on the right grip) never pressed. Not the headset: the
    board's button was one nothing in the game shows. Found three conflicts besides. Done (not built):
    - In Jak II and 3 on foot a quick squeeze of the right grip (let go within 0.35 s, no punch or kick thrown) is R2
      too: the board, as the game's hints name the grip's button. A fist held or one that hits stays a fist.
    - With the left thumb on its rest (the Quest's d-pad) the right stick's click was Select, which is Jak II and 3's
      pause (drawable.gc determine-pause-mode): standing still, the thumb resting, the click for the board paused the
      game. Where the click is R2 it is R2 now whatever the left thumb does.
    - A right stick click let go within 3 game frames was lost (held back to see whether the left joins it for a
      recentre): now given to the game for 4 frames once let go.
    - A right controller whose binding has no stick click (xr_openxr.cpp logs it) keeps R2 on its grip with the fist.
    To check (sim): buttons2 and buttons3 (the click), boardg2 (the grip's quick squeeze: target-board-start).
    Another player on 0.1.2 (relayed 2026-11-16): "i got bak on jak 2 to try hover board i dunno how but i got on it,
    but i put it away and cant figure out how get back on". On 0.1.2 it is the right stick's click (R2, with Fists on
    the right grip, on to start); 0.1.3 adds the grip's quick squeeze above. The game also lets the board out only
    where it can be ridden (nothing happens elsewhere). CONTROLS.md now opens its board section with "Getting on the
    JET-Board ... and back on after putting it away". Then the same player: "ok jetboard works / button was right
    thumbstic press" (0.1.2's click, as above).
    Seen (build 54): b61 board (Jak II's board by the click, the ride in first person, steady) and b57 buttons3 (Jak 3's
    board by the click: target-board-start). b61 boardg2: the quick squeeze gave R2 ("[vr-pad] a quick squeeze of the
    right grip: R2 (the JET-Board)") but the board did not come out: there Jak had jumped, ducked, punched and paused
    first, at the fortress's edge, and the game lets the board out only on some ground (want-to-board?: no-board
    surfaces, a duck, the pause lock and the like). To see: boardt2 (C:\JakQuest\sim\jak2_board_tap.sim), the board
    sim with its click swapped for the squeeze, same place and moment.
    Seen (build 55, b63 boardt2): the quick squeeze in the same place and moment gets him on the board
    (target-board-start). Every way on works on the sim; the DPVR headset itself is not here to try.
24. [ ] **"Intro for jak 3 was upside down"** (a player, relayed 2026-11-16; "yeah jak 3 upside down"; the owner: "fix
    anything similar that could happen in other cases"). Their log: the opening (intro-drop) seen around you, from the
    scene's camera. To find on the sim (open3, open2, adapt1: the pictures of each game's opening).
    Looked at (2026-11-16, build 52): not reproduced. Sim b59 intro3 (Jak 3's title attract, first person scenes with
    Adaptive, 25 pictures) and b60 introa (the same seen around you, as Vixel's log, gkcfg-around, 71 pictures every
    half second): every picture upright in both eyes. By the code the 3D view cannot turn over: third person takes the
    game camera's yaw alone (jak_vr.cpp camera, the same in 0.1.2), the eyes' rows come from the head's own turn, and
    the HUD and flat panel are turned about the vertical only. What was wrong in that intro: Veger's coat drawn on the
    HUD panel (item 13's fix), which may be what was seen. Still open: their moment; if it comes back, a picture or the
    moment in the scene. Not checked: the composited view on SteamVR through Link (the sim's pictures leave the
    headset's layers out).
    The same player later (relayed 2026-11-16): "jak3 loaded fine this time": not every time, then (the owner: "seems
    like the upside down jak 3 isnt a consistent thing. still, investigate as you see fit").
    One cause that comes and goes and fits: SteamVR started without finding the headset shows its null driver's view,
    which is rolled 180 degrees (this PC's rig shows it). Done (not built): jak_vr.cpp logs once a run "[vr] the
    headset's pose has been upside down and still for 5 s ..." (a player's head is never that still), beside the
    runtime's system name already logged at the start ("[openxr] system: ..."); a log with it would tell.
    Again on build 54 (b62 introa, 71 pictures around you): every one upright in both eyes.
25. [x] **Jak 1's Load Game, the same player's log** ("in jak1 i just closed game after saw text in load acting like it
    start new game"): 0.1.2, the question to write over the file that Teamruns' save rules asked (item 1). Fixed in
    0.1.3's code; sim b57 load1 (build 52): the title, Load Game, the file, A: the save loaded (target-warp-in at its
    place), no question.
26. [x] **Jak 3's leaper lizard shakes the first-person view** (a player, relayed 2026-11-16: "i have found 1st person
    vehicle issue for specifically in jak 3, there is a lizard mount when riding it shakes camera to point where you
    can't see where your going / can we get camera to still be 1st person without inheriting the crazy shake"). The
    leapers (Spargus, the leaper race: was-leaper-race.gc) are ridden as the flut (target-flut.gc, focus flut), so
    First person in vehicles puts the view in Jak's head (first_person_anchor's riding branch: his eyes from his root,
    eased over 0.15 s in the world's axes, and the heading his own each frame), and his head bounds with the lizard.
    Done in the code (not built): **Steady view when riding** (VR menu, both launchers, on to start): the view alone
    (a view shift, so the body stays in his seat) keeps his eyes' place in the seat through two 0.2 s eases in the
    ride's own axes (a turn is not eased, only the bob: a 2-3 Hz bound kept to a sixth or less) and his heading through
    two 0.05 s eases (a cut past 1.2 rad taken at once). For every ride (cars, the board, the Zoomer, the flut flut).
    jak_vr.cpp ride_steady_shift, ride_steady_yaw; CONTROLS.md. To check on the sim: a leaper ride in Jak 3 (a
    continue point in Spargus by the leapers), the board (board2) and a car as before.
    Checked (b63, build 55): leap3 (Steady view on) the view in his seat moves at most 0.4-2.7 cm a frame against his
    head's 5-8.7, its turn 2.5 degrees; leap3off 1.4-8.5 cm, 4.2 degrees. Works; in the notes.
27. [x] **A crosshair for the guns, Jak II and 3** (the owner, 2026-11-16: "add a crosshair option. some people want
    that when using guns ... add a dot, a typical crosshair, etc. crosshair size, etc.", then "make sure its a proper
    dynamic crosshair that reacts to the world and is accurate asf. similar to our halo ce decomp crosshair").
    Done: gun-util.gc vr-crosshair! (Jak II and 3) probes from the gun's fire-point along
    fire-dir-out (the hand's aim, or a combo's), the game's own line-sphere probe as its laser's, 40 m, against what
    the yellow shot hits (Jak 3: the laser's set), enemy = process-mask enemy/guard in the gun's fire-range; called at
    the end of vr-gun-aim! when the hand aims (shot >= 2), only with Crosshair on (pc-vr-crosshair-kind). Runtime
    (jak_vr.cpp crosshair_at, camera(), end_frame): the hit into tracking space by that frame's own mapping
    (to_tracking_dir, to_world_dir's inverse), then onto the gun's line as the hand holds it in those pictures at that
    depth (no game frame of lag in a swing; a hit over 5 degrees off the line, a combo's, kept); shown as a quad layer
    (xr.h CrosshairLayer, xr_openxr.cpp, over the eyes, under the HUD) facing the eyes with the head's roll, 2 degrees
    across at 100% at any distance (as HaloCE's reticle). Its picture: crosshair.h (Off, Dot, Cross, Cross and dot,
    Circle, Circle and dot, Plus; White, Green, Yellow, Cyan, Red, Pink; a dark rim; red on enemies, yellow if the
    color is red). Options in the VR menu (guns only, after Gun angle) and both launchers (VrOptions.java): Crosshair
    (Dot to start), Crosshair size 50-300% (100), Crosshair color, Crosshair opacity 20-100% (100), Crosshair red on
    enemies (on). The sim logs "[sim] crosshair: kind K at D m (...)" and stamps it into the saved eye pictures.
    Built in build 56; CONTROLS.md, the NOTES draft and whats_new have it.
    Checked (b64, gkcfg-cross: a Cross at 200%): cross2 (Jak II) and cross3 (Jak 3) show it in both eyes where the gun
    points, its distance following the aim (Jak 3 10.0 m ahead, 8.0 m aimed left, the same size to the eye); crossc2
    and crossd2 (the gun course, the city): it moves in as Jak walks up to a wall (22.7 m down to 2.1 m), back out
    when the line clears.
    Red on enemies: no b64 sim had the line on one (b67, with a trace of what the line hits, JAKVR_STATE_TRACE: the
    gun course's walls, instance-tie, beside the first dummy). Build 58: the enemy test also counts what the gun's
    tracking holds (as its laser's spark does, gun-info-method-9), and the trace "[vr] crosshair: on ..." (Jak II and
    3 gun-util.gc vr-crosshair-trace). b68 crossdum2 (C:\JakQuest\sim\jak2_cross_dummy.sim: the hand turned left a
    degree at a time across the course's targets): on "gun-dummy-a" (its mask has enemy), "on an enemy" in the log,
    and the cross drawn red on the target in the pictures, where the laser lands; white again off it.
28. [x] **Where the games are kept, on PC** (a player, relayed 2026-11-16: "Is there a way to configure where the game
    data installs? I think it goes to a directory on the C/main drive, right? Is there a way we choose another
    drive?"). It did: each game's set-up (the extracted disc and the built game, about 3 GB for Jak 1, up to about
    10 GB for Jak II or 3) was always in %LOCALAPPDATA%\JakQuestVR\data, on C:. The Quest keeps its in the app's own
    storage (nothing to choose there).
    Done (PC launcher): the Play tab's **Where the games are kept** card shows the folder and the free space on its
    drive, with **Move to another drive or folder...** (a folder picker; a JakQuestVR folder is made in the one picked,
    unless it is named so), **Back to the default place** and **Open the folder**. The choice is data-folder.txt in
    the launcher's own folder (PcPaths.data(): OpenGOAL's --proj-path, so gk, the set-up and the Logs zip follow it);
    saves and options stay in %LOCALAPPDATA%\JakQuestVR\config. A move (PcDataFolder.java, on its own thread, a bar on
    the Play tab, Stop): on the same drive a rename, at once; to another drive every file is copied (dates kept), each
    copy's size checked, the choice switched, then the old copy removed, only the files that are in the new place at
    the same size (data-folder-leftover.txt finishes it at the next start if the launcher closed meanwhile). Stopped
    or failed: what was copied is removed, the games stay where they were. Refused: a folder with files in it, the
    release's folder, the launcher's own, the logs folder, one inside the games' folder or holding it, not enough free
    space (the games plus 2 GB). The games cannot be played, set up or removed while files are copied (their buttons
    and the Mods tab's are dimmed; a disabled button now looks it everywhere in the launcher). A chosen folder that is
    not there (a drive unplugged) says so at the top of the Play tab, with Look again. The Logs tab's Where things
    are lists the games' folder.
    Checked: tests/java/dev/jakquest/vr/DataFolderTest.java, 32 checks passed three times (C:\JakQuest\sim\javatest015,
    with a copy from C: to D: and back, and a stop before the first file); the launcher compiles with -Xlint:all
    -Werror; the Play tab drawn (--render) at the default place, moved, and with the folder missing.
29. [x] **Texture packs: import the ones people download, on a tab of their own** (the owner, 2026-11-16: "some people
    arent able to find texture packs on the launcher so they download some online. if anything, add a way to import
    texture packs (compatible ones) that then appear in the mod list or something so they can be enabled and disabled",
    then "add a seperate section in the launcher for texture packs. it can be confusing bundling them with the mods
    section"). Both launchers had a small "Add a texture pack (.zip)" low on the Mods page, for one shape of zip only.
    Done:
    - launcher/common TexturePacks.Stager (both launchers): a .zip (on PC read by its index, so any PC tool's zip and
      old non-UTF-8 names; the Quest streams it and copies it first only when the stream cannot read it), a .zip inside
      the .zip, a folder (PC Import a folder, the Quest's folder picker file by file). The game a pack is for from its
      folders (custom_assets/jak2/, jak2/texture_replacements), else metadata.json's supportedGames, else the page's
      game (Quest), else asked (PC: Jak 1 / Jak II / Jak 3); pictures for two games make a pack in each. A pack zipped
      from inside its texture_replacements (texture page folders at its top: -vis-, -tfrag, common, hud...) is taken.
      Refused in plain words: .7z and .rar (by their first bytes: unpack with 7-Zip, import the folder), PCSX2's packs
      (replacements/, a disc serial folder, hash-named pictures, .dds), pictures in no texture page, a file that is no
      zip. Browse mods' Add and Update go through it too (that game only, as before). Each pack says Imported or From a
      mod list.
    - PC launcher: a **Texture packs** tab between Controls and Mods: what packs are, **Import a .zip...** and
      **Import a folder...**, a .zip or a folder dropped anywhere on the window (the tab opens), each game's packs (On /
      Off, Up / Down, Remove, Update, "Changed since the game was built" with Build the game again with them), **Find
      texture packs** (the lists' packs; Browse mods on Mods now lists the rest). The Play tab's rebuild line says
      (Texture packs).
    - Quest app: a game's options page has **Texture packs** (tab_texture_packs in all six languages) between Controls
      and Mods: the same, Import a .zip (Downloads) and Import a folder (ACTION_OPEN_DOCUMENT_TREE); a pack for another
      of the games goes to that game's list and the message says so; a texture pack's .zip given to Import a mod (no
      mod.json) goes in as a texture pack.
    - Texts: whats_new.txt, NOTES-0.1.3-DRAFT.md, docs/MODS.md, docs/HEADSET_CHECKLIST.md, tests/README.md.
    Checked: tests/java TexturePacksTest 37 checks (14 new: a pack in a zip for another game, asked then given, the
    metadata's game, a folder, texture_replacements picked itself, page folders, PCSX2, photos, .7z, .rar, no zip,
    Browse mods' other-game refusal, nothing left behind) PASS, ModList, GameCode and DataFolder tests pass
    (C:\JakQuest\sim\javatest016); the PC launcher compiles with -Xlint:all -Werror; the Texture packs and Mods tabs
    drawn (--render, sample packs, 1180 and 860 wide); the Quest app's Java type-checks against android-35 (no Gradle).
    On the headset: docs/HEADSET_CHECKLIST.md's Texture packs.
## Performance (the owner: "do EVERYTHING that can boost performance and have them as options in the launcher")

4. [x] **Game frame rate the headset's by default** where it holds (PC VR); checked at 90 and 120 on the sim; saved
   options moved once.
   Done in the code (not built yet): Game frame rate **Automatic** (vr_options frame_rate 3, to start, under a new key
   "game-frame-rate"; a player's own The headset's or Half the headset's from 0.1.1's "frame-rate" kept): the
   headset's rate while the game keeps up (of the display frames a picture was due on, at most a tenth late over 3 s),
   60 a second while it does not, the headset's again after 45 s, and 60 until the next load after 3 tries
   (jak_vr.cpp AutoRate). The log: `[vr-perf] game frame rate (Automatic): ...`.
   Checked (build 56, b64 auto120: the sim's headset at 120, a late stretch in play): "105 of 170 game frames late at
   120 a second: 60 a second for 45 s", then the game at 60 (the sim's own display keeps 57 a second, so 27-29 game
   pictures; a headset's would be 60). Not seen yet: the 45 s try back at 120 and the three tries' stop (the sim ends
   first); a real headset's numbers.
   Seen (gk 60, b75 autolong: tests/jak1_play_long.sim, play kept going to 14000 frames, JAKVR_TEST_LATE=3700-14000):
   at 13:09 "111 of 171 game frames late at 120 a second: 60 a second for 45 s"; 13:54 "the headset's rate tried
   again", 13:57 late again, 60 for 45 s; 14:42 tried again, 14:45 late again: "60 a second until the next load"; no
   fourth try in the 81 s to the run's end. So the 45 s try back and the three tries' stop work. Left: a real
   headset's numbers (with items 5 to 8).
5. [~] **Show performance** (VR menu and launcher, off to start): frames a second and how close to the limit, in the
   headset.
   Done in the code (not built yet): a small dark line low in the view (both eyes, 1.5 m ahead, 0.36 m down), made
   again twice a second: "72/72 FPS GAME 72 WORK 6.1/9 MS EYES 100%" (the frames given of the headset's rate, the
   game's pictures a second, each picture's work on the display thread, average and most, and the eyes' resolution).
   jak_vr.cpp PerfLine, xr_openxr.cpp draw_perf (its own quad layer), notice.h's digits; the sim writes perf.png and
   the line every 5 s.
   Checked (b63, b64): the line is drawn in both eyes and its numbers follow the game ("57/120 FPS GAME 57 WORK
   5.9/7 MS EYES 100%" in play, "GAME 27 ... EYES 60%" once the game was at 60 and the eyes at 60%).
6. [~] **Dynamic resolution on by default on the Quest** (and the PC): a new key, "dynamic-resolution-on", on to start.
   Checked (b63, b64 auto120): late frames take the eyes down 10% a step (to 60% in that run), "the headset keeps up"
   takes them back up 10% a step. The Quest's own numbers need the headset.
7. [~] **Both eyes in one pass (multiview)**, as an option.
   Done first (not built yet), **Share work between eyes** (VR menu and launchers, on to start): the second eye keeps
   what the processor worked out for the first eye of the same frame: the level's time of day colors (tfrag, TIE,
   shrub, hfrag: the same for both eyes) and TIE's culling and draw lists (from the game's planes, the same for both
   eyes). jakvr::eye_work_kept.
   Looked at for multiview itself (2026-11-16): not possible with OpenGOAL's renderer as it is. Of its 46 vertex
   shaders one (generic) takes the eye from a uniform; the others are given each eye's camera by the processor per
   pass, and the sprites, particles, the HUD's 3D parts, shadows and the ocean (DirectRenderer2 and the VU programs'
   C++ copies) are put on the screen by the processor, as the PS2 did, so each eye's vertices exist only after that
   eye's pass. One draw for both eyes would need every renderer rewritten to carry both eyes' vertices or matrices.
   Share work between eyes is what can be had of it: the second eye skips the work that is the same for both.
   Merc2's bones (the same in both eye passes) were looked at too: its bone buffer is refilled batch by batch within
   a pass, so keeping the first eye's would need it whole for the frame; left as is.
   Measured (b63, the PC sim, Jak II's slums): 6.42 ms a picture with Share work between eyes on, 8.91 off: about
   2.5 ms saved.
8. [ ] **Max draw distance's cost on the Quest:** measured (the owner's: on, everything drawn).
   On the PC sim (b63) none to see: Jak 1's Geyser Rock 3.90 ms a picture on, 3.97 off; Jak II's slums 6.42 on, 7.28
   off. The Quest's cost needs the headset (Show performance's WORK and EYES with it on and off).
9. [x] **Less log writing in play.**
   Done in the code (not built yet): Jak 3's intro debug lines (Precursor's "[J3-INTRO]", 57 of them: one each frame
   on the title, more at each scene and load) left out of the build (compiler-setup.gc j3-intro-log); the loader
   thread's heartbeat and CancelDGO lines at trace (not written); OpenGL's notifications and low messages not sent by
   the driver. The port's own body and pointer lines are test aids (JAKVR_BODY_TRACE and the like) already.
   Checked (build 54): no "[J3-INTRO" line in any Jak 3 run (b62 introa, gun3), where 0.1.2's code wrote about 1250 a
   run (b40, b41); what is left of the loader's is upstream's own "CancelDGO" warning, one a load.
10. [x] **Faster preparing:** only what changed (LIST-0.1.2 item 1).
    Done in the code, PC (not built yet): build_pc_release.py writes a compiler line in game-code.txt (the SHA-256 of
    Precursor's goalc, decompiler and common sources); PcSetup copies only the sources whose bytes changed
    (syncTree), compiles again only what changed when the compiler and game data version are the same (the compiler's
    own make), and decompiles again only when what the decompiler reads changed (decompileKey in the ready mark).
    GameCodeTest has the checks. The Quest: its preparing deletes its intermediate files (gigabytes) to save room, so
    it stays as it is (a full prepare, only when the game code changed, as since 0.1.2).
    Checked: GameCodeTest, 30 checks passed (C:\JakQuest\sim\javatest014): the compiler line, the decompile key (the
    decompiler's config or the game data version changed: another key), and syncTree on made-up folders (a file with
    the same bytes keeps its time, a changed one of the same length is copied and newer, one the release no longer has
    is removed, a new one copied; syncTree is package-private for the test). The whole of it in the launcher runs with
    the next PC release's first update.
11. [x] **Anything else that boosts performance,** each an option in both launchers (the Quest's threads marked for the
    system's scheduler, and the like).
    Already there (each in both launchers' Graphics): Eye resolution, Refresh rate, Smoother frames (AppSW),
    Foveated rendering, CPU and GPU boost, Max draw distance, Anti-aliasing, Anisotropic filtering, Game frame rate
    (Automatic), Dynamic resolution, Share work between eyes, Show performance, PS2 level of detail, Sharpening; the
    Quest's game threads are given to the system as the app's main and render threads (xrSetAndroidApplicationThreadKHR,
    from Ratchet 0.1.19). Nothing more found that does not change how the games look or play.
12. [x] **The launchers:** every performance option there, plain names, best defaults; texts.
    Done (VrOptions.java, both launchers): Game frame rate with Automatic (to start), Dynamic resolution on to start,
    Share work between eyes (on), Show performance (off), each with its text.
    Checked: all of item 11's options are in VrOptions' Graphics list, which both launchers show, each with its text;
    both launchers' Java compiles (the PC's tests in javatest014, the Quest app's against android.jar).
