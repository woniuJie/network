package com.chuchujie.core.download.core

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.chuchujie.core.download.extension.Extension
import com.chuchujie.core.download.utils.ANY
import com.chuchujie.core.download.utils.d
import io.reactivex.Flowable
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by wangjing on 2018/3/23.
 */
class DownloadService : Service() {

    private val missionBox = LocalMissionBox()
    private val binder = DownloadBinder()

    private lateinit var networkChangeBroadcastReceiver: NetworkChangeBroadcastReceiver

    private lateinit var networksChangeCallback: NetworksChangeCallback

    private val connectivityManager: ConnectivityManager
            = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        d("download service onCreate()")
        super.onCreate()
        registerNetworkChangeBR()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        d("download service onStartCommand()")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        d("download service onDestroy()")
        missionBox.stopAll()
        super.onDestroy()
        unRegisterNetworkChangeBR()
    }

    private fun registerNetworkChangeBR() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            networkChangeBroadcastReceiver = NetworkChangeBroadcastReceiver()
            registerReceiver(networkChangeBroadcastReceiver,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        } else {
            networksChangeCallback = NetworksChangeCallback()
            connectivityManager.registerDefaultNetworkCallback(networksChangeCallback)
        }
    }

    private fun unRegisterNetworkChangeBR() {
        if (networkChangeBroadcastReceiver != null) {
            unregisterReceiver(networkChangeBroadcastReceiver)
        }
        if (networksChangeCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.unregisterNetworkCallback(networksChangeCallback)
        }
    }

    private fun pauseDownloadMission() {
        Flowable.just(ANY)
                .map {
                    d("pause download mission.")
                    missionBox.stopAll().subscribe()
                }
                .sample(1, TimeUnit.SECONDS)
                .doOnError {
                    d("pause download mission exception:$it.message")
                }
                .doOnNext {
                    d("pause download mission finish.")
                }
                .subscribe()
    }

    private fun resumeDownloadMission() {
        Flowable.just(ANY)
                .map {
                    d("resume download mission.")
                    missionBox.startAll().subscribe()
                }
                .sample(1, TimeUnit.SECONDS)
                .doOnError {
                    d("resume download mission exception:$it.message")
                }
                .doOnNext {
                    d("resume download mission finish.")
                }
                .subscribe()
    }

    inner class NetworkChangeBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || context == null) {
                return
            }
            if (ConnectivityManager.CONNECTIVITY_ACTION != intent.action) {
                return
            }
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                d("network available, network info:$activeNetworkInfo")
                resumeDownloadMission()
            } else {
                d("network losing, network info:$activeNetworkInfo")
                pauseDownloadMission()
            }
        }

    }

    open inner class NetworksChangeCallback : ConnectivityManager.NetworkCallback() {

        override fun onLosing(network: Network?, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            d("network losing, network:$network, maxMsToLive:$maxMsToLive")
            pauseDownloadMission()
        }

        override fun onAvailable(network: Network?) {
            super.onAvailable(network)
            d("network available, network:$network")
            resumeDownloadMission()
        }

    }

    inner class DownloadBinder : Binder() {

        fun isExists(mission: Mission, boolCallback: BoolCallback, errorCb: ErrorCallback) {
            missionBox.isExists(mission)
                    .subscribe(boolCallback::apply, errorCb::apply)
        }

        fun create(mission: Mission, statusCallback: StatusCallback) {
            missionBox.create(mission).subscribe(statusCallback::apply)
        }

        fun update(newMission: Mission, successCb: SuccessCallback, errorCb: ErrorCallback) {
            missionBox.update(newMission)
                    .subscribe(successCb::apply, errorCb::apply)
        }

        fun start(mission: Mission, successCb: SuccessCallback, errorCb: ErrorCallback) {
            missionBox.start(mission).subscribe(successCb::apply, errorCb::apply)
        }

        fun stop(mission: Mission, successCb: SuccessCallback, errorCb: ErrorCallback) {
            missionBox.stop(mission).subscribe(successCb::apply, errorCb::apply)
        }

        fun delete(mission: Mission, deleteFile: Boolean, successCb: SuccessCallback,
                   errorCb: ErrorCallback) {
            missionBox.delete(mission, deleteFile).subscribe(successCb::apply, errorCb::apply)
        }

        fun createAll(missions: List<Mission>, successCb: SuccessCallback, errorCb: ErrorCallback) {
            missionBox.createAll(missions).subscribe(successCb::apply, errorCb::apply)
        }

        fun startAll(successCb: SuccessCallback, errorCb: ErrorCallback) {
            missionBox.startAll().subscribe(successCb::apply, errorCb::apply)
        }

        fun stopAll(successCb: SuccessCallback, errorCb: ErrorCallback) {
            missionBox.stopAll().subscribe(successCb::apply, errorCb::apply)
        }

        fun deleteAll(deleteFile: Boolean, successCallback: SuccessCallback,
                      errorCallback: ErrorCallback) {
            missionBox.deleteAll(deleteFile).subscribe(successCallback::apply, errorCallback::apply)
        }

        fun file(mission: Mission, fileCallback: FileCallback, errorCb: ErrorCallback) {
            missionBox.file(mission).subscribe(fileCallback::apply, errorCb::apply)
        }

        fun extension(mission: Mission, type: Class<out Extension>,
                      successCallback: SuccessCallback, errorCb: ErrorCallback) {
            missionBox.extension(mission, type)
                    .subscribe(successCallback::apply, errorCb::apply)
        }

        fun clear(mission: Mission, successCb: SuccessCallback, errorCb: ErrorCallback) {
            missionBox.clear(mission)
                    .subscribe(successCb::apply, errorCb::apply)
        }

        fun clearAll(successCb: SuccessCallback, errorCb: ErrorCallback) {
            missionBox.clearAll()
                    .subscribe(successCb::apply, errorCb::apply)
        }

    }

    interface BoolCallback {
        fun apply(value: Boolean)
    }

    interface StatusCallback {
        fun apply(status: Status)
    }

    interface FileCallback {
        fun apply(file: File)
    }

    interface SuccessCallback {
        fun apply(any: Any)
    }

    interface ErrorCallback {
        fun apply(throwable: Throwable)
    }

}