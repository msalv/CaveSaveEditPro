package com.leo.cse.backend.profile.model;

import com.leo.cse.backend.BytesReaderWriter;
import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfilePointers;
import com.leo.cse.backend.profile.exceptions.ProfileFieldException;
import com.leo.cse.backend.profile.fields.BooleanArrayField;
import com.leo.cse.backend.profile.fields.ByteArrayField;
import com.leo.cse.backend.profile.fields.FlagsField;
import com.leo.cse.backend.profile.fields.IntegerArrayField;
import com.leo.cse.backend.profile.fields.IntegerField;
import com.leo.cse.backend.profile.fields.LongField;
import com.leo.cse.backend.profile.fields.MapAndPositionField;
import com.leo.cse.backend.profile.fields.MimCostumeField;
import com.leo.cse.backend.profile.fields.PositionField;
import com.leo.cse.backend.profile.fields.ProfileField;
import com.leo.cse.backend.profile.fields.ShortArrayField;
import com.leo.cse.backend.profile.fields.ShortField;

import java.util.HashMap;
import java.util.Map;

public class NormalProfile extends Profile {
    /**
     * The expected file length.
     */
    public static final int FILE_LENGTH = 0x604;

    /**
     * Map of registered fields, with the keys being the field names.
     */
    private final Map<String, ProfileField> fields = new HashMap<>();

    public NormalProfile(byte[] data) throws ProfileFieldException {
        super(data);
        registerFields();
        registerModFields();
    }

    public NormalProfile() throws ProfileFieldException {
        this(new byte[FILE_LENGTH]);
        BytesReaderWriter.writeString(data, ProfilePointers.HEADER_PTR, Profile.DEFAULT_HEADER);
        BytesReaderWriter.writeString(data, ProfilePointers.FLAGS_HEADER_PTR, Profile.DEFAULT_FLAGH);
    }

    @Override
    public Map<String, ProfileField> getFields() {
        return fields;
    }

