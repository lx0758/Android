package com.liux.android.http;

import android.content.Context;

import androidx.annotation.IntDef;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.liux.android.http.converter.FastJsonConverterFactory;
import com.liux.android.http.dns.HttpDns;
import com.liux.android.http.dns.TencentHttpDns;
import com.liux.android.http.dns.TimeOutDns;
import com.liux.android.http.interceptor.RequestInterceptor;
import com.liux.android.http.interceptor.HttpLoggingInterceptor;
import com.liux.android.http.interceptor.BaseUrlInterceptor;
import com.liux.android.http.interceptor.TimeoutInterceptor;
import com.liux.android.http.interceptor.UserAgentInterceptor;
import com.liux.android.http.request.BodyRequest;
import com.liux.android.http.request.QueryRequest;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * 基于 OkHttp3 封装的Http客户端 <br>
 * 0.全局单例模式 <br>
 * 1.GET/HEAD/POST/DELETE/PUT/PATCH六种方法的同步/异步访问调用 <br>
 * 2.Retorfit2 + RxJava2 支持 <br>
 * 3.数据解析使用 FastJson <br>
 * 4.请求头/请求参数回调 <br>
 * 5.超时时间/BaseUrl/UserAgent灵活设置 <br>
 * 6.请求数据进度回调支持 <br>
 * 7.流传输能力 <br>
 * @author Liux
 */

public class Http {
    private static volatile Http mInstance;
    public static Http get() {
        if (mInstance == null) throw new NullPointerException("Http has not been initialized");
        return mInstance;
    }
    public static boolean isInit() {
        synchronized(Http.class) {
            return mInstance != null;
        }
    }
    public static void init(Context context, String baseUrl) {
        init(
                context,
                null,
                new Retrofit.Builder().baseUrl(baseUrl)
        );
    }
    public static void init(Context context, OkHttpClient.Builder okHttpBuilder, Retrofit.Builder retrofitBuilder) {
        if (mInstance != null) return;
        synchronized(Http.class) {
            if (mInstance != null) return;
            mInstance = new Http(context, okHttpBuilder, retrofitBuilder);
        }
    }
    public static void release() {
        if (mInstance != null) {
            mInstance.mRequestInterceptor.setOnHeaderListener(null);
            mInstance.mRequestInterceptor.setOnRequestListener(null);
            mInstance.mOkHttpClient.dispatcher().executorService().shutdown();
            mInstance.mOkHttpClient.connectionPool().evictAll();
            mInstance = null;
        }
    }

    public static final String TAG = "[Http]";

    private Context mContext;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;

    private TimeoutInterceptor mTimeoutInterceptor;
    private BaseUrlInterceptor mBaseUrlInterceptor;
    private UserAgentInterceptor mUserAgentInterceptor;
    private RequestInterceptor mRequestInterceptor;
    private HttpLoggingInterceptor mHttpLoggingInterceptor;

