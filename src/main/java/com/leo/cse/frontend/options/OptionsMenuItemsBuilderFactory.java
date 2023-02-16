package com.leo.cse.frontend.options;

import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Ids;

public class OptionsMenuItemsBuilderFactory {
    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;
    private final int currentTabId;

    public OptionsMenuItemsBuilderFactory(ProfileManager profileManager, GameResourcesManager resourcesManager, int currentTabId) {
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
        this.currentTabId = currentTabId;
    }

    public OptionsMenuItemsBuilder create(int menuId) {
        switch (menuId) {
            case Ids.MENU_FILE:
                return new FileOptionsMenuBuilder(profileManager, resourcesManager);
            case Ids.MENU_EDIT:
                return new EditOptionsMenuBuilder(profileManager);
            case Ids.MENU_TOOLS:
                return new ToolsOptionsMenuBuilder();
            case Ids.MENU_HELP:
                return new HelpOptionsMenuBuilder(profileManager, currentTabId);
            default:
                throw new IllegalArgumentException(String.format("Menu item with id %d is incorrect", menuId));
        }
    }
}
