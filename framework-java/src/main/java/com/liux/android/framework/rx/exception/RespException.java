package com.liux.android.framework.rx.exception;

import com.liux.android.framework.rx.IResp;

/**
 * date：2019/1/10 13:34
 * author：Liux
 * email：lx0758@qq.com
 * description：
 */

public class RespException extends RuntimeException {
    private IResp mIResp;

    public RespException(IResp IResp) {
        super(IResp.message());
        mIResp = IResp;
    }

    public IResp getResp() {
        return mIResp;
    }

    public void setResp(IResp IResp) {
        mIResp = IResp;
    }
}
