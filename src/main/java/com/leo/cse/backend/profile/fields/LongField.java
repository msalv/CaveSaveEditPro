package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;

public class LongField extends ProfileField {
    private final int ptr;
    private final byte[] data;

    public LongField(byte[] data, int ptr) {
        this.ptr = ptr;
        this.data = data;
    }

    @Override
    public Class<?> getType() {
        return Long.class;
    }

    @Override
    public boolean acceptsValue(int index, Object value) {
        return value instanceof Long;
    }

    @Override
    public Object getValue(int index) {
        return BytesReaderWriter.readLong(data, ptr);
    }

    @Override
    public void setValue(int index, Object value) {
        BytesReaderWriter.writeLong(data, ptr, (Long) value);
    }
}
