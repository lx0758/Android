package com.liux.android.abstracts;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.liux.android.abstracts.touch.TouchCallback;
import com.liux.android.abstracts.touch.TouchHost;

import java.lang.reflect.Field;

/**
 * 2018/2/12
 * By Liux
 * lx0758@qq.com
 */

public class AbstractsFragmentProxy {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    private IAbstractsFragment mIAbstractsFragment;

    public AbstractsFragmentProxy(IAbstractsFragment IAbstractsFragment) {
        mIAbstractsFragment = IAbstractsFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        restoreHideState(savedInstanceState);
    }

    public void onViewCreated() {
        mViewCreated = true;
        if (!isViewPagerItem()) mUserVisible = true;
        checkLazyLoad();
    }

    public void onStart() {
        checkVisibleChanged();
    }

    public void onStop() {
        mStopCalled = true;
    }

    public void onHiddenChanged(boolean hidden) {
        if (hidden) checkVisibleChanged();
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        boolean callLazyLoad = mCallLazyLoad;

        mUserVisible = isVisibleToUser;
        checkLazyLoad();

        if (callLazyLoad) checkVisibleChanged();
    }

    public void onSaveInstanceState(Bundle outState) {
        saveHideState(outState);
    }

    public void onDetach() {
        fixNestFragmrntBug();
    }

    // ===============================================================

    private void restoreHideState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SAVE_IS_HIDDEN)) {
            boolean isHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            savedInstanceState.remove(STATE_SAVE_IS_HIDDEN);
            Fragment fragment = mIAbstractsFragment.getTarget();
            FragmentManager fm = fragment.getFragmentManager();
            if (fm != null) {
                FragmentTransaction ft = fm.beginTransaction();
                if (isHidden) {
                    ft.hide(fragment);
                } else {
                    ft.show(fragment);
                }
                ft.commit();
            }
        }
    }

    private void saveHideState(Bundle outState) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, mIAbstractsFragment.getTarget().isHidden());
    }

    private void fixNestFragmrntBug() {
        try {
            // http://blog.csdn.net/primer_programer/article/details/27184877
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(mIAbstractsFragment.getTarget(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===============================================================

    private boolean mViewCreated = false;
    private boolean mUserVisible = false;
    private boolean mCallLazyLoad = false;

    /**
     * 检查是否调用懒加载方法
     * 保证在视图创建完成后第一次显示时调用一次目标方法
     */
    private void checkLazyLoad() {
        if (mCallLazyLoad) return;
        if (!mViewCreated) return;
        if (!mUserVisible) return;

        mCallLazyLoad = true;
        mIAbstractsFragment.onLazyLoad();
    }

    private ViewGroup mContainer;

    /**
     * 检查是不是 ViewPager 的一个条目
     * @return
     */
    private boolean isViewPagerItem() {
        if (mContainer == null) {
            try {
                Class clazz = Fragment.class;
                Field[] fields = clazz.getDeclaredFields();
                Field containerField = null;
                for (Field field : fields) {
                    if (field.getType() == ViewGroup.class) {
                        containerField = field;
                        break;
                    }
                }
                if (containerField != null) {
                    containerField.setAccessible(true);
                    mContainer = (ViewGroup) containerField.get(mIAbstractsFragment.getTarget());
                }
            } catch (Exception ignore) {}
        }
        return mContainer instanceof ViewPager;
    }

    /**
     * 当Fragment为第一个展示的页面时,会调用 {@link Fragment#onStart()} 方法
     */
    private boolean mStopCalled = false;

    /**
     * 检查视图状态是否已经改变为可视状态
     * 保证是在懒加载和视图创建完毕之后的生命周期中调用
     */
    private void checkVisibleChanged() {
        if (!mStopCalled) {
            mStopCalled = true;
            return;
        }

        if (!mCallLazyLoad) return;
        if (!mViewCreated) return;
        if (!mUserVisible) return;

        mIAbstractsFragment.onVisibleChanged();
    }

    // ===============================================================

    public boolean isHandlerTouch() {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return false;
        if (!touchHost.isHandlerTouch()) return false;
        if (touchHost.hasIgnoreView(mIAbstractsFragment.getTarget().getView())) return false;
        return true;
    }

    public void setHandlerTouch(boolean handlerTouch) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return;
        if (!touchHost.isHandlerTouch()) return;
        if (handlerTouch) {
            touchHost.removeIgnoreView(mIAbstractsFragment.getTarget().getView());
        } else {
            touchHost.addIgnoreView(mIAbstractsFragment.getTarget().getView());
        }
    }

    public boolean hasIgnoreView(View view) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return false;
        return touchHost.hasIgnoreView(view);
    }

    public void addIgnoreView(View view) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return;
        touchHost.addIgnoreView(view);
    }

    public void removeIgnoreView(View view) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return;
        touchHost.removeIgnoreView(view);
    }

    public void addTouchCallback(TouchCallback touchCallback) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return;
        touchHost.addTouchCallback(touchCallback);
    }

    public void removeTouchCallback(TouchCallback touchCallback) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return;
        touchHost.removeTouchCallback(touchCallback);
    }

    private TouchHost getHandlerTouch() {
        Activity activity = mIAbstractsFragment.getTarget().getActivity();
        if (activity instanceof TouchHost) {
            return (TouchHost) activity;
        }
        return null;
    }
}
