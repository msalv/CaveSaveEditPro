package com.leo.cse.backend.exe;

public class ExePointers {
    // ---------------
    // .rdata Pointers
    // ---------------

    /**
     * Pointer to file name for "ArmsItem.tsc".
     */
    public static final int ARMSITEM_PTR = 0x270;
    /**
     * Pointer to image file extension.
     */
    public static final int IMG_EXT_PTR = 0x280;
    /**
     * Pointer to name for the "Credit.tsc" file.
     */
    public static final int CREDIT_PTR = 0x368;
    /**
     * Pointer to name for the "npc.tbl" file.
     */
    public static final int NPC_TBL_PTR = 0x3AB;
    /**
     * Pointer to name for the "PIXEL" resource file.
     */
    public static final int PIXEL_PTR = 0x4E8;
    /**
     * Pointer to name for the "MyChar" graphics file.
     */
    public static final int MYCHAR_PTR = 0x4F0;
    /**
     * Pointer to name for the "Title" graphics file.
     */
    public static final int TITLE_PTR = 0x4F8;
    /**
     * Pointer to name for the "ArmsImage" graphics file.
     */
    public static final int ARMSIMAGE_PTR = 0x500;
    /**
     * Pointer to name for the "Arms" graphics file.
     */
    public static final int ARMS_PTR = 0x50C;
    /**
     * Pointer to name for the "ItemImage" graphics file.
     */
    public static final int ITEMIMAGE_PTR = 0x514;
    /**
     * Pointer to name for the "StageImage" graphics file.
     */
    public static final int STAGEIMAGE_PTR = 0x520;
    /**
     * Pointer to name for the "NpcSym" graphics file.
     */
    public static final int NPCSYM_PTR = 0x52C;
    /**
     * Pointer to name for the "NpcRegu" graphics file.
     */
    public static final int NPCREGU_PTR = 0x538;
    /**
     * Pointer to name for the "TextBox" graphics file.
     */
    public static final int TEXTBOX_PTR = 0x544;
    /**
     * Pointer to name for the "Caret" graphics file.
     */
    public static final int CARET_PTR = 0x54C;
    /**
     * Pointer to name for the "Bullet" graphics file.
     */
    public static final int BULLET_PTR = 0x554;
    /**
     * Pointer to name for the "Face" graphics file.
     */
    public static final int FACE_PTR = 0x55C;
    /**
     * Pointer to name for the "Fade" graphics file.
     */
    public static final int FADE_PTR = 0x564;
    /**
     * Pointer to name for the "data" folder.
     */
    public static final int DATA_FOLDER_PTR = 0x5BC;
    /**
     * Pointer to name for the "Loading" graphics file.
     */
    public static final int LOADING_PTR = 0x5FC;
    /**
     * Pointer to the PXM file tag.
     */
    public static final int PXM_TAG_PTR = 0x67C;
    /**
     * Pointer to the profile name.
     */
    public static final int PROFILE_NAME_PTR = 0x700;
    /**
     * Pointer to the profile header.
     */
    public static final int PROFILE_HEADER_PTR = 0x70C;
    /**
     * Pointer to the profile flag section header.
     */
    public static final int PROFILE_FLAGH_PTR = 0x720;
    /**
     * Pointer to name for the "StageSelect.tsc" file.
     */
    public static final int STAGESELECT_PTR = 0x770;
    /**
     * Pointer to name for the "Stage" folder.
     */
    public static final int STAGE_FOLDER_PTR = 0x7D4;
    /**
     * Pointer to prefix for tileset graphics files.
     */
    public static final int PRT_PREFIX_PTR = 0x7DC;
    /**
     * Pointer to PXA (tile attributes) file extension.
     */
    public static final int PXA_EXT_PTR = 0x7E8;
    /**
     * Pointer to PXM (map) file extension.
     */
    public static final int PXM_EXT_PTR = 0x7F4;
    /**
     * Pointer to PXE (entities) file extension.
     */
    public static final int PXE_EXT_PTR = 0x800;
    /**
     * Pointer to TSC file extension.
     */
    public static final int TSC_EXT_PTR = 0x80C;
    /**
     * Pointer to name for the "Npc" folder.
     */
    public static final int NPC_FOLDER_PTR = 0x81C;
    /**
     * Pointer to prefix for NPC sheet graphics files.
     */
    public static final int NPC_PREFIX_PTR = 0x820;
    /**
     * Pointer to name for the "Head.tsc" file.
     */
    public static final int HEAD_PTR = 0x9A8;

    /**
     * Array of pointers which point to string values.
     */
    public static final int[] STRING_POINTERS = new int[] {
            ARMSITEM_PTR,
            IMG_EXT_PTR,
            CREDIT_PTR,
            NPC_TBL_PTR,
            PIXEL_PTR,
            MYCHAR_PTR,
            TITLE_PTR,
            ARMSIMAGE_PTR,
            ARMS_PTR,
            ITEMIMAGE_PTR,
            STAGEIMAGE_PTR,
            NPCSYM_PTR,
            NPCREGU_PTR,
            TEXTBOX_PTR,
            CARET_PTR,
            BULLET_PTR,
            FACE_PTR,
            FADE_PTR,
            DATA_FOLDER_PTR,
            LOADING_PTR,
            PXM_TAG_PTR,
            PROFILE_NAME_PTR,
            PROFILE_HEADER_PTR,
            PROFILE_FLAGH_PTR,
            STAGESELECT_PTR,
            STAGE_FOLDER_PTR,
            PRT_PREFIX_PTR,
            PXA_EXT_PTR,
            PXM_EXT_PTR,
            PXE_EXT_PTR,
            TSC_EXT_PTR,
            NPC_FOLDER_PTR,
            NPC_PREFIX_PTR,
            HEAD_PTR
    };
}
