package com.liux.android.example.downloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.liux.android.downloader.Downloader;
import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.core.Task;
import com.liux.android.example.R;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SingleTaskDownloaderActivity extends AppCompatActivity {

    private Task task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_downloader_single);

        task = Downloader.createTaskBuilder("http://html.6xyun.cn/media/mp3?t=123")
                .method("GET")
                .dir(null)
                .fileName("temp")
                .build();
        task.bindStatusListener(new OnStatusListener() {
            @Override
            public void onBind() {
                onUpdate();
            }

            @Override
            public void onUpdate() {
                switch (task.getStatus()) {
                    case NEW:
                        break;
                    case WAIT:
                        break;
                    case CONN:
                        break;
                    case START:
                        break;
                    case STOP:
                        break;
                    case ERROR:
                        break;
                    case COMPLETE:
                        break;
                    case DELETE:
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        task.delete();
    }
}
