package com.liux.android.example.downloader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liux.android.downloader.Downloader;
import com.liux.android.downloader.Config;
import com.liux.android.downloader.network.OKHttpConnectFactory;
import com.liux.android.example.R;

public class DownloaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Downloader.init(Config.builder(getApplicationContext())
                .maxTaskCount(3)
                .runUndoneForStart(false)
                .connectFactory(new OKHttpConnectFactory())
                .defaultDirectory(getExternalFilesDir("download"))
                .build());

        setContentView(R.layout.activity_downloader);
    }

    public void onSingleTask(View view) {
        startActivity(new Intent(this, SingleTaskDownloaderActivity.class));
    }

    public void onMultiTask(View view) {
        startActivity(new Intent(this, MultiTaskDownloaderActivity.class));
    }
}
