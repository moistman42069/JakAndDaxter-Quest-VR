# Handoff, 2026-11-19, Session 29

Read this first, then docs/LIST-0.1.8.md (items 1 to 8). 0.1.7 is the latest release. 0.1.8 is NOT published, and
publishing has not been asked for: finish the list, then ask the owner before publishing. Nothing is committed: all of
0.1.8 is uncommitted in this repo and in the Precursor submodule (`git status`).

## The asks (0.1.8)

- A tester's reports and four logs on 0.1.7 (read-only in C:\JakQuest\downloads\tester-logs-20261119), with: "regress
  nothing, just pick up from where it was", a toggle on by default to hold the gun while driving (all games but
  Jak 1), and "physical driving if possible as a toggle that is on by default ... make this implementation as smooth as
  possible". Items 1 to 7.
- "proceed with list. forget nothing." Then: "for physical steering, make sure to look over all the vehicles to be sure
  gripping whatever wheel/control type they wave works as smooth and responsively as possible."
- Newest (item 8): "someone was asking if theres a way to choose where to allocate files (like on which drive and
  such). if not already there, add an option in the launcher to choose the file locations and move them as needed."
- Then: "stop and do a handoff". This session stopped there.

## Where it stands

- Items 1 to 7: coded. Builds: gk at build 100, all three games' GOAL at build 101. The vehicle review (item 6's
  sub-bullets in the list) is coded: the steer flags come from the game's rider stance (kFlagSteered 1<<20,
  kFlagHandlebars 1<<21), the grab zone at the body's handles or ahead of the head, the tilt-free turn measure, full
  lock, smoothing that adapts to speed, the dead-zone byte, the stick clicks as R2 and L2 while holding the wheel, and
  no letting go by itself.
- Jak II's ride test aid (pc-vr.gc, vr-test-warp!'s ride window): it now puts Jak on the vehicle with `'change-mode
  'pilot` once he is standing (sim rg2 had failed: the warp dropped him by the bike, and the sim's Triangle came while
  he fell). The Triangle lines are gone from tests/jak2_wheel.sim and tests/jak2_ride_gun.sim. This needs build 102
  (Jak II GOAL), written as C:\JakQuest\sim\build102.sh and NOT yet run.
- No sim has passed for 0.1.8 yet: batch31a was stopped after rg2 failed on the ride set-up, before the aid's fix.
- Item 8 is half coded, and NOT compiled (see below).

## Item 8 (Where your files are kept): what is coded

PC launcher only. The Quest keeps everything in its one storage, so it has no choice to add.

- PcPaths.java: `enum Place { GAMES, PACKS, SAVES, LOGS }`, each with its choice file in home (data-folder.txt, as
  0.1.3 had; packs-folder.txt; saves-folder.txt; logs-folder.txt), the folder made in a picked one (JakQuestVR,
  JakQuestVR-texture-packs, JakQuestVR-saves, JakQuestVR-logs) and leftover() (data-folder-leftover.txt, ...).
  folder(p), defaultFolder(p), moved(p), chosenFolder(p) and choose(p, f) are new. data(), defaultData(), dataMoved()
  and chooseData() wrap GAMES. config() and texturePacks() follow their places. modDownloads() goes inside the packs'
  folder when that is moved. The `logs` field is now the method logs(): a chosen folder whose drive is not there gives
  the default meanwhile.
- PcDataFolder.java: rewritten for any Place: target(p, picked), refuse(p, target) (refuses other places' folders
  too), missing(p) (never true for LOGS), waiting(p) (being copied, or missing), start(p, target, changed), and
  STATE.place. The spare room needed is 2 GB for the games and 64 MB for the others. finishLeftover goes through
  every place's note in one thread, dropping a note that names a current place, home or the release's folder. The
  0.1.3 methods are kept as GAMES wrappers, so DataFolderTest's old checks still apply.
- PcLauncher.java:
  - close() asks by place.
  - showTab: the options tabs (1 to 3) and TAB_MODS show showSavesWait() while the saves are being moved or their
    folder is missing, and the watcher redraws them.
  - The Play tab has missingCard() cards for the games, saves and packs folders.
  - dataFolderProgress() is worded by place.
  - filesCard() ("Where your files are kept", one row per place: Move..., Back to the default place, Open the folder),
    pickFolder(p), moveFolder(p, target) and moveBlocked(p) replace dataFolderCard, pickDataFolder and moveData. Packs
    can move while a game plays; the other places need it closed.
  - The game card: set-up waits while the packs folder is missing, PLAY waits while the saves folder is missing, and
    a game not set up says where it will go and the free space there.
  - setUp() and play() have the same guards, and play() writes the output log to paths.logs().
  - The Logs tab: Gather and Open are off while the logs move, and "Where things are" lists every folder.
- PcLogs.java and PcSetup.java: paths.logs is now paths.logs().
- docs/LIST-0.1.8.md: item 8 added, marked [ ].

## Item 8: what is left (in this order)

1. PcLauncher.java:
   - Replace the three `boolean locked = packBusy || PcSetup.STATE.running || PcDataFolder.STATE.copying;` with
     `boolean locked = packsLocked();`.
   - Add after `private volatile boolean packBusy;`:
     `private boolean packsLocked() { return packBusy || PcSetup.STATE.running || PcDataFolder.STATE.copying ||
     PcDataFolder.missing(paths, Place.PACKS); }`
   - In PackDrop.importData, change `packBusy` to `packsLocked()`.
   - On the Texture packs tab, after packsNote, show a note while `PcDataFolder.waiting(paths, Place.PACKS)`.
   - The exact replacements are in C:/JakQuest/sim/item8_packs_guard.py. Its PcLogs part failed because `\\n` became a
     newline, so make that edit with the Edit tool.
