#!/bin/sh
# Run one of the games on the simulated headset (Git Bash):
#   tools/run_sim.sh <jak1|jak2|jak3> <script.sim> <out folder> [extra env assignments...]
# Pictures and the run's log (run.log) go to the out folder. The script's "quit" line ends the run.
set -e
GAME=$1
SCRIPT=$2
OUT=$3
shift 3
P=/c/JakQuest/JakQuestVR/third_party/Precursor
CFG=${SIM_CFG:-/c/JakQuest/sim/gkcfg}
mkdir -p "$OUT" "$CFG"
# A fresh start each run (KEEP_SAVES=1 keeps them): a save from an earlier run changes the menus.
[ -n "$KEEP_SAVES" ] || rm -rf "$CFG/OpenGOAL/$GAME/saves"
env QUEST_SIM_SCRIPT="$(cygpath -w "$SCRIPT")" QUEST_SIM_OUT="$(cygpath -w "$OUT")" "$@" \
  "$P/out/build/Release/bin/gk.exe" -g "$GAME" --proj-path "$(cygpath -w "$P")" --config-path "$(cygpath -w "$CFG")" \
  -- -boot -fakeiso > "$OUT/run.log" 2>&1
echo "exit $? ($OUT/run.log)"
