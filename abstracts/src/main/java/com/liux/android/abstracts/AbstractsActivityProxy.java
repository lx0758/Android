package com.liux.android.abstracts;

import android.content.Context;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.liux.android.abstracts.titlebar.TitleBar;
import com.liux.android.abstracts.touch.TouchCallback;
import com.liux.android.abstracts.util.FixInputMethodManagerLeak;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 2018/2/1
 * By Liux
 * lx0758@qq.com
 */

public class AbstractsActivityProxy {

    private IAbstractsActivity mIAbstractsActivity;

    public AbstractsActivityProxy(IAbstractsActivity IAbstractsActivity) {
        mIAbstractsActivity = IAbstractsActivity;
    }

    public void onCreate() {
        initTitleBar();
        restoreData();
    }

    public Object onRetainCustomNonConfigurationInstance() {
        Map<String, Object> data = new HashMap<String, Object>();
        mIAbstractsActivity.onSaveData(data);
        if (data.isEmpty()) {
            return null;
        }
        return data;
    }

    public void onDestroy() {
        FixInputMethodManagerLeak.fix(mIAbstractsActivity.getTarget());
    }

    public void onTitleChanged(CharSequence title, int color) {
        changeTitleBar(title, color);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!mHandlerTouch) return mIAbstractsActivity.superDispatchTouchEvent(event);
        getGestureDetector().onTouchEvent(event);
        return mIAbstractsActivity.superDispatchTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!mHandlerTouch) return mIAbstractsActivity.superOnTouchEvent(event);
        getGestureDetector().onTouchEvent(event);
        return mIAbstractsActivity.superOnTouchEvent(event);
    }

    // ===============================================================

    private TitleBar mTitleBar;

    public <T extends TitleBar>T getTitleBar() {
        return (T) mTitleBar;
    }

    private void initTitleBar() {
        mTitleBar = mIAbstractsActivity.onInitTitleBar();
        if (mTitleBar != null) {
            ActionBar actionBar = mIAbstractsActivity.getTarget().getSupportActionBar();
            if (actionBar != null) mTitleBar.setup(actionBar);
        }
    }

    private void changeTitleBar(CharSequence title, int color) {
        if (mTitleBar != null) {
            mTitleBar.setTitle(title);
            if (color != 0) mTitleBar.setTitleColor(color);
        }
    }

    // ===============================================================

    private void restoreData() {
        try {
            Map<String, Object> data = (Map<String, Object>) mIAbstractsActivity.getTarget().getLastCustomNonConfigurationInstance();
            if (data != null && !data.isEmpty()) {
                mIAbstractsActivity.onRestoreData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===============================================================

    private boolean mHandlerTouch = true;
    private List<View> mIgnoreViews;
    private List<TouchCallback> mTouchCallbacks = new LinkedList<>();
    private GestureDetector mGestureDetector;
    private InputMethodManager mInputMethodManager;

    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            for (TouchCallback touchCallback : mTouchCallbacks) {
                touchCallback.hideKeyboard();
            }
            hideKeyboard(event);
            return true;
        }
    };

    public boolean isHandlerTouch() {
        return mHandlerTouch;
    }

    public void setHandlerTouch(boolean handlerTouch) {
        mHandlerTouch = handlerTouch;
    }

    public boolean hasIgnoreView(View view) {
        List<View> views = getIgnoreViews();
        for (View v : views) {
            if (v == view) return true;
        }
        return false;
    }

    public void addIgnoreView(View view) {
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                v.removeOnAttachStateChangeListener(this);
                removeIgnoreView(v);
            }
        });
        getIgnoreViews().add(view);
    }

    public void removeIgnoreView(View view) {
        getIgnoreViews().remove(view);
    }

    private List<View> getIgnoreViews() {
        if (mIgnoreViews == null) {
            mIgnoreViews = new LinkedList<>();
        }
        return mIgnoreViews;
    }

    private GestureDetector getGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(mIAbstractsActivity.getTarget(), mSimpleOnGestureListener);
        }
        return mGestureDetector;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param event
     */
    private void hideKeyboard(MotionEvent event) {
        if (event == null) return;

        if (hasEditTextEvent(event) || hasIgnoreViewEvent(event)) return;

        View view = mIAbstractsActivity.getTarget().getCurrentFocus();
        if (view == null) return;
        IBinder token = view.getWindowToken();
        if (token == null) return;

        if (mInputMethodManager == null) {
            mInputMethodManager = (InputMethodManager) mIAbstractsActivity.getTarget().getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (mInputMethodManager != null) {
            mInputMethodManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 判断事件是否在编辑框范围内
     * @param event
     * @return
     */
    private boolean hasEditTextEvent(MotionEvent event) {
        View view = mIAbstractsActivity.getTarget().getCurrentFocus();
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完
        // 第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        if (view == null || !(view instanceof EditText)) return false;
        if (inTheViewRange(view, event)) return true;
        return false;
    }

    /**
     * 判断事件是否在过滤目标范围内
     * @param event
     * @return
     */
    private boolean hasIgnoreViewEvent(MotionEvent event) {
        List<View> views = getIgnoreViews();
        for (View v : views) {
            if (inTheViewRange(v, event)) return true;
        }
        return false;
    }

    /**
     * 判断事件是否在某个View范围内
     * @param view
     * @param event
     * @return
     */
    private boolean inTheViewRange(View view, MotionEvent event) {
        if (view == null) return false;
        int[] location = {0, 0};
        view.getLocationInWindow(location);
        int left = location[0], top = location[1], right = left + view.getWidth(), bottom = top + view.getHeight();
        if (event.getRawX() > left && event.getRawX() < right && event.getRawY() > top && event.getRawY() < bottom) {
            return true;
        } else {
            return false;
        }
    }

    public void addTouchCallback(TouchCallback touchCallback) {
        mTouchCallbacks.add(touchCallback);
    }

    public void removeTouchCallback(TouchCallback touchCallback) {
        mTouchCallbacks.remove(touchCallback);
    }
}
