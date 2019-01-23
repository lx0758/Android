package com.liux.android.example.http;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.liux.android.example.R;
import com.liux.android.http.Http;
import com.liux.android.http.HttpUtil;
import com.liux.android.http.progress.OnProgressListener;
import com.liux.android.http.progress.OnResponseProgressListener;
import com.liux.android.http.request.Result;
import com.liux.android.http.request.UIResult;
import com.liux.android.http.request.Request;
import com.liux.android.http.request.RequestManager;
import com.liux.android.tool.TT;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.observers.DisposableObserver;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * Created by Liux on 2017/11/28.
 */

public class HTTPActivity extends AppCompatActivity implements RequestManager {
    private static final String TAG = "HTTPActivity";

    @BindView(R.id.tv_log)
    TextView tvLog;
    @BindView(R.id.et_data)
    EditText etData;

    private TestApiModel mTestApiModle = new TestApiModelImpl(this);
    private RequestManager mRequestManager = RequestManager.Builder.build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 动态设置全局BaseUrl
        Http.get().setBaseUrl("http://api.6xyun.cn/");

        // 动态设置全局BaseUrl规则
        Http.get().putDomainRule("138", "http://api.ip138.com/");

        setContentView(R.layout.activity_http);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestManager.cancelAll();
        cancelAll();
    }

    @OnClick({R.id.btn_retorfit_get, R.id.btn_retorfit_post_body, R.id.btn_retorfit_post_form, R.id.btn_retorfit_post_multipart, R.id.btn_retorfit_base_header, R.id.btn_retorfit_base_header_rule, R.id.btn_retorfit_base_global, R.id.btn_retorfit_base_global_root, R.id.btn_retorfit_timeout_header, R.id.btn_retorfit_timeout_global})
    public void onRetorfitClicked(View view) {
        String data = etData.getText().toString();
        Observer<JSONObject> observable = new DisposableObserver<JSONObject>() {
            @Override
            public void onNext(JSONObject jsonObject) {
                Log.d(TAG, "onNext" + jsonObject.toJSONString());
                showData(jsonObject.toJSONString());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError", e);
                TT.show(getApplicationContext(), "onError" + e, TT.LENGTH_LONG);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
        switch (view.getId()) {
            case R.id.btn_retorfit_get:
                mTestApiModle.testGet(
                        123,
                        "liux",
                        observable
                );
                break;
            case R.id.btn_retorfit_post_body:
                mTestApiModle.testPostBody(
                        123,
                        "liux",
                        observable
                );
                break;
            case R.id.btn_retorfit_post_form:
                mTestApiModle.testPostForm(
                        123,
                        "liux",
                        observable
                );
                break;
            case R.id.btn_retorfit_post_multipart:
                mTestApiModle.testPostMultipart(
                        123,
                        "liux",
                        getTempFile(),
                        getTempBytes(),
                        getTempInputStream(),
                        observable
                );
                break;
            case R.id.btn_retorfit_base_header:
                // 设置Base,正常获取数据
                mTestApiModle.queryWeather(data, observable);
                break;
            case R.id.btn_retorfit_base_header_rule:
                // 设置Base-Rule,正常获取数据
                mTestApiModle.queryIP(data, observable);
                break;
            case R.id.btn_retorfit_base_global:
                // 使用全局 Base, 404
                mTestApiModle.queryMobile(data, observable);
                break;
            case R.id.btn_retorfit_base_global_root:
                // 使用全局Base,并使用根路径, 404
                mTestApiModle.queryExpress(data, observable);
                break;
            case R.id.btn_retorfit_timeout_header:
                mTestApiModle.testTimeout(data, observable);
                break;
            case R.id.btn_retorfit_timeout_global:
                Http.get().setOverallConnectTimeout(5);
                Http.get().setOverallWriteTimeout(20);
                Http.get().setOverallReadTimeout(20);
                mTestApiModle.testTimeoutGlobal(data, observable);
                break;
        }
    }

    @OnClick({R.id.btn_request_get, R.id.btn_request_post_body, R.id.btn_request_post_form, R.id.btn_request_post_multipart, R.id.btn_request_timeout_header, R.id.btn_request_timeout_global})
    public void onRequestClicked(View view) {
        String url = etData.getText().toString();
        if (HttpUrl.parse(url) == null) {
            TT.show(this, "URL不正确,必须形如 http://www.domain.com/", TT.LENGTH_LONG);
            etData.setText("http://api.6xyun.cn/");
            return;
        }
        switch (view.getId()) {
            case R.id.btn_request_get:
                Http.get().get(url + "request-get")
                        .addHeader("Request-Header-Id", "btn_request_get")
                        .addQuery("Request-Query-Id", "btn_request_get")
                        // 很多服务不支持
                        //.fragment("testFragment")
                        .progress(new OnResponseProgressListener() {
                            @Override
                            public void onResponseProgress(final HttpUrl httpUrl, final long bytesRead, final long contentLength, final boolean done) {
                                System.out.println("onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength + "," + done);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TT.show(HTTPActivity.this, "onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength + "," + done, TT.LENGTH_LONG);
                                    }
                                });
                            }
                        })
                        .manager(mRequestManager)
                        .async(new Result() {
                            @Override
                            public void onFailure(final IOException e) {
                                System.out.println("onFailure:" + e);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TT.show(HTTPActivity.this, "onFailure:" + e, TT.LENGTH_LONG);
                                    }
                                });
                            }

                            @Override
                            public void onSucceed(Response response) throws IOException {
                                final String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showData(result);
                                    }
                                });
                            }
                        });
                break;
            case R.id.btn_request_post_body:
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Request-Body-Id", "btn_request_post_body");
                Http.get().post(url + "request-post-body")
                        .addHeader("Request-Header-Id", "btn_request_post_body")
                        .addQuery("Request-Query-Id", "btn_request_post_body")
                        .body(HttpUtil.parseJson(jsonObject.toJSONString()))
                        .manager(this)
                        .async(new UIResult() {
                            @Override
                            public void onFailure(IOException e) {
                                System.out.println("onFailure:" + e);
                                TT.show(HTTPActivity.this, "onFailure:" + e, TT.LENGTH_LONG);
                            }

                            @Override
                            public void onSucceed(Response response) throws IOException {
                                String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                showData(result);
                            }
                        });
                break;
            case R.id.btn_request_post_form:
                Http.get().post(url + "request-post-form")
                        .addHeader("Request-Header-Id", "btn_request_post_form")
                        .addQuery("Request-Query-Id", "btn_request_post_form")
                        .addParam("Request-Param-Id", "btn_request_post_form")
                        .manager(this)
                        .async(new UIResult() {
                            @Override
                            public void onFailure(IOException e) {
                                System.out.println("onFailure:" + e);
                                TT.show(HTTPActivity.this, "onFailure:" + e, TT.LENGTH_LONG);
                            }

                            @Override
                            public void onSucceed(Response response) throws IOException {
                                String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                showData(result);
                            }
                        });
                break;
            case R.id.btn_request_post_multipart:
                Http.get().post(url + "request-post-multipart")
                        .addHeader("Request-Header-Id", "btn_request_post_multipart")
                        .addQuery("Request-Query-Id", "btn_request_post_multipart")
                        .addParam("Request-Param-Id", "btn_request_post_multipart")
                        .addParam("id", "3")
                        .addParam("name", "liux")
                        .addParam("file", getTempFile())
                        .addParam("bytes", getTempBytes())
                        .addParam("stream", getTempInputStream())
                        .progress(new OnProgressListener() {
                            @Override
                            public void onRequestProgress(final HttpUrl httpUrl, final long bytesWrite, final long contentLength, final boolean done) {
                                System.out.println("onRequestProgress:" + httpUrl + "," + bytesWrite + "," + contentLength + "," + done);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TT.show(HTTPActivity.this, "onRequestProgress:" + httpUrl + "," + bytesWrite + "," + contentLength + "," + done, TT.LENGTH_LONG);
                                    }
                                });
                            }

                            @Override
                            public void onResponseProgress(final HttpUrl httpUrl, final long bytesRead, final long contentLength, final boolean done) {
                                System.out.println("onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength + "," + done);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TT.show(HTTPActivity.this, "onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength + "," + done, TT.LENGTH_LONG);
                                    }
                                });
                            }
                        })
                        .manager(this)
                        .async(new UIResult() {
                            @Override
                            public void onFailure(IOException e) {
                                System.out.println("onFailure:" + e);
                                TT.show(HTTPActivity.this, "onFailure:" + e, TT.LENGTH_LONG);
                            }

                            @Override
                            public void onSucceed(Response response) throws IOException {
                                String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                showData(result);
                            }
                        });
                break;
            case R.id.btn_request_timeout_header:
                Http.get().post(url + "request-timeout")
                        .addHeader("Request-Header-Id", "btn_request_timeout_header")
                        .addQuery("Request-Query-Id", "btn_request_timeout_header")
                        .addParam("Request-Param-Id", "btn_request_timeout_header")
                        .connectTimeout(5)
                        .writeTimeout(10)
                        .readTimeout(10)
                        .manager(this)
                        .async(new UIResult() {
                            @Override
                            public void onFailure(IOException e) {
                                System.out.println("onFailure:" + e);
                                TT.show(HTTPActivity.this, "onFailure:" + e, TT.LENGTH_LONG);
                            }

                            @Override
                            public void onSucceed(Response response) throws IOException {
                                String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                showData(result);
                            }
                        });
                break;
            case R.id.btn_request_timeout_global:
                Http.get().setOverallConnectTimeout(5);
                Http.get().setOverallWriteTimeout(20);
                Http.get().setOverallReadTimeout(20);
                Http.get().post(url + "request-timeout-global")
                        .addHeader("Request-Header-Id", "btn_request_timeout_header")
                        .addQuery("Request-Query-Id", "btn_request_timeout_header")
                        .addParam("Request-Param-Id", "btn_request_timeout_header")
                        .manager(this)
                        .async(new UIResult() {
                            @Override
                            public void onFailure(IOException e) {
                                System.out.println("onFailure:" + e);
                                TT.show(HTTPActivity.this, "onFailure:" + e, TT.LENGTH_LONG);
                            }

                            @Override
                            public void onSucceed(Response response) throws IOException {
                                String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                showData(result);
                            }
                        });
                break;
        }
    }

    private File getTempFile() {
        File temp = null;
        FileOutputStream fileOutputStream = null;
        try {
            temp = File.createTempFile("测试文件_", ".apk");
            fileOutputStream = new FileOutputStream(temp);

            int random = new Random().nextInt(10240) + 51200;

            byte[] bytes = new byte[random];
            for (int i = 0; i < random; i++) {
                bytes[i] = (byte) System.nanoTime();
            }

            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(bytes);
            StringBuffer buf = new StringBuffer();
            byte[] bits = messageDigest.digest();
            for (int i = 0; i < bits.length; i++) {
                int a = bits[i];
                if (a < 0) a += 256;
                if (a < 16) buf.append("0");
                buf.append(Integer.toHexString(a));
            }
            System.out.println("getTempFile(len):" + bytes.length);
            System.out.println("getTempFile(sha1):" + buf.toString().toUpperCase());

            fileOutputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (temp != null) temp.deleteOnExit();
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return temp;
    }

    private byte[] getTempBytes() {
        int random = new Random().nextInt(30) + 50;

        StringBuilder builder = new StringBuilder();

        byte[] bytes = new byte[random];
        for (int i = 0; i < random; i++) {
            bytes[i] = (byte) System.nanoTime();

            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            builder.append(hex);
            builder.append(" ");
        }

        System.out.println("getTempBytes(len):" + bytes.length);
        System.out.println("getTempBytes(int):" + Arrays.toString(bytes));
        System.out.println("getTempBytes(hex):" + builder.toString().toUpperCase());
        return bytes;
    }

    private InputStream getTempInputStream() {
        int random = new Random().nextInt(50) + 200;

        StringBuilder builder = new StringBuilder();

        byte[] bytes = new byte[random];
        for (int i = 0; i < random; i++) {
            bytes[i] = (byte) System.nanoTime();

            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            builder.append(hex);
            builder.append(" ");
        }

        System.out.println("getTempInputStream(len):" + bytes.length);
        System.out.println("getTempInputStream(int):" + Arrays.toString(bytes));
        System.out.println("getTempInputStream(hex):" + builder.toString().toUpperCase());
        return new ByteArrayInputStream(bytes);
    }

    private void showData(String data) {
        data = formatJson(data);
        tvLog.setText(data);
    }

    /**
     * 格式化 Json
     * @param json
     * @return
     */
    private static String formatJson(String json) {
        try {
            String check = json.trim();
            if (check.startsWith("{") && check.endsWith("}")) {
                json = new org.json.JSONObject(json).toString(4);
            } else if (check.startsWith("[") && check.endsWith("]")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (JSONException e) {

        }
        return json;
    }

    List<Request> mRequests = new LinkedList<>();

    @Override
    public void add(Request request) {
        if (!mRequests.contains(request)) {
            mRequests.add(request);
        }
    }

    @Override
    public void remove(Request cancel) {
        mRequests.remove(cancel);
    }

    @Override
    public void cancelAll() {
        for (Request cancel : mRequests) {
            if (cancel != null) cancel.cancel();
        }
        mRequests.clear();
    }
}
