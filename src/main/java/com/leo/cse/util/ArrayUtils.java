package com.leo.cse.util;

public class ArrayUtils {
    public static <T> T getOrDefault(T[] array, int index, T def) {
        if (array == null || array.length == 0) {
            return def;
        }
        if (index >= 0 && index < array.length) {
            return array[index];
        }
        return def;
    }
}
