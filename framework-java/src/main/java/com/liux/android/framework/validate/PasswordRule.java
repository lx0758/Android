package com.liux.android.framework.validate;

import android.widget.TextView;

public class PasswordRule extends RegularRule {

    private static String REGEX = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";

    private PasswordRule(String matchVal) {
        super(matchVal, REGEX);
    }

    public static PasswordRule match(String matchVal) {
        return new PasswordRule(matchVal);
    }

    public static PasswordRule match(TextView textView, boolean trim) {
        String data = textView.getText().toString();
        if (trim) {
            data = data.trim();
        }
        return new PasswordRule(data);
    }

}
