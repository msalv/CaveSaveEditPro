package com.leo.cse.backend.profile.undo;

import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.profile.ProfileManager;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * Represents an edit to a field in the profile.
 *
 * @author Leo
 */
public class ProfileEdit implements UndoableEdit {
    /**
     * Profile Manager instance that accepts undo and redo actions
     */
    private final ProfileManager profileManager;

    /**
     * The name of the field that was modified.
     */
    private String field;

    /**
     * The index of the field that was modified.
     */
    private final int index;

    /**
     * The field's old value.
     */
    private Object oldVal;

    /**
     * The field's new value.
     */
    private Object newVal;

    /**
     * If <code>true</code>, this edit has been undone and thus can be redone.
     */
    private boolean hasBeenUndone;

    /**
     * Constructs a new <code>ProfileEdit</code>.
     *
     * @param profileManager ProfileManager instance
     * @param field  the name of the field that was modified
     * @param index  the index of the field that was modified
     * @param oldVal the field's old value
     * @param newVal the field's new value
     */
    public ProfileEdit(ProfileManager profileManager, String field, int index, Object oldVal, Object newVal) {
        this.profileManager = profileManager;
        this.field = field;
        this.index = index;
        this.oldVal = oldVal;
        this.newVal = newVal;
    }

    public String getField() {
        return field;
    }

    public int getIndex() {
        return index;
    }

    public Object newValue() {
        return newVal;
    }

    @Override
    public void undo() throws CannotUndoException {
        AppLogger.trace("Attempting to undo: " + getUndoPresentationName());
        try {
            profileManager.setField(field, index, oldVal, false);
        } catch (Exception e) {
            AppLogger.error("Error while undoing: " + getUndoPresentationName(), e);
            throw new CannotUndoException();
        }
        hasBeenUndone = true;
    }

    @Override
    public boolean canUndo() {
        return !hasBeenUndone;
    }

    @Override
    public void redo() throws CannotRedoException {
        AppLogger.trace("Attempting to redo: " + getRedoPresentationName());
        try {
            profileManager.setField(field, index, newVal, false);
        } catch (Exception e) {
            AppLogger.error("Error while redoing: " + getRedoPresentationName(), e);
            throw new CannotRedoException();
        }
        hasBeenUndone = false;
    }

    @Override
    public boolean canRedo() {
        return hasBeenUndone;
    }

    @Override
    public void die() {
        field = null;
        oldVal = null;
        newVal = null;
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public String getPresentationName() {
        String fn = field;
        if (index > -1) {
            fn += "[" + index + "]";
        }
        return fn;
    }

    @Override
    public String getUndoPresentationName() {
        final String pn = getPresentationName();
        return "undo " + pn + " from " + newVal + " to " + oldVal;
    }

    @Override
    public String getRedoPresentationName() {
        final String pn = getPresentationName();
        return "redo " + pn + " from " + oldVal + " to " + newVal;
    }

}
