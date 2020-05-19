package com.chuchujie.core.download.core

import com.chuchujie.core.download.extension.Extension
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.io.File

/**
 * Created by wangjing on 2018/3/23.
 */
interface MissionBox {

    fun isExists(mission: Mission): Maybe<Boolean>

    fun create(mission: Mission): Flowable<Status>

    fun update(newMission: Mission): Maybe<Any>

    fun start(mission: Mission): Maybe<Any>

    fun stop(mission: Mission): Maybe<Any>

    fun delete(mission: Mission, deleteFile: Boolean): Maybe<Any>

    fun clear(mission: Mission): Maybe<Any>

    fun createAll(missions: List<Mission>): Maybe<Any>

    fun startAll(): Maybe<Any>

    fun stopAll(): Maybe<Any>

    fun deleteAll(deleteFile: Boolean): Maybe<Any>

    fun clearAll(): Maybe<Any>

    fun file(mission: Mission): Maybe<File>

    fun extension(mission: Mission, type: Class<out Extension>): Maybe<Any>

}