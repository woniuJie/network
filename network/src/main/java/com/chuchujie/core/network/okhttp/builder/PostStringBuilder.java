package com.chuchujie.core.network.okhttp.builder;

import com.chuchujie.core.network.okhttp.request.PostStringRequest;
import com.chuchujie.core.network.okhttp.request.RequestCall;

import okhttp3.MediaType;

/**
 * Created by wangjing on 16/12/15.
 */
public class PostStringBuilder extends OkHttpRequestBuilder<PostStringBuilder> {

    private String content;

    private MediaType mediaType;

    public PostStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostStringRequest(url, tag, params, headers, content, mediaType, id).build();
    }

}
