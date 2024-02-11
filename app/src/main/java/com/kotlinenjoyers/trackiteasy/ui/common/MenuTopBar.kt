package com.kotlinenjoyers.trackiteasy.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.kotlinenjoyers.trackiteasy.util.ConnectivityObserver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopBar(
    modifier: Modifier = Modifier,
    title: String,
    navBack: () -> Unit,
    networkStatus: ConnectivityObserver.Status? = null,
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
            IconButton(onClick = navBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
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
        }
    )
}