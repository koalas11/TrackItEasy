package com.kotlinenjoyers.trackiteasy.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kotlinenjoyers.trackiteasy.util.ConnectivityObserver

/**
 * App bar to display title and conditionally display the back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    navBack: (() -> Unit)? = null,
    openOrderSheet: (() -> Unit)? = null,
    openFilterSheet: (() -> Unit)? = null,
    networkStatus: ConnectivityObserver.Status? = null,
    image: Int? = null,
    scrollBehavior: TopAppBarScrollBehavior? =
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        else null,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text(title) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            when {
                navBack != null -> IconButton(onClick = navBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
                openOrderSheet != null -> IconButton(onClick = openOrderSheet, modifier = modifier) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = modifier.fillMaxSize(0.9f))
                }
            }
        },
        actions = {
            if (networkStatus != null && networkStatus != ConnectivityObserver.Status.Available)
                Icon(
                    modifier = modifier
                        .padding(end = 8.dp)
                        .size(36.dp),
                    imageVector = Icons.Filled.WifiOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            if (openFilterSheet != null)
                IconButton( onClick = openFilterSheet, modifier = modifier) {
                    Icon(Icons.Filled.Search, contentDescription = null, modifier = modifier.fillMaxSize(0.9f))
                }
            if (image != null)
                Icon(
                    modifier = modifier
                        .padding(end = 16.dp)
                        .size(42.dp),
                    painter = painterResource(id = image),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
        }
    )
}