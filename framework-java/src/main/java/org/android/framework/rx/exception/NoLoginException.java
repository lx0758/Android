package org.android.framework.rx.exception;

import org.android.framework.rx.IResp;

/**
 * date：2019/1/21 15:52
 * author：Liux
 * email：lx0758@qq.com
 * description：
 */

public class NoLoginException extends RespException {

    public NoLoginException(IResp IResp) {
        super(IResp);
    }
}
