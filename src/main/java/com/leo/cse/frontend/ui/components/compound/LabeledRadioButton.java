package com.leo.cse.frontend.ui.components.compound;

public class LabeledRadioButton extends LabeledCompoundButton {

    public LabeledRadioButton() {
        super(new RadioButton());
    }

    @Override
    protected void setChecked(boolean isChecked, boolean notify) {
        if (notify) {
            if (!isChecked()) {
                super.setChecked(isChecked, true);
            }
        } else {
            super.setChecked(isChecked, false);
        }
    }
}
