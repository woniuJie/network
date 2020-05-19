package com.chuchujie.core.network.okhttp.interceptor;

import android.text.TextUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Http请求重试拦截器, 使用http请求的tag来去掉重试机制，如果需要去掉重试某个请求的重试，
 * 必须在请求的时候加上tag，并添加到这个黑名单中
 * <p>
 * Created by wangjing on 2017/2/15.
 */
public class RetryInterceptor implements Interceptor {

    /**
     * 最大的重试次数
     */
    private final int mMaxRetryCount;

    /**
     * 不重试的黑名单
     */
    private final Set<String> mBlackList;

    private RetryInterceptor(Builder builder) {
        this.mMaxRetryCount = builder.maxRetryCount;
        this.mBlackList = builder.blackList;
    }

    @Override
    public Response intercept(Chain chain) throws IOException { // 请求重试
        // 重试,下单怎么处理
        Request request = chain.request();
        Response response = chain.proceed(request);
        // 黑名单中的url，不参与重试
        if (!mBlackList.contains(request.tag().toString())) {
            int tryCount = 0;
            while (!response.isSuccessful() && tryCount < mMaxRetryCount) {
                tryCount++;
                response = chain.proceed(request);
            }
        }
        return response;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static final class Builder {

        private static final int DEFAULT_MAX_TRY_COUNT = 1;

        private int maxRetryCount;

        private Set<String> blackList;

        public Builder() {
            maxRetryCount = DEFAULT_MAX_TRY_COUNT;
            blackList = new HashSet<>();
        }

        public Builder(RetryInterceptor retryInterceptor) {
            this.maxRetryCount = retryInterceptor.mMaxRetryCount;
            this.blackList = retryInterceptor.mBlackList;
        }

        /**
         * 添加黑名单
         *
         * @param url
         */
        public Builder addBlackList(String url) {
            if (!TextUtils.isEmpty(url)) {
                blackList.add(url);
            }
            return this;
        }

        /**
         * 添加黑名单
         *
         * @param urls
         */
        public Builder addBlackList(List<String> urls) {
            if (urls == null) {
                blackList.addAll(urls);
            }
            return this;
        }

        /**
         * 添加黑名单
         *
         * @param urls
         */
        public Builder addBlackList(String[] urls) {
            if (urls == null) {
                blackList.addAll(Arrays.asList(urls));
            }
            return this;
        }

        /**
         * 设置最大的重试次数
         *
         * @param maxRetryCount
         */
        public Builder setMaxRetryCount(int maxRetryCount) {
            this.maxRetryCount = maxRetryCount;
            return this;
        }

        /**
         * 构建重试器
         *
         * @return
         */
        public RetryInterceptor build() {
            return new RetryInterceptor(this);
        }

    }

}
