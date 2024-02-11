package com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [ParcelHistory] from a given data source.
 */
interface ParcelHistoryRepository {
    /**
     * Retrieve all the Parcels from the the given data source.
     */
    fun getAllParcels(): List<ParcelHistory>
    fun getAllParcelsStream(): Flow<List<ParcelHistory>>
    fun getParcelsFilteredSorted(query: SupportSQLiteQuery): Flow<List<ParcelHistory>>

    /**
     * Retrieve an Parcel from the given data source that matches with the [id] and [trackingId].
     */
    fun getParcelByIdStream(id: String, trackingId: String): Flow<ParcelHistory?>

    /**
     * Retrieve the Parcels from the given data source that matches with the [id] and [extraInfo].
     */
    fun getParcelsByIdAndExtraInfo(id: String, extraInfo: String): List<ParcelHistory>

    /**
     * Insert Parcel in the data source
     */
    suspend fun insert(parcel: ParcelHistory)

    /**
     * Delete All Parcel in the data source
     */
    suspend fun deleteAll()

    /**
     * Delete Parcel from the data source
     */
    suspend fun delete(parcel: ParcelHistory)

    /**
     * Update Parcel in the data source
     */
    suspend fun update(parcel: ParcelHistory)
}
