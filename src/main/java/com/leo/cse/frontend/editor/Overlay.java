package com.leo.cse.frontend.editor;

import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.anim.IntAnimator;
import com.leo.cse.frontend.ui.layout.StackLayout;
import com.leo.cse.util.ColorUtils;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Overlay extends StackLayout {
    private Runnable clickListener;

    private final IntAnimator bgAnimator = new IntAnimator();
    private int bgAlpha = ThemeData.getHoverColor().getAlpha();

    public Overlay() {
        addMouseListener(new MouseEventsListener());
    }

    @Override
    public void setVisible(boolean b) {
        final boolean wasVisible = isVisible();

        super.setVisible(b);

        if (b && !wasVisible) {
            bgAnimator.animate(0, ThemeData.getHoverColor().getAlpha(), 200, (value) -> {
                bgAlpha = value;
                repaint();
            });
        }
    }

    public void setOnClickListener(Runnable clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(ColorUtils.setAlphaComponent(ThemeData.getHoverColor(), bgAlpha));
        g.fillRect(0, 0, getWidth(), getHeight());

        super.paint(g);
    }

    private class MouseEventsListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (clickListener != null) {
                clickListener.run();
            }
        }
    }
}
