package com.leo.cse.backend.mci;

import java.io.IOException;
import java.io.InputStream;

public class CustomMCI extends MCI {

    public CustomMCI(InputStream inputStream, String filePath) throws IOException, MCIException {
        super(inputStream, filePath);
    }

    @Override
    public boolean hasSpecial(String value) {
        return false;
    }

    @Override
    public String getSpecials() {
        final int density = getGraphicsDensity();
        if (density != 1) {
            return density + "x Resolution";
        }
        return "None";
    }
}
