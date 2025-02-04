package com.leo.cse.frontend;

import com.leo.cse.log.AppLogger;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JOptionPane;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		AppLogger.fatal(String.format("Uncaught exception in thread %s", t.getName()), e);
		final int choice = JOptionPane.showConfirmDialog(
				null,
				"An uncaught exception has occurred in thread " + t.getName() + ":\n"
						+ e + "\n" +
						"Please send the error log (\"cse.log\") to the developer,\n" +
						"along with a description of what you did leading up to the exception.\n" +
						"\nWould you like to restart the app?",
				"Uncaught exception! Restart?",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.ERROR_MESSAGE);

		if (choice == JOptionPane.YES_OPTION) {
			try {
				// Do not load profile and game resources on restart
				// to avoid same errors
				Config.safeMode();
				CaveSaveEdit.restart();
			} catch (Exception ex) {
				System.exit(1);
			}
		} else {
			System.exit(1);
		}
	}
}
