package com.chuchujie.core.download.notification

import android.app.Notification
import android.content.Context
import com.chuchujie.core.download.core.RealMission
import com.chuchujie.core.download.core.Status

/**
 * Created by wangjing on 2018/3/21.
 */
interface NotificationFactory {

    fun build(context: Context, mission: RealMission, status: Status): Notification?

}