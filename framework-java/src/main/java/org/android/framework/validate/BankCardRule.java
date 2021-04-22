package org.android.framework.validate;

import android.widget.TextView;

/**
 * Create by zzw on 2018/11/29
 */
public class BankCardRule extends RegularRule {

    private static String REGEX = "^\\d{15,21}$";

    protected BankCardRule(String matchVal) {
        super(matchVal, REGEX);
    }

    public static BankCardRule match(String matchVal) {
        return new BankCardRule(matchVal);
    }

    public static BankCardRule match(TextView textView, boolean trim) {
        String data = textView.getText().toString();
        if (trim) {
            data = data.trim();
        }
        return new BankCardRule(data);
    }
}
