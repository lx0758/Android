package com.liux.android.downloader.storage;

import android.content.Context;

import java.util.List;

public interface DataStorage {

    void onInit(Context context);

    void onCreate(Record record);

    void onDelete(Record record);

    void onUpdate(Record record);

    void onQuery(Record record);

    List<Record> onQueryAll();
}
