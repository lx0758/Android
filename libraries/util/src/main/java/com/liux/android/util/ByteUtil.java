package com.liux.android.util;

public class ByteUtil {

    /**
     * 整数型转字节(大端模式,低地址存放高位)
     * @param i
     * @return
     */
    public static byte[] int2BytesBigEndian(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((i >> 24) & 0xff);
        bytes[1] = (byte) ((i >> 16) & 0xff);
        bytes[2] = (byte) ((i >> 8) & 0xff);
        bytes[3] = (byte) (i & 0xff);
        return bytes;
    }

    /**
     * 字节转整数型(大端模式,低地址存放高位)
     * @param bytes
     * @return
     */
    public static int bytes2IntBigEndian(byte... bytes) {
        int result = 0;
        if (bytes == null || bytes.length == 0) return result;
        result |= (bytes[0] & 0xff);
        for (int i = 1; i < bytes.length; i++) {
            result = result << 8;
            result |= (bytes[i] & 0xff);
        }
        return result;
    }

    /**
     * 长整数型转字节(大端模式,低地址存放高位)
     * @param l
     * @return
     */
    public static byte[] long2BytesBigEndian(long l) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) ((l >> 56) & 0xff);
        bytes[1] = (byte) ((l >> 48) & 0xff);
        bytes[2] = (byte) ((l >> 40) & 0xff);
        bytes[3] = (byte) ((l >> 32) & 0xff);
        bytes[4] = (byte) ((l >> 24) & 0xff);
        bytes[5] = (byte) ((l >> 16) & 0xff);
        bytes[6] = (byte) ((l >> 8) & 0xff);
        bytes[7] = (byte) (l & 0xff);
        return bytes;
    }

    /**
     * 字节转长整数型(大端模式,低地址存放高位)
     * @param bytes
     * @return
     */
    public static long bytes2LongBigEndian(byte... bytes) {
        long result = 0L;
        if (bytes == null || bytes.length == 0) return result;
        result |= (bytes[0] & 0xff);
        for (int i = 1; i < bytes.length; i++) {
            result = result << 8;
            result |= (bytes[i] & 0xff);
        }
        return result;
    }

    /**
     * 整数型转字节(小端模式,低地址存放低位)
     * @param i
     * @return
     */
    public static byte[] int2BytesLittleEndian(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i & 0xff);
        bytes[1] = (byte) ((i >> 8) & 0xff);
        bytes[2] = (byte) ((i >> 16) & 0xff);
        bytes[3] = (byte) ((i >> 24) & 0xff);
        return bytes;
    }

    /**
     * 字节转整数型(小端模式,低地址存放低位)
     * @param bytes
     * @return
     */
    public static int bytes2IntLittleEndian(byte... bytes) {
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result |= (bytes[i] & 0xFF) << (i * 8);
            if (i == 3) break;
        }
        return result;
    }

    /**
     * 长整数型转字节(小端模式,低地址存放低位)
     * @param l
     * @return
     */
    public static byte[] long2BytesLittleEndian(long l) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (l & 0xFF);
        bytes[1] = (byte) (l >> 8 & 0xFF);
        bytes[2] = (byte) (l >> 16 & 0xFF);
        bytes[3] = (byte) (l >> 24 & 0xFF);
        bytes[4] = (byte) (l >> 32 & 0xFF);
        bytes[5] = (byte) (l >> 40 & 0xFF);
        bytes[6] = (byte) (l >> 48 & 0xFF);
        bytes[7] = (byte) (l >> 56 & 0xFF);
        return bytes;
    }

    /**
     * 字节转长整数型(小端模式,低地址存放低位)
     * @param bytes
     * @return
     */
    public static long bytes2LongLittleEndian(byte... bytes) {
        long result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result |= ((long) (bytes[i] & 0xFF)) << (i * 8);
            if (i == 7) break;
        }
        return result;
    }

    /**
     * 将字节数组倒序
     * @param bytes
     * @return
     */
    public static byte[] reverse(byte... bytes) {
        int length = bytes.length;
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[length - i - 1] = bytes[i];
        }
        return result;
    }

    /**
     * 数组查找
     * @param source
     * @param target
     * @return
     */
    public static int bytesIndexOf(byte[] source, byte[] target, int begin) {
        if (source == null || target == null || target.length == 0 || source.length < target.length) return -1;

        int i, j, max = source.length - target.length;
        for (i = begin; i <= max; i++) {
            if (source[i] == target[0]) {
                for (j = 1; j < target.length; j++) {
                    if (source[i + j] != target[j]) break;
                }

                if (j == target.length) return i;
            }
        }

        return -1;
    }

    /**
     * BBC
     * @param datas
     * @return
     */
    public static byte bbc(byte... datas){
        byte bbc = 0x00;
        for (int i = 0; i < datas.length; i++) {
            bbc ^= datas[i];
        }
        return bbc;
    }

    /**
     * CRC16-MODBUS
     * @param datas
     * @return
     */
    public static byte[] crc16Modbus(byte... datas) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < datas.length; i++) {
            CRC ^= ((int) datas[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }

        return new byte[]{(byte) ((CRC >> 8) & 0xFF), (byte) (CRC & 0xFF)};
    }
}
