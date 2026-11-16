// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
package dev.jakquest.vr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Every VR option the launchers offer: its words, its values and where it goes. Plain Java, nothing of Android's or
 * Swing's, so the Quest's launcher and the PC's are built from this one file and offer the same options, as Ratchet &
 * Clank Quest VR's Options. The names, help, ranges and defaults are the in-game VR menu's (game/vr/vr_menu.cpp) and the
 * game's own (game/vr/vr_options.h); the file is the game's vr-options.txt, one per game, in the game's settings folder
 * (OpenGOAL/jak1/settings and so on), which the game reads at start and its VR menu writes.
 */
public final class VrOptions {
    private VrOptions() {}

    public enum Kind { TOGGLE, CHOICE, NUMBER }

    /** One option: a row in a launcher tab and a line of vr-options.txt. */
    public static final class Option {
        public final String key, label, help;
        public final Kind kind;
        /** CHOICE: the names shown and the values written. TOGGLE: On and Off, "on" and "off". */
        public final String[] names, values;
        /** NUMBER: its range, step and unit (after the number). */
        public final int low, high, step;
        public final String unit;
        public final String fallback;
        /** Only Jak II and 3 have guns. */
        public final boolean gunsOnly;
        /** Read when the game starts (the VR menu says "when the game next starts" for it). */
        public final boolean atStart;

        private Option(String key, String label, String help, Kind kind, String[] names, String[] values, int low,
                int high, int step, String unit, String fallback, boolean gunsOnly, boolean atStart) {
            this.key = key;
            this.label = label;
            this.help = help;
            this.kind = kind;
            this.names = names;
            this.values = values;
            this.low = low;
            this.high = high;
            this.step = step;
            this.unit = unit;
            this.fallback = fallback;
            this.gunsOnly = gunsOnly;
            this.atStart = atStart;
        }

        /** The name shown for a value written (a CHOICE's name, On or Off, the number and its unit). */
        public String show(String value) {
            switch (kind) {
                case TOGGLE:
                    return isOn(value) ? "On" : "Off";
                case CHOICE:
                    for (int i = 0; i < values.length; i++) if (values[i].equals(value)) return names[i];
                    return names[indexOf(fallback)];
                default:
                    return value + unit;
            }
        }

        /** A CHOICE's index of a value (its fallback's when the value is none of them). */
        public int indexOf(String value) {
            for (int i = 0; i < values.length; i++) if (values[i].equals(value)) return i;
            for (int i = 0; i < values.length; i++) if (values[i].equals(fallback)) return i;
            return 0;
        }

        /** A value as the game would read it: a number held to its range, a choice to its values. */
        public String clean(String value) {
            if (value == null) return fallback;
            switch (kind) {
                case TOGGLE:
                    return isOn(value) ? "on" : "off";
                case CHOICE:
                    return values[indexOf(value)];
                default:
                    try {
                        int v = Math.round(Float.parseFloat(value.trim()));
                        return Integer.toString(Math.max(low, Math.min(high, v)));
                    } catch (NumberFormatException e) {
                        return fallback;
                    }
            }
        }
    }

    public static boolean isOn(String value) {
        return "on".equals(value) || "1".equals(value) || "true".equals(value);
    }

    private static final String[] ON_OFF_NAMES = {"On", "Off"};
    private static final String[] ON_OFF = {"on", "off"};

    private static Option toggle(String key, String label, String help, boolean on) {
        return new Option(key, label, help, Kind.TOGGLE, ON_OFF_NAMES, ON_OFF, 0, 1, 1, "", on ? "on" : "off", false,
                false);
    }

    private static Option number(String key, String label, String help, int low, int high, int step, String unit,
            int fallback) {
        return new Option(key, label, help, Kind.NUMBER, null, null, low, high, step, unit, Integer.toString(fallback),
                false, false);
    }

    private static Option choice(String key, String label, String help, String[] names, String[] values,
            String fallback) {
        return new Option(key, label, help, Kind.CHOICE, names, values, 0, names.length - 1, 1, "", fallback, false,
                false);
    }

    private static Option guns(Option o) {
        return new Option(o.key, o.label, o.help, o.kind, o.names, o.values, o.low, o.high, o.step, o.unit, o.fallback,
                true, o.atStart);
    }

