package com.kotlinenjoyers.trackiteasy.util.httprequest

import org.htmlunit.util.Cookie

data class HttpRequestInfo(
    val userAgent: String,
    val method: String,
    val cookies: MutableMap<String, String>,
    var updateCookies : Boolean,
    val requestHeaders: MutableMap<String, String>,
    var payload: String?,
) {
    constructor(userAgent: String) : this(userAgent, "GET", mutableMapOf(), false, mutableMapOf(), null)

    constructor(userAgent: String, method: String) : this(userAgent, method, mutableMapOf(), false, mutableMapOf(), null)

    fun addRequestHeader(key : String, value : String) {
        requestHeaders[key] = value
    }

    fun setCookie(cookiesList: Set<Cookie>) {
        cookiesList.forEach {
            cookies[it.name] = it.value
        }
    }

    fun addCookie(fullCookie: String) {
        val cookie = fullCookie.substringBefore(";")
        cookies[cookie.substringBefore("=").trim()] = cookie.substringAfter("=").trim()
    }

    fun getCookies() : String {
        var result = ""
        val it = cookies.iterator()
        while (it.hasNext()) {
            val current = it.next()
            result += current.key + "=" + current.value
            if (it.hasNext())
                result += ";"
        }
        return result
    }
}