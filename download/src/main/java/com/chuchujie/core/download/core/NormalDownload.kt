package com.chuchujie.core.download.core

import com.chuchujie.core.download.http.HttpFace
import com.chuchujie.core.download.utils.ANY
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.io.File

/**
 * Created by wangjing on 2018/3/27.
 */
class NormalDownload(mission: RealMission) : DownloadType(mission) {

    private val targetFile = NormalTargetFile(mission)

    private val tmpFile = RangeTmpFile(mission)

    override fun initStatus() {
        var status = targetFile.getStatus()
        mission.status = when {
            targetFile.isFinish() -> Succeed(status)
            else -> Normal(status)
        }
    }

    override fun isFinish(): Boolean = targetFile.isFinish()

    override fun getFile(): File? {
        if (targetFile.isFinish()) {
            return targetFile.realFile()
        }
        return null
    }

    override fun getLastModify(): Long = tmpFile.getLastModify()

    override fun download(): Flowable<out Status> {
        if (targetFile.isFinish()) {
            updateDownloadedFileStatus(targetFile.realFile())
            return Flowable.just(mission.status)
        }

        tmpFile.checkFile()
        targetFile.checkFile()

        return Maybe.just(ANY)
                .flatMap { HttpFace.download(mission) }
                .flatMapPublisher { targetFile.save(it) }
    }

    override fun delete() {
        targetFile.delete()
        tmpFile.delete()
    }

}