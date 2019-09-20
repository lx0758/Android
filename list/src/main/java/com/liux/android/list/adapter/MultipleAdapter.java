package com.liux.android.list.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.liux.android.list.adapter.append.AppendProxy;
import com.liux.android.list.adapter.append.IAppend;
import com.liux.android.list.adapter.rule.IRule;
import com.liux.android.list.adapter.rule.Rule;
import com.liux.android.list.adapter.rule.RuleProxy;
import com.liux.android.list.adapter.state.IState;
import com.liux.android.list.adapter.state.State;
import com.liux.android.list.adapter.state.StateProxy;
import com.liux.android.list.holder.MarginHolder;
import com.liux.android.list.listener.OnSelectListener;

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
        IRule<T, MultipleAdapter>, IState<T, MultipleAdapter>, IAppend<T, MultipleAdapter> {

    private RuleProxy<T> mRuleProxy = new RuleProxy<>(this);
    private StateProxy<T> mStateProxy = new StateProxy<>(this);
    private AppendProxy<T> mAppendProxy = new AppendProxy<>(this);

    public MultipleAdapter() {

    }

    @Override
    public int getItemViewType(int position) {
        if (mAppendProxy.isAppendPosition(position)) {
            return mAppendProxy.getAppendPositionType(position);
        }

        position = mAppendProxy.getRealPosition(position);

        T t = getData().get(position);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MarginHolder) {
            return;
        }

        position = getRealPosition(position);

        T t = getData().get(position);
        Rule rule = mRuleProxy.getObjectRule(t);

        State<T> state = mStateProxy.getData().getState(position);
        if (!isEnabledSelect()) state.setSelectDisabled();

        rule.onDataBind(holder, t, state, position);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        count += mAppendProxy.getAppendItemCount();
        count += mStateProxy.getData().size();
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
        mRuleProxy.addRule(rule);
        return this;
    }

    @Override
    public MultipleAdapter<T> setHeader(@NonNull View view) {
        mAppendProxy.setHeader(view);
        return this;
    }

    @Override
    public MultipleAdapter<T> setFooter(@NonNull View view) {
        mAppendProxy.setFooter(view);
        return this;
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

    @Override
    public List<T> getData() {
        return mStateProxy.getData();
    }

    @Override
    public List<State<T>> getState() {
        return mStateProxy.getState();
    }

    @Override
    public boolean isEnabledSelect() {
        return mStateProxy.isEnabledSelect();
    }

    @Override
    public MultipleAdapter<T> setEnabledSelect(boolean enabled) {
        mStateProxy.setOpenSelect(enabled);
        return this;
    }

    @Override
    public MultipleAdapter<T> setEnabledSelect(boolean enabled, int maxSelectCount) {
        mStateProxy.setOpenSelect(enabled, maxSelectCount);
        return this;
    }

    @Override
    public int getMaxSelectCount() {
        return mStateProxy.getMaxSelectCount();
    }

    @Override
    public MultipleAdapter<T> setMaxSelectCount(int count) {
        mStateProxy.setMaxSelectCount(count);
        return this;
    }

    @Override
    public boolean toggleSelect(@NonNull T t) {
        return mStateProxy.toggleSelect(t);
    }

    @Override
    public boolean toggleSelect(int position) {
        return mStateProxy.toggleSelect(position);
    }

    @Override
    public boolean isSelect(@NonNull T t) {
        return mStateProxy.isSelect(t);
    }

    @Override
    public boolean isSelect(int position) {
        return mStateProxy.isSelect(position);
    }

    @Override
    public boolean setSelect(T t, boolean selected) {
        return mStateProxy.setSelect(t, selected);
    }

    @Override
    public boolean setSelect(int position, boolean selected) {
        return mStateProxy.setSelect(position, selected);
    }

    @Override
    public boolean selectAll() {
        return mStateProxy.selectAll();
    }

    @Override
    public boolean unSelectAll() {
        return mStateProxy.unSelectAll();
    }

    @Override
    public boolean reverseSelectAll() {
        return mStateProxy.reverseSelectAll();
    }

    @Override
    public List<T> getSelectedAll() {
        return mStateProxy.getSelectedAll();
    }

    @Override
    public List<T> getUnselectedAll() {
        return mStateProxy.getUnselectedAll();
    }

    @Override
    public MultipleAdapter<T> setOnSelectListener(OnSelectListener<T> listener) {
        mStateProxy.setOnSelectListener(listener);
        return this;
    }

    @Override
    public boolean isEnabledSlide() {
        return mStateProxy.isEnabledSlide();
    }

    @Override
    public MultipleAdapter<T> setEnabledSlide(boolean enabled) {
        mStateProxy.setEnabledSlide(enabled);
        return this;
    }

    @Override
    public MultipleAdapter<T> setEnabledSlide(boolean enabled, boolean single) {
        mStateProxy.setEnabledSlide(enabled, single);
        return this;
    }

    @Override
    public void toggleSlide(T t) {
        mStateProxy.toggleSlide(t);
    }

    @Override
    public void toggleSlide(int position) {
        mStateProxy.toggleSlide(position);
    }

    @Override
    public boolean isSlide(T t) {
        return mStateProxy.isSlide(t);
    }

    @Override
    public boolean isSlide(int position) {
        return mStateProxy.isSlide(position);
    }

    @Override
    public void setSlide(T t, boolean slided) {
        mStateProxy.setSlide(t, slided);
    }

    @Override
    public void setSlide(int position, boolean slided) {
        mStateProxy.setSlide(position, slided);
    }
}
