package com.liux.android.abstracts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liux.android.abstracts.touch.TouchCallback;
import com.liux.android.abstracts.touch.TouchHost;

/**
 * 抽象Fragment,提供以下能力 <br>
 * 1.自动隐藏输入法 {@link TouchHost} <br>
 * 2.重定义生命周期细节 {@link #onLazyLoad()} {@link #onVisibleChanged()}
 * 3.修复某些版本某些情况下 Fragent 显示状态不保存的问题
 * Created by Liux on 2017/8/7.
 */

public abstract class AbstractsFragment extends Fragment implements IAbstractsFragment {
    private String TAG = "AbstractsFragment";

    private AbstractsFragmentProxy mProxy = new AbstractsFragmentProxy(this);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProxy.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProxy.onViewCreated();
    }

    @Override
    public void onStart() {
        super.onStart();
        mProxy.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mProxy.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mProxy.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mProxy.onDetach();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mProxy.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mProxy.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public Fragment getTarget() {
        return this;
    }

    @Override
    public boolean isHandlerTouch() {
        return mProxy.isHandlerTouch();
    }

    @Override
    public void setHandlerTouch(boolean handlerTouch) {
        mProxy.setHandlerTouch(handlerTouch);
    }

    @Override
    public boolean hasIgnoreView(View view) {
        return mProxy.hasIgnoreView(view);
    }

    @Override
    public void addIgnoreView(View view) {
        mProxy.addIgnoreView(view);
    }

    @Override
    public void removeIgnoreView(View view) {
        mProxy.removeIgnoreView(view);
    }

    @Override
    public void addTouchCallback(TouchCallback touchCallback) {
        mProxy.addTouchCallback(touchCallback);
    }

    @Override
    public void removeTouchCallback(TouchCallback touchCallback) {
        mProxy.removeTouchCallback(touchCallback);
    }

    @Override
    public void onLazyLoad() {

    }

    @Override
    public void onVisibleChanged() {

    }
}
