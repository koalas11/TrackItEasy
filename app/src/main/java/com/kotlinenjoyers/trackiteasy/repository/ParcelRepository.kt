package com.kotlinenjoyers.trackiteasy.repository

import android.util.Log
import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.data.storage.room.IParcel
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.Parcel
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory.ParcelHistory
import com.kotlinenjoyers.trackiteasy.ui.navigation.ParcelType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object ParcelRepository {
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("ParcelRepository", coroutineContext.toString())
        Log.e("ParcelRepository", throwable.toString())
        Log.e("ParcelRepository", throwable.stackTraceToString())
    }

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    fun deleteParcel(container: AppContainer, scope: CoroutineScope, type: ParcelType, parcel: IParcel, navBack: () -> Unit) {
        scope.launch(dispatcher + exceptionHandler) {
            if (type == ParcelType.Home) {
                val parcelsRepository = container.parcelsRepository
                parcelsRepository.delete(parcel as Parcel)
            } else if (type == ParcelType.History) {
                val parcelsHistoryRepository = container.parcelsHistoryRepository
                parcelsHistoryRepository.delete(parcel as ParcelHistory)
            }
        }.invokeOnCompletion {
            runBlocking(Dispatchers.Main) {
                navBack()
            }
        }
    }

    fun moveToHistory(container: AppContainer, scope: CoroutineScope, parcel: IParcel, navBack: () -> Unit) {
        scope.launch(dispatcher + exceptionHandler) {
            if (parcel.trackingEnd == null)
                return@launch
            val parcelsHistoryRepository = container.parcelsHistoryRepository
            val parcelsRepository = container.parcelsRepository
            val parcelHistory = ParcelHistory(
                parcel.id,
                parcel.trackingId,
                parcel.name,
                parcel.extraInfo,
                parcel.trackingEnd!!,
                parcel.json,
            )
            parcelsHistoryRepository.insert(parcelHistory)
            parcelsRepository.delete(parcel as Parcel)
        }.invokeOnCompletion {
            runBlocking(Dispatchers.Main) {
                navBack()
            }
        }
    }
}