# Jak and Daxter Quest VR 0.1.2: the list

Started 2026-11-16, the day 0.1.1 was published. Marks: [ ] to do, [~] in progress, [x] done (how it was checked).
The owner first said to look into it for the next version, not now; then, the same day: "just do a quick release again
fixing that detail. and refine anything else you see fit off the bat. but yea, make it clear though about this change
in the whats new section/release and keep other release notes from today since they are close together. proceed with
new release with this in mind." So 0.1.2 is a quick release with item 2 and the PC part of item 1, and no game code
changed (so nothing is prepared again coming from 0.1.1). The rest of item 1 is for a later version.

1. [~] **Smoother updates** (the owner, 2026-11-16, after hearing that 0.1.1 needs every game prepared again: "If its
   absolutely necessary then fine. If you can make any updates in the next version to make it smoother, look into it,
   but don't do anything now, just notate for the future").
   **Done in 0.1.2, the PC's build again only when the game code changed:** tools/build_pc_release.py hashes the
   release's game\data (each file's path and the SHA-256 of its bytes) and ships it with the game data version
   (build.gradle's gameDataVersion, the Quest's) in game\data\game-code.txt, with the releases before it of the same
   two (pc/game-code.txt, the record: 0.1.0 and 0.1.1 hashed from their zips; 0.1.1's equals 0.1.2's). A game built
   now gets a ready mark with the release, the id and the data version; PcSetup.newSources builds it again only when
   they differ (a mark of 0.1.0 or 0.1.1, which has the release alone, counts as current when the release is in the
   list of the same). Without game-code.txt (a launcher built from the repository) it works as before. The launcher's
   self-test checks the file is there; the log zip says which release built each game. Checked:
   tests/java/dev/jakquest/vr/GameCodeTest.java (19 checks), TexturePacksTest and ModListTest as before. The Quest's
   launcher already did this (its toolchain.sha256 of the same sources): 0.1.2's must equal 0.1.1's
   (d71dc10a...c748, out/release-0.1.1/toolchain.sha256.quest).
   How it worked in 0.1.1 (before the change above):
   - Both launchers' updaters find a new release by themselves (Quest: once a day, Updater.java, UpdateFlow.java,
     Versions.java; PC: at start, every 6 hours, PcUpdater.java, UpdatePolicy.java). The player confirms: the Quest's
     dialog and then Android's installer; the PC's Download button, then the launcher restarts itself.
   - Quest: a game prepared with an older game data version (build.gradle `gameDataVersion`, 2 since 0.1.1) can't be
     played until it is prepared again (LauncherStore.staleness: INCOMPATIBLE). If only the game code changed, it is
     recommended but the game still plays (OUTDATED).
   - PC: every new release number makes each game OUTDATED (PcSetup.newSources compares the release number in the
     game's ready mark), and PLAY IN VR becomes **Build it again** (from the extracted disc, a few minutes), even when
     the game code did not change.
   Still to look into, for a later version:
   - Prepare or build only what changed: keep the last build's compiled files and let the compiler recompile the
     changed sources alone (the VR layer's files, pc-vr.gc and the few engine files we touch), instead of the whole
     game. Check that the Quest's preparation can keep its compiled output between versions, and how long a partial
     build takes against a full one.
   - Fewer forced re-preparations: leave spare room in the structures the runtime and the game code share (pc-vr.gc
     vr-body-info and the like), so a runtime change does not have to raise the game data version.
   - All games at once: after an update, one button (or the update dialog itself) prepares or builds every game that
     needs it in a row, instead of one per game page.
   - Say it before updating: the update offer says whether the games will need preparing again and about how long.
   - PC: download the update in the background, so the player only presses Restart to update.
2. [x] **0.1.1's notes on the PC rebuild** (found 2026-11-16): docs/release-notes.md (the GitHub release page) says the
   PC launcher "builds each game again by itself the first time you press PLAY IN VR", and whats_new.txt says "the
   launcher builds them again by itself". Actually the player presses **Build it again** (PLAY IN VR is not shown
   until then). Done in 0.1.2: its release notes and What's new (which keep 0.1.1's, out the same day) say so, with a
   "Corrected" line, and say what an update from 0.1.1 or from 0.1.0 needs. 0.1.1's own release page left as it was.
3. [ ] **Performance: pitched, waiting on the owner** (the owner, 2026-11-16: "is there no way to go ahead and boost the
   native framerate of the game by default? just asking", then "is the launcher/game in general optimized to the max
   potential? if theres anything you think we missed that could make the game run better, just pitch it to me before
   we do anything"). Nothing done; the pitch, most worth it first:
   - Read the headset logs first: every log has `[vr-perf]` lines (frames a second, the frame's period, the wait, the
     work, the most), so a 0.1.2 log from a Quest and from a PC shows whether the game or the eyes are the limit.
   - Game frame rate the headset's by default (it is an option since 0.1.1, 60 to start: "off by default", the
     owner's rule for performance options): smoother, but the game's work rises with the rate (72 to 120 a second);
     on PC likely fine, on the Quest not known for Jak II and 3 until a headset log shows it. Runtime only: nothing to
     prepare again. Saved options keep the player's own choice unless changed once, as AppSW was.
   - Both eyes in one pass (multiview): each eye now replays the whole frame's drawing (opengl.cpp, draw_passes); one
     pass would take much of the drawing's CPU work off the Quest. The biggest job: every renderer's shaders.
   - Smoother frames (AppSW) and Dynamic resolution on by default on the Quest, once a headset shows 0.1.1's AppSW
     rework has no flicker.
   - Max draw distance on the Quest (on by default, the owner's: everything drawn, shadows too): the costliest
     setting; measure what it costs there.
   - Preparing a game faster: item 1's "only what changed".
