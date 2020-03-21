package com.liux.android.list.adapter.state;

public class State {

    private IStateBean mIStateBean;
    private StateCallback mStateCallback;

    public State(IStateBean iStateBean, StateCallback stateCallback) {
        mIStateBean = iStateBean;
        mStateCallback = stateCallback;
    }

    public <T> T getState(String key) {
        return (T) mIStateBean.getState(key);
    }

    public void putState(String key, Object tag, boolean notifyChanged) {
        mIStateBean.putState(key, tag);
        if (notifyChanged) mStateCallback.notifyChanged(mIStateBean);
    }
}
