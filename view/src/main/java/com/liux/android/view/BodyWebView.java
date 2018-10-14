package com.liux.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Liux on 2017/9/5.
 */

public class BodyWebView extends WebView {

    public BodyWebView(Context context) {
        super(context);
        initSetting();
    }

    public BodyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSetting();
    }

    public BodyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSetting();
    }

    /**
     * 载入 Body 数据
     * @param body
     */
    public void loadBodyData(String body) {
        loadBodyData(null, body, null);
    }

    /**
     * 载入 Body 数据
     * @param baseUrl
     * @param body
     */
    public void loadBodyData(String baseUrl, String body) {
        loadBodyData(baseUrl, body, null);
    }

    /**
     * 载入 Body 数据
     * @param baseUrl
     * @param body
     * @param encoding
     */
    public void loadBodyData(String baseUrl, String body, String encoding) {
        if (baseUrl == null || baseUrl.length() == 0) {
            baseUrl = "about:blank";
        }
        String html = getHtmlData(body);
        if (encoding == null || encoding.length() == 0) {
            encoding = "utf-8";
        }
        loadDataWithBaseURL(baseUrl, html, "text/html; charset=" + encoding, encoding, null);
    }

    private void initSetting() {
        WebSettings webSettings = getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        webSettings.setDatabaseEnabled (true);
        webSettings.setDomStorageEnabled (true);
        webSettings.setGeolocationEnabled (true);

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    private String getHtmlData(String bodyHTML) {
        if (bodyHTML == null) bodyHTML = "";
        String head = "<head>" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                        "<style>img{max-width: 100%; width:auto; height:auto;}</style>" +
                        "</head>";
        return "<html>" +
                        head +
                        "<body>" +
                        bodyHTML +
                        "</body>" +
                        "</html>";
    }
}
