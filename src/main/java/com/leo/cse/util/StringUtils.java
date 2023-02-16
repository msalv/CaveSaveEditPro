package com.leo.cse.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.TextAttribute;
import java.io.UnsupportedEncodingException;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.Map;

import javax.swing.JOptionPane;

public class StringUtils {
    private static final String ELLIPSIS = "...";

    public static String toString(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof Object[]) {
            return Arrays.toString((Object[]) obj);
        } else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        } else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        } else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        } else if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        } else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        } else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        } else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        } else if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        }

        return obj.toString();
    }

    public static boolean isNullOrEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static String ellipsize(String text, FontMetrics fontMetrics, int availWidth) {
        if (isNullOrEmpty(text)) {
            return "";
        }

        final int width = fontMetrics.stringWidth(text);
        if (width <= availWidth) {
            return text;
        }

        final int ellipsisWidth = fontMetrics.stringWidth(ELLIPSIS);

        final int avail = availWidth - ellipsisWidth;
        if (avail <= 0) {
            return ELLIPSIS;
        }

        final StringBuilder sb = new StringBuilder(text);
        final int length = text.length();

        int measuredWidth = 0;
        for (int i = 0; i < length; ++i) {
            final char ch = text.charAt(i);
            measuredWidth += fontMetrics.charWidth(ch);
            if (measuredWidth > avail) {
                sb.setLength(i);
                break;
            }
        }

        int trimCount = 0;
        for (int j = sb.length() - 1; j >= 0; --j) {
            if (!Character.isWhitespace(sb.charAt(j))) {
                break;
            }
            trimCount++;
        }

        if (trimCount > 0) {
            sb.setLength(sb.length() - trimCount);
        }

        return sb.append(ELLIPSIS).toString();
    }

    public static AttributedString newAttributedString(String text, Font font) {
        final Map<TextAttribute, ?> attrs = (font != null && font.hasLayoutAttributes()) ? font.getAttributes() : null;
        final AttributedString styledText = (attrs != null && !attrs.isEmpty()) ? new AttributedString(text, attrs) : new AttributedString(text);
        if (font != null && !isNullOrEmpty(text)) {
            styledText.addAttribute(TextAttribute.FONT, font, 0, text.length());
        }
        return styledText;
    }

    public static boolean isEncodingSupported(String encoding) {
        final byte[] test = new byte[] { (byte) 'T', (byte) 'e', (byte) 's', (byte) 't' };

        try {
            new String(test, encoding);
        } catch (UnsupportedEncodingException e1) {
            JOptionPane.showMessageDialog(
                    null,
                    String.format("Encoding \"%s\" is unsupported!", encoding),
                    "Unsupported encoding",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        return true;
    }

    public static int parseIntSafe(String val) {
        int result = 0;

        int j = 1;
        for (int i = val.length() - 1; i >= 0; --i) {
            final char ch = val.charAt(i);
            if (ch >= '0' && ch <= '9') {
                result += Character.digit(ch, 10) * j;
                j *= 10;
            }
        }

        return result;
    }
}
