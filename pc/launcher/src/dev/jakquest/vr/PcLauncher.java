// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
// After Ratchet & Clank Quest VR's PcLauncher (the same author's project): its look, tabs, updates and logs.
package dev.jakquest.vr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import static dev.jakquest.vr.PcWidgets.Btn;
import static dev.jakquest.vr.PcWidgets.Card;
import static dev.jakquest.vr.PcWidgets.Page;
import static dev.jakquest.vr.PcWidgets.column;
import static dev.jakquest.vr.PcWidgets.gap;
import static dev.jakquest.vr.PcWidgets.row;
import static dev.jakquest.vr.PcWidgets.text;
import static dev.jakquest.vr.PcWidgets.weighted;

/**
 * The PC's launcher: each of the three games set up from the player's own disc image, the VR options of the headset's
 * launcher (VrOptions, the same file for both), the controls of every PC VR controller, the logs in one zip, updates
 * from the project's GitHub releases. Play starts the game for PC VR, which opens in whichever headset software runs.
 */
public final class PcLauncher {
    static final String TITLE = "Jak and Daxter VR";
    static final String RATCHET_URL = "https://github.com/moistman42069/RatchetAndClank-Quest-VR/releases/latest";
    private static final String[] TABS = {"Play", "Graphics", "VR Body", "VR Controls", "Controls", "Mods", "Logs",
        "Credits"};

    private final PcPaths paths;
    private JFrame frame;
    private JPanel root;
    private Page page;
    private JScrollPane scroll;
    private final List<Btn> tabs = new ArrayList<>();
    private int tab = 0;
    /** The game each options tab writes, -1 all three (the headset's launcher writes the game it opens). */
    private int optionsGame = -1;
    /** The controller type the Controls tab shows (ControlsHelp.controllerTypes). */
    private int controller = 0;
    private String logsNote = "";
    private File logsZip;
    private String presetNote = "";
    private String modsNote = "";
    /** The mod lists once read (ModList: by Browse mods, or as the launcher starts with Automatic checks on). */
    private volatile ModList modList;
    private volatile boolean modListBusy;
    /** The game Browse mods shows, and whether its flat-screen mods are shown too. */
    private int browseGame = 0;
    private boolean browseFlat = false;
    /** Multiplayer's name and party server as typed (party.txt). */
    private String partyName = "", partyServer = "";
    private volatile Process game;
    private volatile String gameName;
    private volatile String playFailure;
    private final Timer watcher = new Timer(400, event -> {
        if (tab == 0) {
            redraw();
        }
    });

    PcLauncher(PcPaths paths) {
        this.paths = paths;
    }

    // ---- Starting ----

