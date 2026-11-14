# Jak and Daxter Quest VR 0.1.0: the first release's list

The owner's asks, 2026-11-09. Jak 1, Jak II and Jak 3, all three in the first release. Simple VR: full-body IK, finger
gestures, world collision, two-hand grip, physical melee, with every pad input on the controllers so each game plays start
to finish as it would on a pad. Every VR mechanic is on to start and has its own off switch (and size or strength where
that makes sense) in the in-game VR menu. Launcher and options set up like Ratchet & Clank Quest VR's.

Base: Precursor (OpenGOAL fork with a GOAL ARM64 backend, GLES 3.2, an Android launcher), pinned at 8c6f2c6e.
Owner's discs (C:\Users\Shadow\Downloads): Jak 1 PAL (SCES-50361), Jak II US v2.01 (SCUS-97265), Jak 3 PAL (SCES-52460).

Status marks: [ ] not started, [~] in progress, [x] done (with how it was checked).

## A. Base and every disc version
1. [x] PC build of the fork (gk, goalc, decompiler, extractor): tools/build_pc.bat. Built 2026-11-11 (logs C:\JakQuest\build_pc_10.log to _12): the "still dirty after 100 tries" loop was the root CMakeLists.txt configure_file of compile_commands.json (CMake 4.4 writes it after build.ninja), now file(COPY_FILE); curl's Makefile.inc.cmake written only when changed; kmachine.cpp included jak_vr.h only on Android.
2. [~] Extract and compile each game from the owner's disc on PC; boot to play in each.
   2026-11-11: Jak 1 PAL (SCES-50361, "pal" config) extracted and compiled with the VR hooks (547 targets, log
   C:\JakQuest\compile_jak1.log); boots headless (gk --no-display) to the title's logo loop for 60 s, pc-vr linked
   before cam-update (C:\JakQuest\boot_jak1.log). Jak II US v2.01 the same (934 targets; boots headless to "enter title",
   boot_jak2.log). Not yet: Jak 3 PAL; a boot with a window and the simulated headset; play.
   2026-11-12: Jak 3 PAL compiled too; all three reach play with a window on the simulated headset (tests/jak1_play.sim,
   jak2_play.sim, jak3_toplay.sim): Jak 1 at Geyser Rock, Jak II out of the fortress, Jak 3 in the desert after the opening.
3. [~] **Every disc version starts and draws the same.** OpenGOAL compiles the game from source per version
   (`PAL_VERSION` and friends in goal_src), so every patch here is source, never an address or offset tied to a disc.
   Check each patch for version branches (`#when PAL_VERSION`, `(-> *setting-control* ...)` PAL timing, text ids). PAL
   runs the game at 60 frames like the US disc (the fork's display.gc), not 50. Run the owner's three discs on PC and on the
   Quest; for the versions not in hand (Jak 1 US v1/v2 and JP, Jak II PAL/JP/KO, Jak 3 US/KO), compile the patched source
   with each version's flags so nothing breaks unseen. The launcher says which version it found.
   2026-11-14: [~] There are no per-version flags to compile with: goal_src has one source for every disc (no
   PAL_VERSION; Jak 1's only switch is *jak1-full-game*, the developers' own build, off for everyone; PAL is the
   runtime's video-mode, which the fork's display.gc keeps at 60 frames). What differs between discs is the extractor's
   data side: decompiler/extractor/extractor_util.cpp knows every retail version (Jak 1 US black label and later
   printing, PAL, Japanese, Korean; Jak II US, PAL, Japanese, Korean; Jak 3 US, Greatest Hits, PAL, Korean) by serial
   and program hash, each with its decompiler config. Our patches are all source with no version branch, so every disc
   builds the same code. Done: the launchers say which disc they found (launcher/common DiscVersion.java reads
   iso_data/<game>/buildinfo.json, which the extractor writes; the PC launcher's game card "Your disc: European (PAL),
   SCES-50361", the Quest launcher's game panel the same in its six languages). javac checks pass (PC -Werror; the
   Quest app against android.jar). Left: the owner's discs on the Quest (item 32's build).
