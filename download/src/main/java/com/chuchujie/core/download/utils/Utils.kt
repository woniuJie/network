package com.chuchujie.core.download.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import com.chuchujie.core.download.core.RETRY_HINT
import io.reactivex.FlowableTransformer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.io.File
import java.lang.Thread.currentThread
import java.net.*
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone.getTimeZone


/**
 * Created by wangjing on 2018/3/20.
 */

internal val ANY = Any()

fun retry(maxRetryCount: Int, currentRetryTime: Int, throwable: Throwable): Boolean {
    when (throwable) {
        is ProtocolException -> {
            if (currentRetryTime < maxRetryCount + 1) {
                d(RETRY_HINT, currentThread().name, "ProtocolException", currentRetryTime.toString())
                return true
            }
            return false
        }
        is UnknownHostException -> {
            if (currentRetryTime < maxRetryCount + 1) {
                d(RETRY_HINT, currentThread().name, "UnknownHostException", currentRetryTime.toString())
                return true
            }
            return false
        }
        is HttpException -> {
            if (currentRetryTime < maxRetryCount + 1) {
                d(RETRY_HINT, currentThread().name, "HttpException", currentRetryTime.toString())
                return true
            }
            return false
        }
        is SocketTimeoutException -> {
            if (currentRetryTime < maxRetryCount + 1) {
                d(RETRY_HINT, currentThread().name, "SocketTimeoutException", currentRetryTime.toString())
                return true
            }
            return false
        }
        is ConnectException -> {
            if (currentRetryTime < maxRetryCount + 1) {
                d(RETRY_HINT, currentThread().name, "ConnectException", currentRetryTime.toString())
                return true
            }
            return false
        }
        is SocketException -> {
            if (currentRetryTime < maxRetryCount + 1) {
                d(RETRY_HINT, currentThread().name, "SocketException", currentRetryTime.toString())
                return true
            }
            return false
        }
        else -> return false
    }
}

fun <U> retry(maxRetryCount: Int): FlowableTransformer<U, U> {
    return FlowableTransformer { upstream ->
        upstream.retry { t1, t2 -> retry(maxRetryCount, t1, t2) }
    }
}

fun dispose(disposable: Disposable?) {
    if (disposable != null && !disposable.isDisposed) {
        disposable.dispose()
    }
}

fun formatSize(size: Long): String {
    val b = size.toDouble()
    val k = size / 1024.0
    val m = size / 1024.0 / 1024.0
    val g = size / 1024.0 / 1024.0 / 1024.0
    val t = size / 1024.0 / 1024.0 / 1024.0 / 1024.0
    val dec = DecimalFormat("0.00")

    return when {
        t > 1 -> dec.format(t) + " TB"
        g > 1 -> dec.format(g) + " GB"
        m > 1 -> dec.format(m) + " MB"
        k > 1 -> dec.format(k) + " KB"
        else -> dec.format(b) + " B"
    }
}

fun getPackageName(context: Context, apkFile: File): String {
    val pm = context.packageManager
    val apkInfo = pm.getPackageArchiveInfo(apkFile.path, PackageManager.GET_META_DATA)
    return apkInfo.packageName
}

fun longToGMT(lastModify: Long): String {
    val d = Date(lastModify)
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
    sdf.timeZone = getTimeZone("GMT")
    return sdf.format(d)
}

@Throws(ParseException::class)
fun GMTToLong(GMT: String?): Long {
    if (GMT == null || "" == GMT) {
        return Date().time
    }
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
    sdf.timeZone = getTimeZone("GMT")
    val date = sdf.parse(GMT)
    return date.time
}

fun drawableToBitmap(context: Context, drawableId: Int): Bitmap {
    var bitmap: Bitmap? = null
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        val drawable = context.getDrawable(drawableId)
        bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
    } else {
        bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
    }
    return bitmap
}