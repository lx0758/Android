package com.liux.android.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteUtilTest {

    @Test
    public void bytes2Int() {
        assertEquals(15856371, ByteUtil.bytes2Int((byte) 0xF1, (byte) 0xF2, (byte) 0xF3));
    }

    @Test
    public void int2Bytes() {
        assertArrayEquals(new byte[]{0, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3}, ByteUtil.int2Bytes(15856371));
    }

    @Test
    public void bytes2Long() {
        assertEquals( 1039163192565L, ByteUtil.bytes2Long((byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0xF5));
    }

    @Test
    public void long2Bytes() {
        assertArrayEquals(new byte[]{0, 0, 0, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0xF5}, ByteUtil.long2Bytes(1039163192565L));
    }

    @Test
    public void bytesIndexOf() {
        assertEquals(2, ByteUtil.bytesIndexOf(new byte[]{(byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0xF5, (byte) 0xF6}, new byte[]{(byte) 0xF3, (byte) 0xF4}, 0));
    }

    @Test
    public void bbc() {
        assertEquals((byte) 0x29, ByteUtil.bbc(TextUtil.hex2Bytes("78 B0 00 01 01 42 00 00 00 01 00 00 00 00 00 00 09 5A FF FF F1 00 00 00 00 00 00 00")));
    }

    @Test
    public void crc16() {
        assertEquals(ByteUtil.bytes2Int((byte) 0xf4, (byte) 0xf5), ByteUtil.crc16Modbus(false, TextUtil.hex2Bytes("00 64 FF FF 62 25 00 15 01 00 02 15 01 03 00 FF 00 00 00 00 00 00 00 12 00 BB B6 D3 AD B9 E2 C1 D9 C7 EB C8 EB B3 A1 CD A3 B3 B5")));
    }
}