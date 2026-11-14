// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
package dev.jakquest.vr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Which disc a game was set up from, for the launchers' game cards. OpenGOAL's extractor writes the disc's serial and
 * the hash of its program to iso_data/(game)/buildinfo.json (decompiler/extractor/main.cpp), and its database
 * (decompiler/extractor/extractor_util.cpp) knows every retail version by those two. Every version is built from the
 * same patched source, so this is only shown, never used to choose code. Plain Java for both launchers: the PC's
 * shows {@link #describe}, the Quest's its own translation of {@link #region}'s key. The hash is read as text: it does
 * not fit in a Java long.
 */
public final class DiscVersion {
    private DiscVersion() {}

    /** The keys {@link #region} gives; the Quest app has a string disc_(key) for each. */
    public static final String US = "us", US_BLACK_LABEL = "us_black_label", US_LATER = "us_later",
        US_GREATEST_HITS = "us_greatest_hits", PAL = "pal", JAPANESE = "japanese", KOREAN = "korean",
        UNKNOWN = "unknown";

    private static final Pattern SERIAL = Pattern.compile("\"serial\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern ELF_HASH = Pattern.compile("\"elf_hash\"\\s*:\\s*(\\d+)");
    private static final String JAK1_US_BLACK_LABEL = "7280758013604870207";

    /** {serial, program hash} from (isoDataFolder)/buildinfo.json, or null when it is not there. */
    public static String[] read(File isoDataFolder) {
        File info = new File(isoDataFolder, "buildinfo.json");
        if (!info.isFile()) {
            return null;
        }
        String text;
        try {
            text = new String(Files.readAllBytes(info.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
        Matcher serial = SERIAL.matcher(text);
        if (!serial.find()) {
            return null;
        }
        Matcher hash = ELF_HASH.matcher(text);
        return new String[] {serial.group(1), hash.find() ? hash.group(1) : ""};
    }

    /** The version's key from its serial (for Jak 1 US, which printing, from its program's hash). */
    public static String region(String serial, String elfHash) {
        switch (serial) {
            case "SCUS-97124":
                return JAK1_US_BLACK_LABEL.equals(elfHash) ? US_BLACK_LABEL : US_LATER;
            case "SCUS-97265": // Jak II, both US printings
            case "SCUS-97330": // Jak 3
                return US;
            case "SCUS-97516":
                return US_GREATEST_HITS;
            case "SCES-50361":
            case "SCES-51608":
            case "SCES-52460":
                return PAL;
            case "SCPS-15021":
            case "SCPS-15057":
                return JAPANESE;
            case "SCPS-56003":
            case "SCKA-20010":
            case "SCKA-20040":
                return KOREAN;
            default:
                return UNKNOWN;
        }
    }

    /** A key's name in English. */
    public static String english(String key) {
        switch (key) {
            case US: return "US";
            case US_BLACK_LABEL: return "US, first printing (black label)";
            case US_LATER: return "US, later printing";
            case US_GREATEST_HITS: return "US, Greatest Hits";
            case PAL: return "European (PAL)";
            case JAPANESE: return "Japanese";
            case KOREAN: return "Korean";
            default: return "a version this release does not know";
        }
    }

    /** "Your disc: European (PAL), SCES-50361", or null when the folder has no buildinfo.json. */
    public static String describe(File isoDataFolder) {
        String[] disc = read(isoDataFolder);
        return disc == null ? null : "Your disc: " + english(region(disc[0], disc[1])) + ", " + disc[0];
    }
}
