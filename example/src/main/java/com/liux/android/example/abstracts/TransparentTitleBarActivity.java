package com.liux.android.example.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.liux.android.abstracts.AbstractsActivity;
import com.liux.android.abstracts.titlebar.TitleBar;
import com.liux.android.abstracts.titlebar.TransparentTitleBar;
import com.liux.android.example.R;

/**
 * Created by Liux on 2017/12/3.
 */

public class TransparentTitleBarActivity extends AbstractsActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abstracts_demo);

        //WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        //WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        //WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        //WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public TitleBar onInitTitleBar() {
        return new TransparentTitleBar(this);
    }
}
