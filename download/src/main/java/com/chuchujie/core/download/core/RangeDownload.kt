package zlc.season.rxdownload3.core

import com.chuchujie.core.download.core.*
import com.chuchujie.core.download.core.DownloadConfig.maxRange
import com.chuchujie.core.download.http.HttpFace
import com.chuchujie.core.download.utils.d
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import java.io.File


class RangeDownload(mission: RealMission) : DownloadType(mission) {

    private val targetFile = RangeTargetFile(mission)

    private val tmpFile = RangeTmpFile(mission)

    override fun initStatus() {
        val status = tmpFile.currentStatus()

        mission.status = when {
            isFinish() -> Succeed(status)
            else -> Normal(status)
        }
    }

    override fun getFile(): File? {
        if (isFinish()) {
            return targetFile.realFile()
        }
        return null
    }

    override fun getLastModify(): Long = tmpFile.getLastModify()

    override fun delete() {
        targetFile.delete()
        tmpFile.delete()
    }

    override fun isFinish(): Boolean = tmpFile.isFinish() && targetFile.isFinish()

    override fun download(): Flowable<out Status> {
        if (isFinish()) {
            updateDownloadedFileStatus(targetFile.realFile())
            return Flowable.just(mission.status)
        }

        val arrays = mutableListOf<Flowable<Any>>()

        if (targetFile.isShadowExists()) {
            tmpFile.checkFile()
        } else {
            targetFile.createShadowFile()
            tmpFile.reset()
        }

        tmpFile.getSegments()
                .filter { !it.isComplete() }
                .forEach { arrays.add(rangeDownload(it)) }

        return Flowable.mergeDelayError(arrays, maxRange)
                .map { Downloading(tmpFile.currentStatus()) }
                .doOnComplete { targetFile.rename() }
    }


    private fun rangeDownload(segment: RangeTmpFile.Segment): Flowable<Any> {
        return Maybe.just(segment)
                .subscribeOn(Schedulers.io())
                .map { "bytes=${it.current}-${it.end}" }
                .doOnSuccess { d("Range: $it") }
                .flatMap { HttpFace.download(mission, it) }
                .flatMapPublisher { targetFile.save(it, segment, tmpFile) }
    }

}


