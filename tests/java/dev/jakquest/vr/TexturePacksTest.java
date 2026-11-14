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
        TexturePacks.deleteTree(scratch);
        System.out.println(failed ? "FAILED" : "PASS");
        System.exit(failed ? 1 : 0);
    }

    static String read(File f) throws IOException {
        return f.isFile() ? new String(java.nio.file.Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8) : "";
    }
}
