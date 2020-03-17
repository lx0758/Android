package com.liux.android.example.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liux.android.http.Http;
import com.liux.android.http.HttpUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * Created by Liux on 2018/1/16.
 */

public class TestApiModelImpl implements TestApiModel {

    private static final String TAG = "TestApiModelImpl";

    private TestApi mTestApi;

    public TestApiModelImpl() {
        mTestApi = Http.get().getService(TestApi.class);
    }

    @Override
    public void queryWeather(String code, SingleObserver<JSON> observer) {
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
    public void queryIP(String ip, SingleObserver<JSON> observer) {
        mTestApi.queryIP(
                "ac1bf65a556b39d1973a40688dacd39f",
                ip
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void queryMobile(String mobile, SingleObserver<JSON> observer) {
        mTestApi.queryMobile(
                "f38fde6a4395eaebe9fd525a96145925",
                mobile
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void queryExpress(String code, SingleObserver<JSON> observer) {
        mTestApi.queryExpress(
                "f13239b8e9de8d5dc90694337d670c39",
                code
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testTimeout(String data, SingleObserver<JSON> observer) {
        mTestApi.testTimeout(
                data
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testTimeoutGlobal(String data, SingleObserver<JSON> observer) {
        mTestApi.testTimeoutGlobal(
                data
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testGet(int id, String name, SingleObserver<JSON> observer) {
        mTestApi.testGet(
                id,
                name
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testPostBody(int id, String name, SingleObserver<JSON> observer) {
        RequestBody requestBody = HttpUtil.parseString("Hello");

        TestBean testBean = new TestBean();
        testBean.setId(id);
        testBean.setName(name);

        JSONObject jsonObject = (JSONObject) JSON.toJSON(testBean);

        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");

        List<String> list = new ArrayList<>();
        list.add("list1");
        list.add("list2");
        list.add("list3");

        String[] strings = new String[]{"array1", "array2", "array3"};

        Single.zip(Arrays.asList(
                mTestApi.testPostBody(requestBody).onErrorReturnItem(new JSONObject()),
                mTestApi.testPostBody(testBean).onErrorReturnItem(new JSONObject()),
                mTestApi.testPostBody(jsonObject).onErrorReturnItem(new JSONObject()),
                mTestApi.testPostBody(154665465).onErrorReturnItem(new JSONObject()),
                mTestApi.testPostBody("TEST").onErrorReturnItem(new JSONObject()),
                mTestApi.testPostBody(map).onErrorReturnItem(new JSONObject()),
                mTestApi.testPostBody(list).onErrorReturnItem(new JSONObject()),
                mTestApi.testPostBody(strings).onErrorReturnItem(new JSONObject())
        ), (Function<Object[], JSON>) objects -> new JSONArray(Arrays.asList(objects)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testPostForm(int id, String name, SingleObserver<JSON> observer) {
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
    public void testPostMultipart(int id, String name, File file, byte[] bytes, InputStream stream, SingleObserver<JSON> observer) {
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
