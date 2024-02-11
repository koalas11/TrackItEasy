package com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts.accounts

import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class EndingState {
    Success, Error, Loading
}

internal class AmazonWebView(val topLevelDomain: String, val addAccountCallback: (String, String) -> Unit, val endCallback: () -> Unit) : WebViewClient() {
    private val urlWhitelistRegex = "https://www\\.amazon\\.$topLevelDomain/".toRegex()
    private val urlSignInRegex = "https://www\\.amazon\\.$topLevelDomain/ap/signin".toRegex()
    private val emailRegex = "([a-z0-9.]+@[a-z0-9]+.[a-z0-9]+)".toRegex()

    private var email: String? = null
    private var emailCheck = false

    override fun onPageFinished(view: WebView, url: String?) {
        val cookies = CookieManager.getInstance()?.getCookie("https://www.amazon.$topLevelDomain")
        super.onPageFinished(view, url)

        if (urlSignInRegex.matches(url.toString())) {
            emailCheck = true
            email = null
            view.evaluateJavascript("javascript:document.signIn.innerHTML") { html ->
                val temp = emailRegex.find(html)?.value
                if (temp != null) {
                    email = temp.trim()
                    emailCheck = false
                }
            }
        }

        if (cookies != null && cookies.contains("x-acbit")) {
            endCallback()
            if (emailCheck) {
                val dispatchersDefault = Dispatchers.Default
                runBlocking(dispatchersDefault) {
                    launch(dispatchersDefault) {
                        var time = 0
                        while (emailCheck && time <= 10000) {
                            delay(1000)
                            time += 1000
                        }
                    }
                }
            }
            val finalEmail = email
            if (finalEmail != null) {
                addAccountCallback(finalEmail, cookies)
            }
        }
    }
}