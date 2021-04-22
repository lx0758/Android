package com.liux.android.util;

/**
 * Created by Liux on 2017/01/02.
 */

public class RegexUtil {

    /**
     * 验证身份证
     * 必须满足以下规则
     * 1. 长度必须是 18 位，前 17 位必须是数字，第 10 位可以是数字或字母 x 或 X
     * 2. 前两位必须是以下情形中的一种：11,12,13,14,15,21,22,23,31,32,33,34,35,36,37,41,42,43,44,45,46,50,51,52,53,54,61,62,63,64,65,71,81,82,91
     * 3. 第 7 到第 14 位出生年月日。第 7 到第 10 位为出生年份； 11 到 12 位表示月份，范围为 01-12 ； 13 到 14 位为合法的日期
     * 4. 第 17 位表示性别，双数表示女，单数表示男
     * 5. 第 18 位为前 17 位的校验位
     * 6. 出生年份的前两位必须是 19 或 20
     * 校验位算法如下：
     * （1）校验和 = (n1 + n11) * 7 + (n2 + n12) * 9 + (n3 + n13) * 10 + (n4 + n14) * 5 + (n5 + n15) * 8 + (n6 + n16) * 4 + (n7 + n17) * 2 + n8 + n9 * 6 + n10 * 3，其中n数值，表示第几位的数字
     * （2）余数 ＝ 校验和 % 11
     * （3）如果余数为 0 ，校验位应为 1 ，余数为 1 到 10 校验位应为字符串“0X98765432”(不包括分号)的第余数位的值（比如余数等于 3 ，校验位应为 9 ）
     * @param num
     * @return
     */
    public static boolean isIdentityNumber(String num) {
        // 地区码
        String area = "(1[1-5]|2[1-3]|3[1-7]|4[1-6]|5[0-4]|6[1-5]|82|[7-9]1)[0-9]{4}";
        // 普通年份
        String year = "(19|20)[0-9]{2}";
        String mmdd = "(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8])))";
        String yearMmdd = year + mmdd;
        // 闰年
        String leapYear = "((19|20)(0[48]|[2468][048]|[13579][26])|2000)";
        String leapMmdd = "0229";
        String leapyearMmdd = leapYear + leapMmdd;
        // 后四位
        String mantissa = "[0-9]{3}[0-9Xx]";
        // 组装
        String regex = String.format("^%s((%s)|(%s))%s$", area, yearMmdd, leapyearMmdd, mantissa);

        num = num.replace(" ", "");
        if (!num.matches(regex)) return false;

        int summary = 0
                + (Integer.valueOf(num.substring(0, 1)) + Integer.valueOf(num.substring(10, 11))) * 7
                + (Integer.valueOf(num.substring(1, 2)) + Integer.valueOf(num.substring(11, 12))) * 9
                + (Integer.valueOf(num.substring(2, 3)) + Integer.valueOf(num.substring(12, 13))) * 10
                + (Integer.valueOf(num.substring(3, 4)) + Integer.valueOf(num.substring(13, 14))) * 5
                + (Integer.valueOf(num.substring(4, 5)) + Integer.valueOf(num.substring(14, 15))) * 8
                + (Integer.valueOf(num.substring(5, 6)) + Integer.valueOf(num.substring(15, 16))) * 4
                + (Integer.valueOf(num.substring(6, 7)) + Integer.valueOf(num.substring(16, 17))) * 2
                + Integer.valueOf(num.substring(7, 8)) *1
                + Integer.valueOf(num.substring(8, 9)) * 6
                + Integer.valueOf(num.substring(9, 10)) * 3;
        int  remainder = summary % 11;
        String check = "10X98765432".substring(remainder, remainder + 1);
        return check.equals(num.substring(17, 18));
    }

    /**
     * 是否是手机号码
     * @param num
     * @return
     */
    public static boolean isCellphoneNumber(String num) {
        return isCellphoneNumber(num, true);
    }

