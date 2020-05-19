package com.chuchujie.core.download.core

import io.reactivex.Flowable
import java.io.File

/**
 * Created by wangjing on 2018/3/21.
 */
abstract class DownloadType(val mission: RealMission) {

    abstract fun initStatus()

    abstract fun isFinish(): Boolean

    abstract fun getFile(): File?

    abstract fun getLastModify(): Long

    abstract fun download(): Flowable<out Status>

    abstract fun delete()

    fun updateDownloadedFileStatus(file: File) {
        mission.status.totalSize = file.length()
        mission.status.downloadSize = file.length()
    }

}