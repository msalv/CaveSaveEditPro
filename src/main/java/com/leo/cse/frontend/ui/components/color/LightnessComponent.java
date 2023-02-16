package com.leo.cse.frontend.ui.components.color;

import com.leo.cse.frontend.ui.ThemeData;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

public class LightnessComponent extends JComponent {
    private final static int POINTER_WIDTH = 3;

    private GradientPaint paint;
    private OnLightnessChangedListener onLightnessChangedListener;
    private float selectedLightness = 0.5f;
    private int pointerPosition = 0;

    public LightnessComponent() {
        super();
        final MouseListener mouseListener = new MouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D graphics = (Graphics2D) g;
        graphics.setPaint(getPaint());
        graphics.fillRect(0, 0, getWidth(), getHeight());

        graphics.setPaint(ThemeData.getForegroundColor());
        graphics.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        graphics.setPaint(Color.BLACK);
        graphics.drawRect(pointerPosition, 0, POINTER_WIDTH, getHeight() - 1);
    }

    private GradientPaint getPaint() {
        if (paint == null || paint.getPoint2().getX() != getWidth()) {
            paint = new GradientPaint(0f, 0f, Color.WHITE, getWidth(), 0f, Color.BLACK);
        }
        return paint;
    }

    public void setLightness(float lightness) {
        if (selectedLightness != lightness) {
            selectedLightness = lightness;
            final int width = getWidth();
            if (width > 0) {
                updatePointerPosition((1f - lightness) * width);
            }
            repaint();
        }
    }

    private void updatePointerPosition(float x) {
        final int px = (int)Math.min(Math.max(0f, x - POINTER_WIDTH / 2f), getWidth() - 1 - POINTER_WIDTH);
        if (pointerPosition != px) {
            pointerPosition = px;
            repaint();
        }
    }

    public void setOnLightnessChangedListener(OnLightnessChangedListener onLightnessChangedListener) {
        this.onLightnessChangedListener = onLightnessChangedListener;
    }

    private void onMouseEvent(int x, int y) {
        float lightness = 1f - Math.min(Math.max(0f, (float)x / getWidth()), 1f);

        if (this.selectedLightness != lightness && onLightnessChangedListener != null) {
            onLightnessChangedListener.onLightnessChanged(lightness);
        }
        this.selectedLightness = lightness;

        updatePointerPosition(x);
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            onMouseEvent(e.getX(), e.getY());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            onMouseEvent(e.getX(), e.getY());
        }
    }

    interface OnLightnessChangedListener {
        void onLightnessChanged(float lightness);
    }
}
