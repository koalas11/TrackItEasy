package com.kotlinenjoyers.trackiteasy

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kotlinenjoyers.trackiteasy.data.storage.datastore.SettingsDataStore
import com.kotlinenjoyers.trackiteasy.worker.MoveToHistoryWorker
import com.kotlinenjoyers.trackiteasy.worker.RetrieverParcelsWorker
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class TrackItEasyApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        var intervalRetrieving : Long = 15
        var intervalMoveToHistory : Long = 24

        runBlocking {
            this@TrackItEasyApplication.SettingsDataStore.updateData {
                if (it.intervalRetrieving.toInt() != 0) {
                    intervalRetrieving = it.intervalRetrieving
                    intervalMoveToHistory = it.intervalMoveToHistory
                    it.toBuilder().build()
                } else
                    it.toBuilder()
                        .setIntervalRetrieving(intervalRetrieving)
                        .setIntervalMoveToHistory(intervalMoveToHistory)
                        .build()
            }
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

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "TrackItEasyRetrieving",
                ExistingPeriodicWorkPolicy.UPDATE,
                retrieveParcels
            )

        val moveParcelsToHistory =
            PeriodicWorkRequestBuilder<MoveToHistoryWorker>(intervalMoveToHistory, TimeUnit.HOURS)
                .addTag("TrackItEasyMoveToHistory")
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "TrackItEasyMoveToHistory",
                ExistingPeriodicWorkPolicy.UPDATE,
                moveParcelsToHistory
            )
    }
}