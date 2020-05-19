package com.chuchujie.core.download.http

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by wangjing on 2018/3/20.
 */
class RetrofitFactory {

    private val FAKE_BASE_URL = "http://www.example.com/"

    private var mRetrofit: Retrofit? = null

    private val mOkHttpClientFactory: OkHttpClientFactoryImpl = OkHttpClientFactoryImpl()

    fun build(baseUrl: String = FAKE_BASE_URL): Retrofit? {
        if (mRetrofit == null) {
            val builder = Retrofit.Builder()
            builder.baseUrl(baseUrl)
            builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            builder.addConverterFactory(GsonConverterFactory.create())
            builder.client(mOkHttpClientFactory.build())
            mRetrofit = builder.build()
        }
        return mRetrofit
    }

}