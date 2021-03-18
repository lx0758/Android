package com.liux.android.framework.validate;

import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by LuoHaifeng on 2018/4/24 0024.
 * Email:496349136@qq.com
 */
public class EmptyRule extends Rule {
    private Object object;
    private boolean isAssertNotEmpty;

    public EmptyRule(Object object, boolean isAssertNotEmpty) {
        this.object = object;
        this.isAssertNotEmpty = isAssertNotEmpty;
    }

    public static EmptyRule isEmpty(String data) {
        return new EmptyRule(data, false);
    }

    public static EmptyRule notEmpty(String object) {
        return new EmptyRule(object, true);
    }

    public static EmptyRule isEmpty(Object data) {
        return new EmptyRule(data, false);
    }

    public static EmptyRule notEmpty(Object object) {
        return new EmptyRule(object, true);
    }

    public static EmptyRule emptyable(TextView textView, boolean trim, boolean isAssertNotEmpty) {
        String data = textView.getText().toString();
        if (trim) {
            data = data.trim();
        }
        return new EmptyRule(data, isAssertNotEmpty);
    }

    public static EmptyRule isEmpty(TextView textView, boolean trim) {
        return emptyable(textView, trim, false);
    }

    public static EmptyRule notEmpty(TextView textView, boolean trim) {
        return emptyable(textView, trim, true);
    }

    @Override
    public boolean valid() {
        if (object instanceof CharSequence) {
            return isAssertNotEmpty != TextUtils.isEmpty((CharSequence) object);
        } else {
            return isAssertNotEmpty != (object == null);
        }
    }
}
