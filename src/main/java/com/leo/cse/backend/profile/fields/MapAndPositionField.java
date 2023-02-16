package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;

public class MapAndPositionField extends ProfileField {
    private final int mapPtr;
    private final int xPtr;
    private final int yPtr;
    private final byte[] data;

    public MapAndPositionField(byte[] data, int mapPtr, int xPtr, int yPtr) {
        this.mapPtr = mapPtr;
        this.xPtr = xPtr;
        this.yPtr = yPtr;
        this.data = data;
    }

    @Override
    public Class<?> getType() {
        return Integer[].class;
    }

    @Override
    public boolean acceptsValue(int index, Object value) {
        if (!(value instanceof Integer[])) {
            return false;
        }
        return ((Integer[]) value).length == 3;
    }

    @Override
    public Object getValue(int index) {
        final Integer[] result = new Integer[3];
        result[0] = BytesReaderWriter.readInt(data, mapPtr);
        result[1] = (int)BytesReaderWriter.readShort(data, xPtr);
        result[2] = (int)BytesReaderWriter.readShort(data, yPtr);
        return result;
    }

    private short int2Short(int i) {
        return (short) Math.min(Math.max(i, Short.MIN_VALUE), Short.MAX_VALUE);
    }

    @Override
    public void setValue(int index, Object value) {
        final Integer[] values = (Integer[]) value;
        BytesReaderWriter.writeInt(data, mapPtr, values[0]);
        BytesReaderWriter.writeShort(data, xPtr, int2Short(values[1]));
        BytesReaderWriter.writeShort(data, yPtr, int2Short(values[2]));
    }
}
