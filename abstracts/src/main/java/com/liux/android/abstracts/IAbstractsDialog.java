package com.liux.android.abstracts;

import android.support.v7.app.AppCompatDialog;

/**
 * 2018/2/12
 * By Liux
 * lx0758@qq.com
 */

public interface IAbstractsDialog {

    AppCompatDialog getTarget();

    // ===============================================================

    /**
     * 是否全屏模式
     * @return
     */
    boolean isFullScreen();

    /**
     * 设置是否全屏模式
     * @param fullScreen
     * @return
     */
    IAbstractsDialog setFullScreen(boolean fullScreen);

    /**
     * 获取全屏背景色
     * @return
     */
    int getBackgroundColor();

    /**
     * 设置全屏背景色
     * @param color
     * @return
     */
    IAbstractsDialog setBackgroundColor(int color);
}
