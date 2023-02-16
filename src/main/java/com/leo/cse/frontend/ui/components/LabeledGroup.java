package com.leo.cse.frontend.ui.components;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.frontend.ui.layout.constraints.LayoutConstraints;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.EmptyBorder;

public class LabeledGroup extends VerticalLayout {
    private final static int CONTENT_MARGIN_TOP = 6;

    private final TextLabel label = new TextLabel();
    private Component content;

    private int topContentMargin = CONTENT_MARGIN_TOP;

    public LabeledGroup() {
        super();

        setBorder(new EmptyBorder(0, 10, 12, 10));

        label.setFont(Resources.getFont());
        label.setForeground(ThemeData.getForegroundColor());
        label.setBackground(ThemeData.getBackgroundColor());
        label.setBorder(new EmptyBorder(0, 2, 0, 2));
        label.setSingleLine(true);

        addLabel();
    }

    private void addLabel() {
        final LayoutConstraints lc = new LayoutConstraints();
        lc.leftMargin = 4;
        lc.rightMargin = 4;
        add(label, lc);
    }

    public void setLabelText(String text) {
        label.setText(text);
    }

    public void setContent(Component component) {
        removeAll();

        addLabel();

        final LayoutConstraints lc = new LayoutConstraints();
        lc.topMargin = topContentMargin;
        add(component, lc);

        content = component;
    }

    public void setTopContentMargin(int margin) {
        if (this.topContentMargin != margin) {
            this.topContentMargin = margin;
            if (content != null) {
                setContent(content);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        final int borderOffsetY = label.getY() + label.getHeight() / 2;

        g.setColor(ThemeData.getForegroundColor());

        g.drawRect(
                0,
                borderOffsetY,
                getWidth() - 1,
                getHeight() - borderOffsetY - 1);

        super.paint(g);
    }
}
