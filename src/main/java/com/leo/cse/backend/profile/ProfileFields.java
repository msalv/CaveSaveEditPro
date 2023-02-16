package com.leo.cse.backend.profile;

public class ProfileFields {
    private ProfileFields() {
    }

    /**
     * A combination of the {@linkplain #FIELD_MAP map field} and the
     * {@linkplain #FIELD_POSITION position field}.
     */
    public static final String FIELD_MAP_AND_POSITION = "map_and_pos";
    /**
     * Map field.
     */
    public static final String FIELD_MAP = "map";
    /**
     * Song field.
     */
    public static final String FIELD_SONG = "song";
    /**
     * Position "field". A field for this doesn't actually exist.
     */
    public static final String FIELD_POSITION = "position";
    /**
     * X position field.
     */
    public static final String FIELD_X_POSITION = "position_x";
    /**
     * Y position field.
     */
    public static final String FIELD_Y_POSITION = "position_y";
    /**
     * Direction field.
     */
    public static final String FIELD_DIRECTION = "direction";
    /**
     * Maximum health field.
     */
    public static final String FIELD_MAXIMUM_HEALTH = "max_health";
    /**
     * Whimsical Star count field.
     */
    public static final String FIELD_STAR_COUNT = "star_count";
    /**
     * Current health field.
     */
    public static final String FIELD_CURRENT_HEALTH = "cur_health";
    /**
     * Current weapon slot field.
     */
    public static final String FIELD_CURRENT_WEAPON = "cur_weapon";
    /**
     * Equipment flags field.
     */
    public static final String FIELD_EQUIPS = "equips";
    /**
     * Time played field.
     */
    public static final String FIELD_TIME_PLAYED = "time";
    /**
     * Weapon ID field.
     */
    public static final String FIELD_WEAPON_IDS = "weapons.id";
    /**
     * Weapon level field.
     */
    public static final String FIELD_WEAPON_LEVELS = "weapons.level";
    /**
     * Weapon EXP field.
     */
    public static final String FIELD_WEAPON_EXP = "weapons.exp";
    /**
     * Weapon maximum ammo field.
     */
    public static final String FIELD_WEAPON_MAXIMUM_AMMO = "weapons.max_ammo";
    /**
     * Weapon current ammo field.
     */
    public static final String FIELD_WEAPON_CURRENT_AMMO = "weapons.cur_ammo";
    /**
     * Items field.
     */
    public static final String FIELD_ITEMS = "items";
    /**
     * Warp ID field.
     */
    public static final String FIELD_WARP_IDS = "warps.id";
    /**
     * Warp location field.
     */
    public static final String FIELD_WARP_LOCATIONS = "warps.location";
    /**
     * Map flags field.
     */
    public static final String FIELD_MAP_FLAGS = "map_flags";
    /**
     * Flags field.
     */
    public static final String FIELD_FLAGS = "flags";
    /**
     * <MIM costume "field". A field for this doesn't actually exist.
     */
    public static final String FIELD_MIM_COSTUME = "mim_costume";
    /**
     * Variables "field". A field for this doesn't actually exist.
     */
    public static final String FIELD_VARIABLES = "variables";
    /**
     * Physics variables "field". A field for this doesn't actually exist.
     */
    public static final String FIELD_PHYSICS_VARIABLES = "phys_vars";
    /**
     * Amount of cash "field". A field for this doesn't actually exist.
     */
    public static final String FIELD_CASH = "cash";
    /**
     * <i>(EQ+ STUFF NOT IMPLEMENTED YET!)</i> EQ+ variables.
     */
    public static final String FIELD_EQP_VARIABLES = "eqp.variables";
    /**
     * <i>(EQ+ STUFF NOT IMPLEMENTED YET!)</i> EQ+ "true" modifiers.
     */
    public static final String FIELD_EQP_MODS_TRUE = "eqp.mods.true";
    /**
     * <i>(EQ+ STUFF NOT IMPLEMENTED YET!)</i> EQ+ "false" modifiers.
     */
    public static final String FIELD_EQP_MODS_FALSE = "eqp.mods.false";

    // --- Cave Story+ Fields --- //

    /**
     * "Last modified" date in Unix time.
     */
    public static final String FIELD_MODIFY_DATE = "modify_date";
    /**
     * Difficulty: 0-1 for Original, 2-3 for Easy, 4-5 for Hard, wraps around (6 =
     * 0, 7 = 1, etc.)
     */
    public static final String FIELD_DIFFICULTY = "difficulty";
    /**
     * Used slots. 0-2 are normal, 3-5 are Curly Story.
     */
    public static final String FIELD_USED_SLOTS = "used_slots";
    /**
     * Music volume. Goes from 1-10 (0 is the same as 1 and 10+ is earrape
     * territory).
     */
    public static final String FIELD_MUSIC_VOLUME = "music_volume";
    /**
     * Sound volume. Goes from 1-10 (0 is the same as 1 and 10+ is earrape
     * territory).
     */
    public static final String FIELD_SOUND_VOLUME = "sound_volume";
    /**
     * Soundtrack type. 0/1 for Remastered, 2 for Original, 3 for New.
     */
    public static final String FIELD_SOUNDTRACK_TYPE = "soundtrack_type";
    /**
     * Graphics style. 0 for New, 1 for Original.
     */
    public static final String FIELD_GRAPHICS_STYLE = "graphics_style";
    /**
     * Language. 0 for English, 1 for Japanese.
     */
    public static final String FIELD_LANGUAGE = "language";
    /**
     * "Beat Bloodstained Sanctuary" flag. Unlocks Sanctuary Time Attack.
     */
    public static final String FIELD_BEAT_HELL = "beat_hell";
    /**
     * Best Bloodstained Sanctuary time.
     */
    public static final String FIELD_BEST_HELL_TIME = "best_hell_time";
    /**
     * Best challenge/mod times. Slots 8+ don't have a "best time" index.
     */
    public static final String FIELD_BEST_MOD_TIMES = "best_mod_times";
}
