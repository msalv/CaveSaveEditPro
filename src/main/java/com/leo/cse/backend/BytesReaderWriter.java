package com.leo.cse.backend;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for reading from and writing to byte arrays.
 * @author Leo
 */
public class BytesReaderWriter {
	/**
	 * Used for converting bytes to other number types.
	 */
	private static final ThreadLocal<ByteBuffer> sBuffer = ThreadLocal.withInitial(() -> {
		return ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN);
	});

	/**
	 * Writes bytes from an array into {@linkplain #sBuffer the byte buffer}.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param size
	 *            amount of bytes to write
	 */
	private static void putBytesToBuffer(byte[] data, int ptr, int size) {
		sBuffer.get().clear();
		for (int i = 0; i < size; i++) {
			sBuffer.get().put(data[ptr + i]);
		}
	}

	/**
	 * Reads a <code>String</code> from a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param length
	 *            string length
	 * @param encoding
	 *            string encoding
	 * @return string
	 */
	public static String readString(byte[] data, int ptr, int length, String encoding) {
		final byte[] dc;
		if (length < 1) {
			// length was either not specified or specified but invalid
			// we're gonna have to guess the string's length
			final List<Byte> chars = new ArrayList<>();
			while (ptr < data.length) {
				// string is (probably) terminated by 0
				if (data[ptr] == 0) {
					break;
				}
				chars.add(data[ptr]);
				ptr++;
			}
			dc = new byte[chars.size()];
			for (int i = 0; i < dc.length; i++) {
				dc[i] = chars.get(i);
			}
		} else {
			dc = new byte[length];
			System.arraycopy(data, ptr, dc, 0, length);
		}
		return CString.newInstance(dc, encoding);
	}

	/**
	 * Reads a <code>String</code> from a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param length
	 *            string length
	 * @return string
	 */
	public static String readString(byte[] data, int ptr, int length) {
		return readString(data, ptr, length, CString.DEFAULT_ENCODING);
	}

	/**
	 * Reads an array of <code>byte</code>s from a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each short
	 * @param dest
	 *            destination array
	 */
	public static void readBytes(byte[] data, int ptr, int off, byte[] dest) {
		if (off == 0) {
			System.arraycopy(data, ptr, dest, 0, dest.length);
			return;
		}
		for (int i = 0; i < dest.length; i++) {
			dest[i] = data[ptr];
			ptr += 1 + off;
		}
	}

	/**
	 * Reads a <code>short</code> from a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @return short
	 */
	public static short readShort(byte[] data, int ptr) {
		putBytesToBuffer(data, ptr, Short.BYTES);
		return sBuffer.get().getShort(0);
	}

	/**
	 * Reads an array of <code>short</code>s from a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each short
	 * @param dest
	 *            destination array
	 */
	public static void readShorts(byte[] data, int ptr, int off, short[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readShort(data, ptr);
			ptr += Short.BYTES + off;
		}
	}

	/**
	 * Reads an <code>int</code> from a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @return integer
	 */
	public static int readInt(byte[] data, int ptr) {
		putBytesToBuffer(data, ptr, Integer.BYTES);
		return sBuffer.get().getInt(0);
	}

	/**
	 * Reads an array of <code>int</code>s from a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each integer
	 * @param dest
	 *            destination array
	 */
	public static void readInts(byte[] data, int ptr, int off, int[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readInt(data, ptr);
			ptr += Integer.BYTES + off;
		}
	}

	/**
	 * Reads a <code>long</code> from a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @return integer
	 */
	public static long readLong(byte[] data, int ptr) {
		putBytesToBuffer(data, ptr, Long.BYTES);
		return sBuffer.get().getLong(0);
	}

	/**
	 * Reads an array of <code>long</code>s from a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each long
	 * @param dest
	 *            destination array
	 */
	public static void readLongs(byte[] data, int ptr, int off, long[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readLong(data, ptr);
			ptr += Long.BYTES + off;
		}
	}

	/**
	 * Reads an array of <code>boolean</code>s from a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param dest
	 *            destination array
	 */
	public static void readFlags(byte[] data, int ptr, boolean[] dest) {
		int s = 0;
		for (int i = 0; i < dest.length; i++) {
			final byte v = data[ptr];
			dest[i] = ((v & (1 << s)) != 0);
			s++;
			if (s >= 8) {
				ptr++;
				s = 0;
			}
		}
	}

	/**
	 * Reads bytes from {@linkplain #sBuffer the byte buffer} into an array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param size
	 *            amount of bytes to read
	 */
	private static void writeBytesFromBuffer(byte[] data, int ptr, int size) {
		for (int i = 0; i < size; i++) {
			data[ptr + i] = sBuffer.get().get(i);
		}
	}

	/**
	 * Writes a <code>String</code> to a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param value
	 *            string to write
	 */
	public static void writeString(byte[] data, int ptr, String value) {
		final byte[] dc = value.getBytes();
		System.arraycopy(dc, 0, data, ptr, value.length());
	}

	/**
	 * Writes an array of <code>byte</code>s to a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each short
	 * @param value
	 *            shorts to write
	 */
	public static void writeBytes(byte[] data, int ptr, int off, byte[] value) {
		if (off == 0) {
			System.arraycopy(value, 0, data, ptr, value.length);
			return;
		}
		for (byte b : value) {
			data[ptr] = b;
			ptr += 1 + off;
		}
	}

	/**
	 * Writes a <code>short</code> to a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param value
	 *            short to write
	 */
	public static void writeShort(byte[] data, int ptr, short value) {
		sBuffer.get().clear();
		sBuffer.get().putShort(value);
		writeBytesFromBuffer(data, ptr, Short.BYTES);
	}

	/**
	 * Writes an array of <code>short</code>s to a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each short
	 * @param value
	 *            shorts to write
	 */
	public static void writeShorts(byte[] data, int ptr, int off, short[] value) {
		for (short item : value) {
			writeShort(data, ptr, item);
			ptr += Short.BYTES + off;
		}
	}

	/**
	 * Writes an <code>int</code> to a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param value
	 *            integer to write
	 */
	public static void writeInt(byte[] data, int ptr, int value) {
		sBuffer.get().clear();
		sBuffer.get().putInt(value);
		writeBytesFromBuffer(data, ptr, Integer.BYTES);
	}

	/**
	 * Writes an array of <code>int</code>s to a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each integer
	 * @param value
	 *            integers to write
	 */
	public static void writeInts(byte[] data, int ptr, int off, int[] value) {
		for (int j : value) {
			writeInt(data, ptr, j);
			ptr += Integer.BYTES + off;
		}
	}

	/**
	 * Writes a <code>long</code> to a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param value
	 *            integer to write
	 */
	public static void writeLong(byte[] data, int ptr, long value) {
		sBuffer.get().clear();
		sBuffer.get().putLong(value);
		writeBytesFromBuffer(data, ptr, Long.BYTES);
	}

	/**
	 * Writes an array of <code>long</code>s to a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each long
	 * @param value
	 *            integers to write
	 */
	public static void writeLongs(byte[] data, int ptr, int off, long[] value) {
		for (long l : value) {
			writeLong(data, ptr, l);
			ptr += Long.BYTES + off;
		}
	}

	/**
	 * Writes an array of <code>boolean</code>s to a byte array.
	 *
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param value
	 *            booleans to write
	 */
	public static void writeFlags(byte[] data, int ptr, boolean[] value) {
		final int length = value.length / 8;
		final byte[] v = (length == 0) ? new byte[1] : new byte[length];

		int vi = 0;
		int s = 0;

		for (boolean b : value) {
			if (b) {
				v[vi % v.length] |= 1 << s;
			}
			s++;
			if (s >= 8) {
				vi++;
				s = 0;
			}
		}

		System.arraycopy(v, 0, data, ptr, v.length);
	}
}
