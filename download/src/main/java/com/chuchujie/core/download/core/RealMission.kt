package com.chuchujie.core.download.core

import android.support.v4.app.NotificationManagerCompat
import com.chuchujie.core.download.core.DownloadConfig.defaultSavePath
import com.chuchujie.core.download.core.DownloadConfig.enableCheckFileChange
import com.chuchujie.core.download.database.DbActor
import com.chuchujie.core.download.extension.Extension
import com.chuchujie.core.download.http.HttpFace
import com.chuchujie.core.download.notification.NotificationFactory
import com.chuchujie.core.download.utils.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import zlc.season.rxdownload3.core.RangeDownload
import java.io.File
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * Created by wangjing on 2018/3/21.
 */
class RealMission(val actual: Mission, val semaphore: Semaphore, initFlag: Boolean = true) {

    var totalSize = 0L
    var lastModify = 0L
    var status: Status = Normal(Status())

    private var semaphoreFlag = false

    private var processor = BehaviorProcessor.create<Status>().toSerialized()

    private var disposable: Disposable? = null
    private var downloadType: DownloadType? = null

    private lateinit var downloadFlowable: Flowable<Status>

    private val enableNotification = DownloadConfig.enableNotification
    private val notificationPeriod = DownloadConfig.notificationPeriod
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationFactory: NotificationFactory

    private val enableDb = DownloadConfig.enableDb
    private lateinit var dbActor: DbActor

    private val autoStart = DownloadConfig.autoStart

    private val extensions = mutableListOf<Extension>()

    init {
        if (initFlag) {
            init()
        }
    }

    fun getFlowable(): Flowable<Status> = processor

    private fun init() {
        Maybe
                .create<Any> {
                    loadConfig()
                    createFlowable()
                    initMission()
                    initExtension()
                    initNotification()

                    it.onSuccess(ANY)
                }
                .subscribeOn(Schedulers.newThread())
                .doOnError {
                    e(" init error!", it)
                }
                .subscribe {
                    emitStatus(status)
                    if (autoStart) {
                        realStart()
                    }
                }
    }

    private fun loadConfig() {
        if (enableNotification) {
            notificationManager = NotificationManagerCompat.from(DownloadConfig.context)
            notificationFactory = DownloadConfig.notificationFactory
        }

        if (enableDb) {
            dbActor = DownloadConfig.dbActor
        }
    }

    private fun initMission() {
        if (enableDb) {
            if (dbActor.isExists(this)) {
                dbActor.read(this)
            } else {
                dbActor.create(this)
            }
        }
    }

    private fun initExtension() {
        DownloadConfig.extensions.mapTo(extensions) { it.newInstance() }
        extensions.forEach { it.init(this) }
    }

    private fun initNotification() {
        processor.sample(notificationPeriod, TimeUnit.SECONDS, true)
                .doOnError {
                    e("create notification exception:" + it.message)
                }
                .subscribe {
                    if (enableNotification) {
                        val notification = notificationFactory
                                .build(DownloadConfig.context!!, this, it)
                        if (notification != null) {
                            notificationManager.notify(hashCode(), notification)
                        }
                    }
                }
    }

