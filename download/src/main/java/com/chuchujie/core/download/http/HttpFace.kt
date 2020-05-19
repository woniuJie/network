package com.chuchujie.core.download.http

import com.chuchujie.core.download.core.DownloadConfig
import com.chuchujie.core.download.core.RealMission
import com.chuchujie.core.download.core.RetryDownloadFunction
import com.chuchujie.core.download.utils.ANY
import com.chuchujie.core.download.utils.GMTToLong
import com.chuchujie.core.download.utils.lastModify
import io.reactivex.Maybe
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by wangjing on 2018/3/27.
 */
object HttpFace {

    private val TEST_RANGE_SUPPORT = "bytes=0-"
    private val api: DownloadApi = RetrofitFactory().build()!!.create(DownloadApi::class.java)

    fun checkRangeSupport(mission: RealMission): Maybe<Any> {
        return api.head(TEST_RANGE_SUPPORT, mission.actual.url)
                .flatMap {
                    mission.setup(it)
                    Maybe.just(ANY)
                }
    }

    fun download(mission: RealMission, range: String = ""): Maybe<Response<ResponseBody>> {
        return api.download(range, mission.actual.url)
                .doOnSuccess {
                    if (!it.isSuccessful) {
                        throw RuntimeException(it.message())
                    }
                }
                .retryWhen(RetryDownloadFunction(DownloadConfig.maxDownloadRetryCount,
                        DownloadConfig.retryDownloadWaitTime))
    }

    fun checkModify(mission: RealMission): Maybe<Any> {
        return api
                .headWithIfRange(TEST_RANGE_SUPPORT, mission.lastModify.toString(),
                        mission.actual.url)
                .flatMap {
                    if (!it.isSuccessful) {
                        throw RuntimeException(it.message())
                    }
                    if (mission.lastModify == GMTToLong(lastModify(it))) {
                        mission.setup(it, true)
                    } else {
                        mission.setup(it, true, true)
                    }
                    Maybe.just(ANY)
                }
    }

}