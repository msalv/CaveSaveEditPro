package com.leo.cse.log;

public abstract class BackendLogger {
    public abstract void trace(String message, Throwable t);
    public abstract void info(String message, Throwable t);
    public abstract void warn(String message, Throwable t);
    public abstract void error(String message, Throwable t);
    public abstract void fatal(String message, Throwable t);

    public void trace(String message) {
        trace(message, null);
    }

    public void info(String message) {
        info(message, null);
    }

    public void warn(String message) {
        warn(message, null);
    }

    public void error(String message) {
        error(message, null);
    }

    public void fatal(String message) {
        fatal(message, null);
    }
}
