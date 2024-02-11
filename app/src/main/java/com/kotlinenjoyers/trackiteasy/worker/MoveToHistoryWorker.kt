package com.kotlinenjoyers.trackiteasy.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.AppDataContainer
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory.ParcelHistory
import java.sql.Timestamp
import java.time.Instant

class MoveToHistoryWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    private lateinit var container: AppContainer

    override suspend fun doWork(): Result {
        container = AppDataContainer(this.applicationContext)
        val timestampNow = Timestamp(Instant.now().minusSeconds(259200).toEpochMilli())
        try {
            val list = container.parcelsRepository.getAllParcels()
            list.forEach {
                if (it.trackingEnd != null && timestampNow.after(it.trackingEnd)) {
                    container.parcelsHistoryRepository.insert(
                        ParcelHistory(
                            id = it.id,
                            trackingId = it.trackingId,
                            name = it.name,
                            extraInfo = it.extraInfo,
                            trackingEnd = it.trackingEnd,
                            json = it.json,
                        )
                    )
                    container.parcelsRepository.delete(it)
                }
            }
        } catch (e: Exception) {
            Log.println(Log.ERROR, "MoveToHistoryWorker", e.toString())
        }

        Log.println(Log.INFO, "MoveToHistoryWorker", "WORK DONE!")
        return Result.success()
    }
}