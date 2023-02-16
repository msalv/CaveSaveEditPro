package com.leo.cse.util;

public class MathUtils {
    public static int coerceIn(int value, int min, int max) {
        value = Math.max(value, min);
        value = Math.min(value, max);
        return value;
    }
}
