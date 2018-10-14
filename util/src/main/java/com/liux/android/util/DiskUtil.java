package com.liux.android.util;

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
}
