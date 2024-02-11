package com.kotlinenjoyers.trackiteasy.repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kotlinenjoyers.trackiteasy.data.storage.datastore.SettingsDataStore
import com.kotlinenjoyers.trackiteasy.worker.MoveToHistoryWorker
import com.kotlinenjoyers.trackiteasy.worker.RetrieverParcelsWorker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object SettingsRepository {
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("SettingsRepository", coroutineContext.toString())
        Log.e("SettingsRepository", throwable.toString())
        Log.e("SettingsRepository", throwable.stackTraceToString())
    }

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    fun updateSettings(
        context: Context,
        scope: CoroutineScope,
        intervalRetrieving: Long,
        intervalMoveToHistory: Long,

    ) {
        scope.launch(dispatcher + exceptionHandler) {
            context.SettingsDataStore.updateData {
                it.toBuilder()
                    .setIntervalRetrieving(intervalRetrieving)
                    .setIntervalMoveToHistory(intervalMoveToHistory)
                    .build()
            }

            val constraints: Constraints =
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            val retrieveParcels =
                PeriodicWorkRequestBuilder<RetrieverParcelsWorker>(intervalRetrieving, TimeUnit.MINUTES)
                    .addTag("TrackItEasyRetrieving")
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "TrackItEasyRetrieving",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    retrieveParcels
                )

            val moveParcelsToHistory =
                PeriodicWorkRequestBuilder<MoveToHistoryWorker>(intervalMoveToHistory, TimeUnit.HOURS)
                    .addTag("TrackItEasyMoveToHistory")
                    .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "TrackItEasyMoveToHistory",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    moveParcelsToHistory
                )
        }
    }
}