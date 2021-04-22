package org.android.framework.ui.provider;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.abstracts.titlebar.TitleBar;

/**
 * 2018/2/11
 * By Liux
 * lx0758@qq.com
 */

public interface UIProvider {

    IToast provideIToast();

    IAlertDialog provideIAlertDialog(Context context);

    ILoadingDialog provideILoadingDialog(Context context);

    IProgressDialog provideIProgressDialog(Context context);

    ISwitchingView provideISwitchingView(Context context);

    TitleBar provideTitleBar(AppCompatActivity appCompatActivity);
}
