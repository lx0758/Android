package com.liux.android.util;

public class BitUtil {

    public static String int2Bin(int source) {
        return Integer.toBinaryString(source);
    }

    public static int bin2Int(String source) {
        if (source.startsWith("0b")) source = source.substring(2);
        return Integer.parseInt(source, 2);
    }

    public static String long2Bin(long source) {
        return Long.toBinaryString(source);
    }

    public static long bin2Long(String source) {
        if (source.startsWith("0b")) source = source.substring(2);
        return Long.parseLong(source, 2);
    }

    public static boolean getIntBit(int source, int pos) {
        pos = pos - 1;
        int bitValue = 1 << pos;
        return (source & bitValue) == bitValue;
    }

    public static int setIntBit(int source, int pos, boolean value) {
        if (getIntBit(source, pos) == value) return source;
        pos = pos - 1;
        int bitValue = 1 << pos;
        if (value) {
            return source | bitValue;
        } else {
            return source ^ bitValue;
        }
    }

    public static boolean getLongBit(long source, int pos) {
        pos = pos - 1;
        long bitValue = 1 << pos;
        return (source & bitValue) == bitValue;
    }

    public static long setLongBit(long source, int pos, boolean value) {
        if (getLongBit(source, pos) == value) return source;
        pos = pos - 1;
        long bitValue = 1 << pos;
        if (value) {
            return source | bitValue;
        } else {
            return source ^ bitValue;
        }
    }
}
