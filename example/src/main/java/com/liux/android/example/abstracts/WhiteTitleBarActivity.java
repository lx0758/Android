package com.liux.android.example.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.liux.android.abstracts.titlebar.TitleBar;
import com.liux.android.abstracts.titlebar.WhiteTitleBar;
import com.liux.android.example.R;

/**
 * Created by Liux on 2017/12/3.
 */

public class WhiteTitleBarActivity extends com.liux.android.abstracts.AbstractsActivity {

    @Override
    public TitleBar onInitTitleBar() {
        return new WhiteTitleBar(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_demo);
//        DefaultTitleBar titleBar = getTitleBar();
//        titleBar
//                .setTitleBarColor()
//                .setStatusBarColor()
//                .setOnTitleBarListener()
//                .setStatusBarMode(false)
//                .setTitle()
//                .setTitleColor()
//                .hasBack()
//                .getBack()
//                .getBackIcon()
//                .getBackText()
//                .hasMore()
//                .getMore()
//                .getMoreIcon()
//                .getMoreText();
    }
}
