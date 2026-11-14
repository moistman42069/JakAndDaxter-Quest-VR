# Multiplayer (Jak 1)

Jak 1 can be played together: the others' Jaks run around your world with their names over them, and what one
player takes (power cells, scout flies, orbs, crates, beaten enemies and tasks) everyone has. Up to 8 players a game.
Every Jak is in his own colors; the name over him says whose he is.

It is [Teamruns](https://github.com/JoKronk/teamruns-jak-project)' multiplayer, built in as a mod; the games you host
and join, the list of games and the saves are this port's own, laid out as Ratchet and Clank Quest VR's Multiplayer.
Jak II and Jak 3 have none.

## On to start, and changes nothing until you play together

Multiplayer is on when the game is installed. Until you host or join a game it changes nothing in Jak 1: the game,
its cutscenes and its saves are as they always were, and it opens no network port. Only when you host, join or look
for games does the game go on the network, and only in a game are the others' Jaks shown, the in-game cutscenes
skipped (so nobody waits on another's) and the taunts on the d-pad.

To keep it out of the game altogether, turn it off:

- **PC:** the launcher's **Mods** tab, **Multiplayer: Off**.
- **Quest:** Jak 1's options page in the launcher, **Mods**, **Multiplayer: Off**.
- Or in the game: the VR menu's **Multiplayer** row.

With it on, the launcher also has:

- **Your name:** what the others see over your Jak (up to 24 letters, digits and signs).
- **Party server:** leave it empty to find games on your own network. See [Over the internet](#over-the-internet).

Everyone in a game needs the same release. A game from another release shows in the list as "(other version)" and
cannot be joined.

## Every VR feature, for everyone

Whatever the host plays on (VR or flat, PC or Quest), every player keeps all of the VR features in a game: physical
melee (punch, spin kick, uppercut), aiming yellow eco with your hand, first person, the body, finger tracking, physical
crouch. Each game works them out itself and the game shares what came of them (an enemy beaten, a crate broken), so
nothing has to be allowed by the host.

A player in VR in first person shows as he moves: his Jak's head turns with his headset and his arms and hands go
where his controllers are, in everyone's game of this release, in VR or not. In third person, or with VR off, his Jak
moves as the game animates him.

## In the game: the VR menu's Multiplayer

Open the VR menu (Options, VR Options, or pause and hold the left menu button a second): its **Multiplayer** rows are

| Row | What it does |
| --- | --- |
| Multiplayer | The mod on or off. |
| Your game | Whether you are hosting or in a game, and how many are in it; what is happening with a save. |
| Host a game | Opens **Host which save?** |
| Join a game | Opens the list of games. |
| Play which save? | Opens **Play which save?** (shown by itself when you join a game). |
| Players | Who is in the game; the host can take a player out of it. |
| Leave the game | Leaves the game you are in, or stops hosting yours. |

### Host which save?

| Row | What it does |
| --- | --- |
| This game as it is | Hosts now, playing on from where you are. |
| Save 1 to Save 4 | Loads that save (its level and power cells shown), then hosts. |
| New game | Hosts a new game, saved nowhere until you save it from the pause menu. |

The game is hosted on your network, or on the party server if the launcher names one.

### Join a game

Every game found on your network and on the party server, with its host's name, level and power cells; the list keeps
itself up to date. A joins the chosen one, and **Play which save?** comes up.

### Play which save?

| Row | What it does |
| --- | --- |
| The host's progress | The host's game sends its progress (power cells, orbs, scout flies, tasks, as far as it has got) and yours becomes the same. It goes into your game, not onto a save slot: your own saves stay as they are, and auto-save is off until you save from the pause menu. |
| This game as it is | Plays on from where you are. |
| Save 1 to Save 4 | Loads one of your own saves. |
| New game | A new game, saved nowhere until you save it from the pause menu. |

What one player takes from then on, everyone has. What another player takes in a level you have not loaded is taken
there too when you get there.

### Players

Everyone in the game, the host first. The host's game shows **Remove** beside the others: A takes that player out of
the game (they can join again).

## On the same network

Nothing to set up: a hosted game says so on the network and the others' **Join a game** lists it. The games use TCP
port 8120 and UDP port 8121; a firewall that asks about gk (JakDaxterVR's game) the first time you host or look for
games should allow it on private networks.

## Over the internet

Two ways:

- **A party server.** Someone runs `party_server.py` (in the PC release's `party-server` folder and the source's
  `tools` folder; Python 3.8 or later, on any PC or server) and opens TCP port 8120 on that machine to the internet:

  ```bash
  python party_server.py --port 8120
  ```

  Everyone types that machine's address in the launcher's **Party server** field (`address`, or `address:port` for
  another port). The games hosted there show in everyone's **Join a game**, with their level and power cells, and
  nobody else has to open a port. The server keeps nothing on disk and logs only counts.

- **Directly.** The host's router lets TCP port 8120 in to the host's PC or headset; the others type the host's
  address in **Party server**, and the host's game shows in their **Join a game**.

## The protocol

Newline-delimited JSON, one object a line, the same between the games on a network (the host's game is the relay)
and through a party server. `tools/party_server.py` describes every message. Protocol 2 (this release) adds the
host's level and power cells in the list, messages to one player (the host's progress, in parts of 36 KB), and the
host removing a player; a game or server on protocol 1 is told to update.

## Teamruns' own app

gk still takes Teamruns' `--socketport` for its app, as Teamruns' own build does. This port does not use Teamruns'
online service: its games go through the hosts and party servers above.
