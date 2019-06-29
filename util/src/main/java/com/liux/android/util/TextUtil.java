package com.liux.android.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TextUtil {
    private final String TAG = "TextUtil";

    public static String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String SHA(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String MD5(String input) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(input.getBytes());
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < md.length; i++) {
                String shaHex = Integer.toHexString(md[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    public static byte[] encryptAES(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            return cipher.doFinal(byteContent); // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static byte[] decryptAES(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            return cipher.doFinal(content); // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * URL编码
     * @param data 需要编码的字符串
     * @param enc 字符编码
     * @return 编码后的字符串
     */
    public static String URLEncode(String data, String enc) {
        try {
            if (enc == null || enc.isEmpty()) {
                enc = "utf-8";
            }
            return URLEncoder.encode(data, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * URL解码
     * @param data 需要解码的字符串
     * @param enc 字符编码
     * @return 解码后的字符串
     */
    public static String URLDecode(String data, String enc) {
        try {
            if (enc == null || enc.isEmpty()) {
                enc = "utf-8";
            }
            return URLDecoder.decode(data, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return data;
    }


    /**
     * 字符串转换unicode
     */
    public static String string2Unicode(String string) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            stringBuilder.append("\\u").append(Integer.toHexString(c));
        }
        return stringBuilder.toString();
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {
        StringBuilder string = new StringBuilder();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }

    /**
     * 字符转JSON
     * @param s
     * @return
     */
    public static String string2Json(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"':
                    stringBuilder.append("\\\"");
                    break;
                case '\\':
                    stringBuilder.append("\\\\");
                    break;
                case '/':
                    stringBuilder.append("\\/");
                    break;
                case '\b':
                    stringBuilder.append("\\b");
                    break;
                case '\f':
                    stringBuilder.append("\\f");
                    break;
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                case '\t':
                    stringBuilder.append("\\t");
                    break;
                default:
                    stringBuilder.append(c);
                    break;
            }
        }
        return stringBuilder.toString();
    }

    /**
     * JSON转字符
     * @param json
     * @return
     */
    public static String json2String(String json) {
        if (json.indexOf('"') == 0) json = json.substring(1);
        if (json.lastIndexOf('"') == json.length() - 1) json = json.substring(0, json.length() - 1);
        return json
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\/", "/")
                .replace("\\b", "\b")
                .replace("\\f", "\f")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    /**
     * 格式化字节尺寸
     * @param size
     * @return
     */
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public static String getFormetSize(long size) {
        String fileSizeString = "";
        if (size < 1024) {
            fileSizeString = size + "B";
        } else if (size < 1048576) {
            fileSizeString = decimalFormat.format(size / 1024.0) + "KB";
        } else if (size < 1073741824) {
            fileSizeString = decimalFormat.format(size / 1048576.0) + "MB";
        } else {
            fileSizeString = decimalFormat.format(size / 1073741824.0) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 人民币转中文简体,精确两位小数
     * @param money
     * @return
     */
    public static String getChinaMoney(double money) {
        long yuan = (long) money;

        String result = getChinaNumber(yuan) + "元";
        String jiaoFen = getChinaMoneyDecimal(money);
        if (jiaoFen == null) {
            result += "整";
        } else {
            result += jiaoFen;
        }

        return result;
    }

    /**
     * 人民币转中文繁体,精确两位小数
     * @param money
     * @return
     */
    public static String getChinaMoneyTraditional(double money) {
        return getChinaAccountantNumber(
                getChinaMoney(money)
        );
    }

    /**
     * 整数转中文
     * @param number
     * @return
     */
    public static String getChinaNumber(long number) {
        String sn = String.valueOf(number);
        String result = "";
        if (sn.length() == 1) { // 个
            result += getChinaNumber((int) number);
            return result;
        } else if (sn.length() == 2) { // 十
            if (sn.substring(0, 1).equals("1")) {
                result += "十";
            } else {
                result += (getChinaNumber((int) (number / 10)) + "十");
            }
            result += getChinaNumber(number % 10);
        } else if (sn.length() == 3) { // 百
            result += (getChinaNumber((int) (number / 100)) + "百");
            if (String.valueOf(number % 100).length() < 2 && number % 100 > 0) {
                result += "零";
            }
            result += getChinaNumber(number % 100);
        } else if (sn.length() == 4) { // 千
            result += (getChinaNumber((int) (number / 1000)) + "千");
            if (String.valueOf(number % 1000).length() < 3 && number % 1000 > 0) {
                result += "零";
            }
            result += getChinaNumber(number % 1000);
        } else if (sn.length() < 9) { // 万 - 千万
            result += (getChinaNumber(number / 10000) + "万");
            if (String.valueOf(number % 10000).length() < 4 && number % 10000 > 0) {
                result += "零";
            }
            result += getChinaNumber(number % 10000);
        } else { // 亿及以上
            result += (getChinaNumber(number / 100000000) + "亿");
            if (String.valueOf(number % 100000000).length() < 8 && number % 100000000 > 0) {
                result += "零";
            }
            result += getChinaNumber(number % 100000000);
        }
        return result;
    }

    /**
     * 个位转中文数字
     * @param input
     * @return
     */
    private static String getChinaNumber(int input) {
        String result = "";
        switch (input) {
            case 1:
                result = "一";
                break;
            case 2:
                result = "二";
                break;
            case 3:
                result = "三";
                break;
            case 4:
                result = "四";
                break;
            case 5:
                result = "五";
                break;
            case 6:
                result = "六";
                break;
            case 7:
                result = "七";
                break;
            case 8:
                result = "八";
                break;
            case 9:
                result = "九";
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 中文简体数字转繁体数字
     * @param string
     * @return
     */
    private static String getChinaAccountantNumber(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            String s = string.substring(i, i + 1);
            switch (s) {
                case "零":
                    stringBuilder.append("零");
                    break;
                case "一":
                    stringBuilder.append("壹");
                    break;
                case "二":
                    stringBuilder.append("贰");
                    break;
                case "三":
                    stringBuilder.append("叁");
                    break;
                case "四":
                    stringBuilder.append("肆");
                    break;
                case "五":
                    stringBuilder.append("伍");
                    break;
                case "六":
                    stringBuilder.append("陆");
                    break;
                case "七":
                    stringBuilder.append("柒");
                    break;
                case "八":
                    stringBuilder.append("捌");
                    break;
                case "九":
                    stringBuilder.append("玖");
                    break;
                case "十":
                    stringBuilder.append("拾");
                    break;
                case "百":
                    stringBuilder.append("佰");
                    break;
                case "千":
                    stringBuilder.append("仟");
                    break;
                case "万":
                case "亿":
                default:
                    stringBuilder.append(s);
                    break;
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 人民币转中文人民币小数
     * @param money
     * @return
     */
    private static String getChinaMoneyDecimal(double money) {
        int fen = (int) (money * 100 % 100);
        String result = "";
        if (fen <= 0) return null;
        if (fen > 9) {
            result += getChinaNumber(fen / 10) + "角";
        }
        if (fen % 10 != 0) {
            result += getChinaNumber(fen % 10) + "分";
        }
        return result;
    }

    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return  转换后的Hex字符串
     */
    public static String bytes2Hex(byte[] bytes) {
        if (bytes == null) return "";
        StringBuffer sb = new StringBuffer();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) sb.append(0);
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte数组结果
     */
    public static byte[] hex2Bytes(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            // 奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            // 偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = (byte)Integer.parseInt(inHex.substring(i, i + 2),16);
            j++;
        }
        return result;
    }
}