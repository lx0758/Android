package com.liux.android.example.downloader;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liux.android.downloader.Downloader;
import com.liux.android.downloader.Config;
import com.liux.android.downloader.InitCallback;
import com.liux.android.downloader.OnStatusListener;
import com.liux.android.downloader.Status;
import com.liux.android.downloader.UIStatusListener;
import com.liux.android.downloader.core.Task;
import com.liux.android.downloader.network.OKHttpConnectFactory;
import com.liux.android.example.R;
import com.liux.android.http.HttpUtil;
import com.liux.android.list.adapter.MultipleAdapter;
import com.liux.android.list.adapter.rule.SingleRule;
import com.liux.android.list.adapter.state.State;
import com.liux.android.list.adapter.rule.SuperRule;
import com.liux.android.list.holder.SuperHolder;
import com.liux.android.permission.Authorizer;
import com.liux.android.permission.install.OnInstallPermissionListener;
import com.liux.android.tool.TT;
import com.liux.android.util.TextUtil;
import com.liux.android.util.UriUtil;

import java.io.File;
import java.util.List;

public class DownloaderActivity extends AppCompatActivity {

    private RecyclerView rvList;
    private MultipleAdapter<Task> taskMultipleAdapter;
    private CreateTaskDialog createTaskDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_downloader);
        rvList = findViewById(R.id.rv_list);

        taskMultipleAdapter = new MultipleAdapter<Task>()
                .addRule(new SingleRule<Task>(R.layout.item_downloader) {
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
                                            openFile(DownloaderActivity.this, task.getFile());
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
                                                        TT.show("删除成功");
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

        createTaskDialog = new CreateTaskDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_downloader_menu, menu);
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
            Downloader.init(Config.builder(getApplicationContext())
                    .maxTaskCount(3)
                    .runUndoneForStart(false)
                    .connectFactory(new OKHttpConnectFactory())
                    .defaultDirectory(getExternalFilesDir("download"))
                    .build());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!Downloader.isInit()) {
            TT.show("下载器还没初始化好呢!怎么回事啊,小老弟~");
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()) {
            case R.id.action_general:
                createTaskDialog
                        .lintener(new CreateTaskDialog.OnFinishListener() {
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
            case R.id.action_temporary:
                createTaskDialog
                        .lintener(new CreateTaskDialog.OnFinishListener() {
                            @Override
                            public void onFinish(String url, String method, String fileName) {
                                Task task = Downloader.createTaskBuilder(url)
                                        .method(method)
                                        .fileName(fileName)
                                        .temporary(true)
                                        .build();
                                DownloadTaskDialog downloadTaskDialog = new DownloadTaskDialog(DownloaderActivity.this, task);
                                downloadTaskDialog.setOwnerActivity(DownloaderActivity.this);
                                downloadTaskDialog.show();
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

    protected static void openFile(final Activity activity, final File file) {
        if (file.getName().endsWith(".apk")) {
            Authorizer.with(activity)
                    .requestInstall()
                    .listener(new OnInstallPermissionListener() {
                        @Override
                        public void onSucceed() {
                            openFileReal(activity, file);
                        }

                        @Override
                        public void onFailure() {
                            TT.show("没有软件安装权限");
                        }
                    })
                    .request();
        } else {
            openFileReal(activity, file);
        }
    }

    private static void openFileReal(Activity activity, File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(UriUtil.getProviderUri(activity, file), HttpUtil.getMimeType(file).toString());
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            TT.show("没有合适的程序来打开这个文件");
        }
    }
}
