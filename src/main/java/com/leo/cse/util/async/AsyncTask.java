package com.leo.cse.util.async;

import com.leo.cse.log.AppLogger;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simplified AsyncTask implementation
 */
public abstract class AsyncTask<Progress, Result> {
    private final static Executor sSingleThreadExecutor = Executors.newSingleThreadExecutor();

    private final FutureTask<Result> future;

    private final AtomicBoolean isCancelled = new AtomicBoolean();
    private final AtomicBoolean isTaskInvoked = new AtomicBoolean();

    private Status status = Status.PENDING;

    public AsyncTask() {
        final Callable<Result> worker = () -> {
            isTaskInvoked.set(true);
            Result result = null;
            try {
                result = doInBackground();
            } catch (Throwable tr) {
                isCancelled.set(true);
                throw tr;
            } finally {
                postResult(result);
            }
            return result;
        };

        future = new FutureTask<Result>(worker) {
            @Override
            protected void done() {
                try {
                    postResultIfNotInvoked(get());
                } catch (InterruptedException e) {
                    AppLogger.warn("AsyncTask", e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("An error occurred while executing doInBackground()",
                            e.getCause());
                } catch (CancellationException e) {
                    postResultIfNotInvoked(null);
                }
            }
        };
    }

    // worker thread
    protected abstract Result doInBackground() throws Exception;

    // ui thread
    protected void onPreExecute() {
    }

    // ui thread
    protected void onPostExecute(Result result) {
    }

    // ui thread
    protected void onProgressUpdate(Progress progress) {
    }

    // ui thread
    protected void onCancelled() {
    }

    // ui thread
    protected void onCancelled(Result result) {
        onCancelled();
    }

    // worker thread
    protected void postResult(Result result) {
        EventQueue.invokeLater(() -> finish(result));
    }

    // worker thread
    protected final void publishProgress(Progress progress) {
        if (!isCancelled()) {
            EventQueue.invokeLater(() -> onProgressUpdate(progress));
        }
    }

    // worker thread
    private void postResultIfNotInvoked(Result result) {
        final boolean wasTaskInvoked = isTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

    // ui thread
    public final AsyncTask<Progress, Result> execute() {
        if (status != Status.PENDING) {
            switch (status) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task has already been executed "
                            + "(a task can be executed only once)");
            }
        }

        status = Status.RUNNING;

        onPreExecute();

        sSingleThreadExecutor.execute(future);

        return this;
    }

    // any thread
    public final boolean cancel(boolean mayInterruptIfRunning) {
        isCancelled.set(true);
        return future.cancel(mayInterruptIfRunning);
    }

    // any thread
    public final boolean isCancelled() {
        return isCancelled.get();
    }

    public final boolean isRunning() {
        return status == Status.RUNNING;
    }

    public final boolean isFinished() {
        return status == Status.FINISHED;
    }

    // ui thread
    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        status = Status.FINISHED;
    }

    public enum Status {
        PENDING,
        RUNNING,
        FINISHED
    }
}