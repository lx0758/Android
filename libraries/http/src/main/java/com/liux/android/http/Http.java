package com.liux.android.http;

import androidx.annotation.IntDef;

import com.liux.android.http.cookie.DefaultCookieJar;
import com.liux.android.http.dns.TimeOutDns;
import com.liux.android.http.interceptor.HttpLoggingInterceptor;
import com.liux.android.http.interceptor.RequestInterceptor;
import com.liux.android.http.interceptor.TimeoutInterceptor;
import com.liux.android.http.interceptor.UserAgentInterceptor;
import com.liux.android.http.request.BodyRequest;
import com.liux.android.http.request.QueryRequest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 基于 OkHttp3 封装的Http客户端 <br>
 * 1.GET/HEAD/POST/DELETE/PUT/PATCH六种方法的同步/异步访问调用 <br>
 * 2.请求头/请求参数回调 <br>
 * 3.超时时间/UserAgent灵活设置 <br>
 * 4.请求数据进度回调支持 <br>
 * 5.流传输能力 <br>
 * @author Liux
 */

public class Http {

    public static final String TAG = "[Http]";

    private final OkHttpClient mOkHttpClient;

    private final TimeoutInterceptor mTimeoutInterceptor;
    private final UserAgentInterceptor mUserAgentInterceptor;
    private final RequestInterceptor mRequestInterceptor;
    private final HttpLoggingInterceptor mHttpLoggingInterceptor;

