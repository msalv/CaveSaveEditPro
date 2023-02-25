package com.leo.cse.backend.profile.convert;

import com.leo.cse.backend.profile.exceptions.ProfileFieldException;
import com.leo.cse.backend.profile.model.Profile;

public abstract class ProfileConverter {
    protected final Profile profile;

    protected ProfileConverter(Profile profile) {
        this.profile = profile;
    }

    public abstract Profile convert() throws ProfileFieldException;
}
