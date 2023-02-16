package com.leo.cse.log;

class NullBackendLogger extends BackendLogger {
    private NullBackendLogger() {
    }

    private static volatile NullBackendLogger sInstance;

    static BackendLogger getInstance() {
        NullBackendLogger localInstance = sInstance;

        if (localInstance == null) {
            synchronized (NullBackendLogger.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    sInstance = localInstance = new NullBackendLogger();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void trace(String message, Throwable t) {
    }

    @Override
    public void info(String message, Throwable t) {
    }

    @Override
    public void warn(String message, Throwable t) {
    }

    @Override
    public void error(String message, Throwable t) {
    }

    @Override
    public void fatal(String message, Throwable t) {
    }
}
