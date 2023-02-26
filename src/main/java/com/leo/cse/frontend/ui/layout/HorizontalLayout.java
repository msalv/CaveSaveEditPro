package com.leo.cse.frontend.ui.layout;

import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.layout.constraints.LayoutConstraints;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

public class HorizontalLayout extends JContainer {
    public HorizontalLayout() {
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
            final Dimension size = measureChild(child, availWidth - lc.getHorizontalMargins(), availHeight);
            availWidth -= size.width + lc.getHorizontalMargins();
            measuredWidth += size.width + lc.getHorizontalMargins();
            measuredHeight = Math.max(measuredHeight, size.height);
        }

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

            final int l;
            final int t;

            if (Gravity.isSet(lc.gravity, Gravity.LEFT)) {
                l = left + lc.leftMargin;
                left += childSize.width + lc.getHorizontalMargins();
            } else {
                l = right - lc.rightMargin - childSize.width;
                right -= childSize.width + lc.getHorizontalMargins();
            }

            if (Gravity.isSet(lc.gravity, Gravity.CENTER_VERTICAL)) {
                t = (bottom - top - childSize.height) / 2 + lc.topMargin;
            } else if (Gravity.isSet(lc.gravity, Gravity.TOP)) {
                t = top + lc.topMargin;
            } else {
                t = bottom - lc.bottomMargin - childSize.height;
            }

            child.setBounds(l, t, childSize.width, childSize.height);
        }
    }
}
