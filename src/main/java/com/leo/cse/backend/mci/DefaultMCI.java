package com.leo.cse.backend.mci;

import java.io.IOException;
import java.io.InputStream;

public class DefaultMCI extends MCI {

    public DefaultMCI(InputStream inputStream, String filePath) throws IOException, MCIException {
        super(inputStream, filePath);
    }

    @Override
    public boolean hasSpecial(String value) {
        return gameInfo.specials.contains(value);
    }

    @Override
    public String getSpecials() {
        final String ret;
        if (hasSpecial("VarHack")) {
            if (hasSpecial("PhysVarHack")) {
                ret = "TSC+ <VAR Hack + <PHY Addon";
            } else {
                ret = "TSC+ <VAR Hack";
            }
        } else if (hasSpecial("MimHack"))
            ret = "<MIM Hack";
        else if (hasSpecial("BuyHack")) {
            ret = "<BUY Hack";
        } else {
            ret = "None";
        }
        final int density = getGraphicsDensity();
        if (density != 1) {
            return "None".equals(ret)
                    ? String.format("%dx Resolution", density)
                    : String.format("%s, %dx Resolution", ret, density);
        }
        return ret;
    }
}
