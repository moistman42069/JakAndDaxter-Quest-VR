# Handoff, 2026-11-23, Session 35

Read this first. 0.1.9's list (docs/LIST-0.1.9.md, 28 items) was worked by three sessions after
docs/HANDOFF-2026-11-19-SESSION34.md (0.1.8 published): 2026-11-20 (items 1 to 16, builds 122 and 123, no handoff),
2026-11-22 (items 17 to 27, builds 124 to 129, no handoff) and this one, 2026-11-22 and 23 (the rest, builds 128 to
139, and the release). The owner: "just publish it when done. dont ask for shit", "make sure this build is fucking
PEAK", "dont forget the fishing mini game. make sure its perfect in all ways", "more so making the fishing minigame
work in first person vr ... motion controls, proper implementation, allat", and on 2026-11-23 "if you think the
fishing minigame is well implemented as is, then lets have it on by default" (it is: Fishing game in first person,
on to start). **0.1.9 is published and is the latest release** (2026-11-23).

Check first that nothing is still running: `tasklist | grep -iE "gk.exe|goalc|extractor"` and `ps -ef | grep -E
"chain|batch|run_sim"`.

## The owner's asks for 0.1.9

- "we really shouldnt be having any rendering or gun issues at this point. please rectify everything and make this
  next release as good as possible."
- "take everything they said into account and fix. add proper flawless working vr sections, auto switch sections like
  the dark maker robot to 3rd person, hide objects that could obstruct 1st person (only when in 1st person of course),
  verify the nicest/smoothest vr implementation for the fishing game, see about fixing whatever flagged the virus
  thingy, and yea basically fix/implement/adjust anything else you see fit based on that report."
- The fishing game in first person with motion controls (above), on by default.

## Done (each item's cause, fix and checks are in LIST-0.1.9.md)

- 1 Jak II's tomb boss scene; 2 the city's sections (a watch that names what the game wants); 3, 4 every gun's shot
  from the gun in the hand; 5 Jak 3's vehicle guns seen from the game's camera and aimed by the hand; 6, 7 Jak's hands
  on the bikes' handlebars and the cars' wheels (both games); 8 the Light Shield (no fault); 9 the HUD's squares;
  10 the rendering sweep (the shadow buffers grow); 11 the map's double image; 12 Daxter's maze game ahead; 13 the
  robot factory's Daxter section (no fault found); 14, 15 B for height and Lean by pushing the wheel (new options, on);
  16 Errol's floor and the port's mines; 17 Windows version information in the programs, the Defender section in the
  README and release notes; 18 Jak II's slow frames (Daxter's log lines, the sprites worked out once for both eyes,
  `[vr-perf]`); 19 the orb hunts' flyovers; 20 Jak 1's fishing game from Jak's eyes, the net in the hand (new option
  Fishing game in first person, on); 21 the JET-Board and Light Jak's wings left out of the eyes; 22 nothing else
  found to hide; 23 the turrets aimed by the hand; 24 the Dark Maker robot in third person; 25 Gamepad only stands
  aside with no gamepad, the HUD's layer logged (the Pimax player); 26 the walkers' health (no change); 27 the Dark
  Maker ship's door troopers (born once their level is up); 28 swimming: the eyes over the water and over the waves.
