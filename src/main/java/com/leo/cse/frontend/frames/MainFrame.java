package com.leo.cse.frontend.frames;

import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.ProfileStateChangeListener;
import com.leo.cse.backend.profile.ProfileStateEvent;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.CaveSaveEdit;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.editor.SaveEditorPanel;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MainFrame extends JFrame implements ProfileStateChangeListener {
    private static final String WINDOW_TITLE = "CaveSaveEdit Pro";
    private static final Dimension WINDOW_SIZE = new Dimension(870, 734);

    private final ProfileManager profileManager;

    public MainFrame(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        super();
        this.profileManager = profileManager;

        final SaveEditorPanel saveEditorPanel = new SaveEditorPanel(this, profileManager, resourcesManager);
        addPanel(saveEditorPanel);

        pack();

        profileManager.addListener(this);
    }

    private void addPanel(JPanel panel) {
        final Dimension size = getSuitableWindowSize();
        if (size.width < WINDOW_SIZE.width || size.height < WINDOW_SIZE.height) {
            panel.setPreferredSize(WINDOW_SIZE);
            final JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setPreferredSize(size);
            add(scrollPane);
            setResizable(true);
        } else {
            add(panel);
        }
    }

    @Override
    protected void frameInit() {
        super.frameInit();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new ConfirmCloseWindowListener());
        setTitle(WINDOW_TITLE);
        setIconImages(Resources.getAppIcons());

        final Dimension size = getSuitableWindowSize();
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);

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

    private static Dimension getSuitableWindowSize() {
        final Dimension result = new Dimension(WINDOW_SIZE);
        final Dimension screenSize = getScreenSize();

        if (screenSize == null) {
            return result;
        }

        if (screenSize.width * screenSize.height <= 320 * 240) {
            return result;
        }

        if (screenSize.width < result.width) {
            result.width = (int)(screenSize.width * 0.9f);
        }
        if (screenSize.height < result.height) {
            result.height = (int)(screenSize.height * 0.9f);
        }

        return result;
    }

    private static Dimension getScreenSize() {
        try {
            final DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDisplayMode();
            return new Dimension(displayMode.getWidth(), displayMode.getHeight());
        } catch (Exception ex) {
            try {
                return Toolkit.getDefaultToolkit().getScreenSize();
            } catch (Exception e) {
                return WINDOW_SIZE;
            }
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
