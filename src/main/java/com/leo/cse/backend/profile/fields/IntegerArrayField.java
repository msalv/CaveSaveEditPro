package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;

public class IntegerArrayField extends ProfileField {
    private final int length;
    private final int ptr;
    private final int off;
    private final byte[] data;
    private final int[] buffer;

    public IntegerArrayField(byte[] data, int length, int ptr, int off) {
        this.length = length;
        this.ptr = ptr;
        this.off = off;
        this.data = data;
        this.buffer = new int[length];
    }

    @Override
    public Class<?> getType() {
        return Integer.class;
    }

    @Override
    public boolean acceptsValue(int index, Object value) {
        return value instanceof Integer;
    }

    @Override
    public Object getValue(int index) {
        BytesReaderWriter.readInts(data, ptr, off, buffer);
        return buffer[index];
    }

    @Override
    public void setValue(int index, Object value) {
        BytesReaderWriter.readInts(data, ptr, off, buffer);
        buffer[index] = (Integer) value;
        BytesReaderWriter.writeInts(data, ptr, off, buffer);
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
