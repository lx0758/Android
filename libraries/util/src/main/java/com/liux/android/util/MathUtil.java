package com.liux.android.util;

import java.math.BigDecimal;

/**
 * Created by XRog
 * on 2/28/2017.
 *
 * 2017-6-16
 * BigDecimal使用double创建会出现精度问题,推荐使用String
 * @author Liux
 */
public class MathUtil {

    /**
     * 提供精确加法计算的add方法
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static double add(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(String.valueOf(value1));
        BigDecimal b2 = new BigDecimal(String.valueOf(value2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确减法运算的sub方法
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double sub(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(String.valueOf(value1));
        BigDecimal b2 = new BigDecimal(String.valueOf(value2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确乘法运算的mul方法
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @param scale  精确范围
     * @return 两个参数的积
     */
    public static double mul(double value1, double value2, int scale) {
        if (scale < 0) scale = 2;
        BigDecimal b1 = new BigDecimal(String.valueOf(value1));
        BigDecimal b2 = new BigDecimal(String.valueOf(value2));
        return b1.multiply(b2).divide(new BigDecimal("1"), scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的除法运算方法div
     *
     * @param value1 被除数
     * @param value2 除数
     * @param scale  精确范围
     * @return 两个参数的商
     */
    public static double div(double value1, double value2, int scale) {
        if (scale < 0) scale = 2;
        BigDecimal b1 = new BigDecimal(String.valueOf(value1));
        BigDecimal b2 = new BigDecimal(String.valueOf(value2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的人民币元转分
     *
     * @param yuan 被除数
     * @return 转换后的Long
     */
    public static long yuan2fen(double yuan) {
        BigDecimal b1 = new BigDecimal(String.valueOf(yuan));
        BigDecimal b2 = new BigDecimal("100");
        return b1.multiply(b2).divide(new BigDecimal("1"), 0, BigDecimal.ROUND_HALF_UP).longValue();
    }

    /**
     * 提供精确的分转元
     *
     * @param fen 被除数
     * @param scale  精确范围
     * @return 两个参数的商
     */
    public static double fen2yuan(long fen, int scale) {
        if (scale < 0) scale = 2;
        BigDecimal b1 = new BigDecimal(String.valueOf(fen));
        BigDecimal b2 = new BigDecimal("100");
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 计算下一页的页码
     * @param size 当前数据源尺寸
     * @param count 每页条数
     * @return 下一页的页码
     */
    public static int nextPage(int size, int count) {
        return nextPage(size, count, false);
    }

    /**
     * 计算下一页的页码
     * @param size 当前数据源尺寸
     * @param count 每页条数
     * @param isZero 是否从第0页开始
     * @return 下一页的页码
     */
    public static int nextPage(int size, int count, boolean isZero) {
        if (size == 0) return isZero ? 0 : 1;
        return size / count + ((size % count) == 0 ? 0 : 1) + (isZero ? 0 : 1);
    }
}
