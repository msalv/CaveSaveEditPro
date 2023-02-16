package com.leo.cse.frontend.ui.components.list;

import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.border.LineBorder;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class RetroScrollBarUI extends BasicScrollBarUI {
    private static final int SCROLLBAR_WIDTH = 24;
    private static final int ARROW_BUTTON_SIZE = 24;

    @Override
    protected void installDefaults() {
        scrollBarWidth = SCROLLBAR_WIDTH;

        minimumThumbSize = new Dimension(8, 8);
        maximumThumbSize = new Dimension(4096, 4096);

        if (scrollbar.getLayout() == null ||
                (scrollbar.getLayout() instanceof UIResource)) {
            scrollbar.setLayout(this);
        }

        scrollbar.setBorder(new LineBorderImpl(ThemeData.getForegroundColor(), scrollbar.getOrientation()));

        configureScrollBarColors();
    }

    @Override
    protected void configureScrollBarColors() {
        final Color foreground = ThemeData.getForegroundColor();
        final Color background = ThemeData.getBackgroundColor();

        scrollbar.setBackground(background);
        scrollbar.setForeground(foreground);

        thumbHighlightColor = foreground;
        thumbLightShadowColor = foreground;
        thumbDarkShadowColor = background;

        thumbColor = background;
        trackColor = background;
        trackHighlightColor = ThemeData.getHoverColor();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        final Color savedThumbColor = this.thumbColor;

        if (isThumbRollover()) {
            this.thumbColor = trackHighlightColor;
        }

        super.paintThumb(g, c, thumbBounds);

        this.thumbColor = savedThumbColor;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new RetroArrowButton(orientation);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new RetroArrowButton(orientation);
    }

    private class RetroArrowButton extends JButton {
        private final int direction;
        private boolean hovered;

        RetroArrowButton(int direction) {
            this.direction = direction;
            addMouseListener(new MouseEventsListener());
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(ThemeData.getBackgroundColor());
            g.fillRect(0, 0, getWidth(), getHeight());

            if (hovered) {
                g.setColor(ThemeData.getHoverColor());
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            g.setColor(ThemeData.getForegroundColor());

            if (direction == NORTH || direction == SOUTH) {
                final int lineY = (direction == NORTH) ? getHeight() - 1 : 0;
                g.drawLine(0, lineY, getWidth(), lineY);
            } else {
                final int lineX = (direction == WEST) ? getWidth() - 1 : 0;
                g.drawLine(lineX, 0, lineX, getHeight());
            }

            final Image icon = Resources.getArrowIcon(direction);
            final int iconX = (getWidth() - icon.getWidth(null)) / 2;
            final int iconY = (getHeight() - icon.getHeight(null)) / 2;
            g.drawImage(icon, iconX, iconY, null);
        }

        @Override
        public Dimension getPreferredSize() {
            int size = ARROW_BUTTON_SIZE;
            if (scrollbar != null) {
                switch (scrollbar.getOrientation()) {
                    case JScrollBar.VERTICAL:
                        size = scrollbar.getWidth();
                        break;
                    case JScrollBar.HORIZONTAL:
                        size = scrollbar.getHeight();
                        break;
                }
                size = Math.max(size, 5);
            }
            return new Dimension(size, size);
        }

        @Override
        public void setEnabled(boolean enabled) {
            if (isEnabled() == enabled) {
                return;
            }

            super.setEnabled(enabled);

            if (!enabled && hovered) {
                hovered = false;
                repaint();
            }
        }

        private class MouseEventsListener extends MouseAdapter {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    hovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    hovered = false;
                    repaint();
                }
            }
        }
    }

    private static final class LineBorderImpl extends LineBorder {
        private final int orientation;

        LineBorderImpl(Color color, int orientation) {
            super(color);
            this.orientation = orientation;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if (orientation == VERTICAL) {
                super.paintBorder(c, g, x, y - 1, width + 1, height + 2);
            } else {
                super.paintBorder(c, g, x - 1, y, width + 2, height + 1);
            }
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            if (orientation == VERTICAL) {
                insets.set(0, thickness, 0, 0);
            } else {
                insets.set(thickness, 0, 0, 0);
            }
            return insets;
        }
    }
}
