package com.chuchujie.core.download

import com.chuchujie.core.download.core.DownloadCore
import com.chuchujie.core.download.core.Mission
import com.chuchujie.core.download.core.Status
import com.chuchujie.core.download.extension.Extension
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.io.File

/**
 * Created by wangjing on 2018/3/20.
 */
object RxDownload {

    private val downloadCore = DownloadCore()

    fun isExists(url: String): Maybe<Boolean> = isExists(Mission(url))

    fun isExists(mission: Mission): Maybe<Boolean> = downloadCore.isExists(mission)

    fun create(url: String): Flowable<Status> = create(Mission(url))

    fun create(mission: Mission): Flowable<Status> = downloadCore.create(mission)

    fun update(mission: Mission): Maybe<Any> = downloadCore.update(mission)

    fun createAll(missions: List<Mission>): Maybe<Any> = downloadCore.createAll(missions)

    fun start(url: String): Maybe<Any> = start(Mission(url))

    fun start(mission: Mission): Maybe<Any> = downloadCore.start(mission)

    fun startAll(): Maybe<Any> = downloadCore.startAll()

    fun stop(url: String): Maybe<Any> = stop(Mission(url))

    fun stop(mission: Mission): Maybe<Any> = downloadCore.stop(mission)

    fun stopAll(): Maybe<Any> = downloadCore.stopAll()

    fun delete(url: String, deleteFile: Boolean = false): Maybe<Any> =
            delete(Mission(url), deleteFile)

    fun delete(mission: Mission, deleteFile: Boolean = false): Maybe<Any> =
            downloadCore.delete(mission, deleteFile)

    fun deleteAll(deleteFile: Boolean = false): Maybe<Any> = downloadCore.deleteAll(deleteFile)

    fun clear(url: String): Maybe<Any> = clear(Mission(url))

    fun clear(mission: Mission): Maybe<Any> = downloadCore.clear(mission)

    fun clearAll(): Maybe<Any> = downloadCore.clearAll()

    fun getAllMission(): Maybe<List<Mission>> = downloadCore.getAllMission()

    fun file(url: String): Maybe<File> = file(Mission(url))

    fun file(mission: Mission): Maybe<File> = downloadCore.file(mission)

    fun extension(url: String, type: Class<out Extension>): Maybe<Any> =
            extension(Mission(url), type)

    fun extension(mission: Mission, type: Class<out Extension>): Maybe<Any> =
            downloadCore.extension(mission, type)

}