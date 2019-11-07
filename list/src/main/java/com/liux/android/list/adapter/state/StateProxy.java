package com.liux.android.list.adapter.state;

import com.liux.android.list.adapter.Payload;
import com.liux.android.list.listener.OnSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 2018/3/6
 * By Liux
 * lx0758@qq.com
 */

public class StateProxy<T> {

    private IState mIState;

    private StateList<T> mStateList = new StateList<>();

    private int mMaxSelectCount = 0;
    private boolean mEnabledSelect = false;
    private OnSelectListener<T> mOnSelectListener;

    private boolean mSingleSlide = false;
    private boolean mEnabledSlide = false;

    public StateProxy(IState iState) {
        mIState = iState;
    }

    public void setData(List<T> dataSource) {
        getData().clear();
        if (dataSource != null) {
            getData().addAll(dataSource);
        }
    }

    public StateList<T> getData() {
        return mStateList;
    }

    public List<State<T>> getState() {
        return getData().getStates();
    }

    public boolean isEnabledSelect() {
        return mEnabledSelect;
    }

    public void setOpenSelect(boolean enabled) {
        setOpenSelect(enabled, 1);
    }

    public void setOpenSelect(boolean enabled, int maxSelectCount) {
        if (isEnabledSelect() == enabled) return;

        enabledSelectState(enabled);
        mEnabledSelect = enabled;

        if (isEnabledSelect() && maxSelectCount < 1) {
            maxSelectCount = 1;
        }
        setMaxSelectCount(maxSelectCount);

        mIState.notifyDataSetChanged();
    }

    public int getMaxSelectCount() {
        return mMaxSelectCount;
    }

    public void setMaxSelectCount(int count) {
        if (isEnabledSelect()) {
            mMaxSelectCount = count;
        }
    }

    public boolean toggleSelect(T t) {
        return toggleSelect(getData().indexOf(t));
    }

    public boolean toggleSelect(int position) {
        if (!isEnabledSelect()) return false;
        return setSelect(position, !isSelect(position));
    }

    public boolean isSelect(T t) {
        return isSelect(getData().indexOf(t));
    }

    public boolean isSelect(int position) {
        return getData().getState(position).isSelected();
    }

    public boolean setSelect(T t, boolean selected) {
        return setSelect(getData().indexOf(t), selected);
    }

    public boolean setSelect(int position, boolean selected) {
        if (!isEnabledSelect()) return false;

        if (isSelect(position) == selected) return selected;

        boolean result = true;
        if (mOnSelectListener != null) {
            result = mOnSelectListener.onSelectChange(getData().get(position), position, selected);
        }
        if (!result) {
            //if (mOnSelectListener != null) {
            //    mOnSelectListener.onSelectFailure();
            //}
            return false;
        }

        List<T> selectedAll = getSelectedAll();
        if (selected) {
            if (mMaxSelectCount == 1) {
                // 单选模式反转上一个
                for (T t : selectedAll) {
                    int index = getData().indexOf(t);

                    result = true;
                    if (mOnSelectListener != null) {
                        result = mOnSelectListener.onSelectChange(t, index, false);
                    }
                    if (!result) {
                        //if (mOnSelectListener != null) {
                        //    mOnSelectListener.onSelectFailure();
                        //}
                        return false;
                    }

                    getData().getState(index).setSelected(false);

                    index = mIState.getShamPosition(index);
                    mIState.notifyItemChanged(index, Payload.STATE);
                }
            } else if (selectedAll.size() >= mMaxSelectCount) {
                // 多选模式检查
                if (mOnSelectListener != null) {
                    mOnSelectListener.onSelectFailure();
                }
                return false;
            }
        }

        State<T> state = getData().getState(position);
        state.setSelected(selected);

        int size = getSelectedAll().size();
        if (size >= mMaxSelectCount) {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectComplete();
            }
        }

