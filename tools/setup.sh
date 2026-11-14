#!/bin/sh
# Gets Precursor at its pinned commit, merges in OpenGOAL's release the way patches/upstream records, and applies the
# port's patches (Git Bash):
#   sh tools/setup.sh
# Run once after cloning. Safe to run again: a step already done is left alone.
set -e
ROOT=$(cd "$(dirname "$0")/.." && pwd)
UP=$ROOT/patches/upstream
. "$UP/BASE"
cd "$ROOT"
git submodule update --init third_party/Precursor
cd third_party/Precursor
if [ "$(git rev-parse -q --verify refs/jakvr/base 2>/dev/null)" = "$TREE" ]; then
  echo "OpenGOAL $TAG already merged"
else
  if [ "$(git rev-parse HEAD)" != "$PRECURSOR" ] || ! git diff --quiet HEAD -- . ':!iso_data' \
      || [ -n "$(git status --porcelain -- . ':!iso_data')" ]; then
    echo "Precursor is not clean at $PRECURSOR; merge $TAG by hand (docs/UPDATING-OPENGOAL.md)" >&2
    exit 1
  fi
  if [ "$(git rev-parse -q --verify "refs/tags/$TAG^{commit}" 2>/dev/null)" != "$TAG_COMMIT" ]; then
    git fetch --no-tags "$URL" "+refs/tags/$TAG:refs/tags/$TAG"
  fi
  [ "$(git rev-parse "refs/tags/$TAG^{commit}")" = "$TAG_COMMIT" ] || { echo "$TAG is not $TAG_COMMIT" >&2; exit 1; }
  tr '\n' '\0' < "$UP/take.txt" | xargs -0 -n 200 git checkout "$TAG_COMMIT" --
  tr '\n' '\0' < "$UP/remove.txt" | xargs -0 -n 200 git rm -q -f --
  git apply --index --whitespace=nowarn "$UP/fixups.patch"
  GOT=$(git write-tree)
  if [ "$GOT" != "$TREE" ]; then
    echo "the merge came out as $GOT, not $TREE" >&2
    exit 1
  fi
  git update-ref refs/jakvr/base "$TREE"
  echo "merged OpenGOAL $TAG"
fi
# tools/update_upstream.sh puts the port's patches on itself, three-way
[ "$1" = "--base-only" ] && exit 0
for p in "$ROOT"/patches/*.patch; do
  if git apply --check "$p" 2>/dev/null; then
    git apply --whitespace=nowarn "$p"
    echo "applied $(basename "$p")"
  elif git apply --check --reverse "$p" 2>/dev/null; then
    echo "already in place: $(basename "$p")"
  else
    echo "does not apply: $(basename "$p") (Precursor not at the merged base?)" >&2
    exit 1
  fi
done
echo "Precursor ready. Next: tools/build_pc.bat"
