package com.leo.cse.frontend.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

public class GridLayout extends JContainer {
    private int horizontalGap = 0;
    private int verticalGap = 0;
    private int spanCount = 1;

    public GridLayout() {
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

    public void setSpanCount(int spanCount) {
        if (this.spanCount != spanCount) {
            this.spanCount = Math.max(1, spanCount);
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

        final int gapsWidth = horizontalGap * (spanCount - 1);
        final int childWidth = Math.round((availWidth - gapsWidth) / (float)spanCount);

        final int rowsCount = (int) Math.ceil(childCount / (float)spanCount);
        final int gapsHeight = verticalGap * (rowsCount - 1);

        for (int row = 0; row < rowsCount; ++row) {
            int availRowWidth = maxWidth - (insets.right + insets.left);
            int rowHeight = 0;
            for (int column = 0; column < spanCount; ++column) {
                final int i = row * spanCount + column;
                if (i >= childCount) {
                    break;
                }

                final Component child = container.getComponent(i);
                if (!child.isVisible()) {
                    continue;
                }
                final Dimension size = measureChild(child, Math.min(childWidth, availRowWidth), availHeight);
                availRowWidth -= childWidth + horizontalGap;
                rowHeight = Math.max(rowHeight, size.height);
            }
            measuredHeight += rowHeight;
            availHeight -= rowHeight + verticalGap;
        }

        setMeasuredDimensions(maxWidth, measuredHeight + gapsHeight);
    }

    @Override
    public void onLayout(Container container) {
        final Insets insets = container.getInsets();
        int left = insets.left;
        int top = insets.top;

        final int childCount = container.getComponentCount();
        final int rowsCount = (int) Math.ceil(childCount / (float)spanCount);

        for (int row = 0; row < rowsCount; ++row) {
            int rowHeight = 0;
            for (int column = 0; column < spanCount; ++column) {
                final int i = row * spanCount + column;
                if (i >= childCount) {
                    break;
                }

                final Component child = container.getComponent(i);
                if (!child.isVisible()) {
                    continue;
                }

                final Dimension childSize = getChildDimension(child);
                final int gap = (column == 0) ? 0 : this.horizontalGap;

                child.setBounds(
                        left + gap,
                        top,
                        childSize.width,
                        childSize.height
                );

                left += childSize.width + gap;
                rowHeight = Math.max(rowHeight, childSize.height);
            }

            top += rowHeight + this.verticalGap;
            left = insets.left;
        }
    }
}
