package com.leo.cse.frontend.ui.components.visual;

import com.leo.cse.frontend.ui.ThemeData;

import java.awt.Graphics;

import javax.swing.JComponent;

public class RectComponent extends JComponent {
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(ThemeData.getForegroundColor());
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
