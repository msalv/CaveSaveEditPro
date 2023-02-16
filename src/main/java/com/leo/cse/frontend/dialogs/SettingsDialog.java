package com.leo.cse.frontend.dialogs;

import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.dialogs.settings.SettingsComponent;
import com.leo.cse.frontend.ui.layout.RootDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;

public class SettingsDialog extends RootDialog {
    private final static Dimension CONTENT_SIZE = new Dimension(300, 187);

    public SettingsDialog(
            Frame parentFrame,
            Component parentComponent,
            ProfileManager profileManager,
            GameResourcesManager resourcesManager) {
        super(parentFrame, "Settings", true);

        setMinimumSize(CONTENT_SIZE);

        setResizable(false);
        setLocationRelativeTo(parentComponent);

        final Container contentPane = getContentPane();
        contentPane.setBackground(ThemeData.getBackgroundColor());
        contentPane.setPreferredSize(CONTENT_SIZE);

        final SettingsComponent component = new SettingsComponent(profileManager, resourcesManager);
        component.setCallback(() -> {
            contentPane.setBackground(ThemeData.getBackgroundColor());
        });
        contentPane.add(component);

        pack();
    }
}
