package com.leo.cse.frontend.ui.components.compound;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.rightMargin;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.Hoverable;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

abstract public class LabeledCompoundButton extends HorizontalLayout implements
        CompoundButton,
        Hoverable {
    private boolean isChecked;
    private boolean hovered;
    private OnCheckedStateChangedListener listener;

    private final Component button;
    private final TextLabel label = new TextLabel();

    LabeledCompoundButton(Component button) {
        this.button = button;
        button.setPreferredSize(new Dimension(16, 16));
        add(button, rightMargin(8));

        label.setMinimumSize(new Dimension(0, 16));
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setTextColor(ThemeData.getForegroundColor());
        label.setFont(Resources.getFont());
        label.setEnabled(false);
        label.setTextColorDisabled(ThemeData.getForegroundColor());
        add(label);

        addMouseListener(new MouseEventsListener());
    }

    public void setText(String text) {
        label.setText(text);
    }

    public void setSingleLine(boolean isSingleLine) {
        label.setSingleLine(isSingleLine);
    }

    @Override
    public void setChecked(boolean isChecked) {
        setChecked(isChecked, false);
    }

    protected void setChecked(boolean isChecked, boolean notify) {
        if (this.isChecked != isChecked) {
            this.isChecked = isChecked;
            if (button instanceof CompoundButton) {
                ((CompoundButton) button).setChecked(isChecked);
            }
            if (notify && listener != null) {
                listener.onCheckedStateChanged(this, isChecked);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) {
            return;
        }

        super.setEnabled(enabled);

        button.setEnabled(enabled);
        label.setEnabled(enabled);

        if (!enabled) {
            setHovered(false);
        }

        repaint();
    }

    @Override
    public void setHovered(boolean isHovered) {
        if (hovered != isHovered) {
            hovered = isHovered;
            if (button instanceof Hoverable) {
                ((Hoverable) button).setHovered(isHovered);
            }
            repaint();
        }
    }

    @Override
    public void setOnCheckedStateListener(OnCheckedStateChangedListener listener) {
        this.listener = listener;
    }

    private class MouseEventsListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isEnabled()) {
                setChecked(!isChecked, true);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (isEnabled()) {
                setHovered(true);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (isEnabled()) {
                setHovered(false);
            }
        }
    }
}
