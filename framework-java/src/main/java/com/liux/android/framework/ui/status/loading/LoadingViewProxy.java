package com.liux.android.framework.ui.status.loading;

import com.liux.android.framework.ui.provider.IDialog;
import com.liux.android.framework.ui.provider.ILoadingDialog;
import com.liux.android.framework.ui.status.OnCancelListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 2018/5/2
 * By Liux
 * lx0758@qq.com
 */
public class LoadingViewProxy implements LoadingView {

    private ILoadingDialog mILoadingDialog;
    private Map<Object, LoadingTask> mTaskMap = new HashMap<>();

    private IDialog.OnDismissListener mOnDismissListener = new IDialog.OnDismissListener() {
        @Override
        public void onDismiss(IDialog dialog) {
            for (LoadingTask loadingTask : mTaskMap.values()) {
                OnCancelListener onCancelListener = loadingTask.getListener();
                if (onCancelListener != null) onCancelListener.onCancel();
            }
            mTaskMap.clear();
        }
    };

    public LoadingViewProxy(ILoadingDialog iLoadingDialog) {
        mILoadingDialog = iLoadingDialog;
    }

    @Override
    public void show(Object tag, boolean canCancel, OnCancelListener listener) {
        show(tag, null, canCancel, listener);
    }

    @Override
    public void show(Object tag, String content, boolean canCancel, OnCancelListener listener) {
        LoadingTask loadingTask = LoadingTask.create(content, canCancel, listener);
        mTaskMap.put(tag, loadingTask);
        update();
    }

    @Override
    public void hide(Object tag) {
        mTaskMap.remove(tag);
        update();
    }

    private void update() {
        Iterator<LoadingTask> loadingTaskIterator = mTaskMap.values().iterator();
        if (!loadingTaskIterator.hasNext()) {
            mILoadingDialog.dismiss();
            return;
        }
        LoadingTask loadingTask = loadingTaskIterator.next();
        mILoadingDialog.setMessage(loadingTask.getContent());
        mILoadingDialog.setCancelable(loadingTask.isCanCancel());
        mILoadingDialog.setOnDismissListener(mOnDismissListener);
        mILoadingDialog.show();
    }
}
