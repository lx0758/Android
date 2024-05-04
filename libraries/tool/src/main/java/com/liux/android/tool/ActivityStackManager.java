package com.liux.android.tool;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Stack;

public class ActivityStackManager {

    private static volatile ActivityStackManager sInstance;
    public static ActivityStackManager getInstance() {
        if (sInstance == null) {
            synchronized (ActivityStackManager.class) {
                if (sInstance == null) {
                    sInstance = new ActivityStackManager();
                }
            }
        }
        return sInstance;
    }

    public static void install(Application application) {
        application.registerActivityLifecycleCallbacks(
                getInstance().mActivityLifecycleCallbacks
        );
    }

    private final Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
            putActivity(activity);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            updateVisibleActivity(activity);
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            updateVisibleActivity(null);
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            removeActivity(activity);
        }
    };

    private final Stack<WeakReference<Activity>> mActivityStack = new Stack<>();
    private WeakReference<Activity> mVisibleActivity = null;

    /**
     * 取可见的 Activity
     * @return
     */
    @Nullable
    public Activity getVisibleActivity() {
        if (mVisibleActivity != null) {
            return mVisibleActivity.get();
        }
        return null;
    }

    /**
     * 取处于栈顶的 Activity
     * @return
     */
    @Nullable
    public Activity getTopActivity() {
        // 先检查可见引用
        if (mVisibleActivity != null) {
            Activity activity = mVisibleActivity.get();
            if (activity != null) return activity;
        }
        // 然后检查堆栈
        for (Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator(); iterator.hasNext(); ) {
            Activity weakActivity = iterator.next().get();
            if (weakActivity == null) {
                iterator.remove();
                continue;
            }
            return weakActivity;
        }
        return null;
    }

    /**
     * 结束指定的 Activity
     * @param classes
     */
    public void finishActivity(Class<Activity>... classes) {
        for (Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator(); iterator.hasNext(); ) {
            Activity weakActivity = iterator.next().get();
            if (weakActivity == null) {
                iterator.remove();
                continue;
            }
            if (checkActivityInClassArray(classes, weakActivity)) {
                weakActivity.finish();
            }
        }
    }

    /**
     * 结束指定的 Activity
     * @param activities
     */
    public void finishActivity(Activity... activities) {
        for (Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator(); iterator.hasNext(); ) {
            Activity weakActivity = iterator.next().get();
            if (weakActivity == null) {
                iterator.remove();
                continue;
            }
            if (checkActivityInActivityArray(activities, weakActivity)) {
                weakActivity.finish();
            }
        }
    }

    /**
     * 结束所有 Activity
     */
    public void finishActivityAll() {
        for (Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator(); iterator.hasNext(); ) {
            Activity weakActivity = iterator.next().get();
            if (weakActivity == null) {
                iterator.remove();
                continue;
            }
            weakActivity.finish();
        }
    }

    /**
     * 结束所有 Activity 但忽略指定的 Activity
     * @param classes
     */
    public void finishActivityAll(Class<Activity>... classes) {
        for (Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator(); iterator.hasNext(); ) {
            Activity weakActivity = iterator.next().get();
            if (weakActivity == null) {
                iterator.remove();
                continue;
            }
            if (!checkActivityInClassArray(classes, weakActivity)) {
                weakActivity.finish();
            }
        }
    }

    /**
     * 结束所有 Activity 但忽略指定的 Activity
     * @param activities
     */
    public void finishActivityAll(Activity... activities) {
        for (Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator(); iterator.hasNext(); ) {
            Activity weakActivity = iterator.next().get();
            if (weakActivity == null) {
                iterator.remove();
                continue;
            }
            if (!checkActivityInActivityArray(activities, weakActivity)) {
                weakActivity.finish();
            }
        }
    }

    private void updateVisibleActivity(Activity activity) {
        if (activity != null) {
            mVisibleActivity = new WeakReference<>(activity);
        } else {
            mVisibleActivity = null;
        }
    }

    private void putActivity(Activity activity) {
        mActivityStack.push(new WeakReference<>(activity));
    }

    private void removeActivity(Activity activity) {
        for (Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator(); iterator.hasNext();) {
            Activity weakActivity = iterator.next().get();
            if (weakActivity == activity) iterator.remove();
        }
    }

    private boolean checkActivityInClassArray(Class<Activity>[] classes, Activity activity) {
        if (classes == null || activity == null) return false;
        for (Class<Activity> activityClass : classes) {
            if (activity.getClass().equals(activityClass)) return true;
        }
        return false;
    }

    private boolean checkActivityInActivityArray(Activity[] activities, Activity activity) {
        if (activities == null || activity == null) return false;
        for (Activity weakActivity : activities) {
            if (weakActivity == activity) return true;
        }
        return false;
    }
}
