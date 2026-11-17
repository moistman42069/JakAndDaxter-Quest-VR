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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    /**
     * This release's game code (game\data\game-code.txt, written by tools/build_pc_release.py): the SHA-256 of the game
     * sources it builds games from, the game data version (raised when the compiler or the runtime change how a game
     * must be built) and the releases before it with the same two. Empty when the file is not there (a build of the
     * launcher from the repository): then every new release builds the games again, as before 0.1.2.
     */
    static final class GameCode {
        final String id;
        final String data;
        /** 0.1.3: the SHA-256 of the compiler's and decompiler's own sources (empty before 0.1.3). */
        final String compiler;
        final List<String> same;

        GameCode(String text) {
            String id = "", data = "", compiler = "";
            List<String> same = new ArrayList<>();
            for (String line : text.split("\\R")) {
                String[] words = line.trim().split("\\s+");
                if (words.length >= 2 && words[0].equals("id")) {
                    id = words[1];
                } else if (words.length >= 2 && words[0].equals("data")) {
                    data = words[1];
                } else if (words.length >= 2 && words[0].equals("compiler")) {
                    compiler = words[1];
                } else if (words[0].equals("same")) {
                    for (int i = 1; i < words.length; i++) {
                        same.add(words[i]);
                    }
                }
            }
            this.id = id;
            this.data = data;
            this.compiler = compiler;
            this.same = same;
        }
    }

    static GameCode gameCode(PcPaths paths) {
        return new GameCode(PcPaths.read(new File(paths.template(), "game-code.txt")));
    }

    /** The release that built the game: its ready mark's first line. */
    static String builtBy(PcPaths paths, String game) {
        return PcPaths.read(paths.readyMark(game)).split("\\R", 2)[0].trim();
    }

    /**
     * The game was built from other game code than this release's. Its ready mark names the release that built it, and
     * since 0.1.2 that release's game code and game data version; a mark of 0.1.0 or 0.1.1 has the release alone, which
     * counts as this release's game code when the release is in its list of the same.
     */
    static boolean newSources(PcPaths paths, String game) {
        String mark = PcPaths.read(paths.readyMark(game));
        String built = mark.split("\\R", 2)[0].trim();
        if (PcBuild.VERSION.equals(built)) {
            return false;
        }
        GameCode now = gameCode(paths);
        if (now.id.isEmpty()) {
            return true;
        }
        GameCode then = new GameCode(mark);
        if (!then.id.isEmpty()) {
            return !(then.id.equals(now.id) && then.data.equals(now.data));
        }
        return !now.same.contains(built);
    }

    /** The ready mark of a game built now: this release, and its game code when it has one. */
    static String readyMark(PcPaths paths) {
        GameCode now = gameCode(paths);
        return now.id.isEmpty() ? PcBuild.VERSION
            : PcBuild.VERSION + "\nid " + now.id + "\ndata " + now.data + "\n";
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
        // 0.1.3, faster building again (LIST-0.1.3 item 10): built again with the same compiler and game data version,
        // the compiler's own make (its "(mi)") compiles only the sources that changed (refreshSources copies only
        // those), and the disc's code and art are not decompiled again when what the decompiler reads is the same
        // (decompileKey). With another compiler or game data version, all of it, as before.
        String before = PcPaths.read(paths.readyMark(game));
        GameCode now = gameCode(paths);
        GameCode then = new GameCode(before);
        boolean sameCompiler = iso == null && !now.compiler.isEmpty() && now.compiler.equals(then.compiler)
            && now.data.equals(then.data);
        String decomp = decompileKey(paths, game, now);
        boolean decompile = !sameCompiler || !decomp.equals(markWord(before, "decomp"))
            || !new File(paths.data(), "decompiler_out/" + game).isDirectory();
        if (iso == null && !sameCompiler) {
            PcPaths.deleteRecursively(new File(paths.compiled(game), "obj"));  // every source compiled again
        }
        if (iso == null) {
            STATE.phase = decompile ? "Building the game again with this version"
                : "Building again what changed in this version";
        }
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
        if (decompile) {
            command.add("-d");
        }
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
        String mark = readyMark(paths);
        if (!mark.endsWith("\n")) {
            mark += "\n";
        }
        if (!now.compiler.isEmpty()) {
            mark += "compiler " + now.compiler + "\n";
        }
        mark += "decomp " + decomp + "\n";
        Files.write(paths.readyMark(game).toPath(), mark.getBytes(StandardCharsets.UTF_8));
    }

    /** A ready mark's word after `key` on its line, or empty. */
    static String markWord(String mark, String key) {
        for (String line : mark.split("\\R")) {
            String[] words = line.trim().split("\\s+");
            if (words.length >= 2 && words[0].equals(key)) {
                return words[1];
            }
        }
        return "";
    }

    /**
     * What the decompiler makes a game's art and levels from, besides the disc: the game data version, the compiler's
     * and decompiler's sources, the decompiler's config (the sources' decompiler folder), the game's custom assets and
     * the texture packs on. The same: its output from the last build is kept.
     */
    static String decompileKey(PcPaths paths, String game, GameCode now) throws IOException {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(("data " + now.data + "\ncompiler " + now.compiler + "\npacks "
                + TexturePacks.signature(paths.texturePacks(), game) + "\n").getBytes(StandardCharsets.UTF_8));
            hashTree(sha, new File(paths.data(), "decompiler"), "decompiler", null);
            hashTree(sha, new File(paths.data(), "custom_assets/" + game), "custom_assets", "texture_replacements");
            StringBuilder hex = new StringBuilder();
            for (byte b : sha.digest()) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    private static void hashTree(MessageDigest sha, File dir, String rel, String skip) throws IOException {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        Arrays.sort(children);
        for (File child : children) {
            if (child.getName().equals(skip) || child.getName().startsWith(".")) {
                continue;  // the packs (their signature counts) and the launcher's own marks
            }
            String name = rel + "/" + child.getName();
            if (child.isDirectory()) {
                hashTree(sha, child, name, null);
            } else {
                sha.update((name + "\0" + child.length() + "\0").getBytes(StandardCharsets.UTF_8));
                sha.update(Files.readAllBytes(child.toPath()));
            }
        }
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
            if (from.isDirectory()) {
                syncTree(from, to);  // 0.1.3: what did not change keeps its time, for the compiler's make
            } else {
                PcPaths.deleteRecursively(to);
            }
        }
        Files.write(mark.toPath(), PcBuild.VERSION.getBytes(StandardCharsets.UTF_8));
    }

    /** to made the same as from: a file copied only when its bytes differ, what from has not removed. (0.1.3; GameCodeTest) */
    static void syncTree(File from, File to) throws IOException {
        if (to.isFile()) {
            PcPaths.deleteRecursively(to);
        }
        if (!to.isDirectory() && !to.mkdirs()) {
            throw new IOException("cannot make " + to);
        }
        File[] children = from.listFiles();
        Set<String> names = new HashSet<>();
        if (children != null) {
            for (File child : children) {
                names.add(child.getName());
                File target = new File(to, child.getName());
                if (child.isDirectory()) {
                    syncTree(child, target);
                } else if (!target.isFile() || target.length() != child.length()
                        || Files.mismatch(child.toPath(), target.toPath()) != -1) {
                    if (target.isDirectory()) {
                        PcPaths.deleteRecursively(target);
                    }
                    Files.copy(child.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        File[] there = to.listFiles();
        if (there != null) {
            for (File old : there) {
                if (!names.contains(old.getName())) {
                    PcPaths.deleteRecursively(old);
                }
            }
        }
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
