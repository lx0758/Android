package com.liux.android.downloader.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.LinkedList;
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
    public Record onInsert(String url, String method, String headers, String dir, String fileName, int status) {
        long time = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put("url", url);
        contentValues.put("method", method);
        contentValues.put("headers", headers);
        contentValues.put("dir", dir);
        contentValues.put("fileName", fileName);
        contentValues.put("status", status);
        contentValues.put("createTime", time);
        contentValues.put("updateTime", time);
        sqliteDatabase.insert(tableName, null, contentValues);
        Cursor cursor = sqliteDatabase.query(
                tableName,
                new String[]{"last_insert_rowid()"},
                null,
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        long id = cursor.getLong(0);
        cursor.close();
        return Record.create(id, url, method, headers, dir, fileName, status, time);
    }

    @Override
    public void onDelete(Record record) {
        sqliteDatabase.delete(
                tableName,
                "id=?",
                new String[]{String.valueOf(record.getId())}
        );
    }

    @Override
    public void onUpdate(Record record) {
        ContentValues contentValues = new ContentValues();
        //contentValues.put("id", record.getId());
        contentValues.put("url", record.getUrl());
        contentValues.put("method", record.getMethod());
        contentValues.put("headers", record.getHeaders());
        contentValues.put("dir", record.getDir());
        contentValues.put("fileName", record.getFileName());
        contentValues.put("fileNameFinal", record.getFileNameFinal());
        contentValues.put("etag", record.getEtag());
        contentValues.put("completed", record.getCompleted());
        contentValues.put("total", record.getTotal());
        contentValues.put("status", record.getStatus());
        contentValues.put("createTime", record.getCreateTime());
        contentValues.put("updateTime", record.getUpdateTime());
        sqliteDatabase.update(
                tableName,
                contentValues,
                "id=?",
                new String[]{String.valueOf(record.getId())}
        );
    }

    @Override
    public void onQuery(Record record) {
        Cursor cursor = sqliteDatabase.query(
                tableName,
                new String[]{"id", "url", "method", "headers", "dir", "fileName", "fileNameFinal", "etag", "completed", "total", "status", "createTime", "updateTime"},
                "id=?",
                new String[]{String.valueOf(record.getId())},
                null,
                null,
                "createTime ASC",
                null
        );
        if (cursor.moveToFirst()) {
            cursor2record(cursor, record);
        }
        cursor.close();
    }

    @Override
    public List<Record> onQueryAll() {
        List<Record> records = new LinkedList<>();
        Cursor cursor = sqliteDatabase.query(
                tableName,
                new String[]{"id", "url", "method", "headers", "dir", "fileName", "fileNameFinal", "etag", "completed", "total", "status", "createTime", "updateTime"},
                null,
                null,
                null,
                null,
                "createTime ASC",
                null
        );
        if (cursor.moveToFirst()) {
            do {
                Record record = new Record();
                cursor2record(cursor, record);
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return records;
    }

    /**
     * 将游标转换为记录实体
     * @param cursor
     * @param record
     */
    private void cursor2record(Cursor cursor, Record record) {
        record.setId(cursor.getLong(0));
        record.setUrl(cursor.getString(1));
        record.setMethod(cursor.getString(2));
        record.setHeaders(cursor.getString(3));
        record.setDir(cursor.getString(4));
        record.setFileName(cursor.getString(5));
        record.setFileNameFinal(cursor.getString(6));
        record.setEtag(cursor.getString(7));
        record.setCompleted(cursor.getLong(8));
        record.setTotal(cursor.getLong(9));
        record.setStatus(cursor.getInt(10));
        record.setCreateTime(cursor.getLong(11));
        record.setUpdateTime(cursor.getLong(12));
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
                            "'id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                            "'url' TEXT NOT NULL,\n" +
                            "'method' TEXT,\n" +
                            "'headers' TEXT,\n" +
                            "'dir' TEXT NOT NULL,\n" +
                            "'fileName' TEXT,\n" +
                            "'fileNameFinal' TEXT,\n" +
                            "'etag' TEXT,\n" +
                            "'completed' INTEGER,\n" +
                            "'total' INTEGER,\n" +
                            "'status' INTEGER,\n" +
                            "'createTime' TIMESTAMP,\n" +
                            "'updateTime' TIMESTAMP\n" +
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
