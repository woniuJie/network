package com.chuchujie.core.download.core

import io.reactivex.Flowable
import io.reactivex.functions.Function
import org.reactivestreams.Publisher
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Created by wangjing on 2018/4/26.
 */
class RetryDownloadFunction(private var maxRetryCount: Int, private var delayTime: Int)
    : Function<Flowable<Throwable>, Publisher<*>> {

    private var currentRetryTime: Int = 0

    @Throws(Exception::class)
    override fun apply(flowable: Flowable<Throwable>): Publisher<*> {
        return flowable.flatMap(Function<Throwable, Publisher<*>> { throwable ->
            if (currentRetryTime < maxRetryCount
                    && (throwable is TimeoutException || throwable is IOException)) {
                currentRetryTime++
                return@Function Flowable.timer(delayTime.toLong(), TimeUnit.MILLISECONDS)
            }
            Flowable.error<Any>(throwable)
        })
    }

}