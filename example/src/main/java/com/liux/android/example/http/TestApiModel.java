package com.liux.android.example.http;

import java.io.File;
import java.io.InputStream;

import io.reactivex.rxjava3.core.SingleObserver;

/**
 * Created by Liux on 2018/1/16.
 */

public interface TestApiModel {

    void queryWeather(String code, SingleObserver<Object> observer);

    void queryIP(String ip, SingleObserver<Object> observer);

    void queryMobile(String mobile, SingleObserver<Object> observer);

    void queryExpress(String code, SingleObserver<Object> observer);

    void testTimeout(String data, SingleObserver<Object> observer);

    void testTimeoutGlobal(String data, SingleObserver<Object> observer);

    void testGet(int id, String name, SingleObserver<Object> observer);

    void testPostBody(int id, String name, SingleObserver<Object> observer);

    void testPostForm(int id, String name, SingleObserver<Object> observer);

    void testPostMultipart(int id, String name, File file, byte[] bytes, InputStream stream, SingleObserver<Object> observer);
}
