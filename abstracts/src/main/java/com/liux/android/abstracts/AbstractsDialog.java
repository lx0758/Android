package com.liux.android.abstracts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;

/**
 * 基础全屏沉浸式的 Dialog <br>
 * Created by Liux on 2017/8/23.
 */

public abstract class AbstractsDialog extends AppCompatDialog
        implements IAbstractsDialog {
    private String TAG = "AbstractsDialog";

    private AbstractsDialogProxy mProxy = new AbstractsDialogProxy(this);

    public AbstractsDialog(@NonNull Context context) {
        this(context, 0);
    }

    public AbstractsDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mProxy.openTranslucentMode();
    }

    public AbstractsDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        this(context);
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mProxy.onContentChanged();
    }

    @Override
    public AppCompatDialog getTarget() {
        return this;
    }

    @Override
    public boolean isFullScreen() {
        return mProxy.isFullScreen();
    }

    @Override
    public AbstractsDialog setFullScreen(boolean fullScreen) {
        mProxy.setFullScreen(fullScreen);
        return this;
    }

    @Override
    public int getBackgroundColor() {
        return mProxy.getBackgroundColor();
    }

    @Override
    public AbstractsDialog setBackgroundColor(int color) {
        mProxy.setBackgroundColor(color);
        return this;
    }
}
