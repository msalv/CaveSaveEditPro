package com.leo.cse.frontend.options;

import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.Ids;
import com.leo.cse.frontend.ui.components.menu.OptionsMenu;

import java.util.ArrayList;
import java.util.List;

class EditOptionsMenuBuilder extends OptionsMenuItemsBuilder {
    private final ProfileManager profileManager;

    EditOptionsMenuBuilder(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public List<OptionsMenu.Option> build() {
        final List<OptionsMenu.Option> options = new ArrayList<>();

        options.add(new OptionsMenu.Option(Ids.OPTION_EDIT_UNDO, null, "Undo", "Ctrl+Z", profileManager.canUndo()));
        options.add(new OptionsMenu.Option(Ids.OPTION_EDIT_REDO, null, "Redo", "Ctrl+Y", profileManager.canRedo()));

        return options;
    }
}
