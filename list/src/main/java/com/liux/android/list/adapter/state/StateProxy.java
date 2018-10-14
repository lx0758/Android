package com.liux.android.list.adapter.state;

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
        return getData().getState(position).isSelectSelected();
    }

    public boolean setSelect(T t, boolean selected) {
        return setSelect(getData().indexOf(t), selected);
    }

    public boolean setSelect(int position, boolean selected) {
        if (!isEnabledSelect()) return false;

        if (isSelect(position) == selected) return selected;

        List<T> selectedAll = getSelectedAll();
        if (selected) {
            if (mMaxSelectCount == 1) {
                // 单选模式反转上一个
                for (T t : selectedAll) {
                    int index = getData().indexOf(t);

                    boolean result = true;
                    if (mOnSelectListener != null) {
                        result = mOnSelectListener.onSelectChange(t, index, false);
                    }
                    if (!result) {
                        //if (mOnSelectListener != null) {
                        //    mOnSelectListener.onSelectFailure();
                        //}
                        return false;
                    }

                    getData().getState(index).setSelectUnselected();

                    index = mIState.getShamPosition(index);
                    mIState.notifyItemChanged(index);
                }
            } else if (selectedAll.size() >= mMaxSelectCount) {
                // 多选模式检查
                if (mOnSelectListener != null) {
                    mOnSelectListener.onSelectFailure();
                }
                return false;
            }
        }

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

        State<T> state = getData().getState(position);
        if (selected) {
            state.setSelectSelected();
        } else {
            state.setSelectUnselected();
        }

        int size = getSelectedAll().size();
        if (size >= mMaxSelectCount) {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectComplete();
            }
        }

        position = mIState.getShamPosition(position);
        mIState.notifyItemChanged(position);
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
        return getData().getState(position).isSlideSlided();
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
                for (State<T> s : slideStateAll) {
                    s.setSlideUnslide();
                    int index = getData().indexOf(s.getData());
                    index = mIState.getShamPosition(index);
                    mIState.notifyItemChanged(index);
                }
            }
        }

        State<T> state = getData().getState(position);
        if (slided) {
            state.setSlideSlided();
        } else {
            state.setSlideUnslide();
        }

        position = mIState.getShamPosition(position);
        mIState.notifyItemChanged(position);
        return true;
    }

    /**
     * 开关数据状态
     * @param enabled 是否开启选择
     */
    private void enabledSelectState(boolean enabled) {
        for (State<T> s : getData().getStates()) {
            if (enabled) {
                s.setSelectUnselected();
            } else {
                s.setSelectDisabled();
            }
        }
    }

    /**
     * 切换数据状态
     * @param isSelect 是否选择
     */
    private void switchSelectState(boolean isSelect) {
        for (State<T> s : getData().getStates()) {
            if (isSelect) {
                s.setSelectSelected();
            } else {
                s.setSelectUnselected();
            }
        }
    }

    /**
     * 不考虑反转后数量的操作
     */
    private void reverseSelectStateAll() {
        for (State<T> s : getData().getStates()) {
            if (s.isSelectSelected()) {
                s.setSelectUnselected();
            } else {
                s.setSelectSelected();
            }
        }
    }

    /**
     * 获取某个状态所有数据
     * @param isSelect
     * @return
     */
    private List<T> getSelectStateAll(boolean isSelect) {
        List<T> list = new ArrayList<>();
        if (!isEnabledSelect()) return list;
        for (State<T> s: getData().getStates()) {
            if (isSelect) {
                if (s.isSelectSelected()) list.add(s.getData());
            } else {
                if (s.isSelectUnselected()) list.add(s.getData());
            }
        }
        return list;
    }

    /**
     * 开关数据状态
     * @param enabled
     */
    private void enableSlideState(boolean enabled) {
        for (State<T> s : getData().getStates()) {
            if (enabled) {
                s.setSlideUnslide();
            } else {
                s.setSlideDisabled();
            }
        }
    }

    /**
     * 获取
     * @return
     */
    private List<State<T>> getSlideStateAll(boolean slided) {
        List<State<T>> list = new ArrayList<>();
        if (!isEnabledSlide()) return list;
        for (State<T> s : getState()) {
            if (slided) {
                if (s.isSlideSlided()) list.add(s);
            } else {
                if (s.isSlideUnslide()) list.add(s);
            }
        }
        return list;
    }
}
