package com.liux.android.http.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;

public class FastJsonConverterFactory extends Factory {

    public static FastJsonConverterFactory create() {
        return new FastJsonConverterFactory();
    }

    private ParserConfig parserConfig = ParserConfig.getGlobalInstance();
    private int featureValues = JSON.DEFAULT_PARSER_FEATURE;
    private Feature[] features = new Feature[] {
            Feature.InitStringFieldAsEmpty,
            Feature.SupportArrayToBean,
            Feature.SupportNonPublicField,
            Feature.SupportAutoType
    };

    private SerializeConfig serializeConfig = SerializeConfig.getGlobalInstance();
    private SerializerFeature[] serializerFeatures = new SerializerFeature[] {
            SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullNumberAsZero,
            SerializerFeature.WriteNullStringAsEmpty
    };

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new FastJsonResponseBodyConverter<>(type, this.parserConfig, this.featureValues, this.features);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new FastJsonRequestBodyConverter<>(type, this.serializeConfig, this.serializerFeatures);
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return super.stringConverter(type, annotations, retrofit);
    }

    public ParserConfig getParserConfig() {
        return parserConfig;
    }

    public FastJsonConverterFactory setParserConfig(ParserConfig parserConfig) {
        this.parserConfig = parserConfig;
        return this;
    }

    public int getParserFeatureValues() {
        return featureValues;
    }

    public FastJsonConverterFactory setParserFeatureValues(int featureValues) {
        this.featureValues = featureValues;
        return this;
    }

    public Feature[] getParserFeatures() {
        return features;
    }

    public FastJsonConverterFactory setParserFeatures(Feature[] features) {
        this.features = features;
        return this;
    }

    public SerializeConfig getSerializeConfig() {
        return serializeConfig;
    }

    public FastJsonConverterFactory setSerializeConfig(SerializeConfig serializeConfig) {
        this.serializeConfig = serializeConfig;
        return this;
    }

    public SerializerFeature[] getSerializerFeatures() {
        return serializerFeatures;
    }

    public FastJsonConverterFactory setSerializerFeatures(SerializerFeature[] serializerFeatures) {
        this.serializerFeatures = serializerFeatures;
        return this;
    }
}