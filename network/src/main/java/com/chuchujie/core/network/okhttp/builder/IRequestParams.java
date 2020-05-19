package com.chuchujie.core.network.okhttp.builder;

import java.util.Map;

/**
 * Created by wangjing on 16/12/15.
 */
public interface IRequestParams {

    /**
     * 多个param参数添加
     *
     * @param params
     *
     * @return
     */
    OkHttpRequestBuilder params(Map<String, String> params);

    /**
     * 单个param参数添加
     *
     * @param key
     * @param value
     *
     * @return
     */
    OkHttpRequestBuilder addParams(String key, String value);

}
