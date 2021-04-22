package com.liux.android.list.helper;

import com.liux.android.list.adapter.MultipleAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectHelper<T extends SelectBean> {

    private MultipleAdapter<T> mMultipleAdapter;

    private boolean mEnableSelect;
    private int mMaxSelectCount;
    private SelectCallback<T> mSelectCallback;

    public SelectHelper(MultipleAdapter<T> multipleAdapter) {
        this(multipleAdapter, 1);
    }

    public SelectHelper(MultipleAdapter<T> multipleAdapter, int maxSelectCount) {
        this.mMultipleAdapter = multipleAdapter;
        setEnableSelect(true, false);
        setMaxSelectCount(maxSelectCount);
    }

    /**
     * 是都开启选择
     * @return
     */
    public boolean isEnableSelect() {
        return mEnableSelect;
    }

    /**
     * 设置是否开启选择
     * @param enableSelect
     * @param reset
     */
    public void setEnableSelect(boolean enableSelect, boolean reset) {
        mEnableSelect = enableSelect;
        if (reset) {
            for (T t : mMultipleAdapter.getData()) {
                t.setSelected(false);
            }
            mMultipleAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取最大可选择数
     *
     * @return 最大可选择数
     */
    public int getMaxSelectCount() {
        return mMaxSelectCount;
    }

    /**
     * 设置最大可选择数
     *
     * @param maxSelectCount 最大可选择数
     */
    public void setMaxSelectCount(int maxSelectCount) {
        mMaxSelectCount = maxSelectCount;
        if (mMaxSelectCount < 1) mMaxSelectCount = 1;
    }

    /**
     * 某条数据是否选中
     *
     * @param t 数据
     * @return 是否选中
     */
    public boolean isSelected(T t) {
        return t.isSelected();
    }

    /**
     * 设置某条数据选中状态
     *
     * @param t 数据
     * @param selected 选中状态
     * @return 是否选中
     */
    public boolean setSelected(T t, boolean selected) {
        if (!mEnableSelect || mMultipleAdapter.getData().indexOf(t) == -1) {
            if (mSelectCallback != null) mSelectCallback.onSelectFailure(SelectCallback.TYPE_NOT_SUPPORT);
            return false;
        }
        if (mSelectCallback != null) {
            if (!mSelectCallback.onSelectBefore(t)) {
                if (mSelectCallback != null) mSelectCallback.onSelectFailure(SelectCallback.TYPE_CALLBACK_PROHIBIT);
                return false;
            }
        }
        if (selected)
            return selected(t);
        else
            return unselected(t);
    }

    /**
     * 切换某条数据选中状态
     *
     * @param t 数据
     * @return 是否选中
     */
    public boolean toggleSelect(T t) {
        return setSelected(t, !t.isSelected());
    }

    /**
     * 全选
     *
     * @return 是否全选成功
     */
    boolean selectAll() {
        if (!mEnableSelect || mMultipleAdapter.getData().size() > mMaxSelectCount) {
            if (mSelectCallback != null) mSelectCallback.onSelectFailure(SelectCallback.TYPE_NOT_SUPPORT);
            return false;
        }
        for (T t : mMultipleAdapter.getData()) {
            t.setSelected(true);
        }
        mMultipleAdapter.notifyDataSetChanged();
        return true;
    }

    /**
     * 全不选
     *
     * @return 是否全不选成功
     */
    public boolean unselectAll() {
        if (!mEnableSelect) {
            if (mSelectCallback != null) mSelectCallback.onSelectFailure(SelectCallback.TYPE_NOT_SUPPORT);
            return false;
        }
        for (T t : mMultipleAdapter.getData()) {
            t.setSelected(false);
        }
        mMultipleAdapter.notifyDataSetChanged();
        return true;
    }

    /**
     * 反选
     *
     * @return 是否反选成功
     */
    public boolean reverseSelectAll() {
        if (!mEnableSelect || getUnselectedAll().size() > mMaxSelectCount) {
            if (mSelectCallback != null) mSelectCallback.onSelectFailure(SelectCallback.TYPE_NOT_SUPPORT);
            return false;
        }
        for (T t : mMultipleAdapter.getData()) {
            t.setSelected(!t.isSelected());
        }
        mMultipleAdapter.notifyDataSetChanged();
        return true;
    }

    /**
     * 获取已选中的全部数据
     *
     * @return 数据列表
     */
    public List<T> getSelectedAll() {
        List<T> ts = new ArrayList<>();
        for (T t : mMultipleAdapter.getData()) {
            if (t.isSelected()) ts.add(t);
        }
        return ts;
    }

    /**
     * 获取未选中的全部数据
     *
     * @return 数据列表
     */
    public List<T> getUnselectedAll() {
        List<T> ts = new ArrayList<>();
        for (T t : mMultipleAdapter.getData()) {
            if (!t.isSelected()) ts.add(t);
        }
        return ts;
    }

    /**
     * 设置选择事件回调
     *
     * @param selectCallback 回调
     */
    public void setSelectCallback(SelectCallback<T> selectCallback) {
        this.mSelectCallback = selectCallback;
    }

    /**
     * 选中某条数据
     * @param t
     * @return
     */
    private boolean selected(T t) {
        List<T> selectedTs = getSelectedAll();

        if (mMaxSelectCount == 1) {
            int selectedCount = selectedTs.size();
            if (selectedCount == 1) {
                // 有一个被选中对象, 需要先取消选中之前的数据, 再选中当前数据
                T selectedT = selectedTs.get(0);
                selectedT.setSelected(false);
                if (mSelectCallback != null) mSelectCallback.onSelect(selectedT, false);
                int selectedPosition = mMultipleAdapter.getAdapterPosition(mMultipleAdapter.getData().indexOf(selectedT));
                mMultipleAdapter.notifyItemChanged(selectedPosition, SelectHelper.this);
            } else if (selectedCount > 1) {
                // 有多个被选中对象, 需要先批量取消选中之前的数据, 再选中当前数据
                for (T selectedT : selectedTs) {
                    selectedT.setSelected(false);
                    if (mSelectCallback != null) mSelectCallback.onSelect(selectedT, false);
                }
                mMultipleAdapter.notifyDataSetChanged();
            }
            t.setSelected(true);
            if (mSelectCallback != null) mSelectCallback.onSelect(t, true);
            int position = mMultipleAdapter.getAdapterPosition(mMultipleAdapter.getData().indexOf(t));
            mMultipleAdapter.notifyItemChanged(position, SelectHelper.this);
            return true;
        }

        if (selectedTs.size() + 1 > mMaxSelectCount) {
            if (mSelectCallback != null) mSelectCallback.onSelectFailure(SelectCallback.TYPE_COUNT_FULL);
            return false;
        }
        t.setSelected(true);
        if (mSelectCallback != null) {
            mSelectCallback.onSelect(t, true);
            if (selectedTs.size() + 1 == mMaxSelectCount && mMaxSelectCount > 1) mSelectCallback.onSelectFull();
        }
        int position = mMultipleAdapter.getAdapterPosition(mMultipleAdapter.getData().indexOf(t));
        mMultipleAdapter.notifyItemChanged(position, SelectHelper.this);
        return true;
    }

    /**
     * 取消选择某条数据
     * @param t
     * @return
     */
    private boolean unselected(T t) {
        t.setSelected(false);
        if (mSelectCallback != null) mSelectCallback.onSelect(t, false);
        int position = mMultipleAdapter.getAdapterPosition(mMultipleAdapter.getData().indexOf(t));
        mMultipleAdapter.notifyItemChanged(position, SelectHelper.this);
        return true;
    }
}
