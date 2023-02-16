package com.leo.cse.frontend.ui.components.menu;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class OptionsMenuItem extends JComponent {
    private boolean hovered = false;

    private Image icon;
    private String text;
    private String hotKey;

    private Runnable clickListener;

    public OptionsMenuItem() {
        super();

        setForeground(ThemeData.getTextColor());
        setFont(Resources.getFontPixel());
        setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        setBorder(new EmptyBorder(0, 4, 0, 4));

        addMouseListener(new MouseEventsListener());
    }

    public void setText(String text) {
        if (this.text == null || !this.text.equals(text)) {
            this.text = text;
            repaint();
        }
    }

    public void setIcon(Image icon) {
        if (this.icon == null || !this.icon.equals(icon)) {
            this.icon = icon;
            repaint();
        }
    }

    public void setHotKey(String hotKey) {
        if (this.hotKey == null || !this.hotKey.equals(hotKey)) {
            this.hotKey = hotKey;
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (hovered) {
            g.setColor(ThemeData.getHoverColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        final Insets insets = getInsets();
        final int left = insets.left;
        final int top = insets.top;
        final int bottom = getHeight() - insets.bottom;
        final int right = getWidth() - insets.right;

        if (icon != null) {
            g.drawImage(icon, left, (bottom - top - icon.getHeight(null)) / 2,null);
        }

        final Graphics graphics = getComponentGraphics(g);
        final int x = left + 22;
        final int y = top + (bottom - 1 - top + graphics.getFontMetrics().getHeight()) / 2;
        graphics.drawString(text, x, y);

        if (hotKey != null && !hotKey.isEmpty()) {
            g.setColor(ThemeData.getTextColorSecondary());
            g.setFont(Resources.getFont().deriveFont(12f));
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            final int stringWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), hotKey);
            final int hx = right - stringWidth;
            final int hy = top + (bottom - 1 - top + g.getFontMetrics().getHeight()) / 2;
            graphics.drawString(hotKey, hx, hy - 1);
        }
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
