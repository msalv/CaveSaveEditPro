package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;

public class ShortField extends ProfileField {
    private final int ptr;
    private final byte[] data;

    public ShortField(byte[] data, int ptr) {
        this.ptr = ptr;
        this.data = data;
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
        return BytesReaderWriter.readShort(data, ptr);
    }

    @Override
    public void setValue(int index, Object value) {
        BytesReaderWriter.writeShort(data, ptr, (Short) value);
    }
}
