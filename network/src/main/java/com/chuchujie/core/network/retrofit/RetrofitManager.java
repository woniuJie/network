package com.chuchujie.core.network.retrofit;

import android.text.TextUtils;

import com.chuchujie.core.network.retrofit.convert.fastjson.StrignFastJsonConverterFactory;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 使用Retrofit请求管理器.帮助创建Retrofit对象,用于网络请求.
 * <p>
 * 如何公共处理一些异常（比如做一些统计性的业务逻辑，如果每个错误都到各自的onError()中去处理，
 * 这样会疯掉的，代码维护困难，开发也困难），但是又不能不断事件的传递呢？从retrofit的Issues中找到了解决方案
 * <p>
 * https://github.com/square/retrofit/issues/1102
 * http://bytes.babbel.com/en/articles/2016-03-16-retrofit2-rxjava-error-handling.html
 * <p>
 * Created by wangjing on 12/15/16 4:44 PM.
 */
public class RetrofitManager {

    /**
     * 缓存Retrofit,防止重复创建
     */
    private Map<String, Retrofit> mCachedRetrofit = new HashMap<>();

    private NetworkManager mRetrofitManager;

    /**
     * 构造函数
     *
     * @param networkManager
     */
    public RetrofitManager(NetworkManager networkManager) {
        this.mRetrofitManager = networkManager;
    }

    /**
     * 清除缓存的Retrofit
     *
     * @return
     */
    public RetrofitManager clearRetrofit() {
        mCachedRetrofit.clear();
        return this;
    }

    /**
     * 获取到最终的retrofit
     *
     * @param baseUrl
     * @return
     */
    private Retrofit getRetrofit(String baseUrl) {
        if (TextUtils.isEmpty(baseUrl))
            throw new NullPointerException("base url can't be null.");
        Retrofit retrofit = mCachedRetrofit.get(baseUrl);
        if (retrofit == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactoryWrapper
                            .create(mRetrofitManager.rxJava2CallErrorHandler))
                    /*
                     * 通过加入ScalarsConverterFactory解决上传文件时参数中带引号的问题。
                     *
                     * 造成的主要原因有两个:
                     * 1、retrofit并不内置String的Converter，只有在Url、Header、普通表单字段相关的注解才会默认处理成String
                     * 2、注册了GsonConverter，而GsonConverter是不会判断能不能处理该类型的，全部转成json，而String在json里就是 "String"的形式
                     *
                     * http://blog.csdn.net/huweijian5/article/details/52610093
                     * https://segmentfault.com/q/1010000009737456
                     *
                     * 参考retrofit源码中Retrofit.nextRequestBodyConverter()，必须先设置
                     * ScalarsConverterFactory然后在设置FastJsonConverterFactory，否则会导致@Part无效
                     *
                     */
                    .addConverterFactory(ScalarsConverterFactory.create())
//                    .addConverterFactory(FastJsonConverterFactory.create())
                    .addConverterFactory(StrignFastJsonConverterFactory.create())
                    .client(mRetrofitManager.okHttpClient);
            retrofit = builder.build();
            mCachedRetrofit.put(baseUrl, retrofit);
        }

        return retrofit;
    }

    /**
     * 创建接口请求对象
     *
     * @param baseUrl
     * @param service
     * @param <T>
     * @return
     */
    public <T> T getService(String baseUrl, Class<T> service) {
        return getRetrofit(baseUrl).create(service);
    }

}
