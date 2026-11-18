# Multiplayer (Jak 1, and Jak II)

Jak 1 can be played together: the others' Jaks run around your world with their names over them, and what one
player takes (power cells, scout flies, orbs, crates, beaten enemies and tasks) everyone has. Up to 8 players a game.
Every Jak is in his own colors; the name over him says whose he is.

It is [Teamruns](https://github.com/JoKronk/teamruns-jak-project)' multiplayer, built in as a mod; the games you host
and join, the list of games and the saves are this port's own, laid out as Ratchet and Clank Quest VR's Multiplayer.
Jak II has its own, built on the community's Jak II multiplayer mod: see [Jak II](#jak-ii) at the end. Jak 3 has none.

## On to start, and changes nothing until you play together

Multiplayer is on when the game is installed. Until you host or join a game it changes nothing in Jak 1: the game,
its cutscenes and its saves are as they always were, and it opens no network port. Only when you host, join or look
for games does the game go on the network, and only in a game are the others' Jaks shown, the in-game cutscenes
skipped (so nobody waits on another's) and the taunts on the d-pad. (Since 0.1.5 also its checkpoint hook and its
crate rule, which ran with the mod merely on.)

To keep it out of the game altogether, turn it off:

- **PC:** the launcher's **Mods** tab, **Jak 1 multiplayer: Off**.
- **Quest:** Jak 1's options page in the launcher, **Mods**, **Jak 1 multiplayer: Off**.
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

**Friends without a headset** play on PC with this port's PC build: with no headset connected the game plays on the
monitor with a pad, as OpenGOAL does (its log says `[vr] no headset`), and hosts and joins like anyone else, seeing
the VR players' heads and hands. Players of Teamruns' own app or of the Jak II mod's own build cannot join this port's
games, nor this port theirs: Teamruns' app goes through Teamruns' online service and the Jak II mod through Steam's
networking, and neither carries the VR players' head and hands.

## In the game: Multiplayer

Choose **Multiplayer** on the title screen (under Load Game) or in the pause menu (under VR Options). Its rows are
also in the VR menu (Options, VR Options, or pause and hold the left menu button a second), under **Multiplayer**.
At the title there is no game yet, so a game is hosted or played from a save or a new game. The rows are

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
| This game as it is | Hosts now, playing on from where you are (not at the title, where there is no game yet). |
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
| This game as it is | Plays on from where you are (not at the title). |
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

## Jak II

Jak II's story can be played together too, on the community's Jak II multiplayer mod (its licence is in
THIRD_PARTY_NOTICES): one player hosts a save, up to 7 others join, and their Jaks are in the host's world. In this
port it runs on plain TCP and UDP of its own (no Steam), so it plays with this port's build of the same release only,
not with the mod's own build. In VR, the others' Jaks show their VR head and hands as in Jak 1.

**On to start, and changes nothing until you play together.** The title's New Game and Load Game are the game's own,
as always: the mod opens no network until you host or join. Its makers call it a beta, so it has its own switch; turned
off, neither the title nor Options has a Multiplayer row and nothing of the mod runs at all (no network, no lobby, no
player list). Since 0.1.5 that holds in play as well: the mod lets only a game's host decide some things (a boss's aim
and its ending, the burning bushes' missions, the guards taking the Kid and Kor, some enemies' moves), and played
alone nobody was the host, so those never happened; now, out of a multiplayer game, the game decides them itself as
it always did, and a failed mission's scene plays again when you walk back to it. Turn it off if anything seems wrong
with it on:

- **PC:** the launcher's **Mods** tab, **Jak II multiplayer: Off**.
- **Quest:** Jak II's options page in the launcher, **Mods**, **Jak II multiplayer: Off**.

A change counts from the game's next start.

**To play together**, the title's **Multiplayer** has **Host Game** (then New Game or Load Game, the save everyone
plays) and **Join Game** (games on this network, or **Direct Connect** to a host's address); **Options**,
**Multiplayer** has your name, your Jak's colors and the mod's settings.

**The network.** A hosted game says so on the local network (UDP port 26211) and the others' **Join Game** lists it.
The game itself uses one port, 26210 to start (**Options**, **Multiplayer**, **Port**), for both TCP and UDP. Over the
internet the host's router has to let that port in, TCP and UDP, to the host's PC or headset; with **Port Mapping** on
(to start) the game asks the router to do it by itself, where the router allows that. The others then use **Direct
Connect** with the host's address. Jak II's games use no party server.

**Typing in VR.** A headset has no keyboard, so while one of the game's text fields is open (your name, an address, a
room code) the sticks type into it, as an arcade's name entry: up or down changes the letter before the cursor, right
keeps it and goes on to the next, left erases one. A enters the text and Y leaves the field. A held push steps on.
