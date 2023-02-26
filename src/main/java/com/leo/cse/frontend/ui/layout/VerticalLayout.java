package com.leo.cse.frontend.ui.layout;

import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.layout.constraints.LayoutConstraints;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

public class VerticalLayout extends JContainer {

    public VerticalLayout() {
        super();
    }

    @Override
    public void onMeasure(Container container, int maxWidth, int maxHeight) {
        final Insets insets = container.getInsets();
        int availWidth = maxWidth - (insets.right + insets.left);
        int availHeight = maxHeight - (insets.bottom + insets.top);

        int measuredWidth = 0;
        int measuredHeight = 0;

        final int childCount = container.getComponentCount();

        for (int i = 0; i < childCount; ++i) {
            final Component child = container.getComponent(i);
            if (!child.isVisible()) {
                continue;
            }
            final LayoutConstraints lc = getChildConstraints(child);
            availHeight -= lc.getVerticalMargins();
            final Dimension size = measureChild(child, availWidth - lc.getHorizontalMargins(), availHeight);
            availHeight -= size.height;
            measuredWidth = Math.max(measuredWidth, size.width + lc.getHorizontalMargins());
            measuredHeight += size.height + lc.getVerticalMargins();
        }

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

        for (int i = 0; i < childCount; ++i) {
            final Component child = container.getComponent(i);
            if (!child.isVisible()) {
                continue;
            }

            final Dimension childSize = getChildDimension(child);
            final LayoutConstraints lc = getChildConstraints(child);

            final int y = Gravity.isSet(lc.gravity, Gravity.BOTTOM)
                    ? bottom - childSize.height - lc.bottomMargin
                    : top + lc.topMargin;

            if (Gravity.isSet(lc.gravity, Gravity.CENTER_HORIZONTAL)) {
                child.setBounds(
                        left + (right - left - childSize.width) / 2 + lc.leftMargin,
                        y,
                        childSize.width,
                        childSize.height
                );
            } else if (Gravity.isSet(lc.gravity, Gravity.LEFT)) {
                child.setBounds(
                        left + lc.leftMargin,
                        y,
                        childSize.width,
                        childSize.height
                );
            } else {
                child.setBounds(
                        right - lc.rightMargin - childSize.width,
                        y,
                        childSize.width,
                        childSize.height
                );
            }

            if (Gravity.isSet(lc.gravity, Gravity.BOTTOM)) {
                bottom -= childSize.height + lc.getVerticalMargins();
            } else {
                top += childSize.height + lc.getVerticalMargins();
            }
        }
    }
}
