package com.leo.cse.backend.profile.convert;

import com.leo.cse.backend.profile.exceptions.ProfileFieldException;
import com.leo.cse.backend.profile.model.NormalProfile;
import com.leo.cse.backend.profile.model.PlusProfile;
import com.leo.cse.backend.profile.model.Profile;

class PlusToNormalProfileConverter extends ProfileConverter {
    PlusToNormalProfileConverter(Profile profile) {
        super(profile);
    }

    @Override
    public Profile convert() throws ProfileFieldException {
        final NormalProfile normalProfile = new NormalProfile();
        final byte[] dest = normalProfile.getData();
        final byte[] source = profile.getData();
        final int slotId = ((PlusProfile) profile).getCurrentSlotId();

        System.arraycopy(source, slotId * PlusProfile.SECTION_LENGTH, dest, 0, NormalProfile.FILE_LENGTH);

        return normalProfile;
    }
}
