package com.leo.cse.backend;

import com.leo.cse.log.AppLogger;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class CString {
	/**
	 * Default string encoding.
	 */
	public static final String DEFAULT_ENCODING = "Cp943C";

	/**
	 * Creates a new String instance from null-terminated C-Style characters buffer
	 * encoded with specified encoding
	 * @param buffer Null-terminated array of characters data
	 * @param encoding String encoding
	 * @return new Java String
	 */
	public static String newInstance(byte[] buffer, String encoding) {
		int length = 0;
		while (length < buffer.length && buffer[length] != 0) {
			length++;
		}
		final byte[] bytes = Arrays.copyOf(buffer, length);
		try {
			return new String(bytes, encoding);
		} catch (UnsupportedEncodingException e) {
			AppLogger.error("Unsupported encoding: " + encoding, e);
		}
		return "";
	}
}
