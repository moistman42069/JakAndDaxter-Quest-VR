# Headset checklist

What the PC tests cannot reach, to try on the Quest (and the PC VR items on a real PC headset). Each line: what to do,
and what shows it worked. The launcher's **Settings**, **Log**, **Save log** saves one text file with everything (from
0.1.4: each game's options, last crash and newest logs, the preparation's and the app's own log), and **Report a
problem** one zip of the logs in Download/JakQuestVR on the headset (PC: the Logs tab's **Gather the logs**, in
Downloads\JakQuestVR). What the simulated headset already showed
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

## 0.1.6's changes (the owner's Quest 3 logs and players' reports on 0.1.5)

- **Light Jak** (Jak 3, once the game has given him to you): hold the **left grip** and press **Y**: Jak stops for his
  power (`[vr-pad] the left grip held and Y: L2 held`). Let **Y** go: he heals if hurt (`Light Jak by the left grip:
  Y let go, Triangle (Regeneration)`); or slide the thumb to **X** (time slows), press **A** (the wings) or **B** (the
  shield), or push the right stick. Y tapped and everything let go at once still heals. The grip let go before a
  choice with Y held: Dark Jak, and no invisibility from the Y still held. Dark Jak's quick squeeze of the left grip
  as before; as Dark Jak, Y with the grip held is his invisibility.
- **Defaults** (a game with no options saved, or one an older release wrote): `[vr] options: ...` with
  `cpu-gpu-boost-on on` (the Quest too: `[openxr] performance levels: CPU and GPU boost`), `extra-vibration-kind 2`
  (Off kept where it was Off) and `crosshair-shape 1` (the Dot, Jak II and 3; a kind picked in 0.1.5 kept). A **Quest 3 or 3S**: `eye-resolution 150` (`[openxr] eye 0: 2520 x 2640 (recommended 1680 x 1760)` on
  a Quest 3), `sharpening-strength 2`; a **Quest 2** or Pro: `eye-resolution 100`, `sharpening-strength 1`; every
  headset and the PC: `anti-aliasing 1` and `anisotropic-filtering 1` (both Off). A choice made in 0.1.5
  other than its start is kept. Say how the Quest 3 runs at 150%: Dynamic resolution's lines (`the eyes at N%`) show
  how often it draws smaller.
- **Smoother frames** at 120 Hz (Quest): `[vr-view] the game at 60 frames a second (the headset shows 60)`, not 30;
  no black flicker. **Dynamic resolution**: `[vr-perf] dynamic resolution: the eyes at ...` lines far fewer than
  0.1.5's one every five seconds.
- **The game's thread** (Quest): `[openxr] thread ... (EE) given to the headset as the app's main thread` with no
  `refused`; `[openxr] the display's thread given to the headset as the renderer`.
- **Leaving and coming back** (Quest): open the Quest's menu or take the headset off for a while, then come back: the
  game is where you left it, not closed (`[vr] the headset's session stopped: nothing drawn, the game held until it
  runs again`, then `... runs again: the game goes on`).
