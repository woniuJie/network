package com.chuchujie.core.download.http

import okhttp3.OkHttpClient

/**
 * Created by wangjing on 2018/3/27.
 */
interface OkHttpClientFactory {

    fun build(): OkHttpClient

}