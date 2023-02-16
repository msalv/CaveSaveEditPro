package com.leo.cse.frontend.frames;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.layout.ValidateRootPanel;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class LoadFrame extends JFrame {
    private final static Dimension sSize = new Dimension(332, 112);

    private final TextLabel textLabel = new TextLabel();

    public LoadFrame() {
        setIconImages(Resources.getAppIcons());
        setPreferredSize(sSize);
        setMaximumSize(sSize);
        setMinimumSize(sSize);
        setUndecorated(true);

        final JPanel panel = new ValidateRootPanel();
        panel.setPreferredSize(sSize);
        panel.setBackground(ThemeData.getBackgroundColor());
        panel.add(initTextLabel());
        add(panel);

        panel.setBorder(new LineBorder(ThemeData.getForegroundColor(), 1));

        pack();

        setLocationRelativeTo(null);
        setVisible(true);
        requestFocus();
    }

    private Component initTextLabel() {
        textLabel.setPreferredSize(sSize);
        textLabel.setTextColor(ThemeData.getTextColor());
        textLabel.setFont(Resources.getFontPixel().deriveFont(15f));
        textLabel.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        textLabel.setSingleLine(true);
        return textLabel;
    }

    public void setText(String text) {
        textLabel.setText(text);
    }
}
