package com.liux.android.downloader.storage;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 默认下载数据存储实现
 */
public class DefaultFileStorage implements FileStorage {

    private File rootDir;

    @Override
    public void onInit(Context context, File rootDir) {
        this.rootDir = rootDir;
        rootDir.mkdirs();
    }

    @Override
    public boolean isExist(String dir, String fileName) {
        File file = getFile(dir, fileName);
        return file.exists() && file.isFile();
    }

    @Override
    public RandomAccessFile onOpen(String dir, String fileName) throws IOException {
        File file = getFile(dir, fileName);
        return new RandomAccessFile(file, "rw");
    }

    @Override
    public void onDelete(String dir, String fileName) {
        File file = getFile(dir, fileName);
        file.delete();
    }

    private File getFile(String dir, String fileName) {
        File dirFile = (dir == null) ? rootDir : new File(dir);
        dirFile.mkdirs();

        return new File(dirFile.getAbsolutePath() + File.separator + fileName);
    }
}
