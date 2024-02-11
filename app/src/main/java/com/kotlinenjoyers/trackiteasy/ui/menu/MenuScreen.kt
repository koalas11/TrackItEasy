package com.kotlinenjoyers.trackiteasy.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinenjoyers.trackiteasy.BuildConfig
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.TrackItEasyApplication
import com.kotlinenjoyers.trackiteasy.data.handler.ParcelHandler
import com.kotlinenjoyers.trackiteasy.ui.common.NavIcon
import com.kotlinenjoyers.trackiteasy.ui.common.ParcelBottomAppBar
import com.kotlinenjoyers.trackiteasy.ui.common.ParcelTopAppBar
import com.kotlinenjoyers.trackiteasy.ui.navigation.MenuGraph
import com.kotlinenjoyers.trackiteasy.ui.navigation.Screens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    navLambda: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            ParcelTopAppBar(
                modifier = modifier,
                title = stringResource(Screens.Menu.titleRes),
            )
        },
        bottomBar = {
            ParcelBottomAppBar (
                modifier = modifier,
                navLambda = navLambda,
                selected = NavIcon.Menu,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = modifier.padding(16.dp),
            ) {
                Icon(
                    modifier = modifier.size(48.dp),
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account",
                )
                TextButton(
                    modifier = modifier,
                    onClick = { navLambda(MenuGraph.Account.route) },
                ) {
                    Text(
                        modifier = modifier,
                        text = stringResource(id = R.string.account),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Row(modifier = modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Group,
                    contentDescription = "Linking Accounts",
                    modifier = modifier.size(48.dp)
                )
                TextButton(onClick = { navLambda(MenuGraph.LinkingAccounts.route) }) {
                    Text(text = stringResource(id = R.string.accountsLinking), fontSize = 20.sp)
                }
            }
            Row(modifier = modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    modifier = modifier.size(48.dp)
                )
                TextButton(onClick = { navLambda(MenuGraph.Settings.route) }) {
                    Text(text = stringResource(id = R.string.settings), fontSize = 20.sp)
                }
            }
            Row(
                modifier = modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Support",
                    modifier = modifier.size(48.dp)
                )
                TextButton(onClick = { navLambda(MenuGraph.Support.route) }) {
                    Text(text = stringResource(id = R.string.support), fontSize = 20.sp)
                }
            }
            Row(
                modifier = modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ThumbUp,
                    contentDescription = "Feedback",
                    modifier = modifier.size(48.dp)
                )
                TextButton(onClick = { navLambda(MenuGraph.Feedback.route) }) {
                    Text(text = stringResource(id = R.string.feedback), fontSize = 20.sp)
                }
            }
            if (BuildConfig.DebugButtons) {
                DebugButtons(modifier)
            }
        }
    }
}

@Composable
fun DebugButtons(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp)
    ) {
        val container = (LocalContext.current.applicationContext as TrackItEasyApplication).container
        val scope = rememberCoroutineScope()
        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = "TEST",
            modifier = modifier.size(48.dp)
        )
        TextButton(
            onClick = {
                scope.launch(Dispatchers.Default) {
                    ParcelHandler.getInstance(container).startRetrieving(container)
                }
            }
        ) {
            Text(text = "REFRESH PARCELS", fontSize = 20.sp)
        }
    }
    Row(
        modifier = modifier.padding(16.dp)
    ) {
        val container =
            (LocalContext.current.applicationContext as TrackItEasyApplication).container
        val scope = rememberCoroutineScope()
        Icon(
            imageVector = Icons.Filled.DeleteForever,
            contentDescription = "TEST",
            modifier = modifier.size(48.dp)
        )
        TextButton(onClick = {
            scope.launch(Dispatchers.Default) {
                container.parcelsRepository.deleteAll()
                container.parcelsHistoryRepository.deleteAll()
            }
        }) {
            Text(text = "DELETE ROOM DB", fontSize = 20.sp)
        }
    }
}