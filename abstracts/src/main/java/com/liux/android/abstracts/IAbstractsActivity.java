package com.liux.android.abstracts;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.liux.android.abstracts.titlebar.TitleBar;
import com.liux.android.abstracts.touch.TouchCallback;

import java.util.Map;

/**
 * 2018/2/12
 * By Liux
 * lx0758@qq.com
 */

interface IAbstractsActivity extends TouchCallback {

    AppCompatActivity getTarget();

    // ===============================================================

    /**
     * 复写此方法实现自定义ToolBar
     * @return
     */
    TitleBar onInitTitleBar();

    /**
     * 获取当前使用的TitleBar
     * @return
     */
    <T extends TitleBar>T getTitleBar();

    // ===============================================================

    /**
     * 使用 {@link AppCompatActivity#getLastCustomNonConfigurationInstance()}
     * @param data
     */
    void onRestoreData(Map<String, Object> data);

    /**
     * {@link AppCompatActivity#onRetainCustomNonConfigurationInstance()} 后调用
     * @param data
     */
    void onSaveData(Map<String, Object> data);

    // ===============================================================

    /**
     * 调用父类 dispatchTouchEvent
     * @param event
     * @return
     */
    boolean superDispatchTouchEvent(MotionEvent event);

    /**
     * 调用父类 onTouchEvent
     * @param event
     * @return
     */
    boolean superOnTouchEvent(MotionEvent event);
}
