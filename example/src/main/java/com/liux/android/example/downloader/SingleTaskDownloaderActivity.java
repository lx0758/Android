package com.liux.android.example.downloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.liux.android.downloader.Downloader;
import com.liux.android.downloader.InitCallback;
import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.UIStatusListener;
import com.liux.android.downloader.core.Task;
import com.liux.android.example.R;

public class SingleTaskDownloaderActivity extends AppCompatActivity {

    private Task task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_downloader_single);

        if (Downloader.isInit()) {
            onCreateTask();
        } else {
            Downloader.registerInitCallback(new InitCallback() {
                @Override
                public void onInitialized() {
                    onCreateTask();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (task != null) task.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (task != null) task.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) task.delete();
    }

    private void onCreateTask() {
        task = Downloader.createTaskBuilder("http://html.6xyun.cn/media/mp3?t=123")
                .method("GET")
                .header(null, null)
                .dir(null)
                .fileName("temp")
                .build();
        task.bindStatusListener(new OnStatusListener() {
            @Override
            public void onBind(Task task) {
                onUpdate(task);
            }

            @Override
            public void onUpdate(Task task) {
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
        task.bindStatusListener(new UIStatusListener() {
            @Override
            protected void onUIBind(Task task) {
                onUIUpdate(task);
            }

            @Override
            protected void onUIUpdate(Task task) {
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
        task.start();
    }
}
