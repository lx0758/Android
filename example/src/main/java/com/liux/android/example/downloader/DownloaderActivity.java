package com.liux.android.example.downloader;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.liux.android.downloader.Downloader;
import com.liux.android.downloader.DownloaderConfig;
import com.liux.android.downloader.network.OKHttpConnectFactory;

public class DownloaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Environment.isExternalStorageEmulated()) {

        }

        Downloader.init(DownloaderConfig.builder(getApplicationContext())
                .connectFactory(new OKHttpConnectFactory())
                .rootDirectory(getApplicationContext().getCacheDir())
                .threadCount(3)
                .build());
    }
}
