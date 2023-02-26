package com.leo.cse.frontend.ui.components.compound;

public interface CompoundButton {
    void setChecked(boolean isChecked);
    boolean isChecked();

    void setOnCheckedStateListener(OnCheckedStateChangedListener listener);
}
