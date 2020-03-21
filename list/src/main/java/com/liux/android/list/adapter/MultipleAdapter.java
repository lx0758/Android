package com.liux.android.list.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liux.android.list.adapter.append.AppendProxy;
import com.liux.android.list.adapter.append.IAppendAdapter;
import com.liux.android.list.adapter.rule.IRuleAdapter;
import com.liux.android.list.adapter.rule.Rule;
import com.liux.android.list.adapter.rule.RuleProxy;
import com.liux.android.list.adapter.state.IStateAdapter;
import com.liux.android.list.adapter.state.StateProxy;
import com.liux.android.list.holder.MarginHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持多项条目的Adapter <br>
 * 典型的应用场景如聊天界面,不同消息类型显示不同布局 <br>
 * 原理是根据不同数据添加不同规则,显示数据时反查询规则实例 <br>
 * Created by Liux on 2017/8/11. <br>
 * <br>
 * 完成适配器 <br>
 * 2017-8-11 <br>
 * <br>
 * 移植添加数据状态能力 <br>
 * 2017-8-11
 * <br>
 * 增强数据状态能力
 * 代理模式重构
 * 2018-3-6
 */

public class MultipleAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        IRuleAdapter<T, MultipleAdapter<T>>, IStateAdapter<T, MultipleAdapter<T>>, IAppendAdapter<T, MultipleAdapter<T>> {

    private List<T> mData;

    private RuleProxy<T, MultipleAdapter<T>> mRuleProxy;
    private StateProxy<T, MultipleAdapter<T>> mStateProxy;
    private AppendProxy<T, MultipleAdapter<T>> mAppendProxy;

    public MultipleAdapter() {
        this(new ArrayList<T>());
    }

    public MultipleAdapter(List<T> data) {
        mData = data;
        mRuleProxy = new RuleProxy<>(this);
        mStateProxy = new StateProxy<>(this, mData, mAppendProxy);
        mAppendProxy = new AppendProxy<>(this);
    }

    @Override
    public int getItemViewType(int position) {
        if (mAppendProxy.isAppendPosition(position)) {
            return mAppendProxy.getAppendPositionType(position);
        }

        position = mAppendProxy.getRealPosition(position);
        T t = mData.get(position);
        return mRuleProxy.getRuleType(t);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mAppendProxy.isAppendType(viewType)) {
            return mAppendProxy.getAppendTypeHolder(viewType);
        }

        Rule rule = mRuleProxy.getTypeRule(viewType);
        return rule.onCreateHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (holder instanceof MarginHolder) return;

        int realPosition = getRealPosition(position);
        T t = mData.get(realPosition);
        Rule rule = mRuleProxy.getObjectRule(t);
        rule.onDataBind(holder, realPosition, t, payloads, mStateProxy.getState(realPosition));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // do nothing
    }

    @Override
    public int getItemCount() {
        int count = 0;
        count += mAppendProxy.getAppendItemCount();
        count += mData.size();
        return count;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mAppendProxy.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        mAppendProxy.onViewAttachedToWindow(holder);
    }

    @Override
    public MultipleAdapter<T> addRule(@NonNull Rule<? extends T, ? extends RecyclerView.ViewHolder> rule) {
        return mRuleProxy.addRule(rule);
    }

    @Override
    public MultipleAdapter<T> setHeader(@NonNull View view) {
        return mAppendProxy.setHeader(view);
    }

    @Override
    public MultipleAdapter<T> setFooter(@NonNull View view) {
        return mAppendProxy.setFooter(view);
    }

    @Override
    public int getRealPosition(int position) {
        return mAppendProxy.getRealPosition(position);
    }

    @Override
    public int getShamPosition(int position) {
        return mAppendProxy.getShamPosition(position);
    }

    @Override
    public boolean isHeaderPosition(int position) {
        return mAppendProxy.isHeaderPosition(position);
    }

    @Override
    public boolean isFooterPosition(int position) {
        return mAppendProxy.isFooterPosition(position);
    }

    public List<T> getData() {
        return mData;
    }
}
