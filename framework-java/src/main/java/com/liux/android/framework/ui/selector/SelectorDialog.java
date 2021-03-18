package com.liux.android.framework.ui.selector;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.liux.android.framework.R;

import java.util.ArrayList;
import java.util.List;

/**
 * date：2018/11/26 14:51
 * author：Liux
 * email：lx0758@qq.com
 * description：多级别选择器控件
 */
public class SelectorDialog extends AppCompatDialogFragment implements SelectorFragment.OnSelectedListener {

    private Selector selector;
    private SelectorRequest.Handler handler;
    private SelectorCallback selectorCallback;

    private TextView textView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SelectorPagerAdapter selectorPagerAdapter;

    private String title = "请选择";
    private int maxLevel = Integer.MAX_VALUE;

    // 已选择的数据
    private List<SelectorBean> selectedBeans = new ArrayList<>();
    // 缓存的数据源
    private List<List<? extends SelectorBean>> selectorBeanLists = new ArrayList<>();

    public SelectorDialog(Selector selector, SelectorRequest.Handler handler, SelectorCallback selectorCallback) {
        this.selector = selector;
        this.handler = handler;
        this.selectorCallback = selectorCallback;
    }

    public void setTitle(String title) {
        this.title = title;
        if (textView != null) {
            textView.setText(title);
        }
    }

    public void setMaxLevel(int maxLevel) {
        if (maxLevel < 1) maxLevel = 1;
        this.maxLevel = maxLevel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_selector, container, false);
        textView = view.findViewById(R.id.tv_title);
        textView.setText(title);
        tabLayout = view.findViewById(R.id.tl_tab);
        viewPager = view.findViewById(R.id.vp_content);
        if (maxLevel == 1) tabLayout.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectorPagerAdapter = new SelectorPagerAdapter(this);
        viewPager.setAdapter(selectorPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AppCompatDialog dialog = (AppCompatDialog) super.onCreateDialog(savedInstanceState);

        dialog.getDelegate().requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = getResources().getDisplayMetrics().heightPixels * 2 / 3;
        attributes.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(attributes);

        return dialog;
    }

    public void show(List<? extends SelectorBean> firstSelectorBeans, FragmentManager fragmentManager, String tag) {
        selectorBeanLists.clear();
        selectorBeanLists.add(firstSelectorBeans);
        show(fragmentManager, tag);
    }

    @Override
    public void onSelected(int level, SelectorBean selectorBean) {
        // 如果是已经选到最终级数,则返回
        if (level >= maxLevel) {
            selectedBeans.add(selectorBean);
            selectorCallback.onSelected(selectedBeans);
            dismiss();
            return;
        }

        // 如果选择的层级小于已经选择过的层级, 则清除子层级
        if (level <= selectedBeans.size()) {
            do {
                selectedBeans.remove(selectedBeans.size() - 1);
            } while (level <= selectedBeans.size());
            // 删除子层级
            deleteSubLevel(level);
        }
        selectedBeans.add(selectorBean);

        requestLevel(level + 1, selectorBean);
    }

    List<? extends SelectorBean> getSelectedBeans() {
        return selectedBeans;
    }

    List<List<? extends SelectorBean>> getSelectorBeans() {
        return selectorBeanLists;
    }

    /**
     * 请求某层数据
     * @param level
     * @param lastLevelSelectedBean
     */
    private void requestLevel(int level, SelectorBean lastLevelSelectedBean) {
        handler.request(new SelectorRequest(this), level, lastLevelSelectedBean, new SelectorRequest.Callback() {
            @Override
            public void onShow(List<? extends SelectorBean> selectorBeanList) {
                if (selectorBeanList == null || selectorBeanList.isEmpty()) {
                    selectorCallback.onEmptyData(selector, level, lastLevelSelectedBean);
                    return;
                }
                addLevel(level, selectorBeanList);
            }

            @Override
            public void onComplete() {
                selectorCallback.onSelected(selectedBeans);
                dismiss();
            }
        });
    }

    /**
     * 添加层级
     * @param level
     * @param selectorBeanList
     */
    private void addLevel(int level, List<? extends SelectorBean> selectorBeanList) {
        if (level > selectorBeanLists.size()) {
            selectorBeanLists.add(selectorBeanList);
        } else {
            selectorBeanLists.set(level - 1, selectorBeanList);
        }
        selectorPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(level - 1, false);
    }

    /**
     * 删除子层级
     * @param level
     */
    private void deleteSubLevel(int level) {
        do {
            selectorBeanLists.remove(selectorBeanLists.size() - 1);
        } while (level < selectorBeanLists.size());
        selectorPagerAdapter.notifyDataSetChanged();
    }
}
