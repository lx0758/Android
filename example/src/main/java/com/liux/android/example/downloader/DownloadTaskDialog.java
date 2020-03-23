package com.liux.android.example.downloader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.Status;
import com.liux.android.downloader.UIStatusListener;
import com.liux.android.downloader.core.Task;
import com.liux.android.example.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadTaskDialog extends Dialog {

    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_url)
    TextView tvUrl;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.pb_progress)
    ProgressBar pbProgress;
    @BindView(R.id.tv_percentage)
    TextView tvPercentage;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.btn_operate)
    Button btnOperate;
    @BindView(R.id.btn_close)
    Button btnClose;

    private Task task;
    private OnStatusListener onStatusListener = new UIStatusListener() {
        @Override
        protected void onUIUpdate(Task task) {
            tvId.setText(String.valueOf(task.getId()));
            tvUrl.setText(task.getUrl());
            tvName.setText(task.getFile().getName());
            String state = task.getStatus().name();
            if (task.getStatus() == Status.ERROR) {
                state += "-" + task.getErrorInfo().getMessage();
            }
            tvState.setText(state);
            int progress;
            if (task.getTotal() == 0) {
                progress = 0;
            } else {
                progress = (int) (task.getCompleted() / (task.getTotal() + 0.0) * 100);
            }
            tvPercentage.setText(progress + "%");
            tvSpeed.setText(
                    (task.getSpeed() / 1024) + "kb/s"
            );
            tvProgress.setText(
                    (task.getCompleted() / 1024) + "kb/" + (task.getTotal() / 1024) + "kb"
            );
            pbProgress.setProgress(progress);
        }
    };

    public DownloadTaskDialog(Context context, Task task) {
        super(context);
        this.task = task;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_task_download);
        ButterKnife.bind(this);

        setCancelable(false);

        task.bindStatusListener(onStatusListener);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                task.unbindStatusListener(onStatusListener);
            }
        });
    }

    @OnClick({R.id.btn_operate, R.id.btn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_operate:
                if (task.isCompleted()) {
                    DownloaderActivity.openFile(getOwnerActivity(), task.getFile());
                } else if (!task.isStarted()) {
                    task.start();
                } else {
                    task.stop();
                }
                break;
            case R.id.btn_close:
                if (task.isDeleted()) return;
                task.delete();
                dismiss();
                break;
        }
    }
}
