package com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.kotlinenjoyers.trackiteasy.data.storage.room.IParcel
import org.json.JSONObject
import java.sql.Timestamp

@Entity(primaryKeys= ["id", "trackingId", "name"], tableName = "ParcelHistory")
data class ParcelHistory(
    override val id: String,
    override val trackingId: String,
    override val name: String,
    @ColumnInfo(name = "extraInfo") override val extraInfo: String?,
    @ColumnInfo(name = "trackingEnd") override val trackingEnd: Timestamp,
    @ColumnInfo(name = "json") override val json: JSONObject,
) : IParcel