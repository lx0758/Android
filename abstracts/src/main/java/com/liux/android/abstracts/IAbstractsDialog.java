package com.liux.android.abstracts;

import android.support.v7.app.AppCompatDialog;

/**
 * 2018/2/12
 * By Liux
 * lx0758@qq.com
 */

interface IAbstractsDialog {

    AppCompatDialog getTarget();

    /**
     * 设置是否撑满布局
     * @param width
     * @param height
     */
    void setMatchParentLayout(boolean width, boolean height);
}
