// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
package dev.jakquest.vr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.jakquest.vr.PcPaths.Place;

/**
 * Moving what the player can keep in another folder or on another drive (PcPaths.Place; the Play tab's "Where your
 * files are kept"): the games' set-up (PcPaths.data(): each game's extracted disc, the game built from it and the
 * sources it is built from; up to about 10 GB a game; since 0.1.3), and since 0.1.8 (LIST-0.1.8 item 8) the texture
 * packs, the saves and options, and the logs. Each choice is its own file in the launcher's own folder (PcPaths.home);
 * without it the place is its default one.
 *
 * A move on the same drive renames the folder, at once. To another drive it copies every file, checks each copy's size
 * against its original, switches over, then removes the old copy: only the files that are in the new place at the same
 * size, so nothing that was not copied is lost. A move that is stopped or fails removes what it copied and keeps the
 * old. If the launcher closes while the old copy is being removed, its next start finishes it (the place's leftover
 * note, data-folder-leftover.txt for the games). One move at a time.
 */
final class PcDataFolder {
    /** Where a move is; written by its thread, read by the launcher's page. */
    static final class State {
        volatile boolean running;
        /** While what uses the place waits: until the new place is chosen. */
        volatile boolean copying;
        /** What is moving (or whose old copy is being removed). */
        volatile Place place = Place.GAMES;
        volatile String phase = "";
        volatile int percent = -1;
        volatile String failure;   // why the last one stopped, in the player's words
        volatile String done;      // what the last one did
        volatile boolean stopped;
        volatile Thread thread;
    }

    static final State STATE = new State();

    /** The folder made for the games in the one the player picks (unless the picked one is already named so). */
    static final String NAME = Place.GAMES.folderName;
    /** Room kept free on the new drive beyond the games' copy: the logs, and a game's next build. */
    static final long SPARE = 2L << 30;
    /** ... and beyond the others' (the packs, the saves, the logs): what they write next. */
    static final long SPARE_SMALL = 64L << 20;
    /** The games' leftover note (the others: Place.leftover()). */
    static final String LEFTOVER = Place.GAMES.leftover();

    private PcDataFolder() {}

    /** The folder the games go in for the folder the player picked. */
    static File target(File picked) {
        return target(Place.GAMES, picked);
    }

    /** The folder `place` goes in for the folder the player picked. */
    static File target(Place place, File picked) {
        File folder = picked.getAbsoluteFile();
        return folder.getName().equalsIgnoreCase(place.folderName) ? folder : new File(folder, place.folderName);
    }

    /** Why `target` cannot hold the games' set-up, in the player's words; null when it can. */
    static String refuse(PcPaths paths, File target) {
        return refuse(paths, Place.GAMES, target);
    }

    /** Why `target` cannot hold `place`, in the player's words; null when it can. */
    static String refuse(PcPaths paths, Place place, File target) {
        File now = paths.folder(place);
        if (same(target, now)) {
            return place.words + " are already kept in " + target + ".";
        }
        if (PcPaths.inside(target, now)) {
            return target + " is inside the folder " + place.inWords + " are kept in now: pick another.";
        }
        if (PcPaths.inside(now, target)) {
            return place.words + " are kept inside " + target + " now: pick another folder.";
        }
        if (PcPaths.inside(target, paths.install) || PcPaths.inside(paths.install, target)) {
            return target + " is the launcher's own folder, which an update replaces: pick another.";
        }
        for (Place other : Place.values()) {
            File there = paths.folder(other);
            if (other != place && (PcPaths.inside(target, there) || PcPaths.inside(there, target))) {
                return target + " is where " + other.inWords + " are kept: pick another folder.";
            }
        }
        if ((PcPaths.inside(target, paths.home) && !same(target, paths.defaultFolder(place)))
                || PcPaths.inside(paths.home, target)) {
            return target + " is where the launcher keeps its own files: pick another folder.";
        }
        if (target.exists() && !target.isDirectory()) {
            return "There is a file named " + target.getName() + " there: pick another folder.";
        }
        String[] inside = target.list();
        if (inside != null && inside.length > 0) {
            return target + " already has files in it. Pick an empty folder, or the folder to make " + place.folderName
                + " in.";
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
        if (gb < 0.1) {
            return String.format(Locale.ROOT, "%.0f MB", Math.max(1.0, bytes / (double) (1L << 20)));
        }
        return gb >= 10 ? String.format(Locale.ROOT, "%.0f GB", gb) : String.format(Locale.ROOT, "%.1f GB", gb);
    }

    /** Whether the games' folder the player chose is not there (a drive not plugged in, say). */
    static boolean missing(PcPaths paths) {
        return missing(paths, Place.GAMES);
    }

    /**
     * Whether the folder the player chose for `place` is not there (a drive not plugged in, say). Never the logs': they
     * go to the default place meanwhile (PcPaths.logs()).
     */
    static boolean missing(PcPaths paths, Place place) {
        return place != Place.LOGS && paths.moved(place) && !paths.folder(place).isDirectory();
    }

    /** Whether what uses `place` waits: it is being copied, or its chosen folder is not there. */
    static boolean waiting(PcPaths paths, Place place) {
        return (STATE.copying && STATE.place == place) || missing(paths, place);
    }

    /**
     * Moves the games' set-up to `target` (PcPaths.defaultData() for the default place) on a thread of its own;
     * `changed` runs when it is done.
     */
    static void start(PcPaths paths, File target, Runnable changed) {
        start(paths, Place.GAMES, target, changed);
    }

    /**
     * Moves `place` to `target` (PcPaths.defaultFolder(place) for its default place) on a thread of its own; `changed`
     * runs when it is done.
     */
    static void start(PcPaths paths, Place place, File target, Runnable changed) {
        if (STATE.running) {
            return;
        }
        STATE.running = true;
        STATE.copying = true;
        STATE.place = place;
        STATE.phase = "Looking at what to move";
        STATE.percent = -1;
        STATE.failure = null;
        STATE.done = null;
        STATE.stopped = false;
        Thread thread = new Thread(() -> {
            try {
                move(paths, place, target.getAbsoluteFile());
            } catch (Exception e) {
                STATE.failure = (STATE.stopped ? "Stopped" : message(e)) + ". " + place.words + " stay in "
                    + paths.folder(place) + ".";
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

    /** Stops a copy between two files: what was copied is removed, and the place stays where it was. */
    static void stop() {
        STATE.stopped = true;
    }

    private static void move(PcPaths paths, Place place, File target) throws IOException {
        if (PcSetup.STATE.running) {
            throw new IOException("A game is being set up: wait for it to finish");
        }
        String why = refuse(paths, place, target);
        if (why != null) {
            throw new IOException(why);
        }
        File from = paths.folder(place);
        String[] had = from.list();
        if (had == null || had.length == 0) {
            // nothing there yet (or the chosen folder is gone): only the choice changes
            Files.createDirectories(target.toPath());
            paths.choose(place, target);
            STATE.done = place.words + " will be kept in " + target + ".";
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
                paths.choose(place, target);
            } catch (IOException e) {
                Files.move(target.toPath(), from.toPath(), StandardCopyOption.ATOMIC_MOVE);
                if (existed) {
                    target.mkdirs();
                }
                throw e;
            }
            STATE.done = "Moved. " + place.words + " are now kept in " + target + ".";
            return;
        }
        copy(paths, place, from, target, existed);
        // From here the place is the new one: the old copy goes.
        File leftover = new File(paths.home, place.leftover());
        Files.write(leftover.toPath(), from.getPath().getBytes(StandardCharsets.UTF_8));
        STATE.copying = false;
        STATE.phase = "Removing the old copy";
        STATE.percent = -1;
        boolean all = removeCopied(from, target);
        Files.deleteIfExists(leftover.toPath());
        STATE.done = "Moved. " + place.words + " are now kept in " + target + "."
            + (all ? "" : " Some files of the old place were not copies and were kept: " + from + ".");
    }

    private static void copy(PcPaths paths, Place place, File from, File target, boolean existed) throws IOException {
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
        long spare = place == Place.GAMES ? SPARE : SPARE_SMALL;
        if (free >= 0 && free < total[0] + spare) {
            throw new IOException(place.words + " take " + size(total[0]) + " and " + target + "'s drive has "
                + size(free) + " free (" + size(total[0] + spare) + " needed, with room to spare)");
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
            paths.choose(place, target);
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
     * each place's in turn, on a thread of its own. A note naming where a place is now, the launcher's own folders or
     * another place's is dropped and nothing removed.
     */
    static void finishLeftover(PcPaths paths, Runnable changed) {
        if (STATE.running) {
            return;
        }
        List<Place> places = new ArrayList<>();
        List<File> olds = new ArrayList<>();
        for (Place p : Place.values()) {
            File leftover = new File(paths.home, p.leftover());
            if (!leftover.isFile()) {
                continue;
            }
            String text = PcPaths.read(leftover).replace("﻿", "").trim();
            File old = new File(text);
            File now = paths.folder(p);
            boolean safe = !text.isEmpty() && old.isAbsolute() && now.isDirectory() && !same(old, now)
                && !PcPaths.inside(old, now) && !PcPaths.inside(now, old) && !PcPaths.inside(paths.home, old)
                && !PcPaths.inside(old, paths.install) && !PcPaths.inside(paths.install, old);
            for (Place other : Place.values()) {
                File there = paths.folder(other);
                if (other != p && (PcPaths.inside(old, there) || PcPaths.inside(there, old))) {
                    safe = false;
                }
            }
            if (!safe) {
                leftover.delete();
                continue;
            }
            places.add(p);
            olds.add(old);
        }
        if (places.isEmpty()) {
            return;
        }
        STATE.running = true;
        STATE.copying = false;
        STATE.place = places.get(0);
        STATE.phase = "Removing the old copy of " + places.get(0).inWords + " from " + olds.get(0);
        STATE.percent = -1;
        STATE.failure = null;
        STATE.done = null;
        STATE.stopped = false;
        Thread thread = new Thread(() -> {
            try {
                for (int i = 0; i < places.size(); i++) {
                    Place p = places.get(i);
                    File old = olds.get(i);
                    STATE.place = p;
                    STATE.phase = "Removing the old copy of " + p.inWords + " from " + old;
                    try {
                        removeCopied(old, paths.folder(p));
                        new File(paths.home, p.leftover()).delete();
                    } catch (Exception e) {
                        STATE.failure = "The old copy of " + p.inWords + " in " + old + " was not all removed: "
                            + message(e);
                    }
                }
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
