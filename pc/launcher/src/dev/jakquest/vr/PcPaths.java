// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
// After Ratchet & Clank Quest VR's PcPaths (the same author's project).
package dev.jakquest.vr;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Where everything lives on the PC. What came in the release's folder (the launcher, and in game\ OpenGOAL's gk and
 * extractor with the three games' sources) is read from it and never written: the player's set-up (each game made from
 * their own disc), saves and choices are in %LOCALAPPDATA%\JakQuestVR, so an update replaces the release's folder and
 * keeps them. The logs go to Downloads\JakQuestVR, as the headset's go to its Download folder.
 *
 * JAKQUESTVR_HOME, JAKQUESTVR_INSTALL and JAKQUESTVR_LOGS move them, for the tests.
 */
final class PcPaths {
    static final String EXE = "JakDaxterVR.exe";

    final File install;
    final File home;
    final File logs;

    PcPaths() {
        install = installFolder();
        home = homeFolder();
        logs = logsFolder(home);
    }

    PcPaths(File install, File home, File logs) {
        this.install = install;
        this.home = home;
        this.logs = logs;
    }

    private static File installFolder() {
        String given = System.getenv("JAKQUESTVR_INSTALL");
        if (given != null && !given.isEmpty()) {
            return new File(given).getAbsoluteFile();
        }
        // The release's launcher (jpackage's) says where its .exe is.
        String app = System.getProperty("jpackage.app-path");
        if (app != null && !app.isEmpty()) {
            return new File(app).getAbsoluteFile().getParentFile();
        }
        // Else the jar's folder's parent: <install>/app/launcher.jar.
        try {
            File jar = new File(PcPaths.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return jar.getParentFile().getParentFile();
        } catch (URISyntaxException | RuntimeException e) {
            return new File(".").getAbsoluteFile();
        }
    }

    private static File homeFolder() {
        String given = System.getenv("JAKQUESTVR_HOME");
        if (given != null && !given.isEmpty()) {
            return new File(given).getAbsoluteFile();
        }
        String local = System.getenv("LOCALAPPDATA");
        if (local == null || local.isEmpty()) {
            local = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local";
        }
        return new File(local, "JakQuestVR");
    }

    private static File logsFolder(File home) {
        String given = System.getenv("JAKQUESTVR_LOGS");
        if (given != null && !given.isEmpty()) {
            return new File(given).getAbsoluteFile();
        }
        File downloads = new File(System.getProperty("user.home"), "Downloads");
        return downloads.isDirectory() ? new File(downloads, "JakQuestVR") : new File(home, "logs");
    }

    // ---- What came with the release ----

    /** OpenGOAL's programs as built for this port: gk.exe (the game) and extractor.exe (the set-up). */
    File gameFolder() { return new File(install, "game"); }
    File gk() { return new File(gameFolder(), "gk.exe"); }
    File extractor() { return new File(gameFolder(), "extractor.exe"); }
    /** The three games' sources and the decompiler's settings, copied into the player's data folder to build from. */
    File template() { return new File(gameFolder(), "data"); }
    /** The folders of template() a game is built from (Precursor's installer bundles the same). */
    static final String[] TEMPLATE_PARTS = {"goal_src", "decompiler", "game", "custom_assets"};
    File texts() { return new File(install, "texts"); }

    // ---- The player's ----

    /** OpenGOAL's project folder (--proj-path): the sources, each game's extracted disc and its compiled game. */
    File data() { return new File(home, "data"); }
    /** gk's --config-path: OpenGOAL\<game>\settings (the game's settings, vr-options.txt) and saves. */
    File config() { return new File(home, "config"); }
    File vrOptions(String game) { return VrOptions.file(config(), game); }
    /** The extracted disc (kept, so an update only compiles again) and the compiled game. */
    File extracted(String game) { return new File(data(), "iso_data/" + game); }
    File compiled(String game) { return new File(data(), "out/" + game); }
    /** What the set-up writes when a game is ready: the release version it was built with. */
    File readyMark(String game) { return new File(data(), "ready-" + game + ".txt"); }
    /** gk's and the extractor's own logs (OpenGOAL writes them under the project folder). */
    File gameLogs() { return new File(data(), "log"); }
    File updates() { return new File(home, "updates"); }
    /** Texture packs the player added (TexturePacks), each game's in its own folder. */
    File texturePacks() { return new File(home, "texture-packs"); }
    File prefs(String name) { return new File(home, name + ".properties"); }
    File launcherLog() { return new File(home, "launcher.log"); }

    /**
     * What gk and the extractor run with: this PC's own environment, less what would turn the game into the simulated
     * headset's tests, with JAKVR=1 (the game draws in the headset on PC).
     */
    Map<String, String> environment() {
        Map<String, String> env = new LinkedHashMap<>(System.getenv());
        for (String key : new String[] {"QUEST_SIM", "QUEST_SIM_SCRIPT", "QUEST_SIM_OUT", "JAKVR_TEST_GUN",
                "JAKVR_BODY_TRACE", "JAKVR_CRASH_DUMP", "JAKVR_HIDE_TEST"}) {
            env.keySet().removeIf(name -> name.equalsIgnoreCase(key));
        }
        env.put("JAKVR", "1");
        return env;
    }

    static boolean inside(File file, File folder) {
        try {
            String base = folder.getCanonicalPath();
            String path = file.getCanonicalPath();
            return path.equals(base) || path.startsWith(base + File.separator);
        } catch (IOException e) {
            return false;
        }
    }

    /** A text file, "" when it cannot be read. */
    static String read(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    static void deleteRecursively(File file) {
        if (file == null) {
            return;
        }
        if (Files.isSymbolicLink(file.toPath())) {
            file.delete();
            return;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }
}
