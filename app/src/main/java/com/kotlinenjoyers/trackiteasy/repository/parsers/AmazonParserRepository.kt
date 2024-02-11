package com.kotlinenjoyers.trackiteasy.repository.parsers

import android.util.Log
import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.data.handler.Constants.AMAZON_ID
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.Parcel
import com.kotlinenjoyers.trackiteasy.repository.parsers.interfaces.SpecialParserRepository
import com.kotlinenjoyers.trackiteasy.util.httprequest.HttpRequest
import com.kotlinenjoyers.trackiteasy.util.httprequest.HttpRequestInfo
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlOptions
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Timestamp
import java.time.Instant

object AmazonParserRepository : SpecialParserRepository {
    private const val SignInPageUrl = "signin"

    override fun handle(trackingID: String, extraInfo: Map<String, List<String>>, httpRequestInfo: HttpRequestInfo): JSONArray {
        val requestResult = HttpRequest(trackingID, httpRequestInfo).httpRequest()
        val result = JSONArray()

        if (requestResult.redirectedUrl.contains(SignInPageUrl)) {
            Log.d("AMAZON PARSER", "ACCOUNT DISCONNECTED")
            return result
        }

        var parseArrival = false
        var parseArrivalDetails = false
        var parseMainStatus = false
        var parseProblem = false
        var orderIndex = 0
        val jsonOBJ = JSONObject()
        result.put(jsonOBJ)

        val arrayImgId = extraInfo["ImgIds"]!!
        val arrayImg = JSONArray()

        val handler = KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, attributes, _ ->
                when {
                    name == "h1" && attributes["class"].equals("pt-promise-main-slot") -> {
                        parseArrival = true
                    }

                    name == "span" && attributes["id"] == "primaryStatus" -> {
                        parseProblem = true
                    }

                    name == "div" && attributes["class"] == "pt-status-milestone" -> {
                        jsonOBJ.put("OrderMilestone$orderIndex", attributes["data-reached"].toString())
                        jsonOBJ.put("OrderMilestonePercentage$orderIndex", attributes["data-percent-complete"]?.toInt())
                        orderIndex++
                    }

                    name == "img" && attributes["class"] == "asin-image a-lazy-loaded" &&
                            attributes["alt"] in arrayImgId ->
                        arrayImg.put(attributes["data-src"].toString())

                    name == "h1" && attributes["class"].equals("pt-status-main-status") -> {
                        parseMainStatus = true
                    }

                    name == "div" && attributes["class"].equals("pt-promise-details-slot") ->
                        parseArrivalDetails = true
                }
            }
            .onCloseTag { name, _ ->
                when (name) {
                    "h1" ->
                        parseArrival = false
                    "span" ->
                        parseProblem = false
                }
            }
            .onText { rawText ->
                val text = rawText.trim()
                when {
                    parseArrival -> {
                        val expectedArrival = if (jsonOBJ.has("ExpectedArrival")) jsonOBJ.getString("ExpectedArrival") else ""
                        jsonOBJ.put("ExpectedArrival", "$expectedArrival $text")
                    }
                    parseProblem -> {
                        val problem = if (jsonOBJ.has("Problem")) jsonOBJ.getString("Problem") else ""
                        jsonOBJ.put("Problem", "$problem $text")
                    }
                    parseMainStatus -> {
                        jsonOBJ.put("MainStatus", text)
                        parseMainStatus = false
                    }
                    parseArrivalDetails -> {
                        jsonOBJ.put("ExpectedArrivalDetails", text)
                        parseArrivalDetails = false
                    }
                }
            }
            .build()

        val ksoupHtmlOptions = KsoupHtmlOptions(
            decodeEntities = true,
        )

        val ksoupHtmlParser = KsoupHtmlParser(
            handler = handler,
            options = ksoupHtmlOptions,
        )

        ksoupHtmlParser.end(requestResult.result)

        val array = JSONArray()
        extraInfo["orders"]!!.forEach {
            array.put(it)
        }

        val arrayImgIds = JSONArray()

        arrayImgId.forEach {
            arrayImgIds.put(it)
        }

        jsonOBJ.put("OrderId", extraInfo["OrderId"]!![0])
        jsonOBJ.put("Email", extraInfo["email"]!![0])
        jsonOBJ.put("Orders", array)
        jsonOBJ.put("Images", arrayImg)
        jsonOBJ.put("ImagesIds", arrayImgIds)
        val newTrackingId= if (trackingID.contains("/gp/your-account/ship-track?itemId="))
            "/gp/your-account/ship-track?itemId="
        else "/progress-tracker/package/"
        jsonOBJ.put("StartLink", newTrackingId)
        jsonOBJ.put("TrackingId", trackingID.substringAfter(newTrackingId))

        if (orderIndex == 0)
            jsonOBJ.put("TrackingEnd", true)

        return result
    }

    override suspend fun updateParcels(
        container: AppContainer,
        parseResult: JSONArray,
        name: List<String>
    ) : Boolean {
        for (j in 0..<parseResult.length()) {
            val jsonOBJ = parseResult[j] as JSONObject
            val list = mutableListOf<Parcel>()
            val orders = jsonOBJ.remove("Orders") as JSONArray
            val images = jsonOBJ.remove("Images") as JSONArray
            val imagesIds = jsonOBJ.remove("ImagesIds") as JSONArray
            val trackingId = jsonOBJ.remove("TrackingId") as String
            val email = jsonOBJ.remove("Email") as String
            for (i in 0..<orders.length()) {
                if (images.length() > i) {
                    jsonOBJ.put("Img", images[i])
                    jsonOBJ.put("ImgId", imagesIds[i])
                }
                val parcel = Parcel(
                    id = AMAZON_ID,
                    trackingId = trackingId,
                    name = orders[i] as String,
                    extraInfo = email,
                    trackingEnd = if (jsonOBJ.has("TrackingEnd")) Timestamp(Instant.now().toEpochMilli()) else null,
                    json = jsonOBJ,
                    lastUpdate = Timestamp(Instant.now().toEpochMilli())
                )
                list.add(parcel)
            }
            container.parcelsRepository.insertAll(*list.toTypedArray())
        }
        return false
    }
}