package com.liux.android.example.http;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liux.android.example.R;
import com.liux.android.example.databinding.ActivityHttpBinding;
import com.liux.android.http.Http;
import com.liux.android.http.HttpUtil;
import com.liux.android.util.JacksonUtil;
import com.liux.android.http.progress.OnProgressListener;
import com.liux.android.http.progress.OnResponseProgressListener;
import com.liux.android.http.request.Callback;
import com.liux.android.http.request.DownloadCallback;
import com.liux.android.http.request.Request;
import com.liux.android.http.request.RequestManager;
import com.liux.android.http.request.UICallback;
import com.liux.android.tool.SSLCreator;
import com.liux.android.tool.TT;
import com.liux.android.util.StreamUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * Created by Liux on 2017/11/28.
 */

public class HTTPActivity extends AppCompatActivity {
    private static final String TAG = "HTTPActivity";
    
    private ActivityHttpBinding mViewbinding;
    
    private final Http mHttp = new Http();
    private final RequestManager mRequestManager = RequestManager.Builder.build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHttp.setCallback(new com.liux.android.http.Callback() {
            @Override
            public void onHeaders(okhttp3.Request request, Map<String, String> headers) {

            }

            @Override
            public void onQueryRequest(okhttp3.Request request, Map<String, String> queryParams) {

            }

            @Override
            public void onBodyRequest(okhttp3.Request request, Map<String, String> queryParams, Map<String, String> bodyParams) {

            }

            @Override
            public void onBodyRequest(okhttp3.Request request, Map<String, String> queryParams, BodyParam bodyParam) {

            }
        });

        mViewbinding = ActivityHttpBinding.inflate(getLayoutInflater());
        setContentView(mViewbinding.getRoot());

        
        mViewbinding.btnRequestGet.setOnClickListener(this::onRequestClicked);
        mViewbinding.btnRequestPostForm.setOnClickListener(this::onRequestClicked);
        mViewbinding.btnRequestPostBody.setOnClickListener(this::onRequestClicked);
        mViewbinding.btnRequestPostMultipart.setOnClickListener(this::onRequestClicked);
        mViewbinding.btnRequestDownload.setOnClickListener(this::onRequestClicked);
        mViewbinding.btnRequestTimeoutHeader.setOnClickListener(this::onRequestClicked);
        mViewbinding.btnRequestTimeoutGlobal.setOnClickListener(this::onRequestClicked);

        mViewbinding.btnCertificateTest.setOnClickListener(this::onCertificateTest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestManager.cancelAll();
    }

