# Handoff, 2026-11-19, Session 28

Read this first, then docs/LIST-0.1.7.md (items 1 to 13, each with how it was checked). 0.1.7 is published (latest,
2026-11-19, on the owner's "Publish 0.1.7"): https://github.com/moistman42069/JakAndDaxter-Quest-VR/releases/tag/v0.1.7,
commit 67e563e pushed, tag v0.1.7 on it.

## The asks (0.1.7)

- The owner gathered players' reports and logs on 0.1.6, then: "lets get to work now. proceed with list". Standing:
  "the logs should just indicate a crash and be as detailed as can be going forward" (never ask players or the owner
  for outside diagnostics such as Event Viewer).
- Three sessions worked the list in turn, each picking up on "was working in another session. pick up from where it
  left off. forget/miss nothing. proceed with list": 6ca185ac coded all 13 items (build 87); 0fc2a78a built and checked
  them (builds 88 to 96, s07 runs) and stopped mid-batch; e74a971a (this one) finished the checks (build 97), found
  item 10's real cause and fixed it (build 98).

## Done

Every item is [x] in LIST-0.1.7.md with its cause, fix and check. In short:

- 1 Jak II's pause menu: Game Options, Multiplayer (Host Game, Join Game; in a session Players, Leave the game); B
  back to Game Options with Multiplayer lit (build 97: progress-generic-pc.gc set the row before the pop).
- 2 Eco sparks in first person (option, on to start), all three games.
- 3 Jak 1's add-rider!: the original's rule alone, Teamruns' in a party, never past the rider list (build 97).
- 4 Third person after a pole until you land (option, on to start), all three games.
- 5 Crosshair Off to start (key `crosshair-mark`; 0.1.6's Dot read as Off), the crosshair's and pointer's pictures
  written into the next swapchain image each frame they show (SteamVR's stale pictures).
- 6 Jak 1's "stuck" after a long fall: the original's painful landing (6.2 s), now in the game's camera in all three
  games; Jak 1's touching list 64 pairs (blue eco by an orb cache filled 32).
- 7 Jak 3's forest close: the frame's draw buffer (1459 KB of 1280 KB; the game's own stop), now 5 MB, Jak 3's heap
  +16 MB.
- 8 Jak 3's Light Shield: B held (or the stick held right) keeps Circle held.
- 9 Gamepad only (option, off to start); the Vive wand's roll in CONTROLS.md.
- 10 Jak II's pause behind: its menu's ring (a 3D model) was put in front of the game's own camera, metres behind
  Jak's eyes in first person; with a headset it is put and turned from the view now, as Jak 3's (progress.gc, build
  98). Found this session: the guard 6ca185ac added (the runtime's head space checked against its eyes, past 30
  degrees the head is the eyes') was a guess and stays only as a safeguard (SteamVR's null driver keeps them together).
- 11 Jak II's stadium racer close: the mod's pilot-on read a minimap icon a one-player race never makes.
- 12 Jak 1's black screen loading at Snowy: the levels load from the game's own camera, not the eyes.
- 13 Every crash, abort, terminate, C runtime check, stack overflow and hang leaves a `[crash]` or `[vr-stall]` record
  with the game's state; the Quest's crash.txt too.

## Builds and sims

- Builds 87, 88 and 90 to 98 (89 was 0.1.6's; scripts C:\JakQuest\sim\buildNNN.sh, logs buildNNN-*.log): gk last at
  96, Jak 1 GOAL at 97, Jak II GOAL at 98, Jak 3 GOAL at 95; each clean (Jak 1's PC_KERNEL_VERSION warning as before).
  After 96, xr_openxr.cpp changed only in a comment (the guard's), so gk 96 is the release's.
- Runs in C:\JakQuest\sim\s07 (tests/README.md "The checks added in 0.1.7" lists each with what it showed).
- The last checks (this session): pmp1/pmp0 and pstick3 (build 97); mp1 and mp2 (C:\JakQuest\sim\mp017.sh: Jak 1's
  party and Jak II's session on this PC, Port mapping off; no error, no `[crash]`, no `max number of riders`; the
  guest's pause Multiplayer in the session showed Players and Leave the game); null17 (SteamVR's null driver, Jak II,
  90 s, steamvr.vrsettings put back: its head and eyes together, the guard silent); batch30.sh on build 98: ring1
  (tests/jak2_pause_ring.sim: the ring ahead, paused ahead and turned 150 degrees), pstick4, pmp2, rider1 (Jak 1 alone
  down Rock Village's elevator); title1 (Jak II's title ring ahead, turned too); the ARM64 compile
  (android_native6.log: libmain.so, no error). Every run: no error, no stall, no `[crash]`.

## Next

- 0.1.7 released: VERSION, PcBuild.java and Jak II's *mod-version-text* ("0.3.1-jakvr.0.1.7", Jak II GOAL build
  99, clean) bumped; the release notes' banner, downloads, updating, install and "What we tested", and README's
  banner. JakQuestVR-0.1.7.apk (versionCode 10799, versionName 0.1.7, 0.1.0's key, certificate 66390b53...d59d52a by
  apksigner; its toolchain.zip has build 98/99's GOAL: the version text, the ring, the pause Back row, the rider rule),
  SHA-256 7b6f9cede4dbb92d4e942a5bc517ef567f2c3015cd0d4630437e53c973917908; JakQuestVR-0.1.7-windows.zip (self-test OK,
  game code 1bc64b92...c6fe in pc/game-code.txt, Jak II's version text checked), SHA-256
  bb1cde671e7ce319a061ee445a9ac48b34b6fd34d25cab8b09a57941acb96100; out/release-0.1.7 with SHA256SUMS.txt; GitHub's
  digests the same. make_patch.sh: 3004 files (8 new, all 0.1.7's sources), 33,870,797 bytes. Private scan: no user
  path, no owner email, no tester Windows name; the addresses and player-handle comments the same as 0.1.6's patch.
  Logs: C:\JakQuest\sim\apk_build_017.log, pc_release_017.log, make_patch_017.log.
- Next: players' reports on 0.1.7, then LIST-0.1.8.md. The next release bumps VERSION, PcBuild.java and Jak II's
  *mod-version-text* ("0.3.1-jakvr.<ver>"), with the steps above.
- Seen, not an item: after an enemy's hit at Snowy's fort gate Jak's root moves 10 to 19 m in a frame (the tester's
  log and every eco-fall run, before and after the fixes); the body's guard holds his model there. Not ours to
  explain yet; watch players' logs for `the game's skeleton left his root by` with large numbers.
- For players (HEADSET_CHECKLIST, 0.1.7 section): the crosshair's layer on SteamVR (no HUD at the laser's end), Jak
  II's pause ring in front (ahead, turned round, after a stick turn), the Light Shield held, Gamepad only, the pole's
  third person.
- Scratch to clear on the owner's go (listed, not deleted): C:\JakQuest\sim\s07, the cfg copies gkcfg-poleoff,
  gkcfg-sparksoff, gkcfg-padonly, gkcfg-x016a, gkcfg-x016b, and C:\JakQuest\sim\pcvr (the null run's copy of
  steamvr.vrsettings from before it, identical to the one in place).
