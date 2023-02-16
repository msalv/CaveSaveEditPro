package com.leo.cse.frontend.options;

import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.Ids;
import com.leo.cse.frontend.ui.components.menu.OptionsMenu;

import java.util.ArrayList;
import java.util.List;

class HelpOptionsMenuBuilder extends OptionsMenuItemsBuilder {
    private final ProfileManager profileManager;
    private final int currentTabId;

    public HelpOptionsMenuBuilder(ProfileManager profileManager, int currentTabId) {
        super();
        this.profileManager = profileManager;
        this.currentTabId = currentTabId;
    }

    @Override
    public List<OptionsMenu.Option> build() {
        final List<OptionsMenu.Option> options = new ArrayList<>();

        final String welcomePageOptionTitle = (currentTabId != Ids.TAB_WELCOME_PAGE)
                ? "Show Welcome Page"
                : "Hide Welcome Page";

        options.add(new OptionsMenu.Option(Ids.OPTION_HELP_WELCOME_PAGE, null, welcomePageOptionTitle, profileManager.hasProfile()));
        options.add(new OptionsMenu.Option(Ids.OPTION_HELP_CHECK_UPDATES, null, "Check for Updates..."));
        options.add(new OptionsMenu.Option(Ids.OPTION_HELP_ABOUT, null, "About"));

        return options;
    }
}