        position = mIState.getShamPosition(position);
        mIState.notifyItemChanged(position, Payload.STATE);
        return true;
    }

    public boolean selectAll() {
        if (!isEnabledSelect()) return false;

        if (getData().size() > mMaxSelectCount) {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectFailure();
            }
            return false;
        }

        switchSelectState(true);
        mIState.notifyDataSetChanged();
        return true;
    }

    public boolean unSelectAll() {
        if (!isEnabledSelect()) return false;

        switchSelectState(false);
        mIState.notifyDataSetChanged();
        return true;
    }

    public boolean reverseSelectAll() {
        if (!isEnabledSelect()) return false;

        int size = getSelectedAll().size();
        if (getData().size() - size > mMaxSelectCount) {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectFailure();
            }
            return false;
        }

        reverseSelectStateAll();
        mIState.notifyDataSetChanged();
        return true;
    }

    public List<T> getSelectedAll() {
        return getSelectStateAll(true);
    }

    public List<T> getUnselectedAll() {
        return getSelectStateAll(false);
    }

    public void setOnSelectListener(OnSelectListener<T> listener) {
        mOnSelectListener = listener;
    }

    public boolean isEnabledSlide() {
        return mEnabledSlide;
    }

    public void setEnabledSlide(boolean enabled) {
        setEnabledSlide(enabled, true);
    }

    public void setEnabledSlide(boolean enabled, boolean single) {
        if (isEnabledSlide() == enabled) return;

        enableSlideState(enabled);
        mEnabledSlide = enabled;

        mSingleSlide = single;

        mIState.notifyDataSetChanged();
    }

    public boolean toggleSlide(T t) {
        return toggleSelect(getData().indexOf(t));
    }

    public boolean toggleSlide(int position) {
        if (!isEnabledSlide()) return false;
        return setSlide(position, !isSlide(position));
    }

    public boolean isSlide(T t) {
        return isSlide(getData().indexOf(t));
    }

    public boolean isSlide(int position) {
        return getData().getState(position).isSlideOpen();
    }

    public boolean setSlide(T t, boolean slided) {
        return setSlide(getData().indexOf(t), slided);
    }

    public boolean setSlide(int position, boolean slided) {
        if (!isEnabledSlide()) return false;

        if (isSlide(position) == slided) return slided;

        if (slided) {
            if (mSingleSlide) {
                // 单选模式反转上一个
                List<State<T>> slideStateAll = getSlideStateAll(true);
                for (State<T> state : slideStateAll) {
                    state.setSlideOpen(false);
                    int index = getData().indexOf(state.getData());
                    index = mIState.getShamPosition(index);
                    mIState.notifyItemChanged(index, Payload.STATE);
                }
            }
        }

        State<T> state = getData().getState(position);
        state.setSlideOpen(slided);

        position = mIState.getShamPosition(position);
        mIState.notifyItemChanged(position, Payload.STATE);
        return true;
    }

    /**
     * 开关数据状态
     * @param enabled 是否开启选择
     */
    private void enabledSelectState(boolean enabled) {
        for (State<T> state : getData().getStates()) {
            state.setSupportSelect(enabled);
            state.setSelected(false);
        }
    }

    /**
     * 切换数据状态
     * @param selected 是否选择
     */
    private void switchSelectState(boolean selected) {
        for (State<T> state : getData().getStates()) {
            state.setSelected(selected);
        }
    }

    /**
     * 不考虑反转后数量的操作
     */
    private void reverseSelectStateAll() {
        for (State<T> state : getData().getStates()) {
            state.setSelected(!state.isSelected());
        }
    }

    /**
     * 获取某个状态所有数据
     * @param selected
     * @return
     */
    private List<T> getSelectStateAll(boolean selected) {
        List<T> list = new ArrayList<>();
        if (!isEnabledSelect()) return list;
        for (State<T> state: getData().getStates()) {
            if (selected == state.isSelected()) {
                list.add(state.getData());
            }
        }
        return list;
    }

    /**
     * 开关数据状态
     * @param enabled
     */
    private void enableSlideState(boolean enabled) {
        for (State<T> state : getData().getStates()) {
            state.setSupportSlide(enabled);
        }
    }

    /**
     * 获取
     * @return
     */
    private List<State<T>> getSlideStateAll(boolean slided) {
        List<State<T>> list = new ArrayList<>();
        if (!isEnabledSlide()) return list;
        for (State<T> state : getState()) {
            if (slided == state.isSlideOpen()) {
                list.add(state);
            }
        }
        return list;
    }
}
