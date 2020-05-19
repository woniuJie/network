package com.chuchujie.core.download.core

/**
 * Created by wangjing on 2018/3/22.
 */
class DownloadRange(var start: Long = 0L, var end: Long = 0L, var size: Long = 0L) {

    fun legal(): Boolean = start <= end

}