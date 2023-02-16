package com.leo.cse.frontend.ui;

public class Gravity {
    public static final int TOP = 1;
    public static final int BOTTOM = 1 << 1;
    public static final int LEFT = 1 << 2;
    public static final int RIGHT = 1 << 3;

    public static final int CENTER_VERTICAL = 1 << 4;
    public static final int CENTER_HORIZONTAL = 1 << 5;

    public static boolean isSet(int gravity, int mask) {
        return (gravity & mask) == mask;
    }
}
