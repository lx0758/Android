package com.liux.android.mediaer;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.liux.android.mediaer.inject.InjectFragment;

import java.io.File;

public class MediaerUtil {

    /**
     * 获取缓存目录
     * @param context
     * @return
     */
    public static File getCacheDir(Context context) {
        context = context.getApplicationContext();
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null || !cacheDir.exists()) cacheDir = context.getCacheDir();

        File cache = new File(cacheDir, "mediaer");
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
        return getCacheTempFile(context, null);
    }

    /**
     * 获取缓存目录一个临时文件
     * @param context
     * @param suffix
     * @return
     */
    public static File getCacheTempFile(Context context, String suffix) {
        String fileName = String.valueOf(System.currentTimeMillis());
        if (!TextUtils.isEmpty(suffix)) fileName += ("." + suffix);
        File file = new File(getCacheDir(context), fileName);
        return file.exists() ? getCacheTempFile(context, suffix) : file;
    }

    /**
     * 获取注入的 Fragment
     * @param fragmentActivity
     * @return
     */
    public static InjectFragment getInjectFragment(FragmentActivity fragmentActivity) {
        InjectFragment fragment = (InjectFragment) fragmentActivity.getSupportFragmentManager().findFragmentByTag(Mediaer.TAG);
        if (fragment != null) return fragment;

        fragment = new InjectFragment();
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        manager
                .beginTransaction()
                .add(fragment, Mediaer.TAG)
                .commitAllowingStateLoss();
        manager.executePendingTransactions();
        return fragment;
    }



    /**
     * 获取文件带 file:// 或 content:// 的 Uri
     * @param context
     * @param file
     * @return
     */
    public static Uri getProviderUri(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return MediaerFileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".MediaerFileProvider",
                    file
            );
        }
        return Uri.fromFile(file);
    }
}
