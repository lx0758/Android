package com.liux.android.http.interceptor;

import android.text.TextUtils;

import com.liux.android.http.Http;
import com.liux.android.http.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 根据特定Header更换请求BaseUrl
 * 作用域 HEADER_BASE_URL &gt; HEADER_BASE_RULE &gt; BaseUrl
 * 1.动态更换Scheme       支持
 * 2.动态更换Host         支持
 * 3.动态更换Port         支持
 * 4.动态更换Authority    不需要支持
 * 5.动态更换Path         支持(BaseUrl中部分)
 * 6.动态更换QueryData    不需要支持
 * 7.动态更换Fragment     不需要支持
 * Created by Liux on 2018/1/16.
 */

public class BaseUrlInterceptor implements Interceptor {
    // 自定义头部信息,标记Api动态域名信息
    public static final String HEADER_BASE_URL = "Base-Url";
    // 自定义头部信息,标记Api动态域名规则信息
    public static final String HEADER_BASE_RULE = "Base-Rule";

    private Http mHttp;
    // 全局动态BaseUrl
    private String mBaseUrl = null;
    // 动态域名规则
    private Map<String, String> mDomainRules = new HashMap<>();

    public BaseUrlInterceptor(Http http) {
        mHttp = http;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // 使用 Http 手动发起的请求不做处理
        if (HttpUtil.isManuallyRequest(request)) {
            return chain.proceed(request);
        }

        String baseUrl = null;

        // 检测有没有 HEADER_BASE_URL
        baseUrl = request.header(HEADER_BASE_URL);
        if (!TextUtils.isEmpty(baseUrl)) {
            request = parseNewRequest(baseUrl, request, HEADER_BASE_URL);
            return chain.proceed(request);
        }

        // 检测有没有 HEADER_BASE_RULE
        String rule = request.header(HEADER_BASE_RULE);
        if (!TextUtils.isEmpty(rule)) {
            baseUrl = getDomainRule(rule);
            if (!TextUtils.isEmpty(baseUrl)) {
                request = parseNewRequest(baseUrl, request, HEADER_BASE_RULE);
                return chain.proceed(request);
            }
        }

        // 检测全局 BaseUrl
        baseUrl = getBaseUrl();
        if (!equalsRetorfitBaseUrl(baseUrl)) {
            request = parseNewRequest(baseUrl, request, null);
            return chain.proceed(request);
        }

        return chain.proceed(request);
    }

    /**
     * 获取当前全局BaseUrl
     * @return
     */
    public String getBaseUrl() {
        return mBaseUrl;
    }

    /**
     * 设置当前全局BaseUrl
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    /**
     * 获取某个规则对应的URL
     * @param rule
     * @return
     */
    public String getDomainRule(String rule) {
        String url = mDomainRules.get(rule);
        return url;
    }

    /**
     * 加入某个URL对应的规则
     * @param rule
     * @param baseUrl
     */
    public void putDomainRule(String rule, String baseUrl) {
        mDomainRules.put(rule, baseUrl);
    }

    /**
     * 获取所有URL对应规则,Copy目的是防止跳过检查添加规则
     * @return
     */
    public Map<String, String> getDomainRules() {
        Map<String, String> result = new HashMap<>();
        result.putAll(mDomainRules);
        return result;
    }

    /**
     * 清除所有URL对应规则
     */
    public void clearDomainRules() {
        mDomainRules.clear();
    }

    /**
     * 复制原始请求体并更换Host
     * @param baseUrl
     * @param request
     * @return
     */
    private Request parseNewRequest(String baseUrl, Request request, String header) {
        HttpUrl newHttpUrl = parseNewHttpUrl(baseUrl, request);
        return request.newBuilder()
                .url(newHttpUrl)
                .removeHeader(header != null ? header : "")
                .build();
    }

    /**
     * 复制原始请求URL并替换BaseUrl信息
     * @param baseUrl
     * @param request
     * @return
     */
    private HttpUrl parseNewHttpUrl(String baseUrl, Request request) {
        // 用BASE_URL的时候无法限制BaseUrl格式所以加上判断
        if (!baseUrl.endsWith("/")) baseUrl += "/";

        HttpUrl reqHttpUrl = request.url();
        HttpUrl oldBaseHttpUrl = mHttp.getRetrofit().baseUrl();
        HttpUrl newBaseHttpUrl = HttpUrl.parse(baseUrl);
        if (newBaseHttpUrl == null) throw new IllegalArgumentException("Illegal URL: " + baseUrl);

        String newReqUrl = null;
        String oldReqUrl = reqHttpUrl.url().toString();
        String oldBaseUrl = oldBaseHttpUrl.url().toString();
        String newBaseUrl = newBaseHttpUrl.url().toString();

        // 判断是否是 非根Url 请求
        if (oldReqUrl.startsWith(oldBaseUrl)) {
            // @GET("api/xxx") 表示 非根Url ,即拼接的Url
            newReqUrl =  oldReqUrl.replace(oldBaseUrl, newBaseUrl);
        } else {
            // @GET("/api/xxx") 表示 根Url ,即从根路径开始的Url
            newReqUrl = oldReqUrl.replace(
                    oldBaseUrl.substring(0, oldBaseUrl.indexOf("/", 10)),
                    newBaseUrl.substring(0, newBaseUrl.indexOf("/", 10))
            );
        }

        HttpUrl.Builder builder = request.url().newBuilder(newReqUrl)
                .encodedUsername(reqHttpUrl.username())
                .encodedPassword(reqHttpUrl.password());

        return builder.build();
    }

    /**
     * 比对新的Url和Retorfit中的BaseUrl是否一致
     * @param url
     * @return
     */
    private boolean equalsRetorfitBaseUrl(String url) {
        if (TextUtils.isEmpty(url)) return true;
        HttpUrl newHttpUrl = HttpUrl.parse(url);
        if (newHttpUrl == null) return true;
        String oldUrl = mHttp.getRetrofit().baseUrl().url().toString();
        String newUrl = newHttpUrl.toString();
        return oldUrl.equals(newUrl);
    }
}
