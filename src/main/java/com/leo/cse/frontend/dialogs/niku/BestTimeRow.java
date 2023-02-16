package com.leo.cse.frontend.dialogs.niku;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.decorations.VerticalSeparatorDecoration;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class BestTimeRow extends HorizontalLayout {
    private final VerticalSeparatorDecoration separatorsDecoration = new VerticalSeparatorDecoration();

    private final TextLabel time;
    private final TextLabel characterName;
    private final TextLabel songName;

    private boolean shouldPaintLine = true;

    public BestTimeRow() {
        super();

        time = textLabel(new Dimension(160, 0));
        time.setGravity(Gravity.RIGHT);

        characterName = textLabel(new Dimension(160, 0));
        characterName.setMaximumSize(new Dimension(160, Integer.MAX_VALUE));

        songName = textLabel(new Dimension(160, 0));
        songName.setMaximumSize(new Dimension(160, Integer.MAX_VALUE));

        add(time);
        add(characterName);
        add(songName);

        separatorsDecoration.setPaintLastSeparator(false);

        setBackground(ThemeData.getBackgroundColor());
    }

    private TextLabel textLabel(Dimension minSize) {
        final TextLabel textLabel = new TextLabel();
        textLabel.setSingleLine(true);
        textLabel.setPadding(11, 5, 12, 5);
        textLabel.setFont(Resources.getFont());
        textLabel.setTextColor(ThemeData.getForegroundColor());
        textLabel.setMinimumSize(minSize);
        return textLabel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        separatorsDecoration.paint(g, this);

        if (shouldPaintLine) {
            g.setColor(ThemeData.getForegroundColor());
            g.drawLine(
                    0,
                    getHeight() - 1,
                    getWidth() - 1,
                    getHeight() - 1
            );
        }
    }

    public void setCharacterName(String text) {
        characterName.setText(text);
    }

    public void setSongName(String text) {
        songName.setText(text);
    }

    public void setTime(String text) {
        time.setText(text);
    }

    public void shouldPaintLine(boolean paint) {
        shouldPaintLine = paint;
    }

    public static class CellRenderer implements ListCellRenderer<String[]> {
        private static final int TIME_INDEX = 0;
        private static final int CHARACTER_INDEX = 1;
        private static final int SONG_INDEX = 2;

        private final BestTimeRow holder = new BestTimeRow();

        @Override
        public Component getListCellRendererComponent(JList<? extends String[]> list, String[] value, int index, boolean isSelected, boolean cellHasFocus) {
            holder.setTime(value[TIME_INDEX]);
            holder.setCharacterName(value[CHARACTER_INDEX]);
            holder.setSongName(value[SONG_INDEX]);

            holder.shouldPaintLine(index != list.getModel().getSize() - 1);

            return holder;
        }
    }
}
