package com.leo.cse.frontend.ui.layout;

import com.leo.cse.frontend.ui.layout.constraints.LayoutConstraints;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

public class VerticalFlowLayout extends JContainer {
    private int horizontalGap = 0;
    private int verticalGap = 0;

    public VerticalFlowLayout() {
        super();
    }

    public void setHorizontalGap(int gap) {
        if (this.horizontalGap != gap) {
            this.horizontalGap = gap;
            revalidate();
            repaint();
        }
    }

    public void setVerticalGap(int gap) {
        if (this.verticalGap != gap) {
            this.verticalGap = gap;
            revalidate();
            repaint();
        }
    }

    @Override
    public void onMeasure(Container container, int maxWidth, int maxHeight) {
        final Insets insets = container.getInsets();
        int availWidth = maxWidth - (insets.right + insets.left);
        int availHeight = maxHeight - (insets.bottom + insets.top);

        int measuredWidth = 0;
        int measuredHeight = 0;

        final int childCount = container.getComponentCount();

        int rowWidth = 0;
        int columnHeight = 0;
        int verticalGap = 0;

        for (int i = 0; i < childCount; ++i) {
            final Component child = container.getComponent(i);
            if (!child.isVisible()) {
                continue;
            }

            final LayoutConstraints lc = getChildConstraints(child);
            Dimension size = measureChild(child, availWidth, availHeight);

            if (columnHeight + lc.getVerticalMargins() + verticalGap + size.height > availHeight) {
                columnHeight = 0;
                measuredWidth += rowWidth + horizontalGap;
                availWidth -= rowWidth + horizontalGap;

                rowWidth = size.width + lc.getHorizontalMargins();
                verticalGap = 0;
            }

            columnHeight += size.height + lc.getVerticalMargins() + verticalGap;
            rowWidth = Math.max(rowWidth, size.width + lc.getHorizontalMargins());
            measuredHeight = Math.max(columnHeight, measuredHeight);
            verticalGap = this.verticalGap;
        }

        measuredWidth += rowWidth;

        measuredWidth += insets.right + insets.left;
        measuredHeight += insets.bottom + insets.top;

        setMeasuredDimensions(measuredWidth, measuredHeight);
    }

    @Override
    public void onLayout(Container container) {
        final Insets insets = container.getInsets();
        int left = insets.left;
        int top = insets.top;
        int right = container.getWidth() - insets.right;
        int bottom = container.getHeight() - insets.bottom;

        final int childCount = container.getComponentCount();

        int rowWidth = 0;

        for (int i = 0; i < childCount; ++i) {
            final Component child = container.getComponent(i);
            if (!child.isVisible()) {
                continue;
            }

            final Dimension childSize = getChildDimension(child);
            final LayoutConstraints lc = getChildConstraints(child);

            if (top + lc.topMargin + childSize.height > bottom) {
                top = insets.top;
                left += rowWidth + horizontalGap;
                rowWidth = childSize.width;
            }

            child.setBounds(
                    left + lc.leftMargin,
                    top + lc.topMargin,
                    childSize.width,
                    childSize.height
            );

            top += childSize.height + lc.getVerticalMargins() + verticalGap;
            rowWidth = Math.max(rowWidth, childSize.width);
        }
    }
}
