package com.liux.android.permission.runtime;

import java.util.List;

public interface OnRuntimePermissionListener {

    /**
     * 权限请求结果封装
     * @param allow 被获得的权限
     * @param reject 被拒绝的权限
     * @param prohibit 被禁止的权限
     */
    void onRuntimePermission(List<String> allow, List<String> reject, List<String> prohibit);
}
