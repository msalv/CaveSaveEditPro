package com.leo.cse.frontend.editor.cells;

import com.leo.cse.dto.Flag;

import java.awt.Component;

import javax.swing.JList;

public class FlagCellRenderer extends FlagsRow.CellRenderer<Flag> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Flag> list, Flag value, int index, boolean isSelected, boolean cellHasFocus) {
        return getListCellRendererComponent(value.value,
                value.id,
                value.description,
                index != list.getModel().getSize() - 1,
                index == hoveredIndex);
    }

    @Override
    String getDescriptionPlaceholder() {
        return Flag.DESC_NONE;
    }
}
