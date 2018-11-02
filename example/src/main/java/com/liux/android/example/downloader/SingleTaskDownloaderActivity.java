package com.liux.android.example.downloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.liux.android.downloader.Downloader;
import com.liux.android.downloader.InitCallback;
import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.UIStatusListener;
import com.liux.android.downloader.core.Task;
import com.liux.android.example.R;

public class SingleTaskDownloaderActivity extends AppCompatActivity {
    private static final String TAG = "SingleTaskDownloader";

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
                        Log.d(TAG, "onUpdate: " + task.getId() + " getStatus:" + task.getStatus());
                        break;
                    case WAIT:
                        Log.d(TAG, "onUpdate: " + task.getId() + " getStatus:" + task.getStatus());
                        break;
                    case CONN:
                        Log.d(TAG, "onUpdate: " + task.getId() + " getStatus:" + task.getStatus());
                        break;
                    case START:
                        Log.d(TAG, "onUpdate: " + task.getId() + " getStatus:" + task.getStatus() + " getSpeed:" + (task.getSpeed() / 1024) + "kb/s getProgress:" + (int)(task.getCompleted() / (task.getTotal() + 0.01) * 100) + "%");
                        break;
                    case STOP:
                        Log.d(TAG, "onUpdate: " + task.getId() + " getStatus:" + task.getStatus());
                        break;
                    case ERROR:
                        Log.d(TAG, "onUpdate: " + task.getId() + " getStatus:" + task.getStatus() + " getErrorInfo:" + task.getErrorInfo().getMessage());
                        break;
                    case COMPLETE:
                        Log.d(TAG, "onUpdate: " + task.getId() + " getStatus:" + task.getStatus() + " getFile:" + task.getFile());
                        break;
                    case DELETE:
                        Log.d(TAG, "onUpdate: " + task.getId() + " getStatus:" + task.getStatus());
                        break;
                }
            }
        });
        task.start();
    }
}
