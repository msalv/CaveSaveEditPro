package com.leo.cse.frontend.ui.components;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignBottom;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.constraints;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.rightMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.util.StringUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

public class RecentFileComponent extends VerticalLayout {
    private final TextLabel stateLabel = new TextLabel();
    private final VerticalLayout contentLayout = new VerticalLayout();
    private final TextLabel pathTitle = new TextLabel();
    private final TextLabel pathLabel = new TextLabel();
    private final TextButton button = new TextButton();

    public RecentFileComponent() {
        super();
        init();
    }

    private void init() {
        setBackground(ThemeData.getHoverColor());

        add(stateLabel);
        add(contentLayout, topMargin(12));

        stateLabel.setFont(Resources.getFont());
        stateLabel.setTextColor(ThemeData.getForegroundColor());

        add(initFooter());
    }

    private Component initFooter() {
        final HorizontalLayout footer = new HorizontalLayout();
        footer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        final VerticalLayout recentFileColumn = new VerticalLayout();
        recentFileColumn.setMinimumSize(new Dimension(0, Integer.MAX_VALUE));

        pathTitle.setFont(Resources.getFont());
        pathTitle.setTextColor(ThemeData.getForegroundColor());
        pathTitle.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        recentFileColumn.add(pathTitle, alignBottom(constraints()), 0);

        pathLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pathLabel.setFont(Resources.getFont());
        pathLabel.setTextColor(ThemeData.getTextColor());
        pathLabel.setSingleLine(true);

        recentFileColumn.add(pathLabel, alignBottom(topMargin(4)), 0);

        button.setMinimumSize(new Dimension(66, 19));

        footer.add(button, alignBottom(alignRight()));
        footer.add(recentFileColumn, rightMargin(12));

        return footer;
    }

    public void setTitle(String text) {
        pathTitle.setText(text);
    }

    public void setRecentFilePath(String path) {
        if (!StringUtils.isNullOrEmpty(path)) {
            pathTitle.setVisible(true);
            pathLabel.setVisible(true);
            pathLabel.setText(path);
        } else {
            pathTitle.setVisible(false);
            pathLabel.setVisible(false);
            pathLabel.setText(null);
        }
    }

    public void setButtonText(String text) {
        button.setText(text);
    }

    public void setButtonClickListener(Runnable clickListener) {
        button.setOnClickListener(clickListener);
    }

    public void setPathClickListener(Runnable clickListener) {
        pathLabel.setOnClickListener(clickListener);
    }

    public void setStateText(String text) {
        stateLabel.setText(text);
    }

    public void setStateColor(Color color) {
        stateLabel.setTextColor(color);
    }

    public void addContentComponent(Component component, Object constraints) {
        contentLayout.add(component, constraints);
    }
}
