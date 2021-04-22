package org.android.framework.validate.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

/**
 * Created by Liux on 2018/1/12.
 */

public class IDVerificationUtil {
    // 校验位
    private static final String[] PARITY_BIT = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
    // 计算位
    private static final String[] CALCULATE_BIT = new String[]{"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
    // 1900/01/01 00:00:00
    private static final Date LAST_CENTURY = new Date(-2209017600000L);
    // 是否返回第二代身份证号
    private static boolean RETURN_SECONDARY = false;

    /**
     * 校验身份证号码
     * @param idStr
     * @param returnSecondary 是否返回处理后的结果,否则返回错误信息或者原始数据
     * @return
     */
    public static String validate_effective(String idStr, boolean returnSecondary) {
        RETURN_SECONDARY = returnSecondary;
        return validate_effective(idStr);
    }

    /**
     * 校验身份证号码
     * @param idStr
     * @return
     */
    public static String validate_effective(String idStr) {
        String idString = idStr.trim();
        if (!(idString.length() == 15 || idString.length() == 18)) {
            return "身份证长度必须为15或者18位！";
        } else {
            if (idString.length() == 15) {
                idString = getSecondary(idString);
            }

            for (int i = 0; i < 17; i++) {
                char str = idString.charAt(i);
                if (str < 48 || str > 57) {
                    return "15位身份证都应该为数字，18位身份证都应该前17位应该都为数字！";
                }
            }

            try {
                Date date = verifyBirthday(idString);
                if (date == null) {
                    return "身份证日期验证无效！";
                }

                if (!date.before(new Date())) {
                    return "身份证日期验证无效！";
                }

                if (!date.after(LAST_CENTURY)) {
                    return "身份证日期验证无效！";
                }

                String birthday = getBirthday(idString);
                String format = getDateFromat().format(date);
                if (!birthday.equals(format)) {
                    return "身份证日期验证无效！";
                }
            } catch (Exception e) {
                return "身份证日期验证无效！";
            }

            Hashtable<String, String> secondary = getSecondary();
            if (secondary.get(idString.substring(0, 2)) == null) {
                return "身份证地区编码错误!";
            } else if (!getCalculateBit((CharSequence) idString).equals(String.valueOf(idString.charAt(17)))) {
                return "身份证最后一位校验码有误！";
            } else {
                return !RETURN_SECONDARY ? idStr : idString;
            }
        }
    }

    /**
     * 获取第二代身份证号码
     * @param idStr
     * @return
     */
    private static String getSecondary(String idStr) {
        StringBuilder stringBuilder = new StringBuilder(18);
        stringBuilder.append(idStr.substring(0, 6));
        stringBuilder.append("19");
        stringBuilder.append(idStr.substring(6));
        stringBuilder.append(getCalculateBit((CharSequence) stringBuilder));
        return stringBuilder.toString();
    }

    /**
     * 获取校验位
     * @param idStr
     * @return
     */
    private static String getCalculateBit(CharSequence idStr) {
        int count = 0;

        for (int i = 0; i < 17; ++i) {
            char str = idStr.charAt(i);
            count += (str - 48) * Integer.parseInt(CALCULATE_BIT[i]);
        }

        return PARITY_BIT[count % 11];
    }

    /**
     * 获取地址信息
     * @return
     */
    private static Hashtable<String, String> secondary;
    private static Hashtable<String, String> getSecondary() {
        if (secondary != null) return secondary;
        Hashtable<String, String> secondary = new Hashtable<>();
        secondary.put("11", "北京");
        secondary.put("12", "天津");
        secondary.put("13", "河北");
        secondary.put("14", "山西");
        secondary.put("15", "内蒙古");
        secondary.put("21", "辽宁");
        secondary.put("22", "吉林");
        secondary.put("23", "黑龙江");
        secondary.put("31", "上海");
        secondary.put("32", "江苏");
        secondary.put("33", "浙江");
        secondary.put("34", "安徽");
        secondary.put("35", "福建");
        secondary.put("36", "江西");
        secondary.put("37", "山东");
        secondary.put("41", "河南");
        secondary.put("42", "湖北");
        secondary.put("43", "湖南");
        secondary.put("44", "广东");
        secondary.put("45", "广西");
        secondary.put("46", "海南");
        secondary.put("50", "重庆");
        secondary.put("51", "四川");
        secondary.put("52", "贵州");
        secondary.put("53", "云南");
        secondary.put("54", "西藏");
        secondary.put("61", "陕西");
        secondary.put("62", "甘肃");
        secondary.put("63", "青海");
        secondary.put("64", "宁夏");
        secondary.put("65", "新疆");
        secondary.put("71", "台湾");
        secondary.put("81", "香港");
        secondary.put("82", "澳门");
        secondary.put("91", "国外");
        return secondary;
    }

    /**
     * 验证出生日期
     * @param idStr
     * @return
     */
    private static Date verifyBirthday(String idStr) {
        try {
            return getDateFromat().parse(getBirthday(idStr));
        } catch (Exception ignore) {}
        return null;
    }

    /**
     * 获取校验日期格式
     * @return
     */
    private static SimpleDateFormat getDateFromat() {
        return new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    }

    /**
     * 获取出生日期
     * @param idStr
     * @return
     */
    private static String getBirthday(String idStr) {
        return idStr.substring(6, 14);
    }
}
