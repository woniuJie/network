package com.chuchujie.core.download.utils

import android.util.Log
import com.chuchujie.core.download.core.DownloadConfig.DEBUG
import java.util.*

/**
 * Created by wangjing on 2018/3/20.
 */
private val DEFAULT_LOG_TAG: String = "RxDownload"

fun v(message: String, throwable: Throwable? = null) {
    if (DEBUG) {
        Log.v(DEFAULT_LOG_TAG, message, throwable)
    }
}

fun v(message: String, vararg args: String, throwable: Throwable? = null) {
    v(String.format(Locale.getDefault(), message, args), throwable)
}

fun d(message: String, throwable: Throwable? = null) {
    if (DEBUG) {
        Log.d(DEFAULT_LOG_TAG, message, throwable)
    }
}

fun d(message: String, vararg args: String, throwable: Throwable? = null) {
    d(String.format(Locale.getDefault(), message, args), throwable)
}

fun i(message: String, throwable: Throwable? = null) {
    if (DEBUG) {
        Log.i(DEFAULT_LOG_TAG, message, throwable)
    }
}

fun i(message: String, vararg args: String, throwable: Throwable? = null) {
    i(String.format(Locale.getDefault(), message, args), throwable)
}

fun w(message: String, throwable: Throwable? = null) {
    if (DEBUG) {
        Log.w(DEFAULT_LOG_TAG, message, throwable)
    }
}

fun w(message: String, vararg args: String, throwable: Throwable? = null) {
    w(String.format(Locale.getDefault(), message, args), throwable)
}

fun e(message: String, throwable: Throwable? = null) {
    if (DEBUG) {
        Log.e(DEFAULT_LOG_TAG, message, throwable)
    }
}

fun e(message: String, vararg args: String, throwable: Throwable? = null) {
    e(String.format(Locale.getDefault(), message, args), throwable)
}
