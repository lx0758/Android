package com.liux.android.http.interceptor;

import android.content.Context;
import android.content.pm.PackageManager;

import com.liux.android.http.HttpUtil;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Liux on 2017/11/29.
 */

public class UserAgentInterceptor implements Interceptor {
    private static String USER_AGENT = System.getProperty("http.agent");

    public UserAgentInterceptor(Context context) {
        setUserAgent(
                getDefaultUserAgent(context)
        );
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.header("User-Agent", USER_AGENT);
        return chain.proceed(builder.build());
    }

    public String getUserAgent() {
        return USER_AGENT;
    }

    public void setUserAgent(String userAgent) {
        USER_AGENT = HttpUtil.checkHeaderChar(userAgent);
    }

    /**
     * Dalvik/2.1.0 (Linux; U; Android 6.0.1; MI 4LTE MIUI/7.11.9) App_packageName_versionName/versionCode
     * @param context
     * @return
     */
    private String getDefaultUserAgent(Context context) {
        // Mozilla/5.0 (Linux; Android 6.0.1; MI 4LTE Build/MMB29M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 Mobile Safari/537.36
        // WebSettings.getDefaultUserAgent(context);
        //
        // Dalvik/2.1.0 (Linux; U; Android 6.0.1; MI 4LTE MIUI/7.11.9)
        // System.getProperty("http.agent");

        int versionCode = -1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.format(
                Locale.CHINA,
                "%s App_%s_%s/%d",
                System.getProperty("http.agent"),
                context.getPackageName(),
                versionName,
                versionCode
        );
    }
}
