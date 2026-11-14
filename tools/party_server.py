#!/usr/bin/env python3
# SPDX-License-Identifier: GPL-3.0-or-later
# Copyright (c) 2026 the Jak and Daxter Quest VR contributors
"""Jak and Daxter Quest VR's party server: the list of games and the relay between their players.

The games (gk, game/party.cpp) connect here over TCP and speak newline-delimited JSON, one object a line:

  client -> server
    {"op": "hello", "proto": 2, "name": "<player>", "game": "jak1", "build": "<what must match>"}
    {"op": "list"}                         -> {"op": "games", "games": [{"id", "title", "host", "players", "build",
                                                                         "where", "cells"}]}
    {"op": "host", "title": "<party name>"} -> {"op": "joined", "party": id, "index": 0}
    {"op": "join", "party": id}            -> {"op": "joined", "party": id, "index": k} or {"op": "error", "why"}
    {"op": "state", "data": {...}}         -> to the party's others: {"op": "state", "from": k, "name", "data"}
    {"op": "to", "index": k, "data": {...}} -> to player k of the party alone: {"op": "from", "from": j, "data"}
                                              (the host's progress for a member, as a save, in parts)
    {"op": "where", "where": "<level>", "cells": n}  the host: its level and power cells, for the list
    {"op": "remove", "index": k}           the host: player k out of the game (it hears {"op": "removed"})
    {"op": "leave"}

  server -> client, besides the replies: {"op": "left", "from": k} when a member leaves; {"op": "closed"} when the
  host leaves (the party ends).

A game hosting on its own network runs the same relay inside gk; this program is for parties over the internet,
where nobody has to open a port on their router: everyone connects out to it. It keeps nothing on disk and logs only
counts. Run it anywhere with Python 3.8 or later:

  python party_server.py [--port 8120]
"""
import argparse
import asyncio
import json
import time

PROTO = 2
MAX_LINE = 64 * 1024
MAX_QUEUED = 4 * 1024 * 1024  # bytes waiting to go out to one game; more and it is dropped
MAX_PARTY = 8           # Teamruns' MAX_MULTIPLAYER_COUNT less the local player's slot
MAX_PARTIES = 500
IDLE_SECONDS = 60       # a game that says nothing this long is dropped


class Member:
    def __init__(self, writer):
        self.writer = writer
        self.name = "Player"
        self.game = ""
        self.build = ""
        self.party = None
        self.index = -1
        self.heard = time.monotonic()

    def send(self, obj):
        try:
            # A game that stopped reading is dropped rather than queued for without end.
            if self.writer.transport.get_write_buffer_size() > MAX_QUEUED:
                self.writer.close()
                return
            self.writer.write((json.dumps(obj, separators=(",", ":")) + "\n").encode("utf-8"))
        except Exception:
            pass


class Party:
    next_id = 1

    def __init__(self, host, title):
        self.id = Party.next_id
        Party.next_id += 1
        self.title = title[:40] or "Party"
        self.game = host.game
        self.build = host.build
        self.where = ""  # the host's level and power cells ("where")
        self.cells = -1
        self.members = {}
        self.add(host)

    def add(self, m):
        index = next(i for i in range(MAX_PARTY) if i not in self.members)
        self.members[index] = m
        m.party = self
        m.index = index
        return index

    def summary(self):
        host = self.members.get(0)
        return {"id": self.id, "title": self.title, "host": host.name if host else "", "game": self.game,
                "players": len(self.members), "build": self.build, "where": self.where, "cells": self.cells}


parties = {}


def leave(m):
    p = m.party
    if p is None:
        return
    m.party = None
    p.members.pop(m.index, None)
    if m.index == 0:
        for other in p.members.values():
            other.send({"op": "closed"})
            other.party = None
        parties.pop(p.id, None)
    else:
        for other in p.members.values():
            other.send({"op": "left", "from": m.index})
    m.index = -1


def remove(p, index):
    """The host takes player index out of its game."""
    target = p.members.get(index)
    if index == 0 or target is None:
        return
    target.send({"op": "removed"})
    leave(target)


def handle(m, msg):
    op = msg.get("op")
    if op == "to":
        p = m.party
        index = msg.get("index")
        if p is not None and isinstance(index, int) and index != m.index and isinstance(msg.get("data"), dict):
            other = p.members.get(index)
            if other is not None:
                other.send({"op": "from", "from": m.index, "data": msg["data"]})
    elif op == "where":
        p = m.party
        if p is not None and m.index == 0:
            where = msg.get("where")
            cells = msg.get("cells")
            p.where = where[:32] if isinstance(where, str) else ""
            p.cells = cells if isinstance(cells, int) and not isinstance(cells, bool) and 0 <= cells <= 101 else -1
    elif op == "remove":
        p = m.party
        if p is not None and m.index == 0 and isinstance(msg.get("index"), int):
            remove(p, msg["index"])
    elif op == "state":
        p = m.party
        if p is not None and isinstance(msg.get("data"), dict):
            out = {"op": "state", "from": m.index, "name": m.name, "data": msg["data"]}
            for i, other in p.members.items():
                if i != m.index:
                    other.send(out)
    elif op == "hello":
        if msg.get("proto") != PROTO:
            m.send({"op": "error", "why": "This game needs a newer or older party server."})
            return
        m.name = str(msg.get("name", "Player"))[:24] or "Player"
        m.game = str(msg.get("game", ""))[:8]
        m.build = str(msg.get("build", ""))[:64]
        m.send({"op": "hello", "proto": PROTO})
    elif op == "list":
        games = [p.summary() for p in parties.values() if p.game == m.game]
        m.send({"op": "games", "games": games})
    elif op == "host":
        leave(m)
        if len(parties) >= MAX_PARTIES:
            m.send({"op": "error", "why": "The server is full."})
            return
        p = Party(m, str(msg.get("title", "")))
        parties[p.id] = p
        m.send({"op": "joined", "party": p.id, "index": 0})
    elif op == "join":
        leave(m)
        p = parties.get(msg.get("party"))
        if p is None:
            m.send({"op": "error", "why": "That game has ended."})
        elif p.game != m.game or p.build != m.build:
            m.send({"op": "error", "why": "That game is on a different version: everyone needs the same release."})
        elif len(p.members) >= MAX_PARTY:
            m.send({"op": "error", "why": "That game is full."})
        else:
            index = p.add(m)
            m.send({"op": "joined", "party": p.id, "index": index})
    elif op == "leave":
        leave(m)


async def serve(reader, writer):
    m = Member(writer)
    try:
        while True:
            line = await asyncio.wait_for(reader.readline(), IDLE_SECONDS)
            if not line or len(line) > MAX_LINE:
                break
            m.heard = time.monotonic()
            try:
                msg = json.loads(line)
            except ValueError:
                continue
            if isinstance(msg, dict):
                handle(m, msg)
                await writer.drain()
    except (asyncio.TimeoutError, ConnectionError, asyncio.IncompleteReadError, ValueError):
        pass  # ValueError: a line longer than the reader's limit
    finally:
        leave(m)
        writer.close()


async def report():
    while True:
        await asyncio.sleep(300)
        players = sum(len(p.members) for p in parties.values())
        print(f"{len(parties)} games, {players} players", flush=True)


async def main():
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    ap.add_argument("--port", type=int, default=8120)
    args = ap.parse_args()
    server = await asyncio.start_server(serve, host=None, port=args.port)
    print(f"Jak and Daxter Quest VR party server on port {args.port}", flush=True)
    asyncio.ensure_future(report())
    async with server:
        await server.serve_forever()


if __name__ == "__main__":
    asyncio.run(main())
