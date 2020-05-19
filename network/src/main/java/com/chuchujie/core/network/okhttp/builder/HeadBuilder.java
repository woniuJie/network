package com.chuchujie.core.network.okhttp.builder;

import com.chuchujie.core.network.okhttp.request.OtherRequest;
import com.chuchujie.core.network.okhttp.request.RequestCall;
import com.chuchujie.core.network.okhttp.utils.OkHttpUtils;

/**
 * Created by wangjing on 16/12/15.
 */
public class HeadBuilder extends GetBuilder {

    @Override
    public RequestCall build() {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers, id).build();
    }

}
