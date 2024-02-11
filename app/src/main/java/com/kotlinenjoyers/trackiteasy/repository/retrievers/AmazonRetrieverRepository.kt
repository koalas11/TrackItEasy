package com.kotlinenjoyers.trackiteasy.repository.retrievers

import android.annotation.SuppressLint
import android.util.Log
import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.data.handler.Constants.AMAZON_ID
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.Parcel
import com.kotlinenjoyers.trackiteasy.parceldatastore.Account
import com.kotlinenjoyers.trackiteasy.repository.parsers.AmazonParserRepository
import com.kotlinenjoyers.trackiteasy.util.httprequest.HttpRequest
import com.kotlinenjoyers.trackiteasy.util.httprequest.HttpRequestInfo
import com.mohamedrejeb.ksoup.entities.KsoupEntities
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlOptions
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.htmlunit.BrowserVersion
import org.htmlunit.HttpMethod
import org.htmlunit.SilentCssErrorHandler
import org.htmlunit.StringWebResponse
import org.htmlunit.WebClient
import org.htmlunit.WebRequest
import org.htmlunit.WebResponse
import org.htmlunit.html.HtmlPage
import org.htmlunit.javascript.SilentJavaScriptErrorListener
import org.htmlunit.util.Cookie
import org.htmlunit.util.WebConnectionWrapper
import java.io.IOException
import java.sql.Timestamp

object AmazonRetrieverRepository : RetrieverRepository {
    private const val SignInPageUrl = "signin"

