package com.liux.android.downloader.storage;

import android.content.Context;

import java.io.File;
import java.io.OutputStream;

public interface FileStorage {

    void onInit(Context context, File rootDir);

    boolean isExist(File dir, String fileName);

    OutputStream onOpen(File dir, String fileName);
}
