package com.leo.cse.frontend.editor.cells;

import com.leo.cse.dto.MapFlag;

import java.awt.Component;

import javax.swing.JList;

public class MapFlagCellRenderer extends FlagsRow.CellRenderer<MapFlag> {
    @Override
    public Component getListCellRendererComponent(JList<? extends MapFlag> list, MapFlag value, int index, boolean isSelected, boolean cellHasFocus) {
        return getListCellRendererComponent(value.value,
                value.id,
                value.description,
                index != list.getModel().getSize() - 1,
                index == hoveredIndex);
    }

    @Override
    String getDescriptionPlaceholder() {
        return MapFlag.DESC_NONE;
    }
}
