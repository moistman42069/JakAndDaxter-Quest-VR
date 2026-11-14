"""Protocol test for tools/party_server.py: two clients host, list, join, relay state, send to one player, remove one, leave; a wrong build and an old protocol
are refused.

Run: python -I tests/party_server_test.py tools/party_server.py <a free port>
"""
# SPDX-License-Identifier: GPL-3.0-or-later
# Copyright (c) 2026 the Jak and Daxter Quest VR contributors
import asyncio
import json
import subprocess
import sys


async def client():
    for _ in range(50):
        try:
            return await asyncio.open_connection("127.0.0.1", PORT)
        except OSError:
            await asyncio.sleep(0.1)
    raise SystemExit("server never came up")


async def send(w, obj):
    w.write((json.dumps(obj) + "\n").encode())
    await w.drain()


async def recv(r):
    line = await asyncio.wait_for(r.readline(), 5)
    return json.loads(line)


def check(cond, what):
    print(("ok   " if cond else "FAIL ") + what, flush=True)
    if not cond:
        global failed
        failed = True


async def main():
    hr, hw = await client()
    gr, gw = await client()
    xr, xw = await client()
    await send(hw, {"op": "hello", "proto": 2, "name": "HostJak", "game": "jak1", "build": "jakvr test r1"})
    check((await recv(hr)) == {"op": "hello", "proto": 2}, "host hello")
    await send(gw, {"op": "hello", "proto": 2, "name": "GuestJak", "game": "jak1", "build": "jakvr test r1"})
    check((await recv(gr))["op"] == "hello", "guest hello")
    await send(xw, {"op": "hello", "proto": 2, "name": "OldJak", "game": "jak1", "build": "jakvr old r0"})
    check((await recv(xr))["op"] == "hello", "other-build hello")

    await send(hw, {"op": "host", "title": "HostJak's game"})
    joined = await recv(hr)
    check(joined.get("op") == "joined" and joined.get("index") == 0, "host gets index 0")
    party = joined.get("party")

    await send(gw, {"op": "list"})
    games = (await recv(gr)).get("games", [])
    check(len(games) == 1 and games[0]["host"] == "HostJak" and games[0]["build"] == "jakvr test r1", "guest lists it")
    check(games[0].get("where") == "" and games[0].get("cells") == -1, "no level or cells until the host says")
    await send(hw, {"op": "where", "where": "Sentinel Beach", "cells": 23})
    await send(gw, {"op": "list"})
    games = (await recv(gr)).get("games", [])
    check(games and games[0].get("where") == "Sentinel Beach" and games[0].get("cells") == 23, "the host's level and cells listed")

    await send(xw, {"op": "join", "party": party})
    refused = await recv(xr)
    check(refused.get("op") == "error" and "version" in refused.get("why", ""), "other build refused")

    await send(gw, {"op": "join", "party": party})
    gj = await recv(gr)
    check(gj.get("op") == "joined" and gj.get("index") == 1, "guest joins as 1")

    await send(gw, {"op": "state", "data": {"position": {"transX": 1.5}}})
    st = await recv(hr)
    check(st == {"op": "state", "from": 1, "name": "GuestJak", "data": {"position": {"transX": 1.5}}},
          "guest state relayed to host")
    await send(hw, {"op": "state", "data": {"levels": ["village1"]}})
    st = await recv(gr)
    check(st.get("from") == 0 and st.get("data") == {"levels": ["village1"]}, "host state relayed to guest")

    # one player alone: the member asks the host for its progress, the host sends it in parts
    await send(gw, {"op": "to", "index": 0, "data": {"save": "ask"}})
    check((await recv(hr)) == {"op": "from", "from": 1, "data": {"save": "ask"}}, "a member's ask reaches the host alone")
    part = {"save": "part", "n": 0, "of": 1, "data": "AAAA"}
    await send(hw, {"op": "to", "index": 1, "data": part})
    check((await recv(gr)) == {"op": "from", "from": 0, "data": part}, "the host's part reaches the member")

    # only the host lists its level or takes a player out
    await send(gw, {"op": "where", "where": "Nowhere", "cells": 99})
    await send(gw, {"op": "remove", "index": 0})
    await send(gw, {"op": "list"})
    games = (await recv(gr)).get("games", [])
    check(games and games[0].get("where") == "Sentinel Beach" and games[0].get("players") == 2, "a member cannot")

    await send(hw, {"op": "remove", "index": 1})
    check((await recv(gr)) == {"op": "removed"}, "the removed player is told")
    check((await recv(hr)) == {"op": "left", "from": 1}, "the host hears the player left")
    await send(gw, {"op": "state", "data": {"position": {"transX": 2.0}}})
    await send(gw, {"op": "join", "party": party})
    check((await recv(gr)).get("index") == 1, "a removed player can join again")

    await send(gw, {"op": "leave"})
    check((await recv(hr)) == {"op": "left", "from": 1}, "host told the guest left")

    await send(gw, {"op": "join", "party": party})
    check((await recv(gr)).get("index") == 1, "guest rejoins")
    await send(hw, {"op": "leave"})
    check((await recv(gr)) == {"op": "closed"}, "guest told the game closed when the host left")
    await send(gw, {"op": "list"})
    check((await recv(gr)).get("games") == [], "the list is empty after")

    # An older game's protocol is refused.
    or_, ow = await client()
    await send(ow, {"op": "hello", "proto": 1, "name": "OldJak", "game": "jak1", "build": "jakvr test r1"})
    old = await recv(or_)
    check(old.get("op") == "error" and "party server" in old.get("why", ""), "protocol 1 refused")
    ow.close()

    # A line over the limit drops only that client; the server keeps serving.
    lr, lw = await client()
    lw.write(b"{" + b"x" * (70 * 1024) + b"}\n")
    await lw.drain()
    try:
        gone = (await asyncio.wait_for(lr.readline(), 5)) == b""
    except (ConnectionError, asyncio.TimeoutError):
        gone = True
    check(gone, "an over-long line closes that client")
    await send(gw, {"op": "list"})
    check((await recv(gr)).get("op") == "games", "server still answers")
    for w in (hw, gw, xw):
        w.close()


failed = False
SERVER, PORT = sys.argv[1], int(sys.argv[2])
proc = subprocess.Popen([sys.executable, "-I", SERVER, "--port", str(PORT)], stdout=subprocess.PIPE,
                        stderr=subprocess.STDOUT, text=True)
try:
    asyncio.run(main())
finally:
    proc.terminate()
    out, _ = proc.communicate(timeout=10)
    print("server said:", out.strip().replace("\n", " | ")[:400])
print("PASS" if not failed else "FAILED")
sys.exit(1 if failed else 0)
