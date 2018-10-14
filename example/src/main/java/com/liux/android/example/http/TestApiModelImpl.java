package com.liux.android.example.http;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.liux.android.http.Http;
import com.liux.android.http.HttpUtil;

import java.io.File;
import java.io.InputStream;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liux on 2018/1/16.
 */

public class TestApiModelImpl implements TestApiModel {

    private static final String TAG = "TestApiModelImpl";

    private Context mContext;
    private TestApi mTestApi;

    public TestApiModelImpl(Context context) {
        mContext = context;
        mTestApi = Http.get().getService(TestApi.class);
    }

    @Override
    public void queryWeather(String code, Observer<JSONObject> observer) {
        mTestApi.queryWeather(
                "bd15e11291d68ff100ca0be6ad32b15d",
                code,
                "7"
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void queryIP(String ip, Observer<JSONObject> observer) {
        mTestApi.queryIP(
                "ac1bf65a556b39d1973a40688dacd39f",
                ip
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void queryMobile(String mobile, Observer<JSONObject> observer) {
        mTestApi.queryMobile(
                "f38fde6a4395eaebe9fd525a96145925",
                mobile
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void queryExpress(String code, Observer<JSONObject> observer) {
        mTestApi.queryExpress(
                "f13239b8e9de8d5dc90694337d670c39",
                code
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testTimeout(String data, Observer<JSONObject> observer) {
        mTestApi.testTimeout(
                data
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testTimeoutGlobal(String data, Observer<JSONObject> observer) {
        mTestApi.testTimeoutGlobal(
                data
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testGet(int id, String name, Observer<JSONObject> observer) {
        mTestApi.testGet(
                id,
                name
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testPostBody(int id, String name, Observer<JSONObject> observer) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("name", name);
        mTestApi.testPostBody(
                HttpUtil.parseJson(jsonObject.toJSONString())
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testPostForm(int id, String name, Observer<JSONObject> observer) {
        mTestApi.testPostForm(
                id,
                name,
                id,
                name
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testPostMultipart(int id, String name, File file, byte[] bytes, InputStream stream, Observer<JSONObject> observer) {
        mTestApi.testPostMultipart(
                id,
                name,
                id,
                name,
                HttpUtil.parseFilePart("file", file),
                HttpUtil.parseBytePart("bytes", null, bytes),
                HttpUtil.parseInputStreamPart("stream", null, stream)
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
