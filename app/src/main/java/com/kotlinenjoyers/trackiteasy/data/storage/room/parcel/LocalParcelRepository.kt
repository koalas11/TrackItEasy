package com.kotlinenjoyers.trackiteasy.data.storage.room.parcel

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

class LocalParcelRepository(private val parcelDao: ParcelDao) : ParcelRepository {
    override fun getAllParcels(): List<Parcel> = parcelDao.getAllParcels()

    override fun getAllParcelsStream(): Flow<List<Parcel>> = parcelDao.getAllParcelsStream()

    override fun getParcelsFilteredSorted(query: SupportSQLiteQuery): Flow<List<Parcel>> = parcelDao.getParcelsFilteredSorted(query)

    override fun getParcelByIdStream(id: String, trackingId: String): Flow<Parcel?> = parcelDao.getParcelByIdStream(id, trackingId)

    override fun getParcelsById(id: String): List<Parcel> = parcelDao.getAllParcelsById(id)

    override fun getTrackingIdsByIdAndExtraInfo(id: String, extraInfo: String): List<String> = parcelDao.getTrackingIdsByIdAndExtraInfo(id, extraInfo)

    override fun getParcelsByIdAndExtraInfo(id: String, extraInfo: String): List<Parcel> = parcelDao.getParcelsByIdAndExtraInfo(id, extraInfo)

    override fun getParcelsByIds(ids: List<String>): List<Parcel> = parcelDao.getAllParcelsByIds(ids)

    override fun getAllTrackingIdNameById(id: String): List<ParcelInfo> = parcelDao.getAllTrackingIdNameById(id)

    override fun getAllTrackingIdNameByIds(ids: List<String>): List<ParcelInfo> = parcelDao.getAllTrackingIdNameByIds(ids)

    override suspend fun insertAll(vararg parcels: Parcel) = parcelDao.insertAll(*parcels)

    override suspend fun insert(parcel: Parcel) = parcelDao.insert(parcel)

    override suspend fun delete(parcel: Parcel) = parcelDao.delete(parcel)

    override suspend fun deleteAll() = parcelDao.deleteAll()

    override suspend fun deleteByIdAndExtraInfo(id: String, extraInfo: String) = parcelDao.deleteByIdAndExtraInfo(id, extraInfo)

    override suspend fun deleteById(id: String) = parcelDao.deleteById(id)

    override suspend fun update(parcel: Parcel) = parcelDao.update(parcel)
}