package com.leo.cse.frontend.actions;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.KeyStroke;

public class GenericOptionsAction extends GenericAction {
    private static final Supplier<Boolean> TRUE_SUPPLIER = () -> true;

    private final int optionId;
    private final Consumer<Integer> onOptionSelected;
    private final Supplier<Boolean> isEnabled;

    public GenericOptionsAction(Object key, KeyStroke keyStroke, int optionId, Consumer<Integer> onOptionSelected) {
        this(key, keyStroke, optionId, onOptionSelected, TRUE_SUPPLIER);
    }

    public GenericOptionsAction(Object key, KeyStroke keyStroke, int optionId, Consumer<Integer> onOptionSelected, Supplier<Boolean> isEnabled) {
        super(key, keyStroke);
        this.optionId = optionId;
        this.onOptionSelected = onOptionSelected;
        this.isEnabled = isEnabled;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        onOptionSelected.accept(optionId);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled.get();
    }
}
