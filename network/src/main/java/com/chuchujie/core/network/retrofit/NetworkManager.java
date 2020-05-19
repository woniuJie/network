package com.chuchujie.core.network.retrofit;

import android.content.Context;

import java.io.InputStream;

import javax.net.ssl.HostnameVerifier;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * network manager of retrofit & okhttp
 * <p>
 * Created by wangjing on 2017/12/4.
 */
public class NetworkManager {

    private RetrofitManager mRetrofitManager;

    final boolean enableStetho;

    final Context context;

    final RxJava2CallAdapterWrapper.RxJava2CallErrorHandler rxJava2CallErrorHandler;

    final OkHttpClient okHttpClient;

    final HttpLoggingInterceptor.Logger logger;

    final InputStream[] certificates;

    final InputStream bksFile;

    final String password;

    final String[] udpDnsServers;

    final String[] httpDnsServers;

    final boolean strengthenNetwork;

    final HttpLoggingInterceptor.Level logLevel;

    final HostnameVerifier hostnameVerifier;

    final boolean insertUserAgent;

    final boolean insertSessionId;

    final DnsHijackCallback dnsHijackCallback;

    private NetworkManager(Builder builder) {
        this.enableStetho = builder.enableStetho;
        this.context = builder.context;
        this.rxJava2CallErrorHandler = builder.rxJava2CallErrorHandler;
        this.okHttpClient = builder.okHttpClient;
        this.logger = builder.logger;
        this.certificates = builder.certificates;
        this.bksFile = builder.bksFile;
        this.password = builder.password;
        this.udpDnsServers = builder.udpDnsServers;
        this.httpDnsServers = builder.httpDnsServers;
        this.strengthenNetwork = builder.strengthenNetwork;
        this.logLevel = builder.logLevel;
        this.hostnameVerifier = builder.hostnameVerifier;
        this.insertUserAgent = builder.insertUserAgent;
        this.insertSessionId = builder.insertSessionId;
        this.dnsHijackCallback = builder.dnsHijackCallback;
    }

    public RetrofitManager retrofitManager() {
        if (mRetrofitManager == null) {
            mRetrofitManager = new RetrofitManager(this);
        }
        return mRetrofitManager;
    }

    public OkHttpClient okHttpClient() {
        return okHttpClient;
    }

    public Builder newBuilder() {
        return new NetworkManager.Builder(this);
    }

    public static final class Builder {

        boolean enableStetho;

        Context context;

        RxJava2CallAdapterWrapper.RxJava2CallErrorHandler rxJava2CallErrorHandler;

        OkHttpClient okHttpClient;

        HttpLoggingInterceptor.Logger logger;

        InputStream[] certificates;

        InputStream bksFile;

        String password;

        String[] udpDnsServers;

        String[] httpDnsServers;

        boolean strengthenNetwork;

        HttpLoggingInterceptor.Level logLevel;

        HostnameVerifier hostnameVerifier;

        boolean insertUserAgent;

        boolean insertSessionId;

        DnsHijackCallback dnsHijackCallback;

        public Builder() {
            enableStetho = false;
            rxJava2CallErrorHandler = new RxJava2CallAdapterWrapper
                    .DefaultRxJava2CallErrorHandler();
            logger = HttpLoggingInterceptor.Logger.DEFAULT;
            okHttpClient = OkHttpClientFactory.create().getOkHttpClient();
            strengthenNetwork = false;
            logLevel = HttpLoggingInterceptor.Level.BASIC;
            insertUserAgent = true;
            insertSessionId = true;

        }

        Builder(NetworkManager networkManager) {
            enableStetho = networkManager.enableStetho;
            context = networkManager.context;
            rxJava2CallErrorHandler = networkManager.rxJava2CallErrorHandler;
            okHttpClient = networkManager.okHttpClient;
            logger = networkManager.logger;
            certificates = networkManager.certificates;
            bksFile = networkManager.bksFile;
            password = networkManager.password;
            udpDnsServers = networkManager.udpDnsServers;
            httpDnsServers = networkManager.httpDnsServers;
            strengthenNetwork = networkManager.strengthenNetwork;
            logLevel = networkManager.logLevel;
        }

