package com.liux.android.permission.runtime;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;

import com.liux.android.permission.Continue;
import com.liux.android.permission.PermissionFragment;
import com.liux.android.permission.PermissionUtil;
import com.liux.android.permission.Request;
import com.liux.android.permission.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class RuntimeRequest extends Request<RuntimeRequest> {

    // 被允许的权限
    private List<String> allow;
    // 被拒绝的权限
    private List<String> reject;
    // 被禁止的权限
    private List<String> prohibit;
    // 被请求的权限
    private List<String> permissions;

    private OnRuntimePermissionListener onRuntimePermissionListener;

    public RuntimeRequest(Activity target, String[] permissions) {
        super(target);

        if (this.permissions == null) {
            this.permissions = new ArrayList<>();
        }
        this.permissions.clear();
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public RuntimeRequest listener(OnRuntimePermissionListener listener) {
        this.onRuntimePermissionListener = listener;
        return this;
    }

    /**
     * 权限申请调用
     * 小于 M 的不能直接通过, 因为 Android4.4 还有 AppOps 限制
     */
    @Override
    public void request() {
        if (onRuntimePermissionListener == null) {
            throw new NullPointerException("listener(OnRuntimePermissionListener) cannot is null");
        }

        allow = new ArrayList<>();
        reject = new ArrayList<>();
        prohibit = new ArrayList<>();

        // 检查已经具有的权限
        checkAllowPermissions();

        if (permissions.isEmpty()) {
            onRuntimePermissionListener.onRuntimePermission(allow, reject, prohibit);
            return;
        }

        // 还没找到请求 AppOpsManager 权限的方法
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            prohibit.addAll(permissions);
            onRuntimePermissionListener.onRuntimePermission(allow, reject, prohibit);
            return;
        }

        if (onContinueListener != null) {
            onContinueListener.onContinue(new Continue() {
                @Override
                public void onContinue() {
                    onRealRequestCall();
                }

                @Override
                public void onCancel() {
                    reject.addAll(permissions);
                    onRuntimePermissionListener.onRuntimePermission(allow, reject, prohibit);
                }
            });
        } else {
            onRealRequestCall();
        }
    }

    /**
     * 真正开始请求权限的地方
     */
    private void onRealRequestCall() {
        PermissionFragment permissionFragment = PermissionUtil.getPermissionFragment(target);
        permissionFragment.executeTask(new Task() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onMainThreadExecute(int requestCode, PermissionFragment permissionFragment) {
                permissionFragment.requestPermissions(permissions.toArray(new String[permissions.size()]), requestCode);
            }

            @Override
            public void onActivityResult(int resultCode, Intent data) {

            }

            @Override
            public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
                checkAcceptPermissions();
                onRuntimePermissionListener.onRuntimePermission(allow, reject, prohibit);
            }
        });
    }

    /**
     * 检查请求前已经拥有的权限
     */
    private void checkAllowPermissions() {
        Iterator<String> iterator = permissions.iterator();
        while (iterator.hasNext()) {
            String permission = iterator.next();
            if (RuntimePermissionUtil.hasPermissions(target, permission)) {
                allow.add(permission);
                iterator.remove();
            }
        }
    }

    /**
     * 检查请求后的权限状态
     * 经测试 shouldShowRequestPermissionRationale 返回值主要以下几种情况 ：
     * 第一次打开App时	                                            false
     * 上次弹出权限点击了禁止（但没有勾选“下次不在询问”）	        true
     * 上次选择禁止并勾选：下次不在询问	                            false
     * 所以放在请求返回之后判断
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAcceptPermissions() {
        for (String permission : permissions) {
            if (PermissionChecker.checkCallingOrSelfPermission(target, permission) == PermissionChecker.PERMISSION_GRANTED) {
                allow.add(permission);
            } else if (target.shouldShowRequestPermissionRationale(permission)) {
                reject.add(permission);
            } else {
                prohibit.add(permission);
            }
        }
    }
}