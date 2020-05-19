package com.chuchujie.core.network.okhttp.builder;

import android.net.Uri;

import com.chuchujie.core.network.okhttp.request.GetRequest;
import com.chuchujie.core.network.okhttp.request.RequestCall;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangjing on 16/12/15.
 */
public class GetBuilder extends OkHttpRequestBuilder<GetBuilder> implements IRequestParams {

    @Override
    public RequestCall build() {
        if (params != null) {
            url = appendParams(url, params);
        }
        return new GetRequest(url, tag, params, headers, id).build();
    }

    protected String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }

    @Override
    public GetBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public GetBuilder addParams(String key, String value) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, value);
        return this;
    }

}
