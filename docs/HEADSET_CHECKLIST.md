# Headset checklist

What the PC tests cannot reach, to try on the Quest (and the PC VR items on a real PC headset). Each line: what to do,
and what shows it worked. The launcher's **Report a problem** makes one zip of the logs in Download/JakQuestVR on the
headset (PC: the Logs tab's **Gather the logs**, in Downloads\JakQuestVR). What the simulated headset already showed
is in [LIST-0.1.0.md](LIST-0.1.0.md) and not repeated here.

## First, every session

- **The controllers.** Walk with the left stick, turn with the right. The log's `[openxr] system:` and
  `[openxr] controller bindings suggested for` lines name the headset and its controllers.
- **The view.** A new game in each of the three games: in play you see through Jak's eyes, his head and Daxter out of
  the way; the log says `[vr] view in play: first person`. Hold the left menu button a second: third person
  (`[vr-pad] the view is third person now`), held again back.
- **Frames.** A `[vr-perf]` line every 10 seconds: frames a second near the refresh rate. Note them for each game.

## Installing (list item 8)

- Add each disc image in the launcher. The headset builds the game from it (decompile, then compile); note how long
  each game takes and what the launcher's storage line says before and after. Jak 3 needed 7.3 GB on a phone
  (Precursor's issue 18). If it stops, the **Log** tab's installation log says where.
- The game panel shows the disc's version (US, PAL, Japanese, Korean) once added.

## The three games on the headset's chip (list items 5 to 7)

- **Jak 1 pickups (ARM64):** Precursor's players reported pickups that did nothing on phones: the power cell on
  Sentinel Beach, green eco, some orbs. Collect each: the counter should go up and the pickup go away.
- **Jak II crashes during play** (Precursor's README, issue 12): play from the fortress into Haven City and on; note
  where it stops, with the logs.
- **Jak 3's white screen after its first scene** (Precursor's README): a new game; after the opening scene the desert
  should draw.

## Performance options (launcher's Graphics tab, as Ratchet & Clank Quest VR's)

- **Smoother frames (AppSW)**, on: `[openxr] smoother frames (Application SpaceWarp): on, motion ...`. Turn your head
  and run: no tearing at edges. Off: `[openxr] smoother frames: off`.
- **Foveated rendering**: `[openxr] foveated rendering:` names the level. The edges of the view a little softer at
  High, sharp at Off.
- **CPU and GPU boost**: `[openxr] performance levels: CPU and GPU ...`.
- **Refresh rate** and **Eye resolution**: `[openxr] refresh rate ...` and the eye size in `[openxr] eye ...`.
- **Max draw distance**, on: across Sandover, Haven City and Jak 3's desert nothing should pop in near you. Off for
  comparison, with the `[vr-perf]` lines of both.

## Hands, body and haptics

- **The d-pad**: the left thumb resting on its rest makes the right stick the d-pad (Jak 1's warp gate, Jak II and 3's
  gun choice). Controllers without a thumb rest sensor: the left hand by the head instead.
- **Fingers**: touch and lift the buttons, trigger and grip: Jak's fingers curl and point with yours.
- **Hands at the world**: put a hand into a wall or the floor: it stops there with a short buzz.
- **Melee** (Fists on the right grip): hold the right grip and punch (`[vr-pad] punch: ...`), sweep across for the spin
  kick (`[vr-pad] spin kick: ...`), crouch for real and punch up for the uppercut (`[vr-pad] uppercut: ...`). Jak 1
  with yellow eco: the shot leaves the punching hand.
- **Jak II and 3's gun**: grip at the hip or over the shoulder draws it (`[vr-pad] holster: ...`), let go there puts it
  away; it aims where the hand points; the left hand on it steadies it. Each gun's shot kicks the hand.
- **Body shown**: try each choice in the VR menu (Whole body to Hands only): what you see of Jak changes, nothing else.
- **Vibration**: the game's own rumble (a hit, a Zoomer crash) and the VR actions' buzzes, at Vibration strength 100
  and 50.

## Multiplayer (Jak 1)

- Two Quests on one Wi-Fi: one hosts (VR menu, Multiplayer, Host a game, a save), the other sees it under **Join a
  game** with its level and power cells, and joins. Each sees the other's Jak, with their VR head and hands. A Quest
  and a PC on the same network the same way.
- **Play which save?** on the guest: the host's progress (`party: the host's progress came (... bytes)`), then the
  guest plays at the host's place.
- The host's **Players** page: Remove takes the guest out (the guest's menu says the host took them out).
- Over the internet: a PC runs `party_server.py` (docs/MULTIPLAYER.md), its address in both launchers' Mods section;
  host and join there as above.
- **Multiplayer: Off** in the launcher: nothing of it in the game, and the VR menu's rows say "Turn it on first".

## Mods (Quest)

- **Texture packs**: a game's options page, Mods, **Add a texture pack (.zip)** (an OpenGOAL launcher pack), then
  **Prepare again** and the disc image: the game's pictures changed. Turn it off and prepare again: the game's own.
- **Mod packages** (Precursor's): import one, enable it with the game closed, play, disable it.

## The launcher

- **Check for updates now** on a later release: the launcher updates, and the games start after it with their set-up,
  saves and options kept.
- What's new, the Controls section for your controllers, and the link to Ratchet & Clank Quest VR in the corner.
