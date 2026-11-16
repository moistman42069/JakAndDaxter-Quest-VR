// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
package dev.jakquest.vr;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Texture packs: OpenGOAL's own kind of mod, as its launcher's "Texture Packs". A pack is a zip of pictures (PNG) that
 * replace the game's own, at custom_assets/[game]/texture_replacements/[texture page]/[texture].png in the zip (some
 * packs have texture_replacements at their top), with a metadata.json naming it. The decompiler puts them into the game
 * as it is built (decompilation_process.cpp), so a change of packs means the game built again from the disc extracted
 * before: no disc needed, a few minutes.
 *
 * <p>Both launchers keep them in a folder of their own: [packs]/[game]/[id]/texture_replacements, with pack.txt (its
 * name, author, version, what it says of itself) and [packs]/[game]/list.txt naming them in order, each on or off. Before
 * a build, apply() makes the project's custom_assets/[game]/texture_replacements from the ones on (one higher in the list
 * wins where two change the same picture) and writes what it used; changed() says whether that differs from now.
 */
public final class TexturePacks {
    /** One pack, as the launchers list it. */
    public static final class Pack {
        public final String id;
        public final String name, author, version, description;
        public final int pictures;
        public final long bytes;
        public final String stamp;  // when it was added: changes the build if the same id is added again
        /** The mod list's entry it came from (ModList.Entry.ref()), or "" for one added from a file. */
        public final String from;
        public boolean on;

        Pack(String id, String name, String author, String version, String description, int pictures, long bytes,
                String stamp, String from, boolean on) {
            this.id = id;
            this.name = name;
            this.author = author;
            this.version = version;
            this.description = description;
            this.pictures = pictures;
            this.bytes = bytes;
            this.stamp = stamp;
            this.from = from;
            this.on = on;
        }

        /** "Name 1.2 by Someone, 340 pictures" */
        public String line() {
            StringBuilder b = new StringBuilder(name);
            if (!version.isEmpty()) b.append(' ').append(version);
            if (!author.isEmpty()) b.append(" by ").append(author);
            b.append(", ").append(pictures).append(pictures == 1 ? " picture" : " pictures");
            return b.toString();
        }
    }

    /** The most a pack may unpack to, and its most files: a pack is pictures, not a disc. */
    static final long MOST_BYTES = 3L << 30;
    static final int MOST_FILES = 100000;
    static final String REPLACEMENTS = "texture_replacements";
    private static final String LIST = "list.txt", ABOUT = "pack.txt", APPLIED = "texture-packs.txt";

    private TexturePacks() {}

    // ---- the list ----

    /** The packs added for game, in their order. */
    public static List<Pack> list(File root, String game) {
        List<Pack> out = new ArrayList<>();
        for (String line : readLines(new File(gameFolder(root, game), LIST))) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length < 2 || !validId(parts[0])) continue;
            File folder = new File(gameFolder(root, game), parts[0]);
            if (!new File(folder, REPLACEMENTS).isDirectory()) continue;
            String[] about = readAbout(new File(folder, ABOUT));
            out.add(new Pack(parts[0], about[0].isEmpty() ? parts[0] : about[0], about[1], about[2], about[3],
                    parseInt(about[4]), parseLong(about[5]), about[6], about[7], parts[1].equals("on")));
        }
        return out;
    }

    public static void setOn(File root, String game, String id, boolean on) throws IOException {
        List<Pack> packs = list(root, game);
        for (Pack p : packs) {
            if (p.id.equals(id)) p.on = on;
        }
        writeList(root, game, packs);
    }

    /** Up the list (by -1) or down (+1). */
    public static void move(File root, String game, String id, int by) throws IOException {
        List<Pack> packs = list(root, game);
        for (int i = 0; i < packs.size(); i++) {
            int to = i + by;
            if (packs.get(i).id.equals(id) && to >= 0 && to < packs.size()) {
                packs.add(to, packs.remove(i));
                break;
            }
        }
        writeList(root, game, packs);
    }

    public static void remove(File root, String game, String id) throws IOException {
        List<Pack> packs = list(root, game);
        packs.removeIf(p -> p.id.equals(id));
        writeList(root, game, packs);
        if (validId(id)) deleteTree(new File(gameFolder(root, game), id));
    }

    /**
     * A pack replaced by a mod list's newer version (its Update button): the new one takes the old one's place in the
     * list and its on or off, and the old one goes. Nothing changes when the new zip cannot be added.
     */
    public static Pack update(File root, String game, String oldId, InputStream zip, String fileName,
            ModList.Entry entry) throws IOException {
        Pack added = add(root, game, zip, fileName, entry);
        List<Pack> packs = list(root, game);
        Pack old = null, fresh = null;
        for (Pack p : packs) {
            if (p.id.equals(oldId)) old = p;
            if (p.id.equals(added.id)) fresh = p;
        }
        if (old == null || fresh == null) return fresh != null ? fresh : added;
        packs.remove(fresh);
        fresh.on = old.on;
        packs.set(packs.indexOf(old), fresh);
        writeList(root, game, packs);
        if (validId(oldId)) deleteTree(new File(gameFolder(root, game), oldId));
        return fresh;
    }

    // ---- adding one ----

    /**
     * Unpacks a texture pack's zip (read once, as it comes) for game, on and at the top of the list. Only pictures
     * under a texture_replacements folder are kept; one for another of the games (custom_assets/[other]/) is skipped.
     * Throws with the player's words when it is not a texture pack for this game.
     */
    public static Pack add(File root, String game, InputStream zip, String fileName) throws IOException {
        return add(root, game, zip, fileName, null);
    }

    /**
     * As add(), for a pack from a mod list (ModList): its entry kept with it (for its Update button), and the list's
     * name, authors, version and words used where the pack's own metadata.json has none.
     */
    public static Pack add(File root, String game, InputStream zip, String fileName, ModList.Entry entry)
            throws IOException {
        File games = gameFolder(root, game);
        if (!games.isDirectory() && !games.mkdirs()) throw new IOException("Cannot make " + games);
        String id = freeId(games, fileName);
        File folder = new File(games, id);
        File partial = new File(games, id + ".partial");
        deleteTree(partial);
        File pictures = new File(partial, REPLACEMENTS);
        if (!pictures.mkdirs()) throw new IOException("Cannot make " + pictures);
        int count = 0, files = 0;
        long bytes = 0;
        String metadata = "";
        byte[] buffer = new byte[64 * 1024];
        try (ZipInputStream in = new ZipInputStream(zip)) {
            ZipEntry e;
            while ((e = in.getNextEntry()) != null) {
                if (e.isDirectory()) continue;
                if (++files > MOST_FILES) throw new IOException("It has more than " + MOST_FILES + " files: not a texture pack.");
                String name = e.getName().replace('\\', '/');
                String lower = name.toLowerCase(Locale.ROOT);
                if (lower.endsWith("metadata.json") && name.split("/").length <= 3 && metadata.isEmpty()) {
                    metadata = readSmall(in, 64 * 1024);
                    continue;
                }
                String relative = picturePath(name, game);
                if (relative == null) continue;
                File to = new File(pictures, relative);
                File parent = to.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) throw new IOException("Cannot make " + parent);
                try (OutputStream out = new FileOutputStream(to)) {
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        bytes += n;
                        if (bytes > MOST_BYTES) throw new IOException("It unpacks to more than 3 GB: not a texture pack.");
                        out.write(buffer, 0, n);
                    }
                }
                count++;
            }
        } catch (IOException | RuntimeException ex) {
            deleteTree(partial);
            throw ex instanceof IOException ? (IOException) ex : new IOException("Not a zip that can be read: " + ex.getMessage());
        }
        if (count == 0) {
            deleteTree(partial);
            throw new IOException("No pictures for " + gameName(game) + " in it (" + REPLACEMENTS + "): is it a texture "
                    + "pack for this game?");
        }
        // OpenGOAL's texture-pack metadata.json (its launcher's schemas/texture-packs/v1): name, authors (one string),
        // version, description; "author" too, as some packs have it.
        String name = jsonString(metadata, "name");
        String author = jsonString(metadata, "authors");
        if (author.isEmpty()) author = jsonString(metadata, "author");
        String version = jsonString(metadata, "version");
        String description = jsonString(metadata, "description");
        if (entry != null) {
            if (name.isEmpty()) name = entry.name;
            if (author.isEmpty()) author = entry.authors;
            if (!entry.version.isEmpty()) version = entry.version;  // the list's version is what Update compares
            if (description.isEmpty()) description = entry.description;
        }
        if (name.isEmpty()) name = baseName(fileName);
        String stamp = Long.toString(System.currentTimeMillis());
        writeAbout(new File(partial, ABOUT), new String[] {clean(name, 60), clean(author, 40), clean(version, 20),
            clean(description, 300), Integer.toString(count), Long.toString(bytes), stamp,
            entry == null ? "" : clean(entry.ref(), 200)});
        if (!partial.renameTo(folder)) {
            deleteTree(partial);
            throw new IOException("Cannot make " + folder);
        }
        List<Pack> packs = list(root, game);
        packs.removeIf(p -> p.id.equals(id));
        Pack pack = new Pack(id, clean(name, 60), "", "", "", count, bytes, stamp, "", true);
        packs.add(0, pack);
        writeList(root, game, packs);
        for (Pack p : list(root, game)) {
            if (p.id.equals(id)) return p;
        }
        return pack;
    }

    /**
     * Where a zip entry goes under texture_replacements, or null: not a picture, not under texture_replacements, for
     * another game, or a path that would leave the folder.
     */
    static String picturePath(String name, String game) {
        String lower = name.toLowerCase(Locale.ROOT);
        if (!lower.endsWith(".png")) return null;
        int at = lower.lastIndexOf(REPLACEMENTS + "/");
        if (at < 0) return null;
        String before = lower.substring(0, at);
        for (String other : VrOptions.GAMES) {
            if (!other.equals(game) && before.contains("custom_assets/" + other + "/")) return null;
        }
        String relative = name.substring(at + REPLACEMENTS.length() + 1);
        if (relative.isEmpty() || relative.startsWith("/") || relative.contains(":")) return null;
        for (String part : relative.split("/")) {
            if (part.isEmpty() || part.equals(".") || part.equals("..")) return null;
        }
        return relative;
    }

    // ---- into the game ----

    /** What the packs on are now, in order (with when each was added): changed() compares it with the build's. */
    public static String signature(File root, String game) {
        StringBuilder b = new StringBuilder();
        for (Pack p : list(root, game)) {
            if (p.on) b.append(p.id).append(':').append(p.stamp).append('\n');
        }
        return b.toString();
    }

    /** The packs on are not those the game in project was built with. */
    public static boolean changed(File root, String game, File project) {
        return !signature(root, game).equals(readAll(new File(assets(project, game), APPLIED)));
    }

    /**
     * Before the game is built: project/custom_assets/game/texture_replacements made again from the packs on (the
     * folder is the launcher's; what was there goes), the last of the list first so the first wins.
     */
    public static void apply(File root, String game, File project) throws IOException {
        File target = new File(assets(project, game), REPLACEMENTS);
        deleteTree(target);
        List<Pack> packs = list(root, game);
        for (int i = packs.size() - 1; i >= 0; i--) {
            Pack p = packs.get(i);
            if (p.on) copyTree(new File(new File(gameFolder(root, game), p.id), REPLACEMENTS), target);
        }
        File applied = new File(assets(project, game), APPLIED);
        File parent = applied.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) throw new IOException("Cannot make " + parent);
        try (Writer w = new OutputStreamWriter(new FileOutputStream(applied), StandardCharsets.UTF_8)) {
            w.write(signature(root, game));
        }
    }

    // ---- helpers ----

    static File gameFolder(File root, String game) {
        return new File(root, game);
    }

    static File assets(File project, String game) {
        return new File(new File(project, "custom_assets"), game);
    }

    static boolean validId(String id) {
        return id.matches("[a-z0-9][a-z0-9-]{0,47}");
    }

    private static String freeId(File games, String fileName) {
        String base = baseName(fileName).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (base.isEmpty()) base = "pack";
        if (base.length() > 40) base = base.substring(0, 40);
        if (!Character.isLetterOrDigit(base.charAt(0))) base = "p" + base;
        String id = base;
        for (int n = 2; new File(games, id).exists(); n++) id = base + "-" + n;
        return id;
    }

    private static String baseName(String fileName) {
        String n = fileName == null ? "" : fileName.replace('\\', '/');
        n = n.substring(n.lastIndexOf('/') + 1);
        return n.toLowerCase(Locale.ROOT).endsWith(".zip") ? n.substring(0, n.length() - 4) : n;
    }

    private static String gameName(String game) {
        for (int i = 0; i < VrOptions.GAMES.length; i++) {
            if (VrOptions.GAMES[i].equals(game)) return VrOptions.GAME_NAMES[i];
        }
        return game;
    }

    /** Printable text on one line, at most most characters. */
    static String clean(String s, int most) {
        String t = s == null ? "" : s.replaceAll("[\\p{Cntrl}]+", " ").trim();
        return t.length() > most ? t.substring(0, most) : t;
    }

    private static final Pattern JSON_STRING = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*)\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

    /** A top-level string value of metadata.json (only shown): its escapes undone, the rest left as it is. */
    static String jsonString(String json, String key) {
        Matcher m = JSON_STRING.matcher(json);
        while (m.find()) {
            if (m.group(1).equals(key)) {
                return m.group(2).replace("\\\"", "\"").replace("\\n", " ").replace("\\/", "/").replace("\\\\", "\\");
            }
        }
        return "";
    }

    private static String readSmall(InputStream in, int most) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int n;
        while ((n = in.read(buffer)) > 0) {
            if (b.size() + n > most) return "";
            b.write(buffer, 0, n);
        }
        return new String(b.toByteArray(), StandardCharsets.UTF_8);
    }

    private static void writeList(File root, String game, List<Pack> packs) throws IOException {
        File games = gameFolder(root, game);
        if (!games.isDirectory() && !games.mkdirs()) throw new IOException("Cannot make " + games);
        try (Writer w = new OutputStreamWriter(new FileOutputStream(new File(games, LIST)), StandardCharsets.UTF_8)) {
            w.write("# Jak and Daxter Quest VR texture packs for " + game + ", in order (the first wins): id on|off\n");
            for (Pack p : packs) w.write(p.id + " " + (p.on ? "on" : "off") + "\n");
        }
    }

    private static void writeAbout(File file, String[] about) throws IOException {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            for (String line : about) w.write(line.replace('\n', ' ') + "\n");
        }
    }

    /** name, author, version, description, pictures, bytes, stamp, from (0.1.1; "" in 0.1.0's) */
    private static String[] readAbout(File file) {
        List<String> lines = readLines(file);
        String[] out = new String[8];
        for (int i = 0; i < out.length; i++) out[i] = i < lines.size() ? lines.get(i) : "";
        return out;
    }

    private static List<String> readLines(File file) {
        List<String> out = new ArrayList<>();
        if (!file.isFile()) return out;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (!line.startsWith("#")) out.add(line);
            }
        } catch (IOException e) {
            out.clear();
        }
        return out;
    }

    private static String readAll(File file) {
        StringBuilder b = new StringBuilder();
        for (String line : readLines(file)) {
            if (!line.isEmpty()) b.append(line).append('\n');
        }
        return b.toString();
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static long parseLong(String s) {
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static void copyTree(File from, File to) throws IOException {
        if (!from.isDirectory()) return;
        if (!to.isDirectory() && !to.mkdirs()) throw new IOException("Cannot make " + to);
        File[] children = from.listFiles();
        if (children == null) return;
        byte[] buffer = new byte[64 * 1024];
        for (File child : children) {
            File target = new File(to, child.getName());
            if (child.isDirectory()) {
                copyTree(child, target);
            } else {
                try (InputStream in = new FileInputStream(child); OutputStream out = new FileOutputStream(target)) {
                    int n;
                    while ((n = in.read(buffer)) > 0) out.write(buffer, 0, n);
                }
            }
        }
    }

    /** Deletes f and what is in it; a link is deleted, never what it leads to (the Quest links the release's files). */
    static void deleteTree(File f) {
        if (!java.nio.file.Files.isSymbolicLink(f.toPath())) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) deleteTree(c);
            }
        }
        f.delete();
    }
}
