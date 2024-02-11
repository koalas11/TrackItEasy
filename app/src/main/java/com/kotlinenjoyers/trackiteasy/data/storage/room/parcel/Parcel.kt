package com.kotlinenjoyers.trackiteasy.data.storage.room.parcel

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.kotlinenjoyers.trackiteasy.data.storage.room.IParcel
import org.json.JSONObject
import java.sql.Timestamp

@Entity(primaryKeys= ["id", "trackingId"], tableName = "Parcel")
data class Parcel (
    override val id: String,
    override val trackingId: String,
    override val name: String,
    @ColumnInfo(name = "extraInfo") override val extraInfo: String?,
    @ColumnInfo(name = "trackingEnd") override val trackingEnd: Timestamp?,
    @ColumnInfo(name = "json") override val json: JSONObject,
    @ColumnInfo(name = "lastUpdate") val lastUpdate: Timestamp,
) : IParcel

data class ParcelInfo(
    @ColumnInfo(name = "trackingId") val trackingId: String,
    @ColumnInfo(name = "name") val name: String,
)