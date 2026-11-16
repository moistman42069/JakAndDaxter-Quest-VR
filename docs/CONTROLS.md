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
| X | Square | Punch; in the air, the dive (it presses the floor buttons). A real punch in the air is the dive too. |
| Y | Triangle | Triangle. Jak 1: the game's look-around (Jak stands still and is not drawn; Y again leaves it). |
| Right trigger | R1 | Jak II and 3: draw and fire the gun. |
| Left trigger | L1 | Crouch, roll. |
| Right grip | (fists) | With Physical melee and Fists on the right grip (both on to start): hold it to make fists. Only fists punch and kick (below). While the gun is in your hand, the grip just holds it. |
| Left grip | L2 | Jak II: Dark Jak. Jak 3: Dark and Light Jak. On the front of the drawn gun: the two-hand grip. It makes the left fist as well (0.1.1: in all three games, so the left hand punches by its own grip; Jak 1 gives L2 nothing, so there it is the fist alone). |
| Right stick click | R2 | With Fists on the right grip: the grip's old button. Jak II and 3: the JET-Board on and off; Jak 3: Light Jak's end. With it off: R3. |
| Left stick click | L3 | L3. |
| Both stick clicks together | none | Recentre the view. |
| Menu button | Start | Pause. Held one second: switch first and third person, in play and in a scene (a scene through Jak's eyes or around you); over a menu, the VR menu. |
| Left thumb on its thumb rest | D-pad | The right stick is the d-pad and its click Select (Jak II and 3: pick a gun; Jak 1: the warp gates and totals). A quick pulse in that hand as each direction goes in, and a light one as the thumb rest makes it the d-pad (the VR menu's "Buzz on d-pad presses"). The VR menu's other choice: the left hand held by your head makes the left stick the d-pad. |

### Motions

| Motion | Game button | Notes |
|---|---|---|
| A fist thrown forward fast | Square | His punch. In Jak 1, yellow eco leaves that hand where it points. |
| A fist swept across fast | Circle | His spin kick. |
| Crouched, a fist thrown up fast | Square from the crouch | His uppercut. |
| Your head down (30 cm, to start) | L1 | Physical crouch, on the ground only. |
| Right or left grip squeezed at the hip or over the shoulder | (the gun) | Jak II and 3: draws the gun, without the shot R1's draw fires. Bring it back there and let go (or squeeze and let go there) to put it away. "Draw from hip or shoulder" in the VR menu turns this off. |

Fists: with Fists on the right grip on, the hands hit only while a grip makes fists (a light buzz as they close).
The right grip's fist lets both hands hit; the left grip's lets the left hand hit (Jak 1: both), since in Jak II and
3 the right may be aiming the gun. Turn it off in the VR menu and every fast hand motion hits, as before. Melee speed
sets how fast a hit must be (2.5 m/s to start).

The left stick goes where it is pushed, measured from where you look (or where your left hand points): a thumb a
little off straight walks that little off straight (0.1.1; 0.1.0 turned any sideways push into a wide diagonal).

## Scenes

A scene plays through Jak's eyes when the view is in first person, or around you as a film. With **Adaptive
cutscenes** (on to start) a scene through his eyes goes around you by itself while he is not properly in it (not
drawn, far from what the game's camera shows, or carried along by something), and comes back to his eyes when he is.
Holding the menu button one second in a scene switches the two yourself, and that choice lasts for the rest of that
scene.

## On the JET-Board, in vehicles, in the Titan suit, on rails and poles

The same, except the right grip is the game's R2 again (Jak II's car: up and down a height; the board: off), and the
right stick click is R3. Fists are not made there. On the JET-Board with Fists on the right grip, the right stick
click stays R2 as well, so the click that put Jak on the board takes him off again.

## Menus

The game's own menus, as on a pad: the sticks move through them (Jak 1's menus read only the d-pad, so there both
sticks are its d-pad: 0.1.1), A (Cross) picks, Y (Triangle) goes back. Or point at
them: either hand's dot shows on the menu, the row it moves onto is chosen, and that hand's trigger picks the row under
it (the right hand points until the left trigger is pulled on the menu; Menu pointer hand in the VR menu). A hand
resting still does not choose: moving through a menu with the stick takes over from the dot until the hand moves
again (0.1.1; 0.1.0's dot took the row back from the stick). On a row being changed,
the dot on the left or right half of a two-way choice (Yes / No, On / Off) chooses that side, a slider's bar follows the
dot (Jak 1), and the trigger is done with it. Held on the top or bottom row shown of a list with more past it, the dot
scrolls the list a row at a time. The save and load screens' files and the yes / no questions work the same way.

The VR menu: hold the left menu button over a menu, or pick VR Options in the game's Options (Jak II and 3: Options,
Game Options, VR Options; Jak 1: Options, VR Options). Point at a row and pull the trigger to change it, or choose
with the stick up and down and change it with A or the stick left and right; B closes it.

## PC VR controllers

Every controller family OpenXR names has its buttons set up; SteamVR's own
controller binding page can change any of them. Where a controller lacks a button, the nearest it has:

| Controller | Differences from the tables above |
|---|---|
| Quest Touch, Touch Plus, Touch Pro (Link, Air Link, Virtual Desktop's own VDXR), Vive Focus 3 | None. |
| Quest Touch through SteamVR | SteamVR keeps the left menu button for its dashboard, so the left stick's click is the menu button: a click is Start, held one second it switches first and third person, in play and in a scene (over a menu, the VR menu). No left stick click (L3) then, which none of the games needs. SteamVR has no thumb rest for them either: the d-pad is the left hand held by your head, and the left stick's click there is Select. |
| Pico 4 and Neo 3, Vive Cosmos, HP Reverb G2 | No thumb rest: the d-pad is the left hand held by your head (below). |
| Valve Index | Its A and B are A and B on the right hand, X and Y on the left. A firm squeeze of the left touchpad is the menu button (Start). No thumb rest: the d-pad is the left hand held by your head. |
| Windows Mixed Reality (and the Samsung Odyssey) | Sticks as above. Right touchpad click: A; right menu button: B. Left touchpad click: X, or Y on its upper half; left menu button: Start. The d-pad is the left hand held by your head. |
| Vive wand | The touchpads are the sticks. Right touchpad click: A, or B on its upper half; right menu button: the right stick click (the JET-Board with fists on the grip). Left touchpad click: X, or Y on its upper half; left menu button: Start. No left stick click (L3, which none of the games needs). The d-pad is the left hand held by your head. |

Any controller without a thumb rest gets the "Left hand at your head" d-pad whatever the VR menu says, and the log says
so on its first input line (`[openxr] left hand: ... thumb rest no`). The same way, any controller whose left menu button
the runtime keeps for itself has the left stick's click as the menu button (`menu button no (the left stick's click is
the menu button)`). A controller without touch sensors has the finger
on the trigger when the trigger is pressed and the thumb resting on the controller.
