package com.leo.cse.frontend;

import java.awt.GraphicsEnvironment;

public class CaveSaveEdit {
	/**
	 * Current running App instance
	 */
	private static App app;

	public static void main(String[] args) {
		if (GraphicsEnvironment.isHeadless()) {
			System.out.println("Headless mode is enabled!\nCaveSaveEdit cannot run in headless mode!");
			System.exit(0);
		}

		runApp();
	}

	private static void runApp() {
		CaveSaveEdit.app = new App().run();
	}

	public static void exit() {
		if (app != null && app.shutdown()) {
			app = null;
		}
	}

	public static void restart() {
		if (app != null && app.terminate()) {
			runApp();
		}
	}
}
