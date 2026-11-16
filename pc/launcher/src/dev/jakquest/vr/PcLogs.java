// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
package dev.jakquest.vr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The Logs tab's one button (owner, 2026-11-13: logs as easy to send as OpenRAC's): the games' newest logs, each set-up's,
 * the VR options of each game and the launcher's own, in one zip in Downloads\JakQuestVR, ready to drop into a bug
 * report. Nothing of the player's but those files and a few lines on the PC (Windows, Java, memory): no paths with
 * their name in them.
 */
final class PcLogs {
    private PcLogs() {}

    static File gather(PcPaths paths) throws IOException {
        paths.logs.mkdirs();
        String stamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File zip = new File(paths.logs, "JakQuestVR-logs-" + stamp + ".zip");
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip))) {
            add(out, "summary.txt", summary(paths).getBytes(StandardCharsets.UTF_8));
            for (File f : newest(paths.gameLogs(), ".log", 6)) {
                addFile(out, "game-logs/" + f.getName(), f);
            }
            for (File f : newest(paths.logs, ".log", 6)) {
                addFile(out, "launcher-logs/" + f.getName(), f);
            }
            for (String game : VrOptions.GAMES) {
                File options = paths.vrOptions(game);
                if (options.isFile()) {
                    addFile(out, "settings/" + game + "-vr-options.txt", options);
                }
                File settings = new File(options.getParentFile(), "pc-settings.gc");
                if (settings.isFile()) {
                    addFile(out, "settings/" + game + "-pc-settings.gc", settings);
                }
            }
            if (paths.launcherLog().isFile()) {
                addFile(out, "launcher.log", paths.launcherLog());
            }
        }
        return zip;
    }

    static String summary(PcPaths paths) {
        StringBuilder s = new StringBuilder();
        s.append("Jak and Daxter VR (PC) ").append(PcBuild.VERSION).append('\n');
        s.append("Windows: ").append(System.getProperty("os.name")).append(' ').append(System.getProperty("os.version"))
            .append(' ').append(System.getProperty("os.arch")).append('\n');
        s.append("Java: ").append(System.getProperty("java.version")).append('\n');
        s.append("Processors: ").append(Runtime.getRuntime().availableProcessors()).append('\n');
        for (int i = 0; i < VrOptions.GAMES.length; i++) {
            String game = VrOptions.GAMES[i];
            s.append(VrOptions.GAME_NAMES[i]).append(": ");
            switch (PcSetup.status(paths, game)) {
                case READY:
                    s.append("set up");
                    if (!PcBuild.VERSION.equals(PcSetup.builtBy(paths, game))) {
                        s.append(" (built by ").append(PcSetup.builtBy(paths, game)).append(", the same game code)");
                    }
                    break;
                case OUTDATED: s.append("set up with ").append(PcSetup.builtBy(paths, game))
                    .append(", to build again"); break;
                default: s.append("not set up"); break;
            }
            File buildinfo = new File(paths.extracted(game), "buildinfo.json");
            if (buildinfo.isFile()) {
                s.append("; disc ").append(PcPaths.read(buildinfo).replaceAll("\\s+", " ").trim());
            }
            s.append('\n');
        }
        return s.toString();
    }

    private static List<File> newest(File folder, String suffix, int most) {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(suffix));
        if (files == null) {
            return new ArrayList<>();
        }
        List<File> list = new ArrayList<>(Arrays.asList(files));
        list.sort(Comparator.comparingLong(File::lastModified).reversed());
        return list.subList(0, Math.min(most, list.size()));
    }

    private static void addFile(ZipOutputStream out, String name, File file) throws IOException {
        // a log bigger than 32 MB keeps its last 32 MB (the end is where a crash is)
        long most = 32L << 20;
        byte[] bytes = Files.readAllBytes(file.toPath());
        if (bytes.length > most) {
            bytes = Arrays.copyOfRange(bytes, (int) (bytes.length - most), bytes.length);
        }
        add(out, name, bytes);
    }

    private static void add(ZipOutputStream out, String name, byte[] bytes) throws IOException {
        out.putNextEntry(new ZipEntry(name));
        out.write(bytes);
        out.closeEntry();
    }
}
