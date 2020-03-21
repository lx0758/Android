package com.liux.android.list.adapter.state;

/**
 * 状态记录
 * Created by Liux on 2017/9/19.
 */

public interface IStateBean {

    Object getState(String key);

    void putState(String key, Object tag);
}
