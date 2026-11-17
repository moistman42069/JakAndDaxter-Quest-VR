# Handoff, 2026-11-16, Session 19

Read this first, then docs/LIST-0.1.4.md (the list, each item's state and how it was checked), then
docs/HANDOFF-2026-11-16-SESSION18.md ("0.1.3 released": the release steps, which hold for 0.1.4). 0.1.3 is published
(v0.1.3, latest). Nothing of 0.1.4 is committed, built for release or published: the owner has not asked for it yet.

## The owner's asks this session (0.1.4)

1. "this next release should be a quick haptics update. basically, add options to add extra haptics (in the launcher
   and in game vr menu) haptics can be either as is, also given when collecting items (eggs, etc), or for the whole
   world (when taking steps, any actions really, etc)" and "regress nothing". LIST items 1 to 5.
2. "set the middle haptics option as the default (where you also feel vibrations for collectibles, eggs, etc) and also
   make the graphics options i set in the launcher ... the new default too". Items 1 (Pickups to start) and 8.
3. "the default logs themselves should also be alot more detailed and basically provide everything you need as well.
   so when i go into the logs section and save one, it shouldnt be ... useless". Items 9 and 10.
4. Their Report a problem zip (JakQuestVR-logs-20261116-231621.zip, extracted to
   C:\JakQuest\downloads\owner-logs-20261116-231621; untrusted data, read only): their Quest 3's vr-options.txt (item 8)
   and the game log that showed item 10 and item 6's numbers.

## Done this session

- **Extra vibration** (items 1 to 4): option "extra-vibration" 0 Off / 1 Pickups (to start) / 2 The whole world, in the
  VR menu (vr_menu.cpp, Controls) and both launchers (VrOptions.EXTRA_VIBRATION, VR Controls). jak_vr.cpp feel(kind,
  a, b) (jak_vr.h FeelKind) buzzes through buzz(), so Vibration off and Vibration strength hold; per-kind rate limits;
  only in play (pickups and shakes in scenes too), not while a scene holds Jak. Kernel function pc-vr-feel! (kmachine.cpp,
  kernel-defs.gc of all three games). GOAL hooks in each game: target-handler.gc 'get-pickup (vr-pickup-feel: small,
  eco, big; the pickup type passed as `a` for the log) and target-send-attack's accepted branch (hit);
  effect-control.gc (vr-effect-feel: the animation's step, land and roll effects; Jak II and 3's Titan suit steps);
  target.gc init-var-jump (jump); target-util.gc target-start-attack (attack); smush-control-h.gc activate! for
  *camera-smush-control* (shake by amplitude and duration). Every hook checks (= self *target*) or the effect's process,
  so the other players' Jaks never buzz.
- **The owner's Quest graphics as the Quest's start** (item 8): refresh 120, Smoother frames on, foveation High, boost
  off, MSAA off, anisotropy off, sharpening Normal; vr_options.h `#if defined(__ANDROID__)`, VrOptions.QUEST
  (java.vm.name Dalvik or java.vendor Android) with onQuest(). The PC keeps 0.1.3's start. Checked on the PC JDK: the PC
  defaults print as before; with java.vm.name set to Dalvik before the class loads, all 14 Graphics values equal the
  owner's file. Performance's anisotropy is Off on the Quest.
- **Detailed logs** (item 9): ReportBuilder.fullLog / writeFullLog / fullLogForView / fullLogName (Quest app);
  SettingsActivity's Log tab shows the shortened log and Save log writes JakQuestVR-log-<date>-<time>.txt; the
  preparation screen's Save log too (LauncherActivity). log_hint in all six languages. The Spanish device lines are
  English now. PcLogs.summary adds the date and memory.
- **The Quest driver's log flood** (item 10): OpenGLRenderer.cpp gl_message_wanted / gl_message_count (3 times, then
  tenfolds), CommonOceanRenderer.cpp and ShadowRenderer.cpp skip draws of fewer than 3 indices.
