package dev.jakquest.vr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Offline checks of ModList (Browse mods' lists: reading, version order, the hosts files come from) and TexturePacks'
 * Add and Update from a list entry (0.1.1 item 38): java -cp classes;. dev.jakquest.vr.ModListTest <scratch folder>
 */
public final class ModListTest {
    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok   " : "FAIL ") + what);
        if (!ok) failed++;
    }

    static byte[] zip(String metadata, String picture) throws Exception {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try (ZipOutputStream z = new ZipOutputStream(b)) {
            z.putNextEntry(new ZipEntry("metadata.json"));
            z.write(metadata.getBytes(StandardCharsets.UTF_8));
            z.putNextEntry(new ZipEntry("custom_assets/jak1/texture_replacements/page/" + picture));
            z.write(new byte[] {(byte) 0x89, 'P', 'N', 'G'});
            z.putNextEntry(new ZipEntry("custom_assets/jak2/texture_replacements/page/other.png"));
            z.write(new byte[] {1});
        }
        return b.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        String community = "{\"schemaVersion\":\"1.0.0\",\"sourceName\":\"JakMods\",\"lastUpdated\":\"x\","
            + "\"mods\":{\"speedrun-practice\":{\"displayName\":\"Speedrun Practice\",\"description\":\"Practice.\","
            + "\"authors\":[\"a\",\"b\"],\"tags\":[\"practice\"],\"supportedGames\":[\"jak1\"],\"externalLink\":null,"
            + "\"versions\":[{\"version\":\"0.5.8\",\"publishedDate\":\"2024-01-02T00:00:00Z\",\"supportedGames\":[\"jak1\"],"
            + "\"settings\":{\"decompConfigOverride\":\"\",\"shareVanillaSaves\":false},\"assets\":{\"windows\":"
            + "\"https://github.com/o/r/releases/download/v0.5.8/windows-v0.5.8.zip\",\"linux\":null,\"macos\":null},"
            + "\"assetDownloadCounts\":{\"windows\":5}}]},"
            + "\"two\":{\"displayName\":\"Two\",\"description\":\"\",\"authors\":[],\"tags\":[],\"supportedGames\":[\"jak2\",\"jak3\"],"
            + "\"websiteUrl\":\"https://example.org/two\",\"versions\":[]},"
            + "\"x\":{\"displayName\":\"X only\",\"description\":\"\",\"authors\":[],\"tags\":[],\"supportedGames\":[\"jakx\"],\"versions\":[]}},"
            + "\"texturePacks\":[]}";
        List<ModList.Entry> c = ModList.read(ModList.SOURCE_COMMUNITY, community);
        check(c.size() == 3, "community: three mods, no packs ([] read as none)");
        check(c.get(0).kind == ModList.Kind.FLAT_SCREEN && c.get(0).why != null, "community mod: flat screen only");
        check(c.get(0).authors.equals("a, b") && c.get(0).version.equals("0.5.8"), "authors joined, version read");
        check(c.get(0).page.equals(ModList.COMMUNITY_SITE), "no page of its own: jakmods.dev");
        check(c.get(1).page.equals("https://example.org/two"), "its websiteUrl as its page");
        check(c.get(1).forGame("jak2") && c.get(1).forGame("jak3") && !c.get(1).forGame("jak1"), "games");
        check(!c.get(2).forGame("jak1") && !c.get(2).forGame("jak2"), "Jak X only: for none of these games");

        String ours = "{\"schemaVersion\":\"1.0.0\",\"sourceName\":\"Jak and Daxter Quest VR\",\"lastUpdated\":\"x\","
            + "\"texturePacks\":{\"hd\":{\"displayName\":\"HD\",\"description\":\"Sharper.\",\"authors\":[\"p\"],"
            + "\"tags\":[],\"supportedGames\":[\"jak1\"],\"versions\":["
            + "{\"version\":\"1.2.0\",\"publishedDate\":\"2026-01-01\",\"assets\":{\"any\":\"https://github.com/o/r/releases/download/1.2/hd.zip\"}},"
            + "{\"version\":\"v1.10.0\",\"publishedDate\":\"2026-02-01\",\"assets\":{\"any\":\"https://github.com/o/r/releases/download/1.10/hd.zip\"},"
            + "\"sha256\":\"AB" + "0".repeat(62) + "\",\"size\":1234},"
            + "{\"version\":\"1.9.0\",\"publishedDate\":\"2026-01-15\",\"assets\":{\"any\":\"https://github.com/o/r/releases/download/1.9/hd.zip\"}}]},"
            + "\"elsewhere\":{\"displayName\":\"Elsewhere\",\"description\":\"\",\"authors\":[],\"tags\":[],\"supportedGames\":[\"jak1\"],"
            + "\"versions\":[{\"version\":\"1\",\"publishedDate\":\"\",\"assets\":{\"any\":\"http://example.com/p.zip\"}}]}},"
            + "\"mods\":{\"vrmod\":{\"displayName\":\"VR mod\",\"description\":\"\",\"authors\":[],\"tags\":[],\"supportedGames\":[\"jak1\"],"
            + "\"versions\":[{\"version\":\"0.1\",\"publishedDate\":\"\",\"assets\":{\"quest\":\"https://github.com/o/r/releases/download/0.1/m.zip\"}}]}}}";
        List<ModList.Entry> o = ModList.read(ModList.SOURCE_OURS, ours);
        ModList.Entry hd = o.get(0);
        check(hd.kind == ModList.Kind.TEXTURE_PACK && hd.why == null, "our pack: a texture pack that can be added");
        check(hd.version.equals("v1.10.0") && hd.url.endsWith("1.10/hd.zip"), "the newest version (1.10 after 1.9)");
        check(hd.sha256.equals("ab" + "0".repeat(62)) && hd.size == 1234, "its SHA-256 and size");
        check(o.get(1).why != null && o.get(1).why.contains("not on GitHub"), "a file not on GitHub: not added");
        ModList.Entry vr = o.get(2);
        check(vr.kind == ModList.Kind.QUEST_PACKAGE && vr.whyNot(true) == null && vr.whyNot(false) != null,
            "a Quest package: the Quest's launcher only");
        check(ModList.compareVersions("v1.10", "1.9") > 0 && ModList.compareVersions("1.0", "1") == 0,
            "versions compared number by number");
        boolean refused;
        try {
            ModList.read("x", "{\"schemaVersion\":\"2.0.0\",\"mods\":{}}");
            refused = false;
        } catch (IllegalArgumentException e) {
            refused = true;
        }
        check(refused, "another format refused");
        check(!ModList.allowedFile("https://github.com.evil.example/x.zip") && ModList.allowedFile(hd.url)
            && !ModList.allowedFile("https://user@github.com/x.zip"), "only GitHub's hosts for files");

        File root = new File(args[0]);
        TexturePacks.Pack first = TexturePacks.add(root, "jak1", new ByteArrayInputStream(zip(
            "{\"name\":\"\",\"authors\":\"pack author\",\"version\":\"1.0.0\"}", "a.png")), "hd.zip", hd);
        check(first.pictures == 1, "only this game's picture taken");
        TexturePacks.Pack file = TexturePacks.add(root, "jak1", new ByteArrayInputStream(zip("{\"name\":\"Mine\"}", "b.png")), "mine.zip");
        TexturePacks.setOn(root, "jak1", first.id, false);
        List<TexturePacks.Pack> before = TexturePacks.list(root, "jak1");
        TexturePacks.Pack listed = null;
        for (TexturePacks.Pack p : before) if (p.id.equals(first.id)) listed = p;
        check(listed != null && listed.from.equals(hd.ref()) && listed.version.equals("v1.10.0")
            && listed.name.equals("HD") && listed.author.equals("pack author"), "kept: from, the list's version, name, authors");
        check(before.get(0).id.equals(file.id) && before.get(1).id.equals(first.id), "order: the newest added first");
        TexturePacks.Pack up = TexturePacks.update(root, "jak1", first.id, new ByteArrayInputStream(zip(
            "{\"name\":\"HD\"}", "c.png")), "hd.zip", hd);
        List<TexturePacks.Pack> after = TexturePacks.list(root, "jak1");
        check(after.size() == 2 && after.get(1).id.equals(up.id) && !after.get(1).on && !up.id.equals(first.id)
            && !new File(new File(root, "jak1"), first.id).exists(), "update: in the old one's place, still off, old gone");
        List<TexturePacks.Pack> old = TexturePacks.list(root, "jak1");
        check(old.get(0).from.isEmpty(), "a pack from a file has no list entry");
        System.out.println(failed == 0 ? "ALL OK" : failed + " FAILED");
        System.exit(failed == 0 ? 0 : 1);
    }
}
