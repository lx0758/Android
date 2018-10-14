package com.liux.android.permission;

public interface OnContinueListener {

    /**
     * 正式发起申请前的回调
     * @param aContinue
     */
    void onContinue(Continue aContinue);
}
