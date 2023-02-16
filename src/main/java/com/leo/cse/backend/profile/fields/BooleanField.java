package com.leo.cse.backend.profile.fields;

public class BooleanField extends ProfileField {
    private enum DataType {
        UINT,
        BOOLEAN
    }

    private final byte[] data;
    private final int ptr;
    private final DataType type;

    private BooleanField(byte[] data, int ptr, DataType type) {
        this.data = data;
        this.ptr = ptr;
        this.type = type;
    }

    public BooleanField(byte[] data, int ptr) {
        this(data, ptr, DataType.BOOLEAN);
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
        if (type == DataType.UINT) {
            return Byte.toUnsignedInt(flag) > 0x7F;
        }
        return flag != 0;
    }

    @Override
    public void setValue(int index, Object value) {
        final byte flag;
        if (type == DataType.UINT) {
            flag = (byte) ((Boolean) value ? 0xFF : 0x7F);
        } else {
            flag = (byte) ((Boolean) value ? 1 : 0);
        }
        data[ptr] = flag;
    }

    public static BooleanField uint(byte[] data, int ptr) {
        return new BooleanField(data, ptr, DataType.UINT);
    }
}
