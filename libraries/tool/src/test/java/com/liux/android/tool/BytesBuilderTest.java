package com.liux.android.tool;

import com.liux.android.util.TextUtil;

import org.junit.Test;

import static org.junit.Assert.*;

public class BytesBuilderTest {

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

        bytesBuilder.reverse();
        assertArrayEquals(new byte[]{5, 4, 3, 2, -2, -2}, bytesBuilder.toBytes());

        assertEquals(2, bytesBuilder.indexOf((byte) 3));
        assertEquals(-1, bytesBuilder.indexOf((byte) 8));
        assertEquals(2, bytesBuilder.indexOf(2, (byte) 3));
        assertEquals(-1, bytesBuilder.indexOf(3, (byte) 3));
        assertEquals(2, bytesBuilder.indexArrayOf((byte) 3, (byte) 2));
        assertEquals(-1, bytesBuilder.indexArrayOf((byte) 3, (byte) 3));
        assertEquals(4, bytesBuilder.indexArrayOf(2, (byte) -2, (byte) -2));
        assertEquals(-1, bytesBuilder.indexArrayOf(2, (byte) -2, (byte) 1));

        assertEquals(2, bytesBuilder.lastIndexOf((byte) 3));
        assertEquals(-1, bytesBuilder.lastIndexOf((byte) 8));
        assertEquals(2, bytesBuilder.lastIndexOf(2, (byte) 3));
        assertEquals(-1, bytesBuilder.lastIndexOf(4, (byte) 3));
        assertEquals(2, bytesBuilder.lastIndexArrayOf((byte) 3, (byte) 2));
        assertEquals(-1, bytesBuilder.lastIndexArrayOf((byte) 3, (byte) 3));
        assertEquals(1, bytesBuilder.lastIndexArrayOf(2, (byte) 4, (byte) 3));
        assertEquals(-1, bytesBuilder.lastIndexArrayOf(2, (byte) -2, (byte) 1));

        assertEquals(2, bytesBuilder.byteAt(3));

        assertArrayEquals(new byte[]{4, 3, 2, -2, -2}, bytesBuilder.subBytes(1));
        assertArrayEquals(new byte[]{4, 3, 2}, bytesBuilder.subBytes(1, 3));
    }
}