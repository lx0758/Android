package com.liux.android.example.downloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.liux.android.downloader.Downloader;
import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.core.Task;
import com.liux.android.example.R;

public class SingleTaskDownloaderActivity extends AppCompatActivity {

    private Task task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_downloader_single);

        task = Downloader.createTask("");
        task.bindStatusListener(new OnStatusListener() {
            @Override
            public void onBind() {
                onUpdate();
            }

            @Override
            public void onUpdate() {
                switch (task.getStatus()) {
                    case NONE:
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
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        task.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        task.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        task.delete();
    }
}
