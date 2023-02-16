package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.profile.model.Profile;

public class ByteField extends ProfileField {
    private final int ptr;
    private final byte[] data;

    public ByteField(byte[] data, int ptr) {
        this.ptr = ptr;
        this.data = data;
    }

    @Override
    public Class<?> getType() {
        return Byte.class;
    }

    @Override
    public boolean acceptsValue(int index, Object value) {
        return value instanceof Byte;
    }

    @Override
    public Object getValue(int index) {
        return data[ptr];
    }

    @Override
    public void setValue(int index, Object value) {
        data[ptr] = (Byte) value;
    }

    @Override
    public boolean hasIndexes() {
        return false;
    }

    @Override
    public int getMinimumIndex() {
        return Profile.NO_INDEX;
    }

    @Override
    public int getMaximumIndex() {
        return Profile.NO_INDEX;
    }
}
