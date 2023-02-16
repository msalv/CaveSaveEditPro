package com.leo.cse.frontend.ui.layout.constraints;

import com.leo.cse.frontend.ui.Gravity;

public class LayoutConstraints {
    public int leftMargin;
    public int topMargin;
    public int rightMargin;
    public int bottomMargin;

    public int gravity = Gravity.LEFT | Gravity.TOP;

    public LayoutConstraints() {
    }

    public LayoutConstraints(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
    }

    public int getHorizontalMargins() {
        return leftMargin + rightMargin;
    }

    public int getVerticalMargins() {
        return topMargin + bottomMargin;
    }
}
