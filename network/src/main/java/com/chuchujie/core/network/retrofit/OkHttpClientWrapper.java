package com.chuchujie.core.network.retrofit;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 创建OkHttpClient, 提供默认的OkHttpClient的配置信息,
 * 默认放行所有https，不进行证书验证
 * Created by wangjing on 12/16/16 1:59 PM.
 */
public class OkHttpClientWrapper {

    /**
     * OKHttpClient实例
     */
    private OkHttpClient mOkHttpClient;

    public OkHttpClientWrapper(NetworkManager.Builder networkManagerBuilder) {
        // 日志配置
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                networkManagerBuilder.logger);
        loggingInterceptor.setLevel(networkManagerBuilder.logLevel);

        // 证书校验设置
        SslSocketFactoryGenerator.SSLParams sslSocketFactory = SslSocketFactoryGenerator
                .getSslSocketFactory(networkManagerBuilder.certificates,
                        networkManagerBuilder.bksFile, networkManagerBuilder.password);

        OkHttpClient.Builder builder = networkManagerBuilder.okHttpClient.newBuilder();

        // https校验
        builder.sslSocketFactory(sslSocketFactory.sSLSocketFactory, sslSocketFactory.trustManager);

        // 支持Https域名校验
        HostnameVerifier hostnameVerifier = null;
        if (networkManagerBuilder.hostnameVerifier == null) {
            hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
        }
        builder.hostnameVerifier(hostnameVerifier);

        // 添加Header
        builder.addInterceptor(new HeaderInterceptor(
                networkManagerBuilder.insertUserAgent, networkManagerBuilder.insertSessionId));

        // 添加dns处理
        builder.dns(new DefaultDnsImpl(
                networkManagerBuilder.context,
                networkManagerBuilder.udpDnsServers,
                networkManagerBuilder.httpDnsServers,
                networkManagerBuilder.logger,
                networkManagerBuilder.dnsHijackCallback));

        // 日志打印
        builder.addInterceptor(loggingInterceptor);

        if (networkManagerBuilder.enableStetho) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
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

}
