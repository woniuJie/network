package com.chuchujie.core.network.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 创建OkHttpClient的工厂, 提供默认的OkHttpClient的配置信息
 * Created by wangjing on 12/16/16 1:59 PM.
 */
public class OkHttpClientFactory {

    private static final int DEFAULT_CONNECT_TIMEOUT = 10;

    private static final int DEFAULT_READ_TIMEOUT = 15;

    private static final int DEFAULT_WRITE_TIMEOUT = 15;

    /**
     * OKHttpClient实例
     */
    private OkHttpClient mOkHttpClient;

    private OkHttpClientFactory() {
    }

    /**
     * 初始化
     */
    private void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        mOkHttpClient = builder.build();
    }

    /**
     * 默认的OkHttp
     *
     * @return
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static OkHttpClientFactory create() {
        OkHttpClientFactory okHttpClientFactory = new OkHttpClientFactory();
        okHttpClientFactory.init();
        return okHttpClientFactory;
    }

}