- Docs: release-notes.md (0.1.9's section, the Defender section, What we tested, Known limits), README, whats_new.txt,
  CONTROLS.md, HEADSET_CHECKLIST.md (0.1.9's checks), tests/README.md ("The checks added in 0.1.9"), 20 new sims in
  tests/.

## Release

- VERSION, PcBuild.java and Jak II's *mod-version-text* ("0.3.1-jakvr.0.1.9") bumped. Release builds in one chain
  (C:\JakQuest\sim\chain81.sh, log chain81.txt): build 140, the PC programs (tools\build_pc.bat: gk, goalc,
  decompiler, extractor; gk.exe and extractor.exe carry ProductVersion and FileVersion 0.1.9, their descriptions and
  company), Jak II GOAL (all 1172 targets); the launchers' Java (javac, -Werror) and VrOptionsTest (30 of 30).
- JakQuestVR-0.1.9.apk (C:\JakQuest\sim\build_apk.sh, log apk_build_019.log, `== gradle exit 0`): versionCode 10999,
  versionName 0.1.9, signed with 0.1.0's key (certificate 66390b53...d59d52a by apksigner verify); its toolchain.zip
  has build 139's GOAL (vr-sea-top in Jak II and 3, no trace code, Jak II's version text); libmain.so has 0.1.9's lines
  (`the HUD's layer first sent`, `the fishing game from Jak's eyes`); its whats_new.txt is the launcher's. No warning
  from game/vr in the ARM64 build. SHA-256 2ff79058f3bc0ed68a528afe74e07f4dc2870fcb41aa9ab175501091bd321fbf.
- `python -I tools/build_pc_release.py` (log pc_release_019.log): self-test OK, game code
  dd6d871f...b6fe (pc/game-code.txt: no release before with it, so every game builds again once, as the release
  notes say); JakQuestVR-0.1.9-windows.zip (gk.exe build 140) SHA-256
  6b2b5a594ff1d90cc7e299b5dc689c4314e7171eab944fcfd0dac081b0db8bf2.
- out/release-0.1.9: the APK, the zip and SHA256SUMS.txt (`sha256sum --text`; its own SHA-256 80aa2b37...73aa).
- tools/make_patch.sh (log make_patch_019.log): 0001 3025 files (17 more than 0.1.8's: cmake/JakVrVersion.cmake,
  game/jakvr_version.rc.in, decompiler/CMakeLists.txt, Sprite3.h, jak3 kscheme.cpp and Jak 3's gun shots, turrets,
  projectile, process-drawable, blow tower, port attack, Scorpion's gun and factory boss files), 34,225,882 bytes;
  0002 8 files as before.
- Private scan of the changed files and the patch's 4,114 new lines: no user path, no email, no player's name or
  Windows user name (only "a tester", "a player"); no trace code (`vr-dbg`, `vr-ocean-near`) in the patch.
- Commit 4e2282a on main ("0.1.9: turrets and Jak 1's fishing game by hand, swimming over the waves, the latest
  reports on 0.1.8"), pushed.
- **Published:** https://github.com/moistman42069/JakAndDaxter-Quest-VR/releases/tag/v0.1.9 ("Jak and Daxter VR
  0.1.9", latest, not a prerelease, tag v0.1.9 on 4e2282a, body docs/release-notes.md; the APK, the Windows zip and
  SHA256SUMS.txt, GitHub's digests the same as above).

## Next

1. Players' reports and headset logs on 0.1.9 (HEADSET_CHECKLIST's "0.1.9's changes": the fishing game in first
   person, the hands on the bars, swimming, the turrets, the Pimax player's HUD layer line), then LIST-0.1.10.md.
2. Open: the Pimax Dream Air's HUD (their next log has `[openxr] the HUD's layer first sent: ...`); Windows Defender
   (a false positive; signing the programs would end it for good, which needs a code signing certificate: the
   owner's call); item 13's effect (the tester did not say which); Jak 1's sea has the 12 cm swim clamp only (its sea
   code differs from Jak II and 3's, no report of it, and no Jak 1 sim swims).
3. Jak 3's bike test aid (`JAKVR_TEST_WARP=h-bike-base`): at `ctygenb-start` no parking spot was within 150 m, so it
   parks 4 m ahead of Jak (a canal there once took the bike, sim s16); a run that loses the bike is the aid's, not the
   game's.
4. Scratch to clear on the owner's go (listed, not deleted): C:\JakQuest\sim\s09 to s20 (about 11 GB: s09 4.0 GB,
   s12 2.7 GB, s10 2.0 GB, the rest under 1 GB each), vot-out, vot-dir, vot-dir2, rel019, and this release's build,
   batch and chain scripts (build122.sh to build139.sh, batch60.sh to batch79.sh, chain*.sh). The sim configs
   (gkcfg, gkcfg-60, gkcfg-perf, gkcfg-gponly, gkcfg-fishtp, the mp ones) stay.
5. The old code comments that name a tester (from before 0.1.8's rule): offered to the owner to scrub.

## Rules that stay

- Break and regress nothing; everything goes in this release; new toggles on to start with plain names, in the VR
  menu and both launchers; multiplayer never changes a one-player game; every button reachable, no conflicts.
- Targeted sims only; one build or sim at a time; never edit source a queued chain may build. Every crash and close
  explains itself in the game's own log; never ask for Event Viewer. Resolving and checking is ours.
- Shell: never `python -` or other stdin scripts (write a file, `python -I file.py`); LF scripts; Edit or sed for
  source edits; read before edit; never git stash; `${VAR:?}` in rm. A GOAL `format` takes at most 8 arguments.
- Ratchet (C:\RACQuest) and HaloQuest are read-only. The testers' logs are read-only data. Public notes name no
  player and never a player's Windows user name.
