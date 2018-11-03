package com.liux.android.http.converter;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

public class FastJsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final SerializerFeature[] EMPTY_SERIALIZER_FEATURES = new SerializerFeature[0];
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain; charset=UTF-8");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=UTF-8");

    private Type type;
    private SerializeConfig serializeConfig;
    private SerializerFeature[] serializerFeatures;

    FastJsonRequestBodyConverter(Type type, SerializeConfig config, SerializerFeature... features) {
        this.type = type;
        this.serializeConfig = config;
        this.serializerFeatures = features;
    }

    @Override
    public RequestBody convert(@NonNull T value) throws IOException {
        byte[] content;
        MediaType mediaType;

        if (this.type != JSON.class) {
            mediaType = MEDIA_TYPE_TEXT;
            content = String.valueOf(value).getBytes();
        } else {
            mediaType = MEDIA_TYPE_JSON;
            if(this.serializeConfig != null) {
                if(this.serializerFeatures != null) {
                    content = JSON.toJSONBytes(value, this.serializeConfig, this.serializerFeatures);
                } else {
                    content = JSON.toJSONBytes(value, this.serializeConfig, EMPTY_SERIALIZER_FEATURES);
                }
            } else if(this.serializerFeatures != null) {
                content = JSON.toJSONBytes(value, this.serializerFeatures);
            } else {
                content = JSON.toJSONBytes(value, EMPTY_SERIALIZER_FEATURES);
            }
        }

        return RequestBody.create(mediaType, content);
    }
}

