package com.leo.cse.dto;

public enum Difficulty {
    ORIGINAL((short)0),
    EASY((short)2),
    HARD((short)4);

    public final short value;

    Difficulty(short value) {
        this.value = value;
    }

    public static Difficulty valueOf(short val) {
        final int value = val % 6;
        if (value < 2) {
            return ORIGINAL;
        } else if (value < 4) {
            return EASY;
        } else {
            return HARD;
        }
    }
}
