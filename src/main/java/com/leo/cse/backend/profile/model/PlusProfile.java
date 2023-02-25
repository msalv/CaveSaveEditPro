package com.leo.cse.backend.profile.model;

import com.leo.cse.backend.BytesReaderWriter;
import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfilePointers;
import com.leo.cse.backend.profile.exceptions.ProfileFieldException;
import com.leo.cse.backend.profile.fields.BooleanArrayField;
import com.leo.cse.backend.profile.fields.BooleanField;
import com.leo.cse.backend.profile.fields.ByteField;
import com.leo.cse.backend.profile.fields.FlagsField;
import com.leo.cse.backend.profile.fields.IntegerArrayField;
import com.leo.cse.backend.profile.fields.IntegerField;
import com.leo.cse.backend.profile.fields.LongArrayField;
import com.leo.cse.backend.profile.fields.LongField;
import com.leo.cse.backend.profile.fields.MapAndPositionField;
import com.leo.cse.backend.profile.fields.PositionField;
import com.leo.cse.backend.profile.fields.ProfileField;
import com.leo.cse.backend.profile.fields.ShortField;
import com.leo.cse.backend.profile.fields.UsedSlotsField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PlusProfile extends Profile {
    /**
     * The expected CS+ file section length.
     */
    public static final int SECTION_LENGTH = 0x620;

    /**
     * The expected CS+ file length.
     */
    public static final int FILE_LENGTH = 0x20020;

    /**
     * Number of slots in this Profile.dat
     */
    private static final int SLOTS_NUM = 6;

    /**
     * Map of registered fields, with the keys being the field names.
     */
    private final List<Map<String, ProfileField>> slots = new ArrayList<>();

    private final Map<String, ProfileField> commonFields = new HashMap<>();

    /**
     * Currently active slot.
     */
    private final AtomicInteger currentSlot = new AtomicInteger(-1);

    public PlusProfile(byte[] data) throws ProfileFieldException {
        super(data);
        for (int i = 0; i < SLOTS_NUM; ++i) {
            currentSlot.set(i);
            slots.add(new HashMap<>());
            registerSlotFields();
        }
        currentSlot.set(0);
        setupPlusFields();
    }

    /**
     * Default constructor that is used by MCI via reflection
     */
    public PlusProfile() throws ProfileFieldException {
        this(new byte[FILE_LENGTH]);
        BytesReaderWriter.writeString(data, ProfilePointers.HEADER_PTR, Profile.DEFAULT_HEADER);
        BytesReaderWriter.writeString(data, ProfilePointers.FLAGS_HEADER_PTR, Profile.DEFAULT_FLAGH);
        setField(ProfileFields.FIELD_MUSIC_VOLUME, Profile.NO_INDEX, 8);
        setField(ProfileFields.FIELD_SOUND_VOLUME, Profile.NO_INDEX, 10);
    }

    @Override
    protected Map<String, ProfileField> getFields() {
        return slots.get(currentSlot.get());
    }

    @Override
    public Object getField(String field, int index) throws ProfileFieldException {
        if (commonFields.containsKey(field)) {
            return getField(commonFields, field, index);
        }
        return super.getField(field, index);
    }

    @Override
    public void setField(String field, int index, Object value) throws ProfileFieldException {
        if (commonFields.containsKey(field)) {
            setField(commonFields, field, index, value);
        } else {
            super.setField(field, index, value);
        }
    }

    public int setCurrentSlotId(int slot) {
        return currentSlot.getAndSet(slot);
    }

    public int getCurrentSlotId() {
        return currentSlot.get();
    }

    public boolean isSlotSelected() {
        final int currentSlot = getCurrentSlotId();
        return currentSlot >= 0 && currentSlot < SLOTS_NUM;
    }

    /**
     * Adjusts pointer to the current slot
     * @param ptr a pointer to some data
     * @return new pointer relative to the current slot or ptr as-is if it's beyond any slot
     */
    private int ptr(int ptr) {
        // there are variables beyond the 6 save files, so if the pointer is higher than
        // (SECTION_LENGTH * 6), it should be returned as-is
        if (ptr > SECTION_LENGTH * 6) {
            return ptr;
        }

        // make sure pointer is in correct section
        while (ptr > SECTION_LENGTH) {
            ptr -= SECTION_LENGTH;
        }

        // make sure there is a slot selected
        if (!isSlotSelected()) {
            return ptr;
        }

        return currentSlot.get() * SECTION_LENGTH + ptr;
    }

    /**
     * Register default fields for current slot
     * @throws ProfileFieldException if something went wrong during the operation
     */
    private void registerSlotFields() throws ProfileFieldException {
        addField(ProfileFields.FIELD_MAP_AND_POSITION,
                new MapAndPositionField(data,
                    ptr(ProfilePointers.MAP_PTR),
                    ptr(ProfilePointers.MAP_X_PTR),
                    ptr(ProfilePointers.MAP_Y_PTR))
            );

        addField(ProfileFields.FIELD_MAP, new IntegerField(data, ptr(ProfilePointers.MAP_PTR)));
        addField(ProfileFields.FIELD_SONG, new IntegerField(data, ptr(ProfilePointers.SONG_PTR)));
        addField(ProfileFields.FIELD_X_POSITION, new ShortField(data, ptr(ProfilePointers.MAP_X_PTR)));
        addField(ProfileFields.FIELD_Y_POSITION, new ShortField(data, ptr(ProfilePointers.MAP_Y_PTR)));

        addField(ProfileFields.FIELD_POSITION,
                new PositionField(data,
                    ptr(ProfilePointers.MAP_X_PTR),
                    ptr(ProfilePointers.MAP_Y_PTR))
            );

        addField(ProfileFields.FIELD_DIRECTION, new IntegerField(data, ptr(ProfilePointers.DIRECTION_PTR)));
        addField(ProfileFields.FIELD_MAXIMUM_HEALTH, new ShortField(data, ptr(ProfilePointers.MAX_HEALTH_PTR)));
        addField(ProfileFields.FIELD_STAR_COUNT, new ShortField(data, ptr(ProfilePointers.STAR_COUNT_PTR)));
        addField(ProfileFields.FIELD_CURRENT_HEALTH, new ShortField(data, ptr(ProfilePointers.CURRENT_HEALTH_PTR)));
        addField(ProfileFields.FIELD_CURRENT_WEAPON, new IntegerField(data, ptr(ProfilePointers.CURRENT_WEAPON_PTR)));
        addField(ProfileFields.FIELD_EQUIPS, new FlagsField(data, ProfilePointers.EQUIPS_LENGTH, ptr(ProfilePointers.EQUIPS_PTR)));
        addField(ProfileFields.FIELD_TIME_PLAYED, new IntegerField(data, ptr(ProfilePointers.TIME_PLAYED_PTR)));

        addField(ProfileFields.FIELD_WEAPON_IDS, new IntegerArrayField(data,
                ProfilePointers.WEAPON_IDS_LENGTH,
                ptr(ProfilePointers.WEAPON_IDS_PTR),
                Integer.BYTES * 4));

        addField(ProfileFields.FIELD_WEAPON_LEVELS, new IntegerArrayField(data,
                ProfilePointers.WEAPON_LEVELS_LENGTH,
                ptr(ProfilePointers.WEAPON_LEVELS_PTR),
                Integer.BYTES * 4));

        addField(ProfileFields.FIELD_WEAPON_EXP, new IntegerArrayField(data,
                ProfilePointers.WEAPON_EXP_LENGTH,
                ptr(ProfilePointers.WEAPON_EXP_PTR),
                Integer.BYTES * 4));

        addField(ProfileFields.FIELD_WEAPON_MAXIMUM_AMMO, new IntegerArrayField(data,
                ProfilePointers.WEAPON_MAXIMUM_AMMO_LENGTH,
                ptr(ProfilePointers.WEAPON_MAXIMUM_AMMO_PTR),
                Integer.BYTES * 4));

        addField(ProfileFields.FIELD_WEAPON_CURRENT_AMMO, new IntegerArrayField(data,
                ProfilePointers.WEAPON_CURRENT_AMMO_LENGTH,
                ptr(ProfilePointers.WEAPON_CURRENT_AMMO_PTR),
                Integer.BYTES * 4));

        addField(ProfileFields.FIELD_ITEMS, new IntegerArrayField(data,
                ProfilePointers.ITEMS_LENGTH,
                ptr(ProfilePointers.ITEMS_PTR),
                0));

        addField(ProfileFields.FIELD_WARP_IDS, new IntegerArrayField(data,
                ProfilePointers.WARP_IDS_LENGTH,
                ptr(ProfilePointers.WARP_IDS_PTR),
                Integer.BYTES));

        addField(ProfileFields.FIELD_WARP_LOCATIONS, new IntegerArrayField(data,
                ProfilePointers.WARP_LOCATIONS_LENGTH,
                ptr(ProfilePointers.WARP_LOCATIONS_PTR),
                Integer.BYTES));

        addField(ProfileFields.FIELD_MAP_FLAGS, new BooleanArrayField(data,
                ProfilePointers.MAP_FLAGS_LENGTH,
                ptr(ProfilePointers.MAP_FLAGS_PTR),
                0));

        addField(ProfileFields.FIELD_FLAGS, new FlagsField(data,
                ProfilePointers.FLAGS_LENGTH,
                ptr(ProfilePointers.FLAGS_PTR))
        );

        addField(ProfileFields.FIELD_MODIFY_DATE, new LongField(data, ptr(ProfilePointers.MODIFY_DATE_PTR)));
        addField(ProfileFields.FIELD_DIFFICULTY, new ShortField(data, ptr(ProfilePointers.DIFFICULTY_PTR)));
    }

    /**
     * Registers fields specific to Cave Story+
     */
    protected void setupPlusFields() {
        commonFields.put(ProfileFields.FIELD_USED_SLOTS, new UsedSlotsField(data));
        commonFields.put(ProfileFields.FIELD_MUSIC_VOLUME, new IntegerField(data, ProfilePointers.MUSIC_VOLUME_PTR));
        commonFields.put(ProfileFields.FIELD_SOUND_VOLUME, new IntegerField(data, ProfilePointers.SOUND_VOLUME_PTR));
        commonFields.put(ProfileFields.FIELD_SOUNDTRACK_TYPE, new ByteField(data, ProfilePointers.SOUNDTRACK_TYPE_PTR));
        commonFields.put(ProfileFields.FIELD_GRAPHICS_STYLE, new BooleanField(data, ProfilePointers.GRAPHICS_STYLE_PTR));
        commonFields.put(ProfileFields.FIELD_LANGUAGE, new BooleanField(data, ProfilePointers.LANGUAGE_PTR));
        commonFields.put(ProfileFields.FIELD_BEAT_HELL, new BooleanField(data, ProfilePointers.BEAT_HELL_PTR));
        commonFields.put(ProfileFields.FIELD_BEST_HELL_TIME, new IntegerField(data, ProfilePointers.BEST_HELL_TIME_PTR));
        commonFields.put(ProfileFields.FIELD_BEST_MOD_TIMES, new IntegerArrayField(data, ProfilePointers.BEST_MOD_TIMES_LENGTH, ProfilePointers.BEST_MOD_TIMES_PTR, 0));
    }
}
