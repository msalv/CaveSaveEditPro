package com.leo.cse.backend.profile;

import com.leo.cse.backend.BytesReaderWriter;
import com.leo.cse.dto.StartPoint;
import com.leo.cse.log.AppLogger;
import com.leo.cse.dto.Difficulty;
import com.leo.cse.backend.profile.model.PlusProfile;
import com.leo.cse.backend.profile.model.Profile;
import com.leo.cse.backend.profile.exceptions.ProfileFieldException;

public class PlusProfileManager {
    private static final byte[] EMPTY_SLOT_DATA = new byte[PlusProfile.SECTION_LENGTH];

    private final PlusProfile plusProfile;

    public PlusProfileManager(PlusProfile plusProfile) {
        this.plusProfile = plusProfile;
    }

    public int setCurrentSlotId(int slotId) {
        return plusProfile.setCurrentSlotId(slotId);
    }

    public int getCurrentSlotId() {
        return plusProfile.getCurrentSlotId();
    }

    public void cloneSlot(int srcId, int destId) {
        System.arraycopy(plusProfile.getData(), srcId * PlusProfile.SECTION_LENGTH, plusProfile.getData(), destId * PlusProfile.SECTION_LENGTH, PlusProfile.SECTION_LENGTH);
        if (destId > 2) { // Curly Story
            final int prevSlotId = plusProfile.setCurrentSlotId(destId);
            try {
                plusProfile.setField(ProfileFields.FIELD_DIFFICULTY, Profile.NO_INDEX, Difficulty.ORIGINAL.value);
            } catch (ProfileFieldException e) {
                AppLogger.error("Failed to set field", e);
            }
            plusProfile.setCurrentSlotId(prevSlotId);
        }
        try {
            plusProfile.setField(ProfileFields.FIELD_USED_SLOTS, destId, true);
        } catch (ProfileFieldException e) {
            AppLogger.error("Failed to set field", e);
        }
    }

    public void deleteSlot(int slotId) {
        System.arraycopy(EMPTY_SLOT_DATA, 0, plusProfile.getData(), slotId * PlusProfile.SECTION_LENGTH, PlusProfile.SECTION_LENGTH);
        try {
            plusProfile.setField(ProfileFields.FIELD_USED_SLOTS, slotId, false);
        } catch (ProfileFieldException e) {
            AppLogger.error("Failed to set field", e);
        }
    }

    public boolean isSlotExists(int slotId) {
        try {
            return Boolean.TRUE.equals(plusProfile.getField(ProfileFields.FIELD_USED_SLOTS, slotId));
        } catch (ProfileFieldException e) {
            AppLogger.error("Failed to get field", e);
        }
        return false;
    }

    public void createSlot(int slotId, StartPoint startPoint) {
        final long timestamp = System.currentTimeMillis() / 1000L;
        final byte[] newData = new byte[PlusProfile.SECTION_LENGTH];

        BytesReaderWriter.writeString(newData, ProfilePointers.HEADER_PTR, Profile.DEFAULT_HEADER);
        BytesReaderWriter.writeString(newData, ProfilePointers.FLAGS_HEADER_PTR, Profile.DEFAULT_FLAGH);
        System.arraycopy(newData, 0, plusProfile.getData(), slotId * PlusProfile.SECTION_LENGTH, PlusProfile.SECTION_LENGTH);

        try {
            plusProfile.setField(ProfileFields.FIELD_USED_SLOTS, slotId, true);
            final int currentSlotId = plusProfile.setCurrentSlotId(slotId);

            plusProfile.reset(startPoint);
            plusProfile.setField(ProfileFields.FIELD_MODIFY_DATE, Profile.NO_INDEX, timestamp);
            plusProfile.setField(ProfileFields.FIELD_MUSIC_VOLUME, Profile.NO_INDEX, 8);
            plusProfile.setField(ProfileFields.FIELD_SOUND_VOLUME, Profile.NO_INDEX, 10);

            plusProfile.setCurrentSlotId(currentSlotId);
        } catch (ProfileFieldException e) {
            AppLogger.error("Failed to set field", e);
        }
    }

    public boolean isCurrentSlotExists() {
        return isSlotExists(getCurrentSlotId());
    }
}
