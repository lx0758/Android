package com.liux.android.abstracts;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.liux.android.abstracts.touch.TouchCallback;
import com.liux.android.abstracts.touch.TouchHost;

import java.lang.reflect.Field;

/**
 * 抽象Fragment,提供以下能力 <br>
 * 1.自动隐藏输入法 {@link TouchHost} <br>
 * 2.重定义生命周期细节 {@link #onLazyLoad()} {@link #onVisibleChanged()}
 * 3.修复某些版本某些情况下 Fragent 显示状态不保存的问题
 * Created by Liux on 2017/8/7.
 * 2020-3-17 <br>
 * 1.移除代理模式
 */

public abstract class AbstractsFragment extends Fragment {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    private String TAG = "AbstractsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreHideState(savedInstanceState);
    }

    @Nullable
    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewCreated = true;
        if (!isViewPagerItem()) mUserVisible = true;
        checkLazyLoad();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkVisibleChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        mStopCalled = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveHideState(outState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) checkVisibleChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        boolean callLazyLoad = mCallLazyLoad;

        mUserVisible = isVisibleToUser;
        checkLazyLoad();

        if (callLazyLoad) checkVisibleChanged();
    }

    // ===============================================================

    private void restoreHideState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SAVE_IS_HIDDEN)) {
            boolean isHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            savedInstanceState.remove(STATE_SAVE_IS_HIDDEN);
            Fragment fragment = this;
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
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    // ===============================================================

    public void onVisibleChanged() {

    }

    public void onLazyLoad() {

    }

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
        onLazyLoad();
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
                    mContainer = (ViewGroup) containerField.get(this);
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

        onVisibleChanged();
    }

    // ===============================================================

    public boolean isHandlerTouch() {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return false;
        if (!touchHost.isHandlerTouch()) return false;
        if (touchHost.hasIgnoreView(getView())) return false;
        return true;
    }

    public void setHandlerTouch(boolean handlerTouch) {
        TouchHost touchHost = getHandlerTouch();
        if (touchHost == null) return;
        if (!touchHost.isHandlerTouch()) return;
        if (handlerTouch) {
            touchHost.removeIgnoreView(getView());
        } else {
            touchHost.addIgnoreView(getView());
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
        Activity activity = getActivity();
        if (activity instanceof TouchHost) {
            return (TouchHost) activity;
        }
        return null;
    }
}
