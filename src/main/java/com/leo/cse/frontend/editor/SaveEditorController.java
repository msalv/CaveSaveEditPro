package com.leo.cse.frontend.editor;

import com.leo.cse.util.async.IndeterminateAsyncTaskCallback;
import com.leo.cse.backend.exe.ExePointers;
import com.leo.cse.dto.StartPoint;
import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileFlags;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.exceptions.ProfileFieldException;
import com.leo.cse.backend.res.GameResources;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.GameLauncher;
import com.leo.cse.frontend.UpdatesChecker;
import com.leo.cse.frontend.Version;
import com.leo.cse.backend.mci.MCI;
import com.leo.cse.backend.mci.MCIFactory;
import com.leo.cse.frontend.frames.LoadFrame;
import com.leo.cse.frontend.dialogs.AboutDialog;
import com.leo.cse.frontend.dialogs.PlusSlotsDialog;
import com.leo.cse.frontend.dialogs.SettingsDialog;
import com.leo.cse.frontend.dialogs.niku.NikuEditDialog;
import com.leo.cse.frontend.editor.selectors.SavePointsSelectionDialog;
import com.leo.cse.util.Dialogs;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SaveEditorController {
    private static final int FILTER_ID_EXE = 0;
    private static final int FILTER_ID_TBL = 1;
    private static final int FILTER_ID_MRMAP = 2;
    private static final int FILTER_ID_NX = 3;

    private static final FileFilter[] MOD_FILE_FILTERS = new FileFilter[4];

    static {
        MOD_FILE_FILTERS[FILTER_ID_EXE] = new FileNameExtensionFilter("Executables (*.exe)", "exe");
        MOD_FILE_FILTERS[FILTER_ID_TBL] = new StageTableFileFilter("CS+ stage table (stage.tbl)", "stage.tbl");
        MOD_FILE_FILTERS[FILTER_ID_MRMAP] = new StageTableFileFilter("Doukutsu-rs/CSE2E stage table (mrmap.bin)", "mrmap.bin");
        MOD_FILE_FILTERS[FILTER_ID_NX] = new StageTableFileFilter("NXEngine stage table (stage.dat)", "stage.dat");
    }

    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    public SaveEditorController(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
    }

    public void createFileDefault(Component parentComponent) {
        try {
            createFile(parentComponent, MCIFactory.createDefault());
        } catch (Exception e) {
            showUnableToCreateFileMessage(parentComponent, e);
        }
    }

    public void createFilePlus(Component parentComponent) {
        try {
            createFile(parentComponent, MCIFactory.createPlus());
        } catch (Exception e) {
            showUnableToCreateFileMessage(parentComponent, e);
        }
    }

    private void createFile(Component parentComponent, MCI mci) throws Exception {
        if (profileManager.isModified() && showUnsavedChangesWarning(parentComponent)) {
            return;
        }

        profileManager.createProfile(mci, profile -> {
            final StartPoint startPoint = resourcesManager.hasResources()
                    ? resourcesManager.getResources().getStartPoint()
                    : StartPoint.DEFAULT;

            try {
                profile.reset(startPoint);
            } catch (ProfileFieldException e) {
                AppLogger.error("Unable to reset new profile", e);
            }
        });
    }

    private void showUnableToCreateFileMessage(Component parentComponent, Exception e) {
        AppLogger.error("Failed to create a new profile", e);
        JOptionPane.showMessageDialog(
                parentComponent,
                "An error occurred while creating a new profile file:\n" + e,
                "Could not create profile file",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private boolean showUnsavedChangesWarning(Component parentComponent) {
        final int sel = JOptionPane.showConfirmDialog(parentComponent,
                "Are you sure you want to load a new profile?\nUnsaved changes will be lost!",
                "Unsaved changes detected",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        return sel == JOptionPane.CANCEL_OPTION;
    }

    private boolean showCloseWarning(Component parentComponent) {
        final int sel = JOptionPane.showConfirmDialog(parentComponent,
                "Are you sure you want to close current profile?\nUnsaved changes will be lost!",
                "Unsaved changes detected",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        return sel == JOptionPane.CANCEL_OPTION;
    }

    private File openFileChooser(Component parentComponent) {
        File dir = new File(Config.get(Config.KEY_LAST_PROFILE, System.getProperty("user.dir")));
        if (!dir.exists()) {
            dir = new File(System.getProperty("user.dir"));
        }

        final String currentProfileExt = Config.get(Config.KEY_LAST_PROFILE_EXT, "dat");
        return Dialogs.openFileChooser(parentComponent,
                "Open profile",
                new FileNameExtensionFilter(String.format("Profile Files (*.%s)", currentProfileExt), currentProfileExt),
                dir,
                false,
                false);
    }

    public void openFile(Component parentComponent) {
        if (profileManager.isModified() && showUnsavedChangesWarning(parentComponent)) {
            return;
        }

        final File file = openFileChooser(parentComponent);

        if (file == null || !file.exists()) {
            return;
        }

        try {
            profileManager.loadProfile(file, () -> {
                AppLogger.info(String.format("Loaded profile %s", profileManager.getCurrentFilePath()));

                Config.set(Config.KEY_LAST_PROFILE, file.getAbsolutePath());
                if (Config.get(Config.KEY_LAST_MOD, null) == null) {
                    Config.set(Config.KEY_LAST_MOD, file.getAbsolutePath());
                }
            });
        } catch (Exception e) {
            AppLogger.error("Profile loading failed.", e);
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while loading the profile file:\n" + e,
                    "Could not load profile file!",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void closeFile(Component parentComponent) {
        if (profileManager.isModified() && showCloseWarning(parentComponent)) {
            return;
        }
        profileManager.clear();
    }

    public void loadResources(Component parentComponent) {
        final File resourcesFile = chooseResourcesFile(parentComponent);
        if (resourcesFile == null) {
            return;
        }
        if (!resourcesFile.exists()) {
            showResourcesFileErrorMessage(resourcesFile);
            return;
        }

        resourcesManager.load(resourcesFile, this::updateProfileExtension);
    }

    private void updateProfileExtension(GameResources gameResources) {
        final String filename = gameResources.getExeString(ExePointers.PROFILE_NAME_PTR);
        final int extPosition = filename.lastIndexOf('.');
        if (extPosition > -1) {
            Config.set(Config.KEY_LAST_PROFILE_EXT, filename.substring(extPosition + 1));
        }
    }

    private File chooseResourcesFile(Component parentComponent) {
        int type = FILTER_ID_EXE;
        File dir = new File(Config.get(Config.KEY_LAST_MOD, System.getProperty("user.dir")));
        if (!dir.exists()) {
            dir = new File(System.getProperty("user.dir"));
        }
        if (dir.getAbsolutePath().endsWith(".tbl")) {
            type = FILTER_ID_TBL;
        } else if (dir.getAbsolutePath().endsWith(".bin")) {
            type = FILTER_ID_MRMAP;
        } else if (dir.getAbsolutePath().endsWith(".dat")) {
            type = FILTER_ID_NX;
        }
        File base = null;
        while (base == null || !base.exists()) {
            final File selected = Dialogs.openFileChooser(
                    parentComponent,
                    "Open game or stage table",
                    MOD_FILE_FILTERS,
                    type,
                    (base == null ? dir : base),
                    false,
                    false);
            if (selected != null) {
                base = selected;
            } else {
                return null;
            }
        }
        return base;
    }

    private void showResourcesFileErrorMessage(File file) {
        final String fileName = file.getName();
        JOptionPane.showMessageDialog(
                null,
                String.format("Game base file \"%s\" does not exist!", fileName),
                "Executable does not exist",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void clearResources() {
        resourcesManager.clear();
    }

    public void saveProfile(Component parentComponent) {
        if (!profileManager.hasProfile()) {
            showNoProfileErrorMessage();
            return;
        }

        if (profileManager.getCurrentFilePath() == null) {
            saveProfileAs(parentComponent);
            return;
        }

        profileManager.setField(ProfileFields.FIELD_FLAGS, ProfileFlags.SAVED, true);

        try {
            profileManager.saveCurrentProfile();
        } catch (IOException e1) {
            AppLogger.error("Failed to save profile", e1);
            JOptionPane.showMessageDialog(
                    parentComponent,
                    "An error occurred while saving the profile file:\n" + e1,
                    "Could not save profile file",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void saveProfileAs(Component parentComponent) {
        if (!profileManager.hasProfile()) {
            showNoProfileErrorMessage();
            return;
        }

        final String currentProfileExt = Config.get(Config.KEY_LAST_PROFILE_EXT, "dat");
        final File file = Dialogs.openFileChooser(
                parentComponent,
                "Save profile",
                new FileNameExtensionFilter("Profile Files", currentProfileExt),
                new File(Config.get(Config.KEY_LAST_PROFILE, System.getProperty("user.dir"))),
                false,
                true);

        if (file == null) {
            return;
        }

        if (file.exists()) {
            final int answer = JOptionPane.showConfirmDialog(
                    parentComponent,
                    "Are you sure you want to overwrite this file?",
                    "Overwrite confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }

        profileManager.setField(ProfileFields.FIELD_FLAGS, ProfileFlags.SAVED, true);

        try {
            if (profileManager.saveCurrentProfileAs(file)) {
                Config.set(Config.KEY_LAST_PROFILE, file.getAbsolutePath());
            }
        } catch (IOException e) {
            AppLogger.error("Failed to save profile", e);
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while saving the profile file:\n" + e,
                    "Could not save profile file",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showNoProfileErrorMessage() {
        JOptionPane.showMessageDialog(
                null,
                "There is no profile to save!\nPlease open a profile file",
                "No profile to save",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void launchGame() {
        new GameLauncher(resourcesManager).launch();
    }

    public void selectSavePoint() {
        new SavePointsSelectionDialog(profileManager, resourcesManager).select();
    }

    public void selectSaveSlot(Frame parentFrame, Component parentComponent) {
        final PlusSlotsDialog dialog = new PlusSlotsDialog(parentFrame,
                parentComponent,
                profileManager,
                resourcesManager);

        dialog.setVisible(true);
        dialog.dispose();
    }

    public void openSettings(Frame parentFrame, Component parentComponent) {
        final SettingsDialog dialog = new SettingsDialog(
                parentFrame,
                parentComponent,
                profileManager,
                resourcesManager);
        dialog.setVisible(true);
        dialog.dispose();
    }

    public void showAboutDialog(Frame parentFrame, Component parentComponent) {
        final AboutDialog dialog = new AboutDialog(parentFrame, parentComponent);
        dialog.setVisible(true);
        dialog.dispose();
    }

    public void showNikuEditorDialog(Frame parentFrame, Component parentComponent) {
        final NikuEditDialog dialog = new NikuEditDialog(
                parentFrame,
                parentComponent,
                profileManager,
                resourcesManager);
        dialog.setVisible(true);
        dialog.dispose();
    }

    public void checkUpdates() {
        final LoadFrame loadFrame = new LoadFrame();

        UpdatesChecker.check(true, new IndeterminateAsyncTaskCallback<Version>() {
            @Override
            public void onPreExecute() {
                loadFrame.setText("Checking for updates...");
            }

            @Override
            public void onPostExecute(Version version) {
                loadFrame.dispose();
            }
        });
    }

    private static class StageTableFileFilter extends FileFilter {
        private final String filename;
        private final String description;

        public StageTableFileFilter(String description, String filename) {
            this.filename = filename;
            this.description = description;
        }

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().equalsIgnoreCase(filename);
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
