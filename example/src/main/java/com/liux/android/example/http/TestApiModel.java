package com.liux.android.example.http;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.InputStream;

import io.reactivex.SingleObserver;

/**
 * Created by Liux on 2018/1/16.
 */

public interface TestApiModel {

    void queryWeather(String code, SingleObserver<JSON> observer);

    void queryIP(String ip, SingleObserver<JSON> observer);

    void queryMobile(String mobile, SingleObserver<JSON> observer);

    void queryExpress(String code, SingleObserver<JSON> observer);

    void testTimeout(String data, SingleObserver<JSON> observer);

    void testTimeoutGlobal(String data, SingleObserver<JSON> observer);

    void testGet(int id, String name, SingleObserver<JSON> observer);

    void testPostBody(int id, String name, SingleObserver<JSON> observer);

    void testPostForm(int id, String name, SingleObserver<JSON> observer);

    void testPostMultipart(int id, String name, File file, byte[] bytes, InputStream stream, SingleObserver<JSON> observer);
}
