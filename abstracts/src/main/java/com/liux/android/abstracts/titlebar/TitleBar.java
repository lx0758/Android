package com.liux.android.abstracts.titlebar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 自定义TitleBar需要实现此接口 <br>
 */
public abstract class TitleBar {

    private AppCompatActivity mActivity;

    public TitleBar(AppCompatActivity activity) {
        mActivity = activity;
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public abstract void setup(ActionBar actionBar);

    public abstract void setTitle(CharSequence title);

    public abstract void setTitleColor(int color);
}
