package com.leo.cse.frontend.ui.components.menu;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

public class MenuBarItem extends JComponent {
    private boolean hovered = false;
    private String text;

    private Runnable clickListener;
    private Runnable hoverListener;

    public MenuBarItem() {
        super();

        setForeground(ThemeData.getTextColor());
        setFont(Resources.getFontPixel());

        setBorder(new EmptyBorder(0, 4, 0, 4));

        addMouseListener(new MouseEventsListener());
    }

    public void setText(String text) {
        if (this.text == null || !this.text.equals(text)) {
            this.text = text;
            repaint();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) {
            return;
        }

        super.setEnabled(enabled);

        if (enabled) {
            setForeground(ThemeData.getTextColor());
        } else {
            hovered = false;
            setForeground(ThemeData.getTextColorDisabled());
        }

        repaint();
    }

    public void setOnClickListener(Runnable action) {
        clickListener = action;
    }

    public void setOnHoverListener(Runnable hoverListener) {
        this.hoverListener = hoverListener;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (hovered) {
            g.setColor(ThemeData.getHoverColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        final Insets insets = getInsets();
        final int x = insets.left;
        final int top = insets.top;
        final int bottom = getHeight() - 1 - insets.bottom;

        final Graphics graphics = getComponentGraphics(g);
        final int y = top + (bottom - top + graphics.getFontMetrics().getHeight()) / 2;
        graphics.drawString(text, x, y);
    }

    private class MouseEventsListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isEnabled() && clickListener != null) {
                clickListener.run();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (isEnabled() && clickListener != null) {
                hovered = true;
                if (hoverListener != null) {
                    hoverListener.run();
                }
                repaint();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (isEnabled() && clickListener != null) {
                hovered = false;
                repaint();
            }
        }
    }
}
