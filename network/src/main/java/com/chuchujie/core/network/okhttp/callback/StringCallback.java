package com.chuchujie.core.network.okhttp.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by wangjing on 16/12/15.
 */
public abstract class StringCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException {
        return response.body().string();
    }
}
