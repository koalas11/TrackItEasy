package com.kotlinenjoyers.trackiteasy.repository.parsers

import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.data.handler.Constants
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.Parcel
import com.kotlinenjoyers.trackiteasy.repository.parsers.interfaces.DefaultParserRepository
import com.kotlinenjoyers.trackiteasy.util.httprequest.HttpRequest
import com.kotlinenjoyers.trackiteasy.util.httprequest.HttpRequestInfo
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Timestamp
import java.time.Instant


object TNTParserRepository : DefaultParserRepository {
    override fun handle(container: AppContainer, trackingID: String): JSONArray {
        val httpRequestInfo = HttpRequestInfo(container.userAgent, "POST")
        httpRequestInfo.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        httpRequestInfo.addRequestHeader("Accept", "text/plain")
        val payload = "wt=1&consigNos=${trackingID}&autoSearch=&searchMethod=&pageNo=&numberText=${trackingID}&numberTextArea=&codCli=&tpCod=N"
        httpRequestInfo.payload = payload
        val result = HttpRequest("https://www.tnt.it/tracking/getTrackXML.html", httpRequestInfo).httpRequest()

        val xmlToJson = XmlToJson.Builder(result.result).build()
        val array = JSONArray()
        val json = xmlToJson.toJson()
        if (json != null) {
            val parcels = (json.get("ResultSet") as JSONObject).get("Consignment")
            array.put(parcels as JSONObject)
        }
        return array
    }

    override fun handle(container: AppContainer, trackingIDs: List<String>): JSONArray {
        val httpRequestInfo = HttpRequestInfo(container.userAgent, "POST")
        httpRequestInfo.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        httpRequestInfo.addRequestHeader("Accept", "text/plain")
        var list = ""
        trackingIDs.forEach {
            list += "$it,"
        }
        list = list.removeSuffix(",")
        val payload = "wt=1&consigNos=${list}&autoSearch=&searchMethod=&pageNo=&numberText=${list}&numberTextArea=&codCli=&tpCod=N"
        httpRequestInfo.payload = payload
        val result = HttpRequest("https://www.tnt.it/tracking/getTrackXML.html", httpRequestInfo).httpRequest()

        val xmlToJson = XmlToJson.Builder(result.result).build()
        val array = JSONArray()
        val json = xmlToJson.toJson()
        if (json != null) {
            val parcels = (json.get("ResultSet") as JSONObject).get("Consignment")
            if (parcels is JSONArray)
                return parcels
            array.put(parcels)
        }
        return array
    }

    override fun canParse(container: AppContainer, trackingID: String): Boolean {
        val httpRequestInfo = HttpRequestInfo(container.userAgent, "POST")
        httpRequestInfo.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        httpRequestInfo.addRequestHeader("Accept", "text/plain")
        val payload = "wt=1&consigNos=${trackingID}&autoSearch=&searchMethod=&pageNo=&numberText=${trackingID}&numberTextArea=&codCli=&tpCod=N"
        httpRequestInfo.payload = payload
        val result = HttpRequest("https://www.tnt.it/tracking/getTrackXML.html", httpRequestInfo).httpRequest()

        val xmlToJson = XmlToJson.Builder(result.result).build()
        val json = xmlToJson.toJson()
        if (json != null) {
            return !(json.get("ResultSet") as JSONObject).has("RuntimeError")
        }
        return false
    }

    override suspend fun updateParcels(container: AppContainer, parseResult: JSONArray, name : List<String>) : Boolean {
        val oldParcels = container.parcelsHistoryRepository.getAllParcels()
        for (i in 0..<parseResult.length()) {
            val jsonOBJ = parseResult[i] as JSONObject

            val parcel = Parcel(
                id = Constants.TNT_ID,
                trackingId = (jsonOBJ.remove("ConNo") as JSONObject).remove("__cdata") as String,
                name = name[i],
                extraInfo = null,
                trackingEnd = null,
                json = jsonOBJ,
                lastUpdate = Timestamp(Instant.now().toEpochMilli())
            )
            if (oldParcels.find {it.id == parcel.id && it.trackingId == parcel.trackingId} == null)
                container.parcelsRepository.insert(parcel)
        }
        return true
    }
}