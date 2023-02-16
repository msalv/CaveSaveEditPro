package com.leo.cse.frontend.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

public class HorizontalSpreadLayout extends JContainer {
    private int gap = 0;

    public HorizontalSpreadLayout() {
        super();
    }

    public void setGap(int gap) {
        if (this.gap != gap) {
            this.gap = gap;
            revalidate();
            repaint();
        }
    }

    @Override
    public void onMeasure(Container container, int maxWidth, int maxHeight) {
        final Insets insets = container.getInsets();
        int availWidth = maxWidth - (insets.right + insets.left);
        int availHeight = maxHeight - (insets.bottom + insets.top);

        int measuredHeight = 0;

        final int childCount = container.getComponentCount();

        int visibleChildren = 0;

        for (int i = 0; i < childCount; ++i) {
            final Component child = container.getComponent(i);
            if (child.isVisible()) {
                visibleChildren++;
            }
        }

        if (visibleChildren == 0) {
            setMeasuredDimensions(maxWidth, 0);
            return;
        }

        final int gapsWidth = gap * (visibleChildren - 1);
        final int childWidth = Math.round((availWidth - gapsWidth) / (float)visibleChildren);

        for (int i = 0; i < childCount; ++i) {
            final Component child = container.getComponent(i);
            if (!child.isVisible()) {
                continue;
            }
            final Dimension size = measureChild(child, Math.min(childWidth, availWidth), availHeight);
            measuredHeight = Math.max(measuredHeight, size.height);
            availWidth -= childWidth + gap;
        }

        setMeasuredDimensions(maxWidth, measuredHeight);
    }

    @Override
    public void onLayout(Container container) {
        final Insets insets = container.getInsets();
        int left = insets.left;
        int top = insets.top;

        final int childCount = container.getComponentCount();

        for (int i = 0; i < childCount; ++i) {
            final Component child = container.getComponent(i);
            if (!child.isVisible()) {
                continue;
            }

            final Dimension childSize = getChildDimension(child);
            final int gap = (i == 0) ? 0 : this.gap;

            child.setBounds(
                    left + gap,
                    top,
                    childSize.width,
                    childSize.height
            );

            left += childSize.width + gap;
        }
    }
}
