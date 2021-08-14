package com.liux.android.example.abstracts.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.liux.android.abstracts.titlebar.TitleBar;
import com.liux.android.abstracts.titlebar.WhiteTitleBar;
import com.liux.android.example.R;

/**
 * Created by Liux on 2017/12/3.
 */

public class WhiteTitleBarActivity extends com.liux.android.abstracts.AbstractsActivity {

    @Override
    public TitleBar onInitTitleBar() {
        WhiteTitleBar whiteTitleBar =  new WhiteTitleBar(this);

//        whiteTitleBar.setTitleBarColor();
//        whiteTitleBar.setStatusBarColor();
//        whiteTitleBar.setOnTitleBarListener();
//        whiteTitleBar.setTitle();
//        whiteTitleBar.setTitleColor();
//        whiteTitleBar.hasBack();
//        whiteTitleBar.getBack();
//        whiteTitleBar.getBackIcon();
//        whiteTitleBar.getBackText();
//        whiteTitleBar.hasMore();
//        whiteTitleBar.getMore();
//        whiteTitleBar.getMoreIcon();
//        whiteTitleBar.getMoreText();

        whiteTitleBar.hasMore(true);
        whiteTitleBar.getMoreText().setText("测试");

        return whiteTitleBar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abstracts_demo);
    }
}
