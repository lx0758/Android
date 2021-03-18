package com.liux.android.framework.validate;

import android.widget.TextView;

/**
 * date：2018/5/14 16:42
 * author：t.simeon
 * email：tsimeon@qq.com
 * description：
 */
public class EmailRule extends RegularRule {

    //邮箱正则表达式
    private static String EMAILREGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";

    private EmailRule(String matchVal) {
        super(matchVal, EMAILREGEX);
    }

    public static EmailRule match(String matchVal) {
        return new EmailRule(matchVal);
    }

    public static EmailRule match(TextView textView, boolean trim) {
        String data = textView.getText().toString();
        if (trim) {
            data = data.trim();
        }
        return new EmailRule(data);
    }

}
