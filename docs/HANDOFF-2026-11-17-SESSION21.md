# Handoff, 2026-11-17, Session 21

Read this first, then docs/LIST-0.1.5.md (every item [x], each with how it was checked), then
docs/HANDOFF-2026-11-17-SESSION20.md (items 1 to 12, and "Publishing 0.1.5": the release steps left). 0.1.4 is
published. 0.1.5 is published (latest, 2026-11-17: see "0.1.5 released" at the end). VERSION, PcBuild.java and Jak II's
*mod-version-text* ("0.3.1-jakvr.0.1.5") were bumped by the publish that was stopped; the owner then said "dont
publish anything yet", and later "go ahead and publish on git as latest release".

## The owner's asks this session (0.1.5)

1. "actually, good thing you didnt publish. i just got more feedback. fix before anything:" with the reports of
   items 13 to 15 (theirs and Vixel's), then "be sure to update the controller section to have controls shown for
   each game as well. make sure theres NO control conflicts and all controls are able to be accessed and used
   accurately on the vr controllers. proceed with list" (items 13 to 17).
2. "fix all jak multiplayer conflicts in single player. this is for jak 1 and jak 2 since both have multi currently",
   "I don't want multiplayer to conflict with single player at all" (item 16); a player's per-ride first person ask
   with the owner's "keep the first person everything option" (item 18); cross-play research only, "dont publish
   anything yet, when your done with everything on the list, gve me a breakdown on what you discovered for crossplay
   and whether it makes sense to implement it or not" (item 20); a failed Jak II mission's scene (item 19).
3. A tester's "starting non vr and pause menu behind me are still issue", "the non vr issue also happens every time
   a cutscene starts and ends" (items 21, 22).
4. The owner's question, answered in the closing message: "multiplayer is just on by default with Jack 1 and Jack 2.
   Correct me if I'm wrong there." (Yes: on to start in both, off in the launcher's Mods; since 0.1.5 merely on it
   changes nothing in one player's game.)

## Done this session

Each item's cause, fix and checks are in LIST-0.1.5.md; in short:

- **13, effects' squares** (Jak II and 3): BlitDisplays.cpp leaves the zoom blur, slow time's trail and the copy back
  out of the HUD's and eyes' passes; OpenGLRenderer.cpp's blackout quad dims only the HUD on the panel; Sprite3.cpp
  draws Jak II's screen-space sprites of 4 m and over in the eyes.
- **14, Dark Jak's button**: the left grip's quick squeeze is L2 again (squeezed again and held: L2 held, Light Jak's
  powers); the left stick's click as L2 lasts 0.3 s at least (jak_vr.cpp fill_pad: click_l2_was, click_l2_from,
  click_l2_until, left_quick_tap). Jak 3's test aid (gun-util.gc, `JAKVR_TEST_GUN=all`) keeps both meters full.
- **15, the sky**: DirectRenderer.cpp scales the sky's set depths ("sky" Jak 3, "sky-draw" Jak II) by the eyes' near
  plane over 1024 when nearer (400 to 204 at 356). Jak 1's SkyRenderer untouched.
- **16, multiplayer's host checks in one player's game** (Jak II's mod and Jak 1's Teamruns hooks), **19** (scene.gc's
  same-scene skip in a multiplayer game only).
