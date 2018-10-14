package com.liux.android.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Liux on 2016/5/16.
 * E-Mail:lx0758@qq.com
 */
public class AssetsTool {
    private static volatile AssetsTool mInstance;
    public static AssetsTool getInstance() {
        if (mInstance == null) throw new NullPointerException("AssetsTool has not been initialized");
        return mInstance;
    }
    public static void initialize(Context context){
        if (mInstance != null) return;
        synchronized(AssetsTool.class) {
            if (mInstance != null) return;
            mInstance = new AssetsTool(context);
        }
    }

    private static final String TAG = "AssetsTool";

    private Context mContext;
    private Map<String, Object> mObjects = new HashMap<String, Object>();

    private AssetsTool(Context context){
        mContext = context.getApplicationContext();
    }

    public synchronized Uri getUri(String name) {
        if (mContext == null) {return null;}
        if (mObjects.get("Uri_"+name) != null) return (Uri) mObjects.get("Uri_"+name);

        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = mContext.getAssets().open(name);
            out = mContext.openFileOutput(name, Context.MODE_PRIVATE);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Uri uri = Uri.fromFile(mContext.getFileStreamPath(name));
        mObjects.put("Uri_"+name, uri);
        return uri;
    }

    public synchronized File getFile(String name) {
        if (mContext == null) {return null;}
        if (mObjects.get("File_"+name) != null) return (File) mObjects.get("File_"+name);

        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = mContext.getAssets().open(name);
            out = mContext.openFileOutput(name, Context.MODE_PRIVATE);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file = mContext.getFileStreamPath(name);
        mObjects.put("File_"+name, file);
        return file;
    }

    public synchronized SQLiteDatabase getSQLiteDatabase(String name) {
        if (mContext == null) {return null;}
        if (mObjects.get("DB_"+name) != null) return (SQLiteDatabase) mObjects.get("DB_"+name);

        File file = mContext.getDatabasePath(name);
        createDirs(file);

        InputStream in = null;
        OutputStream out = null;
        try {
            in = mContext.getAssets().open(name);
            out = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SQLiteDatabase sql = SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.OPEN_READONLY);
        mObjects.put("DB_"+name, sql);
        return sql;
    }

    private void createDirs(File file) {
        File dir = new File(file.getParent());
        dir.mkdirs();
    }
}