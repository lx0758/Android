package org.android.framework.ui.selector;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

/**
 * date：2018/11/26 16:27
 * author：Liux
 * email：lx0758@qq.com
 * description：
 */
public class SelectorPagerAdapter extends FragmentPagerAdapter {

    private SelectorDialog selectorDialog;

    public SelectorPagerAdapter(SelectorDialog selectorDialog) {
        super(selectorDialog.getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.selectorDialog = selectorDialog;
    }

    @Override
    public Fragment getItem(int position) {
        SelectorFragment selectorFragment = new SelectorFragment();
        selectorFragment.refresh(position + 1, selectorDialog.getSelectorBeans().get(position), selectorDialog);
        return selectorFragment;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        SelectorFragment selectorFragment = (SelectorFragment) super.instantiateItem(container, position);
        selectorFragment.refresh(position + 1, selectorDialog.getSelectorBeans().get(position), selectorDialog);
        return selectorFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (selectorDialog.getSelectedBeans().size() < position + 1) return "请选择";
        return selectorDialog.getSelectedBeans().get(position).getILabel();
    }

    @Override
    public int getCount() {
        return selectorDialog.getSelectorBeans().size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        SelectorFragment selectorFragment = (SelectorFragment) object;
        if (selectorDialog.getSelectedBeans().size() < selectorFragment.getLevel()) return POSITION_NONE;
        boolean unchanged = selectorDialog.getSelectedBeans().get(selectorFragment.getLevel() - 1) == selectorFragment.getSelectorBeans();
        return unchanged ? PagerAdapter.POSITION_UNCHANGED : PagerAdapter.POSITION_NONE;
    }
}