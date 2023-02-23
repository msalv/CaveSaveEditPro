package com.leo.cse.frontend.editor.selectors;

import com.leo.cse.util.Dialogs;
import com.leo.cse.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EncodingSelectionDialog {
    public String select(String initial) {
        final List<GameEncoding> encodings = getEncodings();

        if (encodings.isEmpty()) {
            return null;
        }

        final GameEncoding current = getCurrentEncoding(initial, encodings);

        final GameEncoding selected = Dialogs.showSelectionDialog("Select an encoding",
                encodings.toArray(new GameEncoding[0]),
                current);

        if (selected == null || selected.equals(current)) {
            return null;
        }

        return selected.charset;
    }

    private static GameEncoding getCurrentEncoding(String initail, List<GameEncoding> encodings) {
        String name = "";
        for (GameEncoding encoding : encodings) {
            if (encoding.charset != null && encoding.charset.compareToIgnoreCase(initail) == 0) {
                name = encoding.name;
                break;
            }
        }

        return new GameEncoding(name, initail);
    }

    private static List<GameEncoding> getEncodings() {
        final List<GameEncoding> encodings = new ArrayList<>();

        encodings.add(new GameEncoding("Chinese (Simplified)", "GB18030"));
        encodings.add(new GameEncoding("Chinese (Traditional)", "Cp950"));
        encodings.add(new GameEncoding("Cyrillic", "Windows-1251"));
        encodings.add(new GameEncoding("Japanese", "Cp943C"));
        encodings.add(new GameEncoding("Western", "Windows-1252"));

        return encodings;
    }

    private static class GameEncoding implements Comparable<GameEncoding> {
        final String name;
        final String charset;

        private GameEncoding(String name, String charset) {
            this.name = name;
            this.charset = charset;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GameEncoding other = (GameEncoding) o;

            if (!name.equals(other.name)) return false;
            return other.charset != null && other.charset.compareToIgnoreCase(charset) == 0;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (charset != null ? charset.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            if (StringUtils.isNullOrEmpty(name)) {
                return charset;
            }
            return String.format("%s - %s", name, charset);
        }

        @Override
        public int compareTo(GameEncoding o) {
            return charset.compareTo(o.charset);
        }
    }
}
