package com.liux.android.tool.autovalue;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.lang.reflect.Field;

public class AutoValueManager {

    private static Application.ActivityLifecycleCallbacks activitySupportCallbacks = new Application.ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(@NonNull Activity activity, Bundle bundle) {
            AutoValueManager.injectValue(activity, activity.getIntent().getExtras(), bundle);
            if (activity instanceof FragmentActivity) {
                ((FragmentActivity) activity).getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
                ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
            }
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
            AutoValueManager.saveValues(activity, bundle);
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    };

    private static FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentPreCreated(@NonNull FragmentManager fm, @NonNull Fragment f, Bundle savedInstanceState) {
            super.onFragmentPreCreated(fm, f, savedInstanceState);
            AutoValueManager.injectValue(f, f.getArguments(), savedInstanceState);
        }

        @Override
        public void onFragmentSaveInstanceState(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Bundle outState) {
            super.onFragmentSaveInstanceState(fm, f, outState);
            AutoValueManager.saveValues(f, outState);
        }
    };

    public static void install(Application application) {
        application.unregisterActivityLifecycleCallbacks(activitySupportCallbacks);
        application.registerActivityLifecycleCallbacks(activitySupportCallbacks);
    }

    /***
     * 获取指定target的所有字段,如果有@AutoParam注解的,那么自动为其取值
     * @param target 需要注入的对象
     * @param froms 提供内容的Bundle列表
     */
    private static void injectValue(Object target, Bundle... froms) {
        try {
            Field[] fields = target.getClass().getDeclaredFields();
            for (Field field : fields) {
                AutoValue parameter = field.getAnnotation(AutoValue.class);
                if (parameter != null) {
                    field.setAccessible(true);
                    String key = parameter.value();
                    Object value = getValueFromBundles(key, field.get(target), froms);
                    field.set(target, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * 将指定对象的所有包含@AutoParam注解的字段,进行存储到指定Bundle容器
     * @param target 需要解析的对象
     * @param outState 存储容器
     */
    private static void saveValues(Object target, Bundle outState) {
        try {
            Field[] fields = target.getClass().getDeclaredFields();
            for (Field field : fields) {
                AutoValue parameter = field.getAnnotation(AutoValue.class);
                if (parameter != null) {
                    field.setAccessible(true);
                    String key = parameter.value();
                    Object value = field.get(target);
                    BundleUtil.putObjectToBundle(key, value, outState);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * 按照列表顺序依次查找key
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> T getValueFromBundles(String key, T defaultValue, Bundle... froms) {
        for (Bundle from : froms) {
            if (from != null && from.containsKey(key)) {
                return (T) from.get(key);
            }
        }

        return defaultValue;
    }
}
