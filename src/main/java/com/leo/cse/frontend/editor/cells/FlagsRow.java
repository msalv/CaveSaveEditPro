package com.leo.cse.frontend.editor.cells;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.center;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.compound.CheckBox;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.decorations.VerticalSeparatorDecoration;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.StackLayout;
import com.leo.cse.util.StringUtils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ListCellRenderer;

public class FlagsRow extends HorizontalLayout {
    private final VerticalSeparatorDecoration separatorsDecoration = new VerticalSeparatorDecoration();

    private final CheckBox checkBox;
    private final TextLabel id;
    private final TextLabel description;

    private boolean shouldPaintLine = true;
    private boolean isHovered = false;

    public FlagsRow() {
        super();

        final StackLayout checkBoxWrapper = new StackLayout();
        checkBoxWrapper.setPreferredSize(new Dimension(50, 23));

        checkBox = new CheckBox();
        checkBox.setPreferredSize(new Dimension(16, 16));

        checkBoxWrapper.add(checkBox, center());

        id = textLabel(new Dimension(50, 0));
        id.setMaximumSize(new Dimension(50, Integer.MAX_VALUE));

        description = textLabel(new Dimension(0, 0));

        add(checkBoxWrapper);
        add(id);
        add(description);

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

    public void setChecked(boolean isChecked) {
        checkBox.setChecked(isChecked);
    }

    public void setId(String text) {
        id.setText(text);
    }

    public void setDescription(String text) {
        description.setText(text);
    }

    public void shouldPaintLine(boolean paint) {
        shouldPaintLine = paint;
    }

    public void setHovered(boolean isHovered) {
        this.isHovered = isHovered;
    }

    public static abstract class CellRenderer<T> implements ListCellRenderer<T> {
        final FlagsRow holder = new FlagsRow();
        int hoveredIndex = -1;

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

        abstract String getDescriptionPlaceholder();

        final Component getListCellRendererComponent(boolean isChecked, int id, String description, boolean paintLine, boolean isHovered) {
            holder.setChecked(isChecked);
            holder.setId(String.format("%04d", id));
            holder.setDescription(!StringUtils.isNullOrEmpty(description) ? description : getDescriptionPlaceholder());

            holder.shouldPaintLine(paintLine);
            holder.setHovered(isHovered);

            return holder;
        }
    }

}
