package com.liux.android.mediaer.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.liux.android.mediaer.glide.video.VideoModelLoader;
import com.liux.android.mediaer.glide.video.Video;

import java.io.InputStream;

import okhttp3.Call;

/**
 * Glide全局配置 <br>
 * 定义缓存参数 <br>
 * 使用全局OkHttpClient <br>
 * Created by Liux on 2017/7/18.
 */

@GlideModule
public class GlideModuleConfig extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder glideBuilder) {
        /* 自定义内存和图片池大小 */
        // 取1/8最大内存作为最大缓存
        int memorySize = (int) (Runtime.getRuntime().maxMemory()) / 8;
        glideBuilder.setMemoryCache(new LruResourceCache(memorySize));
        glideBuilder.setBitmapPool(new LruBitmapPool(memorySize));

        /* 定义SD卡缓存大小和位置 */
        int diskSize = 50 * 1024 * 1024;
        // /sdcard/Android/data/<application package>/cache/glide/
        glideBuilder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, "glide", diskSize));

        /* 默认内存和图片池大小 */
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
        // 默认内存大小
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        // 默认图片池大小
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        // 该两句无需设置，是默认的
        glideBuilder.setMemoryCache(new LruResourceCache(defaultMemoryCacheSize));
        glideBuilder.setBitmapPool(new LruBitmapPool(defaultBitmapPoolSize));

        /* 定义图片格式 */
        glideBuilder.setDefaultRequestOptions(
                new RequestOptions()
                        .centerCrop()
                        .format(DecodeFormat.PREFER_RGB_565)
        );
    }

    /**
     * 注册源 Model 对应的 ModelLoader
     * @param context
     * @param glide
     * @param registry
     */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // 注册全局OkHttp客户端(Http先于Glide初始化完成的情况下会使用)
        Object httpClient = getHttpClient();
        if (httpClient != null) {
            Call.Factory callFactory = (Call.Factory) httpClient;
            OkHttpUrlLoader.Factory loaderFactory = new OkHttpUrlLoader.Factory(callFactory);
            registry.replace(GlideUrl.class, InputStream.class, loaderFactory);
        }

        // 注册视频获取缩略图的扩展
        VideoModelLoader.Factory loaderFactory = new VideoModelLoader.Factory(context);
        registry.append(Video.class, InputStream.class, loaderFactory);
    }

    /**
     * 清单解析是否开启 <br>
     * 要注意避免重复添加 <br>
     * @return
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    /**
     * 通过反射获取Http实例
     * @return
     */
    private Object getHttpClient() {
        try {
            return com.liux.android.http.Http.get().getOkHttpClient();
        } catch (Exception e) {
            return null;
        }
    }
}
