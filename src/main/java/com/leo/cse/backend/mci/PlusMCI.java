package com.leo.cse.backend.mci;

import java.io.IOException;
import java.io.InputStream;

public class PlusMCI extends CustomMCI {
    public PlusMCI(InputStream inputStream, String filePath) throws IOException, MCIException {
        super(inputStream, filePath);
    }
}
