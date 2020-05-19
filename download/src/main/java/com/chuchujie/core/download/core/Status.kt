package com.chuchujie.core.download.core

import com.chuchujie.core.download.utils.formatSize
import java.text.NumberFormat

/**
 *
 * 实时网速计算:
 *
 * 当前时间的downloadSize减去上一秒的downloadSize，即实时网速
 *
 * Created by wangjing on 2018/3/20.
 */
open class Status(var downloadSize: Long = 0L,
                  var totalSize: Long = 0L,
                  var chunkFlag: Boolean = false) {

    constructor(status: Status) : this(status.downloadSize, status.totalSize, status.chunkFlag)

    fun formatTotalSize(): String = formatSize(totalSize)

    fun formatDownloadSize(): String = formatSize(downloadSize)

    fun formatString(): String = formatDownloadSize() + "/" + formatTotalSize()

    fun percent(): String {
        val percent: String
        val result = if (totalSize == 0L) {
            0.0F
        } else {
            downloadSize * 1.0F / totalSize
        }
        val nf = NumberFormat.getPercentInstance()
        nf.minimumFractionDigits = 2
        percent = nf.format(result)
        return percent
    }

    override fun toString(): String =
            "Status(downloadSize=$downloadSize, totalSize=$totalSize, chunkFlag=$chunkFlag)"

}

class Normal(status: Status) : Status(status)

class Suspend(status: Status) : Status(status)

class Waiting(status: Status) : Status(status)

class Downloading(status: Status) : Status(status)

class Failed(status: Status, val throwable: Throwable) : Status(status)

class Succeed(status: Status) : Status(status)

class Deleted(status: Status) : Status(status)