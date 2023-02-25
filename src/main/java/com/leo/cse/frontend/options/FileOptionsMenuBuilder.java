package com.leo.cse.frontend.options;

import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Ids;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.components.menu.OptionsMenu;

import java.util.ArrayList;
import java.util.List;

class FileOptionsMenuBuilder extends OptionsMenuItemsBuilder {
    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    FileOptionsMenuBuilder(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
    }

    @Override
    public List<OptionsMenu.Option> build() {
        final List<OptionsMenu.Option> options = new ArrayList<>();

        options.add(new OptionsMenu.Option(Ids.OPTION_FILE_NEW, Resources.getMenuIcon(Ids.ICON_MENU_NEW_FILE), "New"));
        options.add(new OptionsMenu.Option(Ids.OPTION_FILE_NEW_PLUS, null, "New CS+ File"));
        options.add(new OptionsMenu.Option(Ids.OPTION_FILE_OPEN, Resources.getMenuIcon(Ids.ICON_MENU_OPEN), "Open...", "Ctrl+O"));
        options.add(new OptionsMenu.Option(Ids.OPTION_SLOT_CHANGE, Resources.getMenuIcon(Ids.ICON_MENU_SLOT), "Select Slot...", profileManager.isCurrentProfilePlus()));
        options.add(new OptionsMenu.Option(Ids.OPTION_FILE_CLOSE, null, "Close", profileManager.hasProfile()));

        options.add(new OptionsMenu.Option(Ids.OPTION_RESOURCES_LOAD, Resources.getMenuIcon(Ids.ICON_MENU_EXECUTABLE), "Load Game Resources...", "Ctrl+Shift+O"));

        if (isWindows()) {
            options.add(new OptionsMenu.Option(Ids.OPTION_GAME_RUN, Resources.getMenuIcon(Ids.ICON_MENU_RUN), "Run Game", "Ctrl+R", resourcesManager.hasResources()));
        }

        options.add(new OptionsMenu.Option(Ids.OPTION_RESOURCES_UNLOAD, null, "Unload Game Resources", resourcesManager.hasResources()));

        options.add(new OptionsMenu.Option(Ids.OPTION_FILE_SAVE, Resources.getMenuIcon(Ids.ICON_MENU_SAVE), "Save", "Ctrl+S", profileManager.hasProfile()));
        options.add(new OptionsMenu.Option(Ids.OPTION_FILE_SAVE_AS, null, "Save As...", "Ctrl+Shift+S", profileManager.hasProfile()));

        final String convertOptionTitle = profileManager.isCurrentProfilePlus()
                ? "Convert to Cave Story File..."
                : "Convert to Cave Story+ File...";
        options.add(new OptionsMenu.Option(Ids.OPTION_FILE_CONVERT, null, convertOptionTitle, profileManager.hasProfile()));

        options.add(new OptionsMenu.Option(Ids.OPTION_SETTINGS, Resources.getMenuIcon(Ids.ICON_MENU_SETTINGS), "Settings"));
        options.add(new OptionsMenu.Option(Ids.OPTION_EXIT, null, "Exit", "Esc"));

        return options;
    }

    private boolean isWindows() {
        try {
            final String osName = System.getProperty("os.name");
            return (osName != null && osName.contains("Windows"));
        } catch (Exception ex) {
            AppLogger.error("Unable to get os.name", ex);
            return false;
        }
    }
}
