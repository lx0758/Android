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
     * 整数转中文计数
     * @param intInput
     * @return
     */
    public static String getCHString(int intInput) {
        String si = String.valueOf(intInput);
        String ss = "";
        if (si.length() == 1) { // 个
            ss += getCH(intInput);
            return ss;
        } else if (si.length() == 2) { // 十
            if (si.substring(0, 1).equals("1")) {
                ss += "十";
            } else {
                ss += (getCH(intInput / 10) + "十");
            }
            ss += getCHString(intInput % 10);
        } else if (si.length() == 3) { // 百
            ss += (getCH(intInput / 100) + "百");
            if (String.valueOf(intInput % 100).length() < 2) {
                ss += "零";
            }
            ss += getCHString(intInput % 100);
        } else if (si.length() == 4) { // 千
            ss += (getCH(intInput / 1000) + "千");
            if (String.valueOf(intInput % 1000).length() < 3) {
                ss += "零";
            }
            ss += getCHString(intInput % 1000);
        } else if (si.length() == 5) { // 万
            ss += (getCH(intInput / 10000) + "万");
            if (String.valueOf(intInput % 10000).length() < 4) {
                ss += "零";
            }
            ss += getCHString(intInput % 10000);
        }
        return ss;
    }

    /**
     * 格式化文件大小
     * @param size
     * @return
     */
    public static String getFormetSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    private static String getCH(int input) {
        String string = "";
        switch (input) {
            case 1:
                string = "一";
                break;
            case 2:
                string = "二";
                break;
            case 3:
                string = "三";
                break;
            case 4:
                string = "四";
                break;
            case 5:
                string = "五";
                break;
            case 6:
                string = "六";
                break;
            case 7:
                string = "七";
                break;
            case 8:
                string = "八";
                break;
            case 9:
                string = "九";
                break;
            default:
                break;
        }
        return string;
    }
}