package com.liux.android.list.helper;

import com.liux.android.list.adapter.MultipleAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectHelper<T extends SelectBean> {

    private MultipleAdapter<T> mMultipleAdapter;

    private int mMaxSelectCount;
    private SelectCallback<T> mSelectCallback;

    public SelectHelper(MultipleAdapter<T> multipleAdapter) {
        this(multipleAdapter, 1);
    }

    public SelectHelper(MultipleAdapter<T> multipleAdapter, int maxSelectCount) {
        this.mMultipleAdapter = multipleAdapter;
        setMaxSelectCount(maxSelectCount);
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
     * @param position 数据位置
     * @return 是否选中
     */
    public boolean isSelected(int position) {
        return mMultipleAdapter.getData().get(mMultipleAdapter.getRealPosition(position)).isSelected();
    }

    /**
     * 设置某条数据选中状态
     *
     * @param position 数据位置
     * @param selected 选中状态
     * @return 是否选中
     */
    public boolean setSelected(int position, boolean selected) {
        if (mSelectCallback != null) {
            if (!mSelectCallback.onSelectBefore(mMultipleAdapter.getData().get(mMultipleAdapter.getRealPosition(position)))) return false;
        }
        if (selected)
            return selected(position);
        else
            return unselected(position);
    }

    /**
     * 切换某条数据选中状态
     *
     * @param position 数据位置
     * @return 是否选中
     */
    public boolean toggleSelect(int position) {
        return setSelected(position, !mMultipleAdapter.getData().get(mMultipleAdapter.getRealPosition(position)).isSelected());
    }

    /**
     * 全选
     *
     * @return 是否全选成功
     */
    boolean selectAll() {
        if (mMultipleAdapter.getData().size() > mMaxSelectCount) {
            if (mSelectCallback != null) mSelectCallback.onSelectFailure();
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
        if (getUnselectedAll().size() > mMaxSelectCount) {
            if (mSelectCallback != null) mSelectCallback.onSelectFailure();
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
     * @param position
     * @return
     */
    private boolean selected(int position) {
        List<T> selectedTs = getSelectedAll();
        if (mMaxSelectCount == 1) {
            int selectedCount = selectedTs.size();
            if (selectedCount == 1) {
                // 有一个被选中对象, 需要先取消选中之前的数据, 再选中当前数据
                T selectedT = selectedTs.get(0);
                selectedT.setSelected(false);
                if (mSelectCallback != null) mSelectCallback.onSelect(selectedT, false);
                int selectedPosition = mMultipleAdapter.getShamPosition(mMultipleAdapter.getData().indexOf(selectedT));
                mMultipleAdapter.notifyItemChanged(selectedPosition, SelectHelper.this);
            } else if (selectedCount > 1) {
                // 有多个被选中对象, 需要先批量取消选中之前的数据, 再选中当前数据
                for (T selectedT : selectedTs) {
                    selectedT.setSelected(false);
                    if (mSelectCallback != null) mSelectCallback.onSelect(selectedT, false);
                }
                mMultipleAdapter.notifyDataSetChanged();
            }
            T t = mMultipleAdapter.getData().get(mMultipleAdapter.getRealPosition(position));
            t.setSelected(true);
            if (mSelectCallback != null) mSelectCallback.onSelect(t, true);
            mMultipleAdapter.notifyItemChanged(position, SelectHelper.this);
            return true;
        }
        if (selectedTs.size() + 1 > mMaxSelectCount) {
            if (mSelectCallback != null) mSelectCallback.onSelectFailure();
            return false;
        }
        T t = mMultipleAdapter.getData().get(mMultipleAdapter.getRealPosition(position));
        t.setSelected(true);
        if (mSelectCallback != null) {
            mSelectCallback.onSelect(t, true);
            if (selectedTs.size() + 1 == mMaxSelectCount && mMaxSelectCount > 1) mSelectCallback.onSelectFull();
        }
        mMultipleAdapter.notifyItemChanged(position, SelectHelper.this);
        return true;
    }

    /**
     * 取消选择某条数据
     * @param position
     * @return
     */
    private boolean unselected(int position) {
        T t = mMultipleAdapter.getData().get(mMultipleAdapter.getRealPosition(position));
        t.setSelected(false);
        if (mSelectCallback != null) mSelectCallback.onSelect(t, false);
        mMultipleAdapter.notifyItemChanged(position, SelectHelper.this);
        return true;
    }
}
