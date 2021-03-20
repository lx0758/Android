package org.android.framework.validate;

import android.widget.TextView;

import java.util.regex.Pattern;

public class PhoneRule extends Rule {
    private static final String REG_MOBILE_PHONE_CONTAIN_VIRTUAL = "^(13[0-9]|14[1456789]|15[012356789]|16[6]|17[01345678]|18[0-9]|19[89])[0-9]{8}$";
    private static final String REG_MOBILE_PHONE_EXCLUDE_VIRTUAL = "^(13[0-9]|14[579]|15[012356789]|16[6]|17[35678]|18[0-9]|19[89])[0-9]{8}$";
    private static final String REG_TELEPHONE = "^0\\d{2,3}-?[1-9]\\d{4,7}$";

    private Mode mode;
    private String phone;

    private PhoneRule(String phone, Mode mode) {
        this.phone = phone;
        this.mode = mode;
    }

    public static PhoneRule isValid(String phone, Mode mode) {
        return new PhoneRule(phone, mode);
    }

    public static PhoneRule isValid(TextView textView, boolean trim, Mode mode) {
        String phone = textView.getText().toString();
        if (trim) {
            phone = phone.trim();
        }
        return isValid(phone, mode);
    }

    @Override
    public boolean valid() {
        switch (mode) {
            case MODE_MOBILE_PHONE_CONTAIN_VIRTUAL:
                return Pattern.matches(REG_MOBILE_PHONE_CONTAIN_VIRTUAL, phone);
            case MODE_MOBILE_PHONE_EXCLUDE_VIRTUAL:
                return Pattern.matches(REG_MOBILE_PHONE_EXCLUDE_VIRTUAL, phone);
            case MODE_TELEPHONE:
                return Pattern.matches(REG_TELEPHONE, phone);
            case MODE_MOBILE_PHONE_CONTAIN_VIRTUAL_AND_TELEPHONE:
                return Pattern.matches(REG_MOBILE_PHONE_EXCLUDE_VIRTUAL, phone) || Pattern.matches(REG_TELEPHONE, phone);
            case MODE_MOBILE_PHONE_EXCLUDE_VIRTUAL_AND_TELEPHONE:
                return Pattern.matches(REG_MOBILE_PHONE_CONTAIN_VIRTUAL, phone) || Pattern.matches(REG_TELEPHONE, phone);
            default:
                return false;
        }
    }

    public enum Mode{
        /**
         * 手机号包含虚拟号段
         */
        MODE_MOBILE_PHONE_CONTAIN_VIRTUAL,
        /**
         * 手机号排除虚拟号段
         */
        MODE_MOBILE_PHONE_EXCLUDE_VIRTUAL,
        /**
         * 电话号码
         */
        MODE_TELEPHONE,
        /**
         * 手机号码包含虚拟号段和电话号码
         */
        MODE_MOBILE_PHONE_CONTAIN_VIRTUAL_AND_TELEPHONE,
        /**
         * 手机号码排除虚拟号段和电话号码
         */
        MODE_MOBILE_PHONE_EXCLUDE_VIRTUAL_AND_TELEPHONE
    }
}
