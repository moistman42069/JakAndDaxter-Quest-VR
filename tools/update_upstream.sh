#!/bin/sh
# Brings a newer OpenGOAL release into Precursor the way v0.3.9 went in (docs/UPDATING-OPENGOAL.md) (Git Bash):
#   sh tools/update_upstream.sh start <tag> [<precursor commit>]
#       merges the release into Precursor in a worktree beside this folder (../precursor-merge), keeping Precursor's
#       ARM64 backend over OpenGOAL's (patches/upstream/arm64-commits.txt), and lists what is left to merge by hand.
#       Resolve those there and git add them.
#   sh tools/update_upstream.sh finish
#       records the merge in patches/upstream (take.txt, remove.txt, fixups.patch, BASE).
#   sh tools/update_upstream.sh move
#       puts third_party/Precursor on the new base and the port's patches back on top, three-way; resolve what it
#       lists, then: git read-tree refs/jakvr/base (in third_party/Precursor) and sh tools/make_patch.sh.
# Nothing is committed. Then build (tools/build_pc.bat), compile the three games and run their sim tests.
set -e
ROOT=$(cd "$(dirname "$0")/.." && pwd)
P=$ROOT/third_party/Precursor
UP=$ROOT/patches/upstream
WT=${JAKVR_MERGE_WORKTREE:-$(cd "$ROOT/.." && pwd)/precursor-merge}
WORK=$ROOT/out/upstream-merge
URL=https://github.com/open-goal/jak-project.git

case "$1" in
start)
  TAG=$2
  [ -n "$TAG" ] || { echo "usage: sh tools/update_upstream.sh start <tag> [<precursor commit>]" >&2; exit 2; }
  . "$UP/BASE"
  PREC=$(git -C "$P" rev-parse --verify "${3:-$PRECURSOR}^{commit}")
  [ -e "$WT" ] && { echo "$WT is in the way: git worktree remove it first" >&2; exit 1; }
  cd "$P"
  git fetch --no-tags "$URL" "+refs/tags/$TAG:refs/tags/$TAG"
  FORK=$(git merge-base "$PREC" "$TAG")
  git worktree add --detach "$WT" "$PREC"
  rm -rf "$WORK"
  mkdir -p "$WORK"
  echo "$TAG $PREC" > "$WORK/state"
  cd "$WT"
  git merge --no-commit --no-ff "$TAG" > "$WORK/merge.txt" 2>&1 || true
  python -I "$ROOT/tools/upstream_classify.py" "$WT" "$FORK" "$PREC" "$TAG" "$UP/arm64-commits.txt" "$WORK"
  git ls-tree -r --name-only "$PREC" | sort > "$WORK/ours_files.txt"
  sort "$WORK/pure_arm64.txt" | grep -v '^$' > "$WORK/pure_sorted.txt" || true
  comm -12 "$WORK/pure_sorted.txt" "$WORK/ours_files.txt" > "$WORK/pure_keep.txt"
  comm -23 "$WORK/pure_sorted.txt" "$WORK/ours_files.txt" > "$WORK/pure_remove.txt"
  echo "Precursor's version kept: $(wc -l < "$WORK/pure_keep.txt"); OpenGOAL's ARM64-only files left out: $(wc -l < "$WORK/pure_remove.txt")"
  [ -s "$WORK/pure_keep.txt" ] && tr '\n' '\0' < "$WORK/pure_keep.txt" | xargs -0 -n 200 git checkout "$PREC" --
  [ -s "$WORK/pure_remove.txt" ] && tr '\n' '\0' < "$WORK/pure_remove.txt" | xargs -0 -n 200 git rm -q -f --ignore-unmatch --
  while read -r f cs; do
    [ -z "$f" ] && continue
    if grep -qxF "$f" "$WORK/ours_files.txt"; then git checkout "$PREC" -- "$f"; else git rm -q -f --ignore-unmatch -- "$f"; fi
    for c in $cs; do
      if git show "$c" --format= -- "$f" | git apply -3 --index 2> "$WORK/apply_err.txt"; then
        echo "  $f: Precursor's plus $(git log -1 --format=%h "$c")"
      else
        echo "  $f: BY HAND, $(git log -1 --format='%h %s' "$c") ($(head -1 "$WORK/apply_err.txt"))"
      fi
    done
  done < "$WORK/mixed.txt"
  echo "left to merge by hand in $WT:"
  git diff --name-only --diff-filter=U
  ;;
finish)
  read -r TAG PREC < "$WORK/state"
  cd "$WT"
  LEFT=$(git diff --name-only --diff-filter=U)
  [ -z "$LEFT" ] || { echo "still to merge by hand: $LEFT" >&2; exit 1; }
  MARKED=$(git diff --cached --name-only -z "$PREC" | xargs -0 grep -l -E '^(<<<<<<<|>>>>>>>) ' -- 2>/dev/null || true)
  [ -z "$MARKED" ] || { echo "conflict markers left in: $MARKED" >&2; exit 1; }
  TREE=$(git write-tree)
  python -I "$ROOT/tools/upstream_parts.py" "$WT" "$PREC" "$TAG" "$TREE" "$UP"
  {
    sed -n '/^#/p' "$UP/BASE"
    echo "PRECURSOR=$PREC"
    echo "TAG=$TAG"
    echo "TAG_COMMIT=$(git rev-parse "refs/tags/$TAG^{commit}")"
    echo "URL=$URL"
    echo "TREE=$TREE"
  } > "$WORK/BASE"
  mv "$WORK/BASE" "$UP/BASE"
  echo "recorded OpenGOAL $TAG on Precursor $(git rev-parse --short "$PREC"): $TREE"
  ;;
move)
  . "$UP/BASE"
  cd "$ROOT"
  sh tools/make_patch.sh
  rm -rf "$WORK/old-patches"
  mkdir -p "$WORK/old-patches"
  cp patches/*.patch "$WORK/old-patches/"
  cd "$P"
  OLD=$(git rev-parse --verify refs/jakvr/base)
  for p in $(ls "$WORK"/old-patches/*.patch | sort -r); do git apply -R --check "$p"; done
  for p in $(ls "$WORK"/old-patches/*.patch | sort -r); do git apply -R --whitespace=nowarn "$p"; done
  git read-tree "$OLD"
  if ! git diff --quiet "$OLD" -- . ':!iso_data' || [ -n "$(git ls-files --others --exclude-standard -- . ':!iso_data')" ]; then
    echo "third_party/Precursor has changes outside the patches; the patches are in $WORK/old-patches" >&2
    exit 1
  fi
  # Only the old base and the port's patches (kept in old-patches) were there: back to Precursor's commit.
  git checkout -q --detach "$PRECURSOR"
  git reset -q --hard "$PRECURSOR"
  git update-ref -d refs/jakvr/base
  cd "$ROOT"
  sh tools/setup.sh --base-only
  git add third_party/Precursor
  cd "$P"
  for p in "$WORK"/old-patches/*.patch; do
    git apply -3 --whitespace=nowarn "$p" 2>&1 | grep -E 'with conflicts|error' || true
  done
  echo "left to merge by hand in third_party/Precursor:"
  git diff --name-only --diff-filter=U
  ;;
*)
  sed -n '2,13p' "$0"
  exit 2
  ;;
esac
