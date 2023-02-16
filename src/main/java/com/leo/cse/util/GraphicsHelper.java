package com.leo.cse.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GraphicsHelper {
    private static void drawTextLine(Graphics g, String text, int x, int y) {
        final Color textColor = g.getColor();
        final Color shadowColor = ColorUtils.setAlphaComponent(textColor, 31);
        g.setColor(shadowColor);
        g.drawString(text, x + 1, y + 1);
        g.setColor(textColor);
        g.drawString(text, x, y);
    }

    public static void drawTextCentered(Graphics g, String text, int cx, int cy) {
        if (StringUtils.isNullOrEmpty(text)) {
            return;
        }

        final String[] lines = text.split("\n");
        final int lineSpace = g.getFontMetrics().getHeight() - 1;

        cy -= (lineSpace * lines.length) / 2;

        for (String line : lines) {
            cy += lineSpace;
            drawTextLine(g, line, cx - g.getFontMetrics().stringWidth(line) / 2, cy);
        }
    }

    public static void applyQualityRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}
