package com.leo.cse.backend.mci;

import com.leo.cse.backend.profile.model.NormalProfile;
import com.leo.cse.backend.profile.model.PlusProfile;
import com.leo.cse.backend.profile.model.Profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MCIFactory {
    public static MCI createDefault() throws IOException, MCIException {
        return new DefaultMCI(
            MCI.class.getResourceAsStream("/default.json"),
            "resources://default.json"
        );
    }

    public static MCI createPlus() throws IOException, MCIException {
        return new PlusMCI(
            MCI.class.getResourceAsStream("/plus.json"),
            "resources://plus.json"
        );
    }

    public static MCI fromFile(final File file) throws Exception {
        return new CustomMCI(new FileInputStream(file), file.getAbsolutePath());
    }

    public static MCI fromProfile(Profile profile) throws MCIException, IOException {
        if (profile instanceof PlusProfile) {
            return createPlus();
        }
        if (profile instanceof NormalProfile) {
            return createDefault();
        }
        throw new MCIException(String.format(
                "Profile of type %s has no corresponding MCI",
                profile.getClass().getCanonicalName())
        );
    }
}
