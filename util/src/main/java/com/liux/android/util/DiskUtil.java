package com.liux.android.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
     * 读文件
     * @param file
     * @return
     */
    public static byte[] readFile(File file) {
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            int length;
            byte[] buffer = new byte[2048];
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception ignore) {}
        }
        return null;
    }

    /**
     * 读流
     * @param in
     * @return
     */
    public static byte[] readStream(InputStream in) {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            int length;
            byte[] buffer = new byte[2048];
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception ignore) {}
        }
        return null;
    }

    /**
     * 写文件
     * @param file
     * @param bytes
     */
    public static boolean writeFile(File file, byte[] bytes) {
        return writeFile(file, new ByteArrayInputStream(bytes));
    }

    /**
     * 写文件
     * @param file
     * @param inputStream
     */
    public static boolean writeFile(File file, InputStream inputStream) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            int length;
            byte[] buffer = new byte[2048];
            while ((length = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (out != null) out.close();
            } catch (Exception ignore) {}
        }
        return false;
    }

    /**
     * 文件拷贝
     * @param source
     * @param target
     */
    public static boolean fileCopy(File source, File target) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            int length;
            byte[] buffer = new byte[2048];
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception ignore) {}
        }
        return false;
    }

    /**
     * 获取磁盘总量
     * @return
     */
    public static long getDiskTotal() {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        return (long) sf.getBlockCount() * (long) sf.getBlockSize();
    }

    /**
     * 获取磁盘剩余空间
     * @return
     */
    public static long getDiskAvailable() {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        return (long) sf.getAvailableBlocks() * (long) sf.getBlockSize();
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
