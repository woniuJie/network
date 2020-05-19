package com.chuchujie.core.download.database

import com.chuchujie.core.download.core.Mission
import com.chuchujie.core.download.core.RealMission
import io.reactivex.Maybe

/**
 * Created by wangjing on 2018/3/21.
 */
interface DbActor {

    fun init()

    fun isExists(mission: RealMission): Boolean

    fun create(mission: RealMission)

    fun read(mission: RealMission)

    fun update(mission: RealMission)

    fun updateStatus(mission: RealMission)

    fun delete(mission: RealMission)

    fun getAllMission(): Maybe<List<Mission>>


}