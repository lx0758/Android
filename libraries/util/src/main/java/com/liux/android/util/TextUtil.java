package com.liux.android.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.zip.CRC32;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TextUtil {
    private final String TAG = "TextUtil";

    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static String md5(String content) {
        return md5(content.getBytes());
    }

    public static String md5(byte[] bytes) {
        return bytes2Hex(digest(bytes, "MD5"));
    }

    public static String sha1(String content) {
        return sha1(content.getBytes());
    }

    public static String sha1(byte[] bytes) {
        return bytes2Hex(digest(bytes, "SHA-1"));
    }

    public static String sha224(String content) {
        return sha224(content.getBytes());
    }

    public static String sha224(byte[] bytes) {
        return bytes2Hex(digest(bytes, "SHA-224"));
    }

    public static String sha256(String content) {
        return sha256(content.getBytes());
    }

    public static String sha256(byte[] bytes) {
        return bytes2Hex(digest(bytes, "SHA-256"));
    }

    public static String sha384(String content) {
        return sha384(content.getBytes());
    }

    public static String sha384(byte[] bytes) {
        return bytes2Hex(digest(bytes, "SHA-384"));
    }

    public static String sha512(String content) {
        return sha512(content.getBytes());
    }

    public static String sha512(byte[] bytes) {
        return bytes2Hex(digest(bytes, "SHA-512"));
    }

    /**
     * 散列算法
     * @param data
     * @param algorithm
     * @return
     */
    public static byte[] digest(byte[] data, String algorithm) {
        if (data == null) return null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(data);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String crc32(String data) {
        return crc32(data.getBytes());
    }

    public static String crc32(byte[] data) {
        if (data == null) throw new NullPointerException();
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        long result = crc32.getValue();
        byte[] bytes = ByteUtil.long2BytesBigEndian(result);
        return bytes2Hex(
                bytes[4],
                bytes[5],
                bytes[6],
                bytes[7]
        );
    }

    public static String crc64(String data) {
        return crc64(data.getBytes());
    }

    public static String crc64(byte[] data) {
        if (data == null) throw new NullPointerException();
        CRC64 crc64 = new CRC64();
        crc64.update(data);
        long result = crc64.getValue();
        byte[] bytes = ByteUtil.long2BytesBigEndian(result);
        return bytes2Hex(bytes);
    }

    public static String hmacMD5(String content, String key) {
        return bytes2Hex(hmac(content.getBytes(), "HmacMD5", key.getBytes()));
    }

    public static String hmacSHA1(String content, String key) {
        return bytes2Hex(hmac(content.getBytes(), "HmacSHA1", key.getBytes()));
    }

    public static String hmacSHA224(String content, String key) {
        return bytes2Hex(hmac(content.getBytes(), "HmacSHA224", key.getBytes()));
    }

    public static String hmacSHA256(String content, String key) {
        return bytes2Hex(hmac(content.getBytes(), "HmacSHA256", key.getBytes()));
    }

    public static String hmacSHA384(String content, String key) {
        return bytes2Hex(hmac(content.getBytes(), "HmacSHA384", key.getBytes()));
    }

    public static String hmacSHA512(String content, String key) {
        return bytes2Hex(hmac(content.getBytes(), "HmacSHA512", key.getBytes()));
    }

    public static byte[] hmac(byte[] data, String algorithm, byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, algorithm);
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException|InvalidKeyException e) {
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
    public static String encodeURL(String data, String enc) {
        try {
            if (isEmpty(enc)) enc = "utf-8";
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
    public static String decodeURL(String data, String enc) {
        try {
            if (isEmpty(enc)) enc = "utf-8";
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
            char c = string.charAt(i);
            stringBuilder.append("\\u");
            String hex = Integer.toHexString(c);
            switch (hex.length()) {
                case 2:
                    stringBuilder.append("00");
                    break;
                case 3:
                    stringBuilder.append("0");
                    break;
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {
        StringBuilder stringBuilder = new StringBuilder();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            stringBuilder.append((char) data);
        }

        return stringBuilder.toString();
    }

    /**
     * 格式化字节尺寸
     * @param size
     * @return
     */
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public static String formetByteLength(long size) {
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
    public static String money2Chinese(double money) {
        long yuan = (long) money;

        String result = number2Chinese(yuan) + "元";
        String jiaoFen = null;
        int fen = (int) (money * 100 % 100);
        if (fen > 0) {
            jiaoFen = "";
            if (fen > 9) {
                jiaoFen += number2Chinese(fen / 10) + "角";
            }
            if (fen % 10 != 0) {
                jiaoFen += number2Chinese(fen % 10) + "分";
            }
        }
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
    public static String money2ChineseTraditional(double money) {
        return number2Traditional(
                money2Chinese(money)
        );
    }

    /**
     * 整数转中文
     * @param number
     * @return
     */
    public static String number2Chinese(long number) {
        String sn = String.valueOf(number);
        String result = "";
        if (sn.length() == 1) { // 个
            result += number2Chinese((int) number);
            return result;
        } else if (sn.length() == 2) { // 十
            if (sn.substring(0, 1).equals("1")) {
                result += "十";
            } else {
                result += (number2Chinese((int) (number / 10)) + "十");
            }
            result += number2Chinese(number % 10);
        } else if (sn.length() == 3) { // 百
            result += (number2Chinese((int) (number / 100)) + "百");
            if (String.valueOf(number % 100).length() < 2 && number % 100 > 0) {
                result += "零";
            }
            result += number2Chinese(number % 100);
        } else if (sn.length() == 4) { // 千
            result += (number2Chinese((int) (number / 1000)) + "千");
            if (String.valueOf(number % 1000).length() < 3 && number % 1000 > 0) {
                result += "零";
            }
            result += number2Chinese(number % 1000);
        } else if (sn.length() < 9) { // 万 - 千万
            result += (number2Chinese(number / 10000) + "万");
            if (String.valueOf(number % 10000).length() < 4 && number % 10000 > 0) {
                result += "零";
            }
            result += number2Chinese(number % 10000);
        } else { // 亿及以上
            result += (number2Chinese(number / 100000000) + "亿");
            if (String.valueOf(number % 100000000).length() < 8 && number % 100000000 > 0) {
                result += "零";
            }
            result += number2Chinese(number % 100000000);
        }
        return result;
    }

    /**
     * 个位转中文数字
     * @param input
     * @return
     */
    private static String number2Chinese(int input) {
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
    private static String number2Traditional(String string) {
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
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return  转换后的Hex字符串
     */
    public static String bytes2Hex(byte... bytes) {
        return bytes2Hex(bytes, false);
    }

    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @param space 是否填充空格
     * @return  转换后的Hex字符串
     */
    public static String bytes2Hex(byte[] bytes, boolean space) {
        if (bytes == null) return "";
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) sb.append(0);
            sb.append(hex);
            if (space) sb.append(' ');
        }
        if (space) sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte数组结果
     */
    public static byte[] hex2Bytes(String inHex) {
        if (inHex == null) return new byte[0];
        inHex = inHex.replace(" ", "");
        int hexlen = inHex.length();
        if (hexlen % 2 == 1) {
            // 奇数个字符高位补0
            hexlen++;
            inHex = "0" + inHex;
        }
        byte[] result = new byte[(hexlen / 2)];
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = (byte) Integer.parseInt(inHex.substring(i, i + 2),16);
            j++;
        }
        return result;
    }

    /**
     * Base64 编码
     * @param content
     * @return
     */
    public static String encodeBase64(byte[] content) {
        return Base64.encodeToString(content, Base64.NO_WRAP);
    }

    /**
     * Base64 解码
     * @param content
     * @return
     */
    public static byte[] decodeBase64(String content) {
        return Base64.decode(content, Base64.NO_WRAP);
    }

    public static class CRC64 {

        private static final long poly = 0xC96C5795D7870F42L;
        private static final long crcTable[] = new long[256];

        private long crc = -1;

        static {
            for (int b = 0; b < crcTable.length; ++b) {
                long r = b;
                for (int i = 0; i < 8; ++i) {
                    if ((r & 1) == 1)
                        r = (r >>> 1) ^ poly;
                    else
                        r >>>= 1;
                }

                crcTable[b] = r;
            }
        }

        public CRC64() {
        }

        public void update(byte b) {
            crc = crcTable[(b ^ (int)crc) & 0xFF] ^ (crc >>> 8);
        }

        public void update(byte[] buf) {
            update(buf, 0, buf.length);
        }

        public void update(byte[] buf, int off, int len) {
            int end = off + len;

            while (off < end)
                crc = crcTable[(buf[off++] ^ (int)crc) & 0xFF] ^ (crc >>> 8);
        }

        public long getValue() {
            return ~crc;
        }
    }
}