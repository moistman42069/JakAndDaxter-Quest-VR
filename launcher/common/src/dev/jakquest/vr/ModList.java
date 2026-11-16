// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
package dev.jakquest.vr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The mods there are to browse in both launchers' Mods (0.1.1, the owner: "add a browse mods section similar to our
 * openrac mod with a mod updater, disabler, etc. just make it so poeple arent confused"), as Ratchet & Clank Quest VR's
 * ModIndex. Two lists, both in OpenGOAL's mod list format (its launcher's schemas/mod-source/v1: schemaVersion 1.x,
 * sourceName, mods and texturePacks, each a map of key to displayName, description, authors, supportedGames and
 * versions, each version with its assets): this project's own (mods/mods.json in its repository: texture packs and
 * the Quest's mod packages, made or checked for VR), and the OpenGOAL community's (jakmods.dev's mods.json, the list
 * the OpenGOAL launcher's Mods reads).
 *
 * <p>What each entry is here, in the player's words:
 * <ul>
 *   <li>a texture pack (a list's texturePacks): added as Add a texture pack adds one (TexturePacks), PC and Quest;</li>
 *   <li>a mod package for the Quest (this project's list only, a version with a "quest" asset): added on the Quest as
 *       Import a mod adds one;</li>
 *   <li>anything else: one of OpenGOAL's own mods, a whole game of its own (its own program and compiled code) made for
 *       a flat screen, which cannot run in VR here: shown as "Flat screen only", with its page.</li>
 * </ul>
 *
 * <p>A list is read only when the player asks (Browse, Check for updates) or as the launcher starts with automatic
 * checks on; a good copy is kept and shown when it cannot be read. Files come only from GitHub over HTTPS, at most
 * 2 GB, written to a file as they come (a Quest has no room for a pack in memory); a size and SHA-256 the list gives
 * (this project's list: a version's "size" and "sha256") are checked. Plain Java: both launchers compile it.
 */
public final class ModList {
    /** This project's own list. */
    public static final String OURS =
        "https://raw.githubusercontent.com/moistman42069/JakAndDaxter-Quest-VR/main/mods/mods.json";
    /** How to add to it, and what each kind of mod is (docs/MODS.md on GitHub). */
    public static final String OURS_PAGE =
        "https://github.com/moistman42069/JakAndDaxter-Quest-VR/blob/main/docs/MODS.md";
    /** The OpenGOAL community's list, and its site. */
    public static final String COMMUNITY = "https://jakmods.dev/mods.json";
    public static final String COMMUNITY_SITE = "https://jakmods.dev";
    static final String SOURCE_OURS = "jakquestvr", SOURCE_COMMUNITY = "jakmods";
    private static final int MOST_LIST = 16 << 20;
    private static final int MOST_ENTRIES = 5000;
    /** The most a mod's file may be: a texture pack of pictures, or a Quest package. */
    public static final long MOST_FILE = 2L << 30;

    /** What an entry is here. */
    public enum Kind { TEXTURE_PACK, QUEST_PACKAGE, FLAT_SCREEN }

    /** One mod of a list, its newest version. */
    public static final class Entry {
        public final String source, key, name, description, authors, version, published, page, url, sha256;
        public final long size;
        public final Kind kind;
        public final List<String> games;
        /** Why it cannot be added at all, in the player's words; null when it can (on its own launcher). */
        public final String why;

        Entry(String source, String key, String name, String description, String authors, String version,
                String published, String page, String url, String sha256, long size, Kind kind, List<String> games,
                String why) {
            this.source = source;
            this.key = key;
            this.name = name;
            this.description = description;
            this.authors = authors;
            this.version = version;
            this.published = published;
            this.page = page;
            this.url = url;
            this.sha256 = sha256;
            this.size = size;
            this.kind = kind;
            this.games = games;
            this.why = why;
        }

        /** What the launchers keep with what was added from it: the list and the key. */
        public String ref() {
            return source + ":" + key;
        }

        /** "Texture pack", "Mod package for the Quest" or "Flat screen only". */
        public String kindLabel() {
            switch (kind) {
                case TEXTURE_PACK:
                    return "Texture pack";
                case QUEST_PACKAGE:
                    return "Mod package for the Quest";
                default:
                    return "Flat screen only";
            }
        }

        /** "Name 1.2 by Someone" */
        public String line() {
            StringBuilder b = new StringBuilder(name);
            if (!version.isEmpty()) b.append(' ').append(version);
            if (!authors.isEmpty()) b.append(" by ").append(authors);
            return b.toString();
        }

        /** "From this project's list" or "From jakmods.dev" */
        public String from() {
            return SOURCE_OURS.equals(source) ? "From this project's list" : "From jakmods.dev";
        }

        public boolean forGame(String game) {
            return games.isEmpty() || games.contains(game);
        }

        /** Why it cannot be added on this launcher (the PC's, or the Quest's: quest), or null when it can. */
        public String whyNot(boolean quest) {
            if (why != null) return why;
            if (kind == Kind.QUEST_PACKAGE && !quest) {
                return "A mod package built for the Quest: add it in the Quest's launcher.";
            }
            return null;
        }
    }

    public final List<Entry> entries;
    /** Each list that could not be read now (and whether a copy from before is shown), in the player's words. */
    public final List<String> problems;
    /** When the lists were read (milliseconds). */
    public final long readAt;

    private ModList(List<Entry> entries, List<String> problems, long readAt) {
        this.entries = Collections.unmodifiableList(entries);
        this.problems = Collections.unmodifiableList(problems);
        this.readAt = readAt;
    }

    /** The entries for game: the ones that can be added first, then by name. */
    public List<Entry> forGame(String game) {
        List<Entry> out = new ArrayList<>();
        for (Entry e : entries) {
            if (e.forGame(game)) out.add(e);
        }
        return out;
    }

    /** The entry of ref (Entry.ref()), or null. */
    public Entry find(String ref) {
        if (ref == null || ref.isEmpty()) return null;
        for (Entry e : entries) {
            if (e.ref().equals(ref)) return e;
        }
        return null;
    }

    /**
     * The list's newer version of what the player added from ref and has at have, or null: its Update button. None
     * for one added with no version (nothing to compare: it would ask to update for ever).
     */
    public Entry newer(String ref, String have) {
        Entry e = find(ref);
        if (e == null || e.why != null || e.version.isEmpty() || have == null || have.isEmpty()) return null;
        return compareVersions(e.version, have) > 0 ? e : null;
    }

    // ---- reading the lists ----

    /** Both lists, each fetched now or else its copy from before (kept in cache); never throws: problems say. */
    public static ModList fetch(File cache, String agent) {
        if (!cache.isDirectory() && !cache.mkdirs()) {
            List<String> problems = new ArrayList<>();
            problems.add("The mod lists could not be kept (" + cache + ").");
            return new ModList(new ArrayList<>(), problems, System.currentTimeMillis());
        }
        List<Entry> all = new ArrayList<>();
        List<String> problems = new ArrayList<>();
        readOne(all, problems, SOURCE_OURS, OURS, new File(cache, "jakquestvr-mods.json"), agent,
            "This project's mod list");
        readOne(all, problems, SOURCE_COMMUNITY, COMMUNITY, new File(cache, "jakmods-mods.json"), agent,
            "jakmods.dev's mod list");
        all.sort((a, b) -> {
            int c = Integer.compare(a.why == null ? 0 : 1, b.why == null ? 0 : 1);
            if (c == 0) c = a.kind.compareTo(b.kind);
            return c != 0 ? c : a.name.compareToIgnoreCase(b.name);
        });
        return new ModList(all, problems, System.currentTimeMillis());
    }

    private static void readOne(List<Entry> all, List<String> problems, String source, String address, File kept,
            String agent, String name) {
        String problem;
        try {
            byte[] body = get(address, agent, MOST_LIST);
            List<Entry> got = read(source, new String(body, StandardCharsets.UTF_8));
            Files.write(kept.toPath(), body);
            all.addAll(got);
            return;
        } catch (IOException | RuntimeException e) {
            problem = e.getMessage() == null ? e.toString() : e.getMessage();
        }
        if (kept.isFile()) {
            try {
                all.addAll(read(source, new String(Files.readAllBytes(kept.toPath()), StandardCharsets.UTF_8)));
                problems.add(name + " could not be read now (" + problem + "): the copy from before is shown.");
                return;
            } catch (IOException | RuntimeException e) {
                // The copy cannot be read either.
            }
        }
        problems.add(name + " could not be read (" + problem + ").");
    }

    /** A list's entries, from its text. An IllegalArgumentException when it is not a list of this format. */
    static List<Entry> read(String source, String text) {
        Map<String, Object> top = Json.object(text);
        String schema = Json.string(top, "schemaVersion", "");
        if (!schema.startsWith("1.")) {
            throw new IllegalArgumentException("a list of another format (" + clean(schema, 20) + ")");
        }
        List<Entry> out = new ArrayList<>();
        // jakmods.dev writes texturePacks as [] when it has none: no packs, not a mistake.
        addAll(out, source, Json.object(top.get("texturePacks")), true);
        addAll(out, source, Json.object(top.get("mods")), false);
        return out;
    }

    private static void addAll(List<Entry> out, String source, Map<String, Object> mods, boolean packs) {
        if (mods == null) return;
        for (Map.Entry<String, Object> m : mods.entrySet()) {
            Map<String, Object> mod = Json.object(m.getValue());
            if (mod == null || out.size() >= MOST_ENTRIES) continue;
            out.add(entry(source, clean(m.getKey(), 80), mod, packs));
        }
    }

    private static Entry entry(String source, String key, Map<String, Object> mod, boolean pack) {
        String name = clean(Json.string(mod, "displayName", key), 80);
        String description = clean(Json.string(mod, "description", ""), 600);
        String authors = clean(String.join(", ", strings(mod.get("authors"))), 120);
        if (authors.isEmpty()) authors = clean(Json.string(mod, "authors", ""), 120);
        List<String> games = new ArrayList<>();
        for (String g : strings(mod.get("supportedGames"))) {
            for (String known : VrOptions.GAMES) {
                if (known.equals(g)) games.add(g);
            }
        }
        if (games.isEmpty() && !strings(mod.get("supportedGames")).isEmpty()) {
            games.add("none");  // only games this port does not have (Jak X)
        }
        String page = "";
        for (String k : new String[] {"websiteUrl", "externalLink"}) {
            String u = Json.string(mod, k, "");
            if (page.isEmpty() && u.startsWith("https://") && u.length() < 400) page = u;
        }
        if (page.isEmpty()) page = SOURCE_OURS.equals(source) ? OURS_PAGE : COMMUNITY_SITE;
        // The newest version.
        Map<String, Object> best = null;
        String version = "";
        List<Object> versions = Json.array(mod.get("versions"));
        if (versions != null) {
            for (Object v : versions) {
                Map<String, Object> vm = Json.object(v);
                if (vm == null) continue;
                String ver = Json.string(vm, "version", "");
                if (best == null || compareVersions(ver, version) > 0) {
                    best = vm;
                    version = ver;
                }
            }
        }
        Map<String, Object> assets = best == null ? null : Json.object(best.get("assets"));
        Kind kind;
        String url = "";
        String why = null;
        if (pack) {
            kind = Kind.TEXTURE_PACK;
            url = asset(assets, "any", "texture-pack", "windows", "linux", "macos");
            if (url.isEmpty() && best != null) url = Json.string(best, "downloadUrl", "");
        } else if (SOURCE_OURS.equals(source) && !asset(assets, "quest").isEmpty()) {
            kind = Kind.QUEST_PACKAGE;
            url = asset(assets, "quest");
        } else {
            kind = Kind.FLAT_SCREEN;
            why = "Made for a flat screen: one of OpenGOAL's own mods, a whole game of its own (its own program and "
                + "code), so it cannot run in VR here. Play it with the OpenGOAL launcher.";
        }
        if (why == null) {
            if (best == null || url.isEmpty()) {
                why = "Its list has no file to add yet.";
            } else if (!allowedFile(url)) {
                why = "Its file is not on GitHub: download it from its page, then add it from the file.";
            }
        }
        String sha = best == null ? "" : Json.string(best, "sha256", "").toLowerCase(Locale.ROOT);
        if (!sha.matches("[0-9a-f]{64}")) sha = "";
        long size = best == null ? 0 : number(best.get("size"));
        String published = best == null ? "" : clean(Json.string(best, "publishedDate", ""), 40);
        return new Entry(source, key, name, description, authors, clean(version, 30), published, page, url, sha, size,
            kind, Collections.unmodifiableList(games), why);
    }

    /** The first of keys that assets has a link for, or "". */
    private static String asset(Map<String, Object> assets, String... keys) {
        if (assets == null) return "";
        for (String k : keys) {
            String u = Json.string(assets, k, "");
            if (!u.isEmpty()) return u;
        }
        return "";
    }

    private static List<String> strings(Object value) {
        List<String> out = new ArrayList<>();
        List<Object> list = Json.array(value);
        if (list != null) {
            for (Object o : list) {
                if (o instanceof String && out.size() < 50) out.add(clean((String) o, 60));
            }
        }
        return out;
    }

    private static long number(Object value) {
        if (value instanceof BigDecimal) {
            try {
                return ((BigDecimal) value).longValueExact();
            } catch (ArithmeticException e) {
                return 0;
            }
        }
        return 0;
    }

    /** Printable text on one line, at most most characters. */
    static String clean(String s, int most) {
        String t = s == null ? "" : s.replaceAll("[\\p{Cntrl}]+", " ").trim();
        return t.length() > most ? t.substring(0, most) : t;
    }

    /** Versions number by number ("1.10" after "1.9", a leading "v" left out); a part not a number by its text. */
    public static int compareVersions(String a, String b) {
        String[] x = strip(a).split("[.+-]"), y = strip(b).split("[.+-]");
        for (int i = 0; i < Math.max(x.length, y.length); i++) {
            String p = i < x.length ? x[i] : "0", q = i < y.length ? y[i] : "0";
            int c = p.matches("\\d{1,18}") && q.matches("\\d{1,18}") ? Long.compare(Long.parseLong(p), Long.parseLong(q))
                : p.compareTo(q);
            if (c != 0) return c < 0 ? -1 : 1;
        }
        return 0;
    }

    private static String strip(String v) {
        String t = v == null ? "" : v.trim();
        return t.startsWith("v") || t.startsWith("V") ? t.substring(1) : t;
    }

    // ---- the network ----

    /** The two lists' own addresses, over HTTPS only. */
    static boolean allowedList(String address) {
        return OURS.equals(address) || COMMUNITY.equals(address);
    }

    /** Where a mod's file may come from: GitHub (its releases, and where it keeps their files), over HTTPS only. */
    static boolean allowedFile(String address) {
        try {
            URI uri = new URI(address);
            String host = uri.getHost();
            return "https".equals(uri.getScheme()) && uri.getUserInfo() == null
                && (uri.getPort() == -1 || uri.getPort() == 443)
                && ("github.com".equals(host) || "objects.githubusercontent.com".equals(host)
                    || "release-assets.githubusercontent.com".equals(host)
                    || "raw.githubusercontent.com".equals(host));
        } catch (Exception e) {
            return false;
        }
    }

    private static HttpURLConnection open(String address, String agent) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(30000);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent", agent);
        return connection;
    }

    /** A list's bytes (each redirect checked before it is followed). */
    private static byte[] get(String address, String agent, int limit) throws IOException {
        boolean first = true;
        for (int i = 0; i < 6; i++) {
            if (!(first ? allowedList(address) : allowedFile(address) || allowedList(address))) {
                throw new IOException("an address this launcher does not read from");
            }
            first = false;
            HttpURLConnection connection = open(address, agent);
            try {
                int code = connection.getResponseCode();
                if (code >= 300 && code < 400) {
                    String next = connection.getHeaderField("Location");
                    if (next == null) throw new IOException("a redirect to nowhere");
                    address = new URL(new URL(address), next).toString();
                    continue;
                }
                if (code != 200) throw new IOException("the server answered " + code);
                try (InputStream in = connection.getInputStream();
                     java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
                    byte[] buffer = new byte[65536];
                    for (int n = in.read(buffer); n != -1; n = in.read(buffer)) {
                        if (out.size() + n > limit) throw new IOException("the list is too large");
                        out.write(buffer, 0, n);
                    }
                    return out.toByteArray();
                }
            } finally {
                connection.disconnect();
            }
        }
        throw new IOException("too many redirects");
    }

    /**
     * An entry's file, downloaded into folder (download.partial, replaced each time): its size and SHA-256 those the
     * list gives. An IOException in the player's words when not.
     */
    public static File download(Entry e, String agent, File folder) throws IOException {
        if (e.why != null || e.url.isEmpty()) throw new IOException(e.why == null ? "Nothing to add." : e.why);
        if (!folder.isDirectory() && !folder.mkdirs()) throw new IOException("Cannot make " + folder);
        File to = new File(folder, "download.partial");
        String address = e.url;
        for (int i = 0; i < 6; i++) {
            if (!allowedFile(address)) throw new IOException("its file is not on GitHub");
            HttpURLConnection connection = open(address, agent);
            try {
                int code = connection.getResponseCode();
                if (code >= 300 && code < 400) {
                    String next = connection.getHeaderField("Location");
                    if (next == null) throw new IOException("a redirect to nowhere");
                    address = new URL(new URL(address), next).toString();
                    continue;
                }
                if (code != 200) throw new IOException("GitHub answered " + code);
                if (connection.getContentLengthLong() > MOST_FILE) throw new IOException("its file is larger than 2 GB");
                MessageDigest sha = sha256();
                long total = 0;
                try (InputStream in = connection.getInputStream(); OutputStream out = new FileOutputStream(to)) {
                    byte[] buffer = new byte[65536];
                    for (int n = in.read(buffer); n != -1; n = in.read(buffer)) {
                        total += n;
                        if (total > MOST_FILE) throw new IOException("its file is larger than 2 GB");
                        sha.update(buffer, 0, n);
                        out.write(buffer, 0, n);
                    }
                }
                if (e.size > 0 && total != e.size) {
                    throw new IOException(e.name + "'s file is not the size its list gives: not added.");
                }
                if (!e.sha256.isEmpty() && !hex(sha.digest()).equals(e.sha256)) {
                    throw new IOException(e.name + "'s file is not the one its list names (its SHA-256): not added.");
                }
                return to;
            } catch (IOException ex) {
                if (to.isFile() && !to.delete()) to.deleteOnExit();
                throw ex;
            } finally {
                connection.disconnect();
            }
        }
        throw new IOException("too many redirects");
    }

    private static MessageDigest sha256() throws IOException {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("no SHA-256 here");
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder b = new StringBuilder();
        for (byte x : bytes) b.append(String.format(Locale.ROOT, "%02x", x));
        return b.toString();
    }
}
