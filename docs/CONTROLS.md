# Controls

The same in all three games, on the Quest and on PC VR (jak_vr.cpp `fill_pad`). The controllers stand in for the
game's own pad, so every button of the game is there; the VR motions add to them. Every VR motion has its own row in
the VR menu to turn it off. What each button does in each game, on foot and riding, is under "Each game" below.

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
| Right grip | (fists), or R2 | With Physical melee and Fists on the right grip (both on to start): hold it to make fists. Only fists punch and kick (below). Jak II and 3: a quick squeeze and let go, with no punch or kick, is R2 (the JET-Board on; since 0.1.3). While the gun is in your hand, the grip just holds it. A controller with no right stick click keeps R2 on the grip with the fist. |
| Left grip | (left fist) or L2 | With Fists on the right grip, on foot: held, the left fist (all three games, so the left hand punches by its own grip). Jak II and 3 on foot (since 0.1.5): a quick squeeze and let go, with no punch, kick or other button meanwhile, is L2: Dark Jak (a light buzz says so). Squeeze it again within a third of a second and hold it: L2 held, no fist (Jak 3: Light Jak's powers with Y, A, X or B or the right stick, as the left stick click below; let go alone, Dark Jak). Jak 3 once he has Light Jak (since 0.1.6): the left grip held and Y pressed is L2 held too, then let Y go to heal, or X, A, B or the right stick (Each game, Jak 3). On the front of the drawn gun: the two-hand grip (with the gun drawn, L2 is the left stick click). In a vehicle, on the board and the like, or with Fists on the right grip off: L2 (Jak II: Dark Jak; Jak 3: Dark and Light Jak). |
| Right stick click | R2 | With Fists on the right grip: the grip's old button. Jak II and 3: the JET-Board on and off; Jak 3: Light Jak's end. With it off: R3. A quick click counts too (since 0.1.3). |
| Left stick click | L2 or L3 | Jak II and 3 on foot with Fists on the right grip: L2, as the left grip's quick squeeze (Jak II: Dark Jak; Jak 3: click and let go for Dark Jak, or hold it and push the right stick for Light Jak's powers: up heals (Triangle), down flies (Cross), left slows time (Square), right shields (Circle, up while the stick stays right); A and B work too, B kept held for the shield). Where the click is the menu button (SteamVR with Touch), the click with the left grip squeezed. Otherwise L3. |
| Both stick clicks together | none | Recentre the view. |
| Menu button | Start | Pause. Held one second: switch first and third person, in play and in a scene (a scene through Jak's eyes or around you); over a menu, the VR menu. |
| Left thumb on its thumb rest | D-pad | The right stick is the d-pad (Jak 1: the warp gates; Jak II and 3: the guns), and its click Select (Jak 1 and 3: a pause without the menu; Jak II: the multiplayer player list), except where the click is R2 (Jak II and 3 on foot with Fists on the right grip, and on the JET-Board): it stays R2 there, so the click for the board never pauses Jak 3 (since 0.1.3). A quick pulse in that hand as each direction goes in, and a light one as the thumb rest makes it the d-pad (the VR menu's "Buzz on d-pad presses"). The VR menu's other choice: the left hand held by your head makes the left stick the d-pad. |
| Left hand held by your head, a stick clicked | Select or L3 | Since 0.1.5, everywhere: the left stick's click is Select, the right stick's L3. So Select and L3 are in reach wherever the clicks are R2 and L2 (Jak II and 3 on foot with the fists on the grips). A controller with no left stick click (the Vive wand): the left touchpad's click in its middle is Select there. |

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

Punch and shoot (Jak II and 3's combo, the gun course's lesson): punch or kick something, and the trigger in the next
moment fires at what you hit, wherever the gun points (since 0.1.3; before, the shot went where your hand aimed and
the combo missed). With the gun in your right hand, punch with the left fist or X.

The crosshair (Jak II and 3, since 0.1.3): with the gun in your hand, a mark sits where the shot would land: on the
wall, the ground or the enemy the gun points at, near or far, and it stays the same size to your eye at any distance.
It turns red on an enemy the gun can reach. It starts Off (0.1.7, as 0.1.5 did; 0.1.3, 0.1.4 and 0.1.6 started with
the Dot, so their Dot is read as Off); any other crosshair you picked yourself is kept. The
VR menu (and the launcher's VR options)
has **Crosshair** (Off, Dot, Cross, Cross and dot, Circle, Circle and dot, Plus, Small circle: a thin ring about half a
degree across, 0.1.5), **Crosshair size**, **Crosshair
color**, **Crosshair opacity** and **Crosshair red on enemies**.

The left stick goes where it is pushed, measured from where you look (or where your left hand points): a thumb a
little off straight walks that little off straight (0.1.1; 0.1.0 turned any sideways push into a wide diagonal).

## Scenes

A scene plays through Jak's eyes when the view is in first person, or around you as a film. With **Adaptive
cutscenes** (on to start) a scene through his eyes goes around you by itself while he is not properly in it (not
drawn, far from what the game's camera shows, or carried along by something), and comes back to his eyes when he has
been properly in it a second (two seconds once it has left them twice, then four, so the view never fights the scene's
cuts; to 0.1.2, a scene that had left his eyes three times stayed around you to its end).
Holding the menu button one second in a scene switches the two yourself, and that choice lasts for the rest of that
scene.

## On the JET-Board, in vehicles, in the Titan suit, on rails and poles

**Getting on the JET-Board (Jak II and 3), and back on after putting it away:** on foot, click the right stick, or
(since 0.1.3) give the right grip a quick squeeze and let go without punching. Either is the game's R2, which takes
the board out and puts it away. The game only lets the board out where it can be ridden (once Jak has it: the city
and its outskirts, not indoors or in most missions), so if nothing happens, walk somewhere open and try again.

Riding, the buttons are as on foot, except the right grip is the game's R2 again (Jak II's car: up and down a height; the board: off), and the
right stick click is R3. Fists are not made there. On the JET-Board with Fists on the right grip, the right stick
click stays R2 as well, so the click that put Jak on the board takes him off again.

**Steer by grabbing the wheel** (on to start, since 0.1.8; VR menu and both launchers): in any vehicle steered by the
stick (Jak 1's Zoomer; every bike, car, race bike and desert vehicle in Jak II and 3; Jak 3's glider, the catacombs'
sled and the war factory's hellcat), squeeze a grip with your hand on its controls, where Jak's hand holds them (or
anywhere just ahead of you, low), and turn your hands to steer: as a wheel (one hand down, the other up), tilted back
as a car's, or as handlebars (push one hand, pull the other), all alike; one hand alone turns it about the middle. Let
go and it comes back to the middle. The stick steers as before, and the two add up. **Wheel turn** (60 degrees to
start) is how far you turn it for full steering; handlebars and the glider's bar need a little over half of that.
Jak's hands are drawn on the handles while you hold them. While a grip holds the wheel it is not its button: a quick
squeeze and let go is still the button (R2, L2), squeezed again and held it holds it, and while a hand holds the wheel
its stick's click is that button (Jak II and 3: the right click R2, the car's height or Jak 3's turbo while held,
the turbo in the hover cars and bikes with B for height below; the left click L2, Jak 3's camera view). A grip still
squeezed as you get off is nothing until you let it go.

**B for height while holding the wheel** (on to start, since 0.1.9; VR menu and both launchers; Jak II and 3's hover
cars and bikes): while your right hand holds the wheel, **B** is the height (the game's R2) and the right stick's click
is the turbo (Circle), the button B is otherwise. A quick squeeze of the right grip on the wheel is still the height.
Off: B is the turbo and the click the height, as in 0.1.8.

**Lean by pushing the wheel** (on to start, since 0.1.9; VR menu and both launchers): with the wheel held, push it away
from you to lean forward and pull it back to lean back, as the left stick forward and back (the lean in the air on
Jak 3's desert vehicles and jumps, and whatever forward and back do in each vehicle). It needs both hands moving
together, or one hand on a wheel; turning moves the hands opposite ways and never leans, and on handlebars one hand's
push is its turn. It adds to the stick.

**Hold the gun while driving** (on to start, since 0.1.8; Jak II and 3, once he has a gun): in the vehicles that let
Jak keep his gun (the city's bikes and cars, Jak 3's hijacked pickup too), reach over your right shoulder and squeeze
the right grip to draw it, as on foot; it stays in your right hand, aimed by it, and the right trigger fires it. Let
it go over the shoulder to put it away. The hip holster stays off in vehicles (a hand resting there is the car's
height button). The guards' bike, the hellcat and the desert vehicles have guns of their own and put his away. Off:
the gun is put away as he gets in, as in 0.1.7.

First person in vehicles is on to start since 0.1.3: on the Zoomer, the JET-Board, in cars, turrets and slides, the
Titan suit and the flut flut the view stays in Jak's head, turning with what he rides. The VR menu and the launchers'
VR Body have **Vehicle view height** and **Vehicle view forward** (centimetres from his eyes there; his body stays in
its seat) and **Vehicle view tilt** (degrees: looking straight ahead you see that far down, or up below 0; your hands
and the gun tip with the view, so they stay where yours are). Turn First person in vehicles off for the game's camera
there. Since 0.1.5 each kind of ride has its own switch too, used while First person in vehicles is on (all on to
start; each shown in the games that have it): **Zoomer**, **Flut Flut**, **JET-Board**, **Hover cars**, **Hover
bikes**, **Titan suit**, **Leaper lizards**, **Desert vehicles** and **Other rides** (slides, turrets, the glider) **in
first person**. **Steady view when riding** (on to start, since 0.1.3) keeps the view in Jak's seat and facing where the ride
goes without each bounce of it (Jak 3's leaper lizards bound under him); off, the view follows his head's every bob.

Round a pole the view is the game's camera (first person there spins with him). Since 0.1.7 it stays there after the
jump off until his feet are on the ground (**Third person after a pole until you land**, on to start; swimming, a
ledge, a death or a scene end it too, and so do 6 seconds in the air), so your aim does not jump in the air; off, the view goes back in his eyes as he lets go. A painful landing from a long fall (over 30 m: a health pip
lost, and no control until he is up again) is seen from the game's camera as well (0.1.7), so you see him land and
get up, then back in his eyes.

**Gun turrets aimed by the hand** (since 0.1.9, with **Aim with your hand** on): Jak II's drill platform turrets, and
Jak 3's Spargus wall turret, the forest's turrets, the Scorpion's gun and the pickup's gun in the tower mission (its
reticle goes where your hand points on the game's view). The turret turns to where your right hand points, at no more
than its own speed, and the right trigger fires. On a controller that tells when your first finger rests on the trigger (Touch,
Index), it aims by the hand only while that finger is on the trigger; lift it off and the stick turns the turret as
before. The stick works whenever it is pushed. The gun's lean of the VR menu (**Gun angle**) tilts the aim as it does
for the guns. The log says `[vr-gun] a gun turret: it turns to where the right hand points`.

**The fisherman's game** (Jak 1, since 0.1.9). With **Fishing game in first person** on (to start; the VR menu and both
launchers, Jak 1) and the view in first person, you play it from Jak's eyes on the bridge, the river turned ahead of
you, with the net in your right hand: its pole goes where the controller points, long enough to reach across the
river. Point it down to dip the basket where the fish come; whatever the basket is over is caught, as the game's own
net catches. A catch buzzes the net's hand (a fish lightly, the big one harder, an eel hardest), and so does the
basket going into the water. Squeeze the other grip to take the net in that hand. While you fish the grips and quick
swings of your hands are not punches or kicks. The log says `[vr-pad] the fishing game from Jak's eyes: the net in the
right hand`.

Off, or in third person: the game's camera shows it, and the net goes where your raised hand is, side to side (the
hand held higher, the right to start; 35 cm either way of where you stand is the river's edges), no faster than the
stick moves it. The stick still works: while it is pushed it moves the net, and it stays where the stick left it until
your hand moves again (5 cm).

**Eco sparks in first person** (on to start, since 0.1.7): while Jak holds eco, the game throws sparks off his body,
his head too, so in his eyes they burst right at yours (Jak II's and 3's Dark and Light Jak the same). Off, they are
left out while you see through his eyes; the eco works and sounds the same, and the game's camera still shows them.

## Menus

The game's own menus, as on a pad: the sticks move through them (Jak 1's menus read only the d-pad, so there both
sticks are its d-pad: 0.1.1), A (Cross) picks, B or Y goes back (in the menus B is the game's Triangle, its back
button, since 0.1.3: Jak 1's menus take Circle as Cross, so B as Circle picked instead of going back). Since 0.1.5 B
is the game's Circle where a menu has a Circle of its own: Jak 1's pause pages (the power cells, orbs and scout flies:
B opens the Options, Y the scout flies page) and Jak II's multiplayer menus (the lobby's options and address, the lobby
list's Join by address, the look editor's other character). Or point at
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

Multiplayer (0.1.6): Jak 1's title screen and pause menu have a Multiplayer entry that opens the VR menu's
Multiplayer page (host, join, the games found, the players); B goes back a page, and closes it from Multiplayer
itself. Jak II's title has its own Multiplayer (Host Game, Join Game) while Jak II multiplayer is on.

Typing (Jak II's multiplayer: your name, an address, a room code): while a text field is open the sticks type into
it, as an arcade's name entry. Up or down changes the letter before the cursor, right keeps it and goes on to the
next, left erases one; a held push steps on. A enters the text, Y leaves the field.

## Each game

Every button each game reads in play is on the controllers, and no two of a game's actions share one at the same
moment (0.1.5, checked against each game's code). Where one controller button does two things, they are told apart by
how it is used (a quick squeeze or a held one, on foot or riding, the left hand by your head or not), never by luck.
With the default options (Fists on the right grip on, the d-pad on the left thumb rest):

### Jak and Daxter

| Controller | Game button | In Jak and Daxter |
|---|---|---|
| Left stick, right stick | sticks | Move; turn (first person) or the camera (third person). |
| A | Cross | Jump; again in the air, the double jump. Crouched, the high jump. |
| B | Circle | Spin kick, on the ground and in the air. Talk to someone the game marks with the circle. |
| X | Square | Punch; in the air, the dive (it presses the floor buttons). Crouched, the uppercut. Running, the dive forward. |
| Y | Triangle | The game's look-around (Jak stands still; Y again leaves it). Leave a talk. |
| Left trigger | L1 | Crouch; moving, the roll. |
| Right trigger | R1 | Crouch as well (the game's other crouch button). |
| Right grip | (fists) | The fists. A fist thrown is Square, swept is Circle. |
| Left grip | (left fist) | The left fist. |
| Left thumb on its rest, right stick | D-pad, Select | The d-pad (the warp gates' choice of place); the right stick's click is Select (a pause without the menu). |
| Menu button | Start | Pause; held, first and third person. |
| A-Grav Zoomer | | The left stick steers, or its handlebars gripped and turned (Steer by grabbing the wheel), and A jumps; the other buttons as the game's own. |
| Flut Flut | | A jumps (twice), X its attack, as on foot. |

### Jak II

| Controller | Game button | In Jak II |
|---|---|---|
| Left stick, right stick | sticks | Move; turn (first person) or the camera (third person). |
| A | Cross | Jump; again in the air, the double jump. |
| B | Circle | Spin kick. |
| X | Square | Punch; in the air, the dive (as Dark Jak with his bombs, the dark bomb). |
| Y | Triangle | Talk, use, get in and out of a car. |
| Left trigger | L1 | Crouch; moving, the roll. |
| Right trigger | R1 | Draw the gun and fire. |
| Right grip | (fists), R2 | Held: the fists. A quick squeeze and let go: R2, the JET-Board out and away. |
| Right stick click | R2 | The JET-Board out and away (and off it again). |
| Left grip | (left fist), L2 | Held: the left fist. A quick squeeze and let go: L2, Dark Jak (once his meter is full). |
| Left stick click | L2 | Dark Jak, as the left grip's quick squeeze (with the gun drawn, the click is L2, the grip holds the gun). |
| Left thumb on its rest, right stick | D-pad | The guns: up, down, left, right pick each of his four. |
| Left hand by your head, a stick click | Select, L3 | The left click Select (Jak II multiplayer: the player list; alone the game does nothing with it), the right L3 (multiplayer: the HUD and names while held). |
| Menu button | Start | Pause; held, first and third person. |
| Riding a car or bike | | A speeds up, X brakes and backs up, the left trigger hops, B the turbo, the right grip goes up or down a height, Y gets out. Gripped, the handlebars or the controls steer; with your right hand on them B is the height and the right stick click the turbo (B for height while holding the wheel; off, the click is the height). Pushed away or pulled back, the wheel leans (Lean by pushing the wheel). With the gun, it is drawn over the right shoulder (Hold the gun while driving). |
| On the JET-Board | | A jumps, X, B and the left trigger as the game's board moves; the right grip or the right stick click takes him off. |
| In the Titan suit | | The game's own buttons on the same controller buttons (X punches). |

### Jak 3

| Controller | Game button | In Jak 3 |
|---|---|---|
| Left stick, right stick | sticks | Move; turn (first person) or the camera (third person). |
| A | Cross | Jump; again in the air, the double jump. |
| B | Circle | Spin kick. |
| X | Square | Punch; in the air, the dive. |
| Y | Triangle | Talk, use, get in and out of a vehicle. |
| Left trigger | L1 | Crouch; moving, the roll. |
| Right trigger | R1 | Draw the gun and fire. |
| Right grip | (fists), R2 | Held: the fists. A quick squeeze and let go: R2, the JET-Board out and away. |
| Right stick click | R2 | The JET-Board out and away; as Light Jak, back to Jak. |
| Left grip | (left fist), L2 | Held: the left fist. A quick squeeze and let go: L2, Dark Jak. Light Jak (since 0.1.6, once the game has given him Light Jak; the game's "Hold L2 then Press Triangle"): hold the left grip and press Y. That is L2 held (no fist, a light buzz): Jak stops for his power. Then let Y go: Triangle, he heals. Or, with the grip still held and before Y is let go (Y let go alone is the heal), slide the thumb from Y to X: Square, time slows. Or press A: Cross, the wings. Or press B and keep it held: Circle, the shield, up while B stays held (the game's own rule: up 3 s by itself, then only while Circle is held; only once the story has given it, and it takes light eco; the log says why when the game refuses it). Or push the right stick: up heals, down the wings, left slows time, right the shield (up while the stick stays right, the view still meanwhile). Let the grip go before choosing: L2 alone, Dark Jak, as on the game's pad. As Dark Jak, Y with the grip held is Y (his invisibility), not Light Jak. The 0.1.5 way works too: squeezed again at once and held, L2 held, then Y, A, X or B or the right stick. |
| Left stick click | L2 | As the left grip: a click and let go for Dark Jak; held, Light Jak's powers with Y, A, X or B or the right stick. |
| Left thumb on its rest, right stick | D-pad | The guns: each direction picks a color, and the same direction again the next gun of that color. |
| Left hand by your head, a stick click | Select, L3 | The left click Select (a pause without the menu), the right L3. |
| Menu button | Start | Pause; held, first and third person. |
| Riding a city car | | A speeds up, X brakes and backs up, the left trigger hops, B the turbo, the right grip goes up or down a height, Y gets out. Gripped, the handlebars or the wheel steer; with your right hand on them B is the height and the right stick click the turbo (B for height while holding the wheel; off, the click is the height). Pushed away or pulled back, the wheel leans (Lean by pushing the wheel). With the gun, it is drawn over the right shoulder (Hold the gun while driving). |
| Riding a desert vehicle | | A speeds up, X brakes and backs up, the left trigger jumps, the right trigger fires its guns, the right grip the turbo, the left grip held with the right stick looks around, the right stick click its other switch, Y gets out. Gripped, the wheel steers; the right stick click is then the turbo and the left the camera view. The glider, the catacombs' sled and the war factory's hellcat steer by their bar or wheel the same. |
| On a leaper lizard | | A jumps; the other buttons as the game's own. |
| On the JET-Board | | A jumps. B held grinds a rail (the game's Square) and X zaps (its Circle): swapped from Jak II's board since 0.1.6, so the left thumb stays on the stick. The triggers as the game's board moves; the right grip or the right stick click takes him off. |

## PC VR controllers

Every controller family OpenXR names has its buttons set up; SteamVR's own
controller binding page can change any of them. Where a controller lacks a button, the nearest it has:

| Controller | Differences from the tables above |
|---|---|
| Quest Touch, Touch Plus, Touch Pro (Link, Air Link, Virtual Desktop's own VDXR), Vive Focus 3 | None. |
| Quest Touch through SteamVR | SteamVR keeps the left menu button for its dashboard, so the left stick's click is the menu button: a click is Start, held one second it switches first and third person, in play and in a scene (over a menu, the VR menu). In play the hold switches only with the stick at rest (since 0.1.8): held while the stick moves Jak, as when running in a fight, it switches nothing and is no Start. So the left stick's click is not L3 there: L3 is the right stick's click with the left hand held by your head (Jak II multiplayer's HUD and names). On foot in Jak II and 3 the click with the left grip squeezed is L2 (Dark Jak), not the menu button, and the left grip's quick squeeze is L2 as everywhere. SteamVR has no thumb rest for them either: the d-pad is the left hand held by your head, and the left stick's click there is Select. |
| Pico 4 and Neo 3, Vive Cosmos, HP Reverb G2 | No thumb rest: the d-pad is the left hand held by your head (below). |
| Valve Index | Its A and B are A and B on the right hand, X and Y on the left. A firm squeeze of the left touchpad is the menu button (Start). No thumb rest: the d-pad is the left hand held by your head. |
| Windows Mixed Reality (and the Samsung Odyssey) | Sticks as above. Right touchpad click: A; right menu button: B. Left touchpad click: X, or Y on its upper half; left menu button: Start. The d-pad is the left hand held by your head. |
| Vive wand | The touchpads are the sticks. Right touchpad click: A, or B on its upper half; right menu button: the right stick click (the JET-Board with fists on the grip). Left touchpad click: X, or Y on its upper half; left menu button: Start. No left stick click, so on foot the left grip stays L2 (Dark Jak) as well as the left fist. The d-pad is the left hand held by your head, the left touchpad touched; there its click in the middle is Select (since 0.1.5; before, the wand had no Select), and the right menu button L3. The roll (L1 while he moves): keep the left thumb on the touchpad away from its middle, so he keeps moving, and pull the left trigger; jump with a click on the lower half of the right touchpad (its upper half is B). Or play with a gamepad: **Gamepad only** (below). |

Any controller without a thumb rest gets the "Left hand at your head" d-pad whatever the VR menu says, and the log says
so on its first input line (`[openxr] left hand: ... thumb rest no`). The same way, any controller whose left menu button
the runtime keeps for itself has the left stick's click as the menu button (`menu button no (the left stick's click is
the menu button)`). A controller without touch sensors has the finger
on the trigger when the trigger is pressed and the thumb resting on the controller.

## Gamepad only

**Gamepad only (no VR controllers)** (since 0.1.7; the VR menu's Controls and both launchers' VR options, off to
start): play with a gamepad alone, its buttons and sticks as the flat game's. While a gamepad is connected, the VR
controllers are left out of the game: their buttons, sticks, gestures and hands (no hand aim, no laser). Since 0.1.9,
with no gamepad connected the VR controllers play as usual (so the option left on never locks you out), and the log
says `[vr-pad] Gamepad only: no gamepad connected now, so the VR controllers play`. The headset still turns the view, and in
first person the gamepad's right stick turns you as **Turning** says (snap or smooth). The game's rumble goes to the
gamepad alone. The VR menu takes both: the controllers, and the gamepad's left stick or d-pad, Cross to choose, Circle
or Triangle to go back. With the controllers left out, open it from the game's pause menu (VR Options). The log says
`[vr-pad] Gamepad only: the VR controllers are left out of the game` as it starts.