        /**
         * android's context
         *
         * @param context
         * @return
         */
        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        /**
         * update okhttpclient, if you use this method must close
         * {@link Builder#strengthenNetwork strengthenNetwork()}
         *
         * @param okHttpClient
         * @return
         */
        public Builder okHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        /**
         * intercept request all exception while request
         *
         * @param rxJava2CallErrorHandler
         * @return
         */
        public Builder rxJava2CallErrorHandler(
                RxJava2CallAdapterWrapper.RxJava2CallErrorHandler rxJava2CallErrorHandler) {
            this.rxJava2CallErrorHandler = rxJava2CallErrorHandler;
            return this;
        }

        /**
         * implemention to print request log
         *
         * @param logger
         * @return
         */
        public Builder logger(HttpLoggingInterceptor.Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * https certificates
         *
         * @param certificates
         * @return
         */
        public Builder certificates(InputStream[] certificates) {
            this.certificates = certificates;
            return this;
        }

        /**
         * https bks file
         *
         * @param bksFile
         * @return
         */
        public Builder bksFile(InputStream bksFile) {
            this.bksFile = bksFile;
            return this;
        }

        /**
         * certificates passworld
         *
         * @param password
         * @return
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * custom dns, udp dns servers, implements with qiniu/happy-dns
         *
         * @param udpDnsServers
         * @return
         */
        public Builder udpDnsServers(String[] udpDnsServers) {
            this.udpDnsServers = udpDnsServers;
            return this;
        }

        /**
         * custom dns, http dns server, implements with qiniu/happy-dns
         *
         * @param httpDnsServers
         * @return
         */
        public Builder httpDnsServers(String[] httpDnsServers) {
            this.httpDnsServers = httpDnsServers;
            return this;
        }

        /**
         * to update okhttpclient while {@link Builder#build build()} invoke,
         * add dns、https、stetho support
         *
         * @param strengthenNetwork
         * @return
         */
        public Builder strengthenNetwork(boolean strengthenNetwork) {
            this.strengthenNetwork = strengthenNetwork;
            return this;
        }

        /**
         * enable stetho for grab request
         *
         * @param enableStetho
         * @return
         */
        public Builder enableStetho(boolean enableStetho) {
            this.enableStetho = enableStetho;
            return this;
        }

        /**
         * set HttpLoggingInterceptor print log level
         *
         * @param logLevel
         * @return
         */
        public Builder logLevel(HttpLoggingInterceptor.Level logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        /**
         * https hostname verifier
         *
         * @param hostnameVerifier
         * @return
         */
        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        /**
         * insert user agent to http request header
         *
         * @param insertUserAgent
         * @return
         */
        public Builder insertUserAgent(boolean insertUserAgent) {
            this.insertUserAgent = insertUserAgent;
            return this;
        }

        /**
         * insert sessionId to http request header
         *
         * @param insertSessionId
         * @return
         */
        public Builder insertSessionId(boolean insertSessionId) {
            this.insertSessionId = insertSessionId;
            return this;
        }

        /**
         * set dns hijack callback. must strengthenNetwork=true or
         * self implement okhttp's dns interface
         *
         * @param dnsHijackCallback
         * @return
         */
        public Builder dnsHijackCallback(DnsHijackCallback dnsHijackCallback) {
            this.dnsHijackCallback = dnsHijackCallback;
            return this;
        }

        /**
         * create {@link NetworkManager} instance
         *
         * @return
         */
        public NetworkManager build() {
            // update okhttpclient
            if (strengthenNetwork) {
                okHttpClient = new OkHttpClientWrapper(this).getOkHttpClient();
            }
            return new NetworkManager(this);
        }

    }

}
