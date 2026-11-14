#!/bin/sh
# Writes the port's changes to Precursor back into patches/ (Git Bash):
#   sh tools/make_patch.sh
# 0001: everything changed or added in third_party/Precursor against the merged base (Precursor plus the OpenGOAL
# release, patches/upstream; tools/setup.sh makes it and keeps it as refs/jakvr/base), the OpenXR headers and the
# extracted discs left out; 0002: the OpenXR headers. A scratch index is used, so the submodule's own index is untouched.
set -e
ROOT=$(cd "$(dirname "$0")/.." && pwd)
P=$ROOT/third_party/Precursor
mkdir -p "$ROOT/patches"
IDX=$(mktemp)
trap 'rm -f "$IDX"' EXIT
cd "$P"
BASE=$(git rev-parse --verify refs/jakvr/base)
export GIT_INDEX_FILE="$IDX"
git read-tree "$BASE"
git add -A -- . ':!iso_data' ':!third-party/openxr'
git diff --cached --binary "$BASE" > "$ROOT/patches/0001-jak-quest-vr.patch"
git read-tree "$BASE"
git add -A -- third-party/openxr
git diff --cached --binary "$BASE" > "$ROOT/patches/0002-openxr-headers.patch"
unset GIT_INDEX_FILE
cd "$ROOT"
for f in patches/*.patch; do
  echo "$f: $(grep -c '^diff --git' "$f") files, $(wc -c < "$f") bytes"
done
