package com.leo.cse.frontend;

import java.util.regex.Pattern;

public class Version implements Comparable<Version> {
	private final String version;

	private final int major;
	private final int minor;
	private final int patch;

	public Version(String version) {
		final String[] versions = version.split(Pattern.quote("."));
		major = parseVersionAt(versions, 0);
		minor = parseVersionAt(versions, 1);
		patch = parseVersionAt(versions, 2);

		this.version = version;
	}

	private int parseVersionAt(String[] versions, int index) {
		return versions.length > index ? Integer.parseInt(versions[index]) : 0;
	}

	@Override
	public int compareTo(Version that) {
		int result = Integer.compare(major, that.major);
		if (result != 0) {
			return result;
		}

		result = Integer.compare(minor, that.minor);

		if (result != 0) {
			return result;
		}

		return Integer.compare(patch, that.patch);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null) return false;
		if (this == that) return true;
		if (this.getClass() != that.getClass()) return false;
		return compareTo((Version) that) == 0;
	}

	@Override
	public String toString() {
		return version;
	}
}
