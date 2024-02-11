package com.kotlinenjoyers.trackiteasy.repository.parsers.interfaces

import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.util.httprequest.HttpRequestInfo
import org.json.JSONArray

interface SpecialParserRepository : ParserRepository {

    fun handle(trackingID: String, extraInfo: Map<String, List<String>>, httpRequestInfo: HttpRequestInfo): JSONArray

    suspend fun handleParcels(container: AppContainer, trackingID : String, extraInfo: Map<String, List<String>>, httpRequestInfo: HttpRequestInfo) {
        updateParcels(container, handle(trackingID, extraInfo, httpRequestInfo))
    }
}