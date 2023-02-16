package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;

public class IntegerField extends ProfileField {
    private final int ptr;
    private final byte[] data;

    public IntegerField(byte[] data, int ptr) {
        this.ptr = ptr;
        this.data = data;
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
        return BytesReaderWriter.readInt(data, ptr);
    }

    @Override
    public void setValue(int index, Object value) {
        BytesReaderWriter.writeInt(data, ptr, (Integer) value);
    }
}
