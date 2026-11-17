package dev.jakquest.vr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * A check of the PC launcher's "Where the games are kept" (PcDataFolder, PcPaths.data(); 0.1.3) on made-up folders:
 * java -cp classes;. dev.jakquest.vr.DataFolderTest <scratch folder> [<a scratch folder on another drive>]
 * The second folder tries the copy between drives (the first only renames on its own drive).
 */
public final class DataFolderTest {
    static boolean failed;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok   " : "FAIL ") + what);
        if (!ok) failed = true;
    }

    static void write(File file, String text) throws IOException {
        file.getParentFile().mkdirs();
        Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
    }

    static String read(File file) {
        return PcPaths.read(file);
    }

    /** A made-up set-up: a few files in folders, one empty folder, one 1 MB file. */
    static void fill(File data) throws IOException {
        write(new File(data, "ready-jak1.txt"), "0.1.3\nid x\ndata 2\n");
        write(new File(data, "iso_data/jak1/buildinfo.json"), "{\"gameName\":\"jak1\"}");
        write(new File(data, "out/jak1/iso/GAME.CGO"), "cgo");
        new File(data, "out/jak1/obj").mkdirs();
        byte[] big = new byte[1 << 20];
        for (int i = 0; i < big.length; i++) big[i] = (byte) i;
        Files.write(new File(data, "iso_data/jak1/BIG.WAD").toPath(), big);
        new File(data, "iso_data/jak1/BIG.WAD").setLastModified(1_600_000_000_000L);
    }

    static boolean filled(File data) {
        File big = new File(data, "iso_data/jak1/BIG.WAD");
        return read(new File(data, "ready-jak1.txt")).startsWith("0.1.3")
            && read(new File(data, "out/jak1/iso/GAME.CGO")).equals("cgo")
            && new File(data, "out/jak1/obj").isDirectory()
            && big.length() == (1 << 20)
            && Math.abs(big.lastModified() - 1_600_000_000_000L) < 2000;
    }

    static void await() throws InterruptedException {
        Thread t = PcDataFolder.STATE.thread;
        if (t != null) t.join(60_000);
        for (int i = 0; i < 100 && PcDataFolder.STATE.running; i++) Thread.sleep(50);
    }

    static void move(PcPaths paths, File target) throws InterruptedException {
        PcDataFolder.start(paths, target, () -> { });
        await();
    }

    public static void main(String[] args) throws Exception {
        File scratch = new File(args[0]).getAbsoluteFile();
        TexturePacks.deleteTree(scratch);
        File home = new File(scratch, "home");
        PcPaths paths = new PcPaths(new File(scratch, "install"), home, new File(scratch, "logs"));
        new File(scratch, "install").mkdirs();
        new File(scratch, "logs").mkdirs();

        // The default place, and where a picked folder puts the games.
        check(paths.data().equals(new File(home, "data")) && !paths.dataMoved(), "no choice: the games in home\\data");
        check(PcDataFolder.target(new File(scratch, "games")).equals(new File(scratch, "games/JakQuestVR")),
            "a picked folder: JakQuestVR made in it");
        check(PcDataFolder.target(new File(scratch, "jakquestvr")).equals(new File(scratch, "jakquestvr")),
            "a picked folder named JakQuestVR: that folder itself");

        // What cannot hold them.
        fill(paths.data());
        check(PcDataFolder.refuse(paths, paths.data()) != null, "refused: where they are now");
        check(PcDataFolder.refuse(paths, new File(paths.data(), "out/x")) != null, "refused: inside where they are");
        check(PcDataFolder.refuse(paths, home) != null, "refused: the launcher's folder (they are in it)");
        check(PcDataFolder.refuse(paths, new File(home, "config/JakQuestVR")) != null,
            "refused: inside the launcher's folder, not its data");
        check(PcDataFolder.refuse(paths, new File(scratch, "install/JakQuestVR")) != null, "refused: inside the release");
        check(PcDataFolder.refuse(paths, new File(scratch, "logs")) != null, "refused: the logs folder");
        File full = new File(scratch, "full/JakQuestVR");
        write(new File(full, "mine.txt"), "the player's");
        check(PcDataFolder.refuse(paths, full) != null, "refused: a folder with files in it");
        File aFile = new File(scratch, "afile");
        write(aFile, "x");
        check(PcDataFolder.refuse(paths, aFile) != null, "refused: a file");
        check(PcDataFolder.refuse(paths, new File(scratch, "games/JakQuestVR")) == null, "a new folder: fine");
        new File(scratch, "empty/JakQuestVR").mkdirs();
        check(PcDataFolder.refuse(paths, new File(scratch, "empty/JakQuestVR")) == null, "an empty folder: fine");

        // A refused move through start(): nothing changes.
        move(paths, full);
        check(PcDataFolder.STATE.failure != null && !paths.dataMoved() && filled(paths.data())
                && read(new File(full, "mine.txt")).equals("the player's"),
            "a move into a folder with files: refused, the games and the folder as they were");

        // The same drive: a rename.
        File here = new File(scratch, "games/JakQuestVR");
        move(paths, here);
        check(PcDataFolder.STATE.failure == null && paths.dataMoved() && paths.data().equals(here),
            "moved on the same drive: the games kept there now (" + PcDataFolder.STATE.done + ")");
        check(filled(here) && !new File(home, "data").exists(), "... all of it there, nothing left in home\\data");
        check(read(new File(home, "data-folder.txt")).trim().equals(here.getPath()), "... the choice saved");
        PcPaths again = new PcPaths(new File(scratch, "install"), home, new File(scratch, "logs"));
        check(again.data().equals(here) && again.compiled("jak1").equals(new File(here, "out/jak1")),
            "... and read again as the launcher starts");
        check(!PcDataFolder.missing(again), "... and it is there");

        // Into an empty folder the player made (on the same drive).
        File empty = new File(scratch, "empty/JakQuestVR");
        move(paths, empty);
        check(PcDataFolder.STATE.failure == null && paths.data().equals(empty) && filled(empty) && !here.exists(),
            "moved into an empty folder of the player's");

        // Back to the default place: the choice goes.
        move(paths, paths.defaultData());
        check(PcDataFolder.STATE.failure == null && !paths.dataMoved() && filled(new File(home, "data"))
                && !new File(home, "data-folder.txt").exists(),
            "back to the default place: no choice saved, the games in home\\data");

        // Nothing set up yet: only the choice changes.
        File bare = new File(scratch, "bare");
        TexturePacks.deleteTree(bare);
        PcPaths fresh = new PcPaths(new File(scratch, "install"), new File(bare, "home"), new File(scratch, "logs"));
        move(fresh, new File(bare, "games/JakQuestVR"));
        check(PcDataFolder.STATE.failure == null && fresh.data().equals(new File(bare, "games/JakQuestVR"))
                && fresh.data().isDirectory(),
            "nothing set up yet: the folder made and chosen");

        // The chosen folder gone (a drive not plugged in): shown as missing, and the default place can be chosen.
        write(new File(bare, "home/data-folder.txt"), new File(bare, "nowhere/JakQuestVR").getPath() + "\r\n");
        PcPaths gone = new PcPaths(new File(scratch, "install"), new File(bare, "home"), new File(scratch, "logs"));
        check(PcDataFolder.missing(gone) && gone.data().equals(new File(bare, "nowhere/JakQuestVR")),
            "a chosen folder not there: missing (a Windows line end read the same)");
        move(gone, gone.defaultData());
        check(PcDataFolder.STATE.failure == null && !gone.dataMoved() && !PcDataFolder.missing(gone),
            "... the default place chosen again, nothing to move");
        write(new File(bare, "home/data-folder.txt"), "relative\\path");
        check(!new PcPaths(new File(scratch, "install"), new File(bare, "home"), new File(scratch, "logs")).dataMoved(),
            "a choice that is no full path: the default place");

        // The old copy's removal: only true copies go.
        File old = new File(scratch, "old"), now = new File(scratch, "now");
        write(new File(old, "a/same.txt"), "same");
        write(new File(now, "a/same.txt"), "same");
        write(new File(old, "a/other.txt"), "longer than the copy");
        write(new File(now, "a/other.txt"), "short");
        write(new File(old, "b/only-old.txt"), "not copied");
        write(new File(old, "c/gone.txt"), "x");
        write(new File(now, "c/gone.txt"), "x");
        boolean all = PcDataFolder.removeCopied(old, now);
        check(!all && !new File(old, "a/same.txt").exists() && new File(old, "a/other.txt").exists()
                && new File(old, "b/only-old.txt").exists() && !new File(old, "c").exists()
                && new File(now, "a/same.txt").exists(),
            "removing the old copy: the copies go (and their emptied folders), the rest stays");
        write(new File(old, "d/self.txt"), "self");
        PcDataFolder.removeCopied(old, old);
        check(new File(old, "d/self.txt").exists(), "a folder is never removed as its own copy");

        // A removal the launcher closed in the middle of: finished as it starts again.
        File left = new File(scratch, "left");
        fill(left);
        write(new File(home, PcDataFolder.LEFTOVER), left.getPath());
        PcDataFolder.finishLeftover(paths, () -> { });
        await();
        check(!left.exists() && !new File(home, PcDataFolder.LEFTOVER).exists() && filled(paths.data()),
            "a leftover old copy: removed as the launcher starts, the games kept");
        write(new File(home, PcDataFolder.LEFTOVER), paths.data().getPath());
        PcDataFolder.finishLeftover(paths, () -> { });
        await();
        check(filled(paths.data()) && !new File(home, PcDataFolder.LEFTOVER).exists(),
            "a leftover note naming where the games are now: ignored, nothing removed");

        // Another drive: copied, checked, switched, the old removed.
        if (args.length > 1) {
            File other = new File(args[1]).getAbsoluteFile();
            TexturePacks.deleteTree(other);
            File there = PcDataFolder.target(other);
            PcDataFolder.start(paths, there, () -> { });
            PcDataFolder.stop();  // before its first file (it measures first)
            await();
            String stopped = PcDataFolder.STATE.failure;
            check(stopped != null && stopped.startsWith("Stopped") && !paths.dataMoved() && filled(paths.data())
                    && !there.exists(),
                "a move to another drive stopped: what was copied removed, the games where they were (" + stopped + ")");
            move(paths, there);
            String failure = PcDataFolder.STATE.failure;
            check(failure == null && paths.data().equals(there) && filled(there)
                    && !new File(home, "data").exists() && !new File(home, PcDataFolder.LEFTOVER).exists(),
                "moved to another drive: copied with its dates, chosen, the old copy removed ("
                    + (failure != null ? failure : PcDataFolder.STATE.done) + ")");
            move(paths, paths.defaultData());
            check(PcDataFolder.STATE.failure == null && !paths.dataMoved() && filled(paths.data()) && !there.exists(),
                "and back from it");
            TexturePacks.deleteTree(other);
        }

        System.out.println(failed ? "SOME FAILED" : "all ok");
        System.exit(failed ? 1 : 0);
    }
}