    public Http() {
        this(
                new OkHttpClient.Builder()
                        .dns(new TimeOutDns(5, TimeUnit.SECONDS, 2))
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .pingInterval(30, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .cookieJar(new DefaultCookieJar())
        );
    }

    public Http(OkHttpClient.Builder okHttpBuilder) {
        if (okHttpBuilder == null) throw new NullPointerException("OkHttpClient.Builder required");

        mTimeoutInterceptor = new TimeoutInterceptor();
        mUserAgentInterceptor = new UserAgentInterceptor();
        mRequestInterceptor = new RequestInterceptor();
        mHttpLoggingInterceptor = new HttpLoggingInterceptor();

        mOkHttpClient = okHttpBuilder
                .addInterceptor(mTimeoutInterceptor)
                .addInterceptor(mUserAgentInterceptor)
                .addInterceptor(mRequestInterceptor)
                .addInterceptor(mHttpLoggingInterceptor)
                .build();

        mTimeoutInterceptor.setOverallConnectTimeout(mOkHttpClient.connectTimeoutMillis(), TimeUnit.MILLISECONDS);
        mTimeoutInterceptor.setOverallWriteTimeout(mOkHttpClient.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
        mTimeoutInterceptor.setOverallReadTimeout(mOkHttpClient.readTimeoutMillis(), TimeUnit.MILLISECONDS);
    }

    public QueryRequest get(String url) {
        return new QueryRequest(getOkHttpClient(), QueryRequest.Method.GET).url(url);
    }

    public QueryRequest head(String url) {
        return new QueryRequest(getOkHttpClient(), QueryRequest.Method.HEAD).url(url);
    }

    public BodyRequest post(String url) {
        return new BodyRequest(getOkHttpClient(), BodyRequest.Method.POST).url(url);
    }

    public BodyRequest delete(String url) {
        return new BodyRequest(getOkHttpClient(), BodyRequest.Method.DELETE).url(url);
    }

    public BodyRequest put(String url) {
        return new BodyRequest(getOkHttpClient(), BodyRequest.Method.PUT).url(url);
    }

    public BodyRequest patch(String url) {
        return new BodyRequest(getOkHttpClient(), BodyRequest.Method.PATCH).url(url);
    }

    /**
     * 取 OkHttpClient 实例
     * @return
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 取当前用户识别标志
     * @return
     */
    public String getUserAgent() {
        return mUserAgentInterceptor.getUserAgent();
    }

    /**
     * 设置当前用户识别标志
     * @param userAgent
     * @return
     */
    public Http setUserAgent(String userAgent) {
        mUserAgentInterceptor.setUserAgent(userAgent);
        return this;
    }

    public static final int LOG_LEVEL_NONE = 0;
    public static final int LOG_LEVEL_BASIC = 1;
    public static final int LOG_LEVEL_HEADERS = 2;
    public static final int LOG_LEVEL_BODY = 3;
    /**
     * 设置打印日志级别
     * @param logLevel
     * @return
     */
    public Http setLoggingLevel(@LogLevel int logLevel) {
        HttpLoggingInterceptor.Level level;
        switch (logLevel) {
            case LOG_LEVEL_BODY:
                level = HttpLoggingInterceptor.Level.BODY;
                break;
            case LOG_LEVEL_HEADERS:
                level = HttpLoggingInterceptor.Level.HEADERS;
                break;
            case LOG_LEVEL_BASIC:
                level = HttpLoggingInterceptor.Level.BASIC;
                break;
            case LOG_LEVEL_NONE:
            default:
                level = HttpLoggingInterceptor.Level.NONE;
                break;
        }
        mHttpLoggingInterceptor.setLevel(level);
        return this;
    }

    /**
     * 设置监听
     * @param callback
     * @return
     */
    public Http setCallback(Callback callback) {
        setHeaderCallback(callback);
        setRequestCallback(callback);
        return this;
    }

    /**
     * 设置请求头监听
     * @param callback
     * @return
     */
    public Http setHeaderCallback(HeaderCallback callback) {
        mRequestInterceptor.setHeaderCallback(callback);
        return this;
    }

    /**
     * 设置请求监听
     * @param callback
     * @return
     */
    public Http setRequestCallback(RequestCallback callback) {
        mRequestInterceptor.setRequestCallback(callback);
        return this;
    }

    public static final String HEADER_TIMEOUT_CONNECT = TimeoutInterceptor.HEADER_TIMEOUT_CONNECT;
    public static final String HEADER_TIMEOUT_WRITE = TimeoutInterceptor.HEADER_TIMEOUT_WRITE;
    public static final String HEADER_TIMEOUT_READ = TimeoutInterceptor.HEADER_TIMEOUT_READ;

    /**
     * 获取全局连接超时时间
     * @return
     */
    public int getOverallConnectTimeoutMillis() {
        return mTimeoutInterceptor.getOverallConnectTimeoutMillis();
    }

    /**
     * 设置全局连接超时时间
     * @param overallConnectTimeout
     * @param timeUnit
     * @return
     */
    public Http setOverallConnectTimeout(int overallConnectTimeout, TimeUnit timeUnit) {
        mTimeoutInterceptor.setOverallConnectTimeout(overallConnectTimeout, timeUnit);
        return this;
    }

    /**
     * 获取全局写超时时间
     * @return
     */
    public int getOverallWriteTimeoutMillis() {
        return mTimeoutInterceptor.getOverallWriteTimeoutMillis();
    }

    /**
     * 设置全局写超时时间
     * @param overallWriteTimeout
     * @param timeUnit
     * @return
     */
    public Http setOverallWriteTimeout(int overallWriteTimeout, TimeUnit timeUnit) {
        mTimeoutInterceptor.setOverallConnectTimeout(overallWriteTimeout, timeUnit);
        return this;
    }

    /**
     * 获取全局读超时时间
     * @return
     */
    public int getOverallReadTimeoutMillis() {
        return mTimeoutInterceptor.getOverallReadTimeoutMillis();
    }

    /**
     * 设置全局读超时时间
     * @param overallReadTimeout
     * @param timeUnit
     * @return
     */
    public Http setOverallReadTimeout(int overallReadTimeout, TimeUnit timeUnit) {
        mTimeoutInterceptor.setOverallConnectTimeout(overallReadTimeout, timeUnit);
        return this;
    }

    @IntDef({LOG_LEVEL_NONE, LOG_LEVEL_BASIC, LOG_LEVEL_HEADERS, LOG_LEVEL_BODY})
    @Retention(RetentionPolicy.SOURCE)
    @interface LogLevel{}
}