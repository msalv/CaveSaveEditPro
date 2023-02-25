package com.leo.cse.backend.profile.convert;

import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.exceptions.ProfileFieldException;
import com.leo.cse.backend.profile.model.PlusProfile;
import com.leo.cse.backend.profile.model.Profile;

class NormalToPlusProfileConverter extends ProfileConverter {
    NormalToPlusProfileConverter(Profile profile) {
        super(profile);
    }

    @Override
    public Profile convert() throws ProfileFieldException {
        final PlusProfile plusProfile = new PlusProfile();
        final byte[] dest = plusProfile.getData();
        final byte[] source = profile.getData();

        System.arraycopy(source, 0, dest, 0, source.length);

        plusProfile.setField(ProfileFields.FIELD_USED_SLOTS, 0, true);
        plusProfile.setField(ProfileFields.FIELD_MODIFY_DATE, Profile.NO_INDEX, System.currentTimeMillis() / 1000L);
        plusProfile.setCurrentSlotId(0);

        return plusProfile;
    }
}
