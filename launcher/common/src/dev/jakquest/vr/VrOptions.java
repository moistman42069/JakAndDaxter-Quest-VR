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

    /**
     * 0.1.4: this is the Quest's launcher (Android's virtual machine calls itself Dalvik, its vendor The Android
     * Project), not the PC's. The Quest starts with the owner's own Graphics (their Quest 3's vr-options.txt, 2026-11-16:
     * "make the graphics options i set in the launcher ... the new default too"), the PC with 0.1.3's, as the game's own
     * defaults (game/vr/vr_options.h).
     */
    public static final boolean QUEST =
            android(System.getProperty("java.vm.name", ""), System.getProperty("java.vendor", ""));

    static boolean android(String vm, String vendor) {
        return vm.toLowerCase(java.util.Locale.ROOT).contains("dalvik")
                || vendor.toLowerCase(java.util.Locale.ROOT).contains("android");
    }

    private static String onQuest(String quest, String pc) {
        return QUEST ? quest : pc;
    }

    /**
     * 0.1.6: a Quest 3 or 3S (Android's device names eureka and panther), which starts with the owner's own Graphics
     * (their Quest 3's vr-options.txt, 2026-11-17: "make sure the defaults in the log i sent you are the new ones on
     * quest 3 going forward. keep quest 2 at 100% if possible"; "for quest 3/3s, res in launcher should be 150%"): 150%
     * eyes and Strong sharpening (anti-aliasing and anisotropic filtering Off, as every headset's start: "anti
     * aliasing/anisotropic filtering should be off by default"). The Quest 2 and the others keep their eyes at 100% and
     * Normal sharpening (game/vr/vr_options.cpp strong_quest, the same).
     */
    public static final boolean STRONG_QUEST = QUEST && strongDevice(androidDevice());

    static boolean strongDevice(String device) {
        return "eureka".equals(device) || "panther".equals(device);
    }

    private static String androidDevice() {
        try {
            Object d = Class.forName("android.os.Build").getField("DEVICE").get(null);
            return d == null ? "" : d.toString();
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            return "";
        }
    }

    /** A default by the headset: a Quest 3 or 3S, another Quest, the PC. */
    private static String byHeadset(String strongQuest, String quest, String pc) {
        return STRONG_QUEST ? strongQuest : QUEST ? quest : pc;
    }

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
    // 0.1.6 (the owner: "for quest 3/3s, res in launcher should be 150%"): 150% on a Quest 3 or 3S (STRONG_QUEST), under
    // a new key (read(): their old start, 100%, is left behind once; any other choice, and the other headsets' and the
    // PC's, is kept)
    public static final Option RESOLUTION = atStart(choice("eye-resolution", "Eye resolution",
            "Of the headset's own recommended size. Higher is sharper and slower; the biggest effect on performance. "
                    + "150% to start on a Quest 3 or 3S, 100% elsewhere; Dynamic resolution draws the eyes smaller "
                    + "while the headset is not keeping up.",
            new String[] {"70%", "80%", "90%", "100%", "110%", "120%", "130%", "150%"},
            new String[] {"70", "80", "90", "100", "110", "120", "130", "150"}, byHeadset("150", "100", "100")));
    public static final Option REFRESH = atStart(choice("refresh-hz", "Refresh rate",
            "How many times a second the headset shows a frame. Higher is smoother and costs more. The headset's own "
                    + "is what it is set to in its settings.",
            new String[] {"The headset's own", "72 Hz", "80 Hz", "90 Hz", "120 Hz"},
            new String[] {"0", "72", "80", "90", "120"}, onQuest("120", "0")));
    // Off to start since 0.1.1 (owner: it flickered the title screen; from 0.1.1 it is off on the title and its menus);
    // its key changed from 0.1.0's "space-warp" (on to start then), so a file 0.1.0 wrote starts as this one does. On
    // the Quest on to start from 0.1.4 (the owner's), as High foveation, boost off, no anti-aliasing or anisotropic
    // filtering, Normal sharpening and 120 Hz (the most the headset has up to that).
    public static final Option SPACE_WARP = atStart(toggle("smoother-frames", "Smoother frames (AppSW)",
            "The Quest makes every other frame from how everything moved (Meta's Application SpaceWarp), so the game "
                    + "draws half of them, at half the headset's rate. Fast moves can smear a little at the edges of "
                    + "things. Off in menus and on the title screen. On a PC your headset's own smoothing does this.",
            QUEST));
    public static final Option FOVEATION = atStart(choice("foveation", "Foveated rendering",
            "Draws the edges of each eye's picture at a lower resolution, where the lenses blur it anyway, so the "
                    + "middle keeps its detail and the game runs faster. High saves the most. On a PC it does nothing.",
            new String[] {"Off", "Low", "Medium", "High", "Highest"}, new String[] {"0", "1", "2", "3", "4"},
            onQuest("3", "2")));
    // 0.1.6: on to start on the Quest too (the owner: "put cpu/gpu boost on by default"); its key changed with it, so
    // the Quest's "off" (its start from 0.1.4) is left behind once, a PC player's own off is kept (read()).
    public static final Option BOOST = atStart(toggle("cpu-gpu-boost-on", "CPU and GPU boost",
            "The Quest runs its processors at the highest speed it lets a game ask for, so fewer frames are missed. "
                    + "Uses more battery and the headset gets warmer. On a PC it does nothing.",
            true));
    public static final Option MAX_DRAW = atStart(toggle("max-draw-distance", "Max draw distance",
            "Everything drawn as far as you can see, at its highest detail: enemies, crates, people, effects, the "
                    + "level and the sea, so nothing pops in as you come near. Costs some frame rate. Off: the game's own "
                    + "distances.",
            true));
    // 0.1.6: Off to start on every headset, the PC too (the owner: "anti aliasing/anisotropic filtering should be off by
    // default"; the PC started at 2x and 16x until 0.1.5), under new keys (read(): any choice in an older file is kept)
    public static final Option MSAA = atStart(choice("anti-aliasing", "Anti-aliasing",
            "Smooths jagged edges. Off to start; 2x or 4x costs the graphics chip more with each step.",
            new String[] {"Off", "2x", "4x"}, new String[] {"1", "2", "4"}, "1"));
    public static final Option ANISOTROPY = atStart(choice("anisotropic-filtering", "Anisotropic filtering",
            "Keeps floors and walls sharp at a low angle. Off to start; 16x is the games' own.",
            new String[] {"Off", "2x", "4x", "8x", "16x"}, new String[] {"1", "2", "4", "8", "16"}, "1"));
    // ---- Performance (0.1.1, off to start then; 0.1.3, the owner: "do EVERYTHING that can boost performance and have
    // them as options in the launcher ... leave on the ones you see best for performance by default") ----
    // 0.1.3: Automatic to start, under a new key ("frame-rate" was 0.1.1's, 60 to start), so a file an older release
    // wrote starts at Automatic; a player's own The headset's or Half the headset's is kept (read).
    public static final Option FRAME_RATE = choice("game-frame-rate", "Game frame rate",
            "How often the game draws a new picture. Automatic: a new picture for every frame the headset shows while "
                    + "the game keeps up, and 60 a second while it does not (tried again now and then). 60 is how the "
                    + "games were made. The headset's: always the headset's rate, the smoothest and the most work. Half "
                    + "the headset's: half the work, and the headset turns each picture to your head in between.",
            new String[] {"Automatic", "60", "The headset's", "Half the headset's"}, new String[] {"3", "0", "1", "2"},
            "3");
    // 0.1.3: on to start, under a new key (0.1.1's "dynamic-resolution" was off to start)
    public static final Option DYNAMIC_RESOLUTION = toggle("dynamic-resolution-on", "Dynamic resolution",
            "The eyes are drawn a little smaller while the headset is not keeping up with the game, and back at "
                    + "full size when it is. Eye resolution is the size it starts from.",
            true);
    public static final Option SHARE_EYE_WORK = toggle("share-eye-work", "Share work between eyes",
            "The second eye uses what the processor worked out for the first eye's picture of the same frame (the "
                    + "colors of the level's light and what is in view), so each frame is less work. Looks the same.",
            true);
    public static final Option SHOW_PERFORMANCE = toggle("show-performance", "Show performance",
            "A small line low in your view: frames a second (of the headset's), the game's pictures a second, each "
                    + "picture's work in milliseconds (average / most) and the eyes' resolution.",
            false);
    public static final Option PS2_DETAIL = toggle("ps2-detail", "PS2 detail",
            "Far-off things, effects and shadows drawn as the PlayStation 2 drew them: faster, with some pop-in as "
                    + "you come near. While it is on, Max draw distance does nothing.",
            false);
    // 0.1.6: Strong on the Quest 3 and 3S (STRONG_QUEST), a new key as anti-aliasing's
    public static final Option SHARPEN = atStart(choice("sharpening-strength", "Sharpening",
            "The Quest sharpens the picture as it shows it, so a lower Eye resolution keeps its edges. On a PC it "
                    + "does nothing.",
            new String[] {"Off", "Normal", "Strong"}, new String[] {"0", "1", "2"}, byHeadset("2", "1", "0")));
    public static final Option[] GRAPHICS = {RESOLUTION, REFRESH, SPACE_WARP, FOVEATION, BOOST, MAX_DRAW, MSAA,
            ANISOTROPY, FRAME_RATE, DYNAMIC_RESOLUTION, SHARE_EYE_WORK, SHOW_PERFORMANCE, PS2_DETAIL, SHARPEN};

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
    // 0.1.7 (LIST-0.1.7 item 4; the owner: "make the new 3rd person pole swing toggle on by default")
    public static final Option POLE = toggle("pole-third-person", "Third person after a pole until you land",
            "After a pole swing the view stays third person until his feet are on the ground, so your aim does not "
                    + "jump in the air. Off: back in his eyes as soon as he lets go.", true);
    // 0.1.7 (LIST-0.1.7 item 2; the owner: "option to turn off eco effects in 1st person")
    public static final Option ECO_SPARKS = toggle("eco-sparks-first-person", "Eco sparks in first person",
            "The sparks eco throws off Jak's body, seen from his eyes too. Off: left out while you see through his eyes "
                    + "(they burst right at your eyes); the eco still works and sounds the same.", true);
    // 0.1.3 (the owner: "make that the new default"): on to start, under a new key, so a file an older release wrote
    // (where it was off to start) starts on too
    public static final Option VEHICLES = toggle("first-person-vehicles-on", "First person in vehicles",
            "Everything Jak rides seen from his eyes too, the view turning with it. Off: the game's camera around you "
                    + "there. With it on, each kind of ride below has its own switch as well.", true);
    // 0.1.5 (LIST-0.1.5 item 18; a player: "can you make vehicle types seperate 1st person toggles, for example hover
    // car, hover bike, lizard, hover board?"; the owner: keep First person in vehicles for everything): each kind of
    // ride on its own, on to start (game/vr/vr_options.h fp_*)
    private static Option ride(String key, String label, String what) {
        return toggle(key, label, "With First person in vehicles on: " + what + " from his seat. Off: the game's camera "
                + "around you there.", true);
    }
    public static final Option FP_ZOOMER = ride("first-person-zoomer", "Zoomer in first person", "Jak 1's Zoomer");
    public static final Option FP_FLUT = ride("first-person-flut-flut", "Flut Flut in first person",
            "Jak 1's Flut Flut");
    public static final Option FP_BOARD = ride("first-person-jet-board", "JET-Board in first person",
            "Jak II and 3's JET-Board (its rails and half-pipes too)");
    public static final Option FP_CARS = ride("first-person-hover-cars", "Hover cars in first person",
            "Jak II and 3's city cars");
    public static final Option FP_BIKES = ride("first-person-hover-bikes", "Hover bikes in first person",
            "Jak II and 3's city bikes and Jak II's race bikes");
    public static final Option FP_TITAN = ride("first-person-titan-suit", "Titan suit in first person",
            "Jak II's Titan suit");
    public static final Option FP_LEAPERS = ride("first-person-leaper-lizards", "Leaper lizards in first person",
            "Jak 3's leaper lizards");
    public static final Option FP_DESERT = ride("first-person-desert-vehicles", "Desert vehicles in first person",
            "Jak 3's wasteland vehicles");
    public static final Option FP_OTHER = ride("first-person-other-rides", "Other rides in first person",
            "slides, gun turrets, the glider, the sled and anything else");
    // 0.1.3 (the owner: "add options to adjust angle/positioning as needed"): the view in a vehicle, first person
    public static final Option VEHICLE_HEIGHT = number("vehicle-view-height", "Vehicle view height",
            "First person in a vehicle: raises or lowers the view from his eyes there. His body stays in his seat.",
            -50, 50, 5, " cm", 0);
    public static final Option VEHICLE_FORWARD = number("vehicle-view-forward", "Vehicle view forward",
            "First person in a vehicle: the view ahead of his eyes (towards where it goes) or behind them.",
            -50, 50, 5, " cm", 0);
    public static final Option VEHICLE_TILT = number("vehicle-view-tilt", "Vehicle view tilt",
            "First person in a vehicle: looking straight ahead you see this far down (more of the road, the board "
                    + "under you), or up with less than 0. 0 keeps the horizon level.",
            -30, 30, 5, " degrees", 0);
    // 0.1.3 (a player on Jak 3's leaper lizard: "shakes camera to point where you can't see where your going")
    public static final Option STEADY_RIDE = toggle("steady-ride-view", "Steady view when riding",
            "First person in a vehicle or on an animal (Jak 3's leaper lizards, the flut flut): the view stays in his "
                    + "seat, facing where the ride goes, without each bounce and shake of the ride. Off: the view "
                    + "follows his head's every bob.", true);
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
    public static final Option[] VR_BODY = {VIEW, CUTSCENES, ADAPTIVE, TALKS, POLE, ECO_SPARKS, VEHICLES, FP_ZOOMER, FP_FLUT, FP_BOARD,
            FP_CARS, FP_BIKES, FP_TITAN, FP_LEAPERS, FP_DESERT, FP_OTHER, VEHICLE_HEIGHT, VEHICLE_FORWARD,
            VEHICLE_TILT, STEADY_RIDE, SEATED, VIEW_HEIGHT, WORLD_SCALE, THIRD_SCALE, BODY,
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
    // 0.1.8 (LIST-0.1.8 item 6; the owner: physical driving, "a toggle that is on by default. people can still use
    // normal inputs to drive, but once they grab the wheel on whichever vehicle, that will allow them to use it to
    // steer"; game/vr/jak_vr.cpp steer_wheel)
    public static final Option WHEEL_STEER = toggle("wheel-steer", "Steer by grabbing the wheel",
            "In any vehicle (the Zoomer, every bike, car and desert vehicle, the glider, the sled), grip its wheel, "
                    + "handlebars or bar where Jak's hands are (or just ahead of you) and turn your hands to steer: "
                    + "as a wheel, tilted any way, or as handlebars (push one hand, pull the other). The stick still "
                    + "steers too. A quick squeeze is still the grip's own button, squeezed again and held it holds "
                    + "it, and while a hand holds on, its stick's click is that button (Jak 3's turbo).", true);
    public static final Option WHEEL_TURN = number("wheel-turn", "Wheel turn",
            "How far you turn the wheel for full steering. Handlebars and bars need a little over half of it.", 20,
            180, 10, " degrees", 60);
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
            "Hold the right grip to make fists: only fists hit, so moving your hands never hits by accident. The left "
                    + "grip makes the left fist too. On foot a grip's quick squeeze and let go, or its stick's click, is "
                    + "its old button: the right one the JET-Board, the left one Dark Jak. Jak 3's Light Jak: hold the "
                    + "left grip and press Y, then let Y go to heal, or press X (slow time) or A (wings), or hold B "
                    + "(the shield, up while B stays held), or push the right stick.",
            true);
    // 0.1.7 (LIST-0.1.7 item 9; a tester: "is there a way to implement just the gamepad"): off to start
    public static final Option GAMEPAD_ONLY = toggle("gamepad-only", "Gamepad only (no VR controllers)",
            "Play with a gamepad alone: the VR controllers are left out of the game (their buttons, sticks, gestures "
                    + "and hands); the headset still turns the view. The in-game VR menu takes both, from the game's "
                    + "pause menu (VR Options).", false);
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
    // 0.1.8 (LIST-0.1.8 item 5; a tester: "i miss being able to hold gun while drivng"; the owner: on to start)
    public static final Option RIDE_GUN = guns(toggle("ride-gun", "Hold the gun while driving",
            "In Jak II and 3's city bikes and cars (where the game lets Jak keep his gun), grip over your shoulder to "
                    + "draw the gun and hold it in your hand while you drive; the trigger shoots. The grip at your "
                    + "hip stays the vehicle's own button there.", true));
    public static final Option GUN_ANGLE = guns(number("gun-angle", "Gun angle",
            "The gun tilted up or down from the controller.", -45, 45, 5, " degrees", 0));
    // 0.1.3 the gun's crosshair (LIST-0.1.3 item 27; game/vr/crosshair.h, the in-game VR menu's same rows). 0.1.5
    // (LIST-0.1.5 items 2 and 3): Off to start, under a new key (0.1.3's "crosshair" started with the Dot: read keeps
    // a kind a player chose), and the Small circle. 0.1.6 (the owner: "lets turn the dot crosshair on by default again
    // for this release"): the Dot to start, under a new key again, "crosshair-shape" (read: 0.1.5's Off start is the
    // Dot, any other kind a player chose is kept, and 0.1.3's and 0.1.4's own Off). 0.1.7 (LIST-0.1.7 item 5, the
    // owner: "make it so crosshair is off by default again this update"): Off to start, under a new key again,
    // "crosshair-mark" (read: the Dot of 0.1.6, 0.1.3 and 0.1.4, their start, is Off; any other shape chosen is kept).
    public static final Option CROSSHAIR = guns(choice("crosshair-mark", "Crosshair",
            "A mark on whatever your shot would hit first, the same size however far. First person, aiming with your hand.",
            new String[] {"Off", "Dot", "Cross", "Cross and dot", "Circle", "Circle and dot", "Plus", "Small circle"},
            new String[] {"0", "1", "2", "3", "4", "5", "6", "7"}, "0"));
    public static final Option CROSSHAIR_SIZE = guns(number("crosshair-size", "Crosshair size",
            "How big the crosshair looks. It stays that size near or far.", 50, 300, 25, "%", 100));
    public static final Option CROSSHAIR_COLOR = guns(choice("crosshair-color", "Crosshair color",
            "The crosshair's color. It has a dark edge so it shows on anything.",
            new String[] {"White", "Green", "Yellow", "Cyan", "Red", "Pink"},
            new String[] {"0", "1", "2", "3", "4", "5"}, "0"));
    public static final Option CROSSHAIR_OPACITY = guns(number("crosshair-opacity", "Crosshair opacity",
            "How solid the crosshair is. Less lets you see through it.", 20, 100, 10, "%", 100));
    public static final Option CROSSHAIR_ENEMY = guns(toggle("crosshair-enemy", "Crosshair red on enemies",
            "The crosshair turns red on an enemy your gun can reach (yellow if yours is red).", true));
    public static final Option VIBRATION = toggle("vibration", "Vibration", "The controllers buzz.", true);
    public static final Option VIBRATION_STRENGTH = number("vibration-strength", "Vibration strength",
            "How hard every buzz is.", 25, 200, 25, "%", 100);
    public static final Option WORLD_VIBRATION = toggle("world-vibration", "Vibration on walls and floors",
            "A buzz as a hand meets a wall or the floor, harder the harder you press.", true);
    public static final Option DPAD_BUZZ = toggle("dpad-buzz", "Buzz on d-pad presses",
            "A quick pulse in the stick's hand each time a d-pad direction goes in.", true);
    public static final Option GUN_SWITCH_BUZZ = guns(toggle("gun-switch-buzz", "Buzz on gun switch",
            "A quick pulse in the gun's hand as you change guns.", true));
    // 0.1.4 (LIST-0.1.4 item 1; game/vr/vr_options.h extra_vibration, the in-game VR menu's same row). 0.1.6: the whole
    // world to start (the owner: "make vibarations on everything the new default"), its key changed with it (read():
    // a player's Off is kept, Pickups, the start then, becomes the whole world).
    public static final Option EXTRA_VIBRATION = choice("extra-vibration-kind", "Extra vibration",
            "Pickups: a buzz for each orb, eco, cell and the like you collect. The whole world: also each step, jump,"
            + " landing, roll, punch and kick, each hit, and the world's shakes.",
            new String[] {"Off", "Pickups", "The whole world"}, new String[] {"0", "1", "2"}, "2");
    public static final Option[] VR_CONTROLS = {GAMEPAD_ONLY, TURN, SNAP, SMOOTH, MOVE, WHEEL_STEER, WHEEL_TURN, DPAD, DPAD_REACH, DPAD_BUZZ, MENU_HAND,
            STICK_CLICK_MENU, MELEE, MELEE_GRIP, MELEE_SPEED, SPREAD_SPIN_KICK, HAND_AIM, TWO_HAND, HOLSTER, HOLSTER_SIZE,
            HOLSTER_ON_FOOT, RIDE_GUN, GUN_ANGLE, CROSSHAIR, CROSSHAIR_SIZE, CROSSHAIR_COLOR, CROSSHAIR_OPACITY, CROSSHAIR_ENEMY,
            VIBRATION, VIBRATION_STRENGTH, WORLD_VIBRATION, EXTRA_VIBRATION, GUN_SWITCH_BUZZ};

    /** The launcher's tabs of options, in order. */
    public static final String[] TAB_NAMES = {"Graphics", "VR Body", "VR Controls"};
    public static final Option[][] TABS = {GRAPHICS, VR_BODY, VR_CONTROLS};

    // ---- Mods: built in, each with its switch (the launchers' Mods) ----

    /**
     * Jak 1's multiplayer (game/party.h): Teamruns' game side with a party of our own. Jak 1's file only. On to start
     * (owner, 2026-11-14): it changes nothing in the game until the player hosts or joins a game.
     */
    public static final Option MULTIPLAYER = toggle("multiplayer", "Jak 1 multiplayer",
            "Jak 1: play together, built on Teamruns, the community's Jak 1 multiplayer. One player hosts and the others "
            + "join, on this network or through a party server: their Jaks are in your game, and what one takes, "
            + "everyone has. In the game: Multiplayer on the title or in the pause menu, to host, join or browse the "
            + "games. Until you do, it changes "
            + "nothing in the game; turn it off to keep it out altogether.", true);
    /**
     * 0.1.3 (LIST-0.1.3 item 15): Jak II's multiplayer (the community's Jak II multiplayer mod, game/multiplayer/jak2),
     * the same "multiplayer" key in Jak II's own file. On to start; off (owner, 2026-11-16: "just to make it simple in
     * case of conflicts"), the game starts nothing of it and its title and menus are the game's own.
     */
    public static final Option MULTIPLAYER_JAK2 = toggle("multiplayer", "Jak II multiplayer",
            "Jak II: play the story together, built on the community's Jak II multiplayer mod (an early one: its own "
            + "makers call it a beta). One player hosts and the others join, on this network or by the host's address: "
            + "their Jaks are in your game, with their VR head and hands. In the game: Multiplayer on the title, for "
            + "Host Game and Join Game. Until you host or join, it changes nothing in the game; turned off, nothing of "
            + "it runs at all. Turn it off if anything seems wrong with it on.", true);
    /** Only for reading and writing a file: the key is the same in each game's file (MULTIPLAYER_SWITCHES has both). */
    public static final Option[] MODS = {MULTIPLAYER};
    /** Jak 1's party.txt sits beside its file. */
    public static final String MULTIPLAYER_GAME = "jak1";
    /** The Mods page's multiplayer switches, each in its game's own file: MULTIPLAYER_GAMES[i]. */
    public static final Option[] MULTIPLAYER_SWITCHES = {MULTIPLAYER, MULTIPLAYER_JAK2};
    public static final String[] MULTIPLAYER_GAMES = {"jak1", "jak2"};

    /** The multiplayer switch of this game (jak1, jak2), or null where there is none (jak3). */
    public static Option multiplayerOf(String game) {
        for (int i = 0; i < MULTIPLAYER_GAMES.length; i++) {
            if (MULTIPLAYER_GAMES[i].equals(game)) return MULTIPLAYER_SWITCHES[i];
        }
        return null;
    }

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
            "Wherever the game stutters: 80% eyes, high foveation, the game's own distances, no anti-aliasing.",
            "The defaults for this headset (a Quest 3 or 3S: 150% eyes and Strong sharpening; a Quest 2: its eyes "
                    + "at 100%; anti-aliasing and anisotropic filtering off), and most PCs.",
            "A strong PC: 120% eyes, 4x anti-aliasing, low foveation."};

    public static Map<String, String> preset(int which) {
        Map<String, String> m = new LinkedHashMap<>();
        switch (which) {
            case 0:
                m.put(RESOLUTION.key, "80");
                m.put("foveation", "3");
                m.put("max-draw-distance", "off");
                m.put(MSAA.key, "1");
                m.put(ANISOTROPY.key, "1");  // 0.1.6: Off, as every headset's start now
                // (Smoother frames is left as the player has it: off to start since 0.1.1, as it flickers some screens)
                m.put(BOOST.key, "on");
                break;
            case 2:
                m.put(RESOLUTION.key, "120");
                m.put("foveation", "1");
                m.put("max-draw-distance", "on");
                m.put(MSAA.key, "4");
                m.put(ANISOTROPY.key, "16");
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
        boolean gameRate = false;  // 0.1.3: "game-frame-rate" in the file (else 0.1.1's "frame-rate" 1 or 2 is kept)
        boolean crossKind = false;  // 0.1.7: "crosshair-mark" in the file (else 0.1.6's, 0.1.5's or 0.1.3's: below)
        boolean extraKind = false;  // 0.1.6: "extra-vibration-kind" in the file (else 0.1.4's Off is kept)
        boolean boostOn = false;    // 0.1.6: "cpu-gpu-boost-on" in the file (else a PC's own off is kept)
        // 0.1.6: anti-aliasing, anisotropic filtering, sharpening and Eye resolution under new keys (the Quest 3's new
        // start)
        final Option[] gfx = {MSAA, ANISOTROPY, SHARPEN, RESOLUTION};
        final String[] oldGfx = {"msaa", "anisotropy", "sharpening", "resolution"};
        // the old start, left behind for the new (vr_options.cpp load_options, the same): every Quest's in 0.1.4 and
        // 0.1.5, the PC's up to 0.1.5 (2x and 16x; Off and Off now, the owner: "anti aliasing/anisotropic filtering
        // should be off by default")
        final String[] oldStart = QUEST ? new String[] {"1", "1", "1", "100"} : new String[] {"2", "16", "0", "100"};
        boolean[] newGfx = new boolean[4];
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
                    if (o == FRAME_RATE) gameRate = true;
                    if (o == CROSSHAIR) crossKind = true;
                    if (o == EXTRA_VIBRATION) extraKind = true;
                    if (o == BOOST) boostOn = true;
                    for (int g = 0; g < gfx.length; g++) if (o == gfx[g]) newGfx[g] = true;
                }
            } catch (IOException ignored) {
                // the defaults
            }
        }
        String oldRate = values.get("frame-rate");
        if (!gameRate && ("1".equals(oldRate) || "2".equals(oldRate))) values.put(FRAME_RATE.key, oldRate);
        // 0.1.6's "crosshair-shape" (the Dot to start then): a shape the player chose is kept, its Dot is Off now; with
        // none, 0.1.5's "crosshair-kind" (Off to start then): a kind the player chose is kept; with none, 0.1.3's and
        // 0.1.4's "crosshair" (the Dot to start then): a kind the player chose is kept, the Dot is Off now
        // (vr_options.cpp load_options, the same)
        String shapeCross = values.get("crosshair-shape");
        String kindCross = values.get("crosshair-kind");
        String oldCross = values.get("crosshair");
        if (!crossKind) {
            if (shapeCross != null) {
                if (shapeCross.matches("[2-7]")) values.put(CROSSHAIR.key, shapeCross);
            } else if (kindCross != null) {
                if (kindCross.matches("[1-7]")) values.put(CROSSHAIR.key, kindCross);
            } else if (oldCross != null && oldCross.matches("[2-6]")) {
                values.put(CROSSHAIR.key, oldCross);
            }
        }
        values.remove("crosshair-shape");  // the game reads them no more
        values.remove("crosshair-kind");
        values.remove("crosshair");
        if (!extraKind && "0".equals(values.get("extra-vibration"))) values.put(EXTRA_VIBRATION.key, "0");
        values.remove("extra-vibration");
        String oldBoost = values.get("cpu-gpu-boost");
        if (!boostOn && !QUEST && oldBoost != null && !"on".equals(oldBoost) && !"1".equals(oldBoost)
                && !"true".equals(oldBoost)) {
            values.put(BOOST.key, "off");
        }
        values.remove("cpu-gpu-boost");
        for (int g = 0; g < gfx.length; g++) {
            String old = values.remove(oldGfx[g]);
            if (newGfx[g] || old == null) continue;
            if (oldStart[g].equals(old)) continue;  // the old start: the new one
            values.put(gfx[g].key, gfx[g].clean(old));
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