    private static Option atStart(Option o) {
        return new Option(o.key, o.label, o.help, o.kind, o.names, o.values, o.low, o.high, o.step, o.unit, o.fallback,
                o.gunsOnly, true);
    }

    // ---- Graphics (each read when the game starts) ----
    public static final Option RESOLUTION = atStart(choice("resolution", "Eye resolution",
            "Of the headset's own recommended size. Higher is sharper and slower; the biggest effect on performance. "
                    + "100% to start.",
            new String[] {"70%", "80%", "90%", "100%", "110%", "120%", "130%", "150%"},
            new String[] {"70", "80", "90", "100", "110", "120", "130", "150"}, "100"));
    public static final Option REFRESH = atStart(choice("refresh-hz", "Refresh rate",
            "How many times a second the headset shows a frame. Higher is smoother and costs more. The headset's own "
                    + "is what it is set to in its settings.",
            new String[] {"The headset's own", "72 Hz", "80 Hz", "90 Hz", "120 Hz"},
            new String[] {"0", "72", "80", "90", "120"}, "0"));
    // Off to start since 0.1.1 (owner: it flickered the title screen); its key changed from 0.1.0's "space-warp" (on to
    // start then), so a file 0.1.0 wrote starts off too.
    public static final Option SPACE_WARP = atStart(toggle("smoother-frames", "Smoother frames (AppSW)",
            "The Quest makes every other frame from how everything moved (Meta's Application SpaceWarp), so the game "
                    + "draws half of them, at half the headset's rate. Fast moves can smear a little at the edges of "
                    + "things. Off in menus and on the title screen. On a PC your headset's own smoothing does this.",
            false));
    public static final Option FOVEATION = atStart(choice("foveation", "Foveated rendering",
            "Draws the edges of each eye's picture at a lower resolution, where the lenses blur it anyway, so the "
                    + "middle keeps its detail and the game runs faster. High saves the most. On a PC it does nothing.",
            new String[] {"Off", "Low", "Medium", "High", "Highest"}, new String[] {"0", "1", "2", "3", "4"}, "2"));
    public static final Option BOOST = atStart(toggle("cpu-gpu-boost", "CPU and GPU boost",
            "The Quest runs its processors at the highest speed it lets a game ask for, so fewer frames are missed. "
                    + "Uses more battery and the headset gets warmer. On a PC it does nothing.",
            true));
    public static final Option MAX_DRAW = atStart(toggle("max-draw-distance", "Max draw distance",
            "Everything drawn as far as you can see, at its highest detail: enemies, crates, people, effects, the "
                    + "level and the sea, so nothing pops in as you come near. Costs some frame rate. Off: the game's own "
                    + "distances.",
            true));
    public static final Option MSAA = atStart(choice("msaa", "Anti-aliasing",
            "Smooths jagged edges. Cheap for the graphics chip, and well worth it in VR.",
            new String[] {"Off", "2x", "4x"}, new String[] {"1", "2", "4"}, "2"));
    public static final Option ANISOTROPY = atStart(choice("anisotropy", "Anisotropic filtering",
            "Keeps floors and walls sharp at a low angle. 16x is the games' own.",
            new String[] {"Off", "2x", "4x", "8x", "16x"}, new String[] {"1", "2", "4", "8", "16"}, "16"));
    // ---- Performance (0.1.1; each off to start: the owner, "optional and off by default of course") ----
    public static final Option FRAME_RATE = choice("frame-rate", "Game frame rate",
            "How often the game draws a new picture. 60 is how the games were made. The headset's: a new picture for "
                    + "every frame the headset shows, the smoothest and the most work. Half the headset's: half the "
                    + "work, and the headset turns each picture to your head in between (36 a second at 72 Hz).",
            new String[] {"60", "The headset's", "Half the headset's"}, new String[] {"0", "1", "2"}, "0");
    public static final Option DYNAMIC_RESOLUTION = toggle("dynamic-resolution", "Dynamic resolution",
            "The eyes are drawn a little smaller while the headset is not keeping up with the game, and back at "
                    + "full size when it is. Eye resolution is the size it starts from.",
            false);
    public static final Option PS2_DETAIL = toggle("ps2-detail", "PS2 detail",
            "Far-off things, effects and shadows drawn as the PlayStation 2 drew them: faster, with some pop-in as "
                    + "you come near. While it is on, Max draw distance does nothing.",
            false);
    public static final Option SHARPEN = atStart(choice("sharpening", "Sharpening",
            "The Quest sharpens the picture as it shows it, so a lower Eye resolution keeps its edges. On a PC it "
                    + "does nothing.",
            new String[] {"Off", "Normal", "Strong"}, new String[] {"0", "1", "2"}, "0"));
    public static final Option[] GRAPHICS = {RESOLUTION, REFRESH, SPACE_WARP, FOVEATION, BOOST, MAX_DRAW, MSAA,
            ANISOTROPY, FRAME_RATE, DYNAMIC_RESOLUTION, PS2_DETAIL, SHARPEN};

