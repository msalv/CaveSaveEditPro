package com.leo.cse.backend.profile;

import com.leo.cse.util.async.IndeterminateTask;
import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.profile.model.NormalProfile;
import com.leo.cse.backend.profile.model.PlusProfile;
import com.leo.cse.backend.profile.model.Profile;
import com.leo.cse.backend.profile.exceptions.ProfileFieldException;
import com.leo.cse.dto.factory.ProfileFactory;
import com.leo.cse.backend.profile.undo.ProfileEdit;
import com.leo.cse.backend.mci.MCI;
import com.leo.cse.backend.mci.MCIFactory;
import com.leo.cse.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import javax.swing.undo.UndoManager;

public class ProfileManager {
    private static volatile ProfileManager sInstance;

    private Profile currentProfile;
    private MCI mci;
    private boolean isModified = false;
    private File file;
    private UndoManager undoManager;

    private final MCI fallbackMCI;

    private final Queue<ProfileStateChangeListener> listeners = new ConcurrentLinkedQueue<>();

    private ProfileManager() {
        try {
            fallbackMCI = MCIFactory.createDefault();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ProfileManager getInstance() {
        ProfileManager localInstance = sInstance;

        if (localInstance == null) {
            synchronized (ProfileManager.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    sInstance = localInstance = new ProfileManager();
                }
            }
        }
        return localInstance;
    }

    public MCI getCurrentMCI() {
        if (mci != null) {
            return mci;
        }
        return fallbackMCI;
    }

    public void setCurrentMCI(MCI mci) {
        this.mci = mci;
    }

    public boolean hasProfile() {
        return currentProfile != null;
    }

    public boolean isCurrentProfilePlus() {
        return currentProfile instanceof PlusProfile;
    }

    public boolean isModified() {
        if (!hasProfile()) {
            return false;
        }
        if (getCurrentFilePath() == null) {
            return true;
        }
        return isModified;
    }

    public String getCurrentFilePath() {
        return (file != null && file.exists()) ? file.getAbsolutePath() : null;
    }

    public void createProfile(MCI mci, Consumer<Profile> callback) throws Exception {
        this.mci = mci;
        try {
            this.currentProfile = (Profile) mci.getProfileClass().newInstance();
        } catch (Exception ex) {
            AppLogger.error("Profile class could not be initialized: "
                    + mci.getProfileClassName() + "\n" +
                    "Using default NormalProfile class instead", ex);
            this.currentProfile = new NormalProfile();
            this.mci = MCIFactory.createDefault();
        }
        onProfileCreated(currentProfile, null, callback);
    }

    public void loadProfile(final File file, Consumer<Profile> callback) {
        new IndeterminateTask<Profile>() {
            @Override
            protected Profile doInBackground() throws Exception {
                final Profile profile = ProfileFactory.fromFile(file);
                mci = MCIFactory.fromProfile(profile);
                return profile;
            }

            @Override
            protected void onPostExecute(Profile profile) {
                onProfileCreated(profile, file, callback);
            }
        }.execute();
    }

    private void onProfileCreated(Profile profile, File file, Consumer<Profile> callback) {
        currentProfile = profile;
        undoManager = new UndoManager();
        isModified = false;
        this.file = file;

        if (callback != null) {
            callback.accept(profile);
        }

        notifyListeners(ProfileStateEvent.LOADED);
    }

    public boolean saveCurrentProfile() throws IOException {
        if (file != null) {
            return saveCurrentProfileAs(file);
        }
        return false;
    }

    public boolean saveCurrentProfileAs(File file) throws IOException {
        if (currentProfile == null) {
            return false;
        }

        this.file = file;

        final byte[] data = currentProfile.getData();

        final Path backupPath;
        if (file.exists()) {
            backupPath = backup(file); // back up file just in case
        } else {
            file.createNewFile(); // create file to write to
            backupPath = null;
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        } catch (Exception e) {
            AppLogger.error("Error while saving profile!", e);
            if (backupPath != null) {
                restore(file, backupPath);
            }
            return false;
        }

        isModified = false;

        notifyListeners(ProfileStateEvent.SAVED);

        return true;
    }

    private Path backup(File file) throws IOException {
        return Files.copy(
                file.toPath(),
                FileSystems.getDefault().getPath(file.getAbsolutePath() + ".bkp"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    private void restore(File file, Path backupPath) {
        AppLogger.error("Attempting to restore from backup...");
        try {
            Files.copy(
                    backupPath,
                    file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            AppLogger.error("Successfully restored backup!");
        } catch (Exception e) {
            AppLogger.error("Error while recovering backup!", e);
        }
    }

    public boolean canUndo() {
        return undoManager != null && undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager != null && undoManager.canRedo();
    }

    public void undo() {
        if (undoManager != null && undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    public void redo() {
        if (undoManager != null && undoManager.canRedo()) {
            undoManager.redo();
        }
    }

    public void clear() {
        mci = null;
        currentProfile = null;
        undoManager = null;
        isModified = false;
        file = null;
        notifyListeners(ProfileStateEvent.UNLOADED);
    }

    public void setField(String field, int index, Object value, boolean canUndo) {
        if (!hasProfile()) {
            return;
        }

        final Object oldValue = internalSetField(field, index, value);
        final boolean changed = !Objects.deepEquals(oldValue, value);

        if (changed) {
            isModified = true;
            final ProfileEdit edit = new ProfileEdit(this, field, index, oldValue, value);
            if (canUndo) {
                undoManager.addEdit(edit);
            }
            notifyListeners(ProfileStateEvent.MODIFIED, edit);
        }
    }

    /**
     * Sets a field's value.
     *
     * @param field field to set
     * @param index field's index
     * @param value value to set
     */
    public void setField(String field, int index, Object value) {
        setField(field, index, value, true);
    }

    /**
     * Sets a field's value.
     *
     * @param field field to set
     * @param value value to set
     */
    public void setField(String field, Object value) {
        setField(field, Profile.NO_INDEX, value);
    }

    private Object internalSetField(String field, int index, Object value) {
        try {
            final Object oldValue = currentProfile.getField(field, index);
            currentProfile.setField(field, index, value);

            if (index != Profile.NO_INDEX) {
                AppLogger.info(String.format("setting field %s[%d] to %s", field, index, StringUtils.toString(value)));
            } else {
                AppLogger.info(String.format("setting field %s to %s", field, StringUtils.toString(value)));
            }

            return oldValue;
        } catch (ProfileFieldException ex) {
            AppLogger.error(String.format("Unable to set field: %s", field));
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets a field's value.
     *
     * @param field A field to get
     * @param index Index to get. Will be ignored if the field doesn't have indexes
     * @return value of the field
     */
    public Object getField(String field, int index) {
        if (!hasProfile()) {
            return null;
        }
        try {
            return currentProfile.getField(field, index);
        } catch (ProfileFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a field's value.
     *
     * @param field field to get
     * @return value of the field
     */
    public Object getField(String field) {
        return getField(field, Profile.NO_INDEX);
    }

    public Short getShortField(String field) {
        return (Short) getField(field);
    }

    public Short getShortField(String field, int index) {
        return (Short) getField(field, index);
    }

    public Integer getIntField(String field) {
        return (Integer) getField(field);
    }

    public Integer getIntField(String field, int index) {
        return (Integer) getField(field, index);
    }

    public Long getLongField(String field) {
        return (Long) getField(field);
    }

    public Boolean getBooleanField(String field) {
        return Boolean.TRUE.equals(getField(field));
    }

    public Boolean getBooleanField(String field, int index) {
        return Boolean.TRUE.equals(getField(field, index));
    }

    public Byte getByteField(String field) {
        return (Byte) getField(field);
    }

    public String getStringOfField(String field) {
        return String.valueOf(getField(field));
    }

    public int getCurrentMapId() {
        Integer map = null;
        if (hasProfile()) {
            map = getIntField(ProfileFields.FIELD_MAP);
        }
        if (map == null) {
            return -1;
        }
        return map;
    }

    public Short[] getPlayerPosition() {
        return (Short[]) getField(ProfileFields.FIELD_POSITION);
    }

    public PlusProfileManager getPlusProfileManager() {
        return isCurrentProfilePlus() ? new PlusProfileManager((PlusProfile) currentProfile) : null;
    }

    public int getCurrentSlotId() {
        return isCurrentProfilePlus()
                ? ((PlusProfile) currentProfile).getCurrentSlotId()
                : 0;
    }

    public void addListener(ProfileStateChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ProfileStateChangeListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    private void notifyListeners(ProfileStateEvent event) {
        notifyListeners(event, null);
    }

    private void notifyListeners(ProfileStateEvent event, Object payload) {
        for (ProfileStateChangeListener l : listeners) {
            l.onProfileStateChanged(event, payload);
        }
    }

    public void notifyMCIChanged() {
        if (hasProfile()) {
            notifyListeners(ProfileStateEvent.MCI_CHANGED);
        }
    }

    public void notifySlotChanged(boolean exists) {
        if (hasProfile()) {
            notifyListeners(ProfileStateEvent.SLOT_CHANGED, exists);
        }
    }
}
