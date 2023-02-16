package com.leo.cse.frontend.ui.components.compound;

import com.leo.cse.frontend.ui.Hoverable;
import com.leo.cse.frontend.ui.ThemeData;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class CheckBox extends JComponent implements CompoundButton, Hoverable {
    private boolean isChecked;
    private boolean isHovered;

    private final Image checkedImage = new CheckImage(ThemeData.getForegroundColor());

    public CheckBox() {
        setForeground(ThemeData.getForegroundColor());
    }

    @Override
    public void setHovered(boolean isHovered) {
        if (this.isHovered != isHovered) {
            this.isHovered = isHovered;
            repaint();
        }
    }

    @Override
    public void setChecked(boolean isChecked) {
        if (this.isChecked != isChecked) {
            this.isChecked = isChecked;
            repaint();
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
    public void setOnCheckedStateListener(OnCheckedStateChangedListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isHovered) {
            g.setColor(ThemeData.getHoverColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        final Graphics graphics = getComponentGraphics(g);

        graphics.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        if (isChecked) {
            graphics.drawImage(checkedImage, 0, 0, null);
        }
    }

    private static class CheckImage extends BufferedImage {
        private final Color color;
        private final static int PADDING = 3;

        CheckImage(Color color) {
            super(16, 16, TYPE_INT_ARGB);
            this.color = color;
            init();
        }

        private void init() {
            final int rgb = color.getRGB();
            final int w = getWidth();
            final int l = PADDING;
            final int r = w - l;

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < getHeight(); y++) {
                    if (x == y && x >= l && x < r) {
                        final int xx = w - 1 - y;

                        setRGB(x, y, rgb);
                        setRGB(xx, y, rgb);

                        if (x < r - 1) {
                            setRGB(x + 1, y, rgb);
                            setRGB(x, y + 1, rgb);

                            setRGB(xx - 1, y, rgb);
                            setRGB(xx, y + 1, rgb);
                        }
                    }
                }
            }
        }
    }
}
