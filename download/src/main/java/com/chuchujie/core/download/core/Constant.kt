package com.chuchujie.core.download.core

/**
 * Created by wangjing on 2018/3/22.
 */

val TAG = "RxDownload"
val TEST_RANGE_SUPPORT = "bytes=0-"

val CONTEXT_NULL_HINT = "Context is NULL! You should call [RxDownload.context(Context context)] first!"
val DOWNLOAD_URL_EXISTS = "The url download task already exists."
val DOWNLOAD_RECORD_FILE_DAMAGED = "Record file may be damaged, so we will re-download"

//Normal download hint
val CHUNKED_DOWNLOAD_HINT = "AHA, CHUNKED DOWNLOAD!"
val NORMAL_DOWNLOAD_PREPARE = "NORMAL DOWNLOAD PREPARE..."
val NORMAL_DOWNLOAD_STARTED = "NORMAL DOWNLOAD STARTED..."
val NORMAL_DOWNLOAD_COMPLETED = "NORMAL DOWNLOAD COMPLETED!"
val NORMAL_DOWNLOAD_FAILED = "NORMAL DOWNLOAD FAILED OR CANCEL!"

//Continue download hint
val CONTINUE_DOWNLOAD_PREPARE = "CONTINUE DOWNLOAD PREPARE..."
val CONTINUE_DOWNLOAD_STARTED = "CONTINUE DOWNLOAD STARTED..."
val CONTINUE_DOWNLOAD_COMPLETED = "CONTINUE DOWNLOAD COMPLETED!"
val CONTINUE_DOWNLOAD_FAILED = "CONTINUE DOWNLOAD FAILED OR CANCEL!"

//Multi-thread download hint
val MULTITHREADING_DOWNLOAD_PREPARE = "MULTITHREADING DOWNLOAD PREPARE..."
val MULTITHREADING_DOWNLOAD_STARTED = "MULTITHREADING DOWNLOAD STARTED..."
val MULTITHREADING_DOWNLOAD_COMPLETED = "MULTITHREADING DOWNLOAD COMPLETED!"
val MULTITHREADING_DOWNLOAD_FAILED = "MULTITHREADING DOWNLOAD FAILED OR CANCEL!"

val ALREADY_DOWNLOAD_HINT = "FILE ALREADY DOWNLOADED!"
val UNABLE_DOWNLOAD_HINT = "UNABLE DOWNLOADED!"
val NOT_SUPPORT_HEAD_HINT = "NOT SUPPORT HEAD, NOW TRY GET!"

//Range download hint
val RANGE_DOWNLOAD_STARTED = "[%s] start download! From [%s] to [%s] !"
val RANGE_DOWNLOAD_COMPLETED = "[%s] download completed!"
val RANGE_DOWNLOAD_CANCELED = "[%s] download canceled!"
val RANGE_DOWNLOAD_FAILED = "[%s] download failed or cancel!"
val RETRY_HINT = "[%s] got an [%s] error! [%d] attempt reconnection!"

//Dir hint
val DIR_EXISTS_HINT = "Path [%s] exists."
val DIR_NOT_EXISTS_HINT = "Path [%s] not exists, so create."
val DIR_CREATE_SUCCESS = "Path [%s] create success."
val DIR_CREATE_FAILED = "Path [%s] create failed."
val FILE_DELETE_SUCCESS = "File [%s] delete success."
val FILE_DELETE_FAILED = "File [%s] delete failed."
