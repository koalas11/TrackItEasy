package com.kotlinenjoyers.trackiteasy.data.storage.room

import org.json.JSONObject
import java.sql.Timestamp

interface IParcel {
    val id: String
    val trackingId: String
    val name: String
    val extraInfo: String?
    val trackingEnd: Timestamp?
    val json: JSONObject
}