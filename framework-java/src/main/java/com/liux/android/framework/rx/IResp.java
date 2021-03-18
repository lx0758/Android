package com.liux.android.framework.rx;

/**
 * API 返回状态数据抽象
 * 2016/12/19
 * By Liux
 * lx0758@qq.com
 */

public interface IResp<T> {

    int code();

    String message();

    T data();

    boolean successful();

    boolean needLogin();

    boolean needUpdate();
}
