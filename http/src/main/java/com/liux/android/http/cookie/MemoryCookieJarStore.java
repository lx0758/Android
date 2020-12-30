package com.liux.android.http.cookie;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class MemoryCookieJarStore implements CookieJarStore {

    private final ArrayList<Cookie> mCookies = new ArrayList<>();

    @Override
    public void save(HttpUrl url, @NonNull List<Cookie> cookies) {
        clearRepeatCookie(cookies);
        mCookies.addAll(cookies);
    }

    @NonNull
    @Override
    public List<Cookie> match(@NonNull HttpUrl url) {
        ArrayList<Cookie> cookies = new ArrayList<>();

        long nowTime = System.currentTimeMillis();
        Iterator<Cookie> cookieIterator = mCookies.iterator();
        while (cookieIterator.hasNext()) {
            Cookie cookie = cookieIterator.next();

            if (cookie.expiresAt() < nowTime) {
                cookieIterator.remove();
                continue;
            }

            if (cookie.matches(url)) cookies.add(cookie);
        }

        return cookies;
    }

    /**
     * 清除掉已经存在的重复 Cookie, 重复的满足条件:
     * 1. domain 相同
     * 2. path 相同
     * 3. name 相同
     * @param cookies
     */
    private void clearRepeatCookie(@NonNull List<Cookie> cookies) {
        if (cookies.isEmpty()) return;
        if (mCookies.isEmpty()) return;

        List<String> newKeys = new ArrayList<>();
        for (Cookie cookie : cookies) {
            String key = getCookieKey(cookie);
            newKeys.add(key);
        }

        Iterator<Cookie> cookieIterator = mCookies.iterator();
        while (cookieIterator.hasNext()) {
            Cookie cookie = cookieIterator.next();
            String key = getCookieKey(cookie);
            if (newKeys.contains(key)) {
                cookieIterator.remove();
            }
        }
    }

    private String getCookieKey(@NonNull Cookie cookie) {
        return cookie.domain() + cookie.path() + cookie.name();
    }
}
