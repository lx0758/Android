package com.liux.android.example.downloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.liux.android.downloader.Downloader;
import com.liux.android.downloader.InitCallback;
import com.liux.android.downloader.core.Task;
import com.liux.android.example.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleTaskDownloaderActivity extends AppCompatActivity {

    @BindView(R.id.et_url)
    EditText etUrl;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.btn_download)
    Button btnDownload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_downloader_single);
        ButterKnife.bind(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (Downloader.isInit()) {
            btnDownload.setEnabled(true);
        } else {
            Downloader.registerInitCallback(new InitCallback() {
                @Override
                public void onInitialized() {
                    btnDownload.setEnabled(true);
                }
            });
        }
    }

    @OnClick(R.id.btn_download)
    public void onViewClicked() {
        String url = etUrl.getText().toString();
        String name = etName.getText().toString();
        Task task = Downloader.createTaskBuilder(url)
                .fileName(name)
                .single(true)
                .build();
        new DownloaderDialog(this, task).show();
    }
}
