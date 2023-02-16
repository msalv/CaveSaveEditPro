package com.leo.cse.frontend.ui.decorations;

import com.leo.cse.frontend.ui.ThemeData;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;

public class VerticalSeparatorDecoration {
    private boolean paintLast = true;

    public void paint(Graphics g, Container container) {
        final int childCount = container.getComponentCount() - (paintLast ? 0 : 1);
        for (int i = 0; i < childCount; ++i) {
            final Component child = container.getComponent(i);
            if (child.isVisible()) {
                g.setColor(ThemeData.getForegroundColor());
                g.drawLine(
                        child.getX() + child.getWidth() - 1,
                        0,
                        child.getX() + child.getWidth() - 1,
                        container.getHeight() - 1
                );
            }
        }
    }

    public void setPaintLastSeparator(boolean paint) {
        paintLast = paint;
    }
}
