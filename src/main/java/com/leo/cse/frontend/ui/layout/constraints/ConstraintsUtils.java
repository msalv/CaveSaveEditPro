package com.leo.cse.frontend.ui.layout.constraints;

import com.leo.cse.frontend.ui.Gravity;

public class ConstraintsUtils {
    public static LayoutConstraints constraints() {
        return new LayoutConstraints(0, 0, 0, 0);
    }

    public static LayoutConstraints constraints(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        return new LayoutConstraints(leftMargin, topMargin, rightMargin, bottomMargin);
    }

    public static LayoutConstraints leftMargin(int leftMargin) {
        return constraints(leftMargin, 0, 0, 0);
    }

    public static LayoutConstraints rightMargin(int rightMargin) {
        return topRightMargin(0, rightMargin);
    }

    public static LayoutConstraints topMargin(int topMargin) {
        return topRightMargin(topMargin, 0);
    }

    public static LayoutConstraints topRightMargin(int topMargin, int rightMargin) {
        return constraints(0, topMargin, rightMargin, 0);
    }

    public static LayoutConstraints topLeftMargin(int topMargin, int leftMargin) {
        return constraints(leftMargin, topMargin, 0, 0);
    }

    public static LayoutConstraints alignRight() {
        return alignRight(constraints());
    }

    public static LayoutConstraints alignRight(LayoutConstraints constraints) {
        constraints.gravity &= ~(Gravity.LEFT | Gravity.CENTER_HORIZONTAL);
        constraints.gravity |= Gravity.RIGHT;
        return constraints;
    }

    public static LayoutConstraints alignBottom(LayoutConstraints constraints) {
        constraints.gravity &= ~(Gravity.TOP | Gravity.CENTER_VERTICAL);
        constraints.gravity |= Gravity.BOTTOM;
        return constraints;
    }

    public static LayoutConstraints centerHorizontal(LayoutConstraints constraints) {
        constraints.gravity &= ~(Gravity.LEFT | Gravity.RIGHT);
        constraints.gravity |= Gravity.CENTER_HORIZONTAL;
        return constraints;
    }

    public static LayoutConstraints centerHorizontal() {
        return centerHorizontal(constraints());
    }

    public static LayoutConstraints centerVertical(LayoutConstraints constraints) {
        constraints.gravity &= ~(Gravity.TOP | Gravity.BOTTOM);
        constraints.gravity |= Gravity.CENTER_VERTICAL;
        return constraints;
    }

    public static LayoutConstraints center() {
        final LayoutConstraints constraints = constraints();
        centerHorizontal(constraints);
        centerVertical(constraints);
        return constraints;
    }
}
