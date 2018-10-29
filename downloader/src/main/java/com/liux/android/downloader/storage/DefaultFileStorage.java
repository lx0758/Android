package com.liux.android.downloader.storage;

import android.content.Context;

import java.io.File;
import java.io.OutputStream;

public class DefaultFileStorage implements FileStorage {

    private File rootDir;

    @Override
    public void onInit(Context context, File rootDir) {
        this.rootDir = rootDir;
        rootDir.mkdirs();
    }

    @Override
    public OutputStream onOpen(File dir, String fileName) {
        if (dir == null) dir = rootDir;
        dir.mkdirs();

        return null;
    }
}
