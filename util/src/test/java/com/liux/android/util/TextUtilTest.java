package com.liux.android.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class TextUtilTest {
    @Test
    public void MD5() {
        assertEquals("033bd94b1168d7e4f0d644c3c95e35bf", TextUtil.MD5("TEST"));
    }

    @Test
    public void SHA1() {
        assertEquals("984816fd329622876e14907634264e6f332e9fb3", TextUtil.SHA1("TEST"));
    }

    @Test
    public void SHA224() {
        assertEquals("917ecca24f3e6ceaf52375d8083381f1f80a21e6e49fbadc40afeb8e", TextUtil.SHA224("TEST"));
    }

    @Test
    public void SHA256() {
        assertEquals("94ee059335e587e501cc4bf90613e0814f00a7b08bc7c648fd865a2af6a22cc2", TextUtil.SHA256("TEST"));
    }

    @Test
    public void SHA384() {
        assertEquals("4f37c49c0024445f91977dbc47bd4da9c4de8d173d03379ee19c2bb15435c2c7e624ea42f7cc1689961cb7aca50c7d17", TextUtil.SHA384("TEST"));
    }

    @Test
    public void SHA512() {
        assertEquals("7bfa95a688924c47c7d22381f20cc926f524beacb13f84e203d4bd8cb6ba2fce81c57a5f059bf3d509926487bde925b3bcee0635e4f7baeba054e5dba696b2bf", TextUtil.SHA512("TEST"));
    }

    @Test
    public void digest() {
    }

    @Test
    public void hmacMD5() {
        assertEquals("1aee732e9c1d3faa20775d1438af9472", TextUtil.HmacMD5("TEST", "KEY"));
    }

    @Test
    public void hmacSHA1() {
        assertEquals("7403a7476a1758ff716b8bd405d0ef5574e9cd0a", TextUtil.HmacSHA1("TEST", "KEY"));
    }

    @Test
    public void hmacSHA224() {
        assertEquals("e802b12b9dbfca2785fb1d93864eb984494e110acefe468fea6a3f79", TextUtil.HmacSHA224("TEST", "KEY"));
    }

    @Test
    public void hmacSHA256() {
        assertEquals("615dac1c53c9396d8f69a419a0b2d9393a0461d7ad5f7f3d9beb57264129ef12", TextUtil.HmacSHA256("TEST", "KEY"));
    }

    @Test
    public void hmacSHA384() {
        assertEquals("03ae3f1ba7a6626b24de55db51dbaa498c2baaa58b4a2617f48c8f7eae58a31e70f2d9f82241e1bbb287d89902f2fe3a", TextUtil.HmacSHA384("TEST", "KEY"));
    }

    @Test
    public void hmacSHA512() {
        assertEquals("c0fcb3918e49a7f5af5af7881b9b89951efa39ae1f276237f483d19e0dbc504aaf6febe7e3af4a1dd3bd06ba9de8737b61b903b584d50b78a549a65fa0806100", TextUtil.HmacSHA512("TEST", "KEY"));
    }

    @Test
    public void hmac() {
    }

    @Test
    public void encodeURL() {
        assertEquals("https%3A%2F%2F6xyun.cn%2Fapi%3Fkey%3D%E6%B5%8B%E8%AF%95", TextUtil.encodeURL("https://6xyun.cn/api?key=测试", null));
    }

    @Test
    public void decodeURL() {
        assertEquals("https://6xyun.cn/api?key=测试", TextUtil.decodeURL("https%3a%2f%2f6xyun.cn%2fapi%3fkey%3d%e6%b5%8b%e8%af%95", null));
    }

    @Test
    public void string2Unicode() {
        assertEquals("\\u0054\\u0045\\u0053\\u0054", TextUtil.string2Unicode("TEST"));
    }

    @Test
    public void unicode2String() {
        assertEquals("TEST", TextUtil.unicode2String("\\u0054\\u0045\\u0053\\u0054"));
    }

    @Test
    public void formetByteLength() {
        assertEquals("1.00KB", TextUtil.formetByteLength(1024));
        assertEquals("1.00MB", TextUtil.formetByteLength(1024 * 1024));
        assertEquals("1.00GB", TextUtil.formetByteLength(1024 * 1024 * 1024));
        assertEquals("1.05GB", TextUtil.formetByteLength(1024 * 1024 * 1024 + (1024 * 1024 * 50)));
    }

    @Test
    public void money2Chinese() {
        assertEquals("一亿零二百四十万零九十元整", TextUtil.money2Chinese(102400090));
    }

    @Test
    public void money2ChineseTraditional() {
        assertEquals("壹亿零贰佰肆拾万零玖拾元整", TextUtil.money2ChineseTraditional(102400090));
    }

    @Test
    public void number2Chinese() {
        assertEquals("一亿零二百四十万零九十", TextUtil.number2Chinese(102400090));
    }

    @Test
    public void bytes2Hex() {
        assertEquals("54455354", TextUtil.bytes2Hex("TEST".getBytes()));
        assertEquals("54455354", TextUtil.bytes2Hex("TEST".getBytes(), false));
        assertEquals("54 45 53 54", TextUtil.bytes2Hex("TEST".getBytes(), true));
    }

    @Test
    public void hex2Bytes() {
        assertEquals("TEST", new String(TextUtil.hex2Bytes("54455354")));
        assertEquals("TEST", new String(TextUtil.hex2Bytes("54 45 53 54")));
    }

    @Test
    public void encodeBase64() {
        assertEquals("VEVTVA==", TextUtil.encodeBase64("TEST".getBytes()));
    }

    @Test
    public void decodeBase64() {
        assertArrayEquals(TextUtil.hex2Bytes("54 45 53 54"), TextUtil.decodeBase64("VEVTVA=="));
    }
}