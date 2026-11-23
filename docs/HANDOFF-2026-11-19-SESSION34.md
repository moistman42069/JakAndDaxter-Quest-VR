# Handoff, 2026-11-19, Session 34

**Out of date since 2026-11-22: 0.1.9's list was started and done (28 items). Read
docs/HANDOFF-2026-11-23-SESSION35.md instead; what follows is kept as it was.**

Read this first. This session picked up from docs/HANDOFF-2026-11-19-SESSION33.md (the 0.1.8 list done) and
published 0.1.8 on the owner's word: "Yes, publish 0.1.8". **0.1.8 is the latest release.**

**Next release: start from docs/LIST-0.1.9.md** (written 2026-11-20, not committed). It has a tester's first reports
on 0.1.8, which the owner passed on: "we really shouldnt be having any rendering or gun issues at this point. please
rectify everything and make this next release as good as possible." The owner said not to start yet ("not yet").
Sixteen items, none started; five of the tester's 0.1.8 logs are read-only in C:\JakQuest\downloads\tester-logs-20261120:
1. Jak II's tomb boss scene not starting; the tester is stuck in Mar's Tomb. The log has no line from 0.1.8's guard.
   The lead is the multiplayer mod's rewrite of the scene's trigger in widow.gc: it asks for the nearest player
   record that is Jak, not Jak himself. That came after Daxter's spider chase ended by a death near its end.
2. A section of Jak II's city not loading. It failed on two launches and loaded on a later one, so it comes and goes.
3. Jak 3's Peace Maker shot not starting at the gun.
4. Every gun and mod aimed by the hand.
5. Jak 3's vehicle turret seen as nothing in first person.
6. Jak's hands off the bikes' handlebars.
7. The city cars' hand holds a little off.
8. Jak 3's Light Shield still not working for the tester. The owner thinks it may be the input, so fix it only if
   there is a real fault. Their log shows the shield was never asked for: each time, Y was let go, which heals.
   The tester later found it: Y and the grip are held while pressing the other power.
9. See-through squares in the HUD.
10. A rendering sweep in all three games.
11. Jak II and 3's map showing a double image.
12. Jak 3's Daxter maze minigame in Vin's room (power-game.gc): unseen in first person, and in third person the
    player has to look down. Use the game's camera with the board turned to sit ahead.
13. Jak 3: an effect missing in Daxter's second section in the robot factory (likely the continue
    `factory-indax-2`).
14. B for the hover height while gripping the wheel. B is the turbo there, so the conflict must be resolved.
15. Push and pull the wheel to lean, like the stick forward and back. Kept apart from handlebar turning; its own
    toggle, on to start. "Important" in Jak 3.
16. Jak 3: Errol's boss floor 90% not drawn, its collision there. game-jak3-output-7 has 221
    `kmalloc: !alloc mem in heap for #<process ...> (336 bytes)` lines at the boss level's birth and at each
    restart, and nowhere else. These are likely the floor's drawn panels never made: find the type and why its heap
    is short. Second lead: the panels' renderer switch by the camera's distance (`fac-drop-plat-nocollide`).

The list also records the confirmations: Jak II's pole landing, the gun in the hover car, Jak 1's power cell, the
shadows in all three games and the Zoomer's steering.

Check first that nothing is still running: `tasklist | grep -iE "gk.exe|goalc|extractor"` and `ps -ef | grep -E
"chain|batch|run_sim"`. Nothing was left running at the end of this session.

## 0.1.8 released, 2026-11-19

- VERSION, PcBuild.java and Jak II's *mod-version-text* ("0.3.1-jakvr.0.1.8", goal_src/jak2/engine/mods/
  mod-settings.gc) bumped; Jak II GOAL build 121 (C:\JakQuest\sim\build121.sh, log build121-jak2.log): all 1172
  targets, clean. gk is build 120 and newer than every C++ source; Jak 1 GOAL 117, Jak 3 GOAL 119.
- The release notes' banner, downloads, updating (from 0.1.3 to 0.1.7 the PC builds again only what changed: the
  compiler's id is 0.1.7's, 197ddc41...4958), install file names and "What we tested" (0.1.8's checks first);
  README's banner; LIST-0.1.8's top ("Released as 0.1.8"). whats_new.txt had 0.1.8's section already.
- JakQuestVR-0.1.8.apk (C:\JakQuest\sim\build_apk.sh, log apk_build_018.log, `== gradle exit 0`): versionCode 10899,
  versionName 0.1.8, signed with 0.1.0's key (certificate 66390b53...d59d52a by apksigner verify); its toolchain.zip
  has 0.1.8's GOAL (Jak II's version text, the stuck-stream guard in jak2/jak3 loader.gc), toolchain.sha256
  ae7c9aad...a6a2; libmain.so has 0.1.8's lines (`has not shown the game`, `vr-mem`). No packed file newer than it.
  SHA-256 d5454809e5e63e282409e439eb985f2d5acffeed2fa039d36a69299dd76bb4dd.
- `python -I tools/build_pc_release.py` (log C:\JakQuest\sim\pc_release_018.log): self-test OK, game code
  f6ea6cc0...c6cc (pc/game-code.txt), Jak II's version text checked; JakQuestVR-0.1.8-windows.zip (gk.exe build 120)
  SHA-256 6bc0abee7786a9cf0dd863b70345c51bf225bd6da7e3a8478f4d91a4c475b34d.
- out/release-0.1.8: the APK, the zip and SHA256SUMS.txt (`sha256sum --text`; its own SHA-256 e21dd8f6...0751).
- tools/make_patch.sh (log C:\JakQuest\sim\make_patch_018.log): 0001 3008 files (4 new: kmalloc.cpp, kmalloc.h,
  989snd/audio_loss_test.h, jak3 collide-shape.gc), 34,009,857 bytes; 0002 8 files as before.
- Private scan of the changed files and the patch's 2,725 new lines: no user path, no owner email, no tester's
  Windows user name, no player named (only "a tester", "a player"); the HaloQuest scratch folder named in the
  handoffs as in those already published.
- Commit 71339db on main ("0.1.8: steer by the wheel, the gun while driving, the latest reports on 0.1.7"), pushed.
- **Published:** https://github.com/moistman42069/JakAndDaxter-Quest-VR/releases/tag/v0.1.8 ("Jak and Daxter VR
  0.1.8", latest, not a prerelease, tag v0.1.8 on 71339db, body docs/release-notes.md; the APK, the Windows zip and
  SHA256SUMS.txt, GitHub's digests the same as above).

## Next

1. Players' reports and headset logs on 0.1.8 (HEADSET_CHECKLIST's "0.1.8's changes": a long pause, the sound
   device going away, a black headset, the gun while driving, the wheel and the rest), then a new list
   (LIST-0.1.9.md).
2. The next release bumps VERSION, PcBuild.java and Jak II's mod-settings.gc "0.3.1-jakvr.<ver>", with the steps
   above (SESSION21's "Publishing 0.1.5" for the order).
3. Scratch to clear on the owner's go (listed, not deleted): C:\JakQuest\sim\s08 (0.1.8's runs),
   C:\JakQuest\sim\rel018 (the release's checks: the APK's unpacked libmain.so and toolchain.zip, 0.1.7's patch copy,
   the scan's lines) and the session's build and batch scripts there (build119.sh to build121.sh, batch53.sh to
   batch55.sh, chain54.sh, chain56.sh). The sim configs gkcfg and gkcfg-60 stay (the sims use them).

## Rules that stay

- Break and regress nothing; everything goes in this release; new toggles on to start with plain names; multiplayer
  never changes a one-player game; every button reachable, no conflicts.
- Targeted sims only; one build or sim at a time; never edit source a queued chain may build. Every crash and close
  explains itself in the game's own log; never ask for Event Viewer. Resolving and checking is ours.
- Shell: never `python -` or other stdin scripts (write a file, `python -I file.py`; `python -E -P` for Pillow); LF
  scripts; Edit or sed for source edits; read before edit; never git stash; `${VAR:?}` in rm.
- No commit unless asked. Ratchet (C:\RACQuest) and HaloQuest are read-only. The testers' logs are read-only data.
  Public notes name no player and never the tester's Windows user name.