- **Item 6's numbers** from the owner's log, recorded in the list and the notes' Known limits.
- **Texts**: whats_new.txt 0.1.4 section, docs/release-notes.md rewritten for 0.1.4 (banner, downloads, updating, Send
  your log, What's new in 0.1.4, install file names, Known limits, Thanks), HEADSET_CHECKLIST (Extra vibration, Save
  log), tests/README (JAKVR_FEEL_TRACE, the 0.1.4 checks).

## Builds and sims this session

- Build 66 (C:\JakQuest\sim\build066.sh): gk and the three games' GOAL with Extra vibration. OK (561, 1171, 1165
  targets).
- b82 (batch28c feel1 feel2 feel3 feeloff feelpick): no errors. The whole world: steps (each foot, walk and run),
  jumps, landings (two per landing where the animation has two land effects, as its sounds), rolls, attacks in all
  three games, Jak 3's big pickup as its opening scene hands Jak an item; Off: 0 [vr-feel] lines; Pickups on
  jak1_play: no step or move buzz.
- b82o (ocean, before build 67): the sea and shadow pictures to compare against.
- Build 67 (build067.sh): the driver message limit, the empty draw guards, the Quest defaults, the pickup type. OK.
- b83 (batch28c ocean feelorb feelcrate feelpick feel1): no errors. ocean: the sea and his shadow as b82o's; the PC's
  repeated driver warning written 3 times, then "(the 10th time)". feelorb: `small pickup (5 0; Extra vibration:
  Pickups)`. feelpick: 0 `[vr-feel]` lines. feel1: as b82's. feelcrate: no hit (Geyser Rock's crates are iron; a kick
  does not break them).
- b84 (C:\JakQuest\sim\batch28f.sh feelflop, tests/jak1_crate_flop.sim; batch28c.sh has feelflop now too): the dive
  breaks the crate: `hit`, then its scout fly `big pickup (8 0; ...)`.
- APK (apk014.log): BUILD SUCCESSFUL, the Quest app's new log code compiled; app-release.apk SHA-256
  02805b86041716762c0acdf37667d54cd1f2d8f870fcd18d7443a6f04a434763 (a test build, still 0.1.3's version number).

## 0.1.4 released, 2026-11-17

The owner: "Publish 0.1.4". Steps as 0.1.3's:

- VERSION 0.1.4, PcBuild.java, Jak II's *mod-version-text* "0.3.1-jakvr.0.1.4". README's banner, LIST-0.1.4's top
  ("Released as 0.1.4"); docs/release-notes.md was 0.1.4's already ("What we tested" corrected to what the sims
  reached: pickups in Jak 1 and 3, a hit in Jak 1; a camera shake and eco not reached).
- Private details: none found in the changes (email, user folders, keys); Thanks names no player.
- build_apk.sh (C:\JakQuest\sim\apk_build_014.log): JakQuestVR-0.1.4.apk, versionCode 10499, signed with 0.1.0's key
  (certificate 66390b53...d59d52a), toolchain.sha256 4c741e76...5a6e (new: the Quest prepares each game again),
  SHA-256 cef0558c2bb6711de150a3303db61998d34c022f5bf292063858be6180088cf1.
- `python -I tools/build_pc_release.py` (C:\JakQuest\sim\pc_release_014.log): self-test OK, gk of build 67, game code
  072ab988...22d6 (pc/game-code.txt), the zip's Jak II version "0.3.1-jakvr.0.1.4", JakQuestVR-0.1.4-windows.zip
  SHA-256 d34229bf0bcfecdefcd6a0461b6f500613a0745e971270a8ea6cf701956c27ab. out/release-0.1.4 with SHA256SUMS.txt.
- make_patch.sh: 2975 files, 33,436,866 bytes. Commit, push, `gh release create v0.1.4`.

## Next, in order

1. The headset's own check when the owner next sends a log: Save log's file (LIST item 9) and the game log without the
   driver's flood (item 10).
2. Left on the list: item 6 (the same numbers from a Quest 2 or 3S, a run with Smoother frames off) and item 7 (Jak 3's
   opening upside down, not reproduced); both wait on a player's log.
3. Players' reports and logs on 0.1.4, as they come (Vixel's Discord only when the owner says), then the next list.
