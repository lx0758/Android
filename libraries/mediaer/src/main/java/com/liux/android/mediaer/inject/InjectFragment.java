package com.liux.android.mediaer.inject;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * 操作时注入的Fragment
 */
public class InjectFragment extends Fragment {

    private SparseArray<Task> taskSparseArray = new SparseArray<>();

    public void executeTask(final PermissionTask permissionTask) {
        int requestCode;
        do {
            requestCode = getRequestCode();
        } while (taskSparseArray.indexOfKey(requestCode) >= 0);
        taskSparseArray.append(requestCode, permissionTask);

        final int finalRequestCode = requestCode;
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    permissionTask.onExecute(InjectFragment.this, finalRequestCode);
                }
            });
        } else permissionTask.onExecute(this, finalRequestCode);
    }

    public void executeTask(final IntentTask intentTask) {
        int requestCode;
        do {
            requestCode = getRequestCode();
        } while (taskSparseArray.indexOfKey(requestCode) >= 0);
        taskSparseArray.append(requestCode, intentTask);

        final int finalRequestCode = requestCode;
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    intentTask.onExecute(InjectFragment.this, finalRequestCode);
                }
            });
        } else intentTask.onExecute(this, finalRequestCode);
    }

    public void executeTask(final CallTask callTask) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    callTask.onExecute(InjectFragment.this);
                }
            });
        } else callTask.onExecute(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Task task = taskSparseArray.get(requestCode);
        taskSparseArray.remove(requestCode);
        if (task instanceof IntentTask) {
            IntentTask intentTask = (IntentTask) task;
            intentTask.onActivityResult(resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Task task = taskSparseArray.get(requestCode);
        taskSparseArray.remove(requestCode);
        if (task instanceof PermissionTask) {
            PermissionTask permissionTask = (PermissionTask) task;
            permissionTask.onRequestPermissionsResult(getActivity(), permissions, grantResults);
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
