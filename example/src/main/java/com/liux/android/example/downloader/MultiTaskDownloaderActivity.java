package com.liux.android.example.downloader;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.liux.android.downloader.Downloader;
import com.liux.android.downloader.InitCallback;
import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.Status;
import com.liux.android.downloader.UIStatusListener;
import com.liux.android.downloader.core.Task;
import com.liux.android.example.R;
import com.liux.android.http.HttpUtil;
import com.liux.android.list.adapter.MultipleAdapter;
import com.liux.android.list.adapter.state.State;
import com.liux.android.list.adapter.state.SuperRule;
import com.liux.android.list.holder.SuperHolder;
import com.liux.android.tool.TT;
import com.liux.android.util.TextUtil;
import com.liux.android.util.UriUtil;

import java.io.File;
import java.util.List;

public class MultiTaskDownloaderActivity extends AppCompatActivity {

    private RecyclerView rvList;
    private MultipleAdapter<Task> taskMultipleAdapter;
    private MultiTaskDownloaderDialog multiTaskDownloaderDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_downloader_multi);
        rvList = findViewById(R.id.rv_list);

        taskMultipleAdapter = new MultipleAdapter<Task>()
                .addRule(new SuperRule<Task>(R.layout.item_downloader) {
                    @Override
                    public boolean doBindData(Task task) {
                        return true;
                    }

                    @Override
                    public void onDataBind(final SuperHolder holder, Task task, State state, int position) {
                        OnStatusListener onStatusListener = (OnStatusListener) holder.getItemView().getTag();
                        if (onStatusListener == null) {
                            onStatusListener = new UIStatusListener() {
                                Task task;
                                View.OnClickListener onClickListener = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (task == null) return;
                                        if (task.isCompleted()) {
                                            openFile(task.getFile());
                                        } else if (!task.isStarted()) {
                                            task.start();
                                        } else {
                                            task.stop();
                                        }
                                    }
                                };
                                View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        if (task == null) return false;
                                        final Task deleteTask = task;
                                        new AlertDialog.Builder(v.getContext())
                                                .setMessage("确认删除该任务吗?")
                                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        deleteTask.delete();
                                                        int pos = taskMultipleAdapter.getData().indexOf(deleteTask);
                                                        taskMultipleAdapter.getData().remove(deleteTask);
                                                        taskMultipleAdapter.notifyItemRemoved(pos);
                                                        dialog.dismiss();
                                                        TT.show(MultiTaskDownloaderActivity.this, "删除成功", TT.LENGTH_SHORT);
                                                    }
                                                })
                                                .show();
                                        return true;
                                    }
                                };

                                @Override
                                protected void onUIUpdate(Task task) {
                                    this.task = task;
                                    String status = "", operate = "";
                                    switch (task.getStatus()) {
                                        case NEW:
                                            status = "暂停中";
                                            operate = "开始";
                                            break;
                                        case WAIT:
                                            status = "等待中";
                                            operate = "暂停";
                                            break;
                                        case CONN:
                                            status = "连接中";
                                            operate = "暂停";
                                            break;
                                        case START:
                                            status = "下载中";
                                            operate = "暂停";
                                            break;
                                        case STOP:
                                            status = "暂停中";
                                            operate = "继续";
                                            break;
                                        case ERROR:
                                            status = "发生错误";
                                            operate = "重试";
                                            break;
                                        case COMPLETE:
                                            status = "已完成";
                                            operate = "打开";
                                            break;
                                        case DELETE:
                                            status = "已删除";
                                            operate = "";
                                            break;
                                    }
                                    ProgressBar progressBar = holder
                                            .setText(R.id.tv_name, task.getFile().getName())
                                            .setText(R.id.tv_progress, String.format(
                                                    "%s/%s",
                                                    TextUtil.getFormetSize(task.getCompleted()),
                                                    TextUtil.getFormetSize(task.getTotal())
                                            ))
                                            .setText(R.id.tv_speed, task.getStatus() == Status.START ? String.format("%s/s", TextUtil.getFormetSize(task.getSpeed())) : status)
                                            .setText(R.id.btn_operate, operate)
                                            .setOnClickListener(R.id.btn_operate, onClickListener)
                                            .setOnLongClickListener(onLongClickListener)
                                            .getView(R.id.pb_progress);
                                    int progress;
                                    if (task.getTotal() == 0) {
                                        progress = 0;
                                    } else {
                                        progress = (int) (task.getCompleted() / (task.getTotal() + 0.0) * 100);
                                    }
                                    progressBar.setProgress(progress);
                                }
                            };
                            holder.getItemView().setTag(onStatusListener);
                        }

                        task.bindStatusListener(onStatusListener);
                    }
                });

        rvList.setAdapter(taskMultipleAdapter);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        multiTaskDownloaderDialog = new MultiTaskDownloaderDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_downloader_multi_menu, menu);
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (Downloader.isInit()) {
            readDownloaderTasks();
        } else {
            Downloader.registerInitCallback(new InitCallback() {
                @Override
                public void onInitialized() {
                    readDownloaderTasks();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                multiTaskDownloaderDialog
                        .lintener(new MultiTaskDownloaderDialog.OnFinishListener() {
                            @Override
                            public void onFinish(String url, String method, String fileName) {
                                Task task = Downloader.createTaskBuilder(url)
                                        .method(method)
                                        .fileName(fileName)
                                        .build();
                                taskMultipleAdapter.getData().add(task);
                                taskMultipleAdapter.notifyItemInserted(
                                        taskMultipleAdapter.getData().size() - 1
                                );
                            }
                        })
                        .show();
                break;
            case R.id.action_new_single:
                multiTaskDownloaderDialog
                        .lintener(new MultiTaskDownloaderDialog.OnFinishListener() {
                            @Override
                            public void onFinish(String url, String method, String fileName) {
                                Task task = Downloader.createTaskBuilder(url)
                                        .fileName(fileName)
                                        .single(true)
                                        .build();
                                new SingleTaskDownloaderDialog(MultiTaskDownloaderActivity.this, task).show();
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void readDownloaderTasks() {
        List<Task> tasks = Downloader.getAllTasks();
        taskMultipleAdapter.getData().addAll(tasks);
        taskMultipleAdapter.notifyDataSetChanged();
    }

    private void openFile(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(UriUtil.getProviderUri(this, file), HttpUtil.getMimeType(file).toString());
        try {
            startActivity(intent);
        } catch (Exception e) {
            TT.show(this, "没有合适的程序来打开这个文件", TT.LENGTH_SHORT);
        }
    }
}
