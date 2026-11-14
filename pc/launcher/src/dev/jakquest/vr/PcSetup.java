// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
package dev.jakquest.vr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Each game made from the player's own disc image, as OpenGOAL's launcher and Precursor's installer do it: the
 * extractor (the release's game\extractor.exe) extracts the disc, decompiles it and compiles the game with this port's
 * sources, into the player's data folder. The extracted disc is kept there, so when an update brings new sources the
 * game is only built again, without the disc. One at a time; each run's whole output goes to the logs folder.
 */
final class PcSetup {
    /** Where it is; written by the set-up's thread, read by the launcher's page. */
    static final class State {
        volatile boolean running;
        volatile String game;      // the game being set up
        volatile int percent = -1; // the build's percent, -1 before it
        volatile String phase = "";
        volatile String line = "";
        volatile String failure;   // why the last one stopped, in the player's words
        volatile Process process;
        volatile boolean stopped;
    }

    static final State STATE = new State();

    enum Status { NONE, READY, OUTDATED }

    private PcSetup() {}

    /**
     * NONE: not set up; READY: built with this release's sources and the texture packs on now; OUTDATED: built with an
     * older release's, or other texture packs (newSources says which).
     */
    static Status status(PcPaths paths, String game) {
        File mark = paths.readyMark(game);
        if (!mark.isFile() || !new File(paths.compiled(game), "iso").isDirectory()) {
            return Status.NONE;
        }
        return !newSources(paths, game) && !TexturePacks.changed(paths.texturePacks(), game, paths.data())
            ? Status.READY : Status.OUTDATED;
    }

    /** The game was built with an older release's sources. */
    static boolean newSources(PcPaths paths, String game) {
        return !PcBuild.VERSION.equals(PcPaths.read(paths.readyMark(game)).trim());
    }

    /** Sets `game` up from the disc image `iso` (null: build again from the disc extracted before). */
    static void start(PcPaths paths, String game, File iso, Runnable changed) {
        if (STATE.running) {
            return;
        }
        STATE.running = true;
        STATE.game = game;
        STATE.percent = -1;
        STATE.phase = iso != null ? "Reading your disc" : "Building the game again with this version";
        STATE.line = "";
        STATE.failure = null;
        STATE.stopped = false;
        new Thread(() -> {
            try {
                run(paths, game, iso, changed);
            } catch (Exception e) {
                STATE.failure = STATE.stopped ? "Stopped. It starts again from the beginning next time."
                    : e.getMessage();
            } finally {
                STATE.process = null;
                STATE.running = false;
                changed.run();
            }
        }, "setup-" + game).start();
    }

    static void stop() {
        STATE.stopped = true;
        Process p = STATE.process;
        if (p != null) {
            p.descendants().forEach(ProcessHandle::destroyForcibly);
            p.destroyForcibly();
        }
    }

    private static final Pattern PERCENT = Pattern.compile("\\[\\s*(\\d{1,3})%\\]");

    private static void run(PcPaths paths, String game, File iso, Runnable changed) throws IOException,
            InterruptedException {
        if (!paths.extractor().isFile()) {
            throw new IOException("The release's game\\extractor.exe is missing: unzip the release again.");
        }
        if (iso == null && !paths.extracted(game).isDirectory()) {
            throw new IOException("The disc extracted before is gone: set the game up from your disc image again.");
        }
        refreshSources(paths);
        // the texture packs on, into custom_assets for the decompiler (the game built again picks them up)
        STATE.phase = "Putting your texture packs in";
        changed.run();
        TexturePacks.apply(paths.texturePacks(), game, paths.data());
        paths.logs.mkdirs();
        String stamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File log = new File(paths.logs, "setup-" + game + "-" + stamp + ".log");
        List<String> command = new ArrayList<>();
        command.add(paths.extractor().getPath());
        command.add("-g");
        command.add(game);
        if (iso != null) {
            command.add("-e");
        } else {
            command.add("-f");
        }
        command.add("-d");
        command.add("-c");
        command.add("--proj-path");
        command.add(paths.data().getPath());
        command.add(iso != null ? iso.getPath() : paths.extracted(game).getPath());
        Files.deleteIfExists(paths.readyMark(game).toPath());
        ProcessBuilder builder = new ProcessBuilder(command).directory(paths.gameFolder()).redirectErrorStream(true);
        builder.environment().clear();
        builder.environment().putAll(paths.environment());
        int code;
        try (PrintWriter out = new PrintWriter(new FileOutputStream(log), true, StandardCharsets.UTF_8)) {
            out.println("Jak and Daxter VR " + PcBuild.VERSION + ": set-up of " + game);
            out.println(String.join(" ", command));
            Process process = builder.start();
            STATE.process = process;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(),
                    StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null) {
                    out.println(line);
                    String plain = line.replaceAll("\u001b\\[[0-9;]*m", "").trim();
                    if (plain.isEmpty()) {
                        continue;
                    }
                    Matcher m = PERCENT.matcher(plain);
                    if (m.find()) {
                        STATE.phase = "Building the game";
                        STATE.percent = Math.min(100, Integer.parseInt(m.group(1)));
                    } else if (plain.toLowerCase().contains("decompil")) {
                        STATE.phase = "Decompiling the disc's code and art";
                    } else if (plain.toLowerCase().contains("extract")) {
                        STATE.phase = "Extracting your disc";
                    }
                    STATE.line = plain.length() > 160 ? plain.substring(0, 160) + "..." : plain;
                    changed.run();
                }
            }
            code = process.waitFor();
            out.println("exit " + code);
        }
        if (STATE.stopped) {
            throw new IOException("Stopped.");
        }
        if (code != 0) {
            throw new IOException("The set-up stopped (" + code + "): " + STATE.line + " The whole of it is in " + log
                + ". Is the disc image " + VrOptions.GAME_NAMES[index(game)] + "?");
        }
        Files.write(paths.readyMark(game).toPath(), PcBuild.VERSION.getBytes(StandardCharsets.UTF_8));
    }

    /** The release's sources copied into the data folder when this version's are not there yet (an update). */
    private static void refreshSources(PcPaths paths) throws IOException {
        File mark = new File(paths.data(), "sources-version.txt");
        if (PcBuild.VERSION.equals(PcPaths.read(mark).trim())) {
            return;
        }
        if (!paths.template().isDirectory()) {
            throw new IOException("The release's game\\data folder is missing: unzip the release again.");
        }
        STATE.phase = "Copying this version's game sources";
        paths.data().mkdirs();
        for (String part : PcPaths.TEMPLATE_PARTS) {
            File from = new File(paths.template(), part);
            File to = new File(paths.data(), part);
            PcPaths.deleteRecursively(to);
            if (from.isDirectory()) {
                copyTree(from, to);
            }
        }
        Files.write(mark.toPath(), PcBuild.VERSION.getBytes(StandardCharsets.UTF_8));
    }

    private static void copyTree(File from, File to) throws IOException {
        if (!to.isDirectory() && !to.mkdirs()) {
            throw new IOException("cannot make " + to);
        }
        File[] children = from.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            File target = new File(to, child.getName());
            if (child.isDirectory()) {
                copyTree(child, target);
            } else {
                Files.copy(child.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /** Removes a game's set-up (its extracted disc and compiled game); its saves and settings stay. */
    static void remove(PcPaths paths, String game) {
        PcPaths.deleteRecursively(paths.extracted(game));
        PcPaths.deleteRecursively(paths.compiled(game));
        PcPaths.deleteRecursively(new File(paths.data(), "decompiler_out/" + game));
        paths.readyMark(game).delete();
    }

    static int index(String game) {
        for (int i = 0; i < VrOptions.GAMES.length; i++) {
            if (VrOptions.GAMES[i].equals(game)) {
                return i;
            }
        }
        return 0;
    }
}
