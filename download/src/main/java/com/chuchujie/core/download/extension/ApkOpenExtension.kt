package zlc.season.rxdownload3.extension

import android.content.Context
import android.content.Intent
import com.chuchujie.core.download.core.DownloadConfig
import com.chuchujie.core.download.core.RealMission
import com.chuchujie.core.download.extension.Extension
import com.chuchujie.core.download.utils.d
import com.chuchujie.core.download.utils.getPackageName
import io.reactivex.Maybe
import java.io.File

class ApkOpenExtension : Extension {
    lateinit var mission: RealMission
    lateinit var context: Context

    private var apkFile: File? = null


    override fun init(mission: RealMission) {
        this.mission = mission
        if (DownloadConfig.context == null) {
            d("No context, you should set context first")
        } else {
            this.context = DownloadConfig.context!!
        }
    }

    override fun action(): Maybe<Any> {
        return Maybe.create<Any> {
            this.apkFile = mission.getFile()
            if (apkFile == null) {
                return@create
            }
            openApp()
            it.onSuccess(1)
        }
    }

    private fun openApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(getPackageName(context, apkFile!!))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}