package com.liux.android.http.request;

import java.io.File;
import java.io.IOException;

public interface DownloadCallback {

    void onProgress(long transmittedLength, long totalLength);

    void onSucceed(File file);

    void onFailure(IOException e);
}
