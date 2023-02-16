package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;

public class BooleanArrayField extends ProfileField {
    private final byte[] data;
    private final int ptr;
    private final int offset;
    private final int length;
    private final byte[] buffer;

    public BooleanArrayField(byte[] data, int length, int ptr, int offset) {
        this.data = data;
        this.ptr = ptr;
        this.offset = offset;
        this.length = length;
        this.buffer = new byte[length];
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
        BytesReaderWriter.readBytes(data, ptr, offset, buffer);
        final byte flag = buffer[index];
        return flag != 0;
    }

    @Override
    public void setValue(int index, Object value) {
        final byte val = (byte)((Boolean) value ? 1 : 0);
        BytesReaderWriter.readBytes(data, ptr, offset, buffer);
        buffer[index] = val;
        BytesReaderWriter.writeBytes(data, ptr, offset, buffer);
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
