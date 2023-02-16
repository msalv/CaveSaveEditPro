package com.leo.cse.util;

import com.leo.cse.log.AppLogger;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility methods for loading resources.
 *
 * @author Leo
 *
 */
public class FileUtils {

	/**
	 * Make sure an instance of this class cannot be created.
	 */
	private FileUtils() {
	}

	// code from https://stackoverflow.com/a/35325946
	/**
	 * Maps lower case strings to their case insensitive File
	 */
	private static final Map<String, File> insensitiveFileHandlerCache = new HashMap<>();

	/**
	 * Case insensitive file handler. Cannot return <code>null</code>
	 */
	public static File newFile(String path) {
		if (path == null) {
			throw new IllegalArgumentException("path == null");
		}
		path = path.toLowerCase(Locale.ROOT);
		// First see if it is cached
		if (insensitiveFileHandlerCache.containsKey(path)) {
			return insensitiveFileHandlerCache.get(path);
		} else {
			// If it is not cached, cache it (the path is lower case)
			final File file = new File(path);
			insensitiveFileHandlerCache.put(path, file);

			// If the file does not exist, look for the real path
			if (!file.exists()) {
				// get the directory
				final String parentPath = file.getParent();
				if (parentPath == null) {
					// No parent directory? -> Just return the file since we can't find the real
					// path
					return file;
				}

				// Find the real path of the parent directory recursively
				final File dir = newFile(parentPath);

				final File[] files = dir.listFiles();
				if (files == null) {
					// If it is not a directory
					insensitiveFileHandlerCache.put(path, file);
					return file;
				}

				// Loop through the directory and put everything you find into the cache
				for (File otherFile : files) {
					// the path of our file will be updated at this point
					insensitiveFileHandlerCache.put(otherFile.getPath().toLowerCase(Locale.ROOT), otherFile);
				}

				// if you found what was needed, return it
				if (insensitiveFileHandlerCache.containsKey(path)) {
					return insensitiveFileHandlerCache.get(path);
				}
			}
			// Did not find it? Return the file with the original path
			return file;
		}
	}
	// end code from stack overflow

	/**
	 * Attempts to get CS+'s "base" folder.
	 *
	 * @param currentLoc
	 *            current location
	 * @return location of CS+'s "base" folder
	 */
	public static File getBaseFolder(final File currentLoc) {
		if (currentLoc == null) {
			AppLogger.trace("getBaseFolder: currentLoc == null");
			return null;
		}
		final File curLocParent = currentLoc.getParentFile();
		if (curLocParent != null && curLocParent.getName().equals("base")) {
			return curLocParent;
		}
		File modDir = currentLoc;
		while (!modDir.getName().equals("mod")) {
			if (modDir.getParentFile() == null) {
				AppLogger.trace("getBaseFolder: hierarchy crisis");
				return null; // hierarchy crisis
			}
			modDir = modDir.getParentFile();
		}
		// so barring shenanigans we should be in the 'mod' directory now
		final File modNameDir = modDir.getParentFile(); // modfolder (hurray unnecessarily nested folders)
		final File dataDir = modNameDir.getParentFile(); // data
		return new File(dataDir + File.separator + "base"); // base... ofc
	}
}
