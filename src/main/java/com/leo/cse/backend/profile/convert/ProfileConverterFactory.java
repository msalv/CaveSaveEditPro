package com.leo.cse.backend.profile.convert;

import com.leo.cse.backend.profile.model.PlusProfile;
import com.leo.cse.backend.profile.model.Profile;

public class ProfileConverterFactory {
    public ProfileConverter create(Profile profile) {
        if (profile instanceof PlusProfile) {
            return new PlusToNormalProfileConverter(profile);
        } else {
            return new NormalToPlusProfileConverter(profile);
        }
    }
}
