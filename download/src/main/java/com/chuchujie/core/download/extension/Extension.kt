package com.chuchujie.core.download.extension

import com.chuchujie.core.download.core.RealMission
import io.reactivex.Maybe

/**
 * Created by wangjing on 2018/3/21.
 */
interface Extension {

    fun init(mission: RealMission)

    fun action(): Maybe<Any>

}