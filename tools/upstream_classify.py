"""Sorts every file OpenGOAL changed since Precursor forked: changed only by OpenGOAL's ARM64 backend commits
(Precursor's own backend replaces that work, so Precursor's file is kept), or by those and other commits too
(Precursor's file plus the other commits). Writes pure_arm64.txt and mixed.txt ("<file> <commit> <commit>...", oldest
first) to the out folder. Usage (tools/update_upstream.sh runs it):
python -I upstream_classify.py <repo> <fork point> <precursor commit> <tag> <arm64-commits.txt> <out dir>"""
import subprocess
import sys
from pathlib import Path

repo, base, ours, theirs, arm_list, out = sys.argv[1:7]
ARM64 = {line.split()[0] for line in Path(arm_list).read_text().splitlines() if line.strip() and not line.startswith("#")}


def git(*args):
    return subprocess.run(["git", "-C", repo, *args], check=True, capture_output=True, text=True).stdout


commits = {}
current = None
for line in git("log", "--format=@%H", "--name-only", f"{base}..{theirs}").splitlines():
    if line.startswith("@"):
        current = line[1:]
    elif line.strip():
        commits.setdefault(line.strip(), []).append(current)
pure, mixed = [], []
for f, cs in sorted(commits.items()):
    if all(c in ARM64 for c in cs):
        pure.append(f)
    elif any(c in ARM64 for c in cs):
        mixed.append((f, [c for c in reversed(cs) if c not in ARM64]))
Path(out).mkdir(parents=True, exist_ok=True)
Path(out, "pure_arm64.txt").write_text("".join(f + "\n" for f in pure), newline="\n")
Path(out, "mixed.txt").write_text("".join(f"{f} {' '.join(cs)}\n" for f, cs in mixed), newline="\n")
print(len(pure), "files only OpenGOAL's ARM64 work;", len(mixed), "also changed by other commits")
