package com.leo.cse.util.async;

public class DelayedTask extends IndeterminateTask<Void> {
    private final Runnable action;
    private final Long delay;

    public DelayedTask(Long delay, Runnable action) {
        this.action = action;
        this.delay = delay;
    }

    @Override
    protected Void doInBackground() throws Exception {
        synchronized (this) {
            wait(delay);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        action.run();
    }
}
