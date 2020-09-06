package com.liux.android.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ByteUtilTest {

    @Test
    public void int2BytesBigEndian() {
        assertArrayEquals(new byte[]{0, 0, (byte) 0x12, (byte) 0x34}, ByteUtil.int2BytesBigEndian(0x1234));
        assertArrayEquals(new byte[]{(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78}, ByteUtil.int2BytesBigEndian(0x12345678));
    }

    @Test
    public void bytes2IntBigEndian() {
        assertEquals(0x1234, ByteUtil.bytes2IntBigEndian((byte) 0x12, (byte) 0x34));
        assertEquals(0x12345678, ByteUtil.bytes2IntBigEndian((byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78));
    }

    @Test
    public void long2BytesBigEndian() {
        assertArrayEquals(new byte[]{0, 0, 0, 0, 0 , 0, (byte) 0x12, (byte) 0x34}, ByteUtil.long2BytesBigEndian(0x1234L));
        assertArrayEquals(new byte[]{0, 0, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB}, ByteUtil.long2BytesBigEndian(0x1234567890ABL));
    }

    @Test
    public void bytes2LongBigEndian() {
        assertEquals(0x1234L, ByteUtil.bytes2LongBigEndian((byte) 0x12, (byte) 0x34));
        assertEquals(0x1234567890ABL, ByteUtil.bytes2LongBigEndian((byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB));
    }

    @Test
    public void int2BytesLittleEndian() {
        assertArrayEquals(new byte[]{(byte) 0x34, (byte) 0x12, 0, 0}, ByteUtil.int2BytesLittleEndian(0x1234));
        assertArrayEquals(new byte[]{(byte) 0x78, (byte) 0x56, (byte) 0x34, (byte) 0x12}, ByteUtil.int2BytesLittleEndian(0x12345678));
    }

    @Test
    public void bytes2IntLittleEndian() {
        assertEquals(0x3412, ByteUtil.bytes2IntLittleEndian((byte) 0x12, (byte) 0x34));
        assertEquals(0x78563412, ByteUtil.bytes2IntLittleEndian((byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78));
    }

    @Test
    public void long2BytesLittleEndian() {
        assertArrayEquals(new byte[]{(byte) 0x34, (byte) 0x12, 0, 0, 0, 0, 0, 0}, ByteUtil.long2BytesLittleEndian(0x1234L));
        assertArrayEquals(new byte[]{(byte) 0xAB, (byte) 0x90, (byte) 0x78, (byte) 0x56, (byte) 0x34, (byte) 0x12, 0, 0}, ByteUtil.long2BytesLittleEndian(0x1234567890ABL));
    }

    @Test
    public void bytes2LongLittleEndian() {
        assertEquals(0x3412L, ByteUtil.bytes2LongLittleEndian((byte) 0x12, (byte) 0x34));
        assertEquals(0xAB9078563412L, ByteUtil.bytes2LongLittleEndian((byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB));
    }

    @Test
    public void reverse() {
        assertArrayEquals(new byte[]{(byte) 0x78, (byte) 0x56, (byte) 0x34, (byte) 0x12}, ByteUtil.reverse((byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78));
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
        assertArrayEquals(new byte[]{(byte) 0xf5, (byte) 0xf4}, ByteUtil.crc16Modbus(TextUtil.hex2Bytes("00 64 FF FF 62 25 00 15 01 00 02 15 01 03 00 FF 00 00 00 00 00 00 00 12 00 BB B6 D3 AD B9 E2 C1 D9 C7 EB C8 EB B3 A1 CD A3 B3 B5")));
    }
}