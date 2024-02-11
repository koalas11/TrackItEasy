package com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts.accounts

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SentimentDissatisfied
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.kotlinenjoyers.trackiteasy.TrackItEasyApplication
import com.kotlinenjoyers.trackiteasy.repository.linkingaccounts.AmazonRepository
import com.kotlinenjoyers.trackiteasy.ui.common.Loading
import com.kotlinenjoyers.trackiteasy.ui.common.MenuTopBar
import com.kotlinenjoyers.trackiteasy.ui.navigation.MenuGraph

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AmazonWebViewScreen(
    modifier: Modifier = Modifier,
    navBack: () -> Unit,
    region: String,
) {
    val scope = rememberCoroutineScope()

    var ending by rememberSaveable {
        mutableStateOf(false)
    }

    var endingState by rememberSaveable {
        mutableStateOf(EndingState.Loading)
    }

    val context = LocalContext.current

    val endStateCallback: (EndingState) -> Unit = {
        endingState = it
    }

    val addAccountCallback: (String, String) -> Unit =  { email, cookies ->
        AmazonRepository.addLinkedAccount(
            scope = scope,
            container = (context.applicationContext as TrackItEasyApplication).container,
            topLevelDomain = region,
            email = email,
            cookies = cookies,
            endStateCallback = endStateCallback,
        )
    }

    Scaffold(
        topBar = {
            MenuTopBar(
                modifier = modifier,
                title = stringResource(MenuGraph.LinkingAccountsNested.AmazonWebView.titleRes),
                navBack = navBack,
            )
        },
    ) { innerPadding ->
        if (ending) {
            val text = when(endingState) {
                EndingState.Loading -> null
                EndingState.Success -> "Finished"
                EndingState.Error -> "Error"
            }
            val icon =  if (endingState == EndingState.Success)
                Icons.Rounded.SentimentSatisfied else Icons.Rounded.SentimentDissatisfied
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (endingState == EndingState.Loading) {
                    Loading(modifier)
                } else {
                    Text(
                        modifier = modifier
                            .weight(0.4f)
                            .wrapContentHeight(Alignment.CenterVertically),
                        text = text!!,
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Icon(
                        modifier = modifier
                            .weight(0.6f, true)
                            .fillMaxSize(),
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            }
        } else {
            AndroidView(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                factory = { context ->
                    WebStorage.getInstance().deleteAllData()

                    CookieManager.getInstance().removeAllCookies(null)
                    CookieManager.getInstance().flush()
                    WebView(context).apply {
                        settings.userAgentString = (context.applicationContext as TrackItEasyApplication).container.userAgent
                        clearCache(true)
                        clearFormData()
                        clearHistory()
                        clearSslPreferences()
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = AmazonWebView(
                            topLevelDomain = region,
                            addAccountCallback = addAccountCallback,
                            endCallback = { ending = true },
                        )
                        loadUrl("https://www.amazon.$region")
                    }
                }
            )
        }
    }
}