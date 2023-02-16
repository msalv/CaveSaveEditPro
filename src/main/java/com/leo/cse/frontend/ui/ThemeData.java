package com.leo.cse.frontend.ui;

import com.leo.cse.frontend.Config;
import com.leo.cse.util.ColorUtils;

import java.awt.Color;
import java.awt.Font;

public class ThemeData {
    public static final Color COLOR_FG_DEFAULT = new Color(0xffa0b5de);
    public static final Color COLOR_BG_DEFAULT = new Color(0xff212639);
    private static final Color COLOR_TEXT_DEFAULT = Color.WHITE;
    private static final Color COLOR_ACCENT = new Color(0xffffb500);

    private static Color foregroundColor;
    private static Color backgroundColor;
    private static Color textColor;
    private static Color textColorDisabled;
    private static Color textColorSecondary;
    private static Color hoverColor;
    private static Color accentColor;

    private final static Font fallbackFont = new Font(Font.DIALOG, Font.PLAIN, 11);

    public static void reset() {
        foregroundColor = null;
        backgroundColor = null;
        textColor = null;
        textColorDisabled = null;
        textColorSecondary = null;
        hoverColor = null;
        accentColor = null;
    }

    public static Color getBackgroundColor() {
        if (backgroundColor == null) {
            backgroundColor = Config.getColor(Config.KEY_BACKGROUND_COLOR, COLOR_BG_DEFAULT);
        }
        return backgroundColor;
    }

    public static Color getForegroundColor() {
        if (foregroundColor == null) {
            foregroundColor = Config.getColor(Config.KEY_FOREGROUND_COLOR, COLOR_FG_DEFAULT);
        }
        return foregroundColor;
    }

    public static void setForegroundColor(Color foregroundColor) {
        ThemeData.foregroundColor = foregroundColor;
        Config.setColor(Config.KEY_FOREGROUND_COLOR, foregroundColor);
    }

    public static Color getTextColor() {
        if (textColor == null) {
            textColor = Config.getColor(Config.KEY_TEXT_COLOR, COLOR_TEXT_DEFAULT);
        }
        return textColor;
    }

    public static Color getTextColorDisabled() {
        if (textColorDisabled == null) {
            textColorDisabled = ColorUtils.setAlphaComponent(getTextColor(), 31);
        }
        return textColorDisabled;
    }

    public static Color getHoverColor() {
        if (hoverColor == null) {
            hoverColor = ColorUtils.setAlphaComponent(getForegroundColor(), 31);
        }
        return hoverColor;
    }

    public static Color getTextColorSecondary() {
        if (textColorSecondary == null) {
            textColorSecondary = ColorUtils.setAlphaComponent(getForegroundColor(), 127);
        }
        return textColorSecondary;
    }

    public static Color getAccentColor() {
        if (accentColor == null) {
            accentColor = Config.getColor(Config.KEY_ACCENT_COLOR, COLOR_ACCENT);
        }
        return accentColor;
    }

    public static Font getFallbackFont() {
        return fallbackFont;
    }
}
