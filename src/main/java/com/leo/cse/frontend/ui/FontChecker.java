package com.leo.cse.frontend.ui;

import com.leo.cse.util.StringUtils;

import java.awt.Font;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Checks whether the font can display specified text
 *
 * Sometimes there could be some missing glyphs in a font.
 * So we should check text that we are about to display and fallback to default font
 * if there are characters that could not be displayed
 */
public class FontChecker {
    private final Set<Character> missingGlyphs = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<String> supportedStrings = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<String> notSupportedStrings = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Font font;

    private final static Map<Font, FontChecker> sInstances = new ConcurrentHashMap<>();

    private FontChecker(Font font) {
        this.font = font;
    }

    public static FontChecker getInstance(Font font) {
        FontChecker localInstance = sInstances.get(font);

        if (localInstance == null) {
            synchronized (FontChecker.class) {
                localInstance = sInstances.get(font);
                if (localInstance == null) {
                    sInstances.put(font, localInstance = new FontChecker(font));
                }
            }
        }
        return localInstance;
    }

    /**
     * Indicates whether or not the font can display a specified string.
     * @param text Input string
     * @return True if the font can display a string, false otherwise
     */
    public boolean canFontDisplayText(String text) {
        if (font == null) {
            return false;
        }

        if (StringUtils.isNullOrEmpty(text)) {
            return true;
        }

        if (notSupportedStrings.contains(text)) {
            return false;
        } else if (supportedStrings.contains(text)) {
            return true;
        }

        for (int i = 0; i < text.length(); i++) {
            final char ch = text.charAt(i);
            if (missingGlyphs.contains(ch)) {
                notSupportedStrings.add(text);
                return false;
            }
            if (!font.canDisplay(ch)) {
                missingGlyphs.add(ch);
                notSupportedStrings.add(text);
                return false;
            }
        }

        supportedStrings.add(text);
        return true;
    }
}
