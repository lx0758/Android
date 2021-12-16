package com.liux.android.example.service;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.databinding.ActivityServiceBinding;
import com.liux.android.example.service.api.BusinessManager;

public class ServiceActivity extends AppCompatActivity {
    private static final String TAG = "ServiceActivity";

    private ActivityServiceBinding mViewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivityServiceBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.btnCheck.setOnClickListener(v -> {
            boolean isAvailable = BusinessManager.getInstance(v.getContext()).isAvailable();
            printLog("check:" + isAvailable);
        });
        mViewBinding.btnCall.setOnClickListener(v -> {
            String param = "Hello";
            String result = BusinessManager.getInstance(v.getContext()).call(param);
            printLog("call:" + result);
        });

        mViewBinding.tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());

        printLog("onCreate");
    }

    private void printLog(String msg) {
        Log.i(TAG, "printLog, msg:" + msg);
        TextView log = mViewBinding.tvLog;
        log.append(msg + "\n");

        int offset = log.getLineCount() * log.getLineHeight();
        if (offset > log.getHeight()) {
            log.scrollTo( 0, offset - log.getHeight() );
        }
    }
}