    /**
     * 是否是手机号码
     * @param num
     * @param virtual 是否包含虚拟号段
     * @return
     */
    public static boolean isCellphoneNumber(String num, boolean virtual) {
        if (num == null) return false;
        num = num.replace(" ", "");
        num = num.replaceAll("^\\+?86", "");
        if (!virtual) {
            // 不包含虚拟号段
            return num.matches("^(13[0-9]|14[579]|15[012356789]|17[35678]|18[0-9])[0-9]{8}$");
        } else {
            // 包含虚拟号段
            return num.matches("^(13[0-9]|14[1456789]|15[012356789]|16[6]|17[01345678]|18[0-9]|19[89])[0-9]{8}$");
        }
    }

    /**
     * 是否是座机号码
     * @param num
     * @return
     */
    public static boolean isPhoneNumber(String num) {
        if (num == null) return false;
        num = num.replace(" ", "");
        num = num.replaceAll("^\\+?86", "");
        return num.matches("^0\\d{2,3}-?[1-9]\\d{4,7}$");
    }

    /**
     * 是否是纯数字验证码(4-8位)
     * @param num
     * @return
     */
    public static boolean isAuthCode(String num) {
        return isAuthCode(num, 4, 8);
    }

    /**
     * 是否是纯数字验证码
     * @param num
     * @param min
     * @param max
     * @return
     */
    public static boolean isAuthCode(String num, int min, int max) {
        if (num == null) return false;
        return num.matches("^\\d{" + min + "," + max + "}$");
    }

    /**
     * 是否是中文姓名（包含少数民族）
     * @param name
     * @return
     */
    public static boolean isName(String name) {
        if (name == null) return false;
        return name.matches("^[\\u4e00-\\u9fa5]+([·•][\\u4e00-\\u9fa5]+)*$");
    }

    /**
     * 是否是一个车牌号码
     * @param license
     * @return
     */
    public static boolean isVehicleLicence(String license) {
        if (license == null) return false;
        return license.matches("^(([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[DF])|([DF]([A-HJ-NP-Z0-9])[0-9]{4})))|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳使领]))$");
    }

    /**
     * 是否是一个邮箱地址
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null) return false;
        return email.matches("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
    }

    /**
     * 是否是一个银行卡号码
     * @param card
     * @return
     */
    public static boolean isBankCard(String card) {
        if (card == null) return false;
        card = card.replace(" ", "");
        return card.matches("^\\d{16,19}$");
    }

    /**
     * 是否是金钱数量(0.00)
     * @param money
     * @return
     */
    public static boolean isLowerMoney(String money) {
        if (money == null) return false;
        money = money.replace(",", "");
        return money.matches("^\\d+\\.?\\d{0,2}$");
    }

    /**
     * 是否是一个数字并且在一个区间
     * @param integer
     * @param min
     * @param max
     * @return
     */
    public static boolean isInteger(String integer, long min, long max) {
        if (integer == null) return false;
        if (!integer.matches("^-?[0-9](\\d)*$")) return false;
        long num;
        try {
            num = Long.valueOf(integer);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        return num >= min && num <= max;
    }

    /**
     * 是否是一个密码
     * @param password
     * @return
     */
    public static boolean isPassword(String password) {
        if (password == null) return false;

        if (password.length() < 6 || password.length() > 16) return false;

        return password.matches("^[0-9a-zA-Z~!@#\\$%\\^&\\*\\(\\)_\\\\+-=:\";'<>?,\\.\\/]{6,16}$");
    }

    /**
     * 是否是一个复杂密码
     * @param password
     * @return
     */
    public static boolean isComplexPassword(String password) {
        if (!isPassword(password)) return false;
        return password.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z`~!@#$%^&*()\\-_=+\\[\\]{};:'\"\\\\|,<.>/?]{6,16}$");
    }

    /**
     * 是否是一个合法的IP地址
     * @param address
     * @return
     */
    public static boolean isIpAddress(String address) {
        if (address == null) return false;
        return isIp4Address(address) || isIp6Address(address);
    }

    /**
     * 是否是一个合法的IPV4地址
     * @param address
     * @return
     */
    public static boolean isIp4Address(String address) {
        if (address == null) return false;
        return address.matches("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");
    }

    /**
     * 是否是一个合法的IPV6地址
     * @param address
     * @return
     */
    public static boolean isIp6Address(String address) {
        if (address == null) return false;
        return address.matches("^s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]d|1dd|[1-9]?d)(.(25[0-5]|2[0-4]d|1dd|[1-9]?d)){3}))|:)))(%.+)?s*$");
    }
}