    /**
     * Register default fields
     * @throws ProfileFieldException if something went wrong during the operation
     */
    private void registerFields() throws ProfileFieldException {
        addField(ProfileFields.FIELD_MAP_AND_POSITION, new MapAndPositionField(data, ProfilePointers.MAP_PTR, ProfilePointers.MAP_X_PTR, ProfilePointers.MAP_Y_PTR));
        addField(ProfileFields.FIELD_MAP, new IntegerField(data, ProfilePointers.MAP_PTR));
        addField(ProfileFields.FIELD_SONG, new IntegerField(data, ProfilePointers.SONG_PTR));
        addField(ProfileFields.FIELD_X_POSITION, new ShortField(data, ProfilePointers.MAP_X_PTR));
        addField(ProfileFields.FIELD_Y_POSITION, new ShortField(data, ProfilePointers.MAP_Y_PTR));
        addField(ProfileFields.FIELD_POSITION, new PositionField(data, ProfilePointers.MAP_X_PTR, ProfilePointers.MAP_Y_PTR));
        addField(ProfileFields.FIELD_DIRECTION, new IntegerField(data, ProfilePointers.DIRECTION_PTR));
        addField(ProfileFields.FIELD_MAXIMUM_HEALTH, new ShortField(data, ProfilePointers.MAX_HEALTH_PTR));
        addField(ProfileFields.FIELD_STAR_COUNT, new ShortField(data, ProfilePointers.STAR_COUNT_PTR));
        addField(ProfileFields.FIELD_CURRENT_HEALTH, new ShortField(data, ProfilePointers.CURRENT_HEALTH_PTR));
        addField(ProfileFields.FIELD_CURRENT_WEAPON, new IntegerField(data, ProfilePointers.CURRENT_WEAPON_PTR));
        addField(ProfileFields.FIELD_EQUIPS, new FlagsField(data, ProfilePointers.EQUIPS_LENGTH, ProfilePointers.EQUIPS_PTR));
        addField(ProfileFields.FIELD_TIME_PLAYED, new IntegerField(data, ProfilePointers.TIME_PLAYED_PTR));
        addField(ProfileFields.FIELD_WEAPON_IDS, new IntegerArrayField(data, ProfilePointers.WEAPON_IDS_LENGTH, ProfilePointers.WEAPON_IDS_PTR, Integer.BYTES * 4));
        addField(ProfileFields.FIELD_WEAPON_LEVELS, new IntegerArrayField(data, ProfilePointers.WEAPON_LEVELS_LENGTH, ProfilePointers.WEAPON_LEVELS_PTR, Integer.BYTES * 4));
        addField(ProfileFields.FIELD_WEAPON_EXP, new IntegerArrayField(data, ProfilePointers.WEAPON_EXP_LENGTH, ProfilePointers.WEAPON_EXP_PTR, Integer.BYTES * 4));
        addField(ProfileFields.FIELD_WEAPON_MAXIMUM_AMMO, new IntegerArrayField(data, ProfilePointers.WEAPON_MAXIMUM_AMMO_LENGTH, ProfilePointers.WEAPON_MAXIMUM_AMMO_PTR, Integer.BYTES * 4));
        addField(ProfileFields.FIELD_WEAPON_CURRENT_AMMO, new IntegerArrayField(data, ProfilePointers.WEAPON_CURRENT_AMMO_LENGTH, ProfilePointers.WEAPON_CURRENT_AMMO_PTR, Integer.BYTES * 4));
        addField(ProfileFields.FIELD_ITEMS, new IntegerArrayField(data, ProfilePointers.ITEMS_LENGTH, ProfilePointers.ITEMS_PTR, 0));
        addField(ProfileFields.FIELD_WARP_IDS, new IntegerArrayField(data, ProfilePointers.WARP_IDS_LENGTH, ProfilePointers.WARP_IDS_PTR, Integer.BYTES));
        addField(ProfileFields.FIELD_WARP_LOCATIONS, new IntegerArrayField(data, ProfilePointers.WARP_LOCATIONS_LENGTH, ProfilePointers.WARP_LOCATIONS_PTR, Integer.BYTES));
        addField(ProfileFields.FIELD_MAP_FLAGS, new BooleanArrayField(data, ProfilePointers.MAP_FLAGS_LENGTH, ProfilePointers.MAP_FLAGS_PTR, 0));
        addField(ProfileFields.FIELD_FLAGS, new FlagsField(data, ProfilePointers.FLAGS_LENGTH, ProfilePointers.FLAGS_PTR));
    }

    /**
     * Register special (mod) fields
     * @throws ProfileFieldException if something went wrong during the operation
     */
    private void registerModFields() throws ProfileFieldException {
        addField(ProfileFields.FIELD_MIM_COSTUME, new MimCostumeField(data, ProfilePointers.EXT_MIM_COSTUME_PTR));
        addField(ProfileFields.FIELD_VARIABLES, new ShortArrayField(data, ProfilePointers.EXT_VARIABLES_LENGTH, ProfilePointers.EXT_VARIABLES_PTR, 0));
        addField(ProfileFields.FIELD_PHYSICS_VARIABLES, new ShortArrayField(data, ProfilePointers.EXT_PHYSICS_VARS_LENGTH, ProfilePointers.EXT_PHYSICS_VARS_PTR, 0));
        addField(ProfileFields.FIELD_CASH, new LongField(data, ProfilePointers.EXT_CASH_PTR));
        addField(ProfileFields.FIELD_EQP_VARIABLES, new ByteArrayField(data, ProfilePointers.EXT_EQP_EQP_VARS_LENGTH, ProfilePointers.EXT_EQP_VARS_PTR, 0));
        addField(ProfileFields.FIELD_EQP_MODS_TRUE, new FlagsField(data, ProfilePointers.EXT_EQP_MODS_TRUE_LENGTH, ProfilePointers.EXT_EQP_MODS_TRUE_PTR));
        addField(ProfileFields.FIELD_EQP_MODS_FALSE, new FlagsField(data, ProfilePointers.EXT_EQP_MODS_FALSE_LENGTH, ProfilePointers.EXT_EQP_MODS_FALSE_PTR));
    }
}
