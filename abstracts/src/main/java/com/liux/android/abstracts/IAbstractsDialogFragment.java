package com.liux.android.abstracts;

import android.support.v7.app.AppCompatDialogFragment;

interface IAbstractsDialogFragment {

    AppCompatDialogFragment getTarget();

    /**
     * 设置是否撑满布局
     * @param width
     * @param height
     */
    void setMatchParentLayout(boolean width, boolean height);
}