    @SuppressLint("SetJavaScriptEnabled")
    override suspend fun fetchParcels(container: AppContainer) {
        Log.d("AMAZON RETRIEVER", "STARTED RETRIEVING")
        val accountToUpdate: MutableMap<Int, Account> = mutableMapOf()
        container.accountsDataStore.data.first { accounts ->
            val browserVersion = BrowserVersion.BrowserVersionBuilder(BrowserVersion.BEST_SUPPORTED)
                .setUserAgent(container.userAgent)
                .build()

            WebClient(browserVersion).use { webClient ->
                webClient.options.timeout = 60000
                webClient.options.isDownloadImages = false
                webClient.options.isDoNotTrackEnabled = true
                webClient.options.isRedirectEnabled = true
                webClient.options.isJavaScriptEnabled = true
                webClient.options.isThrowExceptionOnScriptError = false
                webClient.options.isThrowExceptionOnFailingStatusCode = false
                webClient.options.isCssEnabled = false
                webClient.options.isPopupBlockerEnabled = true
                webClient.options.isGeolocationEnabled = false
                webClient.options.isAppletEnabled = false
                webClient.javaScriptTimeout = 30000
                webClient.cssErrorHandler = SilentCssErrorHandler()
                webClient.javaScriptErrorListener = SilentJavaScriptErrorListener()
                webClient.webConnection = object : WebConnectionWrapper(webClient) {
                    @Throws(IOException::class)
                    override fun getResponse(request: WebRequest): WebResponse {
                        return if (request.url.toString().contains("homepage.html") || request.httpMethod == HttpMethod.POST) {
                            super.getResponse(request)
                        } else {
                            StringWebResponse("", request.url)
                        }
                    }
                }

                accounts.accountList.forEachIndexed { index, it ->
                    if (it.idParserRetriever != AMAZON_ID || !it.active)
                        return@forEachIndexed

                    val httpRequestInfo = HttpRequestInfo(container.userAgent)
                    httpRequestInfo.updateCookies = true
                    val topLevelDomain = it.extraInfo
                    val subDirectoryUrl = "https://www.amazon.$topLevelDomain"
                    val url = "$subDirectoryUrl/gp/css/order-history?ref_=nav_AccountFlyout_orders"
                    webClient.cookieManager.clearCookies()
                    it.cookiesList.forEach {
                        webClient.cookieManager.addCookie(Cookie(it.domain, it.name, it.value))
                    }

                    val page: HtmlPage =
                        webClient.getPage("$subDirectoryUrl/gp/css/homepage.html?ref_=nav_youraccount_btn")
                    val page2 = page.documentElement.click<HtmlPage>()

                    page.cleanUp()
                    page2.cleanUp()

                    val accountUpdated = it.toBuilder()
                    accountUpdated.clearCookies()
                    webClient.cookieManager.cookies.forEach {
                        accountUpdated.addCookies(
                            com.kotlinenjoyers.trackiteasy.parceldatastore.Cookie.newBuilder()
                                .setName(it.name)
                                .setValue(it.value)
                                .setDomain(it.domain)
                        )
                    }
                    accountToUpdate[index] = accountUpdated.build()
                    httpRequestInfo.setCookie(webClient.cookieManager.cookies)

                    webClient.cookieManager.clearCookies()

                    val requestResult = HttpRequest(url, httpRequestInfo).httpRequest()
                    val currentOrdersList = container.parcelsRepository.getParcelsByIdAndExtraInfo(AMAZON_ID, it.info)

                    if (requestResult.redirectedUrl.contains(SignInPageUrl)) {
                        Log.d("AMAZON RETRIEVER", "ACCOUNT ${it.info} DISCONNECTED")
                        val resultList = mutableListOf<Parcel>()
                        currentOrdersList.forEach {
                            val newJson = it.json
                            newJson.put("Inactive", true)
                            resultList.add(it.copy(json = newJson))
                        }

                        if (resultList.isNotEmpty())
                            container.parcelsRepository.insertAll(*resultList.toTypedArray())

                        container.accountsDataStore.updateData { accounts ->
                            var accountToFix: Account? = null
                            var accIndex = -1
                            accounts.accountList.forEachIndexed { i, account ->
                                if (account.idParserRetriever == AMAZON_ID && account.info == it.info) {
                                    accountToFix = account
                                    accIndex = i
                                }
                            }
                            val builder = accounts.toBuilder()
                            if (accIndex != -1) {
                                builder.setAccount(accIndex, accountToFix!!.toBuilder().setActive(false))
                            }
                            builder.build()
                        }
                        accountToUpdate.remove(index)
                        return@forEachIndexed
                    }

                    val orderIdList = mutableMapOf<String, Timestamp?>()
                    currentOrdersList.forEach {
                        orderIdList[it.json.getString("OrderId")] = it.trackingEnd
                    }
                    val parcelsHistoryRepository = container.parcelsHistoryRepository.getParcelsByIdAndExtraInfo(AMAZON_ID, it.info)

                    parcelsHistoryRepository.forEach {
                        orderIdList[it.json.getString("OrderId")] = it.trackingEnd
                    }

                    val latestOrders: Flow<Pair<Map<String, List<String>>, String>> = channelFlow {
                        val orderNames: MutableList<String> = mutableListOf()
                        val orderImgIds: MutableList<String> = mutableListOf()
                        var orderId: String? = null
                        var orderlink: String? = null
                        var parseOrderId = false
                        var parse = false
                        var parseName = false
                        val handler = KsoupHtmlHandler
                            .Builder()
                            .onOpenTag { name, attributes, _ ->
                                when {
                                    name == "span" && attributes["dir"] == "ltr" ->
                                        parseOrderId = true

                                    name == "bdi" && attributes["dir"] == "ltr" ->
                                        parseOrderId = true

                                    name == "div" && attributes["class"].equals("your-orders-content-container__content js-yo-main-content") ->
                                        parse = true

                                    name == "div" && attributes.isEmpty() -> {
                                        parse = false
                                        this.close()
                                    }

                                    parse &&
                                            ((name == "div" && attributes["class"].equals("yohtmlc-product-title"))
                                                    || (name == "a" && attributes["class"] == "a-link-normal" && attributes["href"]?.contains("/gp/product/") == true)) ->
                                        parseName = true

                                    parse && (name == "a" && attributes["class"] == "a-link-normal" &&
                                            (attributes["href"]?.startsWith("/gp/product/") == true || attributes["href"]?.startsWith("/dp/") == true)) -> {
                                        val link = attributes["href"]!!.removePrefix("/gp/product/").removePrefix("/dp/")
                                        if (link != attributes["href"]) {
                                            val id = link.substringBefore("?").substringBefore("/")
                                            orderImgIds.add(id)
                                        }
                                    }

                                    parse && (name == "a") && (attributes["href"] != null) -> {
                                        val link =
                                            attributes["href"]!!.removePrefix("/gp/your-account/ship-track?itemId=")
                                                .removePrefix("/progress-tracker/package/")
                                        if (orderId != null && orderIdList[orderId] != null) {
                                            parse = false
                                            this.close()
                                        } else {
                                            if (link != attributes["href"])
                                                orderlink = attributes["href"]!!
                                            if (link != attributes["href"] && orderNames.isNotEmpty() && orderId != null) {
                                                val listNames = orderNames.toList()
                                                val listImgIds = orderImgIds.toList()
                                                val orderIdTmp = orderId!!
                                                launch(Dispatchers.Default) {
                                                    send(Pair(
                                                        mapOf(
                                                            Pair("OrderId", listOf(orderIdTmp)),
                                                            Pair("email", listOf(it.info!!)),
                                                            Pair("orders", listNames),
                                                            Pair("ImgIds", listImgIds)
                                                        ),
                                                        attributes["href"]!!
                                                    ))
                                                }
                                                orderId = null
                                                orderlink = null
                                                orderImgIds.clear()
                                                orderNames.clear()
                                            }
                                        }
                                    }
                                }
                            }
                            .onText { text ->
                                if (parse) {
                                    when {
                                        parseName && text.trim() != "" -> {
                                            orderNames.add(KsoupEntities.decodeHtml(text.trim()))
                                            parseName = false
                                            if (orderlink != null && orderId != null) {
                                                val listNames = orderNames.toList()
                                                val listImgIds = orderImgIds.toList()
                                                val link = orderlink
                                                val orderIdTmp = orderId
                                                launch(Dispatchers.Default) {
                                                    send(Pair(mapOf(Pair("OrderId", listOf(orderIdTmp!!)), Pair("email", listOf(it.info!!)), Pair("orders", listNames), Pair("ImgIds", listImgIds)), link!!))
                                                }
                                                orderId = null
                                                orderlink = null
                                                orderImgIds.clear()
                                                orderNames.clear()
                                            }
                                        }
                                        parseOrderId && text.trim() != "" -> {
                                            orderId = text.trim()
                                            parseOrderId = false
                                        }
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
                    }

                    runBlocking {
                        latestOrders.collect {
                            val exceptionHandler =
                                CoroutineExceptionHandler { coroutineContext, throwable ->
                                    Log.e("Amazon Parser", coroutineContext.toString())
                                    Log.e("Amazon Parser", throwable.toString())
                                    Log.e("Amazon Parser", throwable.stackTraceToString())
                                }
                            val defaultDispatchers = Dispatchers.Default
                            val scope = CoroutineScope(defaultDispatchers + SupervisorJob() + exceptionHandler)
                            scope.launch {
                                launch {
                                    AmazonParserRepository.handleParcels(
                                        container,
                                        subDirectoryUrl + it.second,
                                        it.first,
                                        httpRequestInfo
                                    )
                                }
                            }.join()
                            scope.cancel()
                        }
                    }

                    val exceptionHandler =
                        CoroutineExceptionHandler { coroutineContext, throwable ->
                            Log.e("Amazon Parser", coroutineContext.toString())
                            Log.e("Amazon Parser", throwable.toString())
                            Log.e("Amazon Parser", throwable.stackTraceToString())
                        }
                    val defaultDispatchers = Dispatchers.Default
                    val scope = CoroutineScope(defaultDispatchers + SupervisorJob() + exceptionHandler)
                    scope.launch {
                        val updatedOrdersList = container.parcelsRepository.getParcelsByIdAndExtraInfo(AMAZON_ID, it.info)
                        updatedOrdersList.forEach { parcel ->
                            if (parcel.trackingEnd == null)
                                launch {
                                    AmazonParserRepository.handleParcels(
                                        container,
                                        "https://www.amazon.${it.extraInfo}" + parcel.json.getString("StartLink") + parcel.trackingId,
                                        mapOf(
                                            Pair("email", listOf(it.info!!)),
                                            Pair("orders", listOf(parcel.name)),
                                            Pair("OrderId", listOf(parcel.json.getString("OrderId"))),
                                            Pair("ImgIds", if (parcel.json.has("ImgId")) listOf(parcel.json.getString("ImgId")) else emptyList())),
                                        httpRequestInfo
                                    )
                                }
                        }
                    }.join()
                    scope.cancel()
                }
            }
            true
        }

        if (accountToUpdate.isNotEmpty())
            container.accountsDataStore.updateData {
                val builder = it.toBuilder()

                accountToUpdate.forEach { entry ->
                    builder.setAccount(entry.key, entry.value)
                }

                builder.build()
            }

        Log.d("AMAZON RETRIEVER", "FINISHED RETRIEVING")
    }

    override fun fixVersion(oldVersion: String) {
        TODO("Not yet implemented")
    }
}