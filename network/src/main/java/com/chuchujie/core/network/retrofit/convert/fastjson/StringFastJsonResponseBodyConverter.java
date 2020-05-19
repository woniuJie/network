package com.chuchujie.core.network.retrofit.convert.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.chuchujie.core.network.retrofit.convert.StringJsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by yxb on 2018/7/18.
 */
final class StringFastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final Feature[] EMPTY_SERIALIZER_FEATURES = new Feature[0];

    private Type mType;

    private ParserConfig config;
    private int featureValues;
    private Feature[] features;

    StringFastJsonResponseBodyConverter(Type type, ParserConfig config, int featureValues,
                                        Feature... features) {
        mType = type;
        this.config = config;
        this.featureValues = featureValues;
        this.features = features;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String valueString = "";
        try {
            valueString = value.string();
            return JSON.parseObject(valueString, mType, config, featureValues,
                    features != null ? features : EMPTY_SERIALIZER_FEATURES);
        } catch (JSONException e) {
            throw new StringJsonParseException(e.getMessage(),e,valueString);
        } finally {
            value.close();
        }
    }
}

