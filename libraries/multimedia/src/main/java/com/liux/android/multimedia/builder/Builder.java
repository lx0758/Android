package com.liux.android.multimedia.builder;

import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import com.liux.android.multimedia.MultimediaException;
import com.liux.android.multimedia.MultimediaUtil;
import com.liux.android.multimedia.action.Action;
import com.liux.android.multimedia.inject.CallTask;
import com.liux.android.multimedia.inject.IntentTask;
import com.liux.android.multimedia.inject.PermissionTask;
import com.liux.android.multimedia.listener.OnCancelListener;
import com.liux.android.multimedia.listener.OnFailureListener;

public abstract class Builder<T extends Builder, A extends Action> {

    public A action;
    public FragmentActivity target;
    public OnCancelListener onCancelListener;

    public Builder(A action, FragmentActivity target) {
        this.action = action;
        this.target = target;
    }

    protected T listener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return (T) this;
    }

    public final void start() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            onStart();
            return;
        }

        final String[] permissions = onDeclarePermissions();
        if (permissions == null || permissions.length == 0) {
            onStart();
            return;
        }

        boolean grant = true;
        for (String permission : permissions) {
            if (PermissionChecker.checkCallingOrSelfPermission(target, permission) != PermissionChecker.PERMISSION_GRANTED) {
                grant = false;
                break;
            }
        }
        if (grant) {
            onStart();
            return;
        }

        executeTask(new PermissionTask(permissions) {
            @Override
            protected void onGrant() {
                onStart();
            }

            @Override
            protected void onReject() {
                onFailure(new MultimediaException(MultimediaException.TYPE_PERMISSION));
            }
        });
    }

    protected void executeTask(PermissionTask executeTask) {
        MultimediaUtil.getInjectFragment(target).executeTask(executeTask);
    }

    protected void executeTask(IntentTask intentTask) {
        MultimediaUtil.getInjectFragment(target).executeTask(intentTask);
    }

    protected void executeTask(CallTask callTask) {
        MultimediaUtil.getInjectFragment(target).executeTask(callTask);
    }

    protected void onFailure(MultimediaException e) {
        if (e.getType() == MultimediaException.TYPE_CANCEL) {
            if (onCancelListener != null) onCancelListener.onCancel();
            return;
        }
        OnFailureListener onFailureListener = onDeclareFailureListener();
        if (onFailureListener != null) onFailureListener.onFailure(e);
        e.printStackTrace();
    }

    abstract String[] onDeclarePermissions();

    abstract OnFailureListener onDeclareFailureListener();

    abstract void onStart();

}
