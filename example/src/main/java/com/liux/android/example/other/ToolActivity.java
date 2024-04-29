package com.liux.android.example.other;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.tool.Logger;

/**
 * Created by Liux on 2017/11/28.
 */

public class ToolActivity extends AppCompatActivity {

    private static final String TAG = "ToolActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, "onCreate");
    }
}
