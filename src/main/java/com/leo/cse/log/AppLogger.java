package com.leo.cse.log;

public class AppLogger {
	private static BackendLogger sImpl = NullBackendLogger.getInstance();

	public static void init(BackendLogger impl) {
		AppLogger.sImpl = (impl == null)
				? NullBackendLogger.getInstance()
				: impl;
	}

	public static void trace(String message, Throwable t) {
		sImpl.trace(message, t);
	}

	public static void info(String message, Throwable t) {
		sImpl.info(message, t);
	}

	public static void warn(String message, Throwable t) {
		sImpl.warn(message, t);
	}

	public static void error(String message, Throwable t) {
		sImpl.error(message, t);
	}

	public static void fatal(String message, Throwable t) {
		sImpl.fatal(message, t);
	}

	public static void trace(String message) {
		sImpl.trace(message);
	}

	public static void info(String message) {
		sImpl.info(message);
	}

	public static void warn(String message) {
		sImpl.warn(message);
	}

	public static void error(String message) {
		sImpl.error(message);
	}

	public static void fatal(String message) {
		sImpl.fatal(message);
	}
}
