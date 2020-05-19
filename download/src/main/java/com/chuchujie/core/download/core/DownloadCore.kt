package com.chuchujie.core.download.core

import com.chuchujie.core.download.extension.Extension
import com.chuchujie.core.download.utils.e
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.plugins.RxJavaPlugins
import java.io.File
import java.io.InterruptedIOException
import java.net.SocketException

/**
 * Created by wangjing on 2018/3/23.
 */
class DownloadCore {

    private val missionBox = DownloadConfig.missionBox

    init {
        RxJavaPlugins.setErrorHandler {
            when (it) {
                is InterruptedException -> e("InterruptedException", it)
                is InterruptedIOException -> e("InterruptedIOException", it)
                is SocketException -> e("SocketException", it)
            }
        }
    }

    fun create(mission: Mission): Flowable<Status> = missionBox.create(mission)

    fun createAll(missions: List<Mission>): Maybe<Any> = missionBox.createAll(missions)

    fun start(mission: Mission): Maybe<Any> = missionBox.start(mission)

    fun startAll(): Maybe<Any> = missionBox.startAll()

    fun stop(mission: Mission): Maybe<Any> = missionBox.stop(mission)

    fun stopAll(): Maybe<Any> = missionBox.stopAll()

    fun delete(mission: Mission, deleteFile: Boolean): Maybe<Any> =
            missionBox.delete(mission, deleteFile)

    fun deleteAll(deleteFile: Boolean): Maybe<Any> = missionBox.deleteAll(deleteFile)

    fun file(mission: Mission): Maybe<File> = missionBox.file(mission)

    fun extension(mission: Mission, type: Class<out Extension>): Maybe<Any> =
            missionBox.extension(mission, type)

    fun getAllMission(): Maybe<List<Mission>> {
        return if (DownloadConfig.enableDb) {
            val dbActor = DownloadConfig.dbActor
            dbActor.getAllMission()
        } else {
            Maybe.just(emptyList())
        }
    }

    fun isExists(mission: Mission): Maybe<Boolean> = missionBox.isExists(mission)

    fun update(mission: Mission): Maybe<Any> = missionBox.update(mission)

    fun clear(mission: Mission): Maybe<Any> = missionBox.clear(mission)

    fun clearAll(): Maybe<Any> = missionBox.clearAll()

}