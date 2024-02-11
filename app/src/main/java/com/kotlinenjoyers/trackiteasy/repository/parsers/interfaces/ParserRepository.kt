package com.kotlinenjoyers.trackiteasy.repository.parsers.interfaces

import com.kotlinenjoyers.trackiteasy.AppContainer
import org.json.JSONArray

interface ParserRepository {
    suspend fun updateParcels(container: AppContainer, parseResult : JSONArray, name : List<String> = emptyList()) : Boolean

    suspend fun fetchHandler(container: AppContainer, trackingID : String, name : String) : Boolean {
        return false
    }
}