package com.leo.cse.frontend.options.niku;

import com.leo.cse.frontend.Ids;
import com.leo.cse.frontend.options.OptionsMenuItemsBuilder;
import com.leo.cse.frontend.ui.components.menu.OptionsMenu;

import java.util.ArrayList;
import java.util.List;

class NikuEditorFileOptionsMenuBuilder extends OptionsMenuItemsBuilder {
    @Override
    public List<OptionsMenu.Option> build() {
        final List<OptionsMenu.Option> options = new ArrayList<>();

        options.add(new OptionsMenu.Option(Ids.OPTION_290_REC_FILE_NEW, null, "New"));
        options.add(new OptionsMenu.Option(Ids.OPTION_290_REC_FILE_OPEN, null, "Open..."));
        options.add(new OptionsMenu.Option(Ids.OPTION_290_REC_FILE_SAVE, null, "Save"));
        options.add(new OptionsMenu.Option(Ids.OPTION_290_REC_FILE_SAVE_AS, null, "Save As..."));
        options.add(new OptionsMenu.Option(Ids.OPTION_290_REC_FILE_CLOSE, null, "Close"));

        return options;
    }
}
