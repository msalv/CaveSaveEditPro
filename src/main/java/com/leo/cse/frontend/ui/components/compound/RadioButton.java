package com.leo.cse.frontend.ui.components.compound;

import com.leo.cse.frontend.ui.Hoverable;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.util.GraphicsHelper;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class RadioButton extends JComponent implements CompoundButton, Hoverable {
    private boolean isChecked;
    private boolean isHovered;

    public RadioButton() {
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
    public void setOnCheckedStateListener(OnCheckedStateChangedListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        GraphicsHelper.applyQualityRenderingHints((Graphics2D) g);

        if (isHovered) {
            g.setColor(ThemeData.getHoverColor());
            g.fillOval(0, 0, getWidth(), getHeight());
        }

        final Graphics graphics = getComponentGraphics(g);

        graphics.drawOval(1, 1, getWidth() - 2, getHeight() - 2);

        if (isChecked) {
            graphics.fillOval(4, 4, getWidth() - 8, getHeight() - 8);
        }
    }
}
