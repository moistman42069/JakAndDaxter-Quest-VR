"""Splits the merged Precursor tree into what setup can rebuild from public sources: the files taken whole from the
OpenGOAL release (a list, checked out from the tag), the files the merge removes (a list) and a patch for the rest
(hand-merged files and Precursor's kept ARM64 backend over upstream's). Usage:
python -I upstream_parts.py <repo> <precursor commit> <upstream tag> <merged tree> <out dir>
(tools/update_upstream.sh runs it; the out dir is patches/upstream)"""
import os
import subprocess
import sys
import tempfile
from pathlib import Path

repo, ours, up, tree, out = sys.argv[1:6]
out = Path(out)
out.mkdir(parents=True, exist_ok=True)


def git(*args, env=None, inp=None):
    return subprocess.run(["git", "-C", repo, *args], check=True, capture_output=True, env=env, input=inp).stdout


def ls(t):
    d = {}
    for rec in git("ls-tree", "-r", "-z", "--full-tree", t).split(b"\0"):
        if rec:
            meta, path = rec.split(b"\t", 1)
            mode, _typ, sha = meta.split()
            d[path] = (mode, sha)
    return d


O, U, M = ls(ours), ls(up), ls(tree)
take, remove, other = [], [], []
for p in sorted(set(O) | set(M)):
    if O.get(p) == M.get(p):
        continue
    if p not in M:
        remove.append(p)
    elif U.get(p) == M.get(p):
        take.append(p)
    else:
        other.append(p)
for p in take + remove:
    if b"\n" in p or b"\r" in p:
        sys.exit(f"path with a line break: {p!r}")
(out / "take.txt").write_bytes(b"".join(p + b"\n" for p in take))
(out / "remove.txt").write_bytes(b"".join(p + b"\n" for p in remove))

fd, idx = tempfile.mkstemp()
os.close(fd)
env = dict(os.environ, GIT_INDEX_FILE=idx)
try:
    git("read-tree", ours, env=env)
    info = b"".join(b"%s %s\t%s\n" % (M[p][0], M[p][1], p) for p in take)
    info += b"".join(b"0 0000000000000000000000000000000000000000\t%s\n" % p for p in remove)
    git("update-index", "--index-info", env=env, inp=info)
    mid = git("write-tree", env=env).strip().decode()
finally:
    os.unlink(idx)
patch = git("diff", "--binary", "--no-renames", "--full-index", mid, tree)
(out / "fixups.patch").write_bytes(patch)
print(f"take {len(take)}, remove {len(remove)}, hand/kept {len(other)}; middle tree {mid}; fixups {len(patch)} bytes")
for p in other:
    print("  ", p.decode())
