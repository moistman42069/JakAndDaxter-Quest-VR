// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
package dev.jakquest.vr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The launchers' Controls section, from docs/CONTROLS.md (bundled with each launcher as controls.md), so the tables
 * players read are the ones the code is documented by. Plain Java: the Quest's launcher shows the Quest Touch controls
 * only (every section but "PC VR controllers"); the PC's shows them all, with a pick of controller type that adds that
 * controller's differences.
 */
public final class ControlsHelp {
    private ControlsHelp() {}

    /** A table: its heading row and its rows, the Markdown taken out of each cell. */
    public static final class Table {
        public final String[] header;
        public final List<String[]> rows = new ArrayList<>();

        Table(String[] header) {
            this.header = header;
        }
    }

    /** A section (a "##" or "###" heading) and what is in it: paragraphs (String) and tables (Table), in order. */
    public static final class Section {
        public final String title;
        public final int level;
        public final List<Object> parts = new ArrayList<>();

        Section(String title, int level) {
            this.title = title;
            this.level = level;
        }
    }

    /** The PC VR section's title in CONTROLS.md. */
    public static final String PC_SECTION = "PC VR controllers";
    /** The Quest's own controllers, the first row of the PC VR table. */
    public static final String QUEST_TOUCH = "Quest Touch";

    public static List<Section> parse(String markdown) {
        List<Section> sections = new ArrayList<>();
        Section current = new Section("", 1);
        StringBuilder paragraph = new StringBuilder();
        Table table = null;
        for (String raw : markdown.replace("\r", "").split("\n", -1)) {
            String line = raw.trim();
            if (line.startsWith("|")) {
                flush(current, paragraph);
                String[] cells = cells(line);
                if (table == null) {
                    table = new Table(cells);
                    current.parts.add(table);
                } else if (!isRule(cells)) {
                    table.rows.add(cells);
                }
                continue;
            }
            table = null;
            if (line.startsWith("#")) {
                flush(current, paragraph);
                int level = 0;
                while (level < line.length() && line.charAt(level) == '#') level++;
                if (!current.title.isEmpty() || !current.parts.isEmpty()) sections.add(current);
                current = new Section(plain(line.substring(level).trim()), level);
            } else if (line.isEmpty()) {
                flush(current, paragraph);
            } else {
                if (paragraph.length() > 0) paragraph.append(' ');
                paragraph.append(line);
            }
        }
        flush(current, paragraph);
        if (!current.title.isEmpty() || !current.parts.isEmpty()) sections.add(current);
        return sections;
    }

    /** The Quest's launcher: everything but the PC VR controllers, and not the document's own title. */
    public static List<Section> forQuest(List<Section> all) {
        List<Section> out = new ArrayList<>();
        for (Section s : all) {
            if (s.level <= 1 || s.title.equals(PC_SECTION)) continue;
            out.add(s);
        }
        return out;
    }

    /** The PC's launcher: every section but the PC VR one, which controllerTypes and differences read. */
    public static List<Section> common(List<Section> all) {
        return forQuest(all);
    }

    /** The controller types the PC VR table names, each row's first cell. */
    public static List<String> controllerTypes(List<Section> all) {
        Table t = pcTable(all);
        if (t == null) return Collections.singletonList(QUEST_TOUCH);
        List<String> names = new ArrayList<>();
        for (String[] row : t.rows) names.add(row[0]);
        return names;
    }

    /** What the controller type of `index` (controllerTypes) does differently, "None." for the Quest's own. */
    public static String differences(List<Section> all, int index) {
        Table t = pcTable(all);
        if (t == null || index < 0 || index >= t.rows.size()) return "";
        String[] row = t.rows.get(index);
        return row.length > 1 ? row[1] : "";
    }

    /** The paragraphs of the PC VR section (what every controller without a thumb rest gets, and so on). */
    public static List<String> pcNotes(List<Section> all) {
        List<String> notes = new ArrayList<>();
        for (Section s : all) {
            if (!s.title.equals(PC_SECTION)) continue;
            for (Object o : s.parts) if (o instanceof String) notes.add((String) o);
        }
        return notes;
    }

    private static Table pcTable(List<Section> all) {
        for (Section s : all) {
            if (!s.title.equals(PC_SECTION)) continue;
            for (Object o : s.parts) if (o instanceof Table) return (Table) o;
        }
        return null;
    }

    private static void flush(Section s, StringBuilder paragraph) {
        if (paragraph.length() == 0) return;
        s.parts.add(plain(paragraph.toString()));
        paragraph.setLength(0);
    }

    private static String[] cells(String line) {
        String body = line.substring(1);
        if (body.endsWith("|")) body = body.substring(0, body.length() - 1);
        String[] parts = body.split("\\|", -1);
        for (int i = 0; i < parts.length; i++) parts[i] = plain(parts[i].trim());
        return parts;
    }

    private static boolean isRule(String[] cells) {
        for (String c : cells) if (!c.matches(":?-{3,}:?")) return false;
        return true;
    }

    /** Markdown's marks out: bold, code, links (their text kept). */
    public static String plain(String text) {
        return text.replace("**", "").replace("`", "").replaceAll("\\[([^\\]]*)\\]\\([^)]*\\)", "$1");
    }
}
