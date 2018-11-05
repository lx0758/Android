package com.liux.android.downloader.network;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 网络连接器
 */
public interface Connect {

    /**
     * 是否已经链接
     * @return
     */
    boolean isConnect();

    /**
     * 链接某个资源
     * @param url
     * @param method
     * @param headers
     * @param needBody
     * @return
     * @throws IOException
     */
    ConnectResponse connect(String url, String method, Map<String, List<String>> headers, boolean needBody) throws IOException;

    /**
     * 关闭链接
     */
    void close();
}
