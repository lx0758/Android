package com.liux.android.tool;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Stack;

public class ActivitysManager {

    private static volatile ActivitysManager instance;

    private static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            getInstance().putActivity(activity);
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
            getInstance().removeActivity(activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    public static void install(Application application) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    public static ActivitysManager getInstance() {
        if (instance == null) {
            synchronized (ActivitysManager.class) {
                if (instance == null) {
                    instance = new ActivitysManager();
                }
            }
        }
        return instance;
    }

    private WeakReference<Activity> activityWeakReference = null;
    private Stack<WeakReference<Activity>> activityStack = new Stack<>();

    /**
     * 取处于栈顶的 Activity
     * @return
     */
    public Activity getTopActivity() {
        // 先检查软引用
        if (activityWeakReference != null) {
            Activity activity = activityWeakReference.get();
            if (activity != null) return activity;
        }
        // 在检查堆栈
        for (Iterator<WeakReference<Activity>> iterator = activityStack.iterator(); iterator.hasNext(); ) {
            WeakReference<Activity> weakReference = iterator.next();
            Activity activity = weakReference.get();
            if (activity != null) return activity;
            iterator.remove();
        }
        return null;
    }

    /**
     * 结束指定的 Activity
     * @param classes
     */
    public void finishActivity(Class... classes) {
        for (Iterator<WeakReference<Activity>> iterator = activityStack.iterator(); iterator.hasNext(); ) {
            WeakReference<Activity> weakReference = iterator.next();
            Activity activity = weakReference.get();
            if (activity == null) {
                iterator.remove();
                continue;
            }
            if (checkActivityInClassArray(classes, activity)) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定的 Activity
     * @param activities
     */
    public void finishActivity(Activity... activities) {
        for (Iterator<WeakReference<Activity>> iterator = activityStack.iterator(); iterator.hasNext(); ) {
            WeakReference<Activity> weakReference = iterator.next();
            Activity activity = weakReference.get();
            if (activity == null) {
                iterator.remove();
                continue;
            }
            if (checkActivityInActivityArray(activities, activity)) {
                activity.finish();
            }
        }
    }

    /**
     * 结束所有 Activity
     */
    public void finishActivityAll() {
        for (Iterator<WeakReference<Activity>> iterator = activityStack.iterator(); iterator.hasNext(); ) {
            WeakReference<Activity> weakReference = iterator.next();
            Activity activity = weakReference.get();
            if (activity == null) {
                iterator.remove();
                continue;
            }
            activity.finish();
        }
    }

    /**
     * 结束所有 Activity 但忽略指定的 Activity
     * @param classes
     */
    public void finishActivityAll(Class... classes) {
        for (Iterator<WeakReference<Activity>> iterator = activityStack.iterator(); iterator.hasNext(); ) {
            WeakReference<Activity> weakReference = iterator.next();
            Activity activity = weakReference.get();
            if (activity == null) {
                iterator.remove();
                continue;
            }
            if (!checkActivityInClassArray(classes, activity)) {
                activity.finish();
            }
        }
    }

    /**
     * 结束所有 Activity 但忽略指定的 Activity
     * @param activities
     */
    public void finishActivityAll(Activity... activities) {
        for (Iterator<WeakReference<Activity>> iterator = activityStack.iterator(); iterator.hasNext(); ) {
            WeakReference<Activity> weakReference = iterator.next();
            Activity activity = weakReference.get();
            if (activity == null) {
                iterator.remove();
                continue;
            }
            if (!checkActivityInActivityArray(activities, activity)) {
                activity.finish();
            }
        }
    }

    private void putActivity(Activity activity) {
        activityStack.push(new WeakReference<>(activity));
    }

    private void removeActivity(Activity activity) {
        for (Iterator<WeakReference<Activity>> iterator = activityStack.iterator(); iterator.hasNext(); ) {
            WeakReference<Activity> weakReference = iterator.next();
            Activity tempActivity = weakReference.get();
            if (tempActivity == null || activity == tempActivity) iterator.remove();
        }
    }

    private void updateTopActivity(Activity activity) {
        if (activity != null) {
            activityWeakReference = new WeakReference<>(activity);
        } else {
            activityWeakReference = null;
        }
    }

    private boolean checkActivityInClassArray(Class[] classes, Activity activity) {
        if (classes == null || activity == null) return false;
        for (Class clazz : classes) {
            if (activity.getClass().equals(clazz)) return true;
        }
        return false;
    }

    private boolean checkActivityInActivityArray(Activity[] activities, Activity activity) {
        if (activities == null || activity == null) return false;
        for (Activity tempActivity : activities) {
            if (tempActivity == activity) return true;
        }
        return false;
    }
}
