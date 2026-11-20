# Jak and Daxter Quest VR 0.1.8: the list

Started 2026-11-19, after 0.1.7 was published. Released as 0.1.8 on 2026-11-19 (the owner: "Yes, publish 0.1.8").
Marks: [ ] to do, [~] in progress, [x] done (how it was checked).

A tester's report on 0.1.7 with four logs (PC, SteamVR's OpenXR in Meta compatibility mode 2.19.3, a Quest through
SteamVR, NVIDIA 617.42, 72 frames/s), kept read-only in C:\JakQuest\downloads\tester-logs-20261119. The owner: "regress
nothing, just pick up from where it was ... proceed with list", and for the asks below: "just add a toggle thats on by
default that allows you to grab gun when driving, in all games where relevant (besides jak 1 of course since that
doesnt have guns lol)" and "add physical driving if possible as a toggle that is on by default. people can still use
normal inputs to drive, but once they grab the wheel on whichever vehicle, that will allow them to use it to steer.
make this implementation as smooth as possible." Standing: every crash and close explains itself in the game's own log.

1. [x] **Jak II: a crash after a long pause** (the tester: "paused game walked away and it crashed after i came back";
   their log game-jak2-output-5: `abort()`, exit code 0xC0000409, after 1,045,683 game frames, about four hours at
   72 frames/s, the view paused). Cause, in the log: the game's kernel heap full (`kheap ... 5 bytes before heap
   limit`, `kmalloc: !alloc mem string (27 bytes)`, then `Assertion failed: 'offset'` in Ptr.h), from
   `pc-multi-get-local-version` < `mp-configure-bridge-version!` < `draw-progress-version-text`: the pause menu draws
   the multiplayer mod's version line each frame, and each frame the mod's version getter makes a new string on the
   kernel heap that is never freed (multiplayer_api.cpp pc_multi_get_local_version, make_string_from_c). A paused game
   fills it in a few hours. Fix: the version asked once and kept (GOAL), the getters returning one kept string while
   the text is the same (C++), every other C++ string getter the games call each frame checked for the same leak; the
   log gives the kernel heap's use each minute it grows, and the crash record says when an allocation failed.
   Checked: ps2 (three minutes paused in the pause menu, tests/jak2_pause_soak.sim: one `[vr-mem]` line, at the start,
   none after; 0.1.7 made about 100 KB a minute), pmp3 and pmp4 (the pause menu's Multiplayer page, "Host Game, Join
   Game"), mp2 (two games together: "in a session (Players, Leave the game)").
2. [x] **Jak 1: Jak, Daxter and the power cell held after a power cell** (the tester: "jak froze collecting a snowing
   mountain power cell ... game is not frozen just jak daxter and the power cell"; their log game-jak1-output-8: at
   Snowy Mountain, Jak at 969.1, 227.8, -3387.2, the cell's save and its camera, then nothing moved for 2 min 18 s,
   the game going on at 72 frames/s, until they closed it). The cell's victory animation is streamed: it plays at
   its sound stream's position (loader.gc ja-play-spooled-anim) and ends when the position reaches the animation's
   end. The game's guard gives up only while the position is still 0 (4 s); a stream that stops partway holds Jak,
   Daxter and the cell for ever (no `loader stall` line in the log, so the art was loaded and the stream had begun).
   Fix: in all three games a streamed animation whose stream position has not moved for 4 s of play ends there, as
   the game's own guard does, with a `[vr]` line saying so; and item 3.
   Found in batch31b (session 33): every Jak II run since build 101 ended the title's opening scene 4 s after Start
   (`[vr] the streamed animation title-disk-intro held at 37717 ...`, 22 runs; the only animation the guard ended in
   any run). Not a stopped stream: Start sets the game's skip frame (movie-skip-frame 1105) and Jak II's
   current-str-pos then answers 1105 x 34.13 = 37717 every frame, holding the scene at the logo on purpose until a
   menu choice (title-obs.gc); the guard ended it 4 s after Start instead. Fix (build 119): the guard never counts
   while a skip frame is set (pmp4: no line; the title's camera after Start the same as before the fix) (Jak II
   and 3's loader.gc; Jak 3 has the same pin, nothing sets it there now; Jak 1's current-str-pos has none). The
   game's own "master slot" errors as the title goes are in runs from before 0.1.8 too.
   Checked with the sound stopping as before 0.1.8 (the test aid's `JAKVR_TEST_AUDIO_LOSS=old`, the simulated headset
   taking the sound device away): al1ob, the tester's case (tests/jak1_cell_sound.sim, a power cell at Geyser Rock,
   the device away 5 s into its victory animation): `[vr] the streamed animation fuel-cell-victory-7 held at 4544 for
   4 s (its sound stream stopped): it ends here`, Jak in play again; al2o (Jak II's title scene): `title-disk-intro
   held at 10076`. Left alone where it must be: the game's own skip frame (pmp4, build 119), and a stream that never
   starts stays the game's own guard's (al1o: the device away before the animation, which ended after 4 s at
   position 0 with no line). (boat2 tells nothing here: this copy's Jak 1 has no stream for the opening scene.)
3. [x] **The game's sound stopping, logged and kept going** (item 2's likely cause; also the "game audio cut-out on
   SteamVR" still to find in Known limits). The games' sound synth runs only in the audio device's callback (Cubeb,
   989snd player.cpp Tick), and the stream's state callback is empty: when Windows or SteamVR takes the audio device
   away (the headset's audio device going and coming back), every sound stream stops with nothing in the log, and
   each streamed animation waits on it. Fix (PC): the device's state changes in the log (`[audio]`), and when the
   device has stopped asking for sound for a second, the synth goes on by itself (silently, at the real rate) and the
   device is opened again every few seconds until it plays; the log says each step. The Quest's own pause and resume
   of its sound unchanged.
   Checked (build 119; the test aid: the simulated headset's `audio_loss <seconds>`, the device's asks giving silence
   and not counting): al2 (Jak II's title scene, the device away 15 s): `[audio] the sound device has not asked for
   sound for 1 s ...` a second in, opened again every 3 s (a new audio context each third time), `playing again after
   14.0 s (4 time(s) opened again)`; the scene went on meanwhile (its pictures move from the "presents" sign to the
   city). al1b (the power cell, the device away 4 s into its victory animation) and al1 (away for all of it): the
   animation played to its end (7 to 8 s) and Jak in play again, no `[vr]` guard line; set beside al1o and al1ob.
4. [x] **A black headset with no controls, the game still on the PC's screen** (the tester: "hopped on jak2 screen went
   black and couldn't control jak, it still displayed on tv screen and hands was tracked but couldn't see and buttons
   did nothing. hopped on other 3 games it affected them too, restarting pc fixed it so dunno if was issue with game or
   pc"). In every game until the PC was restarted: SteamVR's state, not one game's. That session's log was written over
   by later runs; the ones we have show the same thing for a while at each start: SteamVR gave the game no focus for
   24 s (Jak 3) and 36 s (Jak II) after the session began, with no controllers, no head and frames not paced (waited
   0.03 ms a frame), and the log could not say so. Fix: the log names each session state as it comes, and while the
   headset is not showing the game or not giving it the controllers it says so every 10 s (the state, whether SteamVR
   paces the frames, the head tracked or not, the controllers); the PC's window title says it too, so it can be seen
   on the screen.
   Checked on SteamVR's own runtime with its null driver (C:\JakQuest\sim\pcvr_null_jak.ps1, Jak II): null18, each
   state named as it came (idle, ready, synchronized, visible, focused); null19 (build 120, the test aid
   `JAKVR_TEST_UNSHOWN=20,25` counting the headset as not showing the game for 25 s): the line at 10 s and 20 s (the
   state, the frames paced or not and the wait, the head, the controllers), `[vr-view] the window's title says: the
   headset is not showing the game (not drawn for 10 s): restart SteamVR if it stays so`, then `the headset shows the
   game and gives it the controllers again, after 25 s` and the title back. Both runs: the game still running at the
   end, steamvr.vrsettings put back.
5. [x] **Hold the gun while driving** (the tester: "i miss being able to hold gun while drivng ... holding gun when in
   1st person hover car was a thing in jak 2 vr. and its better than having a gun obscure your view, so if you can
   bring it back that needs to be a optional feature"; the owner: a toggle, on to start, "allows you to grab gun when
   driving, in all games where relevant"). Jak II's and Jak 3's own vehicle code keeps the gun out on the city's bikes
   and cars (vehicle flag 32; the guard bike, the Hellcat and Jak 3's desert vehicles, which have guns of their own,
   put it away) and has the game's own pilot gun poses. To do: find why the gun is not in the VR hand there now, and
   with **Hold the gun while driving** (VR menu, both launchers, Jak II and 3, on) the gun in the hand, aimed by it,
   in those vehicles, drawn and put away at the holster over the shoulder (the hip holster stays off in vehicles: a
   hand resting by the hip there was the car's height button, LIST-0.1.1 item 16). Off: as 0.1.7.
   Found (sim rg2 on builds 100 to 102): the shoulder drew the gun seated and the trigger fired it, but it was never in
   the hand. The game's seated poses give the drawn gun to Daxter (target-gun.gc target-gun-joint-points: gun-daxter
   1 while the pilot's animation says so), and the body leaves a gun Daxter holds out of Jak's hand. Fix (build 103):
   the holster ask says when Hold the gun while driving applies (pc-vr-holster's bit 8; not with Gamepad only), and
   then the drawn gun stays Jak's (Jak II and 3, `*vr-ride-gun?*`, only his own, not other players'), with a
   `[vr-holster]` line as it starts and stops. Then (sim rg2b, build 103: "in hand 1", but the gun drawn in the air,
   1.29 m from his hand): seated, the game poses the gun joint alone with the gun's animation, his arms staying on the
   handles, and the body carries the gun with the hand by the hold the game posed. Tried (build 104, taken out): his
   right arm and the gun given the gun's pose by the arm mask, as the multiplayer mod's drivers do; the seated gun
   animation still held it far from the hand. Fix (build 105 on, the runtime, jak_vr.cpp "7. Jak II and 3's gun"):
   while it applies and the view is in the seat, the gun is carried by the right hand with the hold his gun stance
   gives it on foot (kept from the last frames on foot, `[vr-body] the gun's hold on foot kept ...`; before any, the
   one measured on foot), not the seat's, with a `[vr-body] Hold the gun while driving: the gun held as on foot ...`
   line once. The kept hold is only one the game holds still (build 106: within 1 cm and 0.03 of a turn for 30
   frames; sim rg2d on build 105 kept 0.49 m from his hand as he got off the bike). The measured one (build 107,
   `ride_gun_hold`): sims gun2m (Jak II) and gun3m (Jak 3) on build 106 logged the same hold in both games to 1 mm,
   0.29 m from his hand, its turn unscaled.
   Checked (build 109): rg2e, Jak II's city bike (`jak2_ride_gun`), and rg3c, Jak 3's glider (`jak3_ride_gun`,
   `hanga-start`): reached over the shoulder, the gun comes into the right hand ("in hand 1", "the gun held as on foot
   ... not the seat's 0.91 m away", 1.03 m on the glider), the other hand staying on the handlebars or the glider's bar
   in the pictures; the trigger fires it (`[vr-gun] a round fired`) and the shoulder puts it away. The hijacked pickup
   has no continue that seats Jak, so it is a headset check (HEADSET_CHECKLIST).
6. [x] **Steer by grabbing the wheel** (the owner: physical driving, on to start, the sticks still steering, "once they
   grab the wheel on whichever vehicle, that will allow them to use it to steer ... as smooth as possible"). The
   vehicles steered by a wheel or handlebars: Jak 1's Zoomer, Jak II's and Jak 3's bikes and cars, the stadium's
   racers, Jak 3's desert vehicles. A grip squeezed with the hand where the wheel is (in front of you, in the seat)
   takes the wheel; turning it (one hand or two, as a wheel or as handlebars) steers as the left stick does, added to
   the stick; let go and the stick alone steers. Jak's hands on the game's own handles while held. The grips' buttons
   there (Jak II: the car's height, Jak 3: the turbo, the look round) stay in reach: a quick squeeze and let go is still
   the button, and a grip squeezed away from the wheel is as before. Plain names in the VR menu and both launchers.
   The owner, during: "for physical steering, make sure to look over all the vehicles to be sure gripping whatever
   wheel/control type they have works as smooth and responsively as possible." The review, vehicle by vehicle (each
   game's own control code read):
   - **Which vehicles.** Every one steered by the stick's left and right: Jak 1's Zoomer (target-racer.gc); Jak II's
     city bikes and cars, the guards' bike and hellcat, the helldog, the paddywagon, the port's and the stadium's race
     bikes (vehicle.gc); Jak 3's city bikes and cars, the hijacked pickup, the desert's vehicles and the factory's car
     (wvehicle.gc), and three the ride kinds had left out: the **glider** (h-glider), the **catacombs' sled** (h-sled)
     and the **war factory's hellcat** (h-warf). Each game now says so itself (vr-steer-flags, kFlagSteered), from the
     vehicle's own type, rather than a list of ride kinds. Not the Flut Flut, the leaper lizards, the JET-Board, the
     Titan suit or the turrets: no wheel there, steered by the stick or the body as before.
   - **What Jak holds.** From the game's own seat for him (the pilot's stance, the vehicle's rider-stance: 0 a bike's
     handlebars, 1 a car's wheel, the sled and the hellcats too, 2 a desert vehicle's wheel, 3 the glider's bar;
     kFlagHandlebars for 0, 3 and the Zoomer): a wheel's full turn is Wheel turn, handlebars' and the bar's 0.6 of it.
   - **Grabbing it.** Where the game's pose has Jak's hands on the controls (the body's handles, first person), within
     22 cm, as well as anywhere ahead of you, low, within reach; one hand alone turns it about the middle half the
     handles' own span in from it (a wheel's, a bike's bars), not one size for all.
   - **Turning it.** A wheel held upright, tilted back as a car's, or as handlebars (pushed and pulled) all steer the
     same for the same turn (the old sum of the two read a tilted wheel up to 1.4 times too far); the frame lies along
     the game's handles, not where the head looked at the grab; past full lock the wheel stops, so turning back steers
     back at once.
   - **Smooth and quick.** The steering eased by how fast it moves (50 ms held still, smoothing tremor, down to 8 ms
     turned briskly, where it was 40 ms always), a middle of 1.5 degrees (was 2), and given the game from just past its
     own dead zone (Jak 1's Zoomer 32, Jak II and 3's vehicles 48): through the thumb's rest and dead zone the first 4
     to 6 degrees did nothing and then jumped (in the Zoomer to a fifth of full).
   - **Buttons with both hands on.** While a hand holds the wheel its stick's click is the grip's button (R2: Jak II
     and 3's flight height, Jak 3's turbo held; L2: Jak 3's camera view), so nothing needs a hand off the wheel; a quick
     squeeze, and the squeeze again held, as before.
   - **Nothing lets go by itself.** A hand on the wheel passing a holster keeps it (only the grip let go, or the gun in
     that hand, lets go); a left hand on the wheel is never taken for the gun's front; the vehicle leaving the game's
     flags for a moment (a state between two) keeps the hands on for 0.4 s; a grip still squeezed as the vehicle goes
     or the gun comes into its hand is nothing (no R2 on foot, no height flip) until let go.
   Checked (builds 103 to 109, tests/README "The checks added in 0.1.8"): wh2b (Jak II's city bike: 27 degrees 0.73,
   full lock about 0.98, back to nothing at once), wh2c (the city car's wheel: 27 degrees 0.43), wh1 (Jak 1's Zoomer:
   0.65), wh3b and wh3c (Jak 3's buggy: 27 to 29 degrees 0.39 to 0.43, the quick squeeze and the stick's click R2, let
   go and it straightens), rw3g3 (the glider's bar: 27 degrees 0.64 each way), rw3s3 (the catacombs' sled: 0.39 each
   way), rw3f3 (the war factory's hellcat: 0.38 each way); each grab found the game's handles ("the game's handles
   seen"). The trace logs the steering at most twice a second, on a change of 0.1, so a line at 0 degrees may still
   show the last 0.05 to 0.11 of its easing.
7. [x] **Reports with no change** (the same tester): "black screen fixed in jak 1" (LIST-0.1.7 item 12), "feet touch
   ground 1st person activation works great" (item 4), "pause menu in jak 2 is fixed" (item 10), "i completed the race
   so thats fixed too" (item 11). Recorded; nothing to change (0.1.8's sims keep these places: race3, the pause sims).
8. [x] **Choosing where each kind of file is kept** (the owner: "someone was asking if theres a way to choose where to
   allocate files (like on which drive and such). if not already there, add an option in the launcher to choose the
   file locations and move them as needed"). The PC launcher has had the games' set-up (each game's extracted disc
   and build, up to about 10 GB a game) movable since 0.1.3 (the Play tab's "Where the games are kept"); the rest was
   fixed: the texture packs (a few GB each) and their downloads, the saves and options in %LOCALAPPDATA%\JakQuestVR,
   and the logs in Downloads\JakQuestVR. Fix: the card becomes "Where your files are kept", one row each for the
   games, texture packs, saves and options, and logs, each with "Move to another drive or folder...", "Back to the
   default place" and "Open the folder", moved as the games are (a rename on the same drive; on another drive copied,
   checked, chosen, the old copy removed, a stop or failure keeping the old; finished at the next start if the
   launcher closed meanwhile). Each choice is its own file in %LOCALAPPDATA%\JakQuestVR, which stays. While a folder
   moves, what uses it waits (the saves: playing and the option tabs; the logs: playing and gathering; the packs:
   set-ups and pack changes); a chosen folder not there (a drive unplugged) is said on the Play tab and its users
   wait (the saves: playing, so no save is started over; the packs: set-ups, so none is built without them); the logs
   go to the default place meanwhile. The game card says where a game will be set up and the free space there, and
   the Logs tab lists every folder. The Quest keeps everything in its one storage (no drives to choose from).
   Checked: the launchers compiled (JDK 17, -Werror); DataFolderTest 50 of 50 with a second folder on D: (each place's
   default and folder, cross-place refusals, the saves and packs moved and back, a missing packs folder waited on, the
   logs falling back from a missing drive, leftovers, the summary line with no path, the saves copied to D: and back),
   VrOptionsTest 25 of 25; the launcher's own pictures of the Play, Texture packs, VR Body and Logs tabs with the packs'
   and the saves' chosen folders missing (tests/README).

## Later reports (2026-11-19)

The owner, passing on a tester's line: "not sure how accurate this is, may just be a runtime issue or something since
not many have reported it, but just incase, see what you can find and fix from this report", then a tester's Jak 1
log on 0.1.7 (a 5-hour game, kept read-only in C:\JakQuest\downloads\tester-log-20261119-jak1-9).

9. [x] **The shadows in each eye** (the tester: "i just remembered shadows on all 3 are still rendered differently for
   each eye"). Jak 1's shadows are made once by the game's shadow program, from between the eyes, and each eye moves
   its points to its own view (0.1.0; 0.1.5's notes kept it as a known limit); Jak II and 3's are made in the camera's
   space and each eye's perspective takes its shift (Shadow2), each face counted front or back as that eye sees it
   (0.1.4). Looked at: each eye pass clears its stencil, both eyes draw with the same even field and only a shift
   apart, and the faces the game's program clips at the middle view's edge leave at most a sliver (under about 1
   degree) at an eye's outer edge, for a shadow under 1.1 m from the eyes. Sim sh1 (build 106, Jak 1 in Sandover by
   day, the head still): Jak's shadow the same in both eyes looking ahead, down 50 and 75 degrees, lowered, raised,
   turned aside and behind (only the 3 cm apart and his arms hiding a little of it in one eye). Next, with the head
   never still and in all three games: the test aid JAKVR_SHADOW_EYES=1 (build 107) measures each eye's shadow cover
   every frame and says where they part ("[vr] shadow in each eye"), in tests/jak1_shadow_eyes.sim,
   jak2_shadow_eyes.sim and jak3_shadow_eyes.sim (the sim's new head_yaw_rate and head_pitch_rate keep the head
   turning and nodding, Jak walking a little).
   Found (build 109): Jak 1 (sh1b) the same in both eyes; Jak II (sh2, the head turning and nodding) apart in 38 of
   600 frames, the left eye's cover up to 20.6% of the picture against the right's 8.1% (frame 4819), and Jak 3 (sh3)
   in 32 and 10 of 600 (up to 1.01% against 0.40%, and 0.41% against none): the frames where a shadow's volume reaches
   past the near plane of one eye and not the other, which the counting from the front (z-pass) gets wrong in that eye
   only. Fix (build 110, ShadowRenderer.cpp and Shadow2.cpp): the eye passes count from behind (depth fail, the
   volume's ends capped), which a near plane cannot cut; the game's flat view keeps its count (`JAKVR_SHADOW_ZPASS=1`
   keeps the old count in the eyes too, for a check). The tester again (2026-11-19): "if next build hasn't fixed
   shadows, shadows need fixed". To check: batch40 (sh2z, sh3z on the old count against the walks on the new, all three
   games and several levels each, with `JAKVR_SHADOW_EYES=1`), each eye's cover the same and no shadow torn or flooded.
   Checked (batch40, gk build 113: the depth-fail count of build 110 unchanged since; each run measures both counts
   every frame, the old one unseen): on the new count the eyes parted in 13 frames of about 90,000 with a shadow, on the
   old in 3,449. sh2z (Jak II, the head turning and nodding) 0 against 39 in a block of 600; sh3z (Jak 3) 0
   against 32 and 8. The walks (Jak walking four ways, looking down at his feet, nodding): Jak 1 (Jak 1's count was
   never wrong: both counts the same) s1w-hut 1 frame of 600 (7.90% of the picture against 10.81%, both counts alike:
   his hands near one eye), s1w-beach, -rock, -crater, -misty 0; Jak II s2w-city 0 against 642 (up to 243 of 600),
   s2w-ruins 0 against 782 (up to 341), s2w-fort 8 in one block against 450 in it (435 elsewhere); Jak 3 s3w-desert 2
   against 234, s3w-spargus 0 against 360, s3w-haven 2 against 462 (batch40b; the first run was cut off). The new count's
   cover is larger than the old's in Jak II and 3 (s2w-city 8.9% against 0.4 and 1.6): the eyes are inside Jak's own
   shadow volume in first person, which the count from the front left out or showed in one eye only; the pictures
   (feet and walks, both eyes) show his shadow on the ground under him alike in each eye, nothing torn or flooded.
   The frames still apart, pictured: s2wn (build 117, s2w-fort with pictures at frames 7380 to 7410) parted elsewhere
   (frame 6384), as the frame `[vr] shadow in each eye` names is the display frame, which counts only the frames drawn
   in stereo (about 295 behind the script's). So build 118's test aid has the simulated headset picture the first six
   frames apart by over 2% of the picture itself (`apart-<frame>`; nothing on a real headset). batch51: s2w-fort 18
   frames in one block, pictured at 6888 (34% against 25%) and 7387 to 7391 (up to 38.7% against 25.6%): in each, Jak's
   own shoulder pads, a few centimetres from the eyes, stand far apart in the two pictures and hide a different part of
   his shadow from each eye (at 7389 the right pad, lit, covers about 16% of the right eye's picture over his shadow
   the left eye sees there, the gap itself); the shadow is the same in both. s3w-haven 0 against 351 this time.
   s3w-desert 1 frame, after its last full block (16243, 4.9% against 7.0%; the old count 123 in the blocks before),
   pictured: looking down at his arms, which a hand's length from the eyes each eye sees from its own side over the
   darkened ground; the shadow by his hand the same in both. So in every frame still apart on the new count, the
   shadow itself is alike in both eyes.
10. [x] **A tester's Jak 1 log** (0.1.7, a normal close after 5 hours). Its only lines of note: the body's guard
    against the game's skeleton jumping from Jak's root ("[vr-body] the game's skeleton left his root by N m across in
    a frame ... held where it was", 0.1.1) 1,734 times, in bursts in some levels: the citadel 761, the final boss 357,
    the crater 230, the snowy mountain 152, Misty 132, the lava tube 52, and a few elsewhere; mostly 1 to 5 m (up to
    21 m), held 1 to 3 frames (1,059 times) to over 30 frames (21 times). Each level's jumps go back and forth along
    one line across the world (the crater's about 17 degrees off its z axis), whichever way Jak faces or walks, so not
    his own animation's step. The guard did its job (his body held, then back on his root), but a skeleton that leaves
    for more than a few frames is followed, eased, so his body could drift from the view for a moment. Also: the game's
    own "ja-channel-push" lines (Jak and the citadel's bunnies), "camera used backup position", one controller error
    from SDL.
    Found (sim rjc, build 106, tests/jak1_root_jumps.sim with `JAKVR_TEST_CONTINUE=citadel-start`): five jumps of 3.6
    to 8.7 m in two minutes, each for one frame, every one along the line from Jak to the world's origin within a
    degree (the citadel is 5.4 km out): Jak 1 sends his skeleton, for a frame, at 0.9984 to 0.9993 of its place from
    the origin. The tester's line in each level is that level's line to the origin (the crater's, 3.7 km out, about 17
    degrees off the z axis), and the farther a level is from the origin the bigger the jump: in Sandover, 0.2 km out, a
    few centimetres, under the guard's 0.8 m, which is why the sims there never met it. (rj3, the crater, did not get
    past the title: the prelude's Start came early.) Fix (jak_vr.cpp before the guard): a jump along that line, his
    root where it was, is set right, his skeleton's bones near his main joint taken back out along the line (in all
    three directions, so his height too), rather than held and, lasting, followed (the tester's 21 holds of over 30
    frames could have drawn his body off for a moment); if it is his root that moved and not his skeleton, his skeleton
    is left as it is. The log says what moved for the first four, then a count each minute; any other jump is held as
    before (with `JAKVR_BODY_TRACE=1`, a line saying how it lay to that line). Build 107 asked for all his bones within
    3 m of his main joint, and sim rjc2 showed some of the bones the game sends stay at the origin itself (5.4 km
    off), so it never applied (its trace: each jump 0.9983 to 0.9998 of the way, under 0.01 m aside of the line, his
    root moving 0.00 to 0.24 m and his main joint the whole jump); build 108 asks instead that his neck still sit where
    it sat on his main joint the frame before (within 0.3 m). Sim rjc3 (build 107, `JAKVR_BODY_TRACE=2`, the spine as
    the game sent it) showed his whole body moving with his main joint at each jump: each joint the same distance from
    the one before as in the frames round it. Build 109's first lines also give his main joint's rise that frame and
    what it would be at the same share of his height.
    Checked (build 109): rjc4 (the citadel, 5.4 km out) four jumps of 0.84 to 9.42 m, each "taken back out along that
    line" (his neck 0.02 to 0.03 m from its place on his main joint, 80 of 84 bones with it; his rise 0.02, -0.04,
    -0.08 and 0.06 m against 0.03, 0.06, 0.03 and 0.01 at that share), then a count each minute; rj3b (the crater, 3.6
    km out) one of 3.07 m, the same. No `left his root` hold after them in either; the one at frame 3405 in each is the
    continue's own move across the world (his root 5 to 7 km), held one frame as before. rjs (Snowy Mountain): his height
    takes the same share (item 12).
11. [x] **The last phase of a boss at 1 health, and the credits flickering** (the owner, passing on a tester's line: "i
    keep ending up with 1 hp on last phase of boss and credits flicker."). The game is not named. The same tester's
    0.1.7 Jak 1 log ends at Jak 1's final boss (four deaths there, two by falls and two on the floor, then a close
    before the credits), and their Jak II log of the same day dies again and again at the tomb's boss. What the games
    do: Jak 1's health is three, a hit taking one; Jak II and 3 halve a hit on Dark Jak and never take him below 1
    health with it (target-hit), then turn him back, so a fight played as Dark Jak ends at 1 again and again by the
    game's own rule. No VR change touches Jak's health or a boss's hits in any of the three. Added (build 110): each
    drop of Jak's health in the game's log, from what to what and the last hit that reached him (what sent it and how),
    and in Jak II and 3 whether he was Dark Jak ("[vr] Jak's health 3.0 to 2.0; the last hit by ...", pc-vr.gc
    vr-health-log, all three), so the next log says what takes it. The credits: a test aid, JAKVR_TEST_CREDITS=1, has
    Jak 1's sage at the final boss start the end credits as after the last scene (sage-finalboss.gc), in
    tests/jak1_credits.sim.
    The tester again (2026-11-19, Jak 1): "credits are annoyingly flickery other than that game seemingly beatable",
    and a log to follow after the credits (they will play on to look at the scenery). Sim fbc (build 110, the test aid):
    the credits' panel drawn every frame and the same in both eyes, its lines moving up 0, 3 or 7 pixels between shots
    (the sim's clock is not the headset's, so not yet a sign). Sim rjs3 (build 111): the health lines work ("[vr] Jak's
    health 3.0 to 2.0; the last hit by ..."). Found (fbc's panel pictures, their alpha): the black behind the credits
    is there in only a third of them (4 of 23: 70 to 85% of the panel opaque); in the rest the panel is the mist alone
    (partly see-through all over) or nothing but the text (80 to 89% wholly see-through), for runs of frames on end. The
    black is a screen-space particle one frame long (sage-finalboss-part.gc 2827, launched each frame with the mist), and
    screen-space particles take the game's HUD particles, 128 of them (sparticle.gc), which the mist's puffs (each for
    5 s) and sparks fill much of the time, so the black finds no room. On a screen nothing then shows behind the credits
    (the level is hidden), but in the eyes the world was drawn behind the panel: Jak's hands and the boss's arena seen
    through it, coming and going. Fix (jak_vr.cpp kFlagCredits; pc-vr.gc *vr-end-credits-frame*, set each frame by
    sage-finalboss-credits): while Jak 1's credits run, the eyes are black behind the panel, as the screen shows them,
    whether or not the black found room; the log says when they begin and end ("[vr-view] the end credits begin: the
    eyes kept black behind them, as on a screen"). Jak II's credits play over the port's fireworks scene, which stays.
    Checked (build 113, fbc2, the test aid, 23 shots over a minute of credits, six of them frame after frame): "[vr-view]
    the end credits begin" as they start; all 46 eye pictures black (mean 0.00), so nothing of the world comes and goes
    behind the panel; the panel still has the game's text and mist each frame (its black still there in only some, 6
    of 23, which no longer shows).
12. [x] **Jak 1: his body away from the view for two seconds after a knockback** (found in item 10's checks). Sim rjs
    (build 109, Snowy Mountain, 3.6 km out, to see whether his height takes the same share in item 10's jumps: it does,
    -0.07, -0.15 and -0.23 m against -0.09, -0.17 and -0.22 at that share, so the fix's three directions stay): the
    lurkers by the start knock Jak back again and again as he stands, and after each the game's skeleton stays 1.7 to
    2.3 m from his root for about two seconds as he stands up and stands (target-hit-ground, then target-stance, his
    hips turned 10 degrees from his facing), then goes back in a frame. It got there a little each frame as he flew
    back, so the guard followed it, and in the eyes his arms and hands left the view all that time (rjs2's shots, looking
    down 40 degrees). On the ground in the other traced Jak 1 sims (rjc to rjc4, rj3b) the game's neck stays within 0.94
    m of the eyes. Fix (build 111, jak_vr.cpp): in Jak 1, on the ground, not held by the game and not riding, a skeleton
    more than 1 m from his root across is kept at its last place within 0.5 m of it until the game brings it back; the
    log says so for the first four ("[vr-body] the game's skeleton N m from his root on the ground (frame F): kept by
    it"), then a count each minute. Sim rjs3 (build 111, Snowy Mountain, the same lurkers): the log says "kept by it"
    after the knockbacks, his body staying with the view as he stands up.

The owner, passing on a tester's lines (2026-11-19), with their Jak 1 log on 0.1.7 to the final boss's end (kept
read-only in C:\JakQuest\downloads\tester-log-20261119-jak1-10):

13. [x] **Jak 1: third person in the final boss with first person chosen** ("at 1 point in boss fight game became 3rd
    person even though was set to 1st changing it back fixes"). Found in the log: "the view is third person now: the
    left menu button held" in the fight. On SteamVR with Touch no left menu button is bound, so the left stick's click
    is the menu button (0.1.5), and held a second in play it switches the view (0.1.6); in a fight the stick pressed in
    while running does just that. Fix (jak_vr.cpp fill_pad): a hold while the stick is pushed past half its reach is no
    switch (and no Start); the log says so ("the left stick's click held a second while the stick moved Jak: no view
    switch"). The switch still works with the stick at rest, and Start on a short press as before. To check:
    tests/jak1_view_hold.sim (the click held while running, then twice at rest).
    Checked (build 112, vh, `JAKVR_TEST_CONTINUE=village1-hut`, the sim with no left menu button bound): the click
    held 2 s while running: "the left stick's click held a second while the stick moved Jak: no view switch", no Start,
    still first person; at rest, held a second: "the view is third person now", then again "first person now".
14. [x] **Jak II: first person again on landing from a swing pole, then third, then first** ("in jak 2 i think the
    return to 1st person from pole when feet touch ground is acting funky. its like it goes 1st person then realizes
    its supposed to be 3rd ... i keep getting disorientated"). Found: in Jak II and 3 only the swing itself
    (target-pole-cycle) sets the pole focus that vr-first-person-ok? refuses, and vr-pole-hold? (which keeps the pole
    in third person, 0.1.4) said no in the pole's flip and jump-off states, so letting go went to first person for the
    flip, then third for the hold after it, then first on landing. Jak 1 refuses all five pole states already. Fix
    (pc-vr.gc vr-pole-hold?, Jak II and 3): yes from the pole's own states too, so the view stays third person from
    the swing to the ground and goes to first person once, on landing. To check: tests/jak2_pole.sim (the test aid
    `JAKVR_TEST_WARP=swingpole`, new in Jak II's vr-test-warp!, puts Jak a metre over a swing pole in the ruins; the
    state trace and "view in play" lines from the swing to the landing).
    Checked (build 112, `jak2_pole`, the ruins' swingpole-17 high on a tower): p2 (A mid-swing): the pole, its flip up
    (target-pole-flip-up, -flip-up-jump), his double jump and the pole again, all in third person with no "view in
    play: first person" between; four more runs took the same flip each time (Jak II takes A only 0.4 s into a swing,
    by then mid-swing, and he catches the same pole on the way down); p2 on build 113 (A late in the swing): he drops
    off it (target-falling), falls too far for the walkway below (target-hit, target-death), and the view stays third
    person until he stands at the continue, then "view in play: first person" once. A plain landing on the ground goes
    by 0.1.7's hold until his feet are down (LIST-0.1.7 item 4, checked then; the tester: "feet touch ground 1st person
    activation works great"), which this does not change.
    Jak 3's vr-pole-hold? has the same change (its build has no warp aid to a pole: HEADSET_CHECKLIST "Off a swing
    pole").

The owner, passing on another player's question (2026-11-19): "Im on the first level of first game and it tells me to
double jump to get up on ledge. Well I'm definitely double jumping but still can't quite make it up there. What am I
doing wrong?" ("they probably are just missing something, but still").

15. [x] **Jak 1, Geyser Rock: the double jump onto the first ledge**. Looked at: in play A is the game's Cross with
    nothing between (jak_vr.cpp fill_pad); the height grows while A is held, to its most at 0.1 s, and the second jump
    counts only once Jak rises slower than 3 m/s, near the top of the first (target-jump's :trans), as in the original.
    Added (test aid, the state trace, pc-vr.gc vr-state-trace): each jump's rise in the log as Jak lands ("[vr] test:
    the jump rose N m"); tests/jak1_double_jump.sim at Geyser Rock's start, at the headset's rate (dj72) and at sixty
    (gkcfg-60, Game frame rate 60: dj60).
    Found (builds 113 and 114): the jumps lower at the headset's rate. dj72: his highest jump 3.20 and 3.22 m, a double
    jump at the top 5.37 m, tapped 2.57; dj60: 3.47 and 3.40, 5.92, 2.65. The second A 12 frames after the first did
    nothing at either rate (he still rose fast), so a quick double press is a single jump. Two causes, both the
    original's sums counting on sixty frames a second, seen frame by frame (build 114's trace, dj72b and dj60b):
    (1) mod-var-jump sets his speed each frame toward the height for the time A has been held, its share of 0.1 s; at
    sixty the clock steps 5 ticks a frame and lands on 0.1 s (30 ticks) itself, at 72 Jak 1's clock steps 4 (300/72
    cut to a whole tick) and the last frame inside was 28 ticks, 0.93 of the way: his speed peaked at 13.5 m/s against
    13.9. (2) collide-shape.gc's integrate-and-collide! moves Jak by the jump animation's collision offset (his feet
    tucked in near the top) as a speed: its change this frame times 60, moved for the frame's time, so at 72 only 60/72
    of it (0.57 m of 0.68), at 90 two thirds, at 120 half. Fix (all three games, target.gc mod-var-jump and
    collide-shape.gc): the frame that steps past 0.1 s counts as its end when the frame before was short of it (at 60,
    50, 90 and 120 a frame lands on it, so nothing changes there); the offset's change times the game's frames a second
    (exactly 60 at sixty). Build 115 (the first fix alone, dj72c): 3.38 m, a double jump 5.52.
    Also found, not changed: Jak 1's clock steps whole ticks, so at 72 it counts 288 a second, not 300 (the world 4%
    slow beside Jak, whose moves go by the real frame time), at 90 270 (10%), at 80 and 120 240 (20%), at 144 288; at
    60, 50, 100 and 150 it is exact. Jak II and 3 carry the fraction (4 and 5 ticks a frame at 72). Carrying it in Jak 1
    too would make all that moves by the clock (platforms, particles, timers) step unevenly at those rates, a change
    to every level there, for a slowness no one has reported; the jump was the one place it changed an outcome. Quest
    with Smoother frames (its start) runs the game at half a 120 Hz display, 60.
    Checked (build 116, both fixes): dj72f (72) his highest jump 3.48 and 3.50 m, a double jump at the top 5.87, late
    4.39; dj60f (sixty) 3.47 and 3.47, 5.91, 3.57: the same within a few centimetres (the tapped jumps vary with when
    the tap falls, 2.28 to 2.65 at either rate). Jak II (tests/jak2_double_jump.sim at the ruins' start, the jump rise
    now in Jak II and 3's state trace too): jdj72 3.50, 3.50, 5.82, 5.44; jdj60 3.48, 3.48, 5.84, 5.33. Build 117 (the
    build to ship, Jak 1's frame-by-frame trace taken out): dj72g 3.48, 3.48, 5.87, as dj72f. Jak 3
    (tests/jak3_double_jump.sim at Spargus, wascitya-start; Jak 3 GOAL 119): j3dj72 3.50, 3.50, 5.82, 5.43; j3dj60
    3.48, 3.48, 5.84, 5.33. For the player, as in the original: hold A on both jumps, and press the second
    near the top of the first (a quick double press is one jump); with Game frame rate at 60 the full height before
    0.1.8 too.
