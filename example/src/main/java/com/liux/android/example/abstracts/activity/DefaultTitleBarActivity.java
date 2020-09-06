package com.liux.android.example.abstracts.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.liux.android.abstracts.AbstractsActivity;
import com.liux.android.abstracts.titlebar.DefaultTitleBar;
import com.liux.android.abstracts.titlebar.TitleBar;
import com.liux.android.example.databinding.ActivityAbstractsDemoBinding;
import com.liux.android.tool.TT;

/**
 * Created by Liux on 2017/12/3.
 */

public class DefaultTitleBarActivity extends AbstractsActivity {

    private ActivityAbstractsDemoBinding mViewBinding;

    @Override
    public TitleBar onInitTitleBar() {
        DefaultTitleBar defaultTitleBar = (DefaultTitleBar) super.onInitTitleBar();

//        defaultTitleBar
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

        defaultTitleBar.hasMore(true).getMoreText().setText("测试");

        return defaultTitleBar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivityAbstractsDemoBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.btnButton1.setOnClickListener(view -> {
            TT.show("点击了按钮1");
        });
        mViewBinding.btnButton2.setOnClickListener(view -> {
            TT.show("点击了按钮2");
        });

        // 忽略某控件
        addIgnoreView(mViewBinding.btnButton1);
    }
}