    // ---- VR Body: the view, the body and the HUD ----
    public static final Option VIEW = choice("first-person", "View",
            "How you see play: through Jak's eyes, or the game's camera around you. The left menu button held a "
                    + "second switches in the game.",
            new String[] {"First person", "Third person"}, ON_OFF, "on");
    public static final Option CUTSCENES = choice("cutscenes", "Cutscenes",
            "How scenes are seen: through his eyes where he is in them, in VR around you, or on a big screen.",
            new String[] {"Through his eyes", "Around you", "On a screen"}, new String[] {"0", "1", "2"}, "0");
    public static final Option ADAPTIVE = toggle("adaptive-cutscenes", "Adaptive cutscenes",
            "A scene seen through his eyes goes around you by itself while Jak is not really in it (the opening "
                    + "titles, a ride, a shot of somewhere else), and back through his eyes when he is.", true);
    public static final Option TALKS = toggle("first-person-in-talks", "Talks in first person",
            "A talk during play seen from his eyes too, rather than the game's camera around you.", true);
    public static final Option VEHICLES = toggle("first-person-vehicles", "First person in vehicles",
            "On the Zoomer, the JET-Board, in cars, turrets, slides, the Titan suit and on the flut flut: seen from his "
                    + "eyes too, the view turning with what he rides. Off: the game's camera around you there.", false);
    public static final Option SEATED = toggle("seated", "Seated play",
            "For playing sitting down: the view at his eyes whatever your height.", false);
    public static final Option VIEW_HEIGHT = number("view-height", "View height",
            "Raises or lowers the view in first person.", -40, 40, 5, " cm", 0);
    public static final Option WORLD_SCALE = number("world-scale", "World scale",
            "First person: 100% is life size, Jak as tall as you. More makes the world bigger around you (145% is "
                    + "how 0.1.0 looked).", 50, 200, 5, "%", 100);
    public static final Option THIRD_SCALE = number("third-person-scale", "Third person world scale",
            "Third person: the size the world is seen at.", 50, 200, 5, "%", 100);
    public static final Option BODY = toggle("body", "Body",
            "Jak's body seen from his eyes, his arms reaching your hands (full-body IK).", true);
    public static final Option BODY_SHOWN = choice("body-shown", "Body shown",
            "What of Jak you see from his eyes. His hands always; his shadow keeps all of him.",
            new String[] {"Whole body", "Body and arms", "Body and legs", "Arms and legs", "Arms only", "Legs only",
                    "Hands only"},
            new String[] {"0", "1", "2", "3", "4", "5", "6"}, "0");
    public static final Option HIDE_ARMS_NEAR_EYES = toggle("hide-arms-near-eyes", "Hide arms near your eyes",
            "An arm brought up close to your face fades out of the view, its hand staying.", false);
    public static final Option FADE_BODY = toggle("fade-body-near-eyes", "Fade body near your eyes",
            "A leg kicked up by your face, and his body in a roll, a flip or a dive, fade out of the view and back, "
                    + "smoothly, so nothing of him blocks what you are looking at.", true);
    public static final Option KEEP_UPRIGHT = toggle("keep-body-upright", "Keep your body upright",
            "His body stays upright under your head whatever move he makes, his legs still stepping where the game "
                    + "puts his feet. Off: it turns and tips as the game moves him.", true);
    public static final Option FINGERS = toggle("fingers", "Finger tracking",
            "His fingers follow yours on the controllers: a fist, a point, a thumbs up.", true);
    public static final Option PREDICT = toggle("predict-hands", "Hands without lag",
            "His hands drawn where yours will be when the frame is shown.", true);
    public static final Option HANDS_ON_WORLD = toggle("hands-on-world", "Hands stop at walls",
            "His hands, and what they hold, stop at walls and floors.", true);
    public static final Option CROUCH = toggle("physical-crouch", "Physical crouch",
            "Crouching down yourself crouches him.", true);
    public static final Option CROUCH_DROP = number("crouch-drop", "Crouch depth",
            "How far down your head goes to crouch him.", 10, 60, 5, " cm", 30);
    public static final Option ARM_LENGTH = number("arm-length", "Arm length",
            "His arms longer or shorter, to fit yours.", 70, 150, 5, "%", 100);
    public static final Option BODY_HEIGHT = number("body-height", "Body height",
            "His body higher or lower under the view.", -30, 30, 5, " cm", 0);
    public static final Option BODY_FORWARD = number("body-forward", "Body forward",
            "His body ahead of or behind your head.", -30, 30, 5, " cm", 0);
    public static final Option HUD_SIZE = number("hud-size", "HUD size",
            "How big the HUD and the menus are.", 50, 150, 10, "%", 100);
    public static final Option HUD_DISTANCE = number("hud-distance", "HUD distance",
            "How far ahead of you the HUD floats.", 80, 300, 10, " cm", 160);
    public static final Option HUD_HEIGHT = number("hud-height", "HUD height",
            "Raises or lowers the HUD.", -40, 40, 5, " cm", 0);
    public static final Option HUD_FOLLOWS = toggle("hud-follows-head", "HUD follows your head",
            "On, the HUD moves with your head. Off, it stays put ahead of you, and moves only when you look well away.",
            true);
    public static final Option[] VR_BODY = {VIEW, CUTSCENES, ADAPTIVE, TALKS, VEHICLES, SEATED, VIEW_HEIGHT, WORLD_SCALE, THIRD_SCALE, BODY,
            BODY_SHOWN, HIDE_ARMS_NEAR_EYES, FADE_BODY, KEEP_UPRIGHT, FINGERS, PREDICT, HANDS_ON_WORLD, CROUCH, CROUCH_DROP, ARM_LENGTH, BODY_HEIGHT, BODY_FORWARD, HUD_SIZE,
            HUD_DISTANCE, HUD_HEIGHT, HUD_FOLLOWS};