- **17, controls**: CONTROLS.md's per-game tables; Select and L3 by the stick clicks with the left hand held by the
  head; the Vive wand's Select (its left touchpad's click with no direction in, the hand by the head; pad.h
  `left_click_sensed` false only there); CONTROLS corrected (Jak II's Select alone does nothing, Jak 1's warp gates
  read the d-pad, L3 is Jak II multiplayer's names and HUD).
- **18, first person per ride**: nine switches, the ride kind in the camera flags' bits 16 to 19.
- **20, cross-play**: research only, no code (the recommendation in LIST and the closing message: not to build it;
  flat friends use this port's PC build without a headset). docs/MULTIPLAYER.md "Friends without a headset".
- **21, blackouts black in the eyes** however long, and at the start; **22, B is Circle** where a menu has its own.
- **Texts**: whats_new.txt (New: the head's Select and L3, the wand; Fixed: Dark Jak, the squares, the sky),
  release-notes.md (banner, the same bullets), CONTROLS.md, HEADSET_CHECKLIST.md (the sky, Dark Jak, Select and L3),
  MULTIPLAYER.md, tests/README.md (rows 38 and 39, the 0.1.5 checks to hclick2). Scanned: no user name, no email (the
  patch's two addresses are upstream libraries' copyright lines).

## Builds and sims this session

Builds (C:\JakQuest\sim\build073.sh to build077.sh, one at a time, each OK): 73 gk (items 13, 14's grip, 18, the sky
trace); 73b Jak II and 3 GOAL (vr-ride-kind through pilot-h.gc's vr-pilot-vehicle); 74 gk (items 21, 22); 75 gk (the
blackout still draws the game's frame, unseen); 76 gk and Jak 3 GOAL (item 15, item 14's click, the test aid); 77 gk
(the wand's Select). The Quest's ARM64 runtime compiled after 77 (android_native_check.sh, exit 0). patches/ made
again after it (make_patch.sh).

Sims (batch28h.sh to batch28j.sh, `OUT=<folder> SIMS="..." sh batch28j.sh`; each 0 errors, 0 stalls, 0 crashes):
s74 (pturn2, pauseb1), s75 (boardon, boardoff, zoomoff, eff3, eff2, joinb2), s75b (dark3 and sky3 on build 75: the
"before"), s76 (dark3, sky3, sky2, eff2 on build 76), s77 hclick2 (build 76), s78 hclick2 with the wand part (build
77). Picture checks with C:\JakQuest\sim\crop's panelstats.py, eyedark.py and diffpics.py (`python -P`: -I hides the
user site's PIL).

## Next

- The list is done (LIST-0.1.5.md, every item [x]). Nothing committed, nothing released: the owner says when, then
  SESSION20's "Publishing 0.1.5" in order (Jak II's GOAL is already built with the 0.1.5 version text since 73b; the
  release build makes everything again anyway).
- For a player (HEADSET_CHECKLIST): Dark Jak by the left grip in Jak II and 3, no square with the red guns, Dark
  Jak's lightning or explosions, the sky's far hills in first person, Select and L3 with the hand by the head.
- Item 15's pictures did not catch the fighting itself (the owner: only at some angles); the trace shows the depth
  scaled. A player's report says whether it is gone.

## Publishing 0.1.5 (started 2026-11-17, stopped on the usage limit)

The owner: "go ahead and publish on git as latest release", then "almost out of usage, just stop for now and do
handoff". Done: README's banner (0.1.5), LIST-0.1.5's top ("Released as 0.1.5"), release-notes.md's install file
names (0.1.5) and "What we tested" (the later fixes' sims). VERSION, PcBuild.java and Jak II's version text were
0.1.5 already. This session's sim folders were deleted on the owner's go.

The APK build (C:\JakQuest\sim\build_apk.sh, log C:\JakQuest\sim\apk_build_015.log) was started and its shell
stopped; Gradle went on by itself. First thing: if no java.exe/clang++ runs and the log ends with `== gradle exit 0`
and the APK listed (scripts/android-port/apk/app/build/outputs/apk/release/), it is done; otherwise run build_apk.sh
again (nothing else building meanwhile). Then, in order, as SESSION19's "0.1.4 released":

1. `python -I tools/build_pc_release.py` (log C:\JakQuest\sim\pc_release_015.log; it builds the games and checks
   Jak II's "0.3.1-jakvr.0.1.5").
2. out/release-0.1.5: JakQuestVR-0.1.5.apk, JakQuestVR-0.1.5-windows.zip, SHA256SUMS.txt (`sha256sum --text`, as
   out/release-0.1.4's).
3. tools/make_patch.sh again; the private-details scan (no user names, no email; the tester's Windows user name
   never).
4. Commit on main (all of 0.1.5: the docs, tests/*.sim, patches, launcher files; end the message with
   `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`), push, then
   `gh release create v0.1.5 --target main --title "Jak and Daxter VR 0.1.5" --notes-file docs/release-notes.md
   --latest` with the three files.
5. Give the owner the APK's path and SHA-256 (no adb steps); record it here and in memory.

## 0.1.5 released, 2026-11-17 (Session 22)

The owner: "proceed from where it left off". Finished the steps above:

- build_apk.sh had finished on its own (`== gradle exit 0`; C:\JakQuest\sim\apk_build_015.log): JakQuestVR-0.1.5.apk,
  versionCode 10599, versionName 0.1.5, signed with 0.1.0's key (certificate 66390b53...d59d52a, apksigner verify),
  toolchain.sha256 cbee7961...0d54, SHA-256 e59d05d94fe64c98cd134ef8e6d71c2282b99d9d8288c1eb2ede5e6ddb45b43d. No
  changed file was newer than it.
- `python -I tools/build_pc_release.py` (C:\JakQuest\sim\pc_release_015.log): self-test OK, game code
  de658c00...d452 (pc/game-code.txt), Jak II's "0.3.1-jakvr.0.1.5" checked, JakQuestVR-0.1.5-windows.zip SHA-256
  81a7c7fb5fdeb40d5a3b013537f41fdc2b3b9cda0494f2d03c984d33b6f8b0fa. out/release-0.1.5 with SHA256SUMS.txt.
- Private details: none in the changes; the patch's addresses are the same 31 upstream copyright lines as 0.1.4's.
  release-notes.md named a tester with his quote: now "a player" (as 0.1.4's notes, which name no player).
- make_patch.sh: 2991 files, 33,538,269 bytes. Commit 8fd3d23 on main, pushed.
- **Published:** https://github.com/moistman42069/JakAndDaxter-Quest-VR/releases/tag/v0.1.5 ("Jak and Daxter VR
  0.1.5", latest, not a prerelease, body docs/release-notes.md; the APK, the Windows zip and SHA256SUMS.txt, GitHub's
  digests the same as above).

Next: players' reports on 0.1.5 (HEADSET_CHECKLIST's new checks: Dark Jak by the left grip, no squares from effects,
the sky's far hills, Select and L3 by the head, no flat screen between scenes), then a new list (LIST-0.1.6.md). Next
release: bump VERSION, PcBuild.java and Jak II's mod-settings.gc "0.3.1-jakvr.<ver>".
