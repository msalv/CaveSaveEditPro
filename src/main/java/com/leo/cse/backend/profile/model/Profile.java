package com.leo.cse.backend.profile.model;

import com.leo.cse.dto.StartPoint;
import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.exceptions.ProfileFieldException;
import com.leo.cse.backend.profile.fields.ProfileField;

import java.util.Map;

abstract public class Profile {
    public static final int NO_INDEX = -1;

    /**
     * The default profile header string.
     */
    public static final String DEFAULT_HEADER = "Do041220";

    /**
     * The default flag section header string.
     */
    public static final String DEFAULT_FLAGH = "FLAG";

    /**
     * Raw profile's data
     */
    protected final byte[] data;

    public Profile(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    protected abstract Map<String, ProfileField> getFields();

    // --- FIELDS --- //

    /**
     * Registers a new field.
     *
     * @param fieldName name to register field as
     * @param field the field itself
     * @throws ProfileFieldException if the field is detected to be invalid or duplicate.
     */
    protected void addField(String fieldName, ProfileField field) throws ProfileFieldException {
        if (fieldName == null) {
            throw new ProfileFieldException("fieldName == null!");
        }
        if (fieldName.isEmpty()) {
            throw new ProfileFieldException("fieldName cannot be empty!");
        }

        final Map<String, ProfileField> fields = getFields();

        if (fields.containsKey(fieldName)) {
            throw new ProfileFieldException(String.format("Field %s is already defined!", fieldName));
        }
        if (field == null) {
            throw new ProfileFieldException("field == null!");
        }
        if (field.getType() == null) {
            throw new ProfileFieldException("field.getType() == null!");
        }
        fields.put(fieldName, field);
    }

    /**
     * Checks if a field exists.
     *
     * @param fieldName field to check
     * @return <code>true</code> if it exists, <code>false</code> otherwise
     */
    private boolean hasField(Map<String, ProfileField> fields, String fieldName) {
        return fields.containsKey(fieldName);
    }

    /**
     * Like {@link #hasField(Map, String)}, but throws an exception if the field doesn't
     * exist.
     *
     * @param fieldName field to check
     * @throws ProfileFieldException if the field doesn't exist.
     */
    private void requireField(Map<String, ProfileField> fields, String fieldName) throws ProfileFieldException {
        if (!hasField(fields, fieldName)) {
            throw new ProfileFieldException(String.format("Field %s is not defined!", fieldName));
        }
    }

    /**
     * Gets a field's value.
     *
     * @param field field to get
     * @param index index to get. will be ignored if the field doesn't have indexes
     * @return value of the field
     * @throws ProfileFieldException if a field-related exception occurs.
     */
    public Object getField(String field, int index) throws ProfileFieldException {
        return getField(getFields(), field, index);
    }

    protected Object getField(Map<String, ProfileField> fields, String field, int index) throws ProfileFieldException {
        requireField(fields, field);
        ProfileField fieldObj = fields.get(field);
        if (fieldObj.hasIndexes()) {
            if (!fieldObj.isValidIndex(index)) {
                throw new ProfileFieldException(String.format("Index %d is invalid for field!", index));
            }
        }
        return fieldObj.getValue(index);
    }

    /**
     * Sets a field's value.
     *
     * @param field field to set
     * @param index index to set. Will be ignored if the field doesn't have indexes
     * @param value value to set
     * @throws ProfileFieldException if a field-related exception occurs.
     */
    public void setField(String field, int index, Object value) throws ProfileFieldException {
        setField(getFields(), field, index, value);
    }

    protected void setField(Map<String, ProfileField> fields, String field, int index, Object value) throws ProfileFieldException {
        requireField(fields, field);
        ProfileField fieldObj = fields.get(field);
        if (fieldObj.hasIndexes()) {
            if (index < fieldObj.getMinimumIndex() || index > fieldObj.getMaximumIndex()) {
                throw new ProfileFieldException(String.format("Index %d is out of bounds for field %s!", index, field));
            }
        }
        fieldObj.setValue(index, value);
    }

    public void reset(StartPoint sp) throws ProfileFieldException {
        setField(ProfileFields.FIELD_MAP, Profile.NO_INDEX, sp.map);
        setField(ProfileFields.FIELD_SONG, Profile.NO_INDEX, 8);
        setField(ProfileFields.FIELD_X_POSITION, Profile.NO_INDEX, (short) (sp.positionX * 32));
        setField(ProfileFields.FIELD_Y_POSITION, Profile.NO_INDEX, (short) (sp.positionY * 32));
        setField(ProfileFields.FIELD_DIRECTION, Profile.NO_INDEX, sp.direction);
        setField(ProfileFields.FIELD_MAXIMUM_HEALTH, Profile.NO_INDEX, sp.maxHealth);
        setField(ProfileFields.FIELD_CURRENT_HEALTH, Profile.NO_INDEX, sp.curHealth);
    }
}
