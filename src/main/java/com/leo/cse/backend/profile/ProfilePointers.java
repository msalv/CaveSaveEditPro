package com.leo.cse.backend.profile;

public class ProfilePointers {
    public static final int HEADER_PTR = 0x000;
    public static final int MAP_PTR = 0x008;
    public static final int SONG_PTR = 0x00C;
    public static final int MAP_X_PTR = 0x011;
    public static final int MAP_Y_PTR = 0x015;
    public static final int DIRECTION_PTR = 0x018;
    public static final int MAX_HEALTH_PTR = 0x01C;
    public static final int STAR_COUNT_PTR = 0x01E;
    public static final int CURRENT_HEALTH_PTR = 0x020;
    public static final int CURRENT_WEAPON_PTR = 0x024;
    public static final int EQUIPS_PTR = 0x02C;
    public static final int TIME_PLAYED_PTR = 0x034;
    public static final int WEAPON_IDS_PTR = 0x038;
    public static final int WEAPON_LEVELS_PTR = 0x03C;
    public static final int WEAPON_EXP_PTR = 0x040;
    public static final int WEAPON_MAXIMUM_AMMO_PTR = 0x044;
    public static final int WEAPON_CURRENT_AMMO_PTR = 0x048;
    public static final int ITEMS_PTR = 0x0D8;
    public static final int WARP_IDS_PTR = 0x158;
    public static final int WARP_LOCATIONS_PTR = 0x15C;
    public static final int MAP_FLAGS_PTR = 0x198;
    public static final int FLAGS_HEADER_PTR = 0x218;
    public static final int FLAGS_PTR = 0x21C;

    // --- Cave Story+ pointers -- //

    public static final int MODIFY_DATE_PTR =      0x00608;
    public static final int DIFFICULTY_PTR =       0x00610;
    public static final int SLOT_QUOTE_PTR =       0x1F020;
    public static final int SLOT_CURLY_PTR =       0x1F021;
    public static final int MUSIC_VOLUME_PTR =     0x1F040;
    public static final int SOUND_VOLUME_PTR =     0x1F044;
    // skipping a byte at 0x1F048
    public static final int SOUNDTRACK_TYPE_PTR =  0x1F049;
    public static final int GRAPHICS_STYLE_PTR =   0x1F04A;
    public static final int LANGUAGE_PTR =         0x1F04B;
    public static final int BEAT_HELL_PTR =        0x1F04C;
    // skipping four bytes at 0x1F04D
    public static final int BEST_HELL_TIME_PTR =   0x1F054;
    public static final int BEST_MOD_TIMES_PTR =   0x1F058;

    // --- Mods extended pointers -- //

    public static final int EXT_MIM_COSTUME_PTR = 0x600;
    public static final int EXT_VARIABLES_PTR = 0x50A;
    public static final int EXT_PHYSICS_VARS_PTR = 0x4DC;
    public static final int EXT_CASH_PTR = 0x600;
    public static final int EXT_EQP_VARS_PTR = 0x196;
    public static final int EXT_EQP_MODS_TRUE_PTR = 0x1D6;
    public static final int EXT_EQP_MODS_FALSE_PTR = 0x1D8;

    // -- Array fields lengths -- //

    public static final int EQUIPS_LENGTH = 16;
    public static final int WEAPON_IDS_LENGTH = 7;
    public static final int WEAPON_LEVELS_LENGTH = 7;
    public static final int WEAPON_EXP_LENGTH = 7;
    public static final int WEAPON_MAXIMUM_AMMO_LENGTH = 7;
    public static final int WEAPON_CURRENT_AMMO_LENGTH = 7;
    public static final int ITEMS_LENGTH = 30;
    public static final int WARP_IDS_LENGTH = 7;
    public static final int WARP_LOCATIONS_LENGTH = 7;
    public static final int MAP_FLAGS_LENGTH = 128;
    public static final int FLAGS_LENGTH = 8000;

    // -- Cave Story+ array fields lengths -- //

    public static final int BEST_MOD_TIMES_LENGTH = 6;

    // Mods extended array fields lengths -- //

    public static final int EXT_VARIABLES_LENGTH = 123;
    public static final int EXT_PHYSICS_VARS_LENGTH = 17;
    public static final int EXT_EQP_EQP_VARS_LENGTH = 64;
    public static final int EXT_EQP_MODS_TRUE_LENGTH = 3;
    public static final int EXT_EQP_MODS_FALSE_LENGTH = 3;

    private ProfilePointers(){
    }
}
