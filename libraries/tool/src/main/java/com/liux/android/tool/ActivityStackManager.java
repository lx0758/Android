package com.liux.android.tool;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Stack;

public class ActivityStackManager {

    private static volatile ActivityStackManager sInstance;

    private static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            getInstance().putActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            getInstance().updateTopActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            getInstance().updateTopActivity(null);
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            getInstance().removeActivity(activity);
        }
    };

    public static void install(Application application) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

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

    private WeakReference<Activity> mTopActivity = null;
    private Stack<WeakReference<Activity>> mActivityStack = new Stack<>();

    /**
     * 取处于栈顶的 Activity
     * @return
     */
    public Activity getTopActivity() {
        // 先检查软引用
        if (mTopActivity != null) {
            Activity activity = mTopActivity.get();
            if (activity != null) return activity;
        }
        // 再检查堆栈
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

    private void putActivity(Activity activity) {
        mActivityStack.push(new WeakReference<>(activity));
    }

    private void removeActivity(Activity activity) {
        for (Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator(); iterator.hasNext();) {
            Activity weakActivity = iterator.next().get();
            if (weakActivity == activity) iterator.remove();
        }
    }

    private void updateTopActivity(Activity activity) {
        if (activity != null) {
            mTopActivity = new WeakReference<>(activity);
        } else {
            mTopActivity = null;
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
