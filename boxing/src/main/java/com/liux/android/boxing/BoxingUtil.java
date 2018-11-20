package com.liux.android.boxing;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import java.io.File;

public class BoxingUtil {

    /**
     * 获取缓存目录
     * @param context
     * @return
     */
    public static File getCacheDir(Context context) {
        context = context.getApplicationContext();
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null || !cacheDir.exists()) cacheDir = context.getCacheDir();

        File cache = new File(cacheDir, "boxing");
        cache.mkdirs();
        cache.mkdir();
        return cache;
    }

    /**
     * 获取缓存目录一个临时文件
     * @param context
     * @return
     */
    public static File getCacheTempFile(Context context) {
        File file = new File(getCacheDir(context), String.valueOf(System.currentTimeMillis()));
        return file.exists() ? getCacheTempFile(context) : file;
    }

    /**
     * 获取注入的 Fragment
     * @param activity
     * @return
     */
    public static BoxingFragment getPermissionFragment(Activity activity) {
        BoxingFragment fragment;
        fragment = (BoxingFragment) activity.getFragmentManager().findFragmentByTag(Boxinger.TAG);
        if (fragment != null) return fragment;

        fragment = new BoxingFragment();
        FragmentManager manager = activity.getFragmentManager();
        manager
                .beginTransaction()
                .add(fragment, Boxinger.TAG)
                .commitAllowingStateLoss();
        manager.executePendingTransactions();
        return fragment;
    }
}
