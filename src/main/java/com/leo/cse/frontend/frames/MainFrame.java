package com.leo.cse.frontend.frames;

import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.ProfileStateChangeListener;
import com.leo.cse.backend.profile.ProfileStateEvent;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.CaveSaveEdit;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.editor.SaveEditorPanel;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class MainFrame extends JFrame implements ProfileStateChangeListener {
    private static final String WINDOW_TITLE = "CaveSaveEdit Pro";
    private static final Dimension WINDOW_SIZE = new Dimension(870, 734);

    private final ProfileManager profileManager;

    public MainFrame(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        super();
        this.profileManager = profileManager;

        final SaveEditorPanel saveEditorPanel = new SaveEditorPanel(this, profileManager, resourcesManager);
        add(saveEditorPanel);

        pack();

        profileManager.addListener(this);
    }

    @Override
    protected void frameInit() {
        super.frameInit();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new ConfirmCloseWindowListener());
        setTitle(WINDOW_TITLE);
        setIconImages(Resources.getAppIcons());

        setMaximumSize(WINDOW_SIZE);
        setMinimumSize(WINDOW_SIZE);
        setPreferredSize(WINDOW_SIZE);

        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void updateTitle() {
        if (profileManager.hasProfile()) {
            final String path = profileManager.getCurrentFilePath();
            if (path == null || path.length() == 0) {
                this.setTitle(String.format("%s - UNSAVED*", WINDOW_TITLE));
            } else {
                this.setTitle(String.format("%s - %s%s", WINDOW_TITLE, path, profileManager.isModified() ? "*" : ""));
            }
        } else {
            this.setTitle(WINDOW_TITLE);
        }
    }

    @Override
    public void onProfileStateChanged(ProfileStateEvent event, Object payload) {
        updateTitle();
    }

    // --- WindowListener ---

    private static class ConfirmCloseWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            CaveSaveEdit.exit();
        }
    }
}
