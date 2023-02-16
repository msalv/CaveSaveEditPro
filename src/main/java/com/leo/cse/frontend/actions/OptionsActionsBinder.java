package com.leo.cse.frontend.actions;

import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Ids;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class OptionsActionsBinder {
    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    public OptionsActionsBinder(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
    }

    public void bind(JComponent component, Consumer<Integer> onOptionSelected) {
        final InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        final ActionMap actionMap = component.getActionMap();

        if (inputMap == null || actionMap == null) {
            return;
        }

        bindAction(inputMap, actionMap, new GenericOptionsAction(
                "Ctrl+O",
                KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK),
                Ids.OPTION_FILE_OPEN,
                onOptionSelected
        ));

        bindAction(inputMap, actionMap, new GenericOptionsAction(
                "Ctrl+Shift+O",
                KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
                Ids.OPTION_RESOURCES_LOAD,
                onOptionSelected
        ));

        bindAction(inputMap, actionMap, new GenericOptionsAction(
                "Ctrl+R",
                KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK),
                Ids.OPTION_GAME_RUN,
                onOptionSelected,
                resourcesManager::hasResources
        ));

        bindAction(inputMap, actionMap, new GenericOptionsAction(
                "Ctrl+S",
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
                Ids.OPTION_FILE_SAVE,
                onOptionSelected,
                profileManager::hasProfile
        ));

        bindAction(inputMap, actionMap, new GenericOptionsAction(
                "Ctrl+Shift+S",
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
                Ids.OPTION_FILE_SAVE_AS,
                onOptionSelected,
                profileManager::hasProfile
        ));

        bindAction(inputMap, actionMap, new GenericOptionsAction(
                "Esc",
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                Ids.OPTION_EXIT,
                onOptionSelected
        ));

        bindAction(inputMap, actionMap, new GenericOptionsAction(
                "Ctrl+Z",
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK),
                Ids.OPTION_EDIT_UNDO,
                onOptionSelected,
                profileManager::canUndo
        ));

        bindAction(inputMap, actionMap, new GenericOptionsAction(
                "Ctrl+Y",
                KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK),
                Ids.OPTION_EDIT_REDO,
                onOptionSelected,
                profileManager::canRedo
        ));
    }

    private void bindAction(InputMap inputMap, ActionMap actionMap, GenericAction action) {
        inputMap.put(action.getKeyStroke(), action.getKey());
        actionMap.put(action.getKey(), action);
    }
}
