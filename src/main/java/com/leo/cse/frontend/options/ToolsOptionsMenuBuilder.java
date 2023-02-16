package com.leo.cse.frontend.options;

import com.leo.cse.frontend.Ids;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.components.menu.OptionsMenu;

import java.util.ArrayList;
import java.util.List;

class ToolsOptionsMenuBuilder extends OptionsMenuItemsBuilder {

    ToolsOptionsMenuBuilder() {
    }

    @Override
    public List<OptionsMenu.Option> build() {
        final List<OptionsMenu.Option> options = new ArrayList<>();

        options.add(new OptionsMenu.Option(Ids.OPTION_TOOLS_290_REC, Resources.getMenuIcon(Ids.ICON_MENU_CLOCK), "Edit 290.rec"));

        return options;
    }
}