- **Jak II's scenes**: no black at a scene's start beyond the game's own (`[vr-view] a blackout kept in the eyes,
  black: N frames` a fraction of a second, not 0.1.5's 4.9 s).
- **Jak II's pause** (PC, SteamVR): pause after turning around by the stick or the head: the menu ahead of you
  (`[vr-view] paused: the menu on the HUD, following the head ...`).
- **Jak 1 at Geyser Rock's scarecrows** (where a Quest 3 closed): break them all. A `[vr-guard]` line in the log means
  the game lost one of its types and it was put back: please send that log.
- **Multiplayer on Jak 1's title and pause menu**: **Multiplayer** (under Load Game on the title, under VR Options in
  the pause menu's Options) opens the VR menu on its Multiplayer page (`[vr-menu] open Multiplayer (the title's
  Multiplayer)`); **Host a game** at the title offers your saves and a new game, no "This game as it is"; **B** goes
  back a page and closes it from Multiplayer, the game's menu still there. New Game and Load Game as before.
- **One player with multiplayer on** (Jak 1 and Jak II, as they start): play as you would alone. Nothing should
  differ from the original game (Jak 1: the swamp's tether rocks let Jak go, Load Game on an empty slot does
  nothing; Jak II: the pause holds the game). After leaving a Jak 1 multiplayer game, the others' Jaks are gone and
  the Zoomer has its own speed.
- **Jak 1's Boggy Swamp tether rocks**: break one with yellow eco (not the last): after its scene Jak walks and jumps
  at once (0.1.5 held him in place after any of the first three). The last rock's scene shows the Precursor arm.
- **Jak 1's ledges** (0.1.6: none of a level's was grabbed before): jump at a ledge, Jak hangs from it and climbs
  up; the climb to Klaww in Rock Village needs one (a high jump: crouch, then jump). With Jak 1's multiplayer, the
  same in a hosted or joined game. While he hangs, first person puts your eyes where his head is, just under the
  ledge's top (its grass seen from under it): say how that feels.
- **Jak II from a new game** (the prison, with its warp gate): the escape plays on into the city; Jak II's other
  warp gates, airlocks, races and the stadium's JET-Board lessons each work as before.
- **Jak 1's opening, the shot of Jak lying back on the boat** (Quest, Dynamic resolution on): at the cut to that shot
  the eyes may get smaller at once (`[vr-perf] dynamic resolution: the eyes at ...% (frames were shown late)`, several
  within a second), and the shot plays without a stutter of more than a moment. Tell us if it still stutters, with
  your Graphics settings.
- **Jak 1's crouch**: crouch by your body or with the left trigger: Jak breathes in and out about once a second, as
  the game's crouch does with the button. That is the game's own sound.
- **Jak 3's JET-Board**: on the board, hold **B** near a rail to grind it, and **X** zaps; A jumps. Off the board X and
  B are as before, and Jak II's board is unchanged (X Square, B Circle).
- **Controllers back** (PC: open and close the SteamVR dashboard; Quest: the Meta button's menu, or the headset off and
  on): hold A while you come back. Jak doesn't jump until you press A again (`[openxr] focus lost`, `[openxr] focus
  back`, `[openxr] focus back with buttons held: ...`).
- **Jak 3's scenes**: talk to Torn in the Hip Hog (or any scene started with Y): the scene plays; Y in its first second
  doesn't skip it, Y after that does (`[vr] the scene ... skipped: Triangle`).
- **Jak II after a mission's load** (the strip mine, the city's missions): the headset comes out of black with the
  game, not seconds later.

## 0.1.5's fixes (a tester's reports on 0.1.4: PC, SteamVR with a Quest over Link)

- **The gun's laser** (Jak II and 3, SteamVR): turn on a crosshair, draw the gun and point it around: at the end of
  the laser only the crosshair, never a small picture of the game or of the HUD. The same with **Show performance**
  on: only its text.
- **The crosshair**: off to start (a game with no options saved yet, or one whose 0.1.3 or 0.1.4 file had the Dot:
  the log's `[vr] options: ... crosshair-kind 0`). **Small circle**: a thin ring, about half a degree across at 100%,
  sharp at any distance; red on an enemy the gun can reach.
- **Game frame rate: Automatic** on SteamVR: the log's `[vr-view] the game at ...` lines no more than a few in a run
  (0.1.4 gave up to 26 changes a second), and Jak 1's `Tried to set unsupported video-mode` gone. A change of the
  headset's own rate shows as `[vr-view] the headset at N a second for 1.0 s: the game follows it`.
- **Jak 1's temple** (Forbidden Jungle, inside): where 0.1.4 froze (`too many sound commands queued`, then `STALL:
  waiting for IOP on RPC port #0`), the game goes on. If the sound stops answering, the log says `the IOP has not
  answered the sound player for over a quarter of a second` (a sound or two missed) and later `the IOP answers the
  sound player again`; say whether the sound came back.
- **Lag spikes** (Jak 1's plant boss, going in and out): say whether they still happen. A frame of over a second
  writes `[vr-stall] the game has not finished a frame for 1.0 s: it is ...` with where the display and the sound
  processor's threads were; send that log.
- **Jak 3's leaper race restarted** several times from the pause menu: say whether it still closes. If the PC game
  closes on its own, its log ends with `[crash] the game crashed: ...` lines; send that log.
- **No flat screen** at the game's start, between scenes, after a mission failed or on a long load: black in the
  headset, then the game around you again (the log's `[vr-view] a blackout kept in the eyes, black: N frames (S s)`
  for each). A scene put **On a screen** in the VR menu's Cutscenes still shows on the screen.
- **B in the pause menu**: Jak 1's pause opens on the power cell page; **B** opens the Options (its "Circle Options"),
  **B** again goes back, **Y** shows the scout flies. In the Options and save menus **B** goes back. Jak II's lobby
  list: **B** opens Manual (Join with Invite, Direct Connect). Turn round before pausing: the menu is in front of you.
- **Dark Jak's button** (Jak II and 3): a quick squeeze of the left grip (or the left stick's click) makes Dark Jak
  (`[vr-pad] a quick squeeze of the left grip: L2`; a quick click, `[vr-pad] a quick click of the left stick: L2 held
  0.30 s`), once his meter has some dark eco. Jak 3: squeeze, let go and squeeze again and hold, then the right
  stick or A, B, X, Y for Light Jak's powers. On SteamVR with Touch (the left click is the menu button) the grip
  works the same.
- **The sky** (Jak 3's desert, Jak II's city), in first person: the farthest hills whole against the sky, clouds and
  the haze behind them, nothing flickering at their edges as you turn your head.
- **First person for each ride**: the VR menu's Body, with First person in vehicles on: turn one ride's switch off
  (the JET-Board, say) and ride it: third person on it, first person on the others.
- **Jak II alone, multiplayer on** (as it starts): the burning bushes offer their missions, the Baron's squid boss
  shoots at you and its ending scene plays, the tomb's widow throws her bombs and her ending plays, and after a
  failed mission walking back to it starts its scene and the mission again.
- **Effects in Jak II and 3**: Dark Jak's lightning and change, the guns' impacts, Jak 3's red guns' shockwave: no
  colored or black square in front of your face; Dark Jak's darkening dims the HUD only.

## Performance options (launcher's Graphics tab, as Ratchet & Clank Quest VR's)

- **0.1.4's Quest start** (a tested Quest 3's Graphics): a game with no options saved yet starts at 120 Hz (`[openxr]
  refresh rate 120 Hz`, or the most a headset offers up to it), Smoother frames on, High foveation, `[openxr]
  performance levels: CPU and GPU sustained high`, anti-aliasing and anisotropic filtering off, `[openxr] sharpening:
  normal`. On a Quest 2 or 3S, note the `[vr-perf]` lines with it, and say whether Balanced or Performance plays better.
- **Smoother frames (AppSW)**, off to start from 0.1.1 to 0.1.3 (the title screen flickered with it), on to start on the
  Quest from 0.1.4: `[openxr] smoother frames (Application SpaceWarp): on, motion ...`; turn your head and run,
  and say whether anything flickers or tears. 0.1.1 changed how it is given (the PC cannot show it: this is the check
  that matters most). The log should say `[vr-view] the game at 36 frames a second (the headset shows 72)`, then
  `[vr-view] smoother frames: the headset makes the frames between from here` once in play and `none made here` on
  the title, in a menu and through a load. Look for: the title and the menus steady (none there); no flicker as a
  scene cuts or the game hitches; the game at its right speed (0.1.0 ran it slow with this on); things that move by
  themselves (enemies, Jak in third person) may still show a double edge, since only the view's own motion is given.
- **Game frame rate** (0.1.1, 60 to start): **Half the headset's**: `the game at 36 frames a second`, half the work,
  the head still turns smoothly, moving things step a little; **The headset's**: `the game at 72 frames a second`
  (90 or 120 at those refresh rates), the smoothest. In each, Jak runs and jumps as far as at 60, and scenes keep
  their sound in step.
- **Dynamic resolution** (0.1.1, off to start): in a heavy place (Haven City, Spargus) `[vr-perf] dynamic resolution:
  the eyes at 90% (frames were shown late)` and lower, down to 60%, and back up a step every few seconds where it is
  light. Say whether the changes of sharpness are seen, and whether it ever goes up and down over and over.
- **PS2 detail** (0.1.1, off to start): far-off things and effects drawn as the PS2 drew them; note the `[vr-perf]`
  frames with it on and off in the same place, and how much pops in.
- **Sharpening** (0.1.1, off to start; next start): `[openxr] sharpening: normal` or `strong`; with a lower Eye
  resolution the edges should stay crisp. If the log says the headset does not offer it, nothing changes.
- **Foveated rendering**: `[openxr] foveated rendering:` names the level. The edges of the view a little softer at
  High, sharp at Off; **Highest** (0.1.1) softer again, for the most frames.
- **CPU and GPU boost**: `[openxr] performance levels: CPU and GPU ...`.
- **Refresh rate** and **Eye resolution**: `[openxr] refresh rate ...` and the eye size in `[openxr] eye ...`.
- **Max draw distance**, on: across Sandover, Haven City and Jak 3's desert nothing should pop in near you. Off for
  comparison, with the `[vr-perf]` lines of both.

## Hands, body and haptics

- **The d-pad**: the left thumb resting on its rest makes the right stick the d-pad (Jak 1's warp gate, Jak II and 3's
  gun choice). Controllers without a thumb rest sensor: the left hand by the head instead. Since 0.1.1 each direction
  pulses in that hand (`[vr-pad] d-pad up (the right stick)` and so on), the thumb rest a light one as it takes the
  stick, and Jak II and 3's gun change pulses in the gun's hand (`[vr-gun] the gun switched`): say whether each is
  felt, and whether the d-pad ever fails to go in. Off with **Buzz on d-pad presses** and **Buzz on gun switch**.
- **Select and L3** (0.1.5): the left hand held by the head, click the left stick (`[vr-pad] Select: the left stick's
  click with the left hand by the head`; Jak 3 pauses without the menu, Jak II multiplayer shows the player list) and
  the right (`L3: the right stick's click ...`; Jak II multiplayer, the names and HUD while held). On foot in Jak II
  and 3 the same clicks with the hand down stay Dark Jak and the JET-Board. Vive wands: the left touchpad's click in
  its middle, the hand by the head (`Select: the left touchpad's click ...`); on a direction, that direction only.
