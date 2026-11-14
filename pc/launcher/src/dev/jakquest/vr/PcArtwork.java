// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (c) 2026 the Jak and Daxter Quest VR contributors
// After Ratchet & Clank Quest VR's PcArtwork (the same author's project): the same plates and icon writer, its own look.
package dev.jakquest.vr;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * The launcher's look, drawn in code and all original: a dusk sky over the sea, soft glows of light, a glowing orb with
 * a ring as the mark, teal and sky-blue plates. No art from the games is carried.
 */
final class PcArtwork {
    static final Color ACCENT = new Color(0xff3cc8ff, true);       // sky blue: the primary button, chosen tabs
    static final Color ACCENT_DARK = new Color(0xff16719c, true);
    static final Color GREEN = new Color(0xff8be35a, true);
    static final Color STEEL = new Color(0xffb7d3d6, true);
    static final Color NAVY = new Color(0xff071c24, true);
    static final Color PANEL = new Color(0xdd0d2a33, true);
    static final Color INK = new Color(0xffeefafb, true);
    static final Color MUTED = new Color(0xff9cbcc2, true);
    static final Color BAD = new Color(0xffff6a5a, true);
    static final Color GOOD = new Color(0xff8be35a, true);
    static final Color BUTTON = new Color(0xff16404b, true);
    static final Color BUTTON_EDGE = new Color(0xff3f7f8c, true);
    static final Color CARD_EDGE = new Color(0x553f7f8c, true);

    private PcArtwork() {}

    static void smooth(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /** The page behind everything: a dusk sky going to sea, a few soft lights. */
    static void sky(Graphics2D g, int w, int h) {
        smooth(g);
        g.setPaint(new LinearGradientPaint(0, 0, 0, Math.max(1, h), new float[] {0f, 0.55f, 0.62f, 1f},
            new Color[] {new Color(0xff0b2233, true), new Color(0xff1d5a6b, true), new Color(0xff0f3c4a, true),
                new Color(0xff04141c, true)}));
        g.fillRect(0, 0, w, h);
        float sx = w * 0.82f, sy = h * 0.52f, sr = h * 0.42f;
        if (sr > 1) {
            g.setPaint(new RadialGradientPaint(sx, sy, sr, new float[] {0f, 0.35f, 1f},
                new Color[] {new Color(0x88ffd38a, true), new Color(0x33ff9f5a, true), new Color(0x00ff9f5a, true)}));
            g.fill(new Ellipse2D.Float(sx - sr, sy - sr, 2 * sr, 2 * sr));
        }
        for (int i = 0; i < 18; i++) {
            float x = (i * 173.3f) % Math.max(1, w), y = h * 0.62f + (i * 61.7f) % Math.max(1, h * 0.36f);
            float r = 6 + (i % 5) * 5;
            Color c = i % 3 == 0 ? new Color(0x3389e35a, true) : new Color(0x333cc8ff, true);
            g.setPaint(new RadialGradientPaint(x, y, r, new float[] {0f, 1f}, new Color[] {c, new Color(0, true)}));
            g.fill(new Ellipse2D.Float(x - r, y - r, 2 * r, 2 * r));
        }
        g.setColor(new Color(0x55040e14, true));
        g.fillRect(0, 0, w, h);
    }

    /** The mark beside the title: a glowing orb in a ring, centred in a square of `size`. */
    static void orb(Graphics2D g, float x, float y, float size) {
        smooth(g);
        float cx = x + size / 2, cy = y + size / 2, r = size * 0.46f;
        g.setPaint(new RadialGradientPaint(cx, cy, r, new float[] {0f, 0.55f, 1f},
            new Color[] {new Color(0xaa3cc8ff, true), new Color(0x443cc8ff, true), new Color(0x003cc8ff, true)}));
        g.fill(new Ellipse2D.Float(cx - r, cy - r, 2 * r, 2 * r));
        float o = r * 0.62f;
        g.setPaint(new RadialGradientPaint(cx - o * 0.3f, cy - o * 0.35f, o * 1.3f, new float[] {0f, 0.45f, 1f},
            new Color[] {new Color(0xffe8fbff, true), ACCENT, ACCENT_DARK}));
        g.fill(new Ellipse2D.Float(cx - o, cy - o, 2 * o, 2 * o));
        g.setStroke(new BasicStroke(Math.max(1f, r * 0.09f)));
        g.setColor(GREEN);
        g.draw(new Ellipse2D.Float(cx - r * 0.86f, cy - r * 0.86f, r * 1.72f, r * 1.72f));
    }

    static BufferedImage orbImage(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        orb(g, 0, 0, size);
        g.dispose();
        return image;
    }

    /** The window's icons at the sizes Windows asks for. */
    static List<BufferedImage> icons() {
        List<BufferedImage> icons = new ArrayList<>();
        for (int size : new int[] {16, 24, 32, 48, 64, 128, 256}) {
            icons.add(orbImage(size));
        }
        return icons;
    }

    /** The .exe's icon (tools/build_pc_launcher.sh): an .ico of PNG images, one per size. */
    static void writeIcon(File file) throws IOException {
        List<byte[]> images = new ArrayList<>();
        int[] sizes = {16, 24, 32, 48, 64, 128, 256};
        for (int size : sizes) {
            ByteArrayOutputStream png = new ByteArrayOutputStream();
            ImageIO.write(orbImage(size), "png", png);
            images.add(png.toByteArray());
        }
        int header = 6 + 16 * sizes.length;
        int total = header;
        for (byte[] image : images) {
            total += image.length;
        }
        ByteBuffer out = ByteBuffer.allocate(total).order(ByteOrder.LITTLE_ENDIAN);
        out.putShort((short) 0).putShort((short) 1).putShort((short) sizes.length);
        int offset = header;
        for (int i = 0; i < sizes.length; i++) {
            out.put((byte) (sizes[i] >= 256 ? 0 : sizes[i])).put((byte) (sizes[i] >= 256 ? 0 : sizes[i]));
            out.put((byte) 0).put((byte) 0).putShort((short) 1).putShort((short) 32);
            out.putInt(images.get(i).length).putInt(offset);
            offset += images.get(i).length;
        }
        for (byte[] image : images) {
            out.put(image);
        }
        Files.write(file.toPath(), out.array());
    }

    /** A plate: a rounded fill with an edge, as the headset's buttons and cards. */
    static void plate(Graphics2D g, int w, int h, Color fill, Color edge, float radius) {
        smooth(g);
        float arc = radius * 2;
        g.setColor(fill);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arc, arc));
        g.setColor(edge);
        g.setStroke(new BasicStroke(1f));
        g.draw(new java.awt.geom.RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arc, arc));
    }
}
