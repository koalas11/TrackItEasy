package com.kotlinenjoyers.trackiteasy.repository.parsers.interfaces

import com.kotlinenjoyers.trackiteasy.AppContainer
import org.json.JSONArray

interface DefaultParserRepository : ParserRepository {

    fun handle(container: AppContainer, trackingID: String): JSONArray

    fun handle(container: AppContainer, trackingIDs: List<String>): JSONArray

    fun canParse(container: AppContainer, trackingID : String) : Boolean

    override suspend fun fetchHandler(container: AppContainer, trackingID : String, name : String) : Boolean {
        if (canParse(container, trackingID))
            return handleParcels(container, trackingID, listOf(name))
        return false
    }

    suspend fun handleParcels(container: AppContainer, trackingID : String, namesList : List<String>) : Boolean {
        return updateParcels(container, handle(container, trackingID), namesList)
    }

    suspend fun handleParcels(container: AppContainer, trackingIDList : List<String>, namesList : List<String>) : Boolean {
        return updateParcels(container, handle(container, trackingIDList), namesList)
    }
}