package com.leo.cse.frontend.actions;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.KeyStroke;

public class KeyboardAction extends GenericAction {
    private static final Supplier<Boolean> TRUE_SUPPLIER = () -> true;

    private final Consumer<Integer> onKeyPressed;
    private final Supplier<Boolean> isEnabled;

    public KeyboardAction(Object key, KeyStroke keyStroke, Consumer<Integer> onKeyPressed) {
        this(key, keyStroke, onKeyPressed, TRUE_SUPPLIER);
    }

    public KeyboardAction(Object key, KeyStroke keyStroke, Consumer<Integer> onKeyPressed, Supplier<Boolean> isEnabled) {
        super(key, keyStroke);
        this.onKeyPressed = onKeyPressed;
        this.isEnabled = isEnabled;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        onKeyPressed.accept(getKeyStroke().getKeyCode());
    }

    @Override
    public boolean isEnabled() {
        return isEnabled.get();
    }
}
