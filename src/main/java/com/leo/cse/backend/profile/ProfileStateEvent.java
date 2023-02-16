package com.leo.cse.backend.profile;

public enum ProfileStateEvent {
    /**
     * Used to notify {@link ProfileStateChangeListener}s of the current profile being
     * modified. Basically a generic "catch-all" event for when something
     * unspecified happens.
     */
    MODIFIED,

    /**
     * Used to notify {@link ProfileStateChangeListener}s of the current profile being
     * saved.
     */
    SAVED,

    /**
     * Used to notify {@link ProfileStateChangeListener}s of a new profile being loaded.
     */
    LOADED,

    /**
     * Used to notify {@link ProfileStateChangeListener}s of the current profile being
     * unloaded.
     */
    UNLOADED,

    /**
     * Used to notify @link ProfileStateChangeListener}s of the current CS+ slot being
     * changed
     */
    SLOT_CHANGED,

    /**
     * Used to notify @link ProfileStateChangeListener}s of the current MCI being changed
     */
    MCI_CHANGED
}
