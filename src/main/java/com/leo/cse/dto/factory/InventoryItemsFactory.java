package com.leo.cse.dto.factory;

import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.dto.InventoryItem;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.backend.mci.MCI;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class InventoryItemsFactory {
    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    public InventoryItemsFactory(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
    }

    public InventoryItem create(int i) {
        final MCI mci = profileManager.getCurrentMCI();
        final int itemId = profileManager.getIntField(ProfileFields.FIELD_ITEMS, i);
        final String title = mci.getItemName(itemId);

        final Image image = (itemId != 0 && resourcesManager.hasResources())
                ? resourcesManager.getResources().getInventoryItemImage(itemId)
                : null;

        return new InventoryItem(itemId, title, image);
    }

    public List<InventoryItem> createAll() {
        final String[] itemNames = profileManager.getCurrentMCI().getItemNames();
        final List<InventoryItem> items = new ArrayList<>();
        for (int itemId = 0; itemId < itemNames.length; ++itemId) {
            final String title = itemNames[itemId];
            final Image image = (itemId != 0 && resourcesManager.hasResources())
                    ? resourcesManager.getResources().getInventoryItemImage(itemId)
                    : null;

            items.add(new InventoryItem(itemId, title, image));
        }

        return items;
    }
}
