package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;

public class MimCostumeField extends ProfileField {
    private final byte[] data;
    private final int ptr;
    private final boolean[] buffer = new boolean[27];

    public MimCostumeField(byte[] data, int ptr) {
        this.data = data;
        this.ptr = ptr;
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
        BytesReaderWriter.readFlags(data, ptr, buffer);
        long ret = 0;
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i]) {
                ret |= 1L << i;
            }
        }
        return ret;
    }

    @Override
    public void setValue(int index, Object value) {
        final long v = (Long) value;
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = ((v & (1L << i)) != 0);
        }
        BytesReaderWriter.writeFlags(data, ptr, buffer);
    }
}