    // ---- VR Controls: turning, the d-pad, the hands in a fight, vibration ----
    public static final Option TURN = choice("turn", "Turning",
            "The right stick turns you smoothly, or in steps.", new String[] {"Smooth", "Snap"},
            new String[] {"1", "0"}, "1");
    public static final Option SNAP = number("snap-degrees", "Snap turn angle",
            "How far one snap turns you.", 15, 90, 15, " degrees", 45);
    public static final Option SMOOTH = number("smooth-degrees", "Smooth turn speed",
            "How fast you turn with the stick full over.", 30, 360, 30, " degrees a second", 120);
    public static final Option MOVE = choice("move-direction", "Move direction",
            "The left stick moves Jak where you look, or where your left hand points.",
            new String[] {"Where you look", "Where your left hand points"}, new String[] {"0", "1"}, "0");
    public static final Option DPAD = choice("dpad", "D-pad",
            "What makes a stick the d-pad (gun choice, warp gates): the left thumb resting on its rest makes the right "
                    + "stick the d-pad, or the left hand held by your head makes the left stick the d-pad. A controller "
                    + "with no thumb rest always uses the hand by your head.",
            new String[] {"Left thumb on its rest", "Left hand at your head"}, new String[] {"1", "0"}, "1");
    public static final Option DPAD_REACH = number("dpad-reach", "D-pad reach",
            "How near your head the left hand counts as there.", 10, 40, 2, " cm", 22);
    public static final Option MENU_HAND = choice("menu-hand", "Menu pointer hand",
            "Which hand points at the game's menus and the VR menu. Either hand: the right points until the left "
                    + "trigger is pulled on a menu.",
            new String[] {"Either hand", "Right hand", "Left hand"}, new String[] {"0", "1", "2"}, "0");
    public static final Option MELEE = toggle("hand-melee", "Physical melee",
            "Punch to punch, sweep a hand across to spin kick, punch upward from a crouch to uppercut.", true);
    public static final Option MELEE_GRIP = toggle("melee-grip", "Fists on the right grip",
            "Hold the right grip to make fists: only fists hit, so moving your hands never hits by accident. Its old "
                    + "button (the JET-Board) moves to the right stick click. The left grip makes the left fist too "
                    + "(in Jak II and 3 it is still Dark Jak's button).",
            true);
    public static final Option STICK_CLICK_MENU = toggle("stick-click-menu", "Stick click as menu button",
            "When SteamVR keeps the left menu button (Touch controllers there), the left stick's click is the menu "
                    + "button: Start, held a second the view or the VR menu. Off: it stays L3.", true);
    public static final Option MELEE_SPEED = number("melee-speed", "Melee speed",
            "How fast a hand must move to hit, in tenths of a metre a second (25: 2.5 m/s).", 10, 50, 5, "", 25);
    public static final Option SPREAD_SPIN_KICK = toggle("spread-spin-kick", "Spin kick by spreading your arms",
            "Hold both arms out wide at your sides, as a T, for a spin kick. Bring them in for the next.", false);
    public static final Option HAND_AIM = toggle("hand-aim", "Aim with your hand",
            "The gun, and Jak 1's yellow eco, go where your hand points.", true);
    public static final Option TWO_HAND = guns(toggle("two-hand-grip", "Two-hand grip",
            "The left hand on the gun's front steadies and aims it.", true));
    public static final Option HOLSTER = guns(toggle("holster-draw", "Draw from hip or shoulder",
            "Grip at your hip or over your shoulder to draw the gun; back there and let go to put it away.", true));
    public static final Option HOLSTER_SIZE = guns(number("holster-size", "Holster size",
            "How big the places at your hips and over your shoulders are, where a grip draws the gun.", 50, 200, 10,
            "%", 130));
    public static final Option HOLSTER_ON_FOOT = guns(toggle("holster-on-foot", "Holsters only on foot",
            "In a car, on the board or a turret, a grip at your hip or shoulder stays the game's own button.", true));
    public static final Option GUN_ANGLE = guns(number("gun-angle", "Gun angle",
            "The gun tilted up or down from the controller.", -45, 45, 5, " degrees", 0));
    public static final Option VIBRATION = toggle("vibration", "Vibration", "The controllers buzz.", true);
    public static final Option VIBRATION_STRENGTH = number("vibration-strength", "Vibration strength",
            "How hard every buzz is.", 25, 200, 25, "%", 100);
    public static final Option WORLD_VIBRATION = toggle("world-vibration", "Vibration on walls and floors",
            "A buzz as a hand meets a wall or the floor, harder the harder you press.", true);
    public static final Option DPAD_BUZZ = toggle("dpad-buzz", "Buzz on d-pad presses",
            "A quick pulse in the stick's hand each time a d-pad direction goes in.", true);
    public static final Option GUN_SWITCH_BUZZ = guns(toggle("gun-switch-buzz", "Buzz on gun switch",
            "A quick pulse in the gun's hand as you change guns.", true));
    public static final Option[] VR_CONTROLS = {TURN, SNAP, SMOOTH, MOVE, DPAD, DPAD_REACH, DPAD_BUZZ, MENU_HAND,
            STICK_CLICK_MENU, MELEE, MELEE_GRIP, MELEE_SPEED, SPREAD_SPIN_KICK, HAND_AIM, TWO_HAND, HOLSTER, HOLSTER_SIZE,
            HOLSTER_ON_FOOT, GUN_ANGLE, VIBRATION, VIBRATION_STRENGTH, WORLD_VIBRATION, GUN_SWITCH_BUZZ};

