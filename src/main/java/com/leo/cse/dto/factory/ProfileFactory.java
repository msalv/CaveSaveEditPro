package com.leo.cse.dto.factory;

import com.leo.cse.backend.profile.model.NormalProfile;
import com.leo.cse.backend.profile.model.PlusProfile;
import com.leo.cse.backend.profile.model.Profile;
import com.leo.cse.backend.profile.exceptions.ProfileFieldException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProfileFactory {
    private ProfileFactory() {
    }

    public static Profile fromFile(File file) throws IOException, ProfileFieldException {
        final Path path = file.toPath();
        final long fileSize = Files.size(path);

        final byte[] data = Files.readAllBytes(path);

        final Profile profile;

        if (fileSize >= PlusProfile.FILE_LENGTH) {
            profile = new PlusProfile(data);
        } else if (fileSize >= NormalProfile.FILE_LENGTH) {
            profile = new NormalProfile(data);
        } else {
            throw new IllegalArgumentException("The file is not a valid Profile.dat file");
        }

        return profile;
    }
}
