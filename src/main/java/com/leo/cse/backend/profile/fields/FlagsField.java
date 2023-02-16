package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;

public class FlagsField extends ProfileField {
    private final int length;
    private final int ptr;
    private final byte[] data;
    private final boolean[] buffer;

    public FlagsField(byte[] data, int length, int ptr) {
        this.data = data;
        this.length = length;
        this.ptr = ptr;
        this.buffer = new boolean[length];
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public boolean acceptsValue(int index, Object value) {
        return value instanceof Boolean;
    }

    @Override
    public Object getValue(int index) {
        BytesReaderWriter.readFlags(data, ptr, buffer);
        return buffer[index];
    }

    @Override
    public void setValue(int index, Object value) {
        BytesReaderWriter.readFlags(data, ptr, buffer);
        buffer[index] = (Boolean) value;
        BytesReaderWriter.writeFlags(data, ptr, buffer);
    }

    @Override
    public boolean hasIndexes() {
        return true;
    }

    @Override
    public int getMinimumIndex() {
        return 0;
    }

    @Override
    public int getMaximumIndex() {
        return length - 1;
    }
}
