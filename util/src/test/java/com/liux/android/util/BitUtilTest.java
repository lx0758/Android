package com.liux.android.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class BitUtilTest {

    @Test
    public void int2Bin() {
        assertEquals("111_1111_1111_1111_1111_1111_1111_1111".replace("_", ""), BitUtil.int2Bin(2147483647));
    }

    @Test
    public void bin2Int() {
        assertEquals(2147483647, BitUtil.bin2Int("111_1111_1111_1111_1111_1111_1111_1111".replace("_", "")));
        assertEquals(2147483647, BitUtil.bin2Int("0b111_1111_1111_1111_1111_1111_1111_1111".replace("_", "")));
    }

    @Test
    public void long2Bin() {
        assertEquals("1_0000_0000_0000_0000_0000_0000_0000_0000".replace("_", ""), BitUtil.long2Bin(4294967296L));
    }

    @Test
    public void bin2Long() {
        assertEquals(4294967296L, BitUtil.bin2Long("1_0000_0000_0000_0000_0000_0000_0000_0000".replace("_", "")));
        assertEquals(4294967296L, BitUtil.bin2Long("0b1_0000_0000_0000_0000_0000_0000_0000_0000".replace("_", "")));
    }

    @Test
    public void getIntBit() {
        assertTrue(BitUtil.getIntBit(4, 3));
        assertFalse(BitUtil.getIntBit(4, 2));
    }

    @Test
    public void setIntBit() {
        assertEquals("1011", BitUtil.int2Bin(BitUtil.setIntBit(BitUtil.bin2Int("1011"), 2, true)));
        assertEquals("1011", BitUtil.int2Bin(BitUtil.setIntBit(BitUtil.bin2Int("1001"), 2, true)));
        assertEquals("1001", BitUtil.int2Bin(BitUtil.setIntBit(BitUtil.bin2Int("1011"), 2, false)));
        assertEquals("1001", BitUtil.int2Bin(BitUtil.setIntBit(BitUtil.bin2Int("1001"), 2, false)));
    }

    @Test
    public void getLongBit() {
        assertTrue(BitUtil.getLongBit(4, 3));
        assertFalse(BitUtil.getLongBit(4, 2));
    }

    @Test
    public void setLongBit() {
        assertEquals("1011", BitUtil.long2Bin(BitUtil.setLongBit(BitUtil.bin2Long("1011"), 2, true)));
        assertEquals("1011", BitUtil.long2Bin(BitUtil.setLongBit(BitUtil.bin2Long("1001"), 2, true)));
        assertEquals("1001", BitUtil.long2Bin(BitUtil.setLongBit(BitUtil.bin2Long("1011"), 2, false)));
        assertEquals("1001", BitUtil.long2Bin(BitUtil.setLongBit(BitUtil.bin2Long("1001"), 2, false)));
    }
}
