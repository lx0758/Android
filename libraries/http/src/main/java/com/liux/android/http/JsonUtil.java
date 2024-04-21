package com.liux.android.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @NonNull
    public static ObjectMapper getDefaultObjectMapper() {
        return OBJECT_MAPPER;
    }

    @Nullable
    public static String toJson(@Nullable Object object) {
        try {
            return getDefaultObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static <T> T toBean(@Nullable String string, @NonNull Class<T> clazz) {
        try {
            return getDefaultObjectMapper().readValue(string, clazz);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static <T> T toBean(@Nullable String string, @NonNull TypeReference<T> typeReference) {
        try {
            return getDefaultObjectMapper().readValue(string, typeReference);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
