package com.liux.android.http.cookie;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class DefaultCookieJar implements CookieJar {

    // 用来存储 "单次有效" 的 Cookie
    private final CookieJarStore mPersistentCookieJarStore = new MemoryCookieJarStore();
    // 用来存储本地持久有效的 Cookie
    private final CookieJarStore mCookieJarStore;

    public DefaultCookieJar() {
        this(new MemoryCookieJarStore());
    }

    public DefaultCookieJar(CookieJarStore cookieJarStore) {
        mCookieJarStore = cookieJarStore;
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        List<Cookie> persistentCookies = new ArrayList<>();
        List<Cookie> storeCookies = new ArrayList<>();

        for (Cookie cookie : cookies) {
            if (cookie.persistent()) {
                persistentCookies.add(cookie);
            } else {
                storeCookies.add(cookie);
            }
        }

        mPersistentCookieJarStore.save(url, persistentCookies);

        mCookieJarStore.save(url, storeCookies);
    }

    @NonNull
    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        // 用 HashMap 是为了去重, 对同名 Cookie 只保留最后一个
        HashMap<String, Cookie> cookieHashMap = new HashMap<>();

        List<Cookie> storeCookies = mCookieJarStore.match(url);
        for (Cookie storeCookie : storeCookies) {
            cookieHashMap.put(storeCookie.name(), storeCookie);
        }

        List<Cookie> persistentCookies = mPersistentCookieJarStore.match(url);
        for (Cookie persistentCookie : persistentCookies) {
            cookieHashMap.put(persistentCookie.name(), persistentCookie);
        }

        return new ArrayList<>(cookieHashMap.values());
    }
}
