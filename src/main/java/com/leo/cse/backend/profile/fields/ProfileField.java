package com.leo.cse.backend.profile.fields;

import com.leo.cse.backend.profile.model.Profile;

/**
 * Represents a field in the profile.
 *
 * @author Leo
 */
public abstract class ProfileField {

    /**
     * Gets the type of the field.<br />
     * <i>This cannot return <code>null</code>,</i> obviously. That'd be like having
     * a <code>void</code> Java field.
     *
     * @return field type
     */
    public abstract Class<?> getType();

    /**
     * Checks if the field accepts this value.
     *
     * @param index index to check against (or -1 if {@linkplain #hasIndexes()
     *              the field is indexless})
     * @param value value to check
     * @return <code>true</code> if value is acceptable, <code>false</code>
     * otherwise
     */
    public abstract boolean acceptsValue(int index, Object value);

    /**
     * Gets the field's value.
     *
     * @param index index to get value from (or -1 if {@linkplain #hasIndexes() the
     *              field is indexless})
     * @return value of field
     */
    public abstract Object getValue(int index);

    /**
     * Sets the field's value.
     *
     * @param index index to set (or -1 if {@linkplain #hasIndexes() the field is
     *              indexless})
     * @param value value to set to
     */
    public abstract void setValue(int index, Object value);

    /**
     * Checks if the field is indexed.
     *
     * @return <code>true</code> if has indexes, <code>false</code> otherwise.
     */
    public boolean hasIndexes() {
        return false;
    }

    /**
     * Gets the minimum index of the field.
     *
     * @return minimum index of the field (or -1 if {@linkplain #hasIndexes() the
     * field is indexless})
     */
    public int getMinimumIndex() {
        return Profile.NO_INDEX;
    }

    /**
     * Gets the maximum index of the field
     *
     * @return maximum index of the field (or -1 if {@linkplain #hasIndexes() the
     * field is indexless})
     */
    public int getMaximumIndex() {
        return Profile.NO_INDEX;
    }

    /**
     * Checks if an index is valid.
     *
     * @param index index to check
     * @return <code>true</code> if index is valid, <code>false</code> otherwise
     */
    public boolean isValidIndex(int index) {
        return index >= getMinimumIndex() && index <= getMaximumIndex();
    }
}
