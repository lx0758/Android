package com.liux.android.example.http;

import androidx.annotation.NonNull;

import com.liux.android.http.Http;
import com.liux.android.http.HttpUtil;
import com.liux.android.http.JsonUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
    public void queryWeather(String code, SingleObserver<Object> observer) {
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
    public void queryIP(String ip, SingleObserver<Object> observer) {
        mTestApi.queryIP(
                "ac1bf65a556b39d1973a40688dacd39f",
                ip
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void queryMobile(String mobile, SingleObserver<Object> observer) {
        mTestApi.queryMobile(
                "f38fde6a4395eaebe9fd525a96145925",
                mobile
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void queryExpress(String code, SingleObserver<Object> observer) {
        mTestApi.queryExpress(
                "f13239b8e9de8d5dc90694337d670c39",
                code
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testTimeout(String data, SingleObserver<Object> observer) {
        mTestApi.testTimeout(
                data
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testTimeoutGlobal(String data, SingleObserver<Object> observer) {
        mTestApi.testTimeoutGlobal(
                data
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testGet(int id, String name, SingleObserver<Object> observer) {
        mTestApi.testGet(
                id,
                name
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testPostBody(int id, String name, SingleObserver<Object> observer) {
        RequestBody requestBody = HttpUtil.parseStringBody("Hello");

        TestBean testBean = new TestBean();
        testBean.setId(id);
        testBean.setName(name);

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
                mTestApi.testPostBody(requestBody).onErrorReturnItem(new HashMap<>()),
                mTestApi.testPostBody(testBean).onErrorReturnItem(new HashMap<>()),
                mTestApi.testPostBody(154665465).onErrorReturnItem(new HashMap<>()),
                mTestApi.testPostBody("TEST").onErrorReturnItem(new HashMap<>()),
                mTestApi.testPostBody(map).onErrorReturnItem(new HashMap<>()),
                mTestApi.testPostBody(list).onErrorReturnItem(new HashMap<>()),
                mTestApi.testPostBody(strings).onErrorReturnItem(new HashMap<>())
        ), new Function<Object[], Object>() {
            @Override
            public Object apply(@NonNull Object[] objects) throws Exception {
                return JsonUtil.toBean(JsonUtil.toJson(objects), ArrayList.class);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void testPostForm(int id, String name, SingleObserver<Object> observer) {
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
    public void testPostMultipart(int id, String name, File file, byte[] bytes, InputStream stream, SingleObserver<Object> observer) {
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
