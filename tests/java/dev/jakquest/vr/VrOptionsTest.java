package dev.jakquest.vr;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

/**
 * Offline checks of VrOptions reading older options files (0.1.7, LIST-0.1.7 items 2, 4, 5 and 9): a fresh file's
 * starts (the crosshair Off under "crosshair-mark", the pole's third person and the eco sparks on, Gamepad only off), and
 * each older crosshair key as the game's vr_options.cpp reads it (0.1.6's Dot Off, any other shape kept):
 * java -cp classes;. dev.jakquest.vr.VrOptionsTest <scratch folder>
 */
public final class VrOptionsTest {
    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok   " : "FAIL ") + what);
        if (!ok) failed++;
    }

    static Map<String, String> readText(File dir, String name, String text) throws Exception {
        File f = new File(dir, name);
        Files.write(f.toPath(), text.getBytes(StandardCharsets.UTF_8));
        return VrOptions.read(f);
    }

    static String cross(File dir, String name, String text) throws Exception {
        return readText(dir, name, text).get(VrOptions.CROSSHAIR.key);
    }

    public static void main(String[] args) throws Exception {
        File dir = new File(args[0]);
        if (!dir.isDirectory() && !dir.mkdirs()) throw new IllegalStateException("cannot make " + dir);

        Map<String, String> fresh = VrOptions.read(new File(dir, "none.txt"));
        check("crosshair-mark".equals(VrOptions.CROSSHAIR.key), "the crosshair's key is crosshair-mark");
        check("0".equals(fresh.get("crosshair-mark")), "no file: the crosshair Off");
        check(VrOptions.isOn(fresh.get("pole-third-person")), "no file: Third person after a pole on");
        check(VrOptions.isOn(fresh.get("eco-sparks-first-person")), "no file: Eco sparks in first person on");
        check(!VrOptions.isOn(fresh.get("gamepad-only")), "no file: Gamepad only off");
        // 0.1.8 (LIST-0.1.8 items 5 and 6): the new toggles on to start, as the game's vr_options.h
        check(VrOptions.isOn(fresh.get("ride-gun")), "no file: Hold the gun while driving on");
        check(VrOptions.isOn(fresh.get("wheel-steer")), "no file: Steer by grabbing the wheel on");
        check("60".equals(fresh.get("wheel-turn")), "no file: Wheel turn 60 degrees");
        java.util.List<VrOptions.Option> controls = java.util.Arrays.asList(VrOptions.VR_CONTROLS);
        check(controls.contains(VrOptions.RIDE_GUN) && controls.contains(VrOptions.WHEEL_STEER)
                && controls.contains(VrOptions.WHEEL_TURN), "the new options are in the launcher's VR controls");

        check("0".equals(cross(dir, "s1.txt", "crosshair-shape 1\n")), "0.1.6's Dot (its start): Off");
        check("0".equals(cross(dir, "s0.txt", "crosshair-shape 0\n")), "0.1.6's Off: Off");
        check("3".equals(cross(dir, "s3.txt", "crosshair-shape 3\n")), "0.1.6's shape 3: kept");
        check("7".equals(cross(dir, "s7.txt", "crosshair-shape 7\n")), "0.1.6's shape 7: kept");
        check("1".equals(cross(dir, "k1.txt", "crosshair-kind 1\n")), "0.1.5's Dot (chosen; Off was its start): kept");
        check("0".equals(cross(dir, "k0.txt", "crosshair-kind 0\n")), "0.1.5's Off: Off");
        check("0".equals(cross(dir, "o1.txt", "crosshair 1\n")), "0.1.3's Dot (its start): Off");
        check("4".equals(cross(dir, "o4.txt", "crosshair 4\n")), "0.1.3's kind 4: kept");
        check("1".equals(cross(dir, "m1.txt", "crosshair-shape 5\ncrosshair-mark 1\n")),
                "0.1.7's own Dot kept over an older key");
        check("0".equals(cross(dir, "m0.txt", "crosshair-mark 0\ncrosshair-shape 5\n")),
                "0.1.7's own Off kept over an older key");

        Map<String, String> v = readText(dir, "r.txt",
                "crosshair-shape 3\npole-third-person off\neco-sparks-first-person off\ngamepad-only on\n");
        File out = new File(dir, "r-out.txt");
        VrOptions.write(out, v);
        String text = new String(Files.readAllBytes(out.toPath()), StandardCharsets.UTF_8);
        check(text.contains("\ncrosshair-mark 3\n"), "written back: crosshair-mark 3");
        check(!text.contains("crosshair-shape") && !text.contains("crosshair-kind") && !text.contains("\ncrosshair "),
                "written back: no older crosshair key");
        Map<String, String> again = VrOptions.read(out);
        check("3".equals(again.get("crosshair-mark")), "read again: crosshair 3");
        check(!VrOptions.isOn(again.get("pole-third-person")), "read again: Third person after a pole off");
        check(!VrOptions.isOn(again.get("eco-sparks-first-person")), "read again: Eco sparks in first person off");
        check(VrOptions.isOn(again.get("gamepad-only")), "read again: Gamepad only on");

        System.out.println(failed == 0 ? "all passed" : failed + " failed");
        System.exit(failed == 0 ? 0 : 1);
    }
}
