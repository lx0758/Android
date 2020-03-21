package com.liux.android.list.adapter.state;

import androidx.recyclerview.widget.RecyclerView;

import com.liux.android.list.adapter.Payload;
import com.liux.android.list.adapter.append.IAppendAdapter;

import java.util.List;

/**
 * 2018/3/6
 * By Liux
 * lx0758@qq.com
 */

public class StateProxy<T, R extends RecyclerView.Adapter> implements IStateAdapter<T, R>, StateCallback {

    private R mAdapter;
    private List<T> mData;
    private IAppendAdapter<T, R> mIAppendAdapter;

    public StateProxy(R adapter, List<T> data, IAppendAdapter<T, R> appendProxy) {
        mAdapter = adapter;
        mData = data;
        mIAppendAdapter = appendProxy;
    }
    @Override
    public void notifyChanged(IStateBean iStateBean) {
        int position = mData.indexOf(iStateBean);
        if (position == -1) return;
        position = mIAppendAdapter.getShamPosition(position);
        mAdapter.notifyItemChanged(position, Payload.STATE);
    }

    public State getState(int position) {
        T t = mData.get(position);
        if (t instanceof IStateBean) return new State((IStateBean) t, this);
        return null;
    }
}
