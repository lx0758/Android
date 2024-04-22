package com.liux.android.tool;

import android.os.FileObserver;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 能够监控子目录的文件监听器
 */
public abstract class RecursiveFileObserver extends FileObserver {
    private final static String TAG = "RecursiveFileObserver";

    private int mMask;
    private String mPath;

    private List<ChildFileObserver> mObservers = new ArrayList<>();

    public RecursiveFileObserver(String path) {
        this(path, ALL_EVENTS);
    }

    public RecursiveFileObserver(String path, int mask) {
        super(path, mask);
        mPath = path;
        mMask = mask;
    }

    @Override
    public void startWatching() {
        mObservers.add(new ChildFileObserver(this, mObservers, mPath, mMask));
        for (ChildFileObserver observer : mObservers) {
            observer.startWatching();
        }
    }

    @Override
    public void stopWatching() {
        for (ChildFileObserver observer : mObservers) {
            observer.stopWatching();
        }
        mObservers.clear();
    }

    public static class ChildFileObserver extends FileObserver {

        private int mMask;
        private String mPath;
        private RecursiveFileObserver mRecursiveFileObserver;
        private List<ChildFileObserver> mChildFileObservers;

        public ChildFileObserver(RecursiveFileObserver recursiveFileObserver, List<ChildFileObserver> childFileObservers, String path, int mask) {
            super(path, mask);
            mPath = path;
            mMask = mask;
            mRecursiveFileObserver = recursiveFileObserver;
            mChildFileObservers = childFileObservers;

            initChildDir();
        }

        private void initChildDir() {
            File[] dirs = new File(mPath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });

            if (dirs != null) {
                for (File dir : dirs) {
                    mChildFileObservers.add(new ChildFileObserver(mRecursiveFileObserver, mChildFileObservers, dir.getPath(), mMask));
                }
            }
        }

        @Override
        public void onEvent(int event, String path) {
            String newPath = mPath + File.separator + path;
            if ((event & ALL_EVENTS) == CREATE) {
                ChildFileObserver observer = new ChildFileObserver(mRecursiveFileObserver, mChildFileObservers, newPath, mMask);
                observer.startWatching();
                mChildFileObservers.add(observer);
            }
            mRecursiveFileObserver.onEvent(event, newPath);
        }
    }
}