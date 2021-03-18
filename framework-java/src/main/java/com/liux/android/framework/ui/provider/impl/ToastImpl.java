package com.liux.android.framework.ui.provider.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.liux.android.framework.ui.provider.IToast;

/**
 * 2018/3/8
 * By Liux
 * lx0758@qq.com
 */

public class ToastImpl implements IToast {

    public static IToast mInstance;
    public static IToast getInstance() {
        return mInstance;
    }
    public static void initialize(Context context) {
        mInstance = new ToastImpl(context);
    }

    private Toast mToast;

    @SuppressLint("ShowToast")
    private ToastImpl(Context context) {
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    @Override
    public void showInfo(CharSequence charSequence) {
        showToast(charSequence);
    }

    @Override
    public void showWarn(CharSequence charSequence) {
        showToast(charSequence);
    }

    @Override
    public void showError(CharSequence charSequence) {
        showToast(charSequence);
    }

    @Override
    public void showSuccess(CharSequence charSequence) {
        showToast(charSequence);
    }

    private void showToast(CharSequence charSequence) {
        mToast.setText(charSequence);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }
}
