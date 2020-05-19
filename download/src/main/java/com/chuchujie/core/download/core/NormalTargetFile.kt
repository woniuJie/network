package com.chuchujie.core.download.core


import com.chuchujie.core.download.core.DownloadConfig.DOWNLOADING_FILE_SUFFIX
import com.chuchujie.core.download.utils.isChunked
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Flowable
import okhttp3.ResponseBody
import okio.Okio
import retrofit2.Response
import java.io.File
import java.io.File.separator
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * Created by wangjing on 2018/3/27.
 */
class NormalTargetFile(val mission: RealMission) {
    private val realFilePath = mission.actual.savePath + separator + mission.actual.saveName
    private val shadowFilePath = realFilePath + DOWNLOADING_FILE_SUFFIX

    private val realFile = File(realFilePath)
    private val shadowFile = File(shadowFilePath)

    init {
        val dir = File(mission.actual.savePath)
        if (!dir.exists() || !dir.isDirectory) {
            dir.mkdirs()
        }
    }

    fun isFinish(): Boolean = realFile.exists()

    fun realFile(): File = realFile

    fun getStatus(): Status {
        return if (isFinish()) {
            Status(realFile.length(), realFile.length())
        } else {
            Status()
        }
    }

    fun checkFile() {
        if (shadowFile.exists()) {
            shadowFile.delete()
        }
        shadowFile.createNewFile()
    }

    fun save(response: Response<ResponseBody>): Flowable<Status> {
        val respBody = response.body() ?: throw RuntimeException("Response body is NULL")

        var downloadSize = 0L
        val byteSize = 8192L
        val totalSize = respBody.contentLength()

        val downloading = Downloading(Status(downloadSize, totalSize, isChunked(response)))

        return Flowable.create<Status>({
            respBody.source().use { source ->
                Okio.buffer(Okio.sink(shadowFile)).use { sink ->
                    val buffer = sink.buffer()
                    var readLen = source.read(buffer, byteSize)

                    while (readLen != -1L && !it.isCancelled) {
                        downloadSize += readLen
                        downloading.downloadSize = downloadSize

                        it.onNext(downloading)
                        readLen = source.read(buffer, byteSize)
                    }

                    if (!it.isCancelled) {
                        shadowFile.renameTo(realFile)
                        it.onNext(downloading)
                        it.onComplete()
                    }
                }
            }
        }, LATEST).sample(200, MILLISECONDS, true)
    }

    fun delete() {
        if (realFile.exists()) realFile.delete()
        if (shadowFile.exists()) shadowFile.delete()
    }

}