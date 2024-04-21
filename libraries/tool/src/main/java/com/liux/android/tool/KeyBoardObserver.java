package com.liux.android.tool;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

/**
 * Created by Liux on 2017/12/12.
 */

public class KeyBoardObserver implements ViewTreeObserver.OnGlobalLayoutListener {

    public static KeyBoardObserver install(Activity activity, Callback callback) {
        return new KeyBoardObserver(activity, callback);
    }

    private int mLastBottom;
    private View mContent;

    private Callback mCallback;

    public KeyBoardObserver(Activity activity) {
        this(activity, null);
    }

    public KeyBoardObserver(Activity activity, Callback callback) {
        if (activity == null) return;

        mContent = getContentView(activity);
        mContent.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mCallback = callback;
    }

    @Override
    public void onGlobalLayout() {
        if (mCallback == null) return;

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

        // 如果差异大于大尺寸1/5则认为是软键盘改变事件
        int bottom_diff = bottom_window - bottom_drawing;
        if (Math.abs(bottom_diff) > Math.max(bottom_window, bottom_drawing) / 5) {
            mCallback.onKeyboardChange(bottom_diff < 0, bottom_diff);
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private View getContentView(Activity activity) {
        return activity.findViewById(Window.ID_ANDROID_CONTENT);
    }

    public interface Callback {

        /**
         * 软键盘状态改变
         * @param isShow
         * @param height
         */
        void onKeyboardChange(boolean isShow, int height);
    }
}
