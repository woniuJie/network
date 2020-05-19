package com.chuchujie.core.network.retrofit;


import java.lang.reflect.Type;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * 拦截RxJava的异常，但不中断rxjava事件传递，同事处理解析异常的上报
 * Created by wangjing on 2017/6/5.
 */
public class RxJava2CallAdapterWrapper<R> implements CallAdapter<R, Object> {

    private final Retrofit mRetrofit;

    private final CallAdapter mWrapperCallAdapter;

    private final RxJava2CallErrorHandler mRxJava2CallErrorHandler;

    public RxJava2CallAdapterWrapper(Retrofit retrofit,
                                     CallAdapter callAdapter) {
        this(retrofit, callAdapter, null);
    }

    public RxJava2CallAdapterWrapper(Retrofit retrofit,
                                     CallAdapter callAdapter,
                                     RxJava2CallErrorHandler rxJava2CallErrorHandler) {
        this.mRetrofit = retrofit;
        this.mWrapperCallAdapter = callAdapter;
        this.mRxJava2CallErrorHandler = rxJava2CallErrorHandler;
    }

    @Override
    public Type responseType() {
        return mWrapperCallAdapter.responseType();
    }

    /**
     * CallAdapter<R, Object>
     * 这个包装将第二个泛型给抹掉了，所以暂时只能兼容了
     *
     * @param call
     * @return
     */
    @Override
    public Object adapt(final Call<R> call) {

        Object result = null;

        Object adapt = mWrapperCallAdapter.adapt(call);

        if (adapt instanceof Observable) {
            result = ((Observable) adapt)
                    .onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                        @Override
                        public ObservableSource apply(@NonNull Throwable throwable) throws Exception {
                            interceptorError(throwable, call);
                            return Observable.error(throwable);
                        }
                    });
        } else if (adapt instanceof Flowable) {
            result = ((Flowable) adapt)
                    .onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                        @Override
                        public ObservableSource apply(@NonNull Throwable throwable) throws Exception {
                            interceptorError(throwable, call);
                            return Observable.error(throwable);
                        }
                    });

        } else if (adapt instanceof Maybe) {
            result = ((Maybe) adapt)
                    .onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                        @Override
                        public ObservableSource apply(@NonNull Throwable throwable) throws Exception {
                            interceptorError(throwable, call);
                            return Observable.error(throwable);
                        }
                    });
        } else if (adapt instanceof Single) {
            result = ((Single) adapt)
                    .onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                        @Override
                        public ObservableSource apply(@NonNull Throwable throwable) throws Exception {
                            interceptorError(throwable, call);
                            return Observable.error(throwable);
                        }
                    });
        } else {    // 最差的情况，不处理异常解析的回调了
            result = adapt;
        }
        return result;
    }

    /**
     * 拦截处理一些异常
     *
     * @param throwable
     * @param call
     */
    private void interceptorError(Throwable throwable, Call<R> call) {
        if (mRxJava2CallErrorHandler != null) {
            mRxJava2CallErrorHandler.handleNetworkError(throwable, mRetrofit, call);
        }
    }

    public interface RxJava2CallErrorHandler {

        void handleNetworkError(Throwable throwable, Retrofit retrofit, Call call);

    }

    public static class DefaultRxJava2CallErrorHandler implements RxJava2CallErrorHandler {

        @Override
        public void handleNetworkError(Throwable throwable, Retrofit retrofit, Call call) {

        }

    }

}
