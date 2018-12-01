package com.liux.android.abstracts;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

/**
 * 2018/2/12
 * By Liux
 * lx0758@qq.com
 */

public class AbstractsDialogProxy {

    private IAbstractsDialog mIAbstractsDialog;
    private boolean mWidthMatchParentLayout = false, mHeightMatchParentLayout = false;

    public AbstractsDialogProxy(IAbstractsDialog IAbstractsDialog) {
        mIAbstractsDialog = IAbstractsDialog;
    }

    public void initDialog() {
        mIAbstractsDialog.getTarget().getDelegate().requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void onContentChanged() {
        // 处理不能撑满布局的问题
        if (mWidthMatchParentLayout) {
            mIAbstractsDialog.getTarget().getWindow().getDecorView().setMinimumWidth(10000);
        }
        if (mHeightMatchParentLayout) {
            mIAbstractsDialog.getTarget().getWindow().getDecorView().setMinimumHeight(10000);
        }

        // 移除自带的边距
        mIAbstractsDialog.getTarget().getWindow().getDecorView().setPadding(0, 0, 0, 0);
        mIAbstractsDialog.getTarget().getWindow().getDecorView().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setMatchParentLayout(boolean width, boolean height) {
        mWidthMatchParentLayout = width;
        mHeightMatchParentLayout = height;
    }
}
