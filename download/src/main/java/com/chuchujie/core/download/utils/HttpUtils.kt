package com.chuchujie.core.download.utils

import android.text.TextUtils
import okhttp3.internal.http.HttpHeaders
import retrofit2.Response
import java.util.regex.Pattern

/**
 * Created by wangjing on 2018/3/22.
 */

fun isSupportRange(resp: Response<*>): Boolean {
    if (!resp.isSuccessful) {
        return false
    }

    if (resp.code() == 206 || contentRange(resp).isNotEmpty() || acceptRanges(resp).isNotEmpty()) {
        return true
    }

    return false
}

fun fileName(saveName: String, url: String, response: Response<*>): String {
    if (saveName.isNotEmpty()) {
        return saveName
    }
    
    var fileName = contentDisposition(response)
    if (fileName.isEmpty()) {
        fileName = substringUrl(url)
    }
    return fileName
}

fun substringUrl(url: String): String = url.substring(url.lastIndexOf('/') + 1)

fun contentDisposition(response: Response<*>): String {
    val disposition = response.headers().get("Content-Disposition")

    if (disposition == null || disposition.isEmpty()) {
        return ""
    }

    val matcher = Pattern.compile(".*filename=(.*)").matcher(disposition.toLowerCase())
    if (!matcher.find()) {
        return ""
    }

    var result = matcher.group(1)
    if (result.startsWith("\"")) {
        result = result.substring(1)
    }
    if (result.endsWith("\"")) {
        result = result.substring(0, result.length - 1)
    }
    return result
}

fun contentLength(response: Response<*>): Long = HttpHeaders.contentLength(response.headers())

fun getTotalSize(response: Response<*>): Long {
    val contentRange = contentRange(response)
    val tmp = contentRange.substringAfterLast('/')
    return tmp.toLong()
}

fun transferEncoding(response: Response<*>): String? {
    var header = response.headers().get("Transfer-Encoding")
    if (header == null) {
        header = ""
    }
    return header
}

fun contentRange(response: Response<*>): String {
    var header = response.headers().get("Content-Range")
    if (header == null) {
        header = ""
    }
    return header
}

fun acceptRanges(response: Response<*>): String {
    var header = response.headers().get("Accept-Ranges")
    if (header == null) {
        header = ""
    }
    return header
}

fun lastModify(response: Response<*>): String {
    var lastModify = response.headers().get("Last-Modified")
    if (lastModify == null) {
        lastModify = ""
    }
    return lastModify
}

fun notSupportRange(response: Response<*>): Boolean =
        TextUtils.isEmpty(contentRange(response))
                || contentLength(response) == -1L
                || isChunked(response)

fun serverFileChanged(response: Response<*>): Boolean = response.code() == 200

fun serverFileNotChanged(response: Response<*>): Boolean = response.code() == 206

fun requestRangeNotSatisfiable(response: Response<*>): Boolean = response.code() == 416

fun isChunked(response: Response<*>): Boolean = "chunked" == transferEncoding(response)
