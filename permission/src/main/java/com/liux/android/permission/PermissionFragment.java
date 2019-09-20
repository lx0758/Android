package com.liux.android.permission;

import android.app.Fragment;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.SparseArray;

/**
 * 申请权限时注入的Fragment
 */
public class PermissionFragment extends Fragment {

    private SparseArray<Task> taskSparseArray = new SparseArray<>();

    public void executeTask(final Task task) {
        final int requestCode = getRequestCode();
        taskSparseArray.append(requestCode, task);

        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    task.onMainThreadExecute(requestCode, PermissionFragment.this);
                }
            });
            return;
        }
        task.onMainThreadExecute(requestCode, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Task task = taskSparseArray.get(requestCode);
        if (task != null) {
            task.onActivityResult(resultCode, data);
            taskSparseArray.remove(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Task task = taskSparseArray.get(requestCode);
        if (task != null) {
            task.onRequestPermissionsResult(requestCode, permissions, grantResults);
            taskSparseArray.remove(requestCode);
        }
    }

    /**
     * 获取一个随机请求码
     * @return
     */
    private int getRequestCode() {
        return (int) (System.currentTimeMillis() & 0xFFFF);
    }
}
