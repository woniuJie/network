package com.chuchujie.download

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.chuchujie.core.download.RxDownload
import com.chuchujie.core.download.core.*
import com.chuchujie.networks.R
import io.reactivex.android.schedulers.AndroidSchedulers
import zlc.season.rxdownload3.extension.ApkInstallExtension

class DownloadActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var messageTextView: TextView

    lateinit var startButton: Button

    lateinit var stopButton: Button

    companion object {
//        val url = "http://imtt.dd.qq.com/16891/9A7CBD9CAFF7AA35E754408E2D2C6288.apk?fsname=com.tencent.mm_6.6.6_1300.apk&csr=1bbd"
        val url = "http://imtt.dd.qq.com/16891/DE3775B37190E52D8A67054E4C30B7B1.apk?fsname=com.tencent.tmgp.pubgmhd_0.5.1_3730.apk&csr=1bbd"
//        val url = "http://oss.ucdl.pp.uc.cn/fs01/union_pack/Wandoujia_267895_web_direct_binded.apk?x-oss-process=udf%2Fpp-udf%2CJjc3LiMnJ3V%2Fd3d3dg%3D%3D"
//        val url = "http://172.16.12.143:8080/Testdown2/http?filename=1.apk"
//        val url = "http://pic25.nipic.com/20121203/668573_115703380139_2.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        initRxDownload()

        messageTextView = findViewById(R.id.textView) as TextView
        startButton = findViewById(R.id.button4) as Button
        stopButton = findViewById(R.id.button5) as Button

        startButton.setOnClickListener(this)
        stopButton.setOnClickListener(this)


        val disposable = RxDownload.create(Mission(url))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    Log.e("xixihaha", "doOnComplete")
                }
                .doOnError {
                    Log.e("xixihaha", it.message)
                }
                .doOnNext {
                    setActionText(it)
                }
                .subscribe()
    }

    private fun setActionText(status: Status) {
        val text = when (status) {
            is Normal -> "开始"
            is Suspend -> "已暂停"
            is Waiting -> "等待中"
            is Downloading -> "下载中"
            is Failed -> "失败"
            is Succeed -> "安装"
            is ApkInstallExtension.Installing -> "安装中"
            is ApkInstallExtension.Installed -> "打开"
            else -> ""
        }
        messageTextView.text = status.formatString() + ", 当前状态:" + text
    }

    private fun initRxDownload() {

        val config = DownloadConfig.Builder.create(this)
                .enableDb(true)
                .enableNotification(true)
                .enableCheckFileChange(true)

        DownloadConfig.init(config)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.button4 -> RxDownload.start(url).subscribe()
            R.id.button5 -> RxDownload.stop(url).subscribe()
        }
    }

}
