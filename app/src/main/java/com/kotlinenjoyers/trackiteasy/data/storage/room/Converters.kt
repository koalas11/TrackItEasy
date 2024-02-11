package com.kotlinenjoyers.trackiteasy.data.storage.room

import androidx.room.TypeConverter
import org.json.JSONObject
import java.sql.Timestamp

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): Timestamp? {
        if (value == null)
            return null
        return Timestamp.valueOf(value)
    }

    @TypeConverter
    fun dateToTimestamp(timestamp: Timestamp?): String? {
        if (timestamp == null)
            return null
        return timestamp.toString()
    }

    @TypeConverter
    fun fromString(value: String): JSONObject {
        return JSONObject(value)
    }

    @TypeConverter
    fun jsonToString(json: JSONObject): String {
        return json.toString()
    }
}