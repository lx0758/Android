package com.liux.android.example.downloader;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.Status;
import com.liux.android.downloader.UIStatusListener;
import com.liux.android.downloader.core.Task;
import com.liux.android.example.databinding.DialogTaskDownloadBinding;

public class DownloadTaskDialog extends Dialog {

    private DialogTaskDownloadBinding mViewBinding;

    private Task task;
    private OnStatusListener onStatusListener = new UIStatusListener() {
        @Override
        protected void onUIUpdate(Task task) {
            mViewBinding.tvId.setText(String.valueOf(task.getId()));
            mViewBinding.tvUrl.setText(task.getUrl());
            mViewBinding.tvName.setText(task.getFile().getName());
            String state = task.getStatus().name();
            if (task.getStatus() == Status.ERROR) {
                state += "-" + task.getErrorInfo().getMessage();
            }
            mViewBinding.tvState.setText(state);
            int progress;
            if (task.getTotal() == 0) {
                progress = 0;
            } else {
                progress = (int) (task.getCompleted() / (task.getTotal() + 0.0) * 100);
            }
            mViewBinding.tvPercentage.setText(progress + "%");
            mViewBinding.tvSpeed.setText(
                    (task.getSpeed() / 1024) + "kb/s"
            );
            mViewBinding.tvProgress.setText(
                    (task.getCompleted() / 1024) + "kb/" + (task.getTotal() / 1024) + "kb"
            );
            mViewBinding.pbProgress.setProgress(progress);
        }
    };

    public DownloadTaskDialog(Context context, Task task) {
        super(context);
        this.task = task;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = DialogTaskDownloadBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(mViewBinding.getRoot());

        setCancelable(false);

        task.bindStatusListener(onStatusListener);

        setOnDismissListener(dialog -> {
            task.unbindStatusListener(onStatusListener);
        });

        mViewBinding.btnOperate.setOnClickListener(view -> {
            if (task.isCompleted()) {
                DownloaderActivity.openFile(getOwnerActivity(), task.getFile());
            } else if (!task.isStarted()) {
                task.start();
            } else {
                task.stop();
            }
        });
        mViewBinding.btnClose.setOnClickListener(view -> {
            if (task.isDeleted()) return;
            task.delete();
            dismiss();
        });
    }
}
