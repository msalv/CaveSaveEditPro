package com.leo.cse.backend.profile.fields;

public class BooleanField extends ProfileField {
    private final byte[] data;
    private final int ptr;

    public BooleanField(byte[] data, int ptr) {
        this.data = data;
        this.ptr = ptr;
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
        final byte flag = data[ptr];
        return flag != 0;
    }

    @Override
    public void setValue(int index, Object value) {
        final byte flag = (byte) ((Boolean) value ? 1 : 0);
        data[ptr] = flag;
    }
}
