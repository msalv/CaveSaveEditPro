package com.leo.cse.frontend.dialogs;

import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.plus.PlusSlots;
import com.leo.cse.frontend.ui.layout.RootDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;

public class PlusSlotsDialog extends RootDialog {
    private final static Dimension CONTENT_SIZE = new Dimension(548, 500);

    public PlusSlotsDialog(
            Frame parentFrame,
            Component parentComponent, ProfileManager profileManager, GameResourcesManager resourcesManager) {
        super(parentFrame, "Select Slot", true);

        setMinimumSize(CONTENT_SIZE);

        setResizable(false);
        setLocationRelativeTo(parentComponent);

        final Container contentPane = getContentPane();
        contentPane.setBackground(ThemeData.getBackgroundColor());
        contentPane.setPreferredSize(CONTENT_SIZE);

        final PlusSlots content = new PlusSlots(profileManager, resourcesManager);
        content.setCallback(() -> {
            dispatchEvent(new WindowEvent(PlusSlotsDialog.this, WindowEvent.WINDOW_CLOSING));
        });

        contentPane.add(content);
        pack();
    }
}