    /** The launcher's tabs of options, in order. */
    public static final String[] TAB_NAMES = {"Graphics", "VR Body", "VR Controls"};
    public static final Option[][] TABS = {GRAPHICS, VR_BODY, VR_CONTROLS};

    // ---- Mods: built in, each with its switch (the launchers' Mods) ----

    /**
     * Jak 1's multiplayer (game/party.h): Teamruns' game side with a party of our own. Jak 1's file only. On to start
     * (owner, 2026-11-14): it changes nothing in the game until the player hosts or joins a game.
     */
    public static final Option MULTIPLAYER = toggle("multiplayer", "Multiplayer",
            "Jak 1: play together, built on Teamruns, the community's Jak 1 multiplayer. One player hosts and the others "
            + "join, on this network or through a party server: their Jaks are in your game, and what one takes, "
            + "everyone has. In the game: Options, VR Options, Multiplayer, to host or join. Until you do, it changes "
            + "nothing in the game; turn it off to keep it out altogether.", true);
    public static final Option[] MODS = {MULTIPLAYER};
    public static final String MULTIPLAYER_GAME = "jak1";

    // ---- party.txt, beside Jak 1's vr-options.txt: what game/party.cpp reads ----

    public static final int NAME_MAX = 24;

    public static File partyFile(File configDir) {
        return new File(file(configDir, MULTIPLAYER_GAME).getParentFile(), "party.txt");
    }

