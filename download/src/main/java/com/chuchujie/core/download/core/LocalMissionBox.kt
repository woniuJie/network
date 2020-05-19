package com.chuchujie.core.download.core

import com.chuchujie.core.download.extension.Extension
import com.chuchujie.core.download.utils.ANY
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.internal.operators.maybe.MaybeToPublisher
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.Semaphore

/**
 * Created by wangjing on 2018/3/23.
 */
class LocalMissionBox : MissionBox {

    private val semaphore = Semaphore(DownloadConfig.maxMission, true)

    private val missionSet = mutableSetOf<RealMission>()

    override fun isExists(mission: Mission): Maybe<Boolean> {
        return Maybe.create<Boolean> {
            val result = missionSet.find { it.actual == mission }
            if (result != null) {
                it.onSuccess(true)
            } else {
                val tmpMission = RealMission(mission, semaphore, false)
                if (DownloadConfig.enableDb) {
                    val isExists = DownloadConfig.dbActor.isExists(tmpMission)
                    it.onSuccess(isExists)
                } else {
                    it.onSuccess(false)
                }
            }
        }
    }

    override fun create(mission: Mission): Flowable<Status> {
        val realMission = missionSet.find { it.actual == mission }

        return if (realMission != null) {
            realMission.getFlowable()
        } else {
            val newRealMission = RealMission(mission, semaphore)
            missionSet.add(newRealMission)
            newRealMission.getFlowable()
        }
    }

    override fun update(newMission: Mission): Maybe<Any> {
        return Maybe.create<Any> {
            val tmpMission = RealMission(newMission, semaphore, false)
            if (DownloadConfig.enableDb) {
                if (DownloadConfig.dbActor.isExists(tmpMission)) {
                    DownloadConfig.dbActor.update(tmpMission)
                }
            }
            it.onSuccess(ANY)
        }
    }

    override fun start(mission: Mission): Maybe<Any> {
        val realMission = missionSet.find { it.actual == mission } ?:
                return Maybe.error(RuntimeException("Mission not create"))
        return realMission.start()
    }

    override fun stop(mission: Mission): Maybe<Any> {
        val realMission = missionSet.find { it.actual == mission } ?:
                return Maybe.error(RuntimeException("Mission not create"))
        return realMission.stop()
    }

    override fun delete(mission: Mission, deleteFile: Boolean): Maybe<Any> {
        val realMission = missionSet.find { it.actual == mission } ?:
                return Maybe.error(RuntimeException("Mission not create"))
        return realMission.delete(deleteFile)
    }

    override fun clear(mission: Mission): Maybe<Any> {
        val realMission = missionSet.find { it.actual == mission } ?:
                return Maybe.error(RuntimeException("Mission not create"))

        return Maybe.create<Any> {
            //stop first.
            realMission.realStop()
            missionSet.remove(realMission)
            it.onSuccess(ANY)
        }
    }


    override fun createAll(missions: List<Mission>): Maybe<Any> {
        return Maybe.create<Any> {
            missions.forEach { mission ->
                val realMission = missionSet.find { it.actual == mission }
                if (realMission == null) {
                    val newRealMission = RealMission(mission, semaphore)
                    missionSet.add(newRealMission)
                }
            }
            it.onSuccess(ANY)
        }.subscribeOn(Schedulers.newThread())
    }

    override fun startAll(): Maybe<Any> {
        val arrays = mutableListOf<Maybe<Any>>()
        missionSet.forEach { arrays.add(it.start()) }
        return Flowable.fromIterable(arrays)
                .flatMap(MaybeToPublisher.INSTANCE)
                .lastElement()
    }

    override fun stopAll(): Maybe<Any> {
        val arrays = mutableListOf<Maybe<Any>>()
        missionSet.forEach { arrays.add(it.stop()) }
        return Flowable.fromIterable(arrays)
                .flatMap(MaybeToPublisher.INSTANCE)
                .lastElement()
    }

    override fun deleteAll(deleteFile: Boolean): Maybe<Any> {
        val arrays = mutableListOf<Maybe<Any>>()
        missionSet.forEach { arrays.add(it.delete(deleteFile)) }
        return Flowable.fromIterable(arrays)
                .flatMap(MaybeToPublisher.INSTANCE)
                .lastElement()
    }

    override fun clearAll(): Maybe<Any> {
        return Maybe.create<Any> {
            missionSet.forEach { it.realStop() }
            missionSet.clear()
        }
    }

    override fun file(mission: Mission): Maybe<File> {
        val realMission = missionSet.find { it.actual == mission } ?:
                return Maybe.error(RuntimeException("Mission not create"))
        return realMission.file()
    }

    override fun extension(mission: Mission, type: Class<out Extension>): Maybe<Any> {
        val realMission = missionSet.find { it.actual == mission } ?:
                return Maybe.error(RuntimeException("Mission not create"))
        return realMission.findExtension(type).action()
    }
}