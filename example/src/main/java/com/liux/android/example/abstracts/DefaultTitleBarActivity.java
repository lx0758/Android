package com.liux.android.example.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.liux.android.abstracts.AbstractsActivity;
import com.liux.android.abstracts.titlebar.DefaultTitleBar;
import com.liux.android.abstracts.titlebar.TitleBar;
import com.liux.android.example.R;
import com.liux.android.tool.TT;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Liux on 2017/12/3.
 */

public class DefaultTitleBarActivity extends AbstractsActivity {

    @BindView(R.id.et_text1)
    EditText etText1;

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
        setContentView(R.layout.activity_abstracts_demo);
        ButterKnife.bind(this);

        // 忽略某控件
        addIgnoreView(findViewById(R.id.btn_button_1));
    }

    @OnClick({R.id.btn_button_1, R.id.btn_button_2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_button_1:
                TT.show("点击了按钮");
                break;
            case R.id.btn_button_2:
                break;
        }
    }
}
