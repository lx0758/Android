package com.liux.android.rx.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import io.reactivex.subjects.BehaviorSubject;

/**
 * 2018/2/13
 * By Liux
 * lx0758@qq.com
 */

public class LifecyleProviderManager {

    private static Map<Activity, LifecycleProviderImpl<ActivityEvent>> activityLifecycleProviderWeakHashMap = Collections.synchronizedMap(new WeakHashMap<Activity, LifecycleProviderImpl<ActivityEvent>>());
//    private static Map<android.app.Fragment, LifecycleProviderImpl<FragmentEvent>> frgamentLifecycleProviderWeakHashMap = Collections.synchronizedMap(new WeakHashMap<android.app.Fragment, LifecycleProviderImpl<FragmentEvent>>());
    private static Map<androidx.fragment.app.Fragment, LifecycleProviderImpl<FragmentEvent>> supportFrgamentLifecycleProviderWeakHashMap = Collections.synchronizedMap(new WeakHashMap<androidx.fragment.app.Fragment, LifecycleProviderImpl<FragmentEvent>>());

    private static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            getBehaviorSubject(activity).onNext(ActivityEvent.CREATE);
            if (activity instanceof FragmentActivity) {
                installSupportFragment(((FragmentActivity) activity).getSupportFragmentManager());
            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    installFragment(activity.getFragmentManager());
//                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            getBehaviorSubject(activity).onNext(ActivityEvent.START);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            getBehaviorSubject(activity).onNext(ActivityEvent.RESUME);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            getBehaviorSubject(activity).onNext(ActivityEvent.PAUSE);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            getBehaviorSubject(activity).onNext(ActivityEvent.STOP);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            getBehaviorSubject(activity).onNext(ActivityEvent.DESTROY);
        }

        private BehaviorSubject<ActivityEvent> getBehaviorSubject(Activity activity) {
            return LifecyleProviderManager.getLifecycleProvider(activity).getSubject();
        }
    };

//    private static android.app.FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new android.app.FragmentManager.FragmentLifecycleCallbacks() {
//        @Override
//        public void onFragmentAttached(android.app.FragmentManager fm, android.app.Fragment f, Context context) {
//            super.onFragmentAttached(fm, f, context);
//            getBehaviorSubject(f).onNext(FragmentEvent.ATTACH);
//        }
//
//        @Override
//        public void onFragmentCreated(android.app.FragmentManager fm, android.app.Fragment f, Bundle savedInstanceState) {
//            super.onFragmentCreated(fm, f, savedInstanceState);
//            getBehaviorSubject(f).onNext(FragmentEvent.CREATE);
//        }
//
//        @Override
//        public void onFragmentViewCreated(android.app.FragmentManager fm, android.app.Fragment f, View v, Bundle savedInstanceState) {
//            super.onFragmentViewCreated(fm, f, v, savedInstanceState);
//            getBehaviorSubject(f).onNext(FragmentEvent.CREATE_VIEW);
//        }
//
//        @Override
//        public void onFragmentStarted(android.app.FragmentManager fm, android.app.Fragment f) {
//            super.onFragmentStarted(fm, f);
//            getBehaviorSubject(f).onNext(FragmentEvent.START);
//        }
//
//        @Override
//        public void onFragmentResumed(android.app.FragmentManager fm, android.app.Fragment f) {
//            super.onFragmentResumed(fm, f);
//            getBehaviorSubject(f).onNext(FragmentEvent.RESUME);
//        }
//
//        @Override
//        public void onFragmentPaused(android.app.FragmentManager fm, android.app.Fragment f) {
//            super.onFragmentPaused(fm, f);
//            getBehaviorSubject(f).onNext(FragmentEvent.PAUSE);
//        }
//
//        @Override
//        public void onFragmentStopped(android.app.FragmentManager fm, android.app.Fragment f) {
//            super.onFragmentStopped(fm, f);
//            getBehaviorSubject(f).onNext(FragmentEvent.STOP);
//        }
//
//        @Override
//        public void onFragmentViewDestroyed(android.app.FragmentManager fm, android.app.Fragment f) {
//            super.onFragmentViewDestroyed(fm, f);
//            getBehaviorSubject(f).onNext(FragmentEvent.DESTROY_VIEW);
//        }
//
//        @Override
//        public void onFragmentDestroyed(android.app.FragmentManager fm, android.app.Fragment f) {
//            super.onFragmentDestroyed(fm, f);
//            getBehaviorSubject(f).onNext(FragmentEvent.DESTROY);
//        }
//
//        @Override
//        public void onFragmentDetached(android.app.FragmentManager fm, android.app.Fragment f) {
//            super.onFragmentDetached(fm, f);
//            getBehaviorSubject(f).onNext(FragmentEvent.DETACH);
//        }
//
//        private BehaviorSubject<FragmentEvent> getBehaviorSubject(android.app.Fragment fragment) {
//            return LifecyleProviderManager.getLifecycleProvider(fragment).getSubject();
//        }
//    };

    private static androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks supportFragmentLifecycleCallbacks = new androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentAttached(androidx.fragment.app.FragmentManager fm, androidx.fragment.app.Fragment f, Context context) {
            super.onFragmentAttached(fm, f, context);
            getBehaviorSubject(f).onNext(FragmentEvent.ATTACH);
        }

        @Override
        public void onFragmentCreated(androidx.fragment.app.FragmentManager fm, androidx.fragment.app.Fragment f, Bundle savedInstanceState) {
            super.onFragmentCreated(fm, f, savedInstanceState);
            getBehaviorSubject(f).onNext(FragmentEvent.CREATE);
        }

        @Override
        public void onFragmentViewCreated(androidx.fragment.app.FragmentManager fm, androidx.fragment.app.Fragment f, View v, Bundle savedInstanceState) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState);
            getBehaviorSubject(f).onNext(FragmentEvent.CREATE_VIEW);
        }

        @Override
        public void onFragmentStarted(androidx.fragment.app.FragmentManager fm, androidx.fragment.app.Fragment f) {
            super.onFragmentStarted(fm, f);
            getBehaviorSubject(f).onNext(FragmentEvent.START);
        }

        @Override
        public void onFragmentResumed(androidx.fragment.app.FragmentManager fm, androidx.fragment.app.Fragment f) {
            super.onFragmentResumed(fm, f);
            getBehaviorSubject(f).onNext(FragmentEvent.RESUME);
        }

        @Override
        public void onFragmentPaused(androidx.fragment.app.FragmentManager fm, androidx.fragment.app.Fragment f) {
            super.onFragmentPaused(fm, f);
            getBehaviorSubject(f).onNext(FragmentEvent.PAUSE);
        }

        @Override
        public void onFragmentStopped(androidx.fragment.app.FragmentManager fm, androidx.fragment.app.Fragment f) {
            super.onFragmentStopped(fm, f);
            getBehaviorSubject(f).onNext(FragmentEvent.STOP);
        }

        @Override
        public void onFragmentViewDestroyed(androidx.fragment.app.FragmentManager fm, androidx.fragment.app.Fragment f) {
            super.onFragmentViewDestroyed(fm, f);
            getBehaviorSubject(f).onNext(FragmentEvent.DESTROY_VIEW);
        }

        @Override
        public void onFragmentDestroyed(androidx.fragment.app.FragmentManager fm, androidx.fragment.app.Fragment f) {
            super.onFragmentDestroyed(fm, f);
            getBehaviorSubject(f).onNext(FragmentEvent.DESTROY);
        }

        @Override
        public void onFragmentDetached(androidx.fragment.app.FragmentManager fm, androidx.fragment.app.Fragment f) {
            super.onFragmentDetached(fm, f);
            getBehaviorSubject(f).onNext(FragmentEvent.DETACH);
        }

        private BehaviorSubject<FragmentEvent> getBehaviorSubject(androidx.fragment.app.Fragment fragment) {
            return LifecyleProviderManager.getLifecycleProvider(fragment).getSubject();
        }
    };

    @SuppressWarnings({"unchecked", "SynchronizationOnLocalVariableOrMethodParameter"})
    public static LifecycleProviderImpl<ActivityEvent> getLifecycleProvider(Activity activity) {
        synchronized (activity) {
            LifecycleProviderImpl<ActivityEvent> lifecycleProvider = activityLifecycleProviderWeakHashMap.get(activity);
            if (lifecycleProvider == null) {
                BehaviorSubject<ActivityEvent> subject = BehaviorSubject.create();
                lifecycleProvider = new LifecycleProviderImpl<ActivityEvent>(subject) {
                    @NonNull
                    @Override
                    public <T> LifecycleTransformer<T> bindToLifecycle() {
                        return RxLifecycleAndroid.bindActivity(getSubject());
                    }
                };
                activityLifecycleProviderWeakHashMap.put(activity, lifecycleProvider);
            }
            return lifecycleProvider;
        }
    }

