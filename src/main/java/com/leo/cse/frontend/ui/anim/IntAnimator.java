package com.leo.cse.frontend.ui.anim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class IntAnimator implements ActionListener {
    private long start = 0L;
    private int to;
    private int duration;

    private final Timer timer = new Timer(16, this);
    private UpdateListener listener;

    public IntAnimator() {
        timer.setInitialDelay(0);
    }

    public void animate(int from, int to, int duration, UpdateListener updateListener) {
        this.start = System.currentTimeMillis();
        this.to = to;
        this.duration = duration;
        this.listener = updateListener;

        updateListener.onUpdate(from);

        timer.restart();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final long elapsed = System.currentTimeMillis() - start;

        final int value = (int) Math.min(to, to * elapsed / (float) duration);
        listener.onUpdate(value);

        if (elapsed >= duration) {
            stop();
        }
    }

    public interface UpdateListener {
        void onUpdate(int value);
    }
}
