package com.kotlinenjoyers.trackiteasy.data.storage.room.parcel

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface ParcelDao {
    @Query("SELECT * FROM parcel")
    fun getAllParcels(): List<Parcel>

    @Query("SELECT * FROM parcel")
    fun getAllParcelsStream(): Flow<List<Parcel>>

    @RawQuery(observedEntities = [Parcel::class])
    fun getParcelsFilteredSorted(query: SupportSQLiteQuery): Flow<List<Parcel>>

    @Query("SELECT * FROM parcel WHERE id = :id")
    fun getAllParcelsById(id: String): List<Parcel>

    @Query("SELECT trackingId FROM parcel WHERE id = :id AND extraInfo = :extraInfo")
    fun getTrackingIdsByIdAndExtraInfo(id: String, extraInfo: String): List<String>

    @Query("SELECT * FROM parcel WHERE id = :id AND extraInfo = :extraInfo")
    fun getParcelsByIdAndExtraInfo(id: String, extraInfo: String): List<Parcel>

    @Query("SELECT * FROM parcel WHERE id IN (:id)")
    fun getAllParcelsByIds(id: List<String>): List<Parcel>

    @Query("SELECT trackingId, name FROM parcel WHERE id IN (:id)")
    fun getAllTrackingIdNameById(id: String): List<ParcelInfo>

    @Query("SELECT trackingId, name FROM parcel WHERE id = :id")
    fun getAllTrackingIdNameByIds(id: List<String>): List<ParcelInfo>

    @Query("SELECT * FROM parcel WHERE id = :id AND trackingId = :trackingId")
    fun getParcelByIdStream(id: String, trackingId: String): Flow<Parcel?>

    @Update
    fun update(parcel: Parcel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg parcels: Parcel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(parcel: Parcel)

    @Delete
    fun delete(parcel: Parcel)

    @Query("DELETE FROM parcel")
    fun deleteAll()

    @Query("DELETE FROM parcel WHERE id = :id AND extraInfo = :extraInfo")
    fun deleteByIdAndExtraInfo(id: String, extraInfo: String)

    @Query("DELETE FROM parcel WHERE id = :id")
    fun deleteById(id: String)
}