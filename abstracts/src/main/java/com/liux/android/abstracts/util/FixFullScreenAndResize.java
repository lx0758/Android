package com.liux.android.abstracts.util;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

/**
 * {@link View#SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN} <br>
 * {@link WindowManager.LayoutParams#SOFT_INPUT_ADJUST_RESIZE} <br>
 * 全屏/沉浸式状态栏下，各种键盘挡住输入框解决办法 <br>
 * http://blog.csdn.net/qq_24531461/article/details/71412623
 */

public class FixFullScreenAndResize implements ViewTreeObserver.OnGlobalLayoutListener {
    private int mLastBottom;
    private View mContent;
    private Activity mActivity;
    private ViewGroup.LayoutParams mLayoutParams;

    public static void fix(AppCompatActivity activity) {
//            // 只有当沉浸式才会需要该类处理,注释的原因是为了支持动态设置 softInputMode 的情况
//            if ((activity.getWindow().getAttributes().softInputMode & WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//                    != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) return;
//            if ((activity.getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//                    != View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) return;
        new FixFullScreenAndResize(activity);
    }

    private FixFullScreenAndResize(Activity activity) {
        mActivity = activity;

        mContent = mActivity.findViewById(Window.ID_ANDROID_CONTENT);
        mLayoutParams = mContent.getLayoutParams();

        mContent.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if ((mActivity.getWindow().getAttributes().softInputMode & WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) return;

        // 获取Window区域尺寸
        Rect rect_window = new Rect();
        mContent.getWindowVisibleDisplayFrame(rect_window);
        int bottom_window = rect_window.bottom;

        // 和上次刷新数据比较,避免无用处理
        if (mLastBottom == 0) mLastBottom = bottom_window;
        if (mLastBottom == bottom_window) return;
        mLastBottom = bottom_window;

        // 获取内容区域尺寸
        Rect rect_drawing = new Rect();
        mContent.getDrawingRect(rect_drawing);
        int bottom_drawing = rect_drawing.bottom;

        // 如果有差异则设置新尺寸并更新布局
        int bottom_diff = bottom_window - bottom_drawing;
        if (bottom_diff != 0) {
            mLayoutParams.height = bottom_drawing + bottom_diff;
            mContent.requestLayout();
        }
    }
}