package org.android.framework.validate;

import android.widget.TextView;

/**
 * date：2018/11/29 18:14
 * author：Liux
 * email：lx0758@qq.com
 * description：
 */

public class NameRule extends RegularRule {

    private static String REGEX = "^[\\u4e00-\\u9fa5]+([·•][\\u4e00-\\u9fa5]+)*$";

    protected NameRule(String matchVal) {
        super(matchVal, REGEX);
    }

    public static NameRule match(String matchVal) {
        return new NameRule(matchVal);
    }

    public static NameRule match(TextView textView, boolean trim) {
        String data = textView.getText().toString();
        if (trim) {
            data = data.trim();
        }
        return new NameRule(data);
    }
}
