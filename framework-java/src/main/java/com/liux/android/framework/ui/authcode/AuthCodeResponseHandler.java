package com.liux.android.framework.ui.authcode;

/**
 * 验证码发送响应处理器
 * @param <R>
 */
public interface AuthCodeResponseHandler<R> {

    /**
     * 是否需要刷新图形验证码
     * @param r
     * @return
     */
    boolean needRefreshImageCode(R r);

    /**
     * 检查响应判断是否发送成功
     * @param r
     * @return
     */
    String onResponse(R r) throws Exception;

    /**
     * 错误异常, 并返回消息
     * @param e
     * @return
     */
    String onFailure(Throwable e);
}
