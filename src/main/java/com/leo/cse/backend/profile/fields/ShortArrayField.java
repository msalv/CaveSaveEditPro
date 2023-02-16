package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;

public class ShortArrayField extends ProfileField {
    private final int length;
    private final int ptr;
    private final int off;
    private final byte[] data;
    private final short[] buffer;

    public ShortArrayField(byte[] data, int length, int ptr, int off) {
        this.data = data;
        this.length = length;
        this.ptr = ptr;
        this.off = off;
        this.buffer = new short[length];
    }

    @Override
    public Class<?> getType() {
        return Short.class;
    }

    @Override
    public boolean acceptsValue(int index, Object value) {
        return value instanceof Short;
    }

    @Override
    public Object getValue(int index) {
        BytesReaderWriter.readShorts(data, ptr, off, buffer);
        return buffer[index];
    }

    @Override
    public void setValue(int index, Object value) {
        BytesReaderWriter.readShorts(data, ptr, off, buffer);
        buffer[index] = (Short) value;
        BytesReaderWriter.writeShorts(data, ptr, off, buffer);
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
