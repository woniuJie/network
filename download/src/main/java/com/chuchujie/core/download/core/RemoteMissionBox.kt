package com.chuchujie.core.download.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.chuchujie.core.download.extension.Extension
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * Created by wangjing on 2018/3/23.
 */
class RemoteMissionBox : MissionBox {

    var context: Context = DownloadConfig.context!!

    var downloadBinder: DownloadService.DownloadBinder? = null

    override fun isExists(mission: Mission): Maybe<Boolean> {
        return Maybe.create<Boolean> { emitter ->
            startBindServiceAndDo {
                it.isExists(mission, object : DownloadService.BoolCallback {
                    override fun apply(value: Boolean) {
                        emitter.onSuccess(value)
                    }
                }, ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun create(mission: Mission): Flowable<Status> {
        return Flowable.create<Status>({ emitter ->
            startBindServiceAndDo {
                it.create(mission, object : DownloadService.StatusCallback {
                    override fun apply(status: Status) {
                        emitter.onNext(status)
                    }
                })
            }
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.newThread())
    }

    override fun update(newMission: Mission): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            startBindServiceAndDo {
                it.update(newMission, SuccessCallbackImpl(emitter), ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun start(mission: Mission): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            startBindServiceAndDo {
                it.start(mission, SuccessCallbackImpl(emitter), ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun stop(mission: Mission): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            startBindServiceAndDo {
                it.stop(mission, SuccessCallbackImpl(emitter), ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun delete(mission: Mission, deleteFile: Boolean): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            startBindServiceAndDo {
                it.delete(mission, deleteFile, SuccessCallbackImpl(emitter),
                        ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun createAll(missions: List<Mission>): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            startBindServiceAndDo {
                it.createAll(missions, SuccessCallbackImpl(emitter),
                        ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun startAll(): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            startBindServiceAndDo {
                it.startAll(SuccessCallbackImpl(emitter), ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun stopAll(): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            startBindServiceAndDo {
                it.stopAll(SuccessCallbackImpl(emitter), ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun deleteAll(deleteFile: Boolean): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            startBindServiceAndDo {
                it.deleteAll(deleteFile, SuccessCallbackImpl(emitter), ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun file(mission: Mission): Maybe<File> {
        return Maybe.create<File> { emitter ->
            startBindServiceAndDo {
                it.file(mission,
                        object : DownloadService.FileCallback {
                            override fun apply(file: File) {
                                emitter.onSuccess(file)
                            }
                        },
                        ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun extension(mission: Mission, type: Class<out Extension>): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            startBindServiceAndDo {
                it.extension(mission, type, SuccessCallbackImpl(emitter),
                        ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun clear(mission: Mission): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            startBindServiceAndDo {
                it.clear(mission, SuccessCallbackImpl(emitter), ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }

    override fun clearAll(): Maybe<Any> {
        return Maybe.create<Any> { emitter ->
            startBindServiceAndDo {
                it.clearAll(SuccessCallbackImpl(emitter), ErrorCallbackImpl(emitter))
            }
        }.subscribeOn(Schedulers.newThread())
    }


    private fun startBindServiceAndDo(callback: (DownloadService.DownloadBinder) -> Unit) {
        if (downloadBinder != null) {
            callback(downloadBinder!!)
            return
        }
        val intent = Intent(context, DownloadService::class.java)
        // context.startService(intent)
        context.bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                downloadBinder = service as DownloadService.DownloadBinder
                callback(downloadBinder!!)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                downloadBinder = null
            }
        }, Context.BIND_AUTO_CREATE)
    }

    private class SuccessCallbackImpl(val emitter: MaybeEmitter<Any>)
        : DownloadService.SuccessCallback {
        override fun apply(any: Any) {
            emitter.onSuccess(any)
        }
    }

    private class ErrorCallbackImpl(val emitter: MaybeEmitter<out Any>)
        : DownloadService.ErrorCallback {
        override fun apply(throwable: Throwable) {
            emitter.onError(throwable)
        }
    }

}