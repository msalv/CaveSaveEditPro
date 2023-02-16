package com.leo.cse.util;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ColorUtils {
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    private static final ThreadLocal<float[]> TEMP_ARRAY = new ThreadLocal<>();
    private static final ThreadLocal<double[]> TEMP_DOUBLE_ARRAY = new ThreadLocal<>();

    /**
     * Filters an image so that any black pixels are turned into transparent pixels.
     *
     * @param src source image
     * @return filtered image
     */
    public static BufferedImage replaceColor(BufferedImage src, Color target, Color replacement) {
        final BufferedImage dest = new BufferedImage(
                src.getWidth(),
                src.getHeight(),
                BufferedImage.TYPE_INT_ARGB_PRE
        );

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int px = src.getRGB(x, y);
                if (px == target.getRGB()) {
                    dest.setRGB(x, y, replacement.getRGB());
                } else {
                    dest.setRGB(x, y, px);
                }
            }
        }
        return dest;
    }

    public static Color setAlphaComponent(Color color, int alpha) {
        return new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                Math.max(0, Math.min(255, alpha)));
    }

    public static Color blendOpaque(Color bottom, Color top) {
        final int ta = top.getAlpha();

        final int red = (top.getRed() * ta + bottom.getRed() * (255 - ta)) / 255;
        final int green = (top.getGreen() * ta + bottom.getGreen() * (255 - ta)) / 255;
        final int blue = (top.getBlue() * ta + bottom.getBlue() * (255 - ta)) / 255;

        return new Color(red, green, blue);
    }

    public static int blendARGB(int color1, int color2, float ratio) {
        final float inverseRatio = 1 - ratio;
        float a = alpha(color1) * inverseRatio + alpha(color2) * ratio;
        float r = red(color1) * inverseRatio + red(color2) * ratio;
        float g = green(color1) * inverseRatio + green(color2) * ratio;
        float b = blue(color1) * inverseRatio + blue(color2) * ratio;
        return argb((int) a, (int) r, (int) g, (int) b);
    }

    public static int setSaturation(int color, float saturation) {
        final float[] tempArray = getTempFloat3Array();
        Color.RGBtoHSB(red(color), green(color), blue(color), tempArray);
        tempArray[1] = saturation;
        return Color.HSBtoRGB(tempArray[0], tempArray[1], tempArray[2]);
    }

    public static Color setLightness(Color color, float lightness) {
        final float[] tempArray = getTempFloat3Array();
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), tempArray);
        tempArray[2] = lightness;
        return new Color(Color.HSBtoRGB(tempArray[0], tempArray[1], tempArray[2]));
    }

    public static float getLightness(Color color) {
        final float[] tempArray = getTempFloat3Array();
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), tempArray);
        return tempArray[2];
    }

    public static boolean isDark(Color color) {
        return calculateLuminance(color.getRGB()) < 0.4;
    }

    public static double calculateLuminance(int color) {
        final double[] result = getTempDouble3Array();
        RGBToXYZ(red(color), green(color), blue(color), result);
        return result[1] / 100;
    }

    public static void RGBToXYZ(int r, int g, int b, double[] outXyz) {
        if (outXyz.length != 3) {
            throw new IllegalArgumentException("outXyz must have a length of 3.");
        }

        double sr = r / 255.0;
        sr = sr < 0.04045 ? sr / 12.92 : Math.pow((sr + 0.055) / 1.055, 2.4);
        double sg = g / 255.0;
        sg = sg < 0.04045 ? sg / 12.92 : Math.pow((sg + 0.055) / 1.055, 2.4);
        double sb = b / 255.0;
        sb = sb < 0.04045 ? sb / 12.92 : Math.pow((sb + 0.055) / 1.055, 2.4);

        outXyz[0] = 100 * (sr * 0.4124 + sg * 0.3576 + sb * 0.1805);
        outXyz[1] = 100 * (sr * 0.2126 + sg * 0.7152 + sb * 0.0722);
        outXyz[2] = 100 * (sr * 0.0193 + sg * 0.1192 + sb * 0.9505);
    }

    public static Color getHueColor(Color color) {
        final float[] tempArray = getTempFloat3Array();
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), tempArray);
        tempArray[1] = 1f;
        tempArray[2] = 0.5f;
        return new Color(Color.HSBtoRGB(tempArray[0], tempArray[1], tempArray[2]));
    }

    public static double distance(int a, int b) {
        final int r1 = red(a);
        final int g1 = green(a);
        final int b1 = blue(a);

        final int r2 = red(b);
        final int g2 = green(b);
        final int b2 = blue(b);

        return Math.sqrt(Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2));
    }

    public static int alpha(int color) {
        return color >>> 24;
    }

    public static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    private static float[] getTempFloat3Array() {
        float[] result = TEMP_ARRAY.get();
        if (result == null) {
            result = new float[3];
            TEMP_ARRAY.set(result);
        }
        return result;
    }

    private static double[] getTempDouble3Array() {
        double[] result = TEMP_DOUBLE_ARRAY.get();
        if (result == null) {
            result = new double[3];
            TEMP_DOUBLE_ARRAY.set(result);
        }
        return result;
    }
}