- **Extra vibration** (0.1.4; the VR menu's Controls, the launchers' VR Controls). **Pickups** (to start): collect an
  orb, eco, a power cell or a scout fly (Jak II and 3: ammo, a gun, a skull gem): a light tick, a longer buzz, a
  strong one. **The whole world**: walk and run (each step in that foot's hand, firmer running), jump, land, roll,
  punch and kick, hit an enemy or a crate (the punching hand), and stand near an explosion or a boss's stomp (a shake
  by how big). The log names the first of each kind in a run: `[vr-feel] small pickup (6 0; Extra vibration:
  Pickups)` (the number is the game's pickup type), `eco`, `big pickup`, `step`, `jump`, `landing`, `roll`, `attack`,
  `hit`, `shake (<cm> <ticks>; ...)`. Say whether any is too strong, too weak or too often (a step on every frame, a
  buzz that never stops), and that **Off** feels as 0.1.3, **Vibration off** stops them all and **Vibration strength**
  scales them. In multiplayer the other players' steps and pickups should not buzz your hands.
- **The menu pointer** (0.1.1): Jak 1's save and load files and its yes / no questions, each game's On / Off rows and
  Yes / No questions (point at a side, then the trigger), Jak 1's sliders, and a long list (Jak 1's options, Jak II and
  3's game options) scrolling while the dot rests on its top or bottom row.
- **The stick in Jak 1's menus** (0.1.1): on the title, the left stick down to Load Game and A: the load screen, not a
  new game (`[vr-pad] d-pad down (a stick in Jak 1's menu)`), with the hand pointing at the menu or not. The same with
  the stick in Jak II and 3's menus (they read it themselves).
- **A scene's view** (0.1.1): hold the menu button a second in a scene: through Jak's eyes, then around you, and back
  (`[vr-pad] scenes are seen ...`).
- **Spin kick by spreading your arms** (off to start, 0.1.1): turn it on, hold both arms out as a T: one spin kick.
- **First person in vehicles** (on to start since 0.1.3): get on the Zoomer, the board or in a car: the view is
  from Jak's seat and turns with the vehicle. Off: third person there. Vehicle view height, forward and tilt (0.1.3)
  move and tip that view; the hands and the gun stay where yours are.
- **Fingers**: touch and lift the buttons, trigger and grip: Jak's fingers curl and point with yours.
- **Hands at the world**: put a hand into a wall or the floor: it stops there with a short buzz. 0.1.1: lay the palm
  on a wall or the ground and the fingers settle on to it; point the fingers into it and they fold; draw the hand
  back and they open again, each finger by itself and without a snap.
- **Jak 1's wrist straps** (0.1.1): the loose ends at his wrists hang down whichever way the hand is turned, swing as
  you shake it and come to rest, and lie on the ground or a wall instead of going through.
- **Walking straight** (0.1.1): push the left stick forward: Jak goes where you look, with the head level, looking
  down and tilted. A thumb a little off straight goes that little off, not a wide diagonal.
- **Adaptive cutscenes** (0.1.1, on to start): Jak 1's opening is around you until Jak stands at the silo, then through
  his eyes (`[vr-cam] adaptive cutscenes: ...`); a scene never flips back and forth. Off: every scene as Cutscene view
  says.
- **A roll** (the left trigger while running): say how it looks and feels in first person.
- **Melee** (Fists on the right grip): hold the right grip and punch (`[vr-pad] punch: ...`), sweep across for the spin
  kick (`[vr-pad] spin kick: ...`), crouch for real and punch up for the uppercut (`[vr-pad] uppercut: ...`). Jak 1
  with yellow eco: the shot leaves the punching hand. 0.1.1: the left grip makes the left fist in Jak II and 3 too
  (`[vr-pad] fists up: the left grip`), and before Jak has a gun a fist made low at the hip is a fist, not a holster.
- **First person in vehicles**, also on Jak 1's and II's slides and at the turrets (0.1.1, with the switch on).
- **Jak II and 3's gun**: grip at the hip or over the shoulder draws it (`[vr-pad] holster: ...`), let go there puts it
  away; it aims where the hand points; the left hand on it steadies it. Each gun's shot kicks the hand. The VR menu's
  **Holster size** (130% to start since 0.1.1): at 100% the places of 0.1.0, at 200% easy to find, at 50% only right
  at the hip or behind the shoulder; say whether 130% draws the gun when you did not mean it (a fist low at your side).
- **Body shown**: try each choice in the VR menu (Whole body to Hands only): what you see of Jak changes, nothing else.
- **Arms by the face** (0.1.1): bring a hand up close in front of your eyes, then over your head and across your face:
  Jak's hand bends at the wrist and his forearm comes up from below (or beside the head), never through your eyes, and
  his arm never vanishes. Move fast and roll the wrist: the elbow follows without flicking side to side. **Hide arms
  near your eyes** (off to start) fades out an arm that still comes within 16 cm of the eyes.
- **Life size** (0.1.1): World scale 100% (the new start): Jak is as tall as you, his hands and the gun a person's
  size, the world as big as a real one around you. 145% is how 0.1.0 looked (you a giant, the hands twice as big):
  say which feels right, and whether 100% makes jumps and ledges read well.
- **His body under you** (0.1.1, **Keep your body upright** and **Fade body near your eyes**, both on to start): punch,
  spin kick, uppercut (button and for real), jump, high jump, long jump, dive, roll, crouch. Look down in each: his
  body stays upright under your head, nothing of him in front of your eyes; a leg kicked up by your face, and his body
  in a roll, a flip or a dive, fades out and back smoothly, never a pop (`[vr-body] ... fades out of the eyes` /
  `comes back to the eyes`). Crouched (the button, and crouching for real), look down: his knees in front of you over
  his feet and his body leaning forward as a person's in a squat, never his legs under the ground or his belt in your
  face. Turn your head without moving: his hips stay, then follow past about 35 degrees. Run and
  turn: his hips face where he runs, but never more than about 60 degrees from where you look. Each switch off: as
  0.1.0 (the body as the game's animation has it; nothing faded).
- **Jak 1's look-around** (Triangle in play): the view stays where Jak is (not ahead of him), his body still; Triangle
  again leaves it.
- **Jak 1's Lost Precursor City** (0.1.1): Rock Village's elevator down (the ride's scene, then the city drawn round
  you at the bottom), the blue eco launchers in the helix room (Sunken B: no crash on entering), the floor buttons that
  raise the square platforms (dive on them: the scene, then play), the pipe game's three buttons at the back of the
  room (dive on one: the pipes' scene, then Jak free to move), the helix button and the room's way out. A crash names
  the process in the log (`kmalloc: !alloc mem in heap for #<process ...>`).
- **Jak 1's scenes through his eyes** (0.1.1): the opening and a talk to the Sage: you face where Jak faces in the scene,
  what happens in front of you.
- **Jak 3's gun magazine** (0.1.1): draw the gun: its ammo stays on the gun as you move it.
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

## Mods (Quest; the PC launcher's Mods tab the same)

- The Mods section starts by saying what works here (Multiplayer, Quest mod packages, texture packs on their own tab)
  and that OpenGOAL's own mods are flat screen only.
- **Browse mods**: both lists read (this project's and jakmods.dev's), "Read at ..." under them; Jak 1 / Jak II / Jak 3
  each show their mods (no texture packs: those are on Texture packs); **Show them** lists OpenGOAL's own mods as Flat
  screen only, each with **Its page**. Without a network: a plain line saying a list could not be read, and the copy
  from before when there is one.
- **Mod packages** (Precursor's): import one, enable it with the game closed, play, disable it. A texture pack's .zip
  given to **Import a mod**: "It is a texture pack: imported to Texture packs".

## Texture packs (Quest: a game's options page, Texture packs; the PC launcher's Texture packs tab the same)

- The tab is beside Mods, between Controls and Mods, and its name reads whole.
- **Import a .zip**: a pack downloaded in the headset's browser (Downloads), an OpenGOAL launcher pack. It shows in the
  game's list, **Imported**, on. Then **Prepare again** and the disc image: the game's pictures changed. Turn it off and
  prepare again: the game's own.
- **Import a folder**: a pack's folder copied to the headset (the folder picker asks to allow it): the same.
- A Jak 1 pack imported from Jak II's page: the message says "for Jak 1 (on that game's Texture packs)", and it is in
  Jak 1's list, not Jak II's.
- A .7z, a PCSX2 pack, a zip of photos: a plain message, nothing added.
- **Find texture packs**: the lists' packs; **Add** one: it shows in the list, **From a mod list**, and the button says
  **Added**. A newer version in the list: the pack's card and the list both offer **Update to ...**, and it keeps its
  place and its On / Off.

## The launcher

- **Check for updates now** on a later release: the launcher updates, and the games start after it with their set-up,
  saves and options kept.
- What's new, the Controls section for your controllers, and the link to Ratchet & Clank Quest VR in the corner.
