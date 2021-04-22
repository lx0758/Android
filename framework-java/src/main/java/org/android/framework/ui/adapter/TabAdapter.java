package org.android.framework.ui.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * 2017/11/11
 * By Liux
 * lx0758@qq.com
 */

public class TabAdapter extends FragmentPagerAdapter {
    private Fragment[] mFragments;

    public TabAdapter(FragmentManager fm, Fragment... fragments) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Fragment fragment = getItem(position);
        if (fragment instanceof TitleFragment) {
            TitleFragment titleFragment = (TitleFragment) fragment;
            return titleFragment.getTitle();
        }
        return "";
    }

    public interface TitleFragment {

        CharSequence getTitle();
    }
}
