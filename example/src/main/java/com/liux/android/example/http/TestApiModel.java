package com.liux.android.example.http;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.InputStream;

import io.reactivex.Observer;

/**
 * Created by Liux on 2018/1/16.
 */

public interface TestApiModel {

    void queryWeather(String code, Observer<JSONObject> observer);

    void queryIP(String ip, Observer<JSONObject> observer);

    void queryMobile(String mobile, Observer<JSONObject> observer);

    void queryExpress(String code, Observer<JSONObject> observer);

    void testTimeout(String data, Observer<JSONObject> observer);

    void testTimeoutGlobal(String data, Observer<JSONObject> observer);

    void testGet(int id, String name, Observer<JSONObject> observer);

    void testPostBody(int id, String name, Observer<JSONObject> observer);

    void testPostForm(int id, String name, Observer<JSONObject> observer);

    void testPostMultipart(int id, String name, File file, byte[] bytes, InputStream stream, Observer<JSONObject> observer);
}
