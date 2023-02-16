package com.leo.cse.frontend.editor.cells;

import com.leo.cse.frontend.format.DurationFormatter;
import com.leo.cse.frontend.Resources;
import com.leo.cse.dto.Challenge;
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

public class ChallengesRow extends HorizontalLayout {
    private final VerticalSeparatorDecoration separatorsDecoration = new VerticalSeparatorDecoration();

    private final TextLabel name;
    private final TextLabel bestTime;

    private boolean shouldPaintLine = true;
    private boolean isHovered = false;

    public ChallengesRow() {
        super();

        name = textLabel(new Dimension(168, 0));
        name.setMaximumSize(new Dimension(168, Integer.MAX_VALUE));

        bestTime = textLabel(new Dimension(70, 0));
        bestTime.setGravity(Gravity.RIGHT);

        add(name);
        add(bestTime);

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

        if (isHovered) {
            g.setColor(ThemeData.getHoverColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

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

    public void setChallengeName(String text) {
        name.setText(text);
    }

    public void setBestTime(String text) {
        bestTime.setText(text);
    }

    public void shouldPaintLine(boolean paint) {
        shouldPaintLine = paint;
    }

    public void setHovered(boolean isHovered) {
        this.isHovered = isHovered;
    }

    public static class CellRenderer implements ListCellRenderer<Challenge> {
        private final ChallengesRow holder = new ChallengesRow();
        private final DurationFormatter durationFormatter = new DurationFormatter();
        private int hoveredIndex = -1;

        public boolean setHoveredIndex(int id) {
            if (hoveredIndex != id) {
                hoveredIndex = id;
                return true;
            }
            return false;
        }

        public boolean clearHover() {
            return setHoveredIndex(-1);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Challenge> list, Challenge value, int index, boolean isSelected, boolean cellHasFocus) {
            holder.setChallengeName(value.name);
            holder.setBestTime(value.time != 0L ? durationFormatter.format(value.time) : "");

            holder.shouldPaintLine(index != list.getModel().getSize() - 1);
            holder.setHovered(index == hoveredIndex);

            return holder;
        }
    }
}