    /** A name the game's font can show: printable ASCII, at most NAME_MAX, trimmed. */
    public static String cleanName(String typed) {
        StringBuilder b = new StringBuilder();
        for (char c : typed.trim().toCharArray()) {
            if (b.length() >= NAME_MAX) break;
            b.append(c >= 0x20 && c < 0x7f ? c : '?');
        }
        return b.toString().trim();
    }

    /** A party server's address as typed: host or host:port, no spaces. */
    public static String cleanServer(String typed) {
        String s = typed.trim();
        return s.matches("[A-Za-z0-9.:\\[\\]_-]{0,253}") ? s : "";
    }

    /** party.txt's "name", "server" and "build" (absent: a made-up name, no server). */
    public static Map<String, String> readParty(File file) {
        Map<String, String> values = new LinkedHashMap<>();
        if (file.isFile()) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                    StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    int space = line.indexOf(' ');
                    if (line.startsWith("#") || space < 0) continue;
                    values.put(line.substring(0, space), line.substring(space + 1).trim());
                }
            } catch (IOException ignored) {
                // a new one
            }
        }
        String name = cleanName(values.getOrDefault("name", ""));
        if (name.isEmpty()) name = "Player" + (1000 + new java.util.Random().nextInt(9000));
        values.put("name", name);
        values.put("server", cleanServer(values.getOrDefault("server", "")));
        return values;
    }

    /**
     * Writes party.txt: the name and server as given, and this release (everyone in a party needs the same one; the
     * launcher writes it each time it starts the game).
     */
    public static void writeParty(File file, String name, String server, String release) throws IOException {
        File dir = file.getParentFile();
        if (dir != null && !dir.isDirectory() && !dir.mkdirs()) throw new IOException("cannot make " + dir);
        File temp = new File(file.getPath() + ".new");
        try (Writer out = new OutputStreamWriter(new FileOutputStream(temp), StandardCharsets.UTF_8)) {
            out.write("# Jak and Daxter Quest VR multiplayer (Jak 1): the launcher writes this.\n");
            out.write("name " + cleanName(name) + "\n");
            String s = cleanServer(server);
            if (!s.isEmpty()) out.write("server " + s + "\n");
            out.write("build " + release + "\n");
        }
        if (file.exists() && !file.delete()) throw new IOException("cannot replace " + file);
        if (!temp.renameTo(file)) throw new IOException("cannot write " + file);
    }

    /** Before the game starts: party.txt with this release, its name and server kept (a made-up name the first time). */
    public static void stampParty(File configDir, String release) throws IOException {
        File f = partyFile(configDir);
        Map<String, String> p = readParty(f);
        writeParty(f, p.get("name"), p.get("server"), release);
    }

    /** Graphics presets (the launcher's buttons): the Graphics options each sets. */
    public static final String[] PRESETS = {"Performance", "Balanced", "Quality"};
    public static final String[] PRESET_HELP = {
            "Quest 2, or wherever the game stutters: 80% eyes, high foveation, the game's own distances, no anti-aliasing.",
            "Quest 3 and 3S, and most PCs: the defaults.",
            "A strong PC: 120% eyes, 4x anti-aliasing, low foveation."};

    public static Map<String, String> preset(int which) {
        Map<String, String> m = new LinkedHashMap<>();
        switch (which) {
            case 0:
                m.put("resolution", "80");
                m.put("foveation", "3");
                m.put("max-draw-distance", "off");
                m.put("msaa", "1");
                m.put("anisotropy", "4");
                // (Smoother frames is left as the player has it: off to start since 0.1.1, as it flickers some screens)
                m.put("cpu-gpu-boost", "on");
                break;
            case 2:
                m.put("resolution", "120");
                m.put("foveation", "1");
                m.put("max-draw-distance", "on");
                m.put("msaa", "4");
                m.put("anisotropy", "16");
                break;
            default:
                for (Option o : GRAPHICS) m.put(o.key, o.fallback);
        }
        return m;
    }

    /** Every option, for reading and writing the file. */
    public static List<Option> all() {
        List<Option> list = new ArrayList<>();
        for (Option[] tab : TABS) for (Option o : tab) list.add(o);
        for (Option o : MODS) list.add(o);
        return list;
    }

    public static Option find(String key) {
        for (Option o : all()) if (o.key.equals(key)) return o;
        return null;
    }

    /** The game's settings folder (OpenGOAL/<game>/settings under a config folder) and its vr-options.txt. */
    public static File file(File configDir, String game) {
        return new File(new File(new File(new File(configDir, "OpenGOAL"), game), "settings"), "vr-options.txt");
    }

    /**
     * The values in a game's file, every option there (its default where the file has none or no file yet), and the
     * file's other lines kept as they are (a world scale written as 1.25 is read as 125%).
     */
    public static Map<String, String> read(File file) {
        Map<String, String> values = new LinkedHashMap<>();
        for (Option o : all()) values.put(o.key, o.fallback);
        if (file.isFile()) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                    StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] words = line.trim().split("\\s+");
                    if (words.length < 2 || words[0].startsWith("#")) continue;
                    Option o = find(words[0]);
                    String value = words[1];
                    if (o == WORLD_SCALE || o == THIRD_SCALE) value = fromScale(value);
                    values.put(words[0], o == null ? value : o.clean(value));
                }
            } catch (IOException ignored) {
                // the defaults
            }
        }
        return values;
    }

    /** Writes a game's file as the game writes it (vr_options.cpp save_options), the scales as factors. */
    public static void write(File file, Map<String, String> values) throws IOException {
        File dir = file.getParentFile();
        if (dir != null && !dir.isDirectory() && !dir.mkdirs()) throw new IOException("cannot make " + dir);
        File temp = new File(file.getPath() + ".new");
        try (Writer out = new OutputStreamWriter(new FileOutputStream(temp), StandardCharsets.UTF_8)) {
            out.write("# Jak and Daxter Quest VR options: the launcher and the in-game VR menu write this.\n");
            for (Map.Entry<String, String> e : values.entrySet()) {
                Option o = find(e.getKey());
                String value = o == null ? e.getValue() : o.clean(e.getValue());
                if (o == WORLD_SCALE || o == THIRD_SCALE) value = toScale(value);
                out.write(e.getKey() + " " + value + "\n");
            }
        }
        if (file.exists() && !file.delete()) throw new IOException("cannot replace " + file);
        if (!temp.renameTo(file)) throw new IOException("cannot write " + file);
    }

    private static String fromScale(String factor) {
        try {
            return Integer.toString(Math.round(Float.parseFloat(factor) * 100f));
        } catch (NumberFormatException e) {
            return "100";
        }
    }

    private static String toScale(String percent) {
        try {
            return Float.toString(Integer.parseInt(percent) / 100f);
        } catch (NumberFormatException e) {
            return "1.0";
        }
    }

    /** The three games: their ids (OpenGOAL's) and names. */
    public static final String[] GAMES = {"jak1", "jak2", "jak3"};
    public static final String[] GAME_NAMES = {"Jak and Daxter: The Precursor Legacy", "Jak II", "Jak 3"};

    public static boolean hasGuns(String game) {
        return !"jak1".equals(game);
    }
}
