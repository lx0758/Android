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

import java.util.LinkedList;
import java.util.List;

/**
 * 抽象Activity,提供以下能力 <br>
 * 1.自动隐藏输入法 {@link TouchHost} <br>
 * 2.实现各种特殊场景的 {@link TitleBar} 详见 {@link #onInitTitleBar()} <br>
 */

public abstract class AbstractsActivity extends AppCompatActivity {

    private String TAG = "AbstractsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleBar = onInitTitleBar();
        if (mTitleBar != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) mTitleBar.setup(actionBar);
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (mTitleBar != null) {
            mTitleBar.setTitle(title);
            if (color != 0) mTitleBar.setTitleColor(color);
        }
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

    private TitleBar mTitleBar;

    public <T extends TitleBar> T getTitleBar() {
        return (T) mTitleBar;
    }

    protected TitleBar onInitTitleBar() {
        return new DefaultTitleBar(this);
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

    protected boolean isHandlerTouch() {
        return mHandlerTouch;
    }

    protected void setHandlerTouch(boolean handlerTouch) {
        mHandlerTouch = handlerTouch;
    }

    protected boolean hasIgnoreView(View view) {
        List<View> views = getIgnoreViews();
        for (View v : views) {
            if (v == view) return true;
        }
        return false;
    }

    protected void addIgnoreView(View view) {
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

    protected void removeIgnoreView(View view) {
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

    protected void addTouchCallback(TouchCallback touchCallback) {
        mTouchCallbacks.add(touchCallback);
    }

    protected void removeTouchCallback(TouchCallback touchCallback) {
        mTouchCallbacks.remove(touchCallback);
    }
}
