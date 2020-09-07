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

//        defaultTitleBar.setTitleBarColor();
//        defaultTitleBar.setStatusBarColor();
//        defaultTitleBar.setOnTitleBarListener();
//        defaultTitleBar.setTitle();
//        defaultTitleBar.setTitleColor();
//        defaultTitleBar.hasBack();
//        defaultTitleBar.getBack();
//        defaultTitleBar.getBackIcon();
//        defaultTitleBar.getBackText();
//        defaultTitleBar.hasMore();
//        defaultTitleBar.getMore();
//        defaultTitleBar.getMoreIcon();
//        defaultTitleBar.getMoreText();

        defaultTitleBar.hasMore(true);
        defaultTitleBar.getMoreText().setText("测试");

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
