package com.chuchujie.core.download.core

import com.chuchujie.core.download.core.DownloadConfig.RANGE_DOWNLOAD_SIZE
import com.chuchujie.core.download.core.DownloadConfig.TMP_DIR_SUFFIX
import com.chuchujie.core.download.core.DownloadConfig.TMP_FILE_SUFFIX
import com.chuchujie.core.download.core.RangeTmpFile.Segment.Companion.SEGMENT_SIZE
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString.decodeHex
import okio.Okio
import java.io.File
import java.io.File.separator

/**
 * Created by wangjing on 2018/3/26.
 */
class RangeTmpFile(val mission: RealMission) {

    private val tmpDirPath = mission.actual.savePath + separator + TMP_DIR_SUFFIX
    private val tmpFilePath = tmpDirPath + separator + mission.actual.saveName + TMP_FILE_SUFFIX

    private val file = File(tmpFilePath)

    private val fileStructure = FileStructure()

    private val status = Status()

    init {
        var tmpDirFile = File(tmpDirPath)
        if (!tmpDirFile.exists() || !tmpDirFile.isDirectory) {
            tmpDirFile.mkdirs()
        }
        if (file.exists()) {
            readStructure()
        }
    }

    fun checkFile() {
        if (file.exists()) {
            if (fileStructure.totalSize != mission.totalSize) {
                reset()
            }
        } else {
            file.createNewFile()
            writeStructure()
        }
    }

    fun reset() {
        file.delete()
        file.createNewFile()
        writeStructure()
    }

    private fun readStructure() {
        Okio.buffer(Okio.source(file)).use {
            fileStructure.readHeader(it)
            fileStructure.readSegments(it)
        }
    }

    private fun writeStructure() {
        Okio.buffer(Okio.sink(file)).use {
            fileStructure.writeHeader(it)
            fileStructure.writeSegments(it)
        }
    }

    fun isFinish(): Boolean = fileStructure.isFinish()

    fun isExists(): Boolean = file.exists()

    fun delete() {
        if (file.exists()) {
            file.delete()
        }
    }

    fun getFile(): File = file

    fun getSegments(): List<Segment> = fileStructure.segments

    fun getPosition(segment: Segment): Long = fileStructure.size() + SEGMENT_SIZE * segment.index

    fun currentStatus(): Status {
        var downloadSize = 0L
        val totalSize = fileStructure.totalSize

        val segments = getSegments()
        segments.forEach {
            downloadSize += (it.current - it.start)
        }

        status.downloadSize = downloadSize
        status.totalSize = totalSize

        return status
    }

    fun getLastModify(): Long = fileStructure.lastModify

    inner class FileStructure {
        private val FILE_HEADER_MAGIC_NUMBER = "a1b2c3d4e5f6"
        private val FILE_HEADER_MAGIC_NUMBER_HEX = decodeHex(FILE_HEADER_MAGIC_NUMBER)

        var totalSize: Long = 0L
        var totalSegments: Long = 0L
        var lastModify: Long = 0L

        var segments = mutableListOf<Segment>()

        fun size(): Long = FILE_HEADER_MAGIC_NUMBER_HEX.size() + 24L

        fun writeHeader(sink: BufferedSink) {
            totalSize = mission.totalSize
            totalSegments = calculateSegments()
            lastModify = mission.lastModify

            sink.write(FILE_HEADER_MAGIC_NUMBER_HEX)
            sink.writeLong(totalSize)
            sink.writeLong(totalSegments)
            sink.writeLong(lastModify)
        }

        fun readHeader(source: BufferedSource) {
            checkFileHeader(source)

            totalSize = source.readLong()
            totalSegments = source.readLong()
            lastModify = source.readLong()
        }

        fun writeSegments(sink: BufferedSink) {
            segments.clear()

            var start = 0L
            for (i in 0 until totalSegments) {
                var end = if (i == totalSegments - 1) {
                    mission.totalSize - 1
                } else {
                    start + RANGE_DOWNLOAD_SIZE - 1
                }

                segments.add(Segment(i, start, start, end).write(sink))

                start += RANGE_DOWNLOAD_SIZE
            }
        }

        fun readSegments(source: BufferedSource) {
            segments.clear()

            for (i in 0 until totalSegments) {
                val buffer = Buffer()
                source.readFully(buffer, SEGMENT_SIZE)

                val index = buffer.readLong()
                val start = buffer.readLong()
                val current = buffer.readLong()
                val end = buffer.readLong()

                segments.add(Segment(index, start, current, end))
            }
        }

        fun isFinish(): Boolean {
            if (segments.isEmpty()) {
                return false
            }

            return segments.any { it.isComplete() }
        }

        private fun checkFileHeader(source: BufferedSource) {
            val header = source.readByteString(FILE_HEADER_MAGIC_NUMBER_HEX.size().toLong()).hex()
            if (header != FILE_HEADER_MAGIC_NUMBER) {
                throw RuntimeException("$file not a temp file")
            }
        }

        private fun calculateSegments(): Long {
            var remainder = mission.totalSize % RANGE_DOWNLOAD_SIZE
            return if (remainder == 0L) {
                mission.totalSize / RANGE_DOWNLOAD_SIZE
            } else {
                mission.totalSize / RANGE_DOWNLOAD_SIZE + 1
            }
        }

    }


    class Segment(val index: Long, val start: Long, var current: Long, val end: Long) {

        companion object {
            val SEGMENT_SIZE = 32L  // each Long is 8 bytes
        }

        fun isComplete(): Boolean = (current - end) == 1L

        fun write(sink: BufferedSink): Segment {
            sink.writeLong(index)
            sink.writeLong(start)
            sink.writeLong(current)
            sink.writeLong(end)
            return this
        }

        fun size(): Long = (end - current) + 1

    }

}