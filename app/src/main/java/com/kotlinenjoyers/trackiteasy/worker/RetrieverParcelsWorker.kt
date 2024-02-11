package com.kotlinenjoyers.trackiteasy.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.AppDataContainer

class RetrieverParcelsWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    private lateinit var container: AppContainer

    override suspend fun doWork(): Result {
        container = AppDataContainer(this.applicationContext)
        try {
            container.parcelHandler.startRetrieving(container)
        } catch (e: Exception) {
            Log.println(Log.ERROR, "RetrieverParcelsWorker", e.toString())
        }

        Log.println(Log.INFO, "RetrieverParcelsWorker", "WORK DONE!")
        return Result.success()
    }
}