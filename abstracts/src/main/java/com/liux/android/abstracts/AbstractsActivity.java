package com.liux.android.abstracts;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.abstracts.titlebar.DefaultTitleBar;
import com.liux.android.abstracts.titlebar.TitleBar;
import com.liux.android.abstracts.touch.TouchCallback;
import com.liux.android.abstracts.touch.TouchHost;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 抽象Activity,提供以下能力 <br>
 * 1.自动隐藏输入法 {@link TouchHost} <br>
 * 2.实现任意数据的"意外"恢复和存储 {@link #onRestoreData(Map)} {@link #onSaveData(Map)} <br>
 * 3.实现各种特殊场景的 {@link TitleBar} 详见 {@link #onInitTitleBar()} <br>
 * 2017-8-21 <br>
 * 调整恢复数据的调用时机<br>
 * 2017-12-4 <br>
 * 实现 {@link TouchHost} 事件的过滤
 * 2018-2-12 <br>
 * 1.改代理模式实现
 * 2.移除自定义生命周期
 * 2020-3-17 <br>
 * 1.移除代理模式
 * Created by Liux on 2017/8/7
 */

public abstract class AbstractsActivity extends AppCompatActivity {

    private String TAG = "AbstractsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitleBar();
        restoreData();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Map<Object, Object> data = new HashMap<>();
        onSaveData(data);
        if (data.isEmpty()) {
            return null;
        }
        return data;
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        changeTitleBar(title, color);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!mHandlerTouch) return super.dispatchTouchEvent(event);
        getGestureDetector().onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mHandlerTouch) return super.onTouchEvent(event);
        getGestureDetector().onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    // ===============================================================

    public TitleBar onInitTitleBar() {
        return new DefaultTitleBar(this);
    }

    private TitleBar mTitleBar;

    public <T extends TitleBar> T getTitleBar() {
        return (T) mTitleBar;
    }

    private void initTitleBar() {
        mTitleBar = onInitTitleBar();
        if (mTitleBar != null) {
            ActionBar actionBar = getSupportActionBar();
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

    protected void onRestoreData(Map<Object, Object> data) {

    }

    public void onSaveData(Map<Object, Object> data) {

    }

    private void restoreData() {
        try {
            Map<Object, Object> data = (Map<Object, Object>) getLastCustomNonConfigurationInstance();
            if (data != null && !data.isEmpty()) {
                onRestoreData(data);
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
            mGestureDetector = new GestureDetector(this, mSimpleOnGestureListener);
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

        View view = getCurrentFocus();
        if (view == null) return;
        IBinder token = view.getWindowToken();
        if (token == null) return;

        if (mInputMethodManager == null) {
            mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
        View view = getCurrentFocus();
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完
        // 第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        if (!(view instanceof EditText)) return false;
        return inTheViewRange(view, event);
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
        return event.getRawX() > left && event.getRawX() < right && event.getRawY() > top && event.getRawY() < bottom;
    }

    public void addTouchCallback(TouchCallback touchCallback) {
        mTouchCallbacks.add(touchCallback);
    }

    public void removeTouchCallback(TouchCallback touchCallback) {
        mTouchCallbacks.remove(touchCallback);
    }
}
