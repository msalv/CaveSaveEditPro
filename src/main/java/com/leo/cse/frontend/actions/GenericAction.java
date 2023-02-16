package com.leo.cse.frontend.actions;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public abstract class GenericAction extends AbstractAction {
    private final Object key;
    private final KeyStroke keyStroke;

    public GenericAction(Object key, KeyStroke keyStroke) {
        this.key = key;
        this.keyStroke = keyStroke;
    }

    public Object getKey() {
        return key;
    }

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }
}
