package com.liux.android.abstracts.titlebar;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.liux.android.abstracts.AbstractsActivity;

/**
 * 自定义TitleBar需要实现此接口 <br>
 * 调用时机: <br>
 * 1.{@link AbstractsActivity#onCreate(Bundle)} <br>
 * 2.{@link AbstractsActivity#onInitTitleBar} to {@link TitleBar} <br>
 * 5.{@link AbstractsActivity#onTitleChanged(CharSequence, int)}
 */
public abstract class TitleBar<T extends TitleBar> {

    private AppCompatActivity mActivity;

    public TitleBar(AppCompatActivity activity) {
        mActivity = activity;
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public abstract void setup(ActionBar actionBar);

    public abstract T setTitle(CharSequence title);

    public abstract T setTitleColor(int color);
}
