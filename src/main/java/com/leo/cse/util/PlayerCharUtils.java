package com.leo.cse.util;

import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.backend.mci.MCI;

import java.awt.Image;
import java.awt.Rectangle;

public class PlayerCharUtils {
    public static Image getCharacterImage(ProfileManager profileManager, GameResourcesManager resourcesManager, int slot, boolean isLeftToRight) {
        if (!resourcesManager.hasResources()) {
            return null;
        }

        final MCI mci = profileManager.getCurrentMCI();

        int costume;

        if (mci.hasSpecial("VarHack")) {
            costume = profileManager.getIntField(ProfileFields.FIELD_VARIABLES, 6);
        } else if (mci.hasSpecial("MimHack")) {
            costume = profileManager.getIntField(ProfileFields.FIELD_MIM_COSTUME);
        } else if (slot > 2 && profileManager.isCurrentProfilePlus() && !resourcesManager.isCurrentModePlus()) {
            final Rectangle bounds = new Rectangle(0, 64 * 3 + (isLeftToRight ? 32 : 0), 32, 32);
            return resourcesManager.getResources().getNpcReguImage(bounds);
        } else {
            costume = profileManager.getBooleanField(ProfileFields.FIELD_EQUIPS, 6) ? 1 : 0;
            if (profileManager.isCurrentProfilePlus() && resourcesManager.isCurrentModePlus()) {
                if (slot > 2) {
                    costume += 10;
                }

                int difficulty = profileManager.getShortField(ProfileFields.FIELD_DIFFICULTY);

                while (difficulty > 5) {
                    difficulty -= 5;
                }

                if (difficulty % 2 == 1) {
                    difficulty--;
                }

                costume += difficulty;
            }
        }

        final Rectangle bounds = new Rectangle(
                0,
                64 * costume + (isLeftToRight ? 32 : 0),
                32,
                32);

        return resourcesManager.getResources().getCharacterImage(bounds);
    }
}
