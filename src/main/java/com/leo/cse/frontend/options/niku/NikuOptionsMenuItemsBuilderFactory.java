package com.leo.cse.frontend.options.niku;

import com.leo.cse.frontend.Ids;
import com.leo.cse.frontend.options.OptionsMenuItemsBuilder;

public class NikuOptionsMenuItemsBuilderFactory {
    public OptionsMenuItemsBuilder create(int menuId) {
        switch (menuId) {
            case Ids.MENU_290_REC_FILE:
                return new NikuEditorFileOptionsMenuBuilder();
            case Ids.MENU_290_REC_EDIT:
                return new NikuEditorEditOptionsMenuBuilder();
            case Ids.MENU_290_REC_HELP:
                return new NikuEditorHelpOptionsMenuBuilder();
            default:
                throw new IllegalArgumentException(String.format("Menu item with id %d is incorrect", menuId));
        }
    }
}
