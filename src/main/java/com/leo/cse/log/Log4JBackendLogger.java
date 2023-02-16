package com.leo.cse.log;

import org.apache.logging.log4j.Logger;

public class Log4JBackendLogger extends BackendLogger {
    private final Logger l;

    public Log4JBackendLogger(Logger l) {
        this.l = l;
    }

    @Override
    public void trace(String message, Throwable t) {
        l.trace(message, t);
    }

    @Override
    public void info(String message, Throwable t) {
        l.info(message, t);
    }

    @Override
    public void warn(String message, Throwable t) {
        l.warn(message, t);
    }

    @Override
    public void error(String message, Throwable t) {
        l.error(message, t);
    }

    @Override
    public void fatal(String message, Throwable t) {
        l.fatal(message, t);
    }
}
