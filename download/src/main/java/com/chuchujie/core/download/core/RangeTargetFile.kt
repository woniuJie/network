package com.chuchujie.core.download.core

import com.chuchujie.core.download.core.DownloadConfig.DOWNLOADING_FILE_SUFFIX
import com.chuchujie.core.download.utils.ANY
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Flowable
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.File.separator
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * Created by wangjing on 2018/3/26.
 */
class RangeTargetFile(val mission: RealMission) {

    private val realFileDirPath = mission.actual.savePath
    private val realFilePath = realFileDirPath + separator + mission.actual.saveName

    private val shadowFilePath = realFilePath + DOWNLOADING_FILE_SUFFIX

    private val realFile = File(realFilePath)
    private val shadowFile = File(shadowFilePath)

    private val MODE = "rw"
    private val BUFFER_SIZE = 8 * 1024
    private val TRIGGER_SIZE = BUFFER_SIZE * 20

    init {
        var dir = File(realFileDirPath)
        if (!dir.exists() || !dir.isDirectory) {
            dir.mkdirs()
        }
    }

    fun isExists(): Boolean = realFile.exists()

    fun isShadowExists(): Boolean = shadowFile.exists()

    fun isFinish(): Boolean = realFile.exists()

    fun createShadowFile() {
        val file = RandomAccessFile(shadowFile, MODE)
        file.setLength(mission.totalSize)
    }

    fun realFile(): File = realFile

    fun rename() = shadowFile.renameTo(realFile)

    fun delete() {
        if (shadowFile.exists()) {
            shadowFile.delete()
        }
        if (realFile.exists()) {
            realFile().delete()
        }
    }

    fun save(response: Response<ResponseBody>,
             segment: RangeTmpFile.Segment,
             tmpFile: RangeTmpFile): Flowable<Any> {

        val respBody = response.body() ?: throw RuntimeException("Response body is NULL")

        return Flowable.create<Any>({

            val buffer = ByteArray(BUFFER_SIZE)
            respBody.byteStream().use { source ->

                RandomAccessFile(shadowFile, MODE).use { target ->
                    RandomAccessFile(tmpFile.getFile(), MODE).use { tmp ->
                        target.channel.use { targetChannel ->
                            tmp.channel.use { tmpChannel ->

                                val segmentBuffer = tmpChannel.map(
                                        FileChannel.MapMode.READ_WRITE,
                                        tmpFile.getPosition(segment),
                                        RangeTmpFile.Segment.SEGMENT_SIZE)

                                var readLength = source.read(buffer)

                                while (readLength != -1 && !it.isCancelled) {

                                    val targetBuffer = targetChannel.map(
                                            FileChannel.MapMode.READ_WRITE,
                                            segment.current,
                                            readLength.toLong())

                                    segment.current += readLength

                                    targetBuffer.put(buffer, 0, readLength)
                                    segmentBuffer.putLong(16, segment.current)

                                    readLength = source.read(buffer)

                                    it.onNext(ANY)
                                }
                                it.onNext(ANY)
                                it.onComplete()
                            }

                        }
                    }
                }
            }

        }, LATEST).sample(200, MILLISECONDS, true)

    }

}