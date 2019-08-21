package com.liux.android.view;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * date：2017/12/11
 * author：Liux
 * email：lx0758@qq.com
 * description：可以控制是否允许左右滑动的 ViewPager
 */

public class ScrollViewPager extends ViewPager {

    private boolean mCanScroll = true;
    private CanScrollCallback mCanScrollCallback;

    public ScrollViewPager(Context context) {
        super(context);
    }

    public ScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isCanScroll()) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isCanScroll()) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    public boolean isCanScroll() {
        if (mCanScrollCallback != null) {
            return mCanScrollCallback.canScroll();
        }
        return mCanScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.mCanScroll = canScroll;
    }

    public void setCanScrollCallback(CanScrollCallback callback) {
        this.mCanScrollCallback = callback;
    }

    /**
     * 是否能滑动翻页的回调接口
     */
    public interface CanScrollCallback {

        boolean canScroll();
    }
}
