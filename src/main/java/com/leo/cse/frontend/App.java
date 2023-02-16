package com.leo.cse.frontend;

import com.leo.cse.util.async.IndeterminateAsyncTaskCallback;
import com.leo.cse.log.AppLogger;
import com.leo.cse.log.Log4JBackendLogger;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.frames.LoadFrame;
import com.leo.cse.frontend.frames.MainFrame;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.util.Dialogs;
import com.leo.cse.util.StringUtils;

import org.apache.logging.log4j.LogManager;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class App {
    public static final Version VERSION = new Version("5.0.0");

    private MainFrame mainFrame;
    private LoadFrame loadFrame;

    private final ProfileManager profileManager = ProfileManager.getInstance();
    private final GameResourcesManager resourcesManager = GameResourcesManager.getInstance();

    public App() {
        try {
            onCreate();
        } catch (Exception e) {
            panic(e);
        }
    }

    private void onCreate() throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
        AppLogger.init(new Log4JBackendLogger(LogManager.getLogger("CSE")));
        Config.init();
        setLookAndFeel();
        setEventQueue();

        Resources.loadIcons();
        Resources.loadFonts();
        Resources.loadUI(ThemeData.getForegroundColor());

        if (!SwingUtilities.isEventDispatchThread()) {
            final LoadFrame frame = new LoadFrame();
            loadFrame = frame;
            if (!checkUpdates()) {
                frame.setText("Loading...");
            }
        }
    }

    private boolean checkUpdates() {
        if (UpdatesChecker.shouldSkipCheck()) {
            AppLogger.info("Update check: disabled, skipping...");
            return false;
        }

        UpdatesChecker.check(false, new IndeterminateAsyncTaskCallback<Version>() {
            @Override
            public void onPreExecute() {
                if (loadFrame != null) {
                    loadFrame.setText("Checking for updates...");
                }
            }

            @Override
            public void onPostExecute(Version version) {
                if (loadFrame != null) {
                    loadFrame.setText("Loading...");
                }
            }
        });

        return true;
    }

    public App run() {
        SwingUtilities.invokeLater(() -> {
            mainFrame = new MainFrame(profileManager, resourcesManager);
            if (loadFrame != null) {
                loadFrame.dispose();
                loadFrame = null;
            }
            mainFrame.setVisible(true);
            mainFrame.requestFocus();

            if (!autoloadProfile()) {
                SwingUtilities.invokeLater(this::autoloadGameResources);
            }
        });
        return this;
    }

    private boolean autoloadProfile() {
        if (Config.getBoolean(Config.KEY_AUTOLOAD_PROFILE, false)) {
            SwingUtilities.invokeLater(() -> {
                final String path = Config.get(Config.KEY_LAST_PROFILE, System.getProperty("user.dir") + File.separator + "Profile.dat");
                final File file = new File(path);
                if (file.exists()) {
                    try {
                        profileManager.loadProfile(file, this::autoloadGameResources);
                    } catch (Exception e) {
                        AppLogger.error("Unable to autoload recent profile: " + file, e);
                    }
                }
            });
            return true;
        }
        return false;
    }

    private void autoloadGameResources() {
        if (Config.getBoolean(Config.KEY_AUTOLOAD_EXE, false)) {
            final String path = Config.get(Config.KEY_LAST_MOD, null);

            if (StringUtils.isNullOrEmpty(path)) {
                return;
            }

            final File file = new File(path);
            if (file.exists()) {
                try {
                    resourcesManager.load(file, null);
                } catch (Exception e) {
                    AppLogger.error("Unable to autoload recent game resources:" + file, e);
                }
            }
        }
    }

    private void setLookAndFeel() {
        if (Config.getBoolean(Config.KEY_NO_LOOK_AND_FEEL, false)) {
            AppLogger.trace("No L&F settings detected, skipping setting Look & Feel");
            return;
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Config.setBoolean(Config.KEY_NO_LOOK_AND_FEEL, true);
            AppLogger.error("Could not set Look & Feel", e);
        }
    }

    private void setEventQueue() {
        final EventQueue current = Toolkit.getDefaultToolkit().getSystemEventQueue();
        if (current != null) {
            current.push(new AppEventQueue());
        }
    }

    private boolean saveProfile() {
        try {
            if (!profileManager.saveCurrentProfile()) {
                final String currentProfileExt = Config.get(Config.KEY_LAST_PROFILE_EXT, "dat");
                final File file = Dialogs.openFileChooser(
                        mainFrame,
                        "Save profile",
                        new FileNameExtensionFilter("Profile Files", currentProfileExt),
                        new File(Config.get(Config.KEY_LAST_PROFILE, System.getProperty("user.dir"))),
                        false,
                        true);

                if (file == null || !profileManager.saveCurrentProfileAs(file)) {
                    return false;
                }
            }
        } catch (IOException e) {
            AppLogger.error("Failed to save profile", e);
            return false;
        }
        return true;
    }

    public boolean shutdown() {
        if (profileManager.hasProfile() && profileManager.isModified()) {
            final int selection = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Save profile?",
                    "Unsaved changes detected",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (selection == JOptionPane.YES_OPTION && !saveProfile()) {
                return false;
            } else if (selection == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        } else {
            final int sel = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Are you sure you want to close the editor?",
                    "Quit CaveSaveEditor Pro?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (sel != JOptionPane.YES_OPTION) {
                return false;
            }
        }

        System.exit(0);
        return true;
    }

    public boolean terminate() {
        profileManager.clear();
        profileManager.removeAllListeners();

        resourcesManager.clear();
        resourcesManager.removeAllListeners();

        if (mainFrame != null) {
            mainFrame.dispose();
            mainFrame = null;
        }

        if (loadFrame != null) {
            loadFrame.dispose();
            loadFrame = null;
        }

        return true;
    }

    private static void panic(Throwable e) {
        AppLogger.error("Could not load resources", e);
        JOptionPane.showMessageDialog(
                null,
                "Could not load resources!\n" +
                        "Please report this error to developers.\n" +
                        "An exception has occurred:\n" +
                        e,
                "Could not load resources",
                JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }
}
