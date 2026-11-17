package dev.jakquest.vr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * A check of the PC launcher's "build it again" after an update (PcSetup.newSources, 0.1.2) on made-up folders:
 * java -cp classes;. dev.jakquest.vr.GameCodeTest <scratch folder>
 */
public final class GameCodeTest {
    static boolean failed;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok   " : "FAIL ") + what);
        if (!ok) failed = true;
    }

    static void write(File file, String text) throws IOException {
        file.getParentFile().mkdirs();
        Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) throws Exception {
        File scratch = new File(args[0]);
        TexturePacks.deleteTree(scratch);
        PcPaths paths = new PcPaths(new File(scratch, "install"), new File(scratch, "home"), new File(scratch, "logs"));
        String id = "a".repeat(64), other = "b".repeat(64);
        File codeFile = new File(paths.template(), "game-code.txt");
        File mark = paths.readyMark("jak1");
        new File(paths.compiled("jak1"), "iso").mkdirs();

        // No game-code.txt (a launcher built from the repository): every other release builds again, as before 0.1.2.
        write(mark, "0.1.1");
        check(PcSetup.newSources(paths, "jak1"), "no game code: a game built by 0.1.1 is built again");
        write(mark, PcBuild.VERSION);
        check(!PcSetup.newSources(paths, "jak1"), "no game code: a game built by this release is not");
        check(PcSetup.readyMark(paths).equals(PcBuild.VERSION), "no game code: the mark is the release alone");

        // This release's game code, the same as 0.1.1's.
        write(codeFile, "id " + id + "\ndata 2\nsame 0.1.1\n");
        PcSetup.GameCode code = PcSetup.gameCode(paths);
        check(code.id.equals(id) && code.data.equals("2") && code.same.size() == 1 && code.same.get(0).equals("0.1.1"),
            "game-code.txt read: id, data 2, the same as 0.1.1");
        write(mark, "0.1.1");
        check(!PcSetup.newSources(paths, "jak1"), "a game built by 0.1.1 (the same game code) is not built again");
        check(PcSetup.status(paths, "jak1") == PcSetup.Status.READY, "... and is ready to play");
        check(PcSetup.builtBy(paths, "jak1").equals("0.1.1"), "... built by 0.1.1");
        write(mark, "0.1.0");
        check(PcSetup.newSources(paths, "jak1"), "a game built by 0.1.0 (other game code) is built again");
        check(PcSetup.status(paths, "jak1") == PcSetup.Status.OUTDATED, "... and shows Build it again");
        write(mark, "0.1.1\r\n");
        check(!PcSetup.newSources(paths, "jak1"), "a mark with a Windows line end reads the same");

        // A mark written by 0.1.2 and later: the release, then its game code and game data version.
        String now = PcSetup.readyMark(paths);
        check(now.startsWith(PcBuild.VERSION + "\n") && now.contains("id " + id) && now.contains("data 2"),
            "the mark of a game built now has the release, the id and the data version");
        write(mark, "0.1.9\nid " + id + "\ndata 2\n");
        check(!PcSetup.newSources(paths, "jak1"), "another release's mark with the same id and data: not built again");
        check(PcSetup.builtBy(paths, "jak1").equals("0.1.9"), "... built by its first line's release");
        write(mark, "0.1.9\nid " + other + "\ndata 2\n");
        check(PcSetup.newSources(paths, "jak1"), "another id: built again");
        write(mark, "0.1.9\nid " + id + "\ndata 1\n");
        check(PcSetup.newSources(paths, "jak1"), "the same id, another game data version: built again");
        write(mark, "0.1.1\nid " + other + "\ndata 2\n");
        check(PcSetup.newSources(paths, "jak1"), "a mark's own id wins over the release's list of the same");

        // A release with no release before it of the same game code.
        write(codeFile, "id " + id + "\ndata 3\nsame \n");
        write(mark, "0.1.1");
        check(PcSetup.gameCode(paths).same.isEmpty(), "an empty list of the same reads as none");
        check(PcSetup.newSources(paths, "jak1"), "then a game built by 0.1.1 is built again");

        // 0.1.3, building again only what changed: the compiler's sources and the decompiler's inputs in the mark.
        write(codeFile, "id " + id + "\ndata 3\ncompiler abc123\nsame \n");
        check(PcSetup.gameCode(paths).compiler.equals("abc123"), "the release's compiler line is read");
        check(new PcSetup.GameCode("0.1.2\nid " + id + "\ndata 3\n").compiler.isEmpty(),
            "a mark of 0.1.2 has no compiler: everything is compiled again");
        check(PcSetup.markWord("0.1.3\r\nid x\r\ndecomp d00d\r\n", "decomp").equals("d00d"),
            "a mark's decompile key is read, Windows line ends too");
        check(PcSetup.markWord("0.1.3\nid x\n", "decomp").isEmpty(), "no decompile key: decompiled again");
        String key1 = PcSetup.decompileKey(paths, "jak1", PcSetup.gameCode(paths));
        check(key1.length() == 64 && key1.equals(PcSetup.decompileKey(paths, "jak1", PcSetup.gameCode(paths))),
            "the decompile key is a SHA-256, the same twice");
        File config = new File(paths.data(), "decompiler/config/jak1/jak1_config.jsonc");
        write(config, "{ \"a\": 1 }");
        String key2 = PcSetup.decompileKey(paths, "jak1", PcSetup.gameCode(paths));
        check(!key2.equals(key1), "the decompiler's config changed: another key");
        write(codeFile, "id " + id + "\ndata 4\ncompiler abc123\nsame \n");
        check(!PcSetup.decompileKey(paths, "jak1", PcSetup.gameCode(paths)).equals(key2),
            "another game data version: another key");

        // 0.1.3: an update's sources copied over the last ones (syncTree): a file with the same bytes keeps its time (so
        // the compiler's make leaves what depends only on it), a changed one is copied, one the release no longer has goes.
        File from = new File(scratch, "sync/from"), to = new File(scratch, "sync/to");
        write(new File(from, "same.gc"), "(same)");
        write(new File(from, "kept/changed.gc"), "(old)");
        write(new File(from, "gone.gc"), "(gone)");
        PcSetup.syncTree(from, to);
        long old = (System.currentTimeMillis() - 3_600_000L) / 1000 * 1000;
        new File(to, "same.gc").setLastModified(old);
        new File(to, "kept/changed.gc").setLastModified(old);
        write(new File(from, "kept/changed.gc"), "(new)");  // the same length, other bytes
        new File(from, "gone.gc").delete();
        write(new File(from, "added.gc"), "(added)");
        PcSetup.syncTree(from, to);
        check(new File(to, "same.gc").lastModified() == old, "an update: a file with the same bytes keeps its time");
        File changed = new File(to, "kept/changed.gc");
        check(new String(Files.readAllBytes(changed.toPath()), StandardCharsets.UTF_8).equals("(new)")
            && changed.lastModified() > old, "... a changed one (the same length) is copied, newer");
        check(!new File(to, "gone.gc").exists(), "... one the release no longer has is removed");
        check(new File(to, "added.gc").isFile(), "... a new one is copied");

        // Not set up at all.
        mark.delete();
        check(PcSetup.status(paths, "jak1") == PcSetup.Status.NONE, "no mark: not set up");

        TexturePacks.deleteTree(scratch);
        System.out.println(failed ? "GameCodeTest: FAILED" : "GameCodeTest: all passed");
        System.exit(failed ? 1 : 0);
    }
}
