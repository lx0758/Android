package com.liux.android.abstracts.titlebar;

import android.os.Build;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 没有Toolbar的状态栏,黑色状态栏背景
 * Created by Liux on 2017/11/7.
 */

public class NoTitleBar extends TitleBar {

    public NoTitleBar(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void setup(ActionBar actionBar) {
        actionBar.hide();
    }

    @Override
    public void setTitle(CharSequence title) {

    }

    @Override
    public void setTitleColor(int color) {

    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(color);
        }
    }
}
