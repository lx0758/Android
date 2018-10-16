package com.liux.android.downloader.network;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 定义一个网络连接
 */
public interface Connect {

    void load(String url, String method, Map<String, List<String>> header);

    boolean isExecuted();

    ConnectResult execute() throws IOException;

    boolean isCanceled();

    void cancel();
}
