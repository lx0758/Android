package com.liux.android.example.downloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.liux.android.downloader.Downloader;
import com.liux.android.downloader.OnStatusListener;
import com.liux.android.example.R;

public class SingleTaskDownloaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_downloader_single);

        Downloader.getTask(0).bindStatusListener(new OnStatusListener() {
        });
    }
}
