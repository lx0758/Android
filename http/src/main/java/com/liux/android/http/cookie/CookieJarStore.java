package com.liux.android.http.cookie;

import androidx.annotation.NonNull;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public interface CookieJarStore {

    void save(HttpUrl url, @NonNull List<Cookie> cookies);

    @NonNull
    List<Cookie> match(@NonNull HttpUrl url);
}
