package org.android.framework.ui.provider.impl;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.abstracts.titlebar.DefaultTitleBar;
import com.liux.android.abstracts.titlebar.TitleBar;
import org.android.framework.ui.provider.IAlertDialog;
import org.android.framework.ui.provider.ILoadingDialog;
import org.android.framework.ui.provider.IProgressDialog;
import org.android.framework.ui.provider.ISwitchingView;
import org.android.framework.ui.provider.IToast;
import org.android.framework.ui.provider.UIProvider;

/**
 * 2018/2/11
 * By Liux
 * lx0758@qq.com
 */

public class UIProviderImpl implements UIProvider {

    public UIProviderImpl(Context context) {
        ToastImpl.initialize(context);
    }

    @Override
    public IToast provideIToast() {
        return ToastImpl.getInstance();
    }

    @Override
    public IAlertDialog provideIAlertDialog(Context context) {
        return new AlertDialogImpl(context);
    }

    @Override
    public ILoadingDialog provideILoadingDialog(Context context) {
        return new LoadingDialogImpl(context);
    }

    @Override
    public IProgressDialog provideIProgressDialog(Context context) {
        return new ProgressDialogImpl(context);
    }

    @Override
    public ISwitchingView provideISwitchingView(Context context) {
        return new SwitchingViewImpl(context);
    }

    @Override
    public TitleBar provideTitleBar(AppCompatActivity appCompatActivity) {
        return new DefaultTitleBar(appCompatActivity);
    }
}