2. PcLogs.summary: one line, "Kept in a folder of the player's choice: none" or each moved place's inWords, with
   "(not there now)" when its folder is missing. No paths (they can carry the player's name).
3. PcSetup.status: when `PcDataFolder.missing(paths, Place.PACKS)`, skip the TexturePacks.changed comparison. Without
   that, an unplugged packs drive shows the games as needing a rebuild. Before TexturePacks.apply in the set-up, throw
   an IOException in the player's words if the packs folder is missing.
4. tests/java/dev/jakquest/vr/DataFolderTest.java: add checks that:
   - the new places' defaults are home\texture-packs, home\config and the logs argument;
   - target(p, picked) names the right folder;
   - the packs can't go into the games' folder, and the saves can't go into the logs folder;
   - the saves move on the same drive (the choice in saves-folder.txt, read again by a new PcPaths), and back to the
     default;
   - modDownloads() follows the moved packs;
   - logs() falls back when the chosen drive is missing (pick a drive letter whose root is not there);
   - missing(PACKS) is true for a gone folder, and missing(LOGS) is never true;
   - a saves leftover note is finished, and one naming home is dropped;
   - the copy to another drive works for the saves too (args[1]).
5. Compile: `sh /c/JakQuest/sim/javac_check.sh` (JDK 17, -Werror, `javac exit 0`). Then run DataFolderTest with a
   scratch folder on C: and a second on D: (D:\HaloQuest\_scratch\dft2), and VrOptionsTest (25 checks). The commands
   are in tests/README.md around line 387.
6. Docs: release-notes.md and launcher/common/texts/whats_new.txt for 0.1.8 ("Where your files are kept"), tests/README
   (DataFolderTest's new checks), HEADSET_CHECKLIST (a launcher check: move each folder to another drive, play, move it
   back), and LIST item 8 marked [x] with how it was checked.

## Then the rest of the list (as planned before item 8)

1. Make sure no gk.exe, goalc or extractor is running (`tasklist | grep -iE "gk.exe|goalc|extractor"`). Run
   `sh /c/JakQuest/sim/build102.sh` in the background, and look for "Successfully built" in
   C:\JakQuest\sim\build102-jak2.log. Item 8 is launcher Java only and needs no game build.
2. `sh /c/JakQuest/sim/batch31a.sh > /c/JakQuest/sim/s08/batch31a.txt 2>&1` in the background (rg2 wh2b wh2c wh1
   wh3 rw3g rw3s rw3f). Look for:
   - "[vr-test] ride: ... on it";
   - "Hold the gun while driving";
   - "the wheel held ... (handlebars or a bar | a wheel, the game's handles seen)";
   - "the wheel: turned ... steering ...";
   - "the right stick's click with that hand on the wheel: R2";
   - the tilt and the lock behaving as described;
   - Jak 3's glider, sled and hellcat getting the steer flags (if the continues seat Jak in them);
   - the pictures.
3. `sh /c/JakQuest/sim/batch31b.sh`: ps2 (one [vr-mem] line only), pmp3, boat2 (no "streamed animation" line), hol3,
   race3, board3, zoom3, buggy4.
4. The Jak II LAN check: `OUT=s08/mp2 GUEST_SIM=/c/JakQuest/sim/mp2_guest_pmp.sim sh /c/JakQuest/sim/mp2.sh` (port
   mapping off; nothing is sent to the router).
5. Read the logs for `[audio]`, `[openxr] session state` and any new error.
6. The ARM64 compile check: C:\JakQuest\sim\android_native_check.sh.
7. Fix anything found. Only the sims the change reaches, one build or sim at a time.
8. Docs:
   - release-notes.md "What's new in 0.1.8";
   - whats_new.txt 0.1.8 at the top;
   - tests/README "The checks added in 0.1.8" (runs in s08, builds 100 to 102, the new sims jak1_wheel, jak2_wheel,
     jak2_ride_gun, jak2_pause_soak, jak3_wheel, jak3_ride_wheel);
   - every LIST-0.1.8 item marked [x] with how it was checked. Item 2 says "3 s" but the code uses 4 s: fix the text.
9. Scan the public docs for private details: no player's name, never the tester's Windows username, no email.
10. Ask the owner before publishing 0.1.8. The release steps (VERSION, PcBuild.java, *mod-version-text*, the APK, the
    PC zip, the tag) are in the SESSION21, SESSION27 and SESSION28 handoffs.

## Rules that stay

- Break and regress nothing. Everything goes in this release. New toggles are on to start, with plain option names.
  Multiplayer never changes a single-player game. Every button is reachable, with no conflicts.
- Targeted sims only. One build or sim at a time. Build nothing until the list is coded, then one build pass.
- Every crash and close explains itself in the game's own log. Never ask for Event Viewer.
- Shell: never `python -` or any other stdin script; write a script file and run `python -I file.py`. Scripts use LF.
  Never git stash. Use `${VAR:?}` in rm. Read before you edit.
- No commit unless asked. Ratchet (C:\RACQuest) and HaloQuest are read-only (compiling with Ratchet's jdk17 is fine).
  The tester's logs are read-only data.
