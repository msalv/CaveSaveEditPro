package com.leo.cse.frontend.actions;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class NumPadActionsBinder {
    public void bind(JComponent component, Consumer<Integer> onKeyPressed) {
        final InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        final ActionMap actionMap = component.getActionMap();

        if (inputMap == null || actionMap == null) {
            return;
        }

        for (int k = KeyEvent.VK_0; k <= KeyEvent.VK_9; k++) {
            bindAction(inputMap, actionMap, new KeyboardAction(
                    k,
                    KeyStroke.getKeyStroke(k, 0),
                    onKeyPressed
            ));
        }

        for (int k = KeyEvent.VK_NUMPAD0; k <= KeyEvent.VK_NUMPAD9; k++) {
            bindAction(inputMap, actionMap, new KeyboardAction(
                    k,
                    KeyStroke.getKeyStroke(k, 0),
                    onKeyPressed
            ));
        }

        bindAction(inputMap, actionMap, new KeyboardAction(
                KeyEvent.VK_DELETE,
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
                onKeyPressed
        ));

        bindAction(inputMap, actionMap, new KeyboardAction(
                KeyEvent.VK_BACK_SPACE,
                KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
                onKeyPressed
        ));
    }

    private void bindAction(InputMap inputMap, ActionMap actionMap, GenericAction action) {
        inputMap.put(action.getKeyStroke(), action.getKey());
        actionMap.put(action.getKey(), action);
    }
}
