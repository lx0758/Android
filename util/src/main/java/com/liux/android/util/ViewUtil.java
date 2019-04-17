package com.liux.android.util;

import android.view.View;

/**
 * date：2019/1/28 16:35
 * author：m.tences
 * email：m.tences@qq.com
 * description：设置指定view是否可以点击
 */
public class ViewUtil {

    public static void setEnable(boolean enable, View... views) {
        if (views == null) return;
        for (View view : views) {
            if (view == null) continue;
            view.setEnabled(enable);
        }
    }

    public static void setReadOnly(boolean readOnly, View... views) {
        if (views == null) return;
        for (View view : views) {
            if (view == null) continue;
            view.setFocusable(readOnly);
            view.setFocusableInTouchMode(readOnly);
        }
    }
}
