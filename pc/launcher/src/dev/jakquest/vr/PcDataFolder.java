// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
package dev.jakquest.vr;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Where the games' set-up is kept on the PC (PcPaths.data(): each game's extracted disc, the game built from it and the
 * sources it is built from; up to about 10 GB a game), and moving it to another folder or drive (the Play tab's "Where
 * the games are kept"). The player's choice is data-folder.txt in the launcher's own folder (PcPaths.home); without it
 * the set-up is in home\data. Saves and options stay in home\config whatever the choice.
 *
 * A move on the same drive renames the folder, at once. To another drive it copies every file, checks each copy's size
 * against its original, switches over, then removes the old copy: only the files that are in the new place at the same
 * size, so nothing that was not copied is lost. A move that is stopped or fails removes what it copied and keeps the
 * old. If the launcher closes while the old copy is being removed, its next start finishes it
 * (data-folder-leftover.txt).
 */
final class PcDataFolder {
    /** Where a move is; written by its thread, read by the launcher's page. */
    static final class State {
        volatile boolean running;
        /** While the games cannot be played or set up: until the new place is chosen. */
        volatile boolean copying;
        volatile String phase = "";
        volatile int percent = -1;
        volatile String failure;   // why the last one stopped, in the player's words
        volatile String done;      // what the last one did
        volatile boolean stopped;
        volatile Thread thread;
    }

    static final State STATE = new State();

    /** The folder made in the one the player picks (unless the picked one is already named so). */
    static final String NAME = "JakQuestVR";
    /** Room kept free on the new drive beyond the copy: the logs, and a game's next build. */
    static final long SPARE = 2L << 30;
    static final String LEFTOVER = "data-folder-leftover.txt";

    private PcDataFolder() {}

    /** The folder the games go in for the folder the player picked. */
    static File target(File picked) {
        File folder = picked.getAbsoluteFile();
        return folder.getName().equalsIgnoreCase(NAME) ? folder : new File(folder, NAME);
    }

    /** Why `target` cannot hold the games' set-up, in the player's words; null when it can. */
    static String refuse(PcPaths paths, File target) {
        File now = paths.data();
        if (same(target, now)) {
            return "The games are already kept in " + target + ".";
        }
        if (PcPaths.inside(target, now)) {
            return target + " is inside the folder the games are kept in now: pick another.";
        }
        if (PcPaths.inside(now, target)) {
            return "The games are kept inside " + target + " now: pick another folder.";
        }
        if (PcPaths.inside(target, paths.install) || PcPaths.inside(paths.install, target)) {
            return target + " is the launcher's own folder, which an update replaces: pick another.";
        }
        if ((PcPaths.inside(target, paths.home) && !same(target, paths.defaultData()))
                || PcPaths.inside(paths.home, target)) {
            return target + " is where the launcher keeps your saves and choices: pick another folder.";
        }
        if (PcPaths.inside(target, paths.logs) || PcPaths.inside(paths.logs, target)) {
            return target + " is the logs folder: pick another.";
        }
        if (target.exists() && !target.isDirectory()) {
            return "There is a file named " + target.getName() + " there: pick another folder.";
        }
        String[] inside = target.list();
        if (inside != null && inside.length > 0) {
            return target + " already has files in it. Pick an empty folder, or the folder to make " + NAME + " in.";
        }
        return null;
    }

    /** The free space on the drive `folder` is (or would be) on, -1 if unknown. */
    static long free(File folder) {
        for (File f = folder.getAbsoluteFile(); f != null; f = f.getParentFile()) {
            if (f.exists()) {
                long free = f.getUsableSpace();
                return free > 0 ? free : -1;
            }
        }
        return -1;
    }

    static String size(long bytes) {
        double gb = bytes / (double) (1L << 30);
        return gb >= 10 ? String.format(Locale.ROOT, "%.0f GB", gb) : String.format(Locale.ROOT, "%.1f GB", gb);
    }

    /** Whether the games' folder the player chose is not there (a drive not plugged in, say). */
    static boolean missing(PcPaths paths) {
        return paths.dataMoved() && !paths.data().isDirectory();
    }

    /**
     * Moves the games' set-up to `target` (PcPaths.defaultData() for the default place) on a thread of its own;
     * `changed` runs when it is done.
     */
    static void start(PcPaths paths, File target, Runnable changed) {
        if (STATE.running) {
            return;
        }
        STATE.running = true;
        STATE.copying = true;
        STATE.phase = "Looking at what to move";
        STATE.percent = -1;
        STATE.failure = null;
        STATE.done = null;
        STATE.stopped = false;
        Thread thread = new Thread(() -> {
            try {
                move(paths, target.getAbsoluteFile());
            } catch (Exception e) {
                STATE.failure = (STATE.stopped ? "Stopped" : message(e)) + ". The games stay in " + paths.data()
                    + ".";
            } finally {
                STATE.copying = false;
                STATE.running = false;
                STATE.thread = null;
                changed.run();
            }
        }, "data-folder");
        STATE.thread = thread;
        thread.start();
    }

    /** Stops a copy between two files: what was copied is removed, and the games stay where they were. */
    static void stop() {
        STATE.stopped = true;
    }

    private static void move(PcPaths paths, File target) throws IOException {
        if (PcSetup.STATE.running) {
            throw new IOException("A game is being set up: wait for it to finish");
        }
        String why = refuse(paths, target);
        if (why != null) {
            throw new IOException(why);
        }
        File from = paths.data();
        String[] had = from.list();
        if (had == null || had.length == 0) {
            // nothing set up yet (or the chosen folder is gone): only the choice changes
            Files.createDirectories(target.toPath());
            paths.chooseData(target);
            STATE.done = "The games will be kept in " + target + ".";
            return;
        }
        boolean existed = target.isDirectory();
        // The same drive: a rename, at once (an atomic move fails, and moves nothing, between drives or with a file
        // in use; then it is copied).
        boolean renamed = false;
        try {
            File parent = target.getParentFile();
            if (parent != null) {
                Files.createDirectories(parent.toPath());
            }
            if (existed) {
                Files.delete(target.toPath());  // empty (refuse): the rename makes it again
            }
            Files.move(from.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
            renamed = true;
        } catch (IOException e) {
            if (existed) {
                target.mkdirs();
            }
        }
        if (renamed) {
            try {
                paths.chooseData(target);
            } catch (IOException e) {
                Files.move(target.toPath(), from.toPath(), StandardCopyOption.ATOMIC_MOVE);
                if (existed) {
                    target.mkdirs();
                }
                throw e;
            }
            STATE.done = "Moved. The games are now kept in " + target + ".";
            return;
        }
        copy(paths, from, target, existed);
        // From here the games are in the new place: the old copy goes.
        File leftover = new File(paths.home, LEFTOVER);
        Files.write(leftover.toPath(), from.getPath().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        STATE.copying = false;
        STATE.phase = "Removing the old copy";
        STATE.percent = -1;
        boolean all = removeCopied(from, target);
        Files.deleteIfExists(leftover.toPath());
        STATE.done = "Moved. The games are now kept in " + target + "."
            + (all ? "" : " Some files of the old place were not copies and were kept: " + from + ".");
    }

    private static void copy(PcPaths paths, File from, File target, boolean existed) throws IOException {
        Path src = from.toPath(), dst = target.toPath();
        List<Path> dirs = new ArrayList<>();
        List<Path> files = new ArrayList<>();
        long[] total = {0};
        Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                dirs.add(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.isRegularFile()) {
                    files.add(file);
                    total[0] += attrs.size();
                }
                return FileVisitResult.CONTINUE;
            }
        });
        long free = free(target);
        if (free >= 0 && free < total[0] + SPARE) {
            throw new IOException("The games take " + size(total[0]) + " and " + target + "'s drive has " + size(free)
                + " free (" + size(total[0] + SPARE) + " needed, with room to spare)");
        }
        STATE.phase = "Copying " + size(total[0]);
        STATE.percent = 0;
        try {
            for (Path dir : dirs) {
                Files.createDirectories(dst.resolve(src.relativize(dir).toString()));
            }
            long copied = 0;
            for (Path file : files) {
                if (STATE.stopped) {
                    throw new IOException("Stopped");
                }
                Path to = dst.resolve(src.relativize(file).toString());
                Files.copy(file, to, StandardCopyOption.COPY_ATTRIBUTES);
                copied += Files.size(file);
                STATE.percent = total[0] == 0 ? 100 : (int) Math.min(100, copied * 100 / total[0]);
            }
            STATE.phase = "Checking the copy";
            for (Path file : files) {
                Path to = dst.resolve(src.relativize(file).toString());
                if (!Files.isRegularFile(to) || Files.size(to) != Files.size(file)) {
                    throw new IOException("The copy of " + src.relativize(file) + " is not the same as its original");
                }
            }
            paths.chooseData(target);
        } catch (IOException | RuntimeException e) {
            STATE.phase = "Removing what was copied";
            STATE.percent = -1;
            PcPaths.deleteRecursively(target);
            if (existed) {
                target.mkdirs();  // the player's empty folder, as it was
            }
            throw e;
        }
    }

    /**
     * Removes from `old` each file that is in `now` at the same place and size (a copy), then the folders left empty.
     * True when nothing is left of `old`.
     */
    static boolean removeCopied(File old, File now) throws IOException {
        if (!old.isDirectory()) {
            return true;
        }
        Path src = old.toPath(), dst = now.toPath();
        Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.isRegularFile()) {
                    File copy = dst.resolve(src.relativize(file).toString()).toFile();
                    if (copy.isFile() && copy.length() == attrs.size() && !sameFile(file, copy.toPath())) {
                        File f = file.toFile();
                        if (!f.delete()) {
                            f.setWritable(true);
                            f.delete();
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException e) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) {
                String[] left = dir.toFile().list();
                if (left != null && left.length == 0) {
                    dir.toFile().delete();
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return !old.exists();
    }

    /**
     * As the launcher starts: a move whose old copy was not all removed (the launcher closed meanwhile) is finished,
     * on a thread of its own.
     */
    static void finishLeftover(PcPaths paths, Runnable changed) {
        File leftover = new File(paths.home, LEFTOVER);
        String text = PcPaths.read(leftover).replace("﻿", "").trim();
        if (text.isEmpty() || STATE.running) {
            return;
        }
        File old = new File(text);
        File now = paths.data();
        if (!old.isAbsolute() || same(old, now) || PcPaths.inside(old, now) || PcPaths.inside(now, old)
                || !now.isDirectory()) {
            leftover.delete();
            return;
        }
        STATE.running = true;
        STATE.copying = false;
        STATE.phase = "Removing the old copy of the games from " + old;
        STATE.percent = -1;
        STATE.failure = null;
        STATE.done = null;
        STATE.stopped = false;
        Thread thread = new Thread(() -> {
            try {
                removeCopied(old, now);
                leftover.delete();
            } catch (Exception e) {
                STATE.failure = "The old copy of the games in " + old + " was not all removed: " + message(e);
            } finally {
                STATE.running = false;
                STATE.thread = null;
                changed.run();
            }
        }, "data-folder-leftover");
        STATE.thread = thread;
        thread.start();
    }

    /** One file under two names (a link to a folder): never removed as its own copy. True when unsure. */
    private static boolean sameFile(Path a, Path b) {
        try {
            return Files.isSameFile(a, b);
        } catch (IOException e) {
            return true;
        }
    }

    static String message(Exception e) {
        return e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : e.toString();
    }

    static boolean same(File a, File b) {
        try {
            return a.getCanonicalFile().equals(b.getCanonicalFile());
        } catch (IOException e) {
            return a.getAbsoluteFile().equals(b.getAbsoluteFile());
        }
    }
}
