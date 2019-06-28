package com.liux.android.example.http;

import com.alibaba.fastjson.JSONObject;
import com.liux.android.http.Http;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Liux on 2018/1/16.
 */

public interface TestApi {

    @GET("retorfit-get")
    Observable<JSONObject> testGet(
            @Query("id") int id,
            @Query("name") String name
    );

    @POST("retorfit-post-body")
    Observable<JSONObject> testPostBody(
            @Body RequestBody body
    );

    @FormUrlEncoded
    @POST("retorfit-post-form")
    Observable<JSONObject> testPostForm(
            @Query("id") int id,
            @Query("name") String name,
            @Field("id") int id2,
            @Field("name") String name2
    );

    @Multipart
    @POST("retorfit-post-multipart")
    Observable<JSONObject> testPostMultipart(
            @Query("id") int id,
            @Query("name") String name,
            @Part("id") int id2,
            @Part("name") String name2,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part aByte,
            @Part MultipartBody.Part stream
    );

    @GET("weather/")
    @Headers({
            Http.HEADER_BASE_URL + ":http://api.ip138.com/"
    })
    Observable<JSONObject> queryWeather(
            @Header("token") String token,
            @Query("code") String code,
            @Query("type") String type
    );

    @GET("query/")
    @Headers({
            Http.HEADER_BASE_RULE + ":138"
    })
    Observable<JSONObject> queryIP(
            @Header("token") String token,
            @Query("ip") String ip
    );

    // 以"/"开头的表示从根路径开始
    @GET("mobile/")
    Observable<JSONObject> queryMobile(
            @Header("token") String token,
            @Query("mobile") String mobile
    );

    @GET("/express/info/")
    Observable<JSONObject> queryExpress(
            @Header("token") String token,
            @Query("no") String no
    );

    // 以"/"开头的表示从根路径开始
    @GET("retorfit-timeout")
    @Headers({
            Http.HEADER_TIMEOUT_CONNECT + ":3000",
            Http.HEADER_TIMEOUT_WRITE + ":6000",
            Http.HEADER_TIMEOUT_READ + ":6000"
    })
    Observable<JSONObject> testTimeout(
            @Query("data") String data
    );

    // 以"/"开头的表示从根路径开始
    @GET("retorfit-timeout-global")
    Observable<JSONObject> testTimeoutGlobal(
            @Query("data") String data
    );
}
