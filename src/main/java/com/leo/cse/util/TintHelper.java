package com.leo.cse.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

// @see https://stackoverflow.com/a/14225857
public class TintHelper {
    private static GraphicsConfiguration getGraphicsConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
    }

    private static BufferedImage createCompatibleImage(int width, int height) {
        final BufferedImage image = getGraphicsConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        image.coerceData(true);
        return image;
    }

    public static BufferedImage tint(BufferedImage source, Color color, float alpha) {
        final int imgWidth = source.getWidth();
        final int imgHeight = source.getHeight();

        final BufferedImage tintedImage = createCompatibleImage(imgWidth, imgHeight);
        final Graphics2D g2 = tintedImage.createGraphics();
        GraphicsHelper.applyQualityRenderingHints(g2);

        g2.drawImage(source, 0, 0, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha));
        g2.setColor(color);

        g2.fillRect(0, 0, source.getWidth(), source.getHeight());
        g2.dispose();

        return tintedImage;
    }
}
