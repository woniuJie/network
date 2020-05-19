package com.chuchujie.core.network.okhttp.callback;

import com.alibaba.fastjson.JSON;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by wangjing on 2017/2/10.
 */
public abstract class ParseCallback<T> extends Callback<T> {

    private Class<T> mTargetObjectClass;

    public ParseCallback(Class<T> targetObjectClass) {
        mTargetObjectClass = targetObjectClass;
    }

    public void setTargetObjectClass(Class<T> targetObjectClass) {
        mTargetObjectClass = targetObjectClass;
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            try {
                return JSON.parseObject(responseBody.string(), mTargetObjectClass);
            } catch (Exception e) {
                // 上报JSON解析异常
//                ReportNetworkErrorUtils.ReportNetworkErrorInfo errorInfo =
//                        new ReportNetworkErrorUtils.ReportNetworkErrorInfo();
//                errorInfo.setUrl(response.request().url().toString());
//                errorInfo.setCode(response.code());
//                errorInfo.setBodyString(response.body().string());
//                errorInfo.setMessage(e != null ? e.getMessage() : "");
//                errorInfo.setSendRequestAtMillis(response.sentRequestAtMillis());
//                errorInfo.setReceiveResponseAtMillis(response.receivedResponseAtMillis());
//                errorInfo.setLength(response.body().contentLength());
//                ReportNetworkErrorUtils.getInstance()
//                        .handleException(errorInfo, false, true);
                throw e;
            }
        }
        return null;
    }

    /**
     * 默认回调器
     */
    public static class DefaultCallback<T> extends ParseCallback<T> {

        private static final String TAG = DefaultCallback.class.getSimpleName();

        public DefaultCallback(Class<T> targetObjectClass) {
            super(targetObjectClass);
        }

        @Override
        public void onResponse(T response, int id) {
        }

        @Override
        public void onError(Call call, Exception exception, int id) {
        }

    }

}
