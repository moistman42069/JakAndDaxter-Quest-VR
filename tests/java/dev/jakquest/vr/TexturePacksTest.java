package dev.jakquest.vr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** A check of TexturePacks on made-up packs: java -cp classes;. dev.jakquest.vr.TexturePacksTest <scratch folder> */
public final class TexturePacksTest {
    static boolean failed;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok   " : "FAIL ") + what);
        if (!ok) failed = true;
    }

    static byte[] zip(String[][] entries) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try (ZipOutputStream z = new ZipOutputStream(b)) {
            for (String[] e : entries) {
                z.putNextEntry(new ZipEntry(e[0]));
                z.write(e[1].getBytes(StandardCharsets.UTF_8));
                z.closeEntry();
            }
        }
        return b.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        File scratch = new File(args[0]);
        TexturePacks.deleteTree(scratch);
        File root = new File(scratch, "packs"), project = new File(scratch, "data");
        byte[] a = zip(new String[][] {
            {"metadata.json", "{\"name\": \"Shiny \\\"Jak\\\"\", \"version\": \"1.2\", \"author\": \"Someone\", \"description\": \"Brighter.\"}"},
            {"custom_assets/jak1/texture_replacements/common/jak-skin.png", "A1"},
            {"custom_assets/jak1/texture_replacements/village1-vis-pris/sand.png", "A2"},
            {"custom_assets/jak2/texture_replacements/common/x.png", "other game"},
            {"custom_assets/jak1/texture_replacements/../../evil.png", "escape"},
            {"custom_assets/jak1/texture_replacements/readme.txt", "not a picture"}});
        byte[] b = zip(new String[][] {
            {"texture_replacements/common/jak-skin.png", "B1"},
            {"texture_replacements/common/eco.PNG", "B2"}});
        byte[] none = zip(new String[][] {{"readme.txt", "nothing"}});

        check(TexturePacks.list(root, "jak1").isEmpty(), "no packs at first");
        check(!TexturePacks.changed(root, "jak1", project), "nothing changed with none on and none built");
        TexturePacks.Pack pa = TexturePacks.add(root, "jak1", new ByteArrayInputStream(a), "C:\\down\\Shiny Jak.zip");
        check(pa.id.equals("shiny-jak") && pa.pictures == 2, "pack A: id shiny-jak, 2 pictures (" + pa.id + ", " + pa.pictures + ")");
        check(pa.name.equals("Shiny \"Jak\"") && pa.version.equals("1.2") && pa.author.equals("Someone"), "pack A's metadata: " + pa.line());
        check(!new File(root, "evil.png").exists() && !new File(scratch, "evil.png").exists(), "the escape path was not written");
        TexturePacks.Pack pb = TexturePacks.add(root, "jak1", new ByteArrayInputStream(b), "flat.zip");
        check(pb.pictures == 2 && pb.name.equals("flat"), "pack B: texture_replacements at its top, named from its file");
        try {
            TexturePacks.add(root, "jak1", new ByteArrayInputStream(none), "none.zip");
            check(false, "a zip with no pictures is refused");
        } catch (IOException e) {
            check(e.getMessage().contains("No pictures"), "a zip with no pictures is refused: " + e.getMessage());
        }
        check(!new File(new File(root, "jak1"), "none").exists() && !new File(new File(root, "jak1"), "none.partial").exists(), "and leaves nothing");
        List<TexturePacks.Pack> list = TexturePacks.list(root, "jak1");
        check(list.size() == 2 && list.get(0).id.equals("flat") && list.get(1).id.equals("shiny-jak"), "the newest first");
        check(TexturePacks.changed(root, "jak1", project), "changed before a build");

        TexturePacks.apply(root, "jak1", project);
        File out = new File(project, "custom_assets/jak1/texture_replacements");
        check(read(new File(out, "common/jak-skin.png")).equals("B1"), "the first in the list wins");
        check(read(new File(out, "village1-vis-pris/sand.png")).equals("A2") && read(new File(out, "common/eco.PNG")).equals("B2"), "both packs' pictures in");
        check(!TexturePacks.changed(root, "jak1", project), "not changed after the build");

        TexturePacks.move(root, "jak1", "flat", 1);
        check(TexturePacks.list(root, "jak1").get(0).id.equals("shiny-jak"), "moved down");
        check(TexturePacks.changed(root, "jak1", project), "the order counts as a change");
        TexturePacks.apply(root, "jak1", project);
        check(read(new File(out, "common/jak-skin.png")).equals("A1"), "now pack A wins");

        TexturePacks.setOn(root, "jak1", "shiny-jak", false);
        TexturePacks.apply(root, "jak1", project);
        check(!new File(out, "village1-vis-pris/sand.png").exists() && read(new File(out, "common/jak-skin.png")).equals("B1"), "a pack off is out");

        TexturePacks.remove(root, "jak1", "shiny-jak");
        check(TexturePacks.list(root, "jak1").size() == 1 && !new File(root, "jak1/shiny-jak").exists(), "removed, folder too");
        TexturePacks.remove(root, "jak1", "flat");
        TexturePacks.apply(root, "jak1", project);
        check(!out.exists() || out.list().length == 0, "none on: the folder is empty");
        check(!TexturePacks.changed(root, "jak1", project), "and not changed");
        check(TexturePacks.picturePath("x/custom_assets/jak1/texture_replacements/a/b.png", "jak1").equals("a/b.png"), "a pack in a folder of its own");
        check(TexturePacks.picturePath("texture_replacements/C:/b.png", "jak1") == null, "a drive in the path refused");
        check(TexturePacks.picturePath("texture_replacements//b.png", "jak1") == null, "an empty part refused");
        imports(new File(scratch, "imports"));
        TexturePacks.deleteTree(scratch);
        System.out.println(failed ? "FAILED" : "PASS");
        System.exit(failed ? 1 : 0);
    }

    /** 0.1.3: packs imported as players find them on the web (TexturePacks.Stager). */
    static void imports(File root) throws IOException {
        byte[] inner = zip(new String[][] {{"custom_assets/jak2/texture_replacements/common/gun.png", "G"}});
        byte[] outer = zipOf(new String[] {"readme.txt", "Gun Pack.zip"}, new byte[][] {"hi".getBytes(StandardCharsets.UTF_8), inner});
        TexturePacks.Imported done = stage(root, "Gun Pack v2.zip", outer, "jak1");
        check(done != null && done.games.equals(java.util.Arrays.asList("jak2")) && done.packs.get(0).pictures == 1
            && TexturePacks.list(root, "jak2").size() == 1 && TexturePacks.list(root, "jak1").isEmpty(),
            "a pack in the zip, for Jak II, imported from Jak 1's: it goes to Jak II (" + (done == null ? "-" : done.summary()) + ")");
        check(done != null && done.summary().endsWith("for Jak II") && done.packs.get(0).from.isEmpty()
            && done.packs.get(0).origin().equals("Imported"), "its summary and origin: " + (done == null ? "-" : done.summary()));

        byte[] top = zip(new String[][] {{"texture_replacements/common/a.png", "T"}});
        File onDisk = new File(root.getParentFile(), "Top Pack.zip");
        java.nio.file.Files.write(onDisk.toPath(), top);
        try (TexturePacks.Stager s = new TexturePacks.Stager(root, onDisk.getName(), null)) {
            s.zip(onDisk);
            boolean asked = false;
            try {
                s.finish(null, null);
            } catch (TexturePacks.NeedsGame e) {
                asked = true;
            }
            TexturePacks.Imported d = s.finish("jak3", null);
            check(asked && d.games.equals(java.util.Arrays.asList("jak3")) && d.packs.get(0).id.equals("top-pack"),
                "a zip on disk that names no game: asked, then Jak 3's (" + d.summary() + ")");
        }

        byte[] said = zip(new String[][] {{"metadata.json", "{\"name\": \"Said\", \"supportedGames\": [\"jak2\"]}"},
            {"texture_replacements/hud/b.png", "S"}});
        done = stage(root, "said.zip", said, "jak1");
        check(done != null && done.games.equals(java.util.Arrays.asList("jak2")), "metadata.json's supportedGames wins over the page it was imported from");

        File folder = new File(root.getParentFile(), "My Pack");
        write(new File(folder, "texture_replacements/common/c.png"), "F1");
        write(new File(folder, "texture_replacements/village1-vis-tfrag/d.png"), "F2");
        write(new File(folder, "preview.png"), "not in it");
        try (TexturePacks.Stager s = new TexturePacks.Stager(root, folder.getName(), null)) {
            s.folder(folder);
            TexturePacks.Imported d = s.finish("jak1", null);
            check(d.packs.get(0).id.equals("my-pack") && d.packs.get(0).pictures == 2, "a folder: " + d.summary());
        }
        try (TexturePacks.Stager s = new TexturePacks.Stager(root, "texture_replacements", null)) {
            s.folder(new File(folder, "texture_replacements"));
            TexturePacks.Imported d = s.finish("jak1", null);
            check(d.packs.get(0).id.equals("my-pack-2") && d.packs.get(0).pictures == 2,
                "its texture_replacements picked itself: named after the folder above (" + d.packs.get(0).id + ")");
        }

        byte[] pages = zip(new String[][] {{"Pack/village1-vis-tfrag/a.png", "P1"}, {"Pack/common/b.png", "P2"},
            {"Pack/preview.png", "P3"}});
        done = stage(root, "pages.zip", pages, "jak1");
        check(done != null && done.packs.get(0).pictures == 2
            && new File(root, "jak1/pages/texture_replacements/common/b.png").isFile(),
            "texture pages with no texture_replacements above them: taken as one");

        failsWith(root, "pcsx2.zip", zip(new String[][] {{"textures/SCUS-97124/replacements/0a1b2c3d4e5f6a7b-00001234.png", "X"}}),
            "jak1", "PCSX2", "an emulator's pack refused");
        failsWith(root, "shots.zip", zip(new String[][] {{"shots/one.png", "X"}}), "jak1", "not in a texture_replacements",
            "pictures in no texture page refused");
        failsWith(root, "a.7z", new byte[] {'7', 'z', (byte) 0xBC, (byte) 0xAF, 0x27, 0x1C, 0, 4}, "jak1", ".7z", "a .7z refused");
        failsWith(root, "a.rar", "Rar!\u001a\u0007\u0000".getBytes(StandardCharsets.ISO_8859_1), "jak1", ".rar", "a .rar refused");
        failsWith(root, "a.txt", "hello".getBytes(StandardCharsets.UTF_8), "jak1", "not a .zip", "a file that is no zip refused");
        try {
            TexturePacks.add(root, "jak2", new java.io.ByteArrayInputStream(zip(new String[][] {
                {"custom_assets/jak1/texture_replacements/common/x.png", "1"}})), "one.zip");
            check(false, "Browse mods' add of another game's pack refused");
        } catch (IOException e) {
            check(e.getMessage().contains("for Jak 1, not Jak II"), "Browse mods' add of another game's pack refused: " + e.getMessage());
        }
        String[] left = root.list((dir, name) -> name.startsWith(".import-"));
        check(left != null && left.length == 0, "no import folders left behind");
    }

    static TexturePacks.Imported stage(File root, String name, byte[] data, String preferred) throws IOException {
        try (TexturePacks.Stager s = new TexturePacks.Stager(root, name, null)) {
            s.zip(new java.io.ByteArrayInputStream(data));
            return s.finish(preferred, null);
        }
    }

    static void failsWith(File root, String name, byte[] data, String preferred, String words, String what) throws IOException {
        try {
            stage(root, name, data, preferred);
            check(false, what);
        } catch (IOException e) {
            check(e.getMessage().contains(words), what + ": " + e.getMessage());
        }
    }

    static byte[] zipOf(String[] names, byte[][] data) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try (ZipOutputStream z = new ZipOutputStream(b)) {
            for (int i = 0; i < names.length; i++) {
                z.putNextEntry(new ZipEntry(names[i]));
                z.write(data[i]);
                z.closeEntry();
            }
        }
        return b.toByteArray();
    }

    static void write(File f, String text) throws IOException {
        f.getParentFile().mkdirs();
        java.nio.file.Files.write(f.toPath(), text.getBytes(StandardCharsets.UTF_8));
    }

    static String read(File f) throws IOException {
        return f.isFile() ? new String(java.nio.file.Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8) : "";
    }
}
