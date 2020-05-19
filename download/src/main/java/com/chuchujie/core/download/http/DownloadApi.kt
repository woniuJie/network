package com.chuchujie.core.download.http

import io.reactivex.Flowable
import io.reactivex.Maybe
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by wangjing on 2018/3/19.
 */
interface DownloadApi {


    @GET
    @Streaming
    fun download(@Header("Range") range: String?,
                 @Url url: String): Maybe<Response<ResponseBody>>


    @GET
    fun getWithIfRange(@Header("Range") range: String,
                       @Header("If-Range") lastModify: String,
                       @Url url: String): Flowable<Response<Void>>

    @HEAD
    fun head(@Header("Range") range: String,
             @Url url: String): Maybe<Response<Void>>


    @HEAD
    fun headWithIfRange(@Header("Range") range: String,
                        @Header("If-Range") lastModify: String,
                        @Url url: String): Maybe<Response<Void>>

}