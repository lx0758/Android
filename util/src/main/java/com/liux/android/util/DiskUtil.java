package com.liux.android.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liux on 2017/11/29.
 */

public class DiskUtil {

    /**
     * 获取目录下文件大小
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null || !dir.exists()) return 0;
        if (dir.isFile()) return dir.length();
        return getDirSizeChild(dir);
    }

    /**
     * 递归遍历文件(文件夹在文件后)
     * @param dir
     * @param hasDir
     * @return
     */
    public static List<File> listAllFile(File dir, boolean hasDir, boolean dirPriority) {
        List<File> files = new ArrayList<>(100);
        listAllFile(files, dir, hasDir, dirPriority);
        if (hasDir) files.add(dir);
        return null;
    }

    /**
     * 扩展存储是否已经挂载
     * @return
     */
    public static boolean isExternalMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 递归清空目录
     * @param dir
     */
    public static void deleteAllFile(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                deleteAllFile(file);
            }
            file.delete();
        }
    }

    /**
     * 获取缓存目录
     * @param context
     * @return
     */
    public static File getCacheDir(Context context) {
        context = context.getApplicationContext();
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null || !cacheDir.exists()) cacheDir = context.getCacheDir();
        return cacheDir;
    }

    /**
     * 获取缓存临时文件
     * @param context
     * @return
     */
    public static File getCacheTempFile(Context context) {
        return getCacheTempFile(context, null);
    }

    /**
     * 获取缓存临时文件
     * @param context
     * @return
     */
    public static File getCacheTempFile(Context context, String suffix) {
        String fileName = String.valueOf(System.currentTimeMillis());
        if (!TextUtils.isEmpty(suffix)) fileName += ("." + suffix);
        File file = new File(getCacheDir(context), fileName);
        return file.exists() ? getCacheTempFile(context, suffix) : file;
    }

    /**
     * 获取私有目录
     * @param context
     * @return
     */
    public static File getFileDir(Context context) {
        context = context.getApplicationContext();
        File filesDir = context.getExternalFilesDir(null);
        if (filesDir == null || !filesDir.exists()) filesDir = context.getFilesDir();
        return filesDir;
    }

    /**
     * 获取私有临时文件
     * @param context
     * @return
     */
    public static File getTempFile(Context context) {
        return getTempFile(context, null);
    }

    /**
     * 获取私有临时文件
     * @param context
     * @return
     */
    public static File getTempFile(Context context, String suffix) {
        String fileName = String.valueOf(System.currentTimeMillis());
        if (!TextUtils.isEmpty(suffix)) fileName += ("." + suffix);
        File file = new File(getFileDir(context), fileName);
        return file.exists() ? getTempFile(context, suffix) : file;
    }

    /**
     * 递归获取文件夹大小
     * @param dir
     */
    private static long getDirSizeChild(File dir) {
        long size = 0L;
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                size = size + getDirSize(file);
            } else {
                size = size + file.length();
            }
        }
        return size;
    }

    /**
     * 递归遍历文件
     * @param files
     * @param dir
     * @param hasDir
     */
    private static void listAllFile(List<File> files, File dir, boolean hasDir, boolean dirPriority) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                if (hasDir && dirPriority) {
                    files.add(file);
                }
                listAllFile(files, file, hasDir, dirPriority);
                if (hasDir && !dirPriority) {
                    files.add(file);
                }
            }
        }
    }
}
