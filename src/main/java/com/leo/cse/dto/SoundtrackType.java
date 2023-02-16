package com.leo.cse.dto;

public enum SoundtrackType {
    REMASTERED((byte)1),
    ORIGINAL((byte)2),
    NEW((byte)3);

    public final byte value;

    SoundtrackType(byte value) {
        this.value = value;
    }

    public static SoundtrackType valueOf(byte val) {
        final int value = val % 4;
        if (value < 2) {
            return REMASTERED;
        } else if (value < 3) {
            return ORIGINAL;
        } else {
            return NEW;
        }
    }
}
