package com.liux.android.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class RegexUtilTest {

    @Test
    public void isIdentityNumber() {
        assertTrue(RegexUtil.isIdentityNumber("510101199001011119"));
        assertFalse(RegexUtil.isIdentityNumber("510101199001011110"));
    }

    @Test
    public void isCellphoneNumber() {
        assertTrue(RegexUtil.isCellphoneNumber("13086668581"));
        assertTrue(RegexUtil.isCellphoneNumber("17108387204", true));
        assertFalse(RegexUtil.isCellphoneNumber("17108387204", false));
    }

    @Test
    public void isPhoneNumber() {
        assertTrue(RegexUtil.isPhoneNumber("08387450459"));
        assertTrue(RegexUtil.isPhoneNumber("0838-7450459"));
        assertTrue(RegexUtil.isPhoneNumber("8608387450459"));
        assertTrue(RegexUtil.isPhoneNumber("+8608387450459"));
    }

    @Test
    public void isAuthCode() {
        assertTrue(RegexUtil.isAuthCode("001235"));
        assertTrue(RegexUtil.isAuthCode("1232", 4, 8));
    }

    @Test
    public void isName() {
        assertTrue(RegexUtil.isName("李荣浩"));
        assertTrue(RegexUtil.isName("尼格买提·买买提·阿凡提"));
    }

    @Test
    public void isVehicleLicence() {
        assertTrue(RegexUtil.isVehicleLicence("川A12345"));
        assertTrue(RegexUtil.isVehicleLicence("川A54374D"));
    }

    @Test
    public void isEmail() {
        assertTrue(RegexUtil.isEmail("name_001@qq.com"));
    }

    @Test
    public void isBankCard() {
        assertTrue(RegexUtil.isBankCard("6222600260001072444"));
    }

    @Test
    public void isLowerMoney() {
        assertTrue(RegexUtil.isLowerMoney("1234567890.73"));
        assertFalse(RegexUtil.isLowerMoney("-1234567890.73"));
    }

    @Test
    public void isInteger() {
        assertTrue(RegexUtil.isInteger("64646416846", Long.MIN_VALUE, Long.MAX_VALUE));
    }

    @Test
    public void isPassword() {
        assertTrue(RegexUtil.isPassword("123456789"));
    }

    @Test
    public void isComplexPassword() {
        assertFalse(RegexUtil.isComplexPassword("123456789"));
        assertFalse(RegexUtil.isComplexPassword("qwerQWER"));
        assertTrue(RegexUtil.isComplexPassword("QWER0000"));
        assertTrue(RegexUtil.isComplexPassword("QWER0000.~@.,;'"));
    }

    @Test
    public void isIpAddress() {
        assertTrue(RegexUtil.isIpAddress("0.0.0.0"));
        assertTrue(RegexUtil.isIpAddress("192.168.1.255"));
        assertTrue(RegexUtil.isIpAddress("fe80:0:0:0:20c:29ff:fe6b:2516"));
        assertTrue(RegexUtil.isIpAddress("1040::1"));
    }

    @Test
    public void isIp4Address() {
        assertTrue(RegexUtil.isIp4Address("0.0.0.0"));
        assertTrue(RegexUtil.isIp4Address("192.168.1.255"));
    }

    @Test
    public void isIp6Address() {
        assertTrue(RegexUtil.isIp6Address("fe80:0:0:0:20c:29ff:fe6b:2516"));
        assertTrue(RegexUtil.isIp6Address("1040::1"));
    }
}