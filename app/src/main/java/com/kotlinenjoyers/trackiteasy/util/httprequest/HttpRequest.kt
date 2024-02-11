package com.kotlinenjoyers.trackiteasy.util.httprequest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HttpRequest(private val urlString : String, private val httpRequestInfo: HttpRequestInfo) {

    fun httpRequest() : HttpRequestResult {
        return runBlocking(Dispatchers.IO) {
            val url = URL(urlString)
            with (url.openConnection() as HttpsURLConnection) {
                try {
                    if (httpRequestInfo.cookies.isNotEmpty())
                        setRequestProperty("Cookie", httpRequestInfo.getCookies())
                    setRequestProperty("User-agent", httpRequestInfo.userAgent)
                    httpRequestInfo.requestHeaders.forEach {
                        setRequestProperty(it.key, it.value)
                    }
                    requestMethod = httpRequestInfo.method
                    allowUserInteraction = true


                    if (httpRequestInfo.payload != null) {
                        doOutput = true
                        outputStream.bufferedWriter().use {
                            it.write(httpRequestInfo.payload)
                            it.flush()
                        }
                        outputStream.close()
                    }

                    val text : String
                    if (responseCode == 200) {
                        inputStream.bufferedReader().use {
                            text = it.readText()
                        }
                        inputStream.close()
                    } else {
                        throw HttpRequestException("Error with an Http Request with response code: $responseCode and message: $responseMessage")
                    }

                    if (httpRequestInfo.updateCookies) {
                        headerFields["Set-Cookie"]?.forEach {
                            if (!(it.contains("=\"\";") || it.contains("=-;")))
                                httpRequestInfo.addCookie(it)
                        }
                    }

                    HttpRequestResult(responseCode, text, this.url.toString(), headerFields)

                } finally {
                    disconnect()
                }
            }
        }
    }
}