package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;

public class PositionField extends ProfileField {
    private final byte[] data;
    private final int xPtr;
    private final int yPtr;

    public PositionField(byte[] data, int xPtr, int yPtr) {
        this.data = data;
        this.xPtr = xPtr;
        this.yPtr = yPtr;
    }

    @Override
    public Class<?> getType() {
        return Short[].class;
    }

    @Override
    public boolean acceptsValue(int index, Object value) {
        if (!(value instanceof Short[])) {
            return false;
        }
        return ((Short[]) value).length >= 2;
    }

    @Override
    public Object getValue(int index) {
        final Short[] result = new Short[2];
        result[0] = BytesReaderWriter.readShort(data, xPtr);
        result[1] = BytesReaderWriter.readShort(data, yPtr);
        return result;
    }

    @Override
    public void setValue(int index, Object value) {
        final Short[] values = (Short[]) value;
        BytesReaderWriter.writeShort(data, xPtr, values[0]);
        BytesReaderWriter.writeShort(data, yPtr, values[1]);
    }
}