//    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter", "unchecked"})
//    public static LifecycleProviderImpl<FragmentEvent> getLifecycleProvider(android.app.Fragment fragment) {
//        synchronized (fragment) {
//            LifecycleProviderImpl<FragmentEvent> lifecycleProvider = frgamentLifecycleProviderWeakHashMap.get(fragment);
//            if (lifecycleProvider == null) {
//                BehaviorSubject<FragmentEvent> subject = BehaviorSubject.create();
//                lifecycleProvider = new LifecycleProviderImpl<FragmentEvent>(subject) {
//                    @NonNull
//                    @Override
//                    public <T> LifecycleTransformer<T> bindToLifecycle() {
//                        return RxLifecycleAndroid.bindFragment(getSubject());
//                    }
//                };
//                frgamentLifecycleProviderWeakHashMap.put(fragment, lifecycleProvider);
//            }
//            return lifecycleProvider;
//        }
//    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter", "unchecked"})
    public static LifecycleProviderImpl<FragmentEvent> getLifecycleProvider(androidx.fragment.app.Fragment fragment) {
        synchronized (fragment) {
            LifecycleProviderImpl<FragmentEvent> lifecycleProvider = supportFrgamentLifecycleProviderWeakHashMap.get(fragment);
            if (lifecycleProvider == null) {
                BehaviorSubject<FragmentEvent> subject = BehaviorSubject.create();
                lifecycleProvider = new LifecycleProviderImpl<FragmentEvent>(subject) {
                    @NonNull
                    @Override
                    public <T> LifecycleTransformer<T> bindToLifecycle() {
                        return RxLifecycleAndroid.bindFragment(getSubject());
                    }
                };
                supportFrgamentLifecycleProviderWeakHashMap.put(fragment, lifecycleProvider);
            }
            return lifecycleProvider;
        }
    }

    public static void install(Application application) {
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private static void installFragment(android.app.FragmentManager fragmentManager) {
//        fragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
//        fragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
//    }

    private static void installSupportFragment(androidx.fragment.app.FragmentManager supportFragmentManager) {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(supportFragmentLifecycleCallbacks);
        supportFragmentManager.registerFragmentLifecycleCallbacks(supportFragmentLifecycleCallbacks, true);
    }
}
