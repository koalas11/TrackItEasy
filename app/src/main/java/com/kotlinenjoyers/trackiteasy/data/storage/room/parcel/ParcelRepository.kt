package com.kotlinenjoyers.trackiteasy.data.storage.room.parcel

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Parcel] from a given data source.
 */
interface ParcelRepository {
    /**
     * Retrieve all the Parcels from the the given data source.
     */
    fun getAllParcels(): List<Parcel>

    /**
     * Retrieve all the Parcels from the the given data source.
     */
    fun getAllParcelsStream(): Flow<List<Parcel>>

    fun getParcelsFilteredSorted(query: SupportSQLiteQuery): Flow<List<Parcel>>

    /**
     * Retrieve a Parcel from the given data source that matches with the [id] and [trackingId].
     */
    fun getParcelByIdStream(id: String, trackingId: String): Flow<Parcel?>

    /**
     * Retrieve a Parcel from the given data source that matches with the [id] and [extraInfo].
     */
    fun getTrackingIdsByIdAndExtraInfo(id: String, extraInfo: String): List<String>

    /**
     * Retrieve a Parcel from the given data source that matches with the [id] and [extraInfo].
     */
    fun getParcelsByIdAndExtraInfo(id: String, extraInfo: String) : List<Parcel>

    /**
     * Retrieve an Parcel from the given data source that matches with the [id].
     */
    fun getParcelsById(id: String): List<Parcel>

    /**
     * Retrieve an Parcel from the given data source that matches with the [ids].
     */
    fun getParcelsByIds(ids: List<String>): List<Parcel>

    /**
     * Retrieve an Parcel from the given data source that matches with the [id].
     */
    fun getAllTrackingIdNameById(id: String): List<ParcelInfo>

    /**
     * Retrieve an Parcel from the given data source that matches with the [ids].
     */
    fun getAllTrackingIdNameByIds(ids: List<String>): List<ParcelInfo>

    /**
     * Insert Multiple Parcels in the data source
     */
    suspend fun insertAll(vararg parcels: Parcel)

    /**
     * Insert Parcel in the data source
     */
    suspend fun insert(parcel: Parcel)

    /**
     * Delete Parcel from the data source
     */
    suspend fun delete(parcel: Parcel)

    /**
     * Delete All Parcels from the data source
     */
    suspend fun deleteAll()

    /**
     * Delete All Parcels by id and extra info from the data source
     */
    suspend fun deleteByIdAndExtraInfo(id: String, extraInfo: String)

    /**
     * Delete All Parcels by id from the data source
     */
    suspend fun deleteById(id: String)

    /**
     * Update Parcel in the data source
     */
    suspend fun update(parcel: Parcel)
}
