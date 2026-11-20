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
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Where everything lives on the PC. What came in the release's folder (the launcher, and in game\ OpenGOAL's gk and
 * extractor with the three games' sources) is read from it and never written: the player's set-up (each game made from
 * their own disc), saves and choices are in %LOCALAPPDATA%\JakQuestVR, so an update replaces the release's folder and
 * keeps them. The logs go to Downloads\JakQuestVR, as the headset's go to its Download folder. Each Place (the games'
 * set-up, the biggest by far; the texture packs; the saves and options; the logs) can be kept on another drive or in
 * another folder: the player's choice is its own <name>-folder.txt in home, which stays (PcDataFolder moves them; the
 * games since 0.1.3, the rest since 0.1.8).
 *
 * JAKQUESTVR_HOME, JAKQUESTVR_INSTALL and JAKQUESTVR_LOGS move them, for the tests.
 */
final class PcPaths {
    static final String EXE = "JakDaxterVR.exe";

    /**
     * What a player can keep in another place (the Play tab's "Where your files are kept"; LIST-0.1.8 item 8): each
     * with the file in home that says where (none: its default place) and the folder made in the one the player picks.
     */
    enum Place {
        GAMES("The games", "the games", "data-folder.txt", "JakQuestVR"),
        PACKS("Your texture packs", "your texture packs", "packs-folder.txt", "JakQuestVR-texture-packs"),
        SAVES("Your saves and options", "your saves and options", "saves-folder.txt", "JakQuestVR-saves"),
        LOGS("The logs", "the logs", "logs-folder.txt", "JakQuestVR-logs");

        /** What it is, as a sentence starts ("The games are kept in ...") and inside one. */
        final String words;
        final String inWords;
        /** The player's choice, in home. */
        final String choice;
        /** The folder made in the one the player picks (unless the picked one is already named so). */
        final String folderName;

        Place(String words, String inWords, String choice, String folderName) {
            this.words = words;
            this.inWords = inWords;
            this.choice = choice;
            this.folderName = folderName;
        }

        /** The note in home of a move whose old copy is still being removed (PcDataFolder.finishLeftover). */
        String leftover() { return choice.replace(".txt", "-leftover.txt"); }
    }

    final File install;
    final File home;
    /** Where the logs go unless the player chose another place (logs()). */
    private final File defaultLogs;
    /** Where the player chose to keep each Place (its choice file in home), null for its default place. */
    private final AtomicReferenceArray<File> chosen = new AtomicReferenceArray<>(Place.values().length);

    PcPaths() {
        install = installFolder();
        home = homeFolder();
        defaultLogs = logsFolder(home);
        readChoices();
    }

    PcPaths(File install, File home, File logs) {
        this.install = install;
        this.home = home;
        this.defaultLogs = logs;
        readChoices();
    }

    private void readChoices() {
        for (Place p : Place.values()) {
            chosen.set(p.ordinal(), readChosen(new File(home, p.choice)));
        }
    }

    private static File readChosen(File choice) {
        String text = read(choice).replace("﻿", "").trim();
        if (text.isEmpty()) {
            return null;
        }
        File folder = new File(text);
        return folder.isAbsolute() ? folder : null;
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

    /** Where `place` is kept: where the player chose (the logs: see logs()), else its default place. */
    File folder(Place place) {
        if (place == Place.LOGS) {
            return logs();
        }
        File c = chosen.get(place.ordinal());
        return c != null ? c : defaultFolder(place);
    }

    File defaultFolder(Place place) {
        switch (place) {
            case GAMES: return new File(home, "data");
            case PACKS: return new File(home, "texture-packs");
            case SAVES: return new File(home, "config");
            default: return defaultLogs;
        }
    }

    /** Whether `place` is kept somewhere the player chose. */
    boolean moved(Place place) { return chosen.get(place.ordinal()) != null; }

    /** The folder the player chose for `place`, there or not; null for its default place. */
    File chosenFolder(Place place) { return chosen.get(place.ordinal()); }

    /** Keeps `place` in `folder` from now on (its default place: no choice saved). Moves nothing. */
    void choose(Place place, File folder) throws IOException {
        File choice = new File(home, place.choice);
        if (folder == null || PcDataFolder.same(folder, defaultFolder(place))) {
            Files.deleteIfExists(choice.toPath());
            chosen.set(place.ordinal(), null);
            return;
        }
        File absolute = folder.getAbsoluteFile();
        Files.createDirectories(home.toPath());
        File written = new File(home, place.choice + ".new");
        Files.write(written.toPath(), (absolute.getPath() + "\n").getBytes(StandardCharsets.UTF_8));
        Files.move(written.toPath(), choice.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        chosen.set(place.ordinal(), absolute);
    }

    /**
     * OpenGOAL's project folder (--proj-path): the sources, each game's extracted disc and its compiled game. In home
     * unless the player keeps it elsewhere (the Play tab's "Where your files are kept").
     */
    File data() { return folder(Place.GAMES); }
    File defaultData() { return defaultFolder(Place.GAMES); }
    /** Whether the games' set-up is kept somewhere the player chose. */
    boolean dataMoved() { return moved(Place.GAMES); }
    /** Keeps the games' set-up in `folder` from now on (the default place: no choice saved). Moves nothing. */
    void chooseData(File folder) throws IOException { choose(Place.GAMES, folder); }

    /**
     * Where the logs and the Logs tab's zips go: Downloads\JakQuestVR, or the folder the player chose (0.1.8). A chosen
     * folder on a drive that is not there (unplugged) gives the default place meanwhile: a log always has a place.
     */
    File logs() {
        File c = chosen.get(Place.LOGS.ordinal());
        if (c == null || c.isDirectory()) {
            return c != null ? c : defaultLogs;
        }
        java.nio.file.Path root = c.toPath().getRoot();
        return root != null && root.toFile().isDirectory() ? c : defaultLogs;
    }

    /** gk's --config-path: OpenGOAL\<game>\settings (the game's settings, vr-options.txt) and saves. */
    File config() { return folder(Place.SAVES); }
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
    File texturePacks() { return folder(Place.PACKS); }
    /**
     * The mod lists' copies from before (ModList), and where a mod's file is downloaded before it is added: with the
     * texture packs when they are kept elsewhere (a pack's download, up to 2 GB, on the drive the player chose for them).
     */
    File modLists() { return new File(home, "mod-lists"); }
    File modDownloads() {
        return moved(Place.PACKS) ? new File(texturePacks(), "downloads") : new File(home, "mod-downloads");
    }
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
