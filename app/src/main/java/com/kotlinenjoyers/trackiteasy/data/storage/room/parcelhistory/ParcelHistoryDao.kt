package com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory

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
interface ParcelHistoryDao {
    @Query("SELECT * FROM ParcelHistory")
    fun getAllParcels(): List<ParcelHistory>

    @Query("SELECT * FROM ParcelHistory")
    fun getAllParcelsStream(): Flow<List<ParcelHistory>>

    @RawQuery(observedEntities = [ParcelHistory::class])
    fun getParcelsFilteredSorted(query: SupportSQLiteQuery): Flow<List<ParcelHistory>>

    @Query("SELECT * FROM ParcelHistory WHERE trackingId IN (:userIds)")
    fun getAllParcelsStreamByIds(userIds: List<String>): List<ParcelHistory>

    @Query("SELECT * FROM ParcelHistory WHERE id = :id AND trackingId = :trackingId")
    fun getParcelByIdStream(id: String, trackingId: String): Flow<ParcelHistory?>

    @Query("SELECT * FROM ParcelHistory WHERE id = :id AND extraInfo = :extraInfo")
    fun getParcelsByIdAndExtraInfo(id: String, extraInfo: String): List<ParcelHistory>

    @Update
    fun update(parcel: ParcelHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg parcels: ParcelHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(parcel: ParcelHistory)

    @Query("DELETE FROM ParcelHistory")
    fun deleteAll()

    @Delete
    fun delete(parcel: ParcelHistory)
}