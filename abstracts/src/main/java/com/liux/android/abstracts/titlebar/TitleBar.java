package com.liux.android.abstracts.titlebar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.liux.android.abstracts.AbstractsActivity;

/**
 * 自定义TitleBar需要实现此接口 <br>
 * 调用时机: <br>
 * 1.{@link AbstractsActivity#onCreate(Bundle)} <br>
 * 2.{@link AbstractsActivity#onInitTitleBar} to {@link TitleBar} <br>
 * 3.{@link AbstractsActivity#onCreate(Bundle)} <br>
 * 4.{@link #initView} <br>
 * 5.{@link AbstractsActivity#onTitleChanged(CharSequence, int)} <br>
 * 6.{@link #setTitle(CharSequence)}
 */
public abstract class TitleBar<T extends TitleBar> {

    private AppCompatActivity mActivity;

    public TitleBar(AppCompatActivity activity) {
        mActivity = activity;
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public abstract void initView();

    public abstract T setTitle(CharSequence title);

    public abstract T setTitleColor(int color);
}
