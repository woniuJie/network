package com.chuchujie.core.download.core

import android.content.Context
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import com.chuchujie.core.download.database.DbActor
import com.chuchujie.core.download.database.SQLiteActor
import com.chuchujie.core.download.extension.Extension
import com.chuchujie.core.download.http.OkHttpClientFactory
import com.chuchujie.core.download.http.OkHttpClientFactoryImpl
import com.chuchujie.core.download.notification.NotificationFactory
import com.chuchujie.core.download.notification.NotificationFactoryImpl

/**
 * Created by wangjing on 2018/3/20.
 */
@SuppressWarnings("StaticFieldLeak")
object DownloadConfig {

    internal var DEBUG = false

    internal val DOWNLOADING_FILE_SUFFIX = ".download"
    internal val TMP_DIR_SUFFIX = ".TMP"
    internal val TMP_FILE_SUFFIX = ".tmp"

    internal val RANGE_DOWNLOAD_SIZE: Long = 500 * 1024  // KB

    internal var maxMission = 3
    internal var maxRange = Runtime.getRuntime().availableProcessors() + 1

    internal var defaultSavePath = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).path

    internal var context: Context? = null

    internal var autoStart = false

    internal var enableDb = false
    internal lateinit var dbActor: DbActor

    internal var missionBox: MissionBox = LocalMissionBox()

    internal var enableNotification = false
    internal var notificationPeriod = 1L  //2s update once
    internal lateinit var notificationFactory: NotificationFactory

    internal var okHttpClientFactory: OkHttpClientFactory = OkHttpClientFactoryImpl()

    internal var extensions = mutableListOf<Class<out Extension>>()

    internal var enableCheckFileChange = false

    internal var maxDownloadRetryCount = 2

    internal var retryDownloadWaitTime = 500

    fun init(builder: Builder) {
        this.context = builder.context

        this.DEBUG = builder.debug

        this.maxMission = builder.maxMission
        this.maxRange = builder.maxRange
        this.defaultSavePath = builder.defaultSavePath

        this.autoStart = builder.autoStart

        this.enableDb = builder.enableDb
        this.dbActor = builder.dbActor

        if (enableDb) {
            dbActor.init()
        }

        this.enableNotification = builder.enableNotification
        this.notificationFactory = builder.notificationFactory
        this.notificationPeriod = builder.notificationPeriod

        this.okHttpClientFactory = builder.okHttpClientFactory

        this.extensions = builder.extensions

        val enableService = builder.enableService
        this.missionBox = if (enableService) {
            RemoteMissionBox()
        } else {
            LocalMissionBox()
        }

        this.enableCheckFileChange = builder.enableCheckFileChange

        this.maxDownloadRetryCount = builder.maxDownloadRetryCount
        this.retryDownloadWaitTime = builder.retryDownloadWaitTime
    }

    class Builder private constructor(val context: Context) {

        internal var maxMission = 3
        internal var maxRange = Runtime.getRuntime().availableProcessors() + 1

        internal var debug = false

        internal var autoStart = false

        internal var notificationPeriod = 2L

        internal var defaultSavePath = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).path

        internal var enableDb = false
        internal var dbActor: DbActor = SQLiteActor(context)

        internal var enableService = false

        internal var enableNotification = false
        internal var notificationFactory: NotificationFactory = NotificationFactoryImpl()

        internal var okHttpClientFactory: OkHttpClientFactory = OkHttpClientFactoryImpl()

        internal var extensions = mutableListOf<Class<out Extension>>()

        internal var enableCheckFileChange = false

        internal var maxDownloadRetryCount = 2

        internal var retryDownloadWaitTime = 500

        companion object {
            fun create(context: Context): Builder = Builder(context.applicationContext)
        }

        fun setDebug(debug: Boolean): Builder {
            this.debug = debug
            return this
        }

        fun setMaxMission(max: Int): Builder {
            this.maxMission = max
            return this
        }

        fun setMaxRange(max: Int): Builder {
            this.maxRange = max
            return this
        }

        fun setNotificationPeriod(period: Long): Builder {
            this.notificationPeriod = period
            return this
        }

        fun enableAutoStart(enable: Boolean): Builder {
            this.autoStart = enable
            return this
        }

        fun enableService(enable: Boolean): Builder {
            this.enableService = enable
            return this
        }

        fun enableNotification(enable: Boolean): Builder {
            this.enableNotification = enable
            return this
        }

        fun setNotificationFactory(notificationFactory: NotificationFactory): Builder {
            this.notificationFactory = notificationFactory
            return this
        }

        fun setDefaultPath(path: String): Builder {
            this.defaultSavePath = path
            return this
        }

        fun enableDb(enable: Boolean): Builder {
            this.enableDb = enable
            return this
        }

        fun setDbActor(dbActor: DbActor): Builder {
            this.dbActor = dbActor
            return this
        }

        fun setOkHttpClientFacotry(okHttpClientFactory: OkHttpClientFactoryImpl): Builder {
            this.okHttpClientFactory = okHttpClientFactory
            return this
        }

        fun addExtension(extension: Class<out Extension>): Builder {
            this.extensions.add(extension)
            return this
        }

        fun enableCheckFileChange(enable: Boolean): Builder {
            this.enableCheckFileChange = enable
            return this
        }

        fun maxDownloadRetryCount(retryCount: Int): Builder {
            this.maxDownloadRetryCount = retryCount
            return this
        }

        fun retryDownloadWaitTime(retryDownloadWaitTime: Int): Builder {
            this.retryDownloadWaitTime = retryDownloadWaitTime
            return this
        }

    }

}