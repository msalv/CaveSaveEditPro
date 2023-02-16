package com.leo.cse.frontend.dialogs;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.RootDialog;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.util.LinksHelper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowEvent;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MapFlagsInfoDialog extends RootDialog {
    private final static Dimension CONTENT_SIZE = new Dimension(480, 360);

    private final static String[] DESCRIPTION = {
            "Map Flags are special kind of flags that were never used in the game.",
            "\nHowever, there are two commands associated with map flags available in the TSC game scripts: <MP+ and <MPJ.",
            "\nThey function the same as regular flags with the exception that you are limited to one map flag per map, " +
                    "and that the jump command (<MPJ) assumes it's looking for the flag for the current map."
    };

    private final static String[] EXAMPLE = {
            "#0101",
            "<KEY<MPJ0102<MSGArea discovered!<MP+0020<NOD<CLO<END",
            "\n#0102",
            "<KEY<MSGYou've been here before...<NOD<CLO<END"
    };

    private final static String EXAMPLE_NOTE = "In this example, event #0101 jumps to event #0102 " +
            "(<MPJ0102) if current map's flag was set, or sets the flag for map 20 (<MP+0020) otherwise.";

    private final static String SOURCE_URL = "https://www.cavestory.org/guides/kimitech/";

    private final static String[] SOURCE_NOTE = {
            "\nSource: The KimiTech Industries Modders Guide",
            "To learn more about in-game scripting language visit",
            SOURCE_URL
    };

    public MapFlagsInfoDialog(
            Frame parentFrame,
            Component parentComponent) {
        super(parentFrame, "Map Flags", true);

        setMinimumSize(CONTENT_SIZE);

        setResizable(false);
        setLocationRelativeTo(parentComponent);

        final Container contentPane = getContentPane();
        contentPane.setBackground(ThemeData.getBackgroundColor());
        contentPane.setPreferredSize(CONTENT_SIZE);
        contentPane.add(new MapFlagsInfoComponent(this));

        pack();
    }

    private void dispatchClose() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private static class MapFlagsInfoComponent extends VerticalLayout {
        private final MapFlagsInfoDialog dialog;

        MapFlagsInfoComponent(MapFlagsInfoDialog dialog) {
            super();
            this.dialog = dialog;
            init();
        }

        private void init() {
            setBorder(new CompoundBorder(
                    new LineBorder(ThemeData.getForegroundColor()),
                    new EmptyBorder(16, 16, 16, 16)
            ));

            addMultiLineTextLabels(this, DESCRIPTION, ThemeData.getForegroundColor());

            final VerticalLayout example = new VerticalLayout();
            example.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
            example.setBorder(new EmptyBorder(8, 12, 12, 12));
            example.setBackground(ThemeData.getHoverColor());
            addMultiLineTextLabels(example, EXAMPLE, ThemeData.getTextColor());
            add(example, topMargin(8));

            final TextLabel label = new TextLabel();
            label.setFont(Resources.getFont());
            label.setForeground(ThemeData.getForegroundColor());
            label.setText(EXAMPLE_NOTE.trim());
            add(label, topMargin(12));

            addMultiLineTextLabels(this, SOURCE_NOTE, ThemeData.getForegroundColor());

            add(initFooter(), topMargin(8));
        }

        private Component initFooter() {
            final HorizontalLayout footer = new HorizontalLayout();
            footer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

            final TextButton okayButton = new TextButton();

            okayButton.setText("OK");
            okayButton.setPadding(6, 5, 6, 5);
            okayButton.setMinimumSize(new Dimension(52, 24));
            okayButton.setOnClickListener(dialog::dispatchClose);

            footer.add(okayButton, alignRight());

            return footer;
        }

        private static void addMultiLineTextLabels(Container container, String[] text, Color textColor) {
            for (String paragraph : text) {
                final String trimmed = paragraph.trim();
                final TextLabel label = new TextLabel();
                label.setFont(Resources.getFont());
                label.setForeground(textColor);
                label.setText(trimmed);

                if (trimmed.startsWith("http")) {
                    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    label.setOnClickListener(() -> {
                        LinksHelper.browse(trimmed);
                    });
                }

                container.add(label, paragraph.startsWith("\n") ? topMargin(12) : topMargin(2));
            }
        }
    }
}
