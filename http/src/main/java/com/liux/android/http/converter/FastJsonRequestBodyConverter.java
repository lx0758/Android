package com.liux.android.http.converter;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

public class FastJsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain;charset=UTF-8");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=UTF-8");

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
        if (!(value instanceof JSON)) {
            return RequestBody.create(MEDIA_TYPE_TEXT, String.valueOf(value));
        }
        byte[] content;
        if(this.serializeConfig != null && this.serializerFeatures != null) {
            content = JSON.toJSONBytes(value, this.serializeConfig, this.serializerFeatures);
        } else if(this.serializeConfig != null) {
            content = JSON.toJSONBytes(value, this.serializeConfig);
        } else if(this.serializerFeatures != null) {
            content = JSON.toJSONBytes(value, this.serializerFeatures);
        } else {
            content = JSON.toJSONBytes(value);
        }
        return RequestBody.create(MEDIA_TYPE_JSON, content);
    }
}

