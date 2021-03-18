package com.liux.android.framework.validate;

import android.widget.TextView;

/**
 * Created by LuoHaifeng on 2018/4/24 0024.
 * Email:496349136@qq.com
 */
public class LengthRule extends Rule {
    public static final int FLAG_EXCLUSIVE_EXCLUSIVE = 0;
    public static final int FLAG_EXCLUSIVE_INCLUSIVE = 1;
    public static final int FLAG_INCLUSIVE_EXCLUSIVE = 2;
    public static final int FLAG_INCLUSIVE_INCLUSIVE = 3;

    private String value;
    private int maxLength;
    private int minLength;
    private int flag;


    public LengthRule(String value, int minLength, int maxLength, int flag) {
        this.value = value;
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.flag = flag;
        if (flag != FLAG_INCLUSIVE_INCLUSIVE && flag != FLAG_EXCLUSIVE_EXCLUSIVE && flag != FLAG_EXCLUSIVE_INCLUSIVE && flag != FLAG_INCLUSIVE_EXCLUSIVE) {
            throw new IllegalArgumentException("not support flag:" + flag);
        }
    }

    public static LengthRule lt(String value, int maxValue) {
        return new LengthRule(value, Integer.MIN_VALUE, maxValue, LengthRule.FLAG_INCLUSIVE_INCLUSIVE);
    }

    public static LengthRule lt(TextView textView, boolean trim, int maxValue) {
        String value = textView.getText().toString();
        if (trim) {
            value = value.trim();
        }
        return new LengthRule(value, Integer.MIN_VALUE, maxValue, LengthRule.FLAG_INCLUSIVE_INCLUSIVE);
    }

    public static LengthRule lt(String value, int maxValue, int flag) {
        return new LengthRule(value, Integer.MIN_VALUE, maxValue, flag);
    }

    public static LengthRule lt(TextView textView, boolean trim, int maxValue, int flag) {
        String value = textView.getText().toString();
        if (trim) {
            value = value.trim();
        }
        return new LengthRule(value, Integer.MIN_VALUE, maxValue, flag);
    }

    public static LengthRule gt(String value, int minValue) {
        return new LengthRule(value, minValue, Integer.MAX_VALUE, LengthRule.FLAG_INCLUSIVE_INCLUSIVE);
    }

    public static LengthRule gt(TextView textView, boolean trim, int minValue) {
        String value = textView.getText().toString();
        if (trim) {
            value = value.trim();
        }
        return new LengthRule(value, minValue, Integer.MAX_VALUE, LengthRule.FLAG_INCLUSIVE_INCLUSIVE);
    }

    public static LengthRule gt(String value, int minValue, int flag) {
        return new LengthRule(value, minValue, Integer.MAX_VALUE, flag);
    }

    public static LengthRule gt(TextView textView, boolean trim, int minValue, int flag) {
        String value = textView.getText().toString();
        if (trim) {
            value = value.trim();
        }
        return new LengthRule(value, minValue, Integer.MAX_VALUE, flag);
    }

    public static LengthRule range(String value, int minValue, int maxValue) {
        return new LengthRule(value, minValue, maxValue, LengthRule.FLAG_INCLUSIVE_INCLUSIVE);
    }

    public static LengthRule range(TextView textView, boolean trim, int minValue, int maxValue) {
        String value = textView.getText().toString();
        if (trim) {
            value = value.trim();
        }
        return new LengthRule(value, minValue, maxValue, LengthRule.FLAG_INCLUSIVE_INCLUSIVE);
    }

    public static LengthRule range(String value, int minValue, int maxValue, int flag) {
        return new LengthRule(value, minValue, maxValue, flag);
    }

    public static LengthRule range(TextView textView, boolean trim, int minValue, int maxValue, int flag) {
        String value = textView.getText().toString();
        if (trim) {
            value = value.trim();
        }
        return new LengthRule(value, minValue, maxValue, flag);
    }

    @Override
    public boolean valid() {
        int length = 0;
        if (value != null) {
            length = value.length();
        }

        switch (flag) {
            case FLAG_INCLUSIVE_INCLUSIVE:
                return length >= minLength && length <= maxLength;
            case FLAG_EXCLUSIVE_EXCLUSIVE:
                return length > minLength && length < maxLength;
            case FLAG_INCLUSIVE_EXCLUSIVE:
                return length >= minLength && length < maxLength;
            case FLAG_EXCLUSIVE_INCLUSIVE:
                return length > minLength && length <= maxLength;
        }
        return false;
    }
}
