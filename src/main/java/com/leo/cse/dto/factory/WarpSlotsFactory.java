package com.leo.cse.dto.factory;

import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.dto.WarpSlot;
import com.leo.cse.backend.res.GameResourcesManager;

import java.awt.Image;

public class WarpSlotsFactory {
    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    public WarpSlotsFactory(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
    }

    public WarpSlot create(int i) {
        final int id = profileManager.getIntField(ProfileFields.FIELD_WARP_IDS, i);
        final String name = profileManager.getCurrentMCI().getWarpName(id);
        final int locationId = profileManager.getIntField(ProfileFields.FIELD_WARP_LOCATIONS, i);
        final String locationName = profileManager.getCurrentMCI().getWarpLocName(locationId);
        final Image image = (id != 0 && resourcesManager.hasResources())
                ? resourcesManager.getResources().getWarpSlotImage(id)
                : null;

        return new WarpSlot(id, name, locationId, locationName, image);
    }
}
