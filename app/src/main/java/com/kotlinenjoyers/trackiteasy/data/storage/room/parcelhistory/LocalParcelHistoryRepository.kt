package com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

class LocalParcelHistoryRepository(private val parcelHistoryDao: ParcelHistoryDao) :
    ParcelHistoryRepository {
    override fun getAllParcels(): List<ParcelHistory> = parcelHistoryDao.getAllParcels()

    override fun getAllParcelsStream(): Flow<List<ParcelHistory>> = parcelHistoryDao.getAllParcelsStream()

    override fun getParcelsFilteredSorted(query: SupportSQLiteQuery): Flow<List<ParcelHistory>> = parcelHistoryDao.getParcelsFilteredSorted(query)

    override fun getParcelByIdStream(id: String, trackingId: String): Flow<ParcelHistory?> = parcelHistoryDao.getParcelByIdStream(id, trackingId)

    override fun getParcelsByIdAndExtraInfo(id: String, extraInfo: String): List<ParcelHistory> = parcelHistoryDao.getParcelsByIdAndExtraInfo(id, extraInfo)

    override suspend fun insert(parcel: ParcelHistory) = parcelHistoryDao.insert(parcel)

    override suspend fun deleteAll() = parcelHistoryDao.deleteAll()

    override suspend fun delete(parcel: ParcelHistory) = parcelHistoryDao.delete(parcel)

    override suspend fun update(parcel: ParcelHistory) = parcelHistoryDao.update(parcel)
}