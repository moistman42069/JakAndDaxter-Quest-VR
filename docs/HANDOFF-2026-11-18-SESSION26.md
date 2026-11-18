# Handoff, 2026-11-18, Session 26

Read this first, then docs/LIST-0.1.6.md (items 1 to 28, each with how it was checked). 0.1.5 is published (latest
until 0.1.6). The owner: "make a new release ... proceed with list and publish when done. dont ask me. you got this."
VERSION, PcBuild.java and Jak II's *mod-version-text* ("0.3.1-jakvr.0.1.6") are 0.1.6 already.

## The asks this session (0.1.6)

1. A tester's reports (with their 0.1.5 jak2 and jak3 logs): "jak3 jet board grind control inconvenient for vr ...
   x and b's button actions switch" (item 23); "holding gun in hand while driving is currently just jak 2 thing"
   (item 26, no change); "jak 3 a cutscene didn't play", twice (item 24); "jak 2 black screen issue, mission seems to
   be have screen dark even after gameplay has loaded" (item 25); "finally got around to testing big button
   challenge, it works i got all the loot" (item 26).
2. The same tester, with a jak1 log: "cutscene after blasting rock in boggy swamp glues your feet to ground", then
   with a second log: "i tried a different rock that does the same thing in boggy swamp" (item 27).

## Done this session

- **23** Jak 3's board: B is the grind (Square, held), X the zap (Circle), only on Jak 3's board (jak_vr.cpp
  board_swap; CONTROLS.md, so both launchers' Controls). Sim board3s.
- **24** Jak 3's Torn briefing ending at once: the end came on the frame focus came back; Triangle (Y) is the skip.
  xr_openxr.cpp: buttons held as focus comes back count once let go (every game); scene.gc: Jak 3's skip only after a
  second; log lines `[openxr] focus lost/back`, `[vr] the scene ... skipped: Triangle, N s in`. Sim skip3.
- **25** Jak II's dark after mission loads: item 8's clock fix plus main.gc's vr-solo-toggle-pause on the
  presentation clock (build 84).
- **27** Jak 1's tether rocks: 0.1.5's merge put `(process-release? *target*)` inside the last rock's
  `(when (zero? s5-2) ...)`, so rocks 1 to 3 never let Jak go. Item 20 had already put the original back alone; the
  alone branch is token for token the original (C:\JakQuest\sim\swamp015: upstream.gc, the rebuilt 0.1.5 file, and
  the scratch scripts release_scope.py / form_at.py that showed it). New test aid JAKVR_TEST_TETHER=1 (kmachine.cpp
  bit 16384, pc-vr.gc vr-test-tether!) and tests/jak1_tether_rock.sim; C:\JakQuest\sim\run_tether.sh.
- **28** Found by the b31 sims: every Jak II sim stopped at the prison's warp gate ("suspend called without enough
  stack ... warp-gate-b ... 640/512"). Item 20's Jak II wrappers called the original :code as a function from a :code
  that still held the mod's code inline, so a wait kept both frames. The 20 wrappers whose original suspends in a
  process with a cut stack (warp gate 512; airlocks, race manager, board training 1024; task-manager hooks 3072) now
  call the mod's code as a lambda of its own (`((lambda :behavior <type> (<args>) ...) <args>)`: a lambda in a call's
  head is a real function, only `:immediate` ones inline). Others have 16 KB. Jak 1's put-backs are symbol tests in
  the same function; Jak 3 has no wrappers. Scratch checks: orig_suspends.py, begin_sites.py, lambda_check.py.

## Builds and sims

- Build 84 (gk, Jak II, Jak 3), 85 (gk, Jak 3), 86 (gk, Jak 1, Jak II: item 27's test aid, item 28's lambdas): all
  clean, Jak II 16 warnings as before.
- b31 (C:\JakQuest\sim\run_b31.sh, build 85): board3s, skip3, buttons3, light3 clean; the eight Jak II sims stopped
  at the warp gate (item 28).
- b32 (run_b32.sh, build 86): the tether rock, then the eight Jak II sims again. Results below.

## Next

The LIST's items marked [x], the multiplayer sessions (Jak 1 batch11b.sh, Jak II `OUT=b32 sh mp2.sh`: the mod's
code is the session's path, now in the lambdas), the ARM64 check, then the publish steps as SESSION21's "Publishing
0.1.5" with 0.1.6's names.
