package com.leo.cse.util;

import java.nio.Buffer;

/**
 * Buffer compatibility wrapper
 *
 * In Java 8, while calling clear() or flip() methods of ByteBuffer class,
 * since it has no implementation for these methods, it is actually calling the method
 * from extended class, Buffer; which is returning Buffer object.
 *
 * However, in Java 9, ByteBuffer class has implemented its own clear() and flip() methods,
 * and the returning object is changed from Buffer to ByteBuffer.
 *
 * @see <a href="https://www.codenong.com/js8f219d981aa9/">java.lang.NoSuchMethodError: java.nio.ByteBuffer.flip()Ljava/nio/ByteBuffer</a>
 */
public class BufferCompat {
    public static void clear(Buffer buffer) {
        buffer.clear();
    }

    public static void flip(Buffer buffer) {
        buffer.flip();
    }
}