    private Http(Context context, OkHttpClient.Builder okHttpBuilder, Retrofit.Builder retrofitBuilder) {
        if (context == null) throw new NullPointerException("Context required.");

        mContext = context.getApplicationContext();

        mTimeoutInterceptor = new TimeoutInterceptor();
        mBaseUrlInterceptor = new BaseUrlInterceptor(this);
        mUserAgentInterceptor = new UserAgentInterceptor(mContext);
        mRequestInterceptor = new RequestInterceptor();
        mHttpLoggingInterceptor = new HttpLoggingInterceptor();

        mOkHttpClient = initOkHttpClient(okHttpBuilder);

        mRetrofit = initRetorfit(retrofitBuilder);

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
     * 取 Retrofit 实例
     * @return
     */
    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * 取 Retrofit 服务
     * @param service
     * @param <T>
     * @return
     */
    public <T> T getService(Class<T> service) {
        return getRetrofit().create(service);
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
     * @param level
     * @return
     */
    public Http setLoggingLevel(@LogLevel int level) {
        HttpLoggingInterceptor.Level l;
        switch (level) {
            case LOG_LEVEL_BODY:
                l = HttpLoggingInterceptor.Level.BODY;
                break;
            case LOG_LEVEL_HEADERS:
                l = HttpLoggingInterceptor.Level.HEADERS;
                break;
            case LOG_LEVEL_BASIC:
                l = HttpLoggingInterceptor.Level.BASIC;
                break;
            case LOG_LEVEL_NONE:
            default:
                l = HttpLoggingInterceptor.Level.NONE;
                break;
        }
        mHttpLoggingInterceptor.setLevel(l);
        return this;
    }

    /**
     * 设置请求头监听
     * @param listener
     * @return
     */
    public Http setOnHeaderListener(OnHeaderListener listener) {
        mRequestInterceptor.setOnHeaderListener(listener);
        return this;
    }

    /**
     * 设置请求监听
     * @param listener
     * @return
     */
    public Http setOnRequestListener(OnRequestListener listener) {
        mRequestInterceptor.setOnRequestListener(listener);
        return this;
    }

    public static final String HEADER_BASE_URL = BaseUrlInterceptor.HEADER_BASE_URL;
    public static final String HEADER_BASE_RULE = BaseUrlInterceptor.HEADER_BASE_RULE;

    /**
     * 获取当前全局BaseUrl
     * @return
     */
    public String getBaseUrl() {
        return mBaseUrlInterceptor.getBaseUrl();
    }

    /**
     * 设置当前全局BaseUrl
     *
     * @Headers({
     *         Http.HEADER_BASE_URL + "https://api.domain.com:88/api/"
     * })
     *
     * @param baseUrl
     * @return
     */
    public Http setBaseUrl(String baseUrl) {
        checkBaseUrl(baseUrl);
        mBaseUrlInterceptor.setBaseUrl(baseUrl);
        return this;
    }

    /**
     * 获取某个规则对应的URL
     * @param rule
     * @return
     */
    public String getDomainRule(String rule) {
        String url = mBaseUrlInterceptor.getDomainRule(rule);
        return url;
    }

    /**
     * 加入某个URL对应的规则
     *
     * @Headers({
     *         Http.HEADER_BASE_RULE + "{rule}"
     * })
     *
     * @param rule
     * @param baseUrl
     * @return
     */
    public Http putDomainRule(String rule, String baseUrl) {
        checkBaseUrl(baseUrl);
        mBaseUrlInterceptor.putDomainRule(rule, baseUrl);
        return this;
    }

    /**
     * 获取所有URL对应规则,Copy目的是防止跳过检查添加规则
     * @return
     */
    public Map<String, String> getDomainRules() {
        return mBaseUrlInterceptor.getDomainRules();
    }

    /**
     * 清除所有URL对应规则
     * @return
     */
    public Http clearDomainRules() {
        mBaseUrlInterceptor.clearDomainRules();
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

    /**
     * 初始化 OkHttpClient
     * @param okHttpBuilder
     * @return
     */
    private OkHttpClient initOkHttpClient(OkHttpClient.Builder okHttpBuilder) {
        if (okHttpBuilder == null) {
            File cacheDir = mContext.getExternalCacheDir();
            if (cacheDir == null || !cacheDir.exists()) cacheDir = mContext.getCacheDir();

            return new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(
                            new SetCookieCache(),
                            new SharedPrefsCookiePersistor(mContext)
                    ))
                    .dns(new TimeOutDns(2, TimeUnit.SECONDS, 2))
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .pingInterval(30, TimeUnit.SECONDS)
                    .cache(new Cache(new File(cacheDir, "okhttp"), 50 * 1024 * 1024))
                    .retryOnConnectionFailure(true)
                    .addInterceptor(mTimeoutInterceptor)
                    .addInterceptor(mBaseUrlInterceptor)
                    .addInterceptor(mUserAgentInterceptor)
                    .addInterceptor(mRequestInterceptor)
                    .addInterceptor(mHttpLoggingInterceptor)
                    .build();
        } else {
            return okHttpBuilder
                    .addInterceptor(mTimeoutInterceptor)
                    .addInterceptor(mBaseUrlInterceptor)
                    .addInterceptor(mUserAgentInterceptor)
                    .addInterceptor(mRequestInterceptor)
                    .addInterceptor(mHttpLoggingInterceptor)
                    .build();
        }
    }

    /**
     * 初始化 Retorfit
     * @param retrofitBuilder
     * @return
     */
    private Retrofit initRetorfit(Retrofit.Builder retrofitBuilder) {
        retrofitBuilder
                .client(mOkHttpClient)
                .addConverterFactory(FastJsonConverterFactory.create());

        CallAdapter.Factory factory;
        factory = HttpUtil.getRxJava2CallAdapterFactory();
        if (factory != null) {
            retrofitBuilder.addCallAdapterFactory(factory);
        }
        factory = HttpUtil.getRxJavaCallAdapterFactory();
        if (factory != null) {
            retrofitBuilder.addCallAdapterFactory(factory);
        }

        return retrofitBuilder.build();
    }

    /**
     * 预检查BaseUrl格式
     * @param baseUrl
     */
    private void checkBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            throw new NullPointerException("baseUrl == null");
        }
        HttpUrl httpUrl = HttpUrl.parse(baseUrl);
        if (httpUrl == null) {
            throw new IllegalArgumentException("Illegal URL: " + baseUrl);
        }
        List<String> pathSegments = httpUrl.pathSegments();
        if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
            throw new IllegalArgumentException("baseUrl must end in /: " + baseUrl);
        }
    }

    @IntDef({LOG_LEVEL_NONE, LOG_LEVEL_BASIC, LOG_LEVEL_HEADERS, LOG_LEVEL_BODY})
    @Retention(RetentionPolicy.SOURCE)
    @interface LogLevel{}
}