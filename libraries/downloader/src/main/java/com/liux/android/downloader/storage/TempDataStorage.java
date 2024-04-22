package com.liux.android.downloader.storage;

import android.content.Context;

import java.util.List;

public class TempDataStorage implements DataStorage {

    private static DataStorage dataStorage;

    public static DataStorage get() {
        if (dataStorage == null) dataStorage = new TempDataStorage();
        return dataStorage;
    }

    @Override
    public void onInit(Context context) {

    }

    @Override
    public Record onInsert(String url, String method, String headers, String dir, String fileName, int status) {
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
}
