package com.leo.cse.util.async;

import com.leo.cse.log.AppLogger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class AsyncTaskThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    AsyncTaskThreadFactory() {
        final SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "async-task-pool-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    public Thread newThread(Runnable r) {
        final Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);

        if (t.isDaemon()) {
            t.setDaemon(false);
        }

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        t.setUncaughtExceptionHandler(new UncaughtExceptionHandlerImpl());

        return t;
    }

    private static class UncaughtExceptionHandlerImpl implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            AppLogger.fatal(String.format("Uncaught exception in thread %s", t.getName()), e);

            if (!t.isInterrupted()) {
                t.interrupt();
            }
        }
    }
}
