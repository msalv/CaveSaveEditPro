package com.leo.cse.frontend;

import com.leo.cse.log.AppLogger;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private Config() {
	}

	// Recent Files
	public static final String KEY_LAST_PROFILE = "last_profile";
	public static final String KEY_LAST_PROFILE_EXT = "last_profile_ex";
	public static final String KEY_LAST_MCI_FILE = "last_defines";
	public static final String KEY_LAST_MOD = "last_mod";
	public static final String KEY_LAST_NIKU = "last_niku";

	// Appearance
	public static final String KEY_HIDE_UNDEFINED_FLAGS = "hide_undefined_flags";
	public static final String KEY_HIDE_SYSTEM_FLAGS = "hide_system_flags";
	public static final String KEY_SHOW_MAP_GRID = "show_map_grid";
	public static final String KEY_SHOW_PLAYER_ABOVE_FG = "show_player_above_fg";
	public static final String KEY_FOREGROUND_COLOR = "foreground_color";
	public static final String KEY_BACKGROUND_COLOR = "background_color";
	public static final String KEY_TEXT_COLOR = "text_color";
	public static final String KEY_ACCENT_COLOR = "accent_color";

	// Game
	public static final String KEY_LOAD_NPCS = "load_npcs";
	public static final String KEY_ENCODING = "encoding";

	// AutoLoad
	public static final String KEY_AUTOLOAD_EXE = "autoload_exe";
	public static final String KEY_AUTOLOAD_PROFILE = "autoload_profile";

	// Application
	public static final String KEY_SKIP_UPDATE_CHECK = "skip_update_check";
	public static final String KEY_NO_LOOK_AND_FEEL = "no_look_and_feel";

	private static final String FILE_NAME = "prefs.cfg";

	private static final Properties config = new Properties();

	public static void init() {
		try (FileInputStream inputStream = new FileInputStream(FILE_NAME)) {
			config.load(inputStream);
		} catch (IOException e) {
			AppLogger.info("Failed to read config", e);
		}
	}

	public static void wipe() {
		try {
			config.clear();
			commit();
		} catch (Exception e) {
			AppLogger.error("Failed to wipe config", e);
		}
	}

	public static String get(String key, String def) {
		return config.getProperty(key, def);
	}

	public static void set(String key, String value) {
		config.setProperty(key, value);
		commit();
	}

	public static boolean getBoolean(String key, boolean def) {
		final String b = get(key, null);
		if (b == null) {
			return def;
		}
		return !"0".equals(b);
	}

	public static void setBoolean(String key, boolean value) {
		set(key, value ? "1" : "0");
	}

	public static Color getColor(String key, Color def) {
		final String nm = get(key, null);
		if (nm == null) {
			return def;
		}
		try {
			return new Color(Integer.parseUnsignedInt(nm, 16));
		} catch (Exception e) {
			AppLogger.error("Failed to parse color", e);
			return def;
		}
	}

	public static void setColor(String key, Color value) {
		set(key, Integer.toHexString(value.getRGB()));
	}

	public static String getColorString(String key, Color fallback) {
		final Color color = Config.getColor(key, fallback);
		final String hex = Integer.toHexString(color.getRGB());
		return String.format("#%s", hex.length() > 6 ? hex.substring(2) : hex);
	}

	private static void commit() {
		try (FileOutputStream outputStream = new FileOutputStream(FILE_NAME)) {
			config.store(outputStream, "CaveSaveEdit Config File");
		} catch (IOException e) {
			AppLogger.error("Failed to save config", e);
		}
	}

	/**
	 * Set some sensitive preferences to safe values
	 * to prevent runtime exceptions from being thrown again
	 */
	public static void safeMode() {
		setBoolean(Config.KEY_AUTOLOAD_PROFILE, false);
		setBoolean(Config.KEY_AUTOLOAD_EXE, false);
		setBoolean(Config.KEY_SKIP_UPDATE_CHECK, true);
	}
}
