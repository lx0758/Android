package com.liux.android.downloader.storage;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 下载数据存储
 */
public interface FileStorage {

    void onInit(Context context, File rootDir);

    boolean isExist(File dir, String fileName);

    RandomAccessFile onOpen(File dir, String fileName) throws IOException;
}
