# Handoff, 2026-11-18, Session 27

Read this first, then docs/LIST-0.1.6.md (items 1 to 29, each with how it was checked). 0.1.6 is published (latest,
2026-11-18): https://github.com/moistman42069/JakAndDaxter-Quest-VR/releases/tag/v0.1.6, commits 3c4235f (Session
26's 0.1.6) and 498429d (this session's items 27 and 29) pushed, tag v0.1.6 on 498429d.

## The asks this session (0.1.6)

1. The same tester as Session 26's item 27, with three more 0.1.5 logs (Jak 1, PC with SteamVR): "3rd rock also shoe
   glue. yep all the cutscene rocks are shoe glue. a ledge that should be grabable is not, so i cant take on boss fight
   i need in order to progress to next stage". The owner: "look into it and see if they are already fixed, or need to
   be. if so, fix, THEN proceed. proceed with list." Then, picking up Session 26's second half (it stopped on its usage
   limit): "pickup from where it left off. miss/forget nothing. proceed with list".

## Done this session

- **27** (more rocks): the logs hold Jak after `swamp-tetherrock-15` and `-14`, 0.1.5's fault, which 0.1.6 already
  fixes for every rock (item 20's put-back). Checked on build 89, each of the four rocks in a run of its own (s27):
  `target-grab` in its scene, then `target-stance` and Jak walking.
- **29** Jak 1's ledges: Teamruns gives each player a ledge search of their own (an edge collide cache and work each,
  `*edge-collide-cache-list*`, `*edge-collide-work-list*`); its collide-cache.gc fill-using-bounding-box puts the box
  in the work it is passed, and the level's tests (fill-from-background, collide-with-box,
  collide-cache-using-box-test) read that work's box. Our merge kept the original's `(let ((a0-1 *collide-work*))`,
  so the ledge search's own work never had a box and found none of the level's tris: no ledge of a level grabbed since
  0.1.0 (0.1.0's and 0.1.5's patches both lack the line), alone or in a party. Teamruns' `coll-work` is back, and
  alone (`*vr-party-on?*` #f) the ledge search uses the original's `*collide-cache*` and `*collide-work*`
  (collide-edge-grab.gc, logic-target.gc's four calls, target2.gc, target-tube.gc). Every other fill passes
  `*collide-work*` (C:\JakQuest\sim\audit-0.1.6.txt's last entry; scratch work_args.py lists the callers).
  - Test aid (build 88, 89): JAKVR_STATE_TRACE also logs `[vr] ledge search: N tris, N edges, N in reach; Jak at x y z`
    each time the search's finds change while Jak may grab (pc-vr.gc vr-state-trace).
  - At that cliff (the continue villageb-ogreboss-1, Jak at 270.1, 28.1, -1753.7) runs and double jumps top out near
    31.4 m under an overhang, `0 tris` in reach; the player's log has Jak twice at 34.85 m in the air, a crouch's high
    jump (duck-jump-height 5 to 7 m). With it (left trigger, then A held) Jak grabs the ledge and climbs onto the grass:
    tests/jak1_ledge_klaww.sim (crouch and high jump at frame 10710 to 10790), run_ledge.sh's default offset now
    23.53,-11.3,8.82.
  - While Jak hangs, first person puts the eyes where his head is, just under the ledge's top: the grass is drawn from
    under it (the level's faces are drawn from both sides). Asked of players in HEADSET_CHECKLIST, not changed.

## Builds and sims

- Builds 87 (the fix), 88 (the trace line), 89 (the trace line's reach only with edges): Jak 1 GOAL only, each clean
  (its one warning, PC_KERNEL_VERSION, as before). gk and the Quest's ARM64 code are build 86's.
- C:\JakQuest\sim\s27 (scratch): explore, explore2, explore3 (runs, double jumps, a look round at the cliff, held
  jumps: no grab), explore4 (h26 with the trace: `0 tris`; up: straight up, top 31.24 m; high: the crouch's high jump,
  the grab at 34.64 m on build 88, then on top), then run_b35.sh on build 89: ledge-after (1 grab, at 33.62 m, Jak
  hanging, then on top), the four rocks, mp1 (host and guest, the progress sent and played), play1; no stack line,
  error, stall or crash in any.

## Next

- 0.1.6 released: JakQuestVR-0.1.6.apk (versionCode 10699, versionName 0.1.6, 0.1.0's key, certificate
  66390b53...d59d52a by apksigner; its toolchain.zip has the ledge fix and the trace line), SHA-256
  3bd2b8e56c28842003ff6abd349aae7fc45e0dd200d3ad5bdd794187670dcd0d; JakQuestVR-0.1.6-windows.zip (self-test OK, game
  code 5ab83d7a...2644 in pc/game-code.txt, Jak II's "0.3.1-jakvr.0.1.6" checked), SHA-256
  96b4865f75173eec0ac8233b7676e399ca1114e4e3b3fae0532ddb87c6e9c7c7; out/release-0.1.6 with SHA256SUMS.txt. Logs:
  C:\JakQuest\sim\apk_build_016b.log, pc_release_016b.log.
- Players' reports on 0.1.6, then LIST-0.1.7.md. For a player (HEADSET_CHECKLIST): Jak 1's ledges (the climb to
  Klaww, a high jump), and how first person feels while Jak hangs from one.
- The next release bumps VERSION, PcBuild.java and Jak II's *mod-version-text* ("0.3.1-jakvr.<ver>").
- Scratch to clear on the owner's go (listed, not deleted): C:\JakQuest\sim\s27 and the earlier run folders.
