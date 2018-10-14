package com.liux.android.permission;

/**
 * 正式发起权限申请前的回调
 * Created by Liux on 2017/12/14.
 */

public interface Continue {

    void onContinue();

    void onCancel();
}
