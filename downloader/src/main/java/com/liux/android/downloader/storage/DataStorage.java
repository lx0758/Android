package com.liux.android.downloader.storage;

import android.content.Context;

import java.util.List;

/**
 * 任务信息存储
 */
public interface DataStorage {

    void onInit(Context context);

    Record onInsert(String url, String method, String headers, String dir, String fileName, int status);

    void onDelete(Record record);

    void onUpdate(Record record);

    void onQuery(Record record);

    List<Record> onQueryAll();
}
