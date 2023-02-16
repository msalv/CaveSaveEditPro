package com.leo.cse.frontend.ui.components.text;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;

public class TextButton extends TextLabel {
    public TextButton() {
        super();
        setPadding(6, 2, 6, 3);
        setBorderWidth(1);
        setSingleLine(true);
        setFont(Resources.getFont());
        setTextColor(ThemeData.getForegroundColor());
        setGravity(Gravity.CENTER_HORIZONTAL);
    }
}
