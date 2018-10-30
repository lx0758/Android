package com.liux.android.downloader.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.List;

/**
 * 默认任务信息存储实现
 */
public class DefaultDataStorage implements DataStorage {
    private static final int VERSION_100 = 100;

    private String dbName = "downloader.db";
    private String tableName = "record";

    private SQLiteDatabase sqliteDatabase;
    private DownloaderSQLiteOpenHelper downloaderSQLiteOpenHelper;

    public DefaultDataStorage() {
        this (null, null);
    }

    public DefaultDataStorage(String dbName, String tableName) {
        if (!TextUtils.isEmpty(dbName)) this.dbName = dbName;
        if (!TextUtils.isEmpty(tableName)) this.tableName = tableName;
    }

    @Override
    public void onInit(Context context) {
        downloaderSQLiteOpenHelper = new DownloaderSQLiteOpenHelper(
                context,
                dbName,
                tableName,
                VERSION_100
        );
        sqliteDatabase = downloaderSQLiteOpenHelper.getWritableDatabase();
    }

    @Override
    public Record onCreate(String url, String method, String headers, String dir, String fileName, int status) {
        return null;
    }

    @Override
    public void onDelete(Record record) {

    }

    @Override
    public void onUpdate(Record record) {

    }

    @Override
    public void onQuery(Record record) {

    }

    @Override
    public List<Record> onQueryAll() {
        return null;
    }

    private class DownloaderSQLiteOpenHelper extends SQLiteOpenHelper {

        private String tableName;

        public DownloaderSQLiteOpenHelper(Context context, String dbName, String tableName, int version) {
            super(context, dbName, null, version);
            this.tableName = tableName;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE 'main'.'" + tableName + "' (\n" +
                            "'id' integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                            "'url' text NOT NULL,\n" +
                            "'headers' text,\n" +
                            "'dir' text NOT NULL,\n" +
                            "'fileName' text,\n" +
                            "'etag' text,\n" +
                            "'completed' integer,\n" +
                            "'total' integer,\n" +
                            "'status' integer,\n" +
                            "'createTime' timestamp,\n" +
                            "'updateTime' timestamp\n" +
                            ");"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (oldVersion) {
                case VERSION_100:
                    break;
            }
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (newVersion) {
                case VERSION_100:
                    break;
            }
        }
    }
}