    private fun createFlowable() {
        downloadFlowable = Flowable.just(ANY)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    emitStatusWithNotification(Waiting(status))
                    semaphoreFlag = false
                    semaphore.acquire()
                    semaphoreFlag = true
                }
                .subscribeOn(Schedulers.newThread())
                .flatMap {
                    startDownload()
                }
                .doOnError {
                    e("Mission error!", it)
                    emitStatusWithNotification(Failed(status, it))
                }
                .doOnComplete {
                    d("Mission complete!")
                    emitStatusWithNotification(Succeed(status))
                }
                .doOnCancel {
                    d("Mission cancel!")
                    emitStatusWithNotification(Suspend(status))
                }
                .doFinally {
                    disposable = null
                    if (semaphoreFlag) {
                        semaphore.release()
                    }
                }
    }

    private fun startDownload(): Flowable<Status> =
            checkRangeSupport()
                    .flatMapPublisher {
                        checkFileLastModifyAndDownload()
                    }

    private fun checkRangeSupport(): Maybe<Any> {
        return if (actual.rangeFlag == null) {
            HttpFace.checkRangeSupport(this)
        } else {
            /*
             * when database has save download record,
             * don't not send a request to get range support
             */
            downloadType = generateDownloadType()
            Maybe.just(ANY)
        }
    }

    private fun checkFileLastModify(): Maybe<Any> {
        lastModify = downloadType!!.getLastModify()
        return HttpFace.checkModify(this)
    }

    private fun checkFileLastModifyAndDownload(): Flowable<out Status> {
        return if (downloadType!!.isFinish() && enableCheckFileChange) {
            checkFileLastModify().flatMapPublisher { download() }
        } else {
            download()
        }
    }

    private fun download(): Flowable<out Status> {
        return downloadType?.download() ?:
                Flowable.error(IllegalStateException("Illegal download type"))
    }

    fun setup(resp: Response<Void>, reSetup: Boolean = false, deleteFile: Boolean = false) {
        if (!reSetup) {
            actual.savePath = if (actual.savePath.isEmpty()) defaultSavePath else actual.savePath
            actual.saveName = fileName(actual.saveName, actual.url, resp)
            actual.rangeFlag = isSupportRange(resp)
            totalSize = contentLength(resp)
            downloadType = generateDownloadType()
            if (enableDb) {
                dbActor.update(this)
            }
        } else {
            lastModify = GMTToLong(lastModify(resp))
        }

        if (deleteFile) {
            downloadType!!.delete()
        }
    }

    private fun generateDownloadType(): DownloadType? = when {
        actual.rangeFlag == true -> RangeDownload(this)
        actual.rangeFlag == false -> NormalDownload(this)
        else -> null
    }

    private fun emitStatus(status: Status) {
        this.status = status
        processor.onNext(status)
        if (enableDb) {
            dbActor.update(this)
        }
    }

    fun emitStatusWithNotification(status: Status) {
        emitStatus(status)
    }

    fun start(): Maybe<Any> {
        return Maybe.create<Any> {
            realStart()
            it.onSuccess(ANY)
        }.subscribeOn(Schedulers.newThread())
    }

    private fun realStart() {
        if (enableDb) {
            if (!dbActor.isExists(this)) {
                dbActor.create(this)
            }
        }

        if (disposable == null) {
            // this::emitStatusWithNotification   Function References
            disposable = downloadFlowable.subscribe(this::emitStatusWithNotification)
        }
    }

    fun stop(): Maybe<Any> {
        return Maybe.create<Any> {
            realStop()
            it.onSuccess(ANY)
        }.subscribeOn(Schedulers.newThread())
    }

    internal fun realStop() {
        dispose(disposable)
        disposable = null
    }

    fun delete(deleteFile: Boolean): Maybe<Any> {
        return Maybe.create<Any> {
            //stop first.
            realStop()

            if (deleteFile) {
                downloadType?.delete()
            }

            if (enableDb) {
                dbActor.delete(this)
            }
            emitStatusWithNotification(Deleted(Status()))
            it.onSuccess(ANY)
        }.subscribeOn(Schedulers.newThread())
    }

    fun getFile(): File? = downloadType?.getFile()

    fun file(): Maybe<File> {
        return Maybe.create<File> {
            val file = getFile()
            if (file == null) {
                it.onError(RuntimeException("No such file"))
            } else {
                it.onSuccess(file)
            }
        }.subscribeOn(Schedulers.newThread())
    }

    fun findExtension(extension: Class<out Extension>): Extension =
            extensions.first { extension.isInstance(it) }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        other as RealMission

        if (actual != other.actual) {
            return false
        }

        return true
    }

    override fun hashCode(): Int = actual.hashCode()

}