    public static void main(String[] args) throws Exception {
        PcPaths paths = new PcPaths();
        if (args.length >= 2 && args[0].equals("--write-icon")) {
            PcArtwork.writeIcon(new File(args[1]));
            return;
        }
        if (args.length >= 3 && args[0].equals("--render")) {
            // drawn at 1x: on a scaled display the text would be measured at its scale and drawn at 1x, so lines
            // would break wider than they are drawn (the window measures and draws at one scale)
            System.setProperty("sun.java2d.uiScale", "1");
            int w = args.length >= 5 ? Integer.parseInt(args[3]) : 1180, h = args.length >= 5 ? Integer.parseInt(args[4]) : 780;
            PcUpdater.network = false;
            SwingUtilities.invokeAndWait(() -> {
                try {
                    new PcLauncher(paths).render(Integer.parseInt(args[1]), new File(args[2]), w, h);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            System.exit(0);
        }
        if (args.length >= 1 && args[0].equals("--self-test")) {
            List<String> wrong = selfTest(paths);
            for (String w : wrong) {
                System.out.println("self-test: " + w);
            }
            System.out.println(wrong.isEmpty() ? "self-test: OK" : "self-test: " + wrong.size() + " wrong");
            System.exit(wrong.isEmpty() ? 0 : 1);
        }
        // One launcher at a time: two would set a game up, or start it, over each other.
        paths.home.mkdirs();
        RandomAccessFile lockFile = new RandomAccessFile(new File(paths.home, "launcher.lock"), "rw");
        FileLock lock = lockFile.getChannel().tryLock();
        if (lock == null) {
            JOptionPane.showMessageDialog(null, "The " + TITLE + " launcher is already open.", TITLE,
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new PcLauncher(paths).open());
    }

    private void open() {
        log("launcher " + PcBuild.VERSION + " opened");
        frame = new JFrame(TITLE);
        frame.setIconImages(PcArtwork.icons());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        buildFrame();
        frame.setContentPane(root);
        frame.setSize(1180, 780);
        frame.setMinimumSize(new Dimension(860, 600));
        frame.setLocationRelativeTo(null);
        showTab(0);
        frame.setVisible(true);
        String last = PcUpdater.lastInstall(paths);
        if (!last.isEmpty()) {
            JOptionPane.showMessageDialog(frame, last, TITLE, JOptionPane.WARNING_MESSAGE);
        }
        showWhatsNewOnce();
        PcUpdater.maybeCheck(paths, () -> SwingUtilities.invokeLater(this::redrawIfPlay));
        if (PcUpdater.automatic(paths)) {
            readModList();  // for the texture packs' Update buttons, as Ratchet's launcher does
        }
    }

    private void close() {
        if (PcSetup.STATE.running) {
            int answer = JOptionPane.showConfirmDialog(frame, "A game is still being set up. Stop and close? It starts "
                + "again from the beginning next time.", TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
            PcSetup.stop();
        }
        frame.dispose();
        System.exit(0);
    }

    /** One line in the launcher's own log (for the Logs zip). */
    private void log(String line) {
        try {
            paths.home.mkdirs();
            String stamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Files.write(paths.launcherLog().toPath(), (stamp + " " + line + "\n").getBytes(StandardCharsets.UTF_8),
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException ignored) {
            // no log is no harm
        }
    }

    // ---- The release's texts ----

    private String readText(String name) {
        File file = new File(paths.texts(), name);
        String text = PcPaths.read(file);
        return text.isEmpty() ? "(" + name + " is missing from the release's texts folder.)" : text;
    }

    // ---- What's new ----

    private void showWhatsNewOnce() {
        Prefs seen = new Prefs(paths.prefs("whats_new"));
        if (PcBuild.VERSION.equals(seen.getString("shown", ""))) {
            return;
        }
        seen.putString("shown", PcBuild.VERSION);
        showWhatsNew();
    }

    /** whats_new.txt over the launcher, until Got it. The Play tab's Updates card shows it again. */
    private void showWhatsNew() {
        JDialog dialog = new JDialog(frame, "What's new", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        Card box = new Card(PcArtwork.NAVY, PcArtwork.ACCENT, 14, 24, 20);
        box.add(text("What's new in " + PcBuild.VERSION, 22, PcArtwork.ACCENT, true));
        JTextArea words = text(readText("whats_new.txt").trim(), 15, PcArtwork.INK, false);
        Page inside = new Page(0);
        inside.setBorder(new EmptyBorder(10, 0, 14, 8));
        inside.add(words);
        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(scrollPane(inside));
        box.add(holder);
        box.add(new Btn("Got it", true, dialog::dispose));
        dialog.getRootPane().registerKeyboardAction(event -> dialog.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.setContentPane(box);
        int height = Math.min(640, frame.getHeight() - 40);
        holder.setPreferredSize(new Dimension(760, height - 150));
        dialog.setSize(820, height);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    // ---- The frame ----

    private static JScrollPane scrollPane(Page inside) {
        JScrollPane pane = new JScrollPane(inside, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setOpaque(false);
        pane.getViewport().setOpaque(false);
        pane.setBorder(BorderFactory.createEmptyBorder());
        PcWidgets.style(pane.getVerticalScrollBar());
        return pane;
    }

    private void buildFrame() {
        root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                PcArtwork.sky(g2, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setBorder(new EmptyBorder(18, 24, 18, 14));

        JPanel top = column(0);
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        JComponent mark = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                PcArtwork.orb(g2, 0, (getHeight() - 48) / 2f, 48);
                g2.dispose();
            }
        };
        mark.setPreferredSize(new Dimension(48, 48));
        header.add(mark, BorderLayout.WEST);
        JPanel titles = column(0);
        titles.add(text("JAK AND DAXTER VR", 26, PcArtwork.ACCENT, true));
        titles.add(text("Jak 1, Jak II, Jak 3  ·  PC VR  ·  unofficial fan port  ·  v" + PcBuild.VERSION, 13,
            PcArtwork.MUTED, false));
        header.add(titles, BorderLayout.CENTER);
        header.add(ratchetLink(), BorderLayout.EAST);
        JPanel headerBox = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 54);
            }
        };
        headerBox.setOpaque(false);
        headerBox.add(header);
        top.add(headerBox);

        JPanel bar = row(6);
        bar.setBorder(new EmptyBorder(12, 0, 10, 10));
        for (int i = 0; i < TABS.length; i++) {
            final int index = i;
            Btn button = new Btn(TABS[i], false, () -> showTab(index));
            bar.add(weighted(button, 1f));
            tabs.add(button);
        }
        top.add(bar);
        root.add(top, BorderLayout.NORTH);

        page = new Page(12);
        page.setBorder(new EmptyBorder(0, 0, 0, 10));
        scroll = scrollPane(page);
        root.add(scroll, BorderLayout.CENTER);
    }

    /**
     * The small link in the corner of the main page (owner, 2026-11-11): the same author's standalone Ratchet & Clank port,
     * opened in the browser.
     */
    private JComponent ratchetLink() {
        JLabel link = new JLabel("<html><div style='text-align:right'>Also by us:<br><u>Ratchet &amp; Clank VR</u>"
            + " &#8599;</div></html>");
        link.setFont(PcWidgets.font(13, false));
        link.setForeground(PcArtwork.MUTED);
        link.setToolTipText(RATCHET_URL);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.setBorder(new EmptyBorder(0, 0, 0, 10));
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                browse(RATCHET_URL);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                link.setForeground(PcArtwork.ACCENT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                link.setForeground(PcArtwork.MUTED);
            }
        });
        return link;
    }

    private void browse(String address) {
        try {
            Desktop.getDesktop().browse(new URI(address));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Open " + address + " in your browser.", TITLE,
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openFolder(File folder) {
        try {
            folder.mkdirs();
            Desktop.getDesktop().open(folder);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "The folder is " + folder, TITLE, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showTab(int index) {
        tab = index;
        for (int i = 0; i < tabs.size(); i++) {
            Btn button = tabs.get(i);
            button.fill = i == index ? PcArtwork.ACCENT_DARK : PcArtwork.BUTTON;
            button.edge = i == index ? PcArtwork.ACCENT : PcArtwork.BUTTON_EDGE;
            button.repaint();
        }
        page.removeAll();
        switch (index) {
            case 0: showPlay(); break;
            case 1: showOptions(0); break;
            case 2: showOptions(1); break;
            case 3: showOptions(2); break;
            case 4: showControls(); break;
            case 5: showMods(); break;
            case 6: showLogs(); break;
            default: showCredits(); break;
        }
        if (index == 0 && (PcSetup.STATE.running || game != null)) {
            watcher.start();
        } else {
            watcher.stop();
        }
        page.revalidate();
        page.repaint();
    }

    /** The same tab drawn again where it was scrolled to: a choice made, a step of the set-up. */
    private void redraw() {
        int at = scroll.getVerticalScrollBar().getValue();
        showTab(tab);
        SwingUtilities.invokeLater(() -> scroll.getVerticalScrollBar().setValue(at));
    }

    private void redrawIfPlay() {
        if (tab == 0) {
            redraw();
        }
    }

    private static Card card() {
        return new Card();
    }

    // ---- Play ----

    private void showPlay() {
        if (game != null) {
            Card card = card();
            card.add(text("Playing " + gameName, 22, PcArtwork.GOOD, true));
            card.add(text("The game is open in its own window and in your headset. Close it (or press Stop the game) "
                + "to come back here; its log is kept for the Logs tab.", 15, PcArtwork.INK, false));
            card.add(gap(10));
            card.add(new Btn("Stop the game", false, () -> {
                Process running = game;
                if (running != null) {
                    running.descendants().forEach(ProcessHandle::destroyForcibly);
                    running.destroyForcibly();
                }
            }));
            page.add(card);
            return;
        }
        if (playFailure != null) {
            Card card = card();
            card.add(text("That did not work", 18, PcArtwork.BAD, true));
            card.add(text(playFailure, 15, PcArtwork.INK, false));
            page.add(card);
        }
        if (PcSetup.STATE.running) {
            Card card = card();
            String name = VrOptions.GAME_NAMES[PcSetup.index(PcSetup.STATE.game)];
            card.add(text("Setting up " + name, 20, PcArtwork.ACCENT, true));
            card.add(text(PcSetup.STATE.phase + (PcSetup.STATE.percent >= 0 ? ": " + PcSetup.STATE.percent + "%" : "...")
                + " This takes a few minutes, once.", 15, PcArtwork.INK, false));
            card.add(new PcWidgets.Bar(Math.max(0, PcSetup.STATE.percent)));
            card.add(text(PcSetup.STATE.line, 12, PcArtwork.MUTED, false));
            card.add(gap(6));
            card.add(new Btn("Stop", false, () -> {
                PcSetup.stop();
                redraw();
            }));
            page.add(card);
        } else if (PcSetup.STATE.failure != null) {
            Card card = card();
            card.add(text("The set-up did not finish", 18, PcArtwork.BAD, true));
            card.add(text(PcSetup.STATE.failure, 15, PcArtwork.INK, false));
            page.add(card);
        }
        for (int i = 0; i < VrOptions.GAMES.length; i++) {
            page.add(gameCard(i));
        }
        showUpdates();
    }

    private Card gameCard(int index) {
        String game = VrOptions.GAMES[index];
        Card card = card();
        card.add(text(VrOptions.GAME_NAMES[index], 20, PcArtwork.INK, true));
        PcSetup.Status status = PcSetup.status(paths, game);
        boolean busy = PcSetup.STATE.running;
        JPanel buttons = row(6);
        buttons.setBorder(new EmptyBorder(8, 0, 0, 0));
        String disc = status == PcSetup.Status.NONE ? null : DiscVersion.describe(paths.extracted(game));
        if (disc != null) {
            card.add(text(disc, 14, PcArtwork.MUTED, false));
        }
        switch (status) {
            case READY:
                card.add(text("Ready. Start your headset's PC VR software (SteamVR, Meta Horizon Link, Virtual Desktop "
                    + "or another), then play.", 14, PcArtwork.MUTED, false));
                buttons.add(weighted(new Btn("PLAY IN VR", true, () -> play(game)), 2f));
                buttons.add(weighted(new Btn("Remove", false, () -> remove(game)), 1f));
                break;
            case OUTDATED:
                card.add(text((PcSetup.newSources(paths, game) ? "This version brings new game code"
                    : "Your texture packs changed (Mods)") + ": the game is built again from your disc (extracted "
                    + "before, so no disc needed). A few minutes, once.", 14, PcArtwork.MUTED, false));
                Btn rebuild = new Btn("Build it again", true, () -> setUp(game, null));
                buttons.add(weighted(rebuild, 2f));
                buttons.add(weighted(new Btn("Remove", false, () -> remove(game)), 1f));
                rebuild.setEnabled(!busy);
                break;
            default:
                card.add(text("Not set up. It is made from your own disc image (.iso) of the game, once; no game files "
                    + "come with this release. Every disc version works (US, PAL, Japanese, Korean).", 14,
                    PcArtwork.MUTED, false));
                Btn pick = new Btn("Set up from your disc image...", true, () -> pickDisc(game));
                pick.setEnabled(!busy);
                buttons.add(weighted(pick, 1f));
                break;
        }
        card.add(buttons);
        return card;
    }

    private void pickDisc(String game) {
        FileDialog dialog = new FileDialog(frame, "Your " + VrOptions.GAME_NAMES[PcSetup.index(game)] + " disc image",
            FileDialog.LOAD);
        dialog.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".iso"));
        dialog.setFile("*.iso");
        dialog.setVisible(true);
        if (dialog.getFile() == null) {
            return;
        }
        setUp(game, new File(dialog.getDirectory(), dialog.getFile()));
    }

    private void setUp(String game, File iso) {
        playFailure = null;
        log("set-up of " + game + (iso == null ? " again" : " from a disc image"));
        PcSetup.start(paths, game, iso, () -> SwingUtilities.invokeLater(this::redrawIfPlay));
        redraw();
    }

    private void remove(String game) {
        int answer = JOptionPane.showConfirmDialog(frame, "Remove " + VrOptions.GAME_NAMES[PcSetup.index(game)]
            + "'s set-up (its extracted disc and built game)? Your saves and settings are kept; setting it up again "
            + "needs your disc image.", TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            PcSetup.remove(paths, game);
            log("removed " + game);
            redraw();
        }
    }

    /** gk for PC VR: the game from the player's data folder, its settings in theirs, JAKVR=1. */
    private void play(String game) {
        playFailure = null;
        if (!paths.gk().isFile()) {
            playFailure = "The release's game\\gk.exe is missing: unzip the release again.";
            redraw();
            return;
        }
        try {
            VrOptions.stampParty(paths.config(), PcBuild.VERSION);  // Multiplayer: this release, for the others' check
        } catch (IOException e) {
            log("party.txt: " + e.getMessage());
        }
        List<String> command = new ArrayList<>();
        command.add(paths.gk().getPath());
        command.add("-g");
        command.add(game);
        command.add("--proj-path");
        command.add(paths.data().getPath());
        command.add("--config-path");
        command.add(paths.config().getPath());
        command.add("--");
        command.add("-boot");
        command.add("-fakeiso");
        paths.logs.mkdirs();
        File output = new File(paths.logs, "game-" + game + "-output.log");
        ProcessBuilder builder = new ProcessBuilder(command).directory(paths.gameFolder()).redirectErrorStream(true)
            .redirectOutput(output);
        builder.environment().clear();
        builder.environment().putAll(paths.environment());
        try {
            Process started = builder.start();
            this.game = started;
            gameName = VrOptions.GAME_NAMES[PcSetup.index(game)];
            log("playing " + game);
            new Thread(() -> {
                int code;
                try {
                    code = started.waitFor();
                } catch (InterruptedException e) {
                    code = -1;
                }
                final int exit = code;
                log(game + " closed (" + exit + ")");
                SwingUtilities.invokeLater(() -> {
                    this.game = null;
                    if (exit != 0) {
                        playFailure = "The game closed with an error (" + exit + "). Gather the logs on the Logs tab and "
                            + "send them with what you were doing.";
                    }
                    redraw();
                });
            }, "game").start();
        } catch (IOException e) {
            playFailure = "The game did not start: " + e.getMessage();
        }
        redraw();
    }

    private void showUpdates() {
        Card card = card();
        card.add(text("Updates", 18, PcArtwork.ACCENT, true));
        card.add(text(PcUpdater.status(paths), 15, PcArtwork.INK, false));
        if (PcUpdater.STATE.percent >= 0) {
            card.add(new PcWidgets.Bar(PcUpdater.STATE.percent));
        }
        JPanel buttons = row(6);
        buttons.setBorder(new EmptyBorder(8, 0, 0, 0));
        Runnable changed = () -> SwingUtilities.invokeLater(this::redrawIfPlay);
        if (PcUpdater.STATE.tag != null && PcUpdater.STATE.ready) {
            buttons.add(weighted(new Btn("Install " + PcUpdater.STATE.tag, true, () -> {
                String why = PcUpdater.install(paths, true);
                if (why == null) {
                    frame.dispose();
                    System.exit(0);
                }
                JOptionPane.showMessageDialog(frame, why, TITLE, JOptionPane.WARNING_MESSAGE);
            }), 1f));
        } else if (PcUpdater.STATE.tag != null) {
            Btn download = new Btn("Download " + PcUpdater.STATE.tag, true, () -> PcUpdater.download(paths, changed));
            download.setEnabled(!PcUpdater.STATE.busy);
            buttons.add(weighted(download, 1f));
        }
        Btn check = new Btn("Check now", false, () -> {
            PcUpdater.check(paths, changed);
            redraw();
        });
        check.setEnabled(!PcUpdater.STATE.busy);
        buttons.add(weighted(check, 1f));
        boolean automatic = PcUpdater.automatic(paths);
        buttons.add(weighted(new Btn(automatic ? "Automatic checks: On" : "Automatic checks: Off", false, () -> {
            PcUpdater.setAutomatic(paths, !automatic);
            redraw();
        }), 1f));
        buttons.add(weighted(new Btn("What's new", false, this::showWhatsNew), 1f));
        card.add(buttons);
        page.add(card);
    }

    // ---- The options (Graphics, VR Body, VR Controls) ----

    /** The options file of the game the tab shows: the one picked, or Jak 1's for all three. */
    private File optionsFile() {
        return paths.vrOptions(VrOptions.GAMES[optionsGame < 0 ? 0 : optionsGame]);
    }

    private void setOption(String key, String value) {
        List<String> games = new ArrayList<>();
        if (optionsGame < 0) {
            for (String g : VrOptions.GAMES) games.add(g);
        } else {
            games.add(VrOptions.GAMES[optionsGame]);
        }
        for (String g : games) {
            File file = paths.vrOptions(g);
            Map<String, String> values = VrOptions.read(file);
            values.put(key, value);
            try {
                VrOptions.write(file, values);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Could not save the option: " + e.getMessage(), TITLE,
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
    }

    private void showOptions(int which) {
        Card picker = card();
        picker.add(text("These options are the in-game VR menu's (hold the left menu button in a menu, or Options, VR "
            + "Options). Each game keeps its own; a change here goes to:", 13, PcArtwork.MUTED, false));
        JPanel games = row(6);
        games.setBorder(new EmptyBorder(6, 0, 0, 0));
        String[] names = {"All three games", "Jak 1", "Jak II", "Jak 3"};
        for (int i = 0; i < names.length; i++) {
            final int g = i - 1;
            Btn b = new Btn(names[i], false, () -> {
                optionsGame = g;
                redraw();
            });
            if (optionsGame == g) {
                b.chosen();
            }
            games.add(weighted(b, 1f));
        }
        picker.add(games);
        page.add(picker);
        Map<String, String> values = VrOptions.read(optionsFile());
        if (which == 0) {
            Card presets = card();
            presets.add(text("Graphics preset", 16, PcArtwork.INK, true));
            presets.add(text("Sets the graphics below at once; adjust from there. Read when the game starts.", 13,
                PcArtwork.MUTED, false));
            JPanel buttons = row(6);
            buttons.setBorder(new EmptyBorder(6, 0, 0, 0));
            for (int i = 0; i < VrOptions.PRESETS.length; i++) {
                final int p = i;
                buttons.add(weighted(new Btn(VrOptions.PRESETS[i], false, () -> {
                    for (Map.Entry<String, String> e : VrOptions.preset(p).entrySet()) {
                        setOption(e.getKey(), e.getValue());
                    }
                    presetNote = VrOptions.PRESETS[p] + ": " + VrOptions.PRESET_HELP[p];
                    redraw();
                }), 1f));
            }
            presets.add(buttons);
            if (!presetNote.isEmpty()) {
                presets.add(text(presetNote, 13, PcArtwork.ACCENT, false));
            }
            page.add(presets);
        }
        boolean guns = optionsGame != 0;
        for (VrOptions.Option o : VrOptions.TABS[which]) {
            if (o.gunsOnly && !guns) {
                continue;
            }
            page.add(optionCard(o, values.getOrDefault(o.key, o.fallback)));
        }
    }

    private Card optionCard(VrOptions.Option o, String current) {
        Card card = card();
        card.add(text(o.label + (o.gunsOnly ? "  (Jak II and 3)" : ""), 16, PcArtwork.INK, true));
        card.add(text(o.help + (o.atStart ? " Read when the game starts." : ""), 13, PcArtwork.MUTED, false));
        JPanel buttons = row(6);
        buttons.setBorder(new EmptyBorder(6, 0, 0, 0));
        if (o.kind == VrOptions.Kind.NUMBER) {
            int value;
            try {
                value = Integer.parseInt(current);
            } catch (NumberFormatException e) {
                value = Integer.parseInt(o.fallback);
            }
            final int v = value;
            Btn down = new Btn("-", false, () -> {
                setOption(o.key, Integer.toString(Math.max(o.low, v - o.step)));
                redraw();
            });
            Btn up = new Btn("+", false, () -> {
                setOption(o.key, Integer.toString(Math.min(o.high, v + o.step)));
                redraw();
            });
            Btn reset = new Btn("Default", false, () -> {
                setOption(o.key, o.fallback);
                redraw();
            });
            down.setEnabled(v > o.low);
            up.setEnabled(v < o.high);
            buttons.add(weighted(down, 1f));
            buttons.add(weighted(PcWidgets.centred(v + o.unit, 18, PcArtwork.ACCENT), 2f));
            buttons.add(weighted(up, 1f));
            buttons.add(weighted(reset, 1f));
        } else {
            for (int i = 0; i < o.names.length; i++) {
                final String value = o.values[i];
                Btn b = new Btn(o.names[i], false, () -> {
                    setOption(o.key, value);
                    redraw();
                });
                if (value.equals(o.clean(current))) {
                    b.chosen();
                }
                buttons.add(weighted(b, 1f));
            }
        }
        card.add(buttons);
        return card;
    }

    // ---- Mods: what works here, Multiplayer built in (VrOptions.MULTIPLAYER, with its switch), texture packs
    // (TexturePacks, each with On / Off and Update) and Browse mods (ModList: this project's list and jakmods.dev's) ----

    private void showMods() {
        Card about = card();
        about.add(text("Mods", 18, PcArtwork.ACCENT, true));
        about.add(text("What works in Jak and Daxter Quest VR:", 14, PcArtwork.INK, true));
        about.add(text("•  Multiplayer (Jak 1) comes built in and on: it changes nothing until you host or join a "
            + "game, and turned off here it is out of the game altogether.", 13, PcArtwork.MUTED, false));
        about.add(text("•  Texture packs (all three games): new pictures in place of the game's own. Add one from "
            + "Browse mods at the bottom, or from a pack's .zip you have. The game is then built again with them from "
            + "your disc (extracted before, no disc needed, a few minutes).", 13, PcArtwork.MUTED, false));
        about.add(text("•  The mods in the OpenGOAL launcher and on jakmods.dev (new gameplay, levels, randomizers) "
            + "are whole games of their own made for a flat screen: they cannot run in VR. Browse mods lists them as "
            + "Flat screen only, with their page, so you can see what there is.", 13, PcArtwork.MUTED, false));
        about.add(text("A change counts from the game's next start.", 13, PcArtwork.MUTED, false));
        if (!modsNote.isEmpty()) {
            about.add(text(modsNote, 13, PcArtwork.ACCENT, false));
        }
        page.add(about);

        VrOptions.Option o = VrOptions.MULTIPLAYER;
        File file = paths.vrOptions(VrOptions.MULTIPLAYER_GAME);
        String current = o.clean(VrOptions.read(file).getOrDefault(o.key, o.fallback));
        Card card = card();
        card.add(text(o.label + "  (Jak 1)", 16, PcArtwork.INK, true));
        card.add(text(o.help, 13, PcArtwork.MUTED, false));
        JPanel buttons = row(6);
        buttons.setBorder(new EmptyBorder(6, 0, 0, 0));
        for (int i = 0; i < o.names.length; i++) {
            final String value = o.values[i];
            Btn b = new Btn(o.names[i], false, () -> {
                Map<String, String> values = VrOptions.read(file);
                values.put(o.key, value);
                try {
                    VrOptions.write(file, values);
                    modsNote = "";
                } catch (IOException e) {
                    modsNote = "Could not save: " + e.getMessage();
                }
                redraw();
            });
            if (value.equals(current)) {
                b.chosen();
            }
            buttons.add(weighted(b, 1f));
        }
        card.add(buttons);
        page.add(card);
        if (VrOptions.isOn(current)) {
            showParty();
        }
        for (int i = 0; i < VrOptions.GAMES.length; i++) {
            page.add(packsCard(i));
        }
        page.add(browseCard());
    }

    /** Multiplayer's name and party server (party.txt; game/party.cpp reads them as the game starts). */
    private void showParty() {
        Map<String, String> party = VrOptions.readParty(VrOptions.partyFile(paths.config()));
        partyName = party.get("name");
        partyServer = party.get("server");
        saveParty();  // a made-up name kept from the first time
        Card who = card();
        who.add(text("Your name", 16, PcArtwork.INK, true));
        who.add(text("What the others see. Up to " + VrOptions.NAME_MAX + " letters, digits and signs.", 13,
            PcArtwork.MUTED, false));
        javax.swing.JTextField name = PcWidgets.field(partyName, VrOptions.NAME_MAX, typed -> {
            partyName = typed;
            saveParty();
        });
        name.setToolTipText(null);
        who.add(name);
        who.add(PcWidgets.gap(8));
        who.add(text("Party server", 16, PcArtwork.INK, true));
        who.add(text("Empty: games on this network, found by themselves. To play over the internet, everyone types "
            + "the same party server here (an address, or address:port), one someone runs with the release's "
            + "party_server.py; its games are then listed in the game too. Or type the address of a friend's PC or "
            + "headset that is hosting, to join it directly (their router must let TCP port 8120 in). Everyone needs "
            + "the same release.", 13, PcArtwork.MUTED, false));
        javax.swing.JTextField server = PcWidgets.field(partyServer, 253, typed -> {
            partyServer = typed;
            saveParty();
        });
        server.setToolTipText(null);
        who.add(server);
        page.add(who);
    }

    /** True while a texture pack is being unpacked (one at a time). */
    private volatile boolean packBusy;

    private Card packsCard(int index) {
        String game = VrOptions.GAMES[index];
        File root = paths.texturePacks();
        boolean locked = packBusy || PcSetup.STATE.running;
        Card card = card();
        card.add(text("Texture packs: " + VrOptions.GAME_NAMES[index], 16, PcArtwork.INK, true));
        List<TexturePacks.Pack> packs = TexturePacks.list(root, game);
        if (packs.isEmpty()) {
            card.add(text("None added. Add one from Browse mods below, or from a pack's .zip. A pack higher in the list "
                + "wins where two change the same picture.", 13, PcArtwork.MUTED, false));
        }
        ModList lists = modList;
        for (TexturePacks.Pack p : packs) {
            card.add(PcWidgets.gap(6));
            card.add(text(p.line(), 14, PcArtwork.INK, true));
            if (!p.description.isEmpty()) {
                card.add(text(p.description, 12, PcArtwork.MUTED, false));
            }
            ModList.Entry newer = lists == null ? null : lists.newer(p.from, p.version);
            if (newer != null) {
                JPanel update = row(6);
                update.setBorder(new EmptyBorder(4, 0, 0, 0));
                Btn take = new Btn("Update to " + newer.version, true, () -> addFromList(game, newer, p));
                take.setEnabled(!locked);
                update.add(weighted(take, 1f));
                card.add(update);
            }
            JPanel buttons = row(6);
            buttons.setBorder(new EmptyBorder(4, 0, 0, 0));
            Btn on = new Btn("On", false, () -> packChange(() -> TexturePacks.setOn(root, game, p.id, true)));
            Btn off = new Btn("Off", false, () -> packChange(() -> TexturePacks.setOn(root, game, p.id, false)));
            if (p.on) {
                on.chosen();
            } else {
                off.chosen();
            }
            Btn up = new Btn("Up", false, () -> packChange(() -> TexturePacks.move(root, game, p.id, -1)));
            Btn down = new Btn("Down", false, () -> packChange(() -> TexturePacks.move(root, game, p.id, 1)));
            Btn remove = new Btn("Remove", false, () -> {
                int answer = JOptionPane.showConfirmDialog(frame, "Remove the texture pack " + p.name + "?", TITLE,
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    packChange(() -> TexturePacks.remove(root, game, p.id));
                }
            });
            for (Btn b : new Btn[] {on, off, up, down, remove}) {
                b.setEnabled(!locked);
                buttons.add(weighted(b, 1f));
            }
            card.add(buttons);
        }
        JPanel buttons = row(6);
        buttons.setBorder(new EmptyBorder(8, 0, 0, 0));
        Btn add = new Btn("Add a texture pack (.zip)...", false, () -> addPack(game));
        add.setEnabled(!locked);
        buttons.add(weighted(add, 1f));
        PcSetup.Status status = PcSetup.status(paths, game);
        if (status != PcSetup.Status.NONE && TexturePacks.changed(root, game, paths.data())) {
            Btn rebuild = new Btn("Build the game again with them", true, () -> {
                setUp(game, null);
                showTab(0);
            });
            rebuild.setEnabled(!locked);
            buttons.add(weighted(rebuild, 1f));
        }
        card.add(buttons);
        return card;
    }

    private interface PackStep {
        void run() throws IOException;
    }

    private void packChange(PackStep step) {
        try {
            step.run();
            modsNote = "";
        } catch (IOException e) {
            modsNote = "Could not change the texture packs: " + e.getMessage();
        }
        redraw();
    }

    private void addPack(String game) {
        FileDialog dialog = new FileDialog(frame, "A texture pack for " + VrOptions.GAME_NAMES[PcSetup.index(game)]
            + " (.zip)", FileDialog.LOAD);
        dialog.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".zip"));
        dialog.setFile("*.zip");
        dialog.setVisible(true);
        if (dialog.getFile() == null) {
            return;
        }
        File zip = new File(dialog.getDirectory(), dialog.getFile());
        packBusy = true;
        modsNote = "Adding " + zip.getName() + "...";
        redraw();
        new Thread(() -> {
            try (java.io.InputStream in = new java.io.FileInputStream(zip)) {
                TexturePacks.Pack p = TexturePacks.add(paths.texturePacks(), game, in, zip.getName());
                modsNote = "Added " + p.line() + ".";
                log("texture pack added for " + game + ": " + p.id);
            } catch (IOException e) {
                modsNote = "Could not add " + zip.getName() + ": " + e.getMessage();
            } finally {
                packBusy = false;
                SwingUtilities.invokeLater(this::redraw);
            }
        }, "texture-pack").start();
    }

    /** The mod lists read again in the background (ModList.fetch never throws: its problems say what failed). */
    private void readModList() {
        if (modListBusy) {
            return;
        }
        modListBusy = true;
        new Thread(() -> {
            ModList read = ModList.fetch(paths.modLists(), "JakQuestVR-launcher/" + PcBuild.VERSION);
            modList = read;
            modListBusy = false;
            log("mod lists read: " + read.entries.size() + " mods" + (read.problems.isEmpty() ? ""
                : "; " + String.join(" ", read.problems)));
            SwingUtilities.invokeLater(() -> {
                if (tab == 5) {
                    redraw();
                }
            });
        }, "mod-lists").start();
    }

    /** Browse mods: each game's mods in the lists, what each is here, and Add or Update for the texture packs. */
    private Card browseCard() {
        Card card = card();
        card.add(text("Browse mods", 16, PcArtwork.INK, true));
        card.add(text("This project's own mod list (texture packs and mods made for VR) and jakmods.dev's (the list the "
            + "OpenGOAL launcher's Mods reads), read when you ask" + (PcUpdater.automatic(paths)
                ? " and as the launcher starts (Automatic checks, on the Play tab)" : "") + ".", 13, PcArtwork.MUTED,
            false));
        ModList lists = modList;
        JPanel buttons = row(6);
        buttons.setBorder(new EmptyBorder(6, 0, 0, 0));
        Btn read = new Btn(modListBusy ? "Reading the mod lists..." : lists == null ? "Browse mods" : "Check again",
            lists == null, () -> {
                readModList();
                redraw();
            });
        read.setEnabled(!modListBusy);
        buttons.add(weighted(read, 1f));
        buttons.add(weighted(new Btn("Open jakmods.dev", false, () -> browse(ModList.COMMUNITY_SITE)), 1f));
        buttons.add(weighted(new Btn("About mods here", false, () -> browse(ModList.OURS_PAGE)), 1f));
        card.add(buttons);
        if (lists == null) {
            return card;
        }
        for (String problem : lists.problems) {
            card.add(text(problem, 13, PcArtwork.ACCENT, false));
        }
        card.add(text("Read at " + new SimpleDateFormat("HH:mm").format(new Date(lists.readAt)) + ".", 12,
            PcArtwork.MUTED, false));
        JPanel games = row(6);
        games.setBorder(new EmptyBorder(8, 0, 0, 0));
        String[] shortNames = {"Jak 1", "Jak II", "Jak 3"};
        for (int i = 0; i < VrOptions.GAMES.length; i++) {
            final int index = i;
            Btn b = new Btn(shortNames[i], false, () -> {
                browseGame = index;
                browseFlat = false;
                redraw();
            });
            if (i == browseGame) {
                b.chosen();
            }
            games.add(weighted(b, 1f));
        }
        card.add(games);
        String game = VrOptions.GAMES[browseGame];
        boolean locked = packBusy || PcSetup.STATE.running;
        List<ModList.Entry> flat = new ArrayList<>();
        int shown = 0;
        for (ModList.Entry e : lists.forGame(game)) {
            if (e.kind == ModList.Kind.FLAT_SCREEN) {
                flat.add(e);
                continue;
            }
            shown++;
            card.add(PcWidgets.gap(8));
            card.add(text(e.line(), 14, PcArtwork.INK, true));
            card.add(text(e.kindLabel() + "  ·  " + e.from() + (e.published.length() >= 10 ? "  ·  "
                + e.published.substring(0, 10) : ""), 12, PcArtwork.ACCENT, false));
            if (!e.description.isEmpty()) {
                card.add(text(e.description, 12, PcArtwork.MUTED, false));
            }
            String why = e.whyNot(false);
            if (why != null) {
                card.add(text(why, 12, PcArtwork.MUTED, false));
            }
            JPanel acts = row(6);
            acts.setBorder(new EmptyBorder(4, 0, 0, 0));
            if (why == null && e.kind == ModList.Kind.TEXTURE_PACK) {
                TexturePacks.Pack have = null;
                for (TexturePacks.Pack p : TexturePacks.list(paths.texturePacks(), game)) {
                    if (p.from.equals(e.ref())) {
                        have = p;
                    }
                }
                final TexturePacks.Pack old = have;
                Btn take;
                if (have == null) {
                    take = new Btn("Add", true, () -> addFromList(game, e, null));
                    take.setEnabled(!locked);
                } else if (lists.newer(e.ref(), have.version) != null) {
                    take = new Btn("Update to " + e.version, true, () -> addFromList(game, e, old));
                    take.setEnabled(!locked);
                } else {
                    take = new Btn("Added", false, () -> { });
                    take.setEnabled(false);
                }
                acts.add(weighted(take, 1f));
            }
            acts.add(weighted(new Btn("Its page", false, () -> browse(e.page)), 1f));
            card.add(acts);
        }
        if (shown == 0) {
            card.add(PcWidgets.gap(6));
            card.add(text("No texture packs or VR mods for " + shortNames[browseGame] + " in the lists yet. A pack's "
                + ".zip you have can be added above (Add a texture pack).", 13, PcArtwork.MUTED, false));
        }
        if (!flat.isEmpty()) {
            card.add(PcWidgets.gap(10));
            card.add(text(flat.size() + " of OpenGOAL's own mods for " + shortNames[browseGame] + " are flat screen "
                + "only: whole games of their own, without VR. Play them with the OpenGOAL launcher.", 13,
                PcArtwork.MUTED, false));
            JPanel toggle = row(6);
            toggle.setBorder(new EmptyBorder(4, 0, 0, 0));
            toggle.add(weighted(new Btn(browseFlat ? "Hide them" : "Show them", false, () -> {
                browseFlat = !browseFlat;
                redraw();
            }), 1f));
            card.add(toggle);
            if (browseFlat) {
                for (ModList.Entry e : flat) {
                    card.add(PcWidgets.gap(6));
                    card.add(text(e.line() + "  ·  Flat screen only", 13, PcArtwork.INK, true));
                    if (!e.description.isEmpty()) {
                        String d = e.description.length() > 220 ? e.description.substring(0, 217) + "..." : e.description;
                        card.add(text(d, 12, PcArtwork.MUTED, false));
                    }
                    JPanel acts = row(6);
                    acts.setBorder(new EmptyBorder(2, 0, 0, 0));
                    acts.add(weighted(new Btn("Its page", false, () -> browse(e.page)), 1f));
                    card.add(acts);
                }
            }
        }
        return card;
    }

    /** A texture pack from a mod list: downloaded, then added (or, with old, put in old's place: Update). */
    private void addFromList(String game, ModList.Entry entry, TexturePacks.Pack old) {
        if (packBusy) {
            return;
        }
        packBusy = true;
        modsNote = "Downloading " + entry.line() + "...";
        redraw();
        new Thread(() -> {
            File file = null;
            try {
                file = ModList.download(entry, "JakQuestVR-launcher/" + PcBuild.VERSION, paths.modDownloads());
                modsNote = "Adding " + entry.line() + "...";
                SwingUtilities.invokeLater(this::redraw);
                String name = entry.key + ".zip";
                TexturePacks.Pack p;
                try (java.io.InputStream in = new java.io.FileInputStream(file)) {
                    p = old == null ? TexturePacks.add(paths.texturePacks(), game, in, name, entry)
                        : TexturePacks.update(paths.texturePacks(), game, old.id, in, name, entry);
                }
                modsNote = (old == null ? "Added " : "Updated to ") + p.line() + ".";
                log("texture pack " + (old == null ? "added" : "updated") + " for " + game + " from " + entry.ref()
                    + " " + entry.version + ": " + p.id);
            } catch (IOException e) {
                modsNote = "Could not add " + entry.name + ": " + e.getMessage();
            } finally {
                if (file != null && file.isFile() && !file.delete()) {
                    file.deleteOnExit();
                }
                packBusy = false;
                SwingUtilities.invokeLater(this::redraw);
            }
        }, "mod-download").start();
    }

    private void saveParty() {
        try {
            VrOptions.writeParty(VrOptions.partyFile(paths.config()), partyName, partyServer, PcBuild.VERSION);
        } catch (IOException e) {
            log("party.txt: " + e.getMessage());
        }
    }

    // ---- Controls ----

    private void showControls() {
        List<ControlsHelp.Section> all = ControlsHelp.parse(readText("controls.md"));
        List<String> types = ControlsHelp.controllerTypes(all);
        Card picker = card();
        picker.add(text("Your controllers", 16, PcArtwork.INK, true));
        // CONTROLS.md's own words first ("...Where a controller lacks a button, the nearest it has:"), then the pick
        List<String> notes = ControlsHelp.pcNotes(all);
        picker.add(text("The same in all three games. " + (notes.isEmpty() ? "" : notes.get(0)), 13, PcArtwork.MUTED,
            false));
        for (int first = 0; first < types.size(); first += 2) {
            JPanel buttons = row(6);
            buttons.setBorder(new EmptyBorder(6, 0, 0, 0));
            for (int i = first; i < Math.min(first + 2, types.size()); i++) {
                final int index = i;
                Btn b = new Btn(types.get(i), false, () -> {
                    controller = index;
                    redraw();
                });
                if (controller == i) {
                    b.chosen();
                }
                buttons.add(weighted(b, 1f));
            }
            picker.add(buttons);
        }
        String differences = ControlsHelp.differences(all, controller);
        picker.add(gap(6));
        picker.add(text("Differences from the tables below: " + differences, 15, PcArtwork.ACCENT, false));
        for (int i = 1; i < notes.size(); i++) {
            picker.add(text(notes.get(i), 13, PcArtwork.MUTED, false));
        }
        page.add(picker);
        for (ControlsHelp.Section s : ControlsHelp.common(all)) {
            page.add(sectionCard(s));
        }
    }

    private Card sectionCard(ControlsHelp.Section s) {
        Card card = card();
        card.add(text(s.title, s.level <= 2 ? 18 : 16, PcArtwork.ACCENT, true));
        for (Object part : s.parts) {
            if (part instanceof String) {
                card.add(text((String) part, 14, PcArtwork.INK, false));
                continue;
            }
            ControlsHelp.Table t = (ControlsHelp.Table) part;
            card.add(tableRow(t.header, true));
            for (String[] r : t.rows) {
                card.add(tableRow(r, false));
            }
        }
        return card;
    }

    /**
     * One row of a table: the first cell bold, the last the widest. Of three (the controls: what you do, the game's
     * button, notes) the button stands out and the notes are quieter; of two (the credits) both read plainly.
     */
    private static JPanel tableRow(String[] r, boolean heading) {
        JPanel line = row(10);
        line.setBorder(new EmptyBorder(heading ? 8 : 4, 0, heading ? 2 : 4, 0));
        for (int i = 0; i < r.length; i++) {
            float weight = i == r.length - 1 ? 3f : 1.2f;
            Color colour = heading ? PcArtwork.MUTED : i == 0 || r.length == 2 ? PcArtwork.INK
                : i == r.length - 1 ? PcArtwork.MUTED : PcArtwork.ACCENT;
            line.add(weighted(text(r[i], heading ? 12 : 14, colour, i == 0 || heading), weight));
        }
        return line;
    }

    // ---- Logs ----

    private void showLogs() {
        Card card = card();
        card.add(text("Logs for a bug report", 18, PcArtwork.ACCENT, true));
        card.add(text("One button gathers everything a report needs into one zip in your Downloads folder: the games' "
            + "newest logs, each set-up's, your VR options and the launcher's own. Post it with what you were doing "
            + "in the project's Discord or GitHub issues. Nothing else of yours is in it.", 15, PcArtwork.INK, false));
        card.add(gap(8));
        JPanel buttons = row(6);
        buttons.add(weighted(new Btn("Gather the logs", true, () -> {
            try {
                logsZip = PcLogs.gather(paths);
                logsNote = "Made " + logsZip.getName() + " in " + paths.logs + ".";
                log("logs gathered: " + logsZip.getName());
                openFolder(paths.logs);
            } catch (IOException e) {
                logsNote = "Could not make the zip: " + e.getMessage();
            }
            redraw();
        }), 2f));
        buttons.add(weighted(new Btn("Open the logs folder", false, () -> openFolder(paths.logs)), 1f));
        card.add(buttons);
        if (!logsNote.isEmpty()) {
            card.add(text(logsNote, 14, PcArtwork.GOOD, false));
        }
        page.add(card);
        Card where = card();
        where.add(text("Where things are", 16, PcArtwork.INK, true));
        where.add(text("Your set-up, saves and choices: " + paths.home + "\nLogs and the zips: " + paths.logs
            + "\nAn update keeps all of them.", 14, PcArtwork.MUTED, false));
        page.add(where);
    }

    // ---- Credits ----

    /** CREDITS.md (bundled as credits.md), then the licence notices word for word. */
    private void showCredits() {
        for (ControlsHelp.Section s : ControlsHelp.parse(readText("credits.md"))) {
            page.add(sectionCard(s));
        }
        Card notices = card();
        notices.add(text("Open-source licences", 16, PcArtwork.INK, true));
        notices.add(PcWidgets.selectable(readText("third_party_notices.txt").trim(), 12, PcArtwork.MUTED));
        page.add(notices);
    }

    // ---- Tests ----

    /**
     * What the release's folder must hold for the launcher to work (tools/build_pc_release.py runs it on the folder it
     * made, before zipping it): an empty list when all is there.
     */
    static List<String> selfTest(PcPaths paths) {
        List<String> wrong = new ArrayList<>();
        for (String name : new String[] {"whats_new.txt", "controls.md", "credits.md", "third_party_notices.txt"}) {
            if (PcPaths.read(new File(paths.texts(), name)).trim().isEmpty()) {
                wrong.add("texts/" + name + " is missing or empty");
            }
        }
        List<ControlsHelp.Section> controls = ControlsHelp.parse(PcPaths.read(new File(paths.texts(), "controls.md")));
        if (ControlsHelp.controllerTypes(controls).size() < 4 || ControlsHelp.common(controls).size() < 3) {
            wrong.add("controls.md has not the controllers and the sections the Controls tab shows");
        }
        boolean table = false;
        for (ControlsHelp.Section s : ControlsHelp.parse(PcPaths.read(new File(paths.texts(), "credits.md")))) {
            for (Object part : s.parts) {
                table |= part instanceof ControlsHelp.Table && ((ControlsHelp.Table) part).rows.size() >= 3;
            }
        }
        if (!table) {
            wrong.add("credits.md has no credits table");
        }
        for (String name : new String[] {"gk.exe", "extractor.exe", "openxr_loader.dll", "SDL3.dll", "vcruntime140.dll",
                "msvcp140.dll"}) {
            if (!new File(paths.gameFolder(), name).isFile()) {
                wrong.add("game/" + name + " is missing");
            }
        }
        for (String part : PcPaths.TEMPLATE_PARTS) {
            if (!new File(paths.template(), part).isDirectory()) {
                wrong.add("game/data/" + part + " is missing");
            }
        }
        for (String game : VrOptions.GAMES) {
            for (String needed : new String[] {"goal_src/" + game, "decompiler/config/" + game, "game/assets/" + game}) {
                if (!new File(paths.template(), needed).isDirectory()) {
                    wrong.add("game/data/" + needed + " is missing");
                }
            }
        }
        if (!new File(paths.template(), "game/graphics/opengl_renderer/shaders").isDirectory()) {
            wrong.add("game/data/game/graphics/opengl_renderer/shaders is missing");
        }
        return wrong;
    }

    /** Draws tab `index` into a PNG (the launcher's tests: no window shown, no network). */
    private void render(int index, File out, int w, int h) throws IOException {
        frame = new JFrame(TITLE);
        buildFrame();
        frame.setContentPane(root);
        root.setSize(w, h);
        showTab(index);
        root.setSize(w, h);
        // twice: the first pass shows the scroll bar, which narrows the page the second lays out
        layoutAll(root);
        layoutAll(root);
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        // the desktop's text hints, as the window draws with: the lines then break where they would on screen
        Object hints = java.awt.Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if (hints instanceof Map) {
            g.addRenderingHints((Map<?, ?>) hints);
        }
        root.printAll(g);
        g.dispose();
        ImageIO.write(image, "png", out);
        watcher.stop();
    }

    private static void layoutAll(java.awt.Container c) {
        c.doLayout();
        for (java.awt.Component child : c.getComponents()) {
            if (child instanceof java.awt.Container) {
                layoutAll((java.awt.Container) child);
            }
        }
    }
}
