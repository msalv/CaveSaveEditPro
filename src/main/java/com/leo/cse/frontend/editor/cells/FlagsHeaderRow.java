package com.leo.cse.frontend.editor.cells;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.decorations.VerticalSeparatorDecoration;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.border.EmptyBorder;

public class FlagsHeaderRow extends HorizontalLayout {
    private final VerticalSeparatorDecoration separatorsDecoration = new VerticalSeparatorDecoration();

    public FlagsHeaderRow() {
        super();

        add(textLabel("State", new Dimension(50, Integer.MAX_VALUE)));
        add(textLabel("ID", new Dimension(50, Integer.MAX_VALUE)));
        add(textLabel("Description", new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)));

        setBorder(new EmptyBorder(0, 1, 0, 1));

        separatorsDecoration.setPaintLastSeparator(false);
    }

    private TextLabel textLabel(String text, Dimension size) {
        final TextLabel textLabel = new TextLabel();
        textLabel.setSingleLine(true);
        textLabel.setPadding(11, 5, 12, 5);
        textLabel.setFont(Resources.getFont());
        textLabel.setTextColor(ThemeData.getForegroundColor());

        textLabel.setText(text);

        textLabel.setMinimumSize(size);
        textLabel.setMaximumSize(size);

        return textLabel;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        separatorsDecoration.paint(g, this);
    }
}
