package com.leo.cse.frontend.options.niku;

import com.leo.cse.frontend.Ids;
import com.leo.cse.frontend.options.OptionsMenuItemsBuilder;
import com.leo.cse.frontend.ui.components.menu.OptionsMenu;

import java.util.ArrayList;
import java.util.List;

class NikuEditorEditOptionsMenuBuilder extends OptionsMenuItemsBuilder {
    @Override
    public List<OptionsMenu.Option> build() {
        final List<OptionsMenu.Option> options = new ArrayList<>();

        options.add(new OptionsMenu.Option(Ids.OPTION_290_REC_EDIT_SELECT_TITLE_SCREEN, null, "Title Screen..."));

        return options;
    }
}
