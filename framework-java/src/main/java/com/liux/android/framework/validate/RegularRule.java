package com.liux.android.framework.validate;

import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * date：2018/5/14 16:42
 * author：t.simeon
 * email：tsimeon@qq.com
 * description：通用正则验证
 */
public class RegularRule extends Rule {

    private String regex;
    private String matchVal;

    protected RegularRule(String matchVal, String regex) {
        this.regex = regex;
        this.matchVal = matchVal;
    }

    public static RegularRule match(String matchVal, String regex) {
        return new RegularRule(matchVal, regex);
    }

    public static RegularRule match(TextView textView, boolean trim, String regex) {
        String data = textView.getText().toString();
        if (trim) {
            data = data.trim();
        }
        return match(data, regex);
    }

    @Override
    public boolean valid() {
        if (regex == null || matchVal == null)
            return false;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(matchVal);
        return m.matches();
    }
}
