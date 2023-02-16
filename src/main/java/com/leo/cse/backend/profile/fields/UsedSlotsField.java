package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.BytesReaderWriter;
import com.leo.cse.backend.profile.ProfilePointers;

public class UsedSlotsField extends ProfileField {
    private final byte[] data;
    private final boolean[] buffer = new boolean[3];

    public UsedSlotsField(byte[] data) {
        this.data = data;
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public boolean acceptsValue(int index, Object value) {
        if (index < 0 || index > 5) {
            return false;
        }
        return value instanceof Boolean;
    }

    private int[] getPointer(int index) {
        int ptr = ProfilePointers.SLOT_QUOTE_PTR;
        if (index > 2) {
            index -= 3;
            ptr = ProfilePointers.SLOT_CURLY_PTR;
        }
        return new int[]{ ptr, index };
    }

    @Override
    public Object getValue(int index) {
        final int[] pointer = getPointer(index);
        final int ptr = pointer[0];
        final int slotId = pointer[1];

        BytesReaderWriter.readFlags(data, ptr, buffer);
        return buffer[slotId];
    }

    @Override
    public void setValue(int index, Object value) {
        final int[] pointer = getPointer(index);
        final int ptr = pointer[0];
        final int slotId = pointer[1];

        BytesReaderWriter.readFlags(data, ptr, buffer);
        buffer[slotId] = (Boolean) value;
        BytesReaderWriter.writeFlags(data, ptr, buffer);
    }
}
