package com.liux.android.qrcode.util;

import android.content.Context;

public class SizeUtil {
    private static final float DEFAULT_RESULT_SIZE_DP = 30;
    private static final float DEFAULT_BUTTON_SIZE_DP = 30;

    public static int getResultSize(Context context) {
        return dp2px(context, DEFAULT_RESULT_SIZE_DP);
    }

    public static int getButtonSize(Context context) {
        return dp2px(context, DEFAULT_BUTTON_SIZE_DP);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
