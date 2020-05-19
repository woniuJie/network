package com.chuchujie.core.network.retrofit;

import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Scheduler;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 包装模式、装饰模式
 * <p>
 * 全局处理网络异常，比如网络异常上报、解析异常上报。
 * http://bytes.babbel.com/en/articles/2016-03-16-retrofit2-rxjava-error-handling.html
 * Created by wangjing on 2017/7/6.
 */
public class RxJava2CallAdapterFactoryWrapper extends CallAdapter.Factory {

    /**
     * 原始的RxJava2CallAdapterFactory
     */
    private RxJava2CallAdapterFactory original;

    /**
     * 请求出现错误进行拦截
     */
    private RxJava2CallAdapterWrapper.RxJava2CallErrorHandler mRxJava2CallErrorHandler;

    private RxJava2CallAdapterFactoryWrapper() {
    }

    public void setOriginal(RxJava2CallAdapterFactory original) {
        this.original = original;
    }

    public void setRxJava2CallErrorHandler(
            RxJava2CallAdapterWrapper.RxJava2CallErrorHandler rxJava2CallErrorHandler) {
        mRxJava2CallErrorHandler = rxJava2CallErrorHandler;
    }


    public static RxJava2CallAdapterFactoryWrapper create() {
        return create(null);
    }

    public static RxJava2CallAdapterFactoryWrapper createAsync() {
        return createAsync(null);

    }

    public static RxJava2CallAdapterFactoryWrapper createWithScheduler(Scheduler scheduler) {
        return createWithScheduler(scheduler, null);
    }

    public static RxJava2CallAdapterFactoryWrapper create(
            RxJava2CallAdapterWrapper.RxJava2CallErrorHandler rxJava2CallErrorHandler) {
        RxJava2CallAdapterFactoryWrapper instance
                = new RxJava2CallAdapterFactoryWrapper();
        instance.setOriginal(RxJava2CallAdapterFactory.create());
        instance.setRxJava2CallErrorHandler(rxJava2CallErrorHandler);
        return instance;
    }

    public static RxJava2CallAdapterFactoryWrapper createAsync(
            RxJava2CallAdapterWrapper.RxJava2CallErrorHandler rxJava2CallErrorHandler) {
        RxJava2CallAdapterFactoryWrapper instance
                = new RxJava2CallAdapterFactoryWrapper();
        instance.setOriginal(RxJava2CallAdapterFactory.createAsync());
        instance.setRxJava2CallErrorHandler(rxJava2CallErrorHandler);
        return instance;

    }

    public static RxJava2CallAdapterFactoryWrapper createWithScheduler(
            Scheduler scheduler,
            RxJava2CallAdapterWrapper.RxJava2CallErrorHandler rxJava2CallErrorHandler) {
        RxJava2CallAdapterFactoryWrapper instance
                = new RxJava2CallAdapterFactoryWrapper();
        instance.setRxJava2CallErrorHandler(rxJava2CallErrorHandler);
        instance.setOriginal(RxJava2CallAdapterFactory.createWithScheduler(scheduler));
        return instance;
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new RxJava2CallAdapterWrapper(retrofit,
                original.get(type, annotations, retrofit),
                mRxJava2CallErrorHandler);
    }

}
