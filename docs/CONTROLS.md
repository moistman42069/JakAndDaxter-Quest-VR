# Controls

The same in all three games, on the Quest and on PC VR (jak_vr.cpp `fill_pad`). The controllers stand in for the
game's own pad, so every button of the game is there; the VR motions add to them. Every VR motion has its own row in
the VR menu to turn it off.

## On foot

| Controller | Game button | What it does |
|---|---|---|
| Left stick | left stick | Move. Where you look, to start; the VR menu's Move direction can make it where your left hand points. |
| Right stick | right stick | First person: turn (smooth to start, 120 degrees a second with the stick full over; or snap, 45 degrees). Third person: the game's camera. |
| A | Cross | Jump. |
| B | Circle | Spin kick. |
| X | Square | Punch. |
| Y | Triangle | Triangle. |
| Right trigger | R1 | Jak II and 3: draw and fire the gun. |
| Left trigger | L1 | Crouch, roll; in the air, the dive. |
| Right grip | (fists) | With Physical melee and Fists on the right grip (both on to start): hold it to make fists. Only fists punch and kick (below). While the gun is in your hand, the grip just holds it. |
| Left grip | L2 | Jak II: Dark Jak. Jak 3: Dark and Light Jak. On the front of the drawn gun: the two-hand grip. Jak 1: fists, like the right grip (the game gives L2 nothing). |
| Right stick click | R2 | With Fists on the right grip: the grip's old button. Jak II and 3: the JET-Board on and off; Jak 3: Light Jak's end. With it off: R3. |
| Left stick click | L3 | L3. |
| Both stick clicks together | none | Recentre the view. |
| Menu button | Start | Pause. Held one second: switch first and third person. |
| Left thumb on its thumb rest | D-pad | The right stick is the d-pad and its click Select (Jak II and 3: pick a gun; Jak 1: the warp gates and totals). The VR menu's other choice: the left hand held by your head makes the left stick the d-pad. |

### Motions

| Motion | Game button | Notes |
|---|---|---|
| A fist thrown forward fast | Square | His punch. In Jak 1, yellow eco leaves that hand where it points. |
| A fist swept across fast | Circle | His spin kick. |
| Crouched, a fist thrown up fast | Square from the crouch | His uppercut. |
| Your head down (30 cm, to start) | L1 | Physical crouch, on the ground only. |
| Right or left grip squeezed at the hip or over the shoulder | (the gun) | Jak II and 3: draws the gun, without the shot R1's draw fires. Bring it back there and let go (or squeeze and let go there) to put it away. "Draw from hip or shoulder" in the VR menu turns this off. |

Fists: with Fists on the right grip on, the hands hit only while a grip makes fists (a light buzz as they close).
Turn it off in the VR menu and every fast hand motion hits, as before. Melee speed sets how fast a hit must be
(2.5 m/s to start).

## On the JET-Board, in vehicles, in the Titan suit, on rails and poles

The same, except the right grip is the game's R2 again (Jak II's car: up and down a height; the board: off), and the
right stick click is R3. Fists are not made there.

## Menus

The game's own menus, as on a pad: the sticks move through them, A (Cross) picks, Y (Triangle) goes back. Or point at
them: either hand's dot shows on the menu, the row under it is chosen, and that hand's trigger picks it (the right
hand points until the left trigger is pulled on the menu; Menu pointer hand in the VR menu).

The VR menu: hold the left menu button over a menu, or pick VR Options in the game's Options (Jak II and 3: Options,
Game Options, VR Options; Jak 1: Options, VR Options). Point at a row and pull the trigger to change it, or choose
with the stick up and down and change it with A or the stick left and right; B closes it.

## PC VR controllers

Every controller family OpenXR names has its buttons set up; SteamVR's own
controller binding page can change any of them. Where a controller lacks a button, the nearest it has:

| Controller | Differences from the tables above |
|---|---|
| Quest Touch, Touch Plus, Touch Pro (Link, Air Link, Virtual Desktop, SteamVR), Vive Focus 3 | None. |
| Pico 4 and Neo 3, Vive Cosmos, HP Reverb G2 | No thumb rest: the d-pad is the left hand held by your head (below). |
| Valve Index | Its A and B are A and B on the right hand, X and Y on the left. A firm squeeze of the left touchpad is the menu button (Start). No thumb rest: the d-pad is the left hand held by your head. |
| Windows Mixed Reality (and the Samsung Odyssey) | Sticks as above. Right touchpad click: A; right menu button: B. Left touchpad click: X, or Y on its upper half; left menu button: Start. The d-pad is the left hand held by your head. |
| Vive wand | The touchpads are the sticks. Right touchpad click: A, or B on its upper half; right menu button: the right stick click (the JET-Board with fists on the grip). Left touchpad click: X, or Y on its upper half; left menu button: Start. No left stick click (L3, which none of the games needs). The d-pad is the left hand held by your head. |

Any controller without a thumb rest gets the "Left hand at your head" d-pad whatever the VR menu says, and the log says
so on its first input line (`[openxr] left hand: ... thumb rest no`). A controller without touch sensors has the finger
on the trigger when the trigger is pressed and the thumb resting on the controller.
