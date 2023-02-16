package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;

public class ByteArrayField extends ProfileField {
    private final int length;
    private final int ptr;
    private final int off;
    private final byte[] data;
    private final byte[] buffer;

    public ByteArrayField(byte[] data, int length, int ptr, int off) {
        this.data = data;
        this.length = length;
        this.ptr = ptr;
        this.off = off;
        this.buffer = new byte[length];
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
        BytesReaderWriter.readBytes(data, ptr, off, buffer);
        return buffer[index];
    }

    @Override
    public void setValue(int index, Object value) {
        BytesReaderWriter.readBytes(data, ptr, off, buffer);
        buffer[index] = (Byte) value;
        BytesReaderWriter.writeBytes(data, ptr, off, buffer);
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
