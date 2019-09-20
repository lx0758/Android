package com.liux.android.abstracts;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatDialog;
import android.view.Window;

/**
 * 2018/2/12
 * By Liux
 * lx0758@qq.com
 */

public class AbstractsDialogFragmentProxy {

    private IAbstractsDialogFragment mIAbstractsDialogFragment;
    private boolean mWidthMatchParentLayout = false, mHeightMatchParentLayout = false;

    public AbstractsDialogFragmentProxy(IAbstractsDialogFragment IAbstractsDialogFragment) {
        mIAbstractsDialogFragment = IAbstractsDialogFragment;
    }

    public void onActivityCreatedBefore() {
        Dialog dialog = mIAbstractsDialogFragment.getTarget().getDialog();
        if (dialog instanceof AppCompatDialog) {
            ((AppCompatDialog) dialog).getDelegate().requestWindowFeature(Window.FEATURE_NO_TITLE);
        } else {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
    }

    public void onActivityCreatedAfter() {
        // 处理不能撑满布局的问题
        if (mWidthMatchParentLayout) {
            mIAbstractsDialogFragment.getTarget().getDialog().getWindow().getDecorView().setMinimumWidth(10000);
        }
        if (mHeightMatchParentLayout) {
            mIAbstractsDialogFragment.getTarget().getDialog().getWindow().getDecorView().setMinimumHeight(10000);
        }

        // 移除自带的边距
        mIAbstractsDialogFragment.getTarget().getDialog().getWindow().getDecorView().setPadding(0, 0, 0, 0);
        mIAbstractsDialogFragment.getTarget().getDialog().getWindow().getDecorView().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setMatchParentLayout(boolean width, boolean height) {
        mWidthMatchParentLayout = width;
        mHeightMatchParentLayout = height;
    }
}