    public void onRequestClicked(View view) {
        showData("");
        String url = mViewbinding.etData.getText().toString();
        if (HttpUrl.parse(url) == null) {
            TT.show("URL不正确,必须形如 http://www.domain.com/");
            mViewbinding.etData.setText("http://api.6xyun.cn/");
            return;
        }
        switch (view.getId()) {
            case R.id.btn_request_get:
                mHttp.get(url + "request-get")
                        .addHeader("Request-Header-Id", "btn_request_get")
                        .addQuery("Request-Query-Id", "btn_request_get")
                        // 很多服务不支持
                        //.fragment("testFragment")
                        .progress(new OnResponseProgressListener() {
                            @Override
                            public void onResponseProgress(final HttpUrl httpUrl, final long downloadLength, final long totalLength, final boolean completed) {
                                System.out.println("onResponseProgress:" + httpUrl + "," + downloadLength + "," + totalLength + "," + completed);
                                mViewbinding.etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TT.show("onResponseProgress:" + httpUrl + "," + downloadLength + "," + totalLength + "," + completed);
                                    }
                                });
                            }
                        })
                        .manager(mRequestManager)
                        .async(new Callback() {
                            @Override
                            public void onSucceed(Request request, Response response) throws IOException {
                                final String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                mViewbinding.etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showData(result);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Request request, IOException e) {
                                System.out.println("onFailure:" + e);
                                mViewbinding.etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TT.show("onFailure:" + e);
                                    }
                                });
                            }
                        });
                break;
            case R.id.btn_request_post_body:
                Map<String, String> params = new HashMap<>();
                params.put("Request-Body-Id", "btn_request_post_body");
                mHttp.post(url + "request-post-body")
                        .addHeader("Request-Header-Id", "btn_request_post_body")
                        .addQuery("Request-Query-Id", "btn_request_post_body")
                        .body(HttpUtil.parseJsonBody(JacksonUtil.toJson(params)))
                        .manager(mRequestManager)
                        .async(new UICallback() {
                            @Override
                            protected void onUISucceed(Request request, Response response) throws IOException {
                                String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                showData(result);
                            }

                            @Override
                            protected void onUIFailure(Request request, IOException e) {
                                System.out.println("onFailure:" + e);
                                TT.show("onFailure:" + e);
                            }
                        });
                break;
            case R.id.btn_request_post_form:
                mHttp.post(url + "request-post-form")
                        .addHeader("Request-Header-Id", "btn_request_post_form")
                        .addQuery("Request-Query-Id", "btn_request_post_form")
                        .addParam("Request-Param-Id", "btn_request_post_form")
                        .manager(mRequestManager)
                        .async(new UICallback() {
                            @Override
                            public void onUISucceed(Request request, Response response) throws IOException {
                                String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                showData(result);
                            }

                            @Override
                            public void onUIFailure(Request request, IOException e) {
                                System.out.println("onFailure:" + e);
                                TT.show("onFailure:" + e);
                            }
                        });
                break;
            case R.id.btn_request_post_multipart:
                mHttp.post(url + "request-post-multipart")
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
                            public void onRequestProgress(final HttpUrl httpUrl, long transmittedLength, long totalLength, final boolean done) {
                                System.out.println("onRequestProgress:" + httpUrl + "," + transmittedLength + "," + totalLength + "," + done);
                                mViewbinding.etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TT.show("onRequestProgress:" + httpUrl + "," + transmittedLength + "," + totalLength + "," + done);
                                    }
                                });
                            }

                            @Override
                            public void onResponseProgress(final HttpUrl httpUrl, long transmittedLength, long totalLength, final boolean completed) {
                                System.out.println("onResponseProgress:" + httpUrl + "," + transmittedLength + "," + totalLength + "," + completed);
                                mViewbinding.etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TT.show("onResponseProgress:" + httpUrl + "," + transmittedLength + "," + totalLength + "," + completed);
                                    }
                                });
                            }
                        })
                        .manager(mRequestManager)
                        .async(new UICallback() {
                            @Override
                            public void onUISucceed(Request request, Response response) throws IOException {
                                String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                showData(result);
                            }

                            @Override
                            public void onUIFailure(Request request, IOException e) {
                                System.out.println("onFailure:" + e);
                                TT.show("onFailure:" + e);
                            }
                        });
                break;
            case R.id.btn_request_download:
                mHttp.get(url)
                        .addQuery("t", String.valueOf(System.currentTimeMillis()))
                        .manager(mRequestManager)
                        .download(new File(getCacheDir(), String.valueOf(System.currentTimeMillis())), new DownloadCallback() {
                            @Override
                            public void onProgress(long transmittedLength, long totalLength) {
                                System.out.println("onProgress:" + transmittedLength + ", " + totalLength);
                                mViewbinding.tvLog.append("onProgress:" + transmittedLength + ", " + totalLength);
                                mViewbinding.tvLog.append("\n");
                            }

                            @Override
                            public void onSucceed(File file) {
                                System.out.println("onSucceed:" + file.getAbsolutePath());
                                mViewbinding.tvLog.append("onSucceed:" + file.getAbsolutePath());
                                mViewbinding.tvLog.append("\n");
                            }

                            @Override
                            public void onFailure(IOException e) {
                                System.out.println("onFailure:" + e);
                                mViewbinding.tvLog.append("onFailure:" + e);
                                mViewbinding.tvLog.append("\n");
                            }
                        });
                mViewbinding.tvLog.setText(null);
                break;
            case R.id.btn_request_timeout_header:
                mHttp.post(url + "request-timeout")
                        .addHeader("Request-Header-Id", "btn_request_timeout_header")
                        .addQuery("Request-Query-Id", "btn_request_timeout_header")
                        .addParam("Request-Param-Id", "btn_request_timeout_header")
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .manager(mRequestManager)
                        .async(new UICallback() {
                            @Override
                            public void onUISucceed(Request request, Response response) throws IOException {
                                String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                showData(result);
                            }

                            @Override
                            public void onUIFailure(Request request, IOException e) {
                                System.out.println("onFailure:" + e);
                                TT.show("onFailure:" + e);
                            }
                        });
                break;
            case R.id.btn_request_timeout_global:
                mHttp.setOverallConnectTimeout(5, TimeUnit.SECONDS);
                mHttp.setOverallWriteTimeout(20, TimeUnit.SECONDS);
                mHttp.setOverallReadTimeout(20, TimeUnit.SECONDS);
                mHttp.post(url)
                        .addHeader("Request-Header-Id", "btn_request_timeout_header")
                        .addQuery("Request-Query-Id", "btn_request_timeout_header")
                        .addParam("Request-Param-Id", "btn_request_timeout_header")
                        .manager(mRequestManager)
                        .async(new UICallback() {
                            @Override
                            public void onUISucceed(Request request, Response response) throws IOException {
                                String result = response.body().string();
                                System.out.println("onSucceed:" + result.length());
                                showData(result);
                            }

                            @Override
                            public void onUIFailure(Request request, IOException e) {
                                System.out.println("onFailure:" + e);
                                TT.show("onFailure:" + e);
                            }
                        });
                break;
        }
    }

    private void onCertificateTest(View view) {
        SSLCreator SSLCreator = new SSLCreator.Builder()
                .addCertificateForPlatform()
                .addCertificateForAssets(this, "certs/6xrootca.pem")
                .addCertificateForAssets(this, "certs/isrgrootx1.pem")
                .build();
        openUrl(SSLCreator, "https://www.baidu.com/");
        openUrl(SSLCreator, "https://6xyun.cn/");
        openUrl(SSLCreator, "https://valid-isrgrootx1.letsencrypt.org/");
        openUrl(SSLCreator, "https://revoked-isrgrootx1.letsencrypt.org/");
        openUrl(SSLCreator, "https://expired-isrgrootx1.letsencrypt.org/");
        openUrl(SSLCreator, "https://docker.6xyun.lan/");
    }

    private void openUrl(SSLCreator SSLCreator, String url) {
        new Thread(() -> {
            long beginTime = System.currentTimeMillis();
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                if (httpURLConnection instanceof HttpsURLConnection) {
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpURLConnection;
                    httpsURLConnection.setSSLSocketFactory(SSLCreator.getSSLSocketFactory());
                }

                if (httpURLConnection.getResponseCode() != 200) throw new IOException("Could not retrieve response code from HttpUrlConnection.");

                byte[] bytes = StreamUtil.readStream(httpURLConnection.getInputStream());

                System.out.println("[Http]: ┏━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("[Http]: ┃--> GET " + url);
                System.out.println("[Http]: ┣━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("[Http]: ┃<-- 200 OK " + url + "  (" + (System.currentTimeMillis() - beginTime) + "ms, " + bytes.length + "-byte body)");
                System.out.println("[Http]: ┗━━━━━━━━━━━━━━━━━━━━━━━━━━");
            } catch (Exception e) {
                System.out.println("[Http]: ┏━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("[Http]: ┃--> GET " + url);
                System.out.println("[Http]: ┣━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("[Http]: ┃<-- HTTP FAILED: " + e.getMessage() + " (" + (System.currentTimeMillis() - beginTime) + "ms)");
                System.out.println("[Http]: ┗━━━━━━━━━━━━━━━━━━━━━━━━━━");
            }
        }).start();
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
        mViewbinding.tvLog.setText(data);
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
}
