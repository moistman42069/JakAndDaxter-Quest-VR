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

        // Not set up at all.
        mark.delete();
        check(PcSetup.status(paths, "jak1") == PcSetup.Status.NONE, "no mark: not set up");

        TexturePacks.deleteTree(scratch);
        System.out.println(failed ? "GameCodeTest: FAILED" : "GameCodeTest: all passed");
        System.exit(failed ? 1 : 0);
    }
}