4. [~] Quest toolchain: decided 2026-11-14 (owner: "do what makes the most sense"): Ratchet's own, NDK 27.2.12479018,
   compileSdk and targetSdk 34, and its unpacked OpenXR loader (build.gradle -PopenxrLoaderDir or
   JAKVR_OPENXR_ANDROID_DIR), so nothing is downloaded. The ARM64 compile check passes (libmain.so linked; miniaudio's
   fileno declaration left out on Android, where NDK 27.2's stdio.h has it). Left: the APK itself, at the end.
35. [~] **Up to date with OpenGOAL, all its features kept** (owner, 2026-11-12): Precursor merged up to upstream
    open-goal/jak-project's latest release before the build (Precursor is pinned at 8c6f2c6e, behind upstream), and
    everything OpenGOAL has stays working: mod support (the mod launcher's mods and custom levels load as on PC), texture
    packs, the PC settings menus, speedrun mode, the PC port's options. Our changes kept as a patch on top so each
    later upstream release goes in the same way (tools/update_upstream.sh), as Ratchet's OpenRAC patch.
    2026-11-12: Precursor (8c6f2c6e, its own latest) forked upstream at e0dc1155 (2026-07-25, #4358); upstream is 68
    commits on (da5d3486, 2026-11-10; last tag v0.3.9). A trial merge (git merge-tree) conflicts in 39 files, nearly all
    the GOAL compiler's ARM64 backend and the kernels' ARM64 code: upstream since added its own ARM64 backend (Apple
    Silicon, #4390, and the goalc arm64 fixes #4384-4386) beside Precursor's. Plan: merge in a Precursor worktree, take
    upstream's backend (the OpenGOAL team keeps it up from now on) and keep Precursor's Android layer (GLES, the Android
    app and launcher, paths), then PC build and the three games' sim tests; the ARM64 side checked with the Quest build.
    2026-11-14, **texture packs in both launchers** (owner: "this should have opengoal mod support and a section in the
    launcher similar to our openrac work"): launcher/common TexturePacks.java (the OpenGOAL launcher's zips: pictures
    under texture_replacements, metadata.json; on/off, order, the first in the list wins; unpacked safely, 3 GB at
    most) shared by the PC launcher's Mods tab (each game's packs; a change marks the game to build again, its card
    offers it, and the set-up puts the packs on into data/custom_assets before the decompiler) and the Quest's options
    page (Mods: add, on/off, order, remove; "Prepare again" goes to the disc step; InstallService links custom_assets
    part by part and makes this game's texture_replacements from the packs, then drops the copies). The Quest keeps
    Precursor's mod packages. Checks: TexturePacksTest (C:\JakQuest\sim\texpack_test, 23 checks: both zip layouts,
    another game's pictures and a path out of the folder left out, order, on/off, remove, apply, changed) pass; javac
    -Werror on the PC launcher and the Quest app against android.jar pass.
    2026-11-14, **a real pack through a full build** (C:\JakQuest\sim\batch15.sh): a zip as the OpenGOAL launcher's,
    Geyser Rock's sand, grass and beach rock (training-vis-tfrag: bch-beach-01, tra-grass, tra-beachrock) in plain
    magenta, added and put in by TexturePacks (the launchers' code; "changed" true before, false after), Jak 1 built
    again as "Build the game again with them" does (extractor -f -d -c): the decompiler's "Replacing
    training-vis-tfrag/..." for all three, and jak1_play's Geyser Rock magenta where its sand and rocks are. Then the
    pack off, built again: nothing replaced, Geyser Rock as before, custom_assets as before.
    2026-11-13, **merged (v0.3.9)**, with one change to the plan: **Precursor's ARM64 backend kept**, not upstream's.
    The two model registers differently (Precursor `Register(id, is_arm64_vector)`, upstream separate vector ids),
    Precursor had fixed the same encoder bugs the same way, and the Quest build runs on Precursor's. Everything else
    is upstream's.
    - How: a file only upstream's ARM64 commits changed keeps Precursor's version; a file they and other commits
      changed gets Precursor's version plus the other commits; 22 files merged by hand.
    - Where: patches/upstream (BASE, take.txt, remove.txt, fixups.patch). tools/setup.sh rebuilds the merged tree
      from Precursor's commit and the public tag, and checks it against BASE's tree id. The port's patches are now made
      against that tree (refs/jakvr/base).
    - Next time: tools/update_upstream.sh (start, finish, move) and docs/UPDATING-OPENGOAL.md.
    - Putting the port's patches back on top clashed twice: the top-level CMakeLists (upstream fixed the same
      reconfigure loop) and curl's CMakeLists (our fix redone for the new curl).
    - Checked on PC (build 71): gk, goalc, decompiler and extractor built; the three games compiled (548, 935 and
      1166 targets); each played on the simulated headset. Jak 1 at Geyser Rock and Jak II in the fortress, first and
      third person; Jak 3 in Spargus, first person.
    - Still to check: the ARM64 side, with the Quest build.

## B. Roadblocks in the fork (each game start to finish)
5. [ ] Jak 1 pickups on ARM64 (power cell in Sentinel Beach, green eco, some orbs; fork issues 11, 13, 14).
6. [ ] Jak II crashes during play (fork README, issue 12).
7. [ ] Jak 3 white screen after its first cutscene (fork README); Jak 3 PAL support (decompiler config has PAL overrides
   but no PAL type files).
8. [ ] Installing on the Quest: decompile and compile on the headset; Jak 3 took 7.3 GB on a phone (issue 18), the Quest 3
   has 8 GB. Bring each game's install under the Quest's memory, and make a failed install say why (issues 7, 9).
9. [~] Vehicles and minigames playable in VR: they switch to third person on their own where first person does not fit
   (Jak 1 Zoomer, Flut Flut, fishing, periscope, Billy; Jak II and 3 vehicles, turrets, JET-Board, Titan suit, races).
   In: each game's vr-first-person-ok? (pc-vr.gc) gives third person for these states. Jak 1: the Zoomer (racing),
   Flut Flut, tube, snowball, poles, fishing, the periscope, Billy, warps and scripted states. Jak II and 3: the pilot,
   JET-Board, Titan suit, Daxter alone, rails, halfpipe, tube, Flut Flut, poles, turrets, teleports, death and
   scripted states. First person comes back by itself after. Not yet played one by one in the sim.
   2026-11-14, sim (JAKVR_TEST_CONTINUE): Jak 3's buggy (tests/jak3_buggy.sim, wasdoors-desert-turtle): the garage's
   vehicle ring, X picks the buggy, the view goes to third person by itself ("[vr] view in play: third person (the
   game's state ...)") and it drives out of Spargus into the desert. Found there: the hands moving in a vehicle threw
   punches and spin kicks (Square and Circle in the buggy); Physical melee is now on foot only. Jak 1's Zoomer from
   firecanyon-start cannot be ridden in a new game (the game asks for power cells): still to do with another continue
   point, and a Jak II vehicle. Jak II's JET-Board (tests/jak2_board.sim, JAKVR_TEST_BOARD=1 gives it, in Haven City):
   the right stick's click puts him on it (target-board-start), the view goes to third person by itself and he rides;
   the right grip gets him off and first person comes back. Jak 1's Zoomer from misty-bike (tests/jak1_zoomer_misty.sim):
   B by it gets him on (target-racing-get-on), third person by itself.
   Fire Canyon pushing Jak back from the lava (Keira: not enough power cells) left the eased view 1 m ahead of him
   and his body held behind the eyes, its arms through the view: the body now glides with the view's ease (item 23).
   2026-11-14, still there after that: walking into the lava, his tunic, belt and arm filled the first-person view
   for a second at a time. Traced frame by frame (JAKVR_BODY_TRACE=2 and the skinning's own head point, bones.gc): his
   bones as posed were right, but the game's animation gives his root node (node 2, its matrix read raw from the
   animation data) a last column up to 0.0019 off 0 0 0 1 in some moves, every bone under it inherits it, and the
   skinning (bind pose x bone x camera) multiplies the camera's place by that corner, so his whole body moved toward the
   world's middle by that part of the eyes' distance from it: 0.27 m at Fire Canyon, 190 m out, into the eyes. The
   game's own picture has the same small error (unseen from behind him). body() now makes each of Jak's bones affine as
   it takes them and as it writes them (jak_vr.cpp make_affine; also Daxter's and the gun's bones it carries, and other
   players' bodies), all three games. Sim (C:\JakQuest\sim\lava_alt.sim, firecanyon-start): the head point on its bone
   in all 1805 frames, the view clear through the push back.

## C. Seeing the games in VR (shared C++ for all three)
10. [~] OpenXR in gk: Ratchet's xr.h / xr_openxr.cpp (GLES on Quest, GL on PC) and xr_sim.cpp for PC tests. Compiles on PC. 2026-11-11: synced with Ratchet 0.1.19 (pad.h, rig.h, xr.h, notice.h, xr_openxr.cpp, xr_sim.cpp; Jak's drawn_views and names kept): the left thumb rest's own sensor, foveated rendering, the frame's work and missed displays in [vr-perf], the game threads (EE, EE-Worker, IOP) given to the headset's fast cores. Old copies: the session scratchpad vr_before_sync.
11. [~] Two eyes: the frame's DMA chain drawn once per eye with an eye matrix (the eye's offset and its own frustum)
    applied to every camera matrix the renderers upload; texture uploads and animation once a frame. The game's own
    culling widened to cover both eyes.
    2026-11-12: Jak II and 3 clear the screen only in BlitDisplays, drawn in a frame's first pass (the HUD's), so the
    left eye kept the right's picture under its own (black apart from particles in the sim): each eye and HUD pass now
    clears its target (opengl.cpp clear_render). Both eyes checked in Jak II's play (C:\JakQuest\sim\j2_play2, 3).
12. [x] Geometry the CPU transforms (Jak 1 ocean, Jak 1 shadows, direct and primitive draws, sprites' screen math)
    at the right depth in each eye.
    2026-11-13: what the game puts on the screen itself (emulated VU programs: the ocean's mid and near parts in all
    three games, ocean_common.vert; Jak 1's shadow volumes, shadow.vert) is reprojected per eye: a shader with a
    `vr_reproject` uniform gets, from Shader::activate, the matrix taking the camera's clip position to the eye's
    (jak_vr.cpp reproject_matrix: the background's camera, its inverse, and the eye's shift between them; the identity
    outside eye passes and for the shadow's last whole-screen draw). Checked on PC: the matrix shifts x only, by 0.222 x
    (z + 1) NDC (parallax growing with nearness, nothing at the far plane), as a 6.4 cm eye distance gives; Geyser Rock's
    sea and Jak's shadow look right in both eyes (tests/jak1_ocean.sim; JAKVR_REPROJECT=0 turns it off to compare).
    Jak II and 3's shadow volumes are in the camera's space: their perspective matrix gets the eye's shift as Merc's does
    (Shadow2.cpp). The sky and the far ocean (direct draws) are at the far plane, where both eyes agree; sprites and
    glow were already shifted (Sprite3, Sprite3_Glow); DirectRenderer2 is unused. Also found: Jak 1's particle launch
    queue (32) filled thousands of times a run with the VR view's particles all round ("queue is full", launches
    dropped): 256 now (sparticle-launcher.gc), none in the same run since.
    2026-11-14: Jak 3's Spargus light beam whited out the eyes as Jak walked into it (tests/jak3_beam.sim): its glow
    draws its flare 5 m nearer the eye than the light, and in first person the eye comes within that; under 1.5 m the
    flare passed the game's most size (2048) and filled the view. In first person a glow now fades as its flare comes
    within 2 m of the eye and is gone at half a metre (jak_vr.h glow_near_fade; Jak II and 3); the sun and sky glows
    are untouched. JAKVR_GLOW_TRACE=1 logs the near ones.
13. [~] The head in each game's camera (update-camera in each game's cam-update.gc): first person through Jak's eyes, and
    a third-person view; smooth and snap turn; height; recentre with both sticks clicked (L3+R3).
    2026-11-11: the hook in all three games (goal_src/jak1|jak2|jak3/pc/pc-vr.gc, called before camera-rot in each
    update-camera; pc-vr-frustum! in each update-math-camera; the game kept at 4:3 in VR, jak1/pc/pckernel-common.gc).
    Jak 1 and Jak II compile and boot with it (VR off). First person sits on Jak's root plus 1.5 m, as Ratchet's
    anchor_now (his neck bobs with each step), a jump of the root eased over 0.12 s (Ratchet 0.1.18's scene eyes), a
    cut past 4 m taken at once; a cutscene through his eyes uses his neck joint raised 10 cm; crouched by the button
    the view drops 55 cm. Third person chosen from each game's own flags (Jak II/3 focus-status: pilot, board, mech,
    indax, rail, tube, flut, pole, turret) and state names.
    2026-11-12: the eyes now sit at Jak's own eyes: the height above his root is learned over 2 s from his neck joint
    (raised 10 cm and 8 cm ahead) while he stands, where 1.5 m had put the view in his chest; the view follows his neck's
    lean ahead (eased 0.2 s, -0.2 to 0.8 m), so his collar and shoulders no longer fill the bottom of the view when he
    runs; the crouch button drops 37% of the eye height. His head and Daxter (on his shoulder: the target's sidekick,
    as no game sets *sidekick*) are hidden in first person. Checked in the sim: Jak 1 walking at Geyser Rock with no
    collar, no Daxter in view (C:\JakQuest\sim\j1_play7, 8).
14. [x] HUD, menus and subtitles on a panel in front of the player. In: the frame's HUD pass draws the game's HUD
    buckets into a panel that follows the head, 160 cm ahead to start (jak_vr.cpp end_frame), sized, placed and raised
    by the VR menu's HUD size, HUD distance and HUD height. The pause menu over play goes there too, with the world
    still in 3D around it. A load or blackout shows the game's flat screen on a panel placed in front of you.
15. [x] 60 game frames on 72, 90 and 120 Hz (Ratchet's kept eyes). In: the headset paces the display loop, and the game
    is let go at 60 frames a second whatever the rate (jak_vr.cpp release_game). A display frame without a new game
    frame shows the last eyes from the poses they were drawn at (drawn_views), so the world holds still under the head.
16. [x] Cutscenes: first person (through Jak's eyes) or third person (in VR around you, or on a screen), a VR menu choice.
    Holding the left menu button about 1 s switches first and third person in play; a short press is the pause menu.
    2026-11-12: Jak II and 3 act their scenes with high-res models (skel-jak-highres, skel-jakc-feet, skel-darkjak-...),
    not Jak: the first-person eyes go in the scene's own Jak (found through *scene-player*'s actors by art name and a neck
    joint), his head hidden, the high-res Daxter hidden within 1 m of the eyes, the view turned with his body when he turns
    past 45 degrees from it. A scene without Jak in it (or with him over 120 m from the game's camera) is seen in third
    person around you. The letterbox bars are left out while the eyes see the game in stereo (main.gc in each game, Jak
    3's blit-displays.gc). Checked: Jak 3's opening and the palace in first person (C:\JakQuest\sim\j3_dbg5).
    Owner, 2026-11-12: "all scenes/cutscenes that are rendered in game should be shown in vr". 2026-11-12: the title
    and its menus are now seen in VR around you from the game's own camera, and a pause menu over play keeps the world
    in 3D (first person stays first person, no recentre going in or out) with the menu on the HUD; only a load or
    blackout goes to the flat screen (jak_vr.cpp stereo_now). None of the three games has disc movies.
17. [~] Graphics at their best to start: the game's own LOD and draw distance settings at the most (the PC port's
    "max level of detail" / actor culling options), no pop-in, all in the launcher's Graphics tab with Ratchet's set
    (refresh rate, eye resolution, MSAA, texture filtering, Max draw distance). Eye resolution starts at 100% of the
    headset's own (owner, 2026-11-09: not higher to start).
    In (2026-11-13): "Max draw distance", on to start (vr_options max_draw_distance; the VR menu's and the launcher's
    Graphics row). While VR draws, each game's pc-vr.gc vr-graphics! sets the PC port's own settings to their most every
    frame: actor culling off (ps2-actor-vis?), no vis cells in Jak 1 (use-vis?), no PS2 LOD distances (ps2-lod-dist?),
    every particle effect in full (ps2-parts?), shadows at any distance (ps2-shadow?), environment maps on, and the
    level, its objects, the sea and the actors forced to their highest LOD (lod-force-*); Jak II and 3 also draw each
    actor past its own vis-dist (entity.gc). Off, the player's own settings. The launcher's Graphics tab has the rest of
    the set (eye resolution 100%, refresh rate, MSAA 2x, anisotropic filtering 16x, smoother frames, foveation, boost).
    Still to do: a pop-in check by sim in each game (a long walk with pictures of the far ground).
    2026-11-14, sim with Max draw distance on and off (tests/jak1_popin, jak1_popin_village, jak2_popin_city,
    jak3_popin, jak3_buggy; settings C:\JakQuest\sim\popin\on and off): no pop-in seen at Geyser Rock, Sandover, Haven
    City's slums or Jak 3's desert; on and off look the same there (the PC port's own defaults already draw the far
    levels; Haven's people come and go by the game's own traffic, not by distance). Jak 3's dark blobs at Spargus are
    its lava crust (the game's own).
18. [~] Smoother frames (AppSW), on: XR_FB_space_warp with motion and depth from OpenGOAL's renderer. The OpenXR side
    is in (Ratchet's); nothing calls space_warp() yet, so no frame is warped: the renderer's motion pass is still to do.
    2026-11-13: the motion pass is in (jak_vr.cpp "Smoother frames (AppSW): the eyes' motion"): each eye's depth, as
    drawn, unprojected with this frame's camera and projected with the last frame's (the background's camera, noted in
    background_common.cpp first_tfrag_draw_setup), into the headset's motion and depth targets, then space_warp() at
    the frame's end. The view's own motion: a turn, a walk, the head. What moves in the world by itself (enemies, Jak in
    third person) gives no motion of its own yet; the player's own body within 1 m in first person gives none (it moves
    with the view). A cut (over 4 m, or 25 degrees a frame) gives none. PC sim (tests/jak1_motion.sim): still 0,
    turning right -0.040 NDC a frame across, left +0.040, walking 50% of the picture moving out from the middle.
    Untested on a headset. Found on the way: the first-person stick turn was backwards (pushed right turned left; the
    turn control gives radians left, the rig's yaw grows left), fixed in jak_vr.cpp.
33. [~] Foveated rendering (Ratchet 0.1.19's, on at medium; carried over as Ratchet is the reference): GL_QCOM_texture_foveated
    on each eye's picture (jak_vr.cpp run_pass calls foveate), option "foveation" 0-3. Quest only; untested.
19. [~] CPU and GPU boost, on: XR_EXT_performance_settings boost (in xr_openxr.cpp, option cpu-gpu-boost; untested on the headset).
36. [~] **The performance options of Ratchet's launcher, each working** (owner, 2026-11-12): Smoother frames (AppSW,
    item 18) that really warps frames, Foveated rendering (33), CPU and GPU boost (19), refresh rate, eye resolution,
    MSAA, texture filtering, Max draw distance (17). A **Recommended performance options** section in the launcher's
    What's new pop-up and on the release page (which to turn on for Quest 2, 3 and 3S, and for PC VR).
    Owner, 2026-11-12: "dont forget the graphics options in the launcher like 90 hz, 120 hz, resolution, etc.": the
    launcher's Graphics tab has the refresh rate (72, 80, 90, 120 Hz where the headset has them), the eye resolution,
    and the rest above, each written to vr-options.txt (refresh-hz, resolution, foveation, space-warp, cpu-gpu-boost,
    max-draw-distance are there already) and read by the game at start; the in-game VR menu (29) has the same rows.
    2026-11-13: each is in and read by the game: smoother frames now give the headset motion and depth (item 18),
    foveation, boost, refresh rate, eye resolution, MSAA, anisotropic filtering and Max draw distance (17) from
    vr-options.txt; the Graphics tab has Performance, Balanced and Quality presets, and What's new
    (launcher/common/texts/whats_new.txt, also the release notes) has "Recommended performance options" for Quest 2,
    Quest 3 and 3S, and PC VR. Still to check: each on a headset (the Quest build, item 32).

## D. Controls (each game's every pad input)
20. [~] The controllers as the game's pad in scePadRead (one place for all three games): sticks, A/B/X/Y, triggers,
    grips, Start, Select, L3/R3. The table per game and per phase (play, menu, vehicle) in vr/src/pad.h.
21. [~] D-pad, **to start: the left thumb on its rest makes the right stick the d-pad** (owner, 2026-11-11, as the
    games now get the full gamepad: thumb off the rest, the right stick is the right stick again). The other choice in
    the VR menu and launcher: the left hand held at the head makes the left stick the d-pad (a fair radius, out of the
    way of play). Needed: Jak 1 warp gates and HUD totals, Jak II and 3 gun choice. Done in code: vr_options.h
    dpad_place starts at kThumbRest. The Quest was the only target on 2026-11-11; the owner then asked for equal polish
    on PC VR (2026-11-12), so (2026-11-13) a controller with no left thumb rest (Index, Pico, Cosmos, Reverb, Windows
    Mixed Reality, the wand: xr_openxr.cpp m_rest_sensed) gets the left-hand-at-the-head d-pad whatever the menu says,
    and the wand and Windows Mixed Reality gained Start on the left menu button and split touchpad clicks (docs/CONTROLS.md
    "PC VR controllers"). Xbox-style button icons in the games' hints were looked into: OpenGOAL has no
    such option and no mod for it was found, so the owner dropped them (2026-11-11).
    2026-11-11: as Ratchet 0.1.19's (same owner ask there): the left controller held by the side of the head (pad.h
    hand_by_head's place, larger by the menu's reach, with a buzz and a leaving margin) makes the left stick the d-pad and
    its click Select; with "Left thumb rest", the left thumb on its rest (its own sensor, left_rest_touch) makes the
    right stick the d-pad. Not in menus.
22. [~] Vibration: the game's rumble to the controllers.
    Owner, 2026-11-12: haptics as good as Ratchet's. The game's two motors split to the hands (the big motor both, the
    small one the hand that acts), and the VR's own buzzes: a punch or kick landing (the punching hand), a hand meeting a
    wall or floor (by how hard), each gun shot (the gun hand, two-hand grip both, by gun), drawing and holstering, a
    pickup, the d-pad zone entered, menu pointer rows. Strength in the VR menu.

## E. Body and hands
Owner, 2026-11-12: the first build should be basically perfect in looks and play: body IK, finger gestures, fingers
bending against the world, controller mappings, satisfying gun handling and proper haptics, as OpenRAC VR's, without
its full-immersion features.
23. [~] Full-body IK on Jak's skeleton in each game: hands to the controllers, body under the head, legs from the game's
    animation; body shown in first person (no head).
    Skeletons (C:\JakQuest\sim\joints_jak1|2|3.txt): the same names in all three: neckB (index 7 in Jak 1, 8 in II/3),
    Lshould, Larm, Lforarm, sk_lhand and the R side, fingers lthumA/B, lindA/B, lmidA/B, lringA/B, lpinkA/B; Jak II and 3
    also gun and board.
    Owner, 2026-11-12: the shadow shows the VR body with its head, as Ratchet's; the IK "as smooth, responsive, and
    natural as it can be", refined against OpenRAC's (Ratchet quest_vr.cpp "His whole body", OpenRAC moby_pose.cpp).
    2026-11-12: posed in the runtime once a frame after the joint math (jak_vr.cpp body, pc-vr-body! from each game's
    vr-body-update!), in the bones, so the shadow has all of it; the head and Daxter left out of the eyes only (Merc2
    handle_pc_model). As Ratchet's: the body hangs from the player's neck (15 cm under the eyes and behind them as the
    head pitches, so looking down shows the chest, not the collar), held within 0.6 m aside, 0.3 up and 0.8 down of
    the game's place; the hips stay until the head turns 35 degrees past them, then follow (4 rad/s, 8 running, his
    heading while running or in the air); the chest twists toward the head over upper_body and chest; the legs bend
    from the moved hips to the game's feet (knee the game's way, a little forward), so a lean or a real crouch keeps
    the feet planted; the head turns as the player's (shadow). Arms: two bones each the least turn, the elbow down, out,
    back and away from the thumb (palm down puts it out), the shoulder rising with a high hand and swinging toward a far
    one, soft IK near full reach (no elbow snap) then up to 15% stretch, the hand turned as the controller with half its
    roll on the forearm (at most 60 degrees).
    2026-11-12, later: the hands drawn where the controllers will be when the frame is shown (the runtime's own
    prediction a refresh ahead, "predict-hands", on); Body forward (centimetres, as Ratchet's) beside Body height; the
    neck (neckA, neckB) straightened on the chest as made before the body is fitted, so an idle's look up or down no
    longer brings the collar under the eyes (sim j1_body5: the neck sat right, 18 cm behind the eyes looking down, but
    the animated neck put the collar under them). JAKVR_BODY_TRACE=1 logs the neck, its base and the chest from the eyes.
    2026-11-14: the brown strips seen hanging from his wrists toward his body (Jak 1, looking down with the hands out)
    are his own wrist straps' tails, not stretched skin: nodes 19 and 51 (left), 25 and 63 (right) hang 16 to 17 cm
    under each hand, carried with it as the game hangs them (sim jak1_straps, JAKVR_TRACE_JOINTS).
24. [~] Finger gestures: the controllers' touch sensors curl Jak's fingers (fist, point, thumbs up, open hand).
    In code since 2026-11-12 (jak_vr.cpp update_fingers, Ratchet's): the index by its trigger's touch sensor and the
    trigger's pull, the other three by the grip, the thumb by its thumb-rest and button sensors, eased 14 a second; so
    a fist, a point (index off its trigger), a thumbs up (thumb off) and an open hand are the player's own. 2026-11-13:
    every finger curled the wrong way, back over the hand (a fist's tips went 11 cm to the back of the right hand, away
    from its palm and thumb; sim JAKVR_TRACE_JOINTS, Jak 1 and II): the bend axis was the palm crossed with the finger,
    now the finger crossed with the palm. Traced after: the right fist's tips 12 cm toward the palm and 10 cm back, the
    left's the mirror; Jak II's right hand now wraps the Morph Gun's handle (sim j2_gun10 g-down). This also turns the
    gun grip, the two-hand hold and the fingers resting on walls the right way.
25. [~] World collision: hands stop at walls and floors, with vibration; the fingers bend against what the hand rests on.
    2026-11-12: each frame the runtime asks the game four sphere probes (pc-vr-hand-probes!, jak_vr.cpp hand_probes):
    each palm from its shoulder to the controller (5 cm), each hand's fingers from the palm 16 cm ahead (2 cm). Each
    game's vr-hand-probes! sweeps them against the level and solid objects (Jak 1 fill-and-probe-using-line-sphere with
    background, wall and ground objects; Jak II and 3 a collide-query, backgnd and obstacle, Jak ignored). The hand stops
    where its palm met something (only what is drawn: the controller's path stays the punches' and gestures'), a buzz as
    it meets it by how fast (0.2 to 1); the fingers curl over what is in front of them (eased, 16 a second). All three
    compile; seen in the sim only as not breaking the body yet (no wall test).
26. [~] Physical melee (all three): a real punch is the punch, a real crouch and an upward punch the uppercut; the spin
    kick stays on its button. Jak 1 yellow eco: shot from the punching hand, where it points.
    2026-11-11: a forward punch is Square; crouched (the head, the trigger's L1 or the game's duck) an upward punch is
    Square from the crouch, the uppercut; a fast sweep across is Circle (the button still works). Physical crouch as
    Ratchet's update_physical_crouch: measured at the neck, standing the highest seen settling 1 cm/s, 7 cm hysteresis,
    holds L1 (the crouch in all three games) only on the ground or already ducked, never in the air (the dive). Jak 1's
    yellow eco (target-yellow-blast and target-yellow-jump-blast in target2.gc) goes where the hand that punched points,
    from that hand in first person (pc-vr-hand-shot!, jak_vr.cpp hand_shot; "hand-aim" and "gun-angle" options).
    Compiles; untested in play.
    Owner, 2026-11-13: the hand motions must not set off melee by accident on a gamepad's mapping; as OpenRAC's wrench
    on the right grip, gripping brings out the melee; an option in the VR menu, on to start. Done (jak_vr.cpp fill_pad,
    vr_options "melee-grip", row "Fists on the right grip"): on foot (the game's first-person-ok: not on the board, in a
    vehicle, the Titan suit, on a rail or pole), the right grip held makes Jak's fists (all fingers and the thumb
    closed, a light buzz) and only then do the hands punch, sweep and uppercut (both hands: in Jak II and 3 the left
    grip is Dark Jak's and the gun's front; Jak 1's left grip, unused by the game, makes fists too). Not while the gun
    is in the right hand (the grip holds it). The grip's R2 on foot (Jak II and 3: the JET-Board on and off, Jak 3's
    Light Jak ended) moves to the right stick's click (its R3 was the games' look-around, the head's in VR); off foot
    the right grip is R2 as before (Jak II's car height, the board's end). Each hit is logged ("[vr-pad] punch", "spin
    kick", "uppercut", "fists up"). Test: tests/jak1_melee.sim.
    2026-11-14, sim (tests/jak1_melee.sim): the right grip's fists, a punch, a spin kick, then the left grip's fists
    and a punch, each in the log, the fists closed in the pictures. Physical melee is now on foot only (it pressed
    Square and Circle in Jak 3's buggy, item 9). Jak II and 3 (jak2_melee, jak3_melee): the right grip's fists, a
    punch, a spin kick and the uppercut from a real crouch, each in the log.
27. [~] Two-hand grip (Jak II and 3 gun): the left hand on it steadies and aims it; the gun aims by the hand.
    2026-11-12: the game makes the gun from his gun joint as it was when the gun's process ran (the pose of the frame
    before), so the runtime carries the gun's bones on to the gun joint as posed now (gun-util.gc vr-gun-fill!, jak_vr.cpp
    body step 7): it stays in the hand, not a frame behind. Drawn, with Aim with your hand: the hand is turned so the
    barrel (the gun's bone 13) points where the right controller's aim pose points, raised by Gun angle (Ratchet's
    pointing_pose), and the shot and laser go there (vr-gun-aim! in each gun's post, after the game's own aim, its aim
    at enemies included). Two-hand grip as Ratchet's support grip: the left grip squeezed with the left hand 6 to 40 cm
    ahead of the right along the aim, within 12 cm of it, takes the gun's front (not L2); then the gun points from the
    right hand to the left and the left hand stays on it, fingers closed, until the grip lets go (under 0.35); a squeeze
    begun elsewhere stays L2's. The right hand's fingers close on the handle while it is drawn. Test aid: JAKVR_TEST_GUN=1
    gives Jak the gun (tests/jak2_gun.sim).
28. [~] Hip or shoulder grip draws the gun (Jak II and 3), back there and let go puts it away; only in those zones.
40. [~] **Body shown** (owner, 2026-11-14: "body representation options in the in game vr settings. like hands only,
    hands and arms only, arms hands and legs only, etc."): what of Jak the eyes see in first person, in the VR menu's
    Body rows and the launcher's VR Body tab (vr_options BodyShown, "body-shown"): Whole body (to start), Body and
    arms, Body and legs, Arms and legs, Arms only, Legs only, Hands only. His hands always; the rest is still posed
    and his shadow keeps all of him (as Ratchet's "Body parts"). In: jak_vr.cpp body() marks the nodes left out (an
    arm's collar goes with the body, its upper arm and forearm with the arm, a leg all from the thigh, the body every
    other node but the head's); Merc2 gives each such bone pad.x 1 and the merc and emerc shaders drop the fragments
    whose skin those bones carry more than half of (vr_cut), so nothing is moved or stretched and what stays ends at
    the joint (OpenRAC collapses joints onto the elbow instead, which stretches the skin between).
    Sim 2026-11-14 (tests/jak1_body_shown.sim for each choice, jak2_body_shown with Legs only and Arms and legs,
    jak3_body_shown with Hands only; C:\JakQuest\sim\body_shown_all.sh): each choice leaves out what it says, in the
    look down and from behind, with clean ends and nothing stretched; a cutscene through his eyes (Jak II and 3's
    high-res Jak) is cut the same. Found: Jak 3's cape and scarf are cloth (cloth.gc, the prim renderer), not his merc
    model, and stayed in the air with the body left out: now kept out of the eyes with his body (pc-vr-body-cut, bit 1).
    2026-11-14: their strips are emptied there too (cloth.gc update!): the game empties them only while Jak is on its
    own screen, and in first person he sometimes is not (traced: first person, off screen), when they drew as last
    made. The grey shape seen under the view in Jak 3 with Hands only is his own shadow on the floor (it stays with
    his whole model and his cloth left out, the floor's grating through it), kept as Ratchet keeps his.

## F. Menus and launcher
29. [~] In-game VR menu: every option above, on/off and adjust, pointed at with the hand. From the start, as Ratchet's
    (owner, 2026-11-12): HUD (size, distance, height, follows the head or stays put), world scale, snap or smooth turn
    (snap angle, smooth speed), seated, view and body height, cutscene view, d-pad place, vibration strength, every
    body, hand and gun option, and the graphics ones that change while playing. The rows in vr_options.h.
    Owner, 2026-11-12: movement goes where the head looks, to start; the other choice where the left hand points
    (vr_options MoveBy, row "move-direction": "Move where you look" / "Move where your left hand points").
    Owner, 2026-11-13: "Fists on the right grip" (vr_options melee_grip, on; item 26) is a row of its own.
    Owner, 2026-11-13: **smooth turn on to start** (vr_options turn = kSmooth, 120 degrees a second; snap 45 the other
    choice), in the launcher's VR Controls too.
    Owner, 2026-11-13: **a menu pointer that works with both controllers, as OpenRAC's** (Ratchet's pointer_hands and
    menu_pointer): on the games' own menus (pause, title, options) and the VR menu; with "Either hand" (Menu pointer
    hand, to start) the right points until the left's trigger is pulled on the menu, then the left; the pointing hand's
    trigger clicks (Cross on the game's row under the dot). jak_vr.cpp menu_pointer, vr_menu.cpp, pc-vr-pointer.
    2026-11-13: the menu (vr_menu.cpp make_rows) has a row for every option in vr_options.h: View, Cutscenes, Talks in
    first person, Turning, Snap turn angle, Smooth turn speed, Seated play, View height, World scale, Third person world
    scale, HUD size, HUD distance, HUD height, **HUD follows your head** (new: off, the HUD stays put ahead of you and
    moves only once you look over 60 degrees away, after a recentre or a load; jak_vr.cpp place_hud; PC sim
    tests/jak1_hud_stays.sim: placed at 0, kept at a 40-degree look, placed again at 90), Move direction, D-pad, D-pad
    reach, Menu pointer hand, Vibration and its strength, Vibration on walls and floors, the Body rows (Body, Finger
    tracking, Hands without lag, Hands stop at walls, Physical crouch, Crouch depth, Arm length, Body height, Body
    forward), Combat (Physical melee, Fists on the right grip, Melee speed, Aim with your hand, Two-hand grip, Draw from
    hip or shoulder, Gun angle), and Graphics (eye resolution, refresh rate, smoother frames, foveation, boost, max draw
    distance, anti-aliasing, anisotropic filtering: marked for the next start). The launcher's tables
    (launcher/common VrOptions.java) have the same rows and keys. Still to check: the pointer on a headset.
30. [~] Launcher (from Precursor's, which installs all three games): the three games, Ratchet's tabs and option names
    (Graphics, VR Body, VR Controls, Controls help), What's new, logs to send back.
    Owner, 2026-11-13: logs as easy to get as OpenRAC's, for bug reports on Discord: one button in the launcher (Quest
    and PC) gathers the games' logs, the VR options and the launcher's own log into one zip in an easy place (the
    Quest's Download folder; on PC beside the game, its folder opened), says where it is, and the README and What's new
    say how to send it.
    Owner, 2026-11-13: **a Controls section in each launcher** breaking the bindings down by controller type (the
    tables of docs/CONTROLS.md: on foot, motions, board and vehicles, menus, and "PC VR controllers": Touch, Index,
    Windows Mixed Reality, Vive wand, Pico, Cosmos, Reverb). The PC launcher shows every controller type (a picker or
    one section each); the standalone Quest APK's launcher shows the Quest Touch mappings only.
    In, both launchers (2026-11-13 review): the PC launcher (pc/launcher, after Ratchet's: tabs Play, Graphics, VR Body,
    VR Controls, Controls, Logs, Credits; the Graphics presets; PcLogs' one-button zip in Downloads\JakQuestVR) and the
    Quest's (Precursor's LauncherActivity with our What's new, report zip to share, mods); the option tables and the
    Controls help (parsed from docs/CONTROLS.md, a controller picker on PC, Quest Touch only on the Quest) shared from
    launcher/common. The PC launcher compiles (javac --release 17 -Werror, 2026-11-13). Still to check: both launchers
    run with a release build (end of the list), the Quest's on the headset.
    2026-11-14: the Quest launcher's texts were still Precursor's, for phones: in all six languages it now names itself
    Jak and Daxter VR (updates, reports, notifications, About), says headset for phone, and its About and disc-copy
    texts are the port's (built on Precursor and OpenGOAL, report problems here; the ISO to the headset's Download
    folder from a PC); the level names keep "Precursor". VR Options' hint said the menu button held in play opens the VR
    menu: it switches the view there; the VR menu is the button held in a menu (pause first) or Options, VR Options.
    The updater's User-Agent and the install log's first line name the port. Quest javac passes.
37. [~] **The launcher updates itself** (owner, 2026-11-12), as OpenGOAL's launcher and Ratchet's do: checks the
    GitHub release on start, shows what's new, downloads and installs the new APK (Quest) or build (PC) with a tap.
    In: PcUpdater (the latest release's Windows zip from moistman42069/JakAndDaxter-Quest-VR, checked by GitHub's
    SHA-256 and size, unpacked, swapped in by a script once the launcher closes; UpdatePolicy limits it to that
    repository's release files) and the Quest's Updater (the APK from the same repository, BuildConfig.UPDATE_REPO;
    same signing key so the data stays). Works only once the repository and a release exist (item 38, the owner's
    word).
34. [x] A small, neat link in a corner of the launcher's main page to Ratchet & Clank Quest VR, the standalone Quest port
    (https://github.com/moistman42069/RatchetAndClank-Quest-VR/releases/latest), e.g. "Also: Ratchet & Clank VR" with
    its icon; opens in the browser. Asked by the owner 2026-11-11.
    In both launchers (PcLauncher RATCHET_URL in the main page's corner; LauncherActivity's shelf corner).
39. [~] **Multiplayer, built in** (on to start since 2026-11-14; owner, 2026-11-12: "pretty sure theres a multiplayer mod similar to
    what we have with openrac ... make it function and implement it in a similar fashion. have it in the mods section of
    the launcher, already installed, just disabled"), as Ratchet bundles OpenRAC's Multiplayer (its docs/MULTIPLAYER.md):
    the community's own mod, pinned to the version PC players use so headset and PC games share parties, VR and flat
    players alike, each VR player's head, hands and fingers shown on their figure when the host is on our build.
    2026-11-12, found: the mod is **Teamruns** (Jak 1 only; in OpenGOAL's mod launcher list). Its game side is
    github.com/JoKronk/teamruns-jak-project (ISC, a fork of OpenGOAL-Mods/OG-Mod-Base rebased on vanilla OpenGOAL, about
    300 files: game/multiplayer.cpp runs a websocket server in gk; remote players and shared cells, orbs, crates and
    tasks in goal_src/jak1). Its client is github.com/JoKronk/teamruns-client (Electron + Angular, latest v0.10.0 of
    2024-09-30, **no license**): it connects to gk's websocket, and players meet through the author's Firebase project
    (Firestore for lobbies and WebRTC signaling, peer-to-peer data channels, one player hosting). No multiplayer mod
    exists for Jak II or Jak 3.
    Plan: Teamruns' game side merged on our tree after item 35 (both on current OpenGOAL), inactive unless the mod is on;
    on PC VR the Teamruns client works with our gk as with a vanilla Teamruns build; on the Quest (no Electron) a client
    built into gk speaking the same protocol (Firestore over HTTPS, WebRTC data channels through libdatachannel, MPL-2.0).
    **Owner's decision, 2026-11-12** ("its opensource, so all good on permission. feel free to implement it, otherwise,
    just follow the openrac approach and at least let people using the same vr builds, see eachother in server browser
    and join"): permission is settled; no need to ask JoKronk. (The client's repo has no license file; the owner counts
    it as open source.) So:
    - **First choice: Teamruns itself.** Its game side merged on our tree; its client shipped with the PC build and its
      protocol built into gk on the Quest; lobbies through Teamruns' own lobby service, so our players and PC Teamruns
      players find each other. VR players' heads, hands and fingers on their figures as Ratchet does it (a word of our
      own on each player's pose that a plain Teamruns game drops unread; docs/MULTIPLAYER.md in Ratchet).
    - **If Teamruns cannot be made to work: the OpenRAC approach** (Ratchet's bundled Multiplayer: one player hosts,
      the others join; a party with a flat game in it still plays).
    - **The least that must ship either way:** players on the same VR build see each other's games in a server browser
      in the game or launcher and join them, on PC VR and the Quest alike.
    - In the launcher's Mods section, installed and off to start (the owner's ask above). Jak II and 3 have no
      multiplayer mod; anything there is new work of ours.
    2026-11-13, what was found and chosen:
    - Teamruns' lobbies cannot be ours: its app reaches Firestore by signing in to the author's one shared account,
      whose password is in its release builds. Using it would be using someone else's credentials: not done. Its app
      also downloads and updates its own game build, so pointing it at our gk does not last.
    - So the OpenRAC approach, on Teamruns' game side: Teamruns' newest branch (jak2-3-multiplayer-support,
      b18149400, 2026-04-15; its multiplayer is Jak 1 only) merged into our Jak 1: gk's websocket server
      (game/multiplayer.cpp), the remote players and the shared cells, orbs, flies and tasks (goal_src/jak1 mods),
      the taunts. Off to start: the socket and the party start only when Multiplayer is on (the launcher's Mods row) or
      when Teamruns' own app starts gk with --socketport. A party is ours: one player's gk hosts, the others' join it,
      and the host's gk does what Teamruns' app does for the game (each player's "position" and pickups in, the
      others' "players" table out, one team: pickups shared). Games found by a beacon on the local network, or by
      address; a list on the internet needs a small lobby server someone hosts (the owner's choice, asked).
    2026-11-13, built:
    - Teamruns' Jak 1 game side merged (gk's websocket, goal_src/jak1/engine/mods and its hooks, the taunts, goalc's
      taunt-info tool). The merge's loose ends fixed: the input display's button glyphs, the projectiles' owner
      argument (mother spider), the collide calls' collide-work (misty cannon), racer.gc's paren, the periscope hooks'
      declarations, two dead art-group imports.
    - Its changes to plain Jak 1 undone where they changed the game with Multiplayer off: vibration and hints on as the
      game has them, no sound-file lookup at every sound, the input display looked for only when switched, and
      init-target's pause settings for the player's own Jak (Teamruns' check left pausing allowed while Jak spawns, so
      with no gamepad the title stopped until Start; level-setup the same). Its in-game cutscene skip, taunt menu and
      party-only logic run only with Multiplayer on (pc-multiplayer-on?).
    - game/party.cpp (asio): host and join, LAN beacons (UDP 8121), the in-gk relay (TCP 8120), the party server's
      protocol, the same build required, what another took in a level taken again when this game loads it.
      tools/party_server.py for games over the internet (tests: C:\JakQuest\sim\party_server_test.py, 15 checks).
    - The VR menu's Multiplayer rows (Jak 1 only): Multiplayer, Your game, Host a game, Join a game, Leave the game.
    - Launchers: the PC launcher's Mods tab and the Quest launcher's Mods card, Multiplayer off, with Your name and
      Party server; party.txt stamped with the release at each start; the Quest's Wi-Fi multicast lock.
    - docs/MULTIPLAYER.md; the PC zip's party-server folder (party_server.py and the guide); credits and notices.
    2026-11-14, two games in one party on the simulated headset (C:\JakQuest\sim\mp_host.sim and mp_guest.sim,
    JAKVR_PARTY_TEST=host and join):
    - Crash fixed: the other players' Jaks crashed the game as they spawned. Teamruns' init-target read the sidekick
      field of a Jak that has none (its leftovers, not #f), so it is set to #f for the others' Jaks.
    - The others' Jaks are drawn whole in the eyes (bones.gc *vr-merc-who*: the eyes left out the player's own head by
      its model's name, which theirs share), and one standing in the player's own body in first person is left out.
    - Every Jak in his own colors (owner, 2026-11-14: the other Jak was blue): Teamruns' team colors tinted the whole
      Jak; the name over him tells them apart (multiplayer.cpp kPartyColor, the player's own too).
    - The names over them drawn in each eye at their depth, a line's height over his head so it never covers them.
    - Shadow2 sorted per eye (the others' shadows).
    - The haze seen in the test's pictures at Geyser Rock is the geyser's own mist (training-part.gc), the same in
      the flat game: not a bug.
    - **Each VR player's body in the others' games** (as Ratchet shares it): in first person his Jak's head, arms,
      hands and fingers as posed by his headset and controllers, in every other game of the party, VR or flat.
      jak_vr.cpp pose_publish puts each named joint in his root's frame into a word (about 600 bytes, base64) sent
      with each update ("vr"), and party.cpp sends it on its own each tick it changed while he stands still (the game
      sends an update only when he moves); pose_other puts it on his Jak before it is drawn, everything under those
      joints carried along. Teamruns' own app is sent the update as before. pc-vr.gc vr-others-pose!.
      Sim (mp_host_pose.sim, mp_guest_pose.sim): the guest, standing still, held his left hand out to the side; the
      host's picture from behind him shows his Jak's left arm out (the game's own animation has it down), the same as
      in the guest's own eyes. The log ("[vr] party: player N's VR body on his Jak") shows each game putting the
      other's body on from the start of play until the guest quit, with no gap while he stood still. Both games exit 0.
    - Test aids: JAKVR_BUCKET_LOG=1 logs, in the left eye, each draw bucket starting and stopping and the share of the
      picture the shadow pass darkens (OpenGLRenderer.cpp, ShadowRenderer.cpp).
    - tools/party_server.py: its 15 checks pass (C:\JakQuest\sim\party_server_test.py).
    2026-11-14, **as Ratchet's Multiplayer, and on to start** (owner: "the ratchet mutiplayer mod lets you choose a save
    for multi, host a game, etc."; "a section in the launcher similar to our openrac work where people can disable the
    multiplayer mod ... feel free to keep it enabled by default"; "games hosted by other vr players allow all vr features
    to be enabled"):
    - **On to start, inert until used.** party::game_on (hosting, joining, in a game, or just left one) is what
      pc-multiplayer-on? now answers, so with Multiplayer on and no game Jak 1 plays exactly as it always has (its
      cutscenes, no taunts, no Teamruns logic). The network opens only when the player first looks for games, hosts or
      joins (Session::open_listening), so no firewall question at every start. vr_options.h and VrOptions.java default
      on; the launchers' Mods keep the off switch.
    - **The VR menu's pages** (vr_menu.cpp; rows now std::string and std::function, made again each frame on a page):
      Host which save? (this game as it is, Save 1-4 with level and power cells, New game), Join a game (each game's
      host, level and power cells, "(other version)"; looking for games while shown), Play which save? (shown on joining:
      the host's progress, this game, Save 1-4, New game), Players (the host's Remove), with Back and B back to VR
      Options.
    - **Saves** (party.h slots/play_*/save_*; kmachine pc-party-save-ask/-made/-copy-into/-done; pc-vr.gc
      vr-party-saves, one job process at a time, run from runs-every-frame): a slot loaded with the pause menu's own
      restore; a new game as the pause menu's; the host's progress made as the save state makes one (save-game! into
      the art control's spare buffer), sent to the member in base64 parts of 36 KB, and loaded as the restore state
      does, into the game and not onto a slot (the member's auto-save off until it saves). The slots read from the
      memory card files' headers (the newer bank).
    - **Removing a player**: LAN, the host's relay tells the player "removed" and drops it; party server, op "remove".
    - **Protocol 2**: ops "to"/"from" (one player alone), "where" (the host's level and power cells for the server's
      list; the LAN beacon carries them too), "remove"/"removed"; protocol 1 refused with "update" words.
      tools/party_server.py: 24 checks pass (party_server_test.py: the relay to one player, the list's level and cells,
      only the host lists or removes, a removed player can join again, protocol 1 refused).
    - **Every VR feature for everyone**: physical melee, hand aim, yellow eco from the hand and the rest are worked out
      by each game itself; Teamruns shares their results (an enemy beaten, a crate broken), so a game hosted by anyone
      (VR or flat) leaves them all on. Each VR player's body shows in the others' games of this release (above).
    - Sim, two games (C:\JakQuest\sim\batch11.sh: JAKVR_PARTY_TEST=host; =join with JAKVR_PARTY_TEST_SAVE=copy): the
      guest found the host's game, joined, asked for its progress, received it (6928 bytes) and loaded it ("save 4
      done"); it then played at the host's place, each game putting the other's VR body on. Both exit 0.
    - Sim, the pages (C:\JakQuest\sim\mp_menu.sim, made by make_mp_menu.py): the stick down to Host a game, each page
      opened and drawn (Host which save?, Join a game "Looking for games...", Play which save? "Join first"), B back;
      "looking for games" only once the list was shown.
    - Sim, two games over the party server with Remove (C:\JakQuest\sim\batch13.sh: tools/party_server.py on
      127.0.0.1:8139 named in both party.txt; the host's script mp_host_remove.sim, made by make_mp_remove.py): the host
      hosted on the server, the guest found it in the server's list ("HostJak's game, 1 player (online)"), joined,
      got the host's progress through the server (6928 bytes, "save 4 done"); then the host paused, opened the VR menu,
      went down to Players (HostJak (host) (you), GuestJak with Remove), took the guest out ("party: took player 1
      out of the game"; the guest: "the host took this game out of the party"), the page left with the host alone.
      Both exit 0.
    - Launchers' Mods (both): Multiplayer's switch, then texture packs (item 35); docs/MULTIPLAYER.md and docs/MODS.md.

## G. Release
31. [x] PC tests on the simulated headset for each game: start, play, each mechanic, cutscenes, every disc in hand.
    2026-11-14, gk build95 with Multiplayer on (as players get it), the owner's discs (Jak 1 PAL SCES-50361, Jak II US
    SCUS-97265, Jak 3 PAL SCES-52460), one run at a time (C:\JakQuest\sim\batch14.sh, pictures checked, then deleted),
    all exit 0:
    - Jak 1: jak1_newgame (boot, title, a new game, the opening scene through his eyes), jak1_play (Geyser Rock in
      first person: walk, jump, punch, third person and back), jak1_melee (fists, punch, spin kick), jak1_body,
      jak1_zoomer_misty (third person by itself on the Zoomer).
    - Jak II: jak2_newgame (the opening scenes through his eyes), jak2_play, jak2_melee (punch, spin kick, uppercut),
      jak2_gun (drawn, aimed by the hand, held two-handed, fired, let go), jak2_board (third person on the JET-Board
      and first person off it, three times).
    - Jak 3: jak3_toplay (the opening, the title, a new game's scenes to play in Spargus), jak3_melee, jak3_gun,
      jak3_buggy (third person by itself in the buggy).
    The logs' only error lines are each game's own, the same in every earlier run (the PAL Jak 1 disc has no
    SAISB.STR; Jak II and 3's "Multiple textures named ..." and their scene scripts' messages). Multiplayer stayed
    inert in every one ("party: on as Player", nothing opened). Earlier the same day: two games in one party on the
    network (batch11) and over the party server with Remove (batch13), the VR menu's pages (mp_menu), Body shown in all
    three games, pop-in, the glows. What the PC cannot reach is in docs/HEADSET_CHECKLIST.md.
32. [ ] Quest build, own application id and signing key, README crediting OpenGOAL and Precursor (ISC) and the licenses,
    HEADSET_CHECKLIST, release notes. APK path and SHA-256 to the owner; nothing published unless the owner asks.
38. [~] **A git repository like Ratchet's** (owner, 2026-11-12): JakAndDaxter-Quest-VR, with a README (what it is, the
    three games, what it adds, setup from your own discs, building), description, CREDITS, licenses, setup scripts,
    Precursor as a submodule with our changes as patches. Local first; created on GitHub and pushed only when the owner
    says.
