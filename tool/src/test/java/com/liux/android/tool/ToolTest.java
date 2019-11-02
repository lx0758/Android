package com.liux.android.tool;

import com.liux.android.util.TextUtil;

import org.junit.Test;

import static org.junit.Assert.*;

public class ToolTest {

    @Test
    public void bytesBuilder() {
        BytesBuilder bytesBuilder = new BytesBuilder();

        bytesBuilder.append(TextUtil.hex2Bytes("00 01"));
        assertArrayEquals(new byte[]{0, 1}, bytesBuilder.toBytes());

        bytesBuilder.append(1, 2, TextUtil.hex2Bytes("02 03 04 05"));
        assertArrayEquals(new byte[]{0, 1, 3, 4}, bytesBuilder.toBytes());

        assertEquals(4, bytesBuilder.length());
        assertEquals(16, bytesBuilder.capacity());

        bytesBuilder.trimToSize();
        assertEquals(4, bytesBuilder.length());
        assertEquals(4, bytesBuilder.capacity());

        bytesBuilder.setLength(6);
        assertEquals(6, bytesBuilder.length());
        assertEquals((1<<3) + 2, bytesBuilder.capacity());
        assertArrayEquals(new byte[]{0, 1, 3, 4, 0, 0}, bytesBuilder.toBytes());
        bytesBuilder.setLength(4);
        assertArrayEquals(new byte[]{0, 1, 3, 4}, bytesBuilder.toBytes());
        assertEquals(4, bytesBuilder.length());
        assertEquals((1<<3) + 2, bytesBuilder.capacity());

        bytesBuilder.ensureCapacity(16);
        assertEquals(4, bytesBuilder.length());
        assertEquals((10 << 1) + 2, bytesBuilder.capacity());

        bytesBuilder.insert(2, (byte) 2, (byte) 2);
        assertArrayEquals(new byte[]{0, 1, 2, 2, 3, 4}, bytesBuilder.toBytes());

        bytesBuilder.deleteAt(2);
        assertArrayEquals(new byte[]{0, 1, 2, 3, 4}, bytesBuilder.toBytes());
        bytesBuilder.delete(3);
        assertArrayEquals(new byte[]{0, 1, 2}, bytesBuilder.toBytes());
        bytesBuilder.append((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5);
        assertArrayEquals(new byte[]{0, 1, 2, 1, 2, 3, 4, 5}, bytesBuilder.toBytes());
        bytesBuilder.delete(1, 2);
        assertArrayEquals(new byte[]{0, 1, 2, 3, 4, 5}, bytesBuilder.toBytes());

        bytesBuilder.replaceAt(0, (byte) -1);
        assertArrayEquals(new byte[]{-1, 1, 2, 3, 4, 5}, bytesBuilder.toBytes());
        bytesBuilder.replace(0, 2, (byte) -2, (byte) -2);
        assertArrayEquals(new byte[]{-2, -2, 2, 3, 4, 5}, bytesBuilder.toBytes());
    }
}