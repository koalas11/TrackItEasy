package com.kotlinenjoyers.trackiteasy.util.httprequest

data class HttpRequestResult(
    val statusCode : Int,
    val result: String,
    val redirectedUrl: String,
    val resultHeader: Map<String, List<String>>,
)