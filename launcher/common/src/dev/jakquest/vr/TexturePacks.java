// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
package dev.jakquest.vr;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
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
 *
 * <p>0.1.3: packs are imported as players find them on the web (Stager): a zip, a zip inside the zip, or the folder it
 * was unpacked to. The game a pack is for is read from it (custom_assets/[game]/, [game]/texture_replacements, its
 * metadata.json's supportedGames), else it is the game the player imports it for, else the launcher asks. A pack zipped
 * from inside its texture_replacements (texture pages at its top) is taken too. What cannot work says so plainly: a .7z
 * or .rar (unpack it first), a pack for the PCSX2 emulator (pictures named by number), pictures not in texture pages.
 */
public final class TexturePacks {
    /** One pack, as the launchers list it. */
    public static final class Pack {
        public final String id;
        public final String name, author, version, description;
        public final int pictures;
        public final long bytes;
        public final String stamp;  // when it was added: changes the build if the same id is added again
        /** The mod list's entry it came from (ModList.Entry.ref()), or "" for one imported from a file or folder. */
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

        /** Where it came from, for its card: "Imported" (a file or folder of the player's) or "From a mod list". */
        public String origin() {
            return from.isEmpty() ? "Imported" : "From a mod list";
        }
    }

    /** What an import added: each pack with the game it went to (a pack with pictures for two games makes two). */
    public static final class Imported {
        public final List<String> games = new ArrayList<>();
        public final List<Pack> packs = new ArrayList<>();

        /** "Shiny Jak 1.2 by Someone, 340 pictures, for Jak 1", each joined by "; " */
        public String summary() {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < packs.size(); i++) {
                if (b.length() > 0) b.append("; ");
                b.append(packs.get(i).line()).append(", for ").append(shortName(games.get(i)));
            }
            return b.toString();
        }
    }

    /** The pack does not say which game it is for and none was given: the launcher asks, then finish() again. */
    public static final class NeedsGame extends IOException {
        private static final long serialVersionUID = 1L;

        NeedsGame() {
            super("It does not say which game it is for.");
        }
    }

    /** The most a pack may unpack to, and its most files: a pack is pictures, not a disc. */
    static final long MOST_BYTES = 3L << 30;
    static final int MOST_FILES = 100000;
    static final String REPLACEMENTS = "texture_replacements";
    private static final String LIST = "list.txt", ABOUT = "pack.txt", APPLIED = "texture-packs.txt";
    private static final String[] SHORT_NAMES = {"Jak 1", "Jak II", "Jak 3"};
    private static final String UNPACK = "It is a [kind] archive, which the launcher cannot open: unpack it first (7-Zip, "
            + "free, opens it), then import the folder it makes, or a .zip of that folder.";

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

    /** add() with no mod list entry. */
    public static Pack add(File root, String game, InputStream zip, String fileName) throws IOException {
        return add(root, game, zip, fileName, null);
    }

    /**
     * Unpacks a texture pack's zip (read once, as it comes) for game alone, on and at the top of the list (Browse mods'
     * Add and Update): its pictures for another of the games are skipped, and the mod list's name, authors, version
     * and words are used where the pack's own metadata.json has none. Throws with the player's words when it is not a
     * texture pack for this game.
     */
    public static Pack add(File root, String game, InputStream zip, String fileName, ModList.Entry entry)
            throws IOException {
        try (Stager s = new Stager(root, fileName, game)) {
            s.zip(zip);
            return s.finish(game, entry).packs.get(0);
        }
    }

    /**
     * One import: the pack's pictures gathered in a folder of the launcher's own beside the games' packs (so they move
     * into place without a copy), from a zip, a folder or file by file (the Quest's folders); finish() makes the pack
     * (one per game it has pictures for), close() takes away what is left. One Stager, one pack.
     */
    public static final class Stager implements Closeable {
        private static final String ANY = "_any", LOOSE = "_loose";
        private final File root, staging;
        private final String only;
        private String fileName;
        private String metadata = "";
        private int files, pictures, loose, pcsx2;
        private long bytes;
        private boolean looseDone;
        private final Set<String> otherGames = new TreeSet<>();
        private final byte[] buffer = new byte[64 * 1024];

        /**
         * fileName: the zip's or the folder's name (the pack's id and, with no metadata.json, its name). only: the one
         * game wanted (its pictures for another are skipped), or null for any (finish() says which).
         */
        public Stager(File root, String fileName, String only) throws IOException {
            this.root = root;
            this.fileName = fileName == null ? "" : fileName;
            this.only = only;
            staging = new File(root, ".import-" + System.currentTimeMillis() + "-" + (System.nanoTime() % 100000));
            if (!staging.mkdirs()) throw new IOException("Cannot make " + staging);
        }

        /** A zip on disk, read by its index (any zip a PC tool makes; old zips' names, not in UTF-8, too). */
        public void zip(File file) throws IOException {
            try (InputStream head = new FileInputStream(file)) {
                refuseOther(head);
            }
            IOException failed = null;
            for (Charset names : new Charset[] {StandardCharsets.UTF_8, Charset.forName("IBM437")}) {
                try (ZipFile z = new ZipFile(file, names)) {
                    Enumeration<? extends ZipEntry> all = z.entries();
                    while (all.hasMoreElements()) {
                        ZipEntry e = all.nextElement();
                        if (e.isDirectory()) continue;
                        try (InputStream in = z.getInputStream(e)) {
                            offer(e.getName(), in, 0);
                        }
                    }
                    return;
                } catch (ZipException | IllegalArgumentException e) {
                    failed = new IOException("Not a zip that can be read: " + e.getMessage());
                    reset();
                }
            }
            throw failed;
        }

        /** A zip as it comes (Browse mods' download; the Quest's picked file). */
        public void zip(InputStream raw) throws IOException {
            BufferedInputStream in = new BufferedInputStream(raw);
            in.mark(16);
            refuseOther(in);
            in.reset();
            try {
                ZipInputStream z = new ZipInputStream(in);
                ZipEntry e;
                while ((e = z.getNextEntry()) != null) {
                    if (!e.isDirectory()) offer(e.getName(), z, 0);
                }
            } catch (ZipException | IllegalArgumentException e) {
                throw new IOException("Not a zip that can be read: " + e.getMessage());
            }
        }

        /**
         * A folder the pack was unpacked to. Its own name counts, as the player may pick texture_replacements itself
         * or custom_assets/jak1; such a folder takes the name of the first above it that is not one of those.
         */
        public void folder(File dir) throws IOException {
            if (!dir.isDirectory()) throw new IOException("Cannot read the folder " + dir);
            File named = dir.getAbsoluteFile();
            while (named != null && genericFolder(named.getName())) named = named.getParentFile();
            if (named != null && !named.getName().isEmpty()) fileName = named.getName();
            walk(dir, dir.getName() + "/");
        }

        private void walk(File dir, String prefix) throws IOException {
            File[] children = dir.listFiles();
            if (children == null) return;
            Arrays.sort(children);
            for (File c : children) {
                if (Files.isSymbolicLink(c.toPath())) continue;
                if (c.isDirectory()) {
                    walk(c, prefix + c.getName() + "/");
                } else if (c.isFile()) {
                    try (InputStream in = new FileInputStream(c)) {
                        offer(prefix + c.getName(), in, 0);
                    }
                }
            }
        }

        /** One file of the pack, at its path in the pack (folders joined by /); the Quest's folder import gives each. */
        public void offer(String path, InputStream in) throws IOException {
            offer(path, in, 0);
        }

        private void offer(String path, InputStream in, int depth) throws IOException {
            if (++files > MOST_FILES) throw new IOException("It has more than " + MOST_FILES + " files: not a texture pack.");
            String name = path.replace('\\', '/');
            while (name.startsWith("/")) name = name.substring(1);
            String lower = name.toLowerCase(Locale.ROOT);
            String base = lower.substring(lower.lastIndexOf('/') + 1);
            if (base.endsWith(".zip") && depth < 2) {
                // a pack inside the download's zip (some come with a read-me or pictures of it beside)
                ZipInputStream inner = new ZipInputStream(new KeepOpen(in));
                try {
                    ZipEntry e;
                    while ((e = inner.getNextEntry()) != null) {
                        if (!e.isDirectory()) offer(e.getName(), inner, depth + 1);
                    }
                } catch (ZipException | IllegalArgumentException e) {
                    throw new IOException("The zip " + name + " inside it cannot be read: " + e.getMessage());
                }
                return;
            }
            if (base.equals("metadata.json")) {
                if (metadata.isEmpty() && name.split("/").length <= 3) metadata = readSmall(in, 64 * 1024);
                return;
            }
            if (base.endsWith(".dds")) {
                pcsx2++;  // OpenGOAL takes PNG alone; the emulator's packs have DDS too
                return;
            }
            if (!base.endsWith(".png")) return;
            String relative = replacementPath(name);
            if (relative == null) {
                if (pcsx2Like(lower)) {
                    pcsx2++;
                } else if (safe(name)) {
                    loose++;
                    write(in, new File(new File(staging, LOOSE), name));
                }
                return;
            }
            String game = namedGame(lower);
            if (only != null && game != null && !game.equals(only)) {
                otherGames.add(game);
                return;
            }
            write(in, new File(new File(staging, game == null ? ANY : game), relative));
            pictures++;
        }

        /**
         * Makes the pack in the list of each game it has pictures for, on and at the top. Pictures that name no game
         * go to only, else to its metadata.json's supportedGames (preferred alone if it is one of them), else to
         * preferred (the game the player imports it for), else to the one game the rest named; with none of those it
         * throws NeedsGame, and the launcher asks and calls again. entry: the mod list's (Browse mods), or null.
         */
        public Imported finish(String preferred, ModList.Entry entry) throws IOException {
            if (pictures == 0 && loose > 0 && !looseDone) {
                looseDone = true;
                pictures = adoptLoose();
            }
            Map<String, File> buckets = new LinkedHashMap<>();
            for (String g : VrOptions.GAMES) {
                File f = new File(staging, g);
                if (f.isDirectory()) buckets.put(g, f);
            }
            File any = new File(staging, ANY);
            if (any.isDirectory()) {
                List<String> to = new ArrayList<>();
                List<String> said = supportedGames(metadata);
                if (only != null) {
                    to.add(only);
                } else if (!said.isEmpty()) {
                    to.addAll(preferred != null && said.contains(preferred) ? Collections.singletonList(preferred) : said);
                } else if (preferred != null) {
                    to.add(preferred);
                } else if (buckets.size() == 1) {
                    to.addAll(buckets.keySet());
                }
                if (to.isEmpty()) throw new NeedsGame();
                for (int i = 0; i < to.size(); i++) {
                    File f = new File(staging, to.get(i));
                    if (i == to.size() - 1 && !f.exists() && any.renameTo(f)) {
                        buckets.put(to.get(i), f);
                        break;
                    }
                    copyTree(any, f);
                    buckets.put(to.get(i), f);
                }
                deleteTree(any);
            }
            if (buckets.isEmpty()) throw new IOException(nothingMessage(only != null ? only : preferred));
            // OpenGOAL's texture-pack metadata.json (its launcher's schemas/texture-packs/v1): name, authors (one
            // string), version, description, supportedGames; "author" too, as some packs have it.
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
            Imported done = new Imported();
            for (Map.Entry<String, File> b : buckets.entrySet()) {
                done.games.add(b.getKey());
                done.packs.add(place(b.getKey(), b.getValue(), new String[] {clean(name, 60), clean(author, 40),
                    clean(version, 20), clean(description, 300)}, entry));
            }
            return done;
        }

        /** The pictures moved in as the game's newest pack: its folder, pack.txt and the list. */
        private Pack place(String game, File from, String[] about, ModList.Entry entry) throws IOException {
            File games = gameFolder(root, game);
            if (!games.isDirectory() && !games.mkdirs()) throw new IOException("Cannot make " + games);
            String id = freeId(games, fileName);
            File folder = new File(games, id), partial = new File(games, id + ".partial");
            deleteTree(partial);
            if (!partial.mkdirs()) throw new IOException("Cannot make " + partial);
            File into = new File(partial, REPLACEMENTS);
            if (!from.renameTo(into)) copyTree(from, into);
            long[] counted = new long[2];
            count(into, counted);
            String stamp = Long.toString(System.currentTimeMillis());
            writeAbout(new File(partial, ABOUT), new String[] {about[0], about[1], about[2], about[3],
                Long.toString(counted[0]), Long.toString(counted[1]), stamp, entry == null ? "" : clean(entry.ref(), 200)});
            if (!partial.renameTo(folder)) {
                deleteTree(partial);
                throw new IOException("Cannot make " + folder);
            }
            List<Pack> packs = list(root, game);
            packs.removeIf(p -> p.id.equals(id));
            Pack pack = new Pack(id, about[0], "", "", "", (int) counted[0], counted[1], stamp, "", true);
            packs.add(0, pack);
            writeList(root, game, packs);
            for (Pack p : list(root, game)) {
                if (p.id.equals(id)) return p;
            }
            return pack;
        }

        /** Why nothing could be taken, in the player's words. */
        private String nothingMessage(String wanted) {
            if (!otherGames.isEmpty()) {
                List<String> names = new ArrayList<>();
                for (String g : otherGames) names.add(shortName(g));
                return "It is a texture pack for " + String.join(" and ", names) + ", not " + shortName(wanted) + ".";
            }
            if (pcsx2 > 0) {
                return "It is a texture pack for the PCSX2 emulator (its pictures are named by number, for PCSX2's "
                        + "replacements folder). OpenGOAL games, this port included, cannot use those: look for the "
                        + "pack made for OpenGOAL (its pictures are in a texture_replacements folder).";
            }
            if (loose > 0) {
                return "Its pictures are not in a texture_replacements folder of texture pages, as OpenGOAL's texture "
                        + "packs have them (texture_replacements/[texture page]/[picture].png): not a pack this port "
                        + "can use.";
            }
            return wanted == null ? "No pictures in it (" + REPLACEMENTS + "): is it a texture pack?"
                    : "No pictures for " + shortName(wanted) + " in it (" + REPLACEMENTS + "): is it a texture pack "
                    + "for this game?";
        }

        /**
         * A pack zipped from inside its texture_replacements (texture pages at its top, or in one folder): the folder
         * whose folders of pictures look most like the games' texture pages (at least one by its name) becomes it.
         * The pictures taken, or 0.
         */
        private int adoptLoose() throws IOException {
            Map<File, Integer> pages = new HashMap<>(), marked = new HashMap<>();
            scorePages(new File(staging, LOOSE), pages, marked);
            File best = null;
            for (Map.Entry<File, Integer> e : pages.entrySet()) {
                if (marked.getOrDefault(e.getKey(), 0) > 0 && (best == null || e.getValue() > pages.get(best))) {
                    best = e.getKey();
                }
            }
            if (best == null) return 0;
            File any = new File(staging, ANY);
            if (!any.isDirectory() && !any.mkdirs()) throw new IOException("Cannot make " + any);
            int taken = 0;
            for (File page : listed(best)) {
                if (!page.isDirectory() || !hasPictures(page)) continue;
                File to = new File(any, page.getName());
                if (!page.renameTo(to)) copyTree(page, to);
                for (File f : listed(to)) {
                    if (f.isFile()) taken++;
                }
            }
            return taken;
        }

        private void scorePages(File dir, Map<File, Integer> pages, Map<File, Integer> marked) {
            for (File c : listed(dir)) {
                if (!c.isDirectory()) continue;
                if (hasPictures(c)) {
                    pages.merge(dir, 1, Integer::sum);
                    if (pageName(c.getName())) marked.merge(dir, 1, Integer::sum);
                }
                scorePages(c, pages, marked);
            }
        }

        private void write(InputStream in, File to) throws IOException {
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
        }

        /** As new (a zip read again with its names in another encoding). */
        private void reset() throws IOException {
            deleteTree(staging);
            if (!staging.mkdirs()) throw new IOException("Cannot make " + staging);
            metadata = "";
            files = pictures = loose = pcsx2 = 0;
            bytes = 0;
            looseDone = false;
            otherGames.clear();
        }

        /** Takes away what was gathered and not made into a pack. */
        @Override
        public void close() {
            deleteTree(staging);
        }
    }

    /** A .7z, a .rar or anything but a zip refused in plain words, from its first bytes. */
    static void refuseOther(InputStream in) throws IOException {
        byte[] h = new byte[8];
        int n = 0, r;
        while (n < h.length && (r = in.read(h, n, h.length - n)) > 0) n += r;
        if (n == 0) throw new IOException("The file is empty.");
        if (n >= 4 && h[0] == '7' && h[1] == 'z' && (h[2] & 0xff) == 0xBC && (h[3] & 0xff) == 0xAF) {
            throw new IOException(UNPACK.replace("[kind]", ".7z"));
        }
        if (n >= 4 && h[0] == 'R' && h[1] == 'a' && h[2] == 'r' && h[3] == '!') {
            throw new IOException(UNPACK.replace("[kind]", ".rar"));
        }
        if (n < 2 || h[0] != 'P' || h[1] != 'K') {
            throw new IOException("It is not a .zip: texture packs come as a .zip, or a folder of pictures.");
        }
    }

    /**
     * Where a zip entry goes under texture_replacements, or null: not a picture, not under texture_replacements, for
     * another game, or a path that would leave the folder.
     */
    static String picturePath(String name, String game) {
        String g = namedGame(name.toLowerCase(Locale.ROOT));
        return g != null && !g.equals(game) ? null : replacementPath(name);
    }

    /** The path under texture_replacements of a picture in it, or null (as picturePath, any game). */
    static String replacementPath(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        if (!lower.endsWith(".png")) return null;
        int at = lower.lastIndexOf(REPLACEMENTS + "/");
        if (at < 0) return null;
        String relative = name.substring(at + REPLACEMENTS.length() + 1);
        return safe(relative) ? relative : null;
    }

    /** A relative path with no drive, no empty part, no . or .. */
    static boolean safe(String relative) {
        if (relative.isEmpty() || relative.startsWith("/") || relative.contains(":")) return false;
        for (String part : relative.split("/")) {
            if (part.isEmpty() || part.equals(".") || part.equals("..")) return false;
        }
        return true;
    }

    /** The game a picture's path names (custom_assets/jak1/, or jak1/ just above texture_replacements), or null. */
    static String namedGame(String lower) {
        int at = lower.lastIndexOf(REPLACEMENTS + "/");
        String before = at < 0 ? lower : lower.substring(0, at);
        for (String g : VrOptions.GAMES) {
            if (before.contains("custom_assets/" + g + "/") || before.equals(g + "/") || before.endsWith("/" + g + "/")) {
                return g;
            }
        }
        return null;
    }

    private static final Pattern PCSX2_NAME = Pattern.compile("[0-9a-f]{8,16}(-[0-9a-f]{8}){1,2}\\.(png|dds)");
    private static final Pattern SERIAL = Pattern.compile("s[a-z]{3}-\\d{5}");

    /** A picture of an emulator's pack: PCSX2's replacements folder, a disc's serial (SCUS-97124) or a hash name. */
    static boolean pcsx2Like(String lower) {
        String[] parts = lower.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if (parts[i].equals("replacements") || SERIAL.matcher(parts[i]).matches()) return true;
        }
        return PCSX2_NAME.matcher(parts[parts.length - 1]).matches();
    }

    /** A texture page's name as the games have them (village1-vis-tfrag, common, ctysluma-pris...). */
    static boolean pageName(String name) {
        String n = name.toLowerCase(Locale.ROOT);
        return n.contains("-vis-") || n.matches(".*-(tfrag|pris|pris2|shrub|alpha|water|sprite|minimap|warp)")
                || n.equals("common") || n.equals("effects") || n.equals("hud") || n.equals("_all")
                || n.equals("environment-generic") || n.equals("zoomerhud");
    }

    /** A folder's name that is no pack's name: texture_replacements, custom_assets, jak1, jak2, jak3. */
    public static boolean genericFolder(String name) {
        String n = name.toLowerCase(Locale.ROOT);
        if (n.equals(REPLACEMENTS) || n.equals("custom_assets")) return true;
        for (String g : VrOptions.GAMES) {
            if (n.equals(g)) return true;
        }
        return false;
    }

    private static final Pattern SUPPORTED = Pattern.compile("\"supportedGames\"\\s*:\\s*\\[([^\\]]*)\\]");
    private static final Pattern QUOTED = Pattern.compile("\"([^\"]*)\"");

    /** metadata.json's supportedGames, the games known here alone. */
    static List<String> supportedGames(String json) {
        List<String> out = new ArrayList<>();
        Matcher m = SUPPORTED.matcher(json);
        if (!m.find()) return out;
        Matcher q = QUOTED.matcher(m.group(1));
        while (q.find()) {
            String g = q.group(1).trim().toLowerCase(Locale.ROOT);
            for (String known : VrOptions.GAMES) {
                if (known.equals(g) && !out.contains(g)) out.add(g);
            }
        }
        return out;
    }

    /** "Jak 1", "Jak II", "Jak 3" */
    public static String shortName(String game) {
        for (int i = 0; i < VrOptions.GAMES.length && i < SHORT_NAMES.length; i++) {
            if (VrOptions.GAMES[i].equals(game)) return SHORT_NAMES[i];
        }
        return game == null ? "" : game;
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

    private static File[] listed(File dir) {
        File[] children = dir.listFiles();
        if (children == null) return new File[0];
        Arrays.sort(children);
        return children;
    }

    private static boolean hasPictures(File dir) {
        for (File f : listed(dir)) {
            if (f.isFile() && f.getName().toLowerCase(Locale.ROOT).endsWith(".png")) return true;
        }
        return false;
    }

    /** files and bytes under dir, added to counted[0] and counted[1] */
    private static void count(File dir, long[] counted) {
        for (File f : listed(dir)) {
            if (f.isDirectory()) {
                count(f, counted);
            } else {
                counted[0]++;
                counted[1] += f.length();
            }
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
        if (!Files.isSymbolicLink(f.toPath())) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) deleteTree(c);
            }
        }
        f.delete();
    }

    /** A zip's entry stream read by a zip inside it, left open for the next entry. */
    private static final class KeepOpen extends FilterInputStream {
        KeepOpen(InputStream in) {
            super(in);
        }

        @Override
        public void close() {
        }
    }
}
