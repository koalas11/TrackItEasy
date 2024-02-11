package com.kotlinenjoyers.trackiteasy.repository.parsers

import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.data.handler.Constants
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.Parcel
import com.kotlinenjoyers.trackiteasy.repository.parsers.interfaces.DefaultParserRepository
import com.kotlinenjoyers.trackiteasy.util.httprequest.HttpRequest
import com.kotlinenjoyers.trackiteasy.util.httprequest.HttpRequestInfo
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Timestamp
import java.time.Instant

object PosteItalianeParserRepository : DefaultParserRepository {
    override fun handle(container: AppContainer, trackingID: String): JSONArray {
        val httpRequestInfo = HttpRequestInfo(container.userAgent, "POST")
        httpRequestInfo.addRequestHeader("Content-Type", "application/json")
        httpRequestInfo.addRequestHeader("Accept", "application/json")
        val payload = "{\"tipoRichiedente\": \"WEB\",\"codiceSpedizione\": \"${trackingID}\",\"periodoRicerca\": 1}"
        httpRequestInfo.payload = payload
        val result = HttpRequest("https://www.poste.it/online/dovequando/DQ-REST/ricercasemplice", httpRequestInfo).httpRequest()
        val array = JSONArray()
        array.put(JSONObject(result.result))
        return array
    }

    override fun handle(container: AppContainer, trackingIDs: List<String>): JSONArray {
        val httpRequestInfo = HttpRequestInfo(container.userAgent, "POST")
        httpRequestInfo.addRequestHeader("Content-Type", "application/json")
        httpRequestInfo.addRequestHeader("Accept", "application/json")
        var list = ""
        trackingIDs.forEach {
            list += "\"$it\","
        }
        val payload = "{\"tipoRichiedente\": \"WEB\",\"listaCodici\": [${list.removeSuffix(",")}]}"
        httpRequestInfo.payload = payload
        val result = HttpRequest("https://www.poste.it/online/dovequando/DQ-REST/ricercamultipla", httpRequestInfo).httpRequest()

        return JSONArray(result.result)
    }

    override fun canParse(container: AppContainer, trackingID: String): Boolean {
        if (trackingID.length != 13)
            return false

        val httpRequestInfo = HttpRequestInfo(container.userAgent, "POST")
        httpRequestInfo.addRequestHeader("Content-Type", "application/json")
        httpRequestInfo.addRequestHeader("Accept", "application/json")
        val payload = "{\"tipoRichiedente\": \"WEB\",\"codiceSpedizione\": \"$trackingID\",\"periodoRicerca\": 1}"
        httpRequestInfo.payload = payload.replace("\n", "")
        val result = HttpRequest("https://www.poste.it/online/dovequando/DQ-REST/verificaricercasemplice", httpRequestInfo).httpRequest()
        return result.result.contains("true")
    }

    override suspend fun updateParcels(container: AppContainer, parseResult: JSONArray, name : List<String>) : Boolean {
        val oldParcels = container.parcelsHistoryRepository.getAllParcels()
        for (i in 0..<parseResult.length()) {
            val jsonOBJ = parseResult[i] as JSONObject

            if (jsonOBJ.get("esitoRicerca") == "3") {
                val parcel = Parcel(
                    id = Constants.POSTE_ITALIANE_ID,
                    trackingId = jsonOBJ.remove("idTracciatura") as String,
                    name = name[i],
                    extraInfo = null,
                    trackingEnd = if (jsonOBJ.getString("stato") == "5") Timestamp(Instant.now().toEpochMilli()) else null,
                    json = jsonOBJ,
                    lastUpdate = Timestamp(Instant.now().toEpochMilli())
                )
                if (oldParcels.find {it.id == parcel.id && it.trackingId == parcel.trackingId} == null)
                    container.parcelsRepository.insert(parcel)
            }
        }
        return true
    }
}