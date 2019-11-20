package com.liux.android.glide.video;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Key;

import java.io.File;
import java.net.URL;
import java.security.MessageDigest;

/**
 * 自定义的视频缩略图显示过程
 * https://juejin.im/entry/59b2076ff265da247e7ddbf1
 * Created by Liux on 2017/9/13.
 */

public class Video implements Key {

    public static Video fromUrl(String url) {
        return fromUri(Uri.parse(url));
    }

    public static Video fromFile(File file) {
        return fromUri(Uri.fromFile(file));
    }

    public static Video fromUri(Uri uri) {
        return new Video(uri);
    }

    private Uri uri;

    private int hashCode;
    private volatile byte[] cacheBytes;

    private Video(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        byte[] bytes = getCacheBytes();
        messageDigest.update(bytes);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Video) {
            Video other = (Video) o;
            return getCacheKey().equals(other.getCacheKey());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = getCacheKey().hashCode();
        }
        return hashCode;
    }

    private byte[] getCacheBytes() {
        if(cacheBytes == null) {
            cacheBytes = getCacheKey().getBytes(CHARSET);
        }
        return cacheBytes;
    }

    private String getCacheKey() {
        return "video:" + getUri().toString();
    }
}