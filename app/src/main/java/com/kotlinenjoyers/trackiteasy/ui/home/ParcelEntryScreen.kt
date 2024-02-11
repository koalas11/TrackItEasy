package com.kotlinenjoyers.trackiteasy.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SentimentDissatisfied
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.TrackItEasyApplication
import com.kotlinenjoyers.trackiteasy.ui.common.Loading
import com.kotlinenjoyers.trackiteasy.ui.common.ParcelTopAppBar
import com.kotlinenjoyers.trackiteasy.ui.navigation.FindingStatus
import com.kotlinenjoyers.trackiteasy.ui.navigation.Screens
import com.kotlinenjoyers.trackiteasy.util.ConnectivityObserver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ParcelEntryScreen has an input form and a save button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelEntryScreen(
    modifier : Modifier = Modifier,
    navBack: () -> Unit,
    scope: CoroutineScope,
    findingParcel: FindingStatus,
    startFindingParcel: () -> Unit,
    finishFindingParcel: (FindingStatus) -> Unit,
) {
    val connectivityObserver = (LocalContext.current.applicationContext as TrackItEasyApplication).container.connectivityObserver
    val networkStatus by connectivityObserver.collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    )
    Scaffold(
        topBar = {
            ParcelTopAppBar(
                modifier = modifier,
                title = stringResource(Screens.HomeNested.ParcelEntry.titleRes),
                navBack = navBack,
                networkStatus = networkStatus,
            )
        },
    ) { innerPadding ->
        EntryForm(
            modifier = modifier,
            paddingValues = innerPadding,
            scope = scope,
            findingParcel = findingParcel,
            startFindingParcel = startFindingParcel,
            finishFindingParcel = finishFindingParcel,
            networkStatus = networkStatus,
        )
    }
}

@Composable
fun EntryForm(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    scope: CoroutineScope,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    findingParcel: FindingStatus,
    startFindingParcel: () -> Unit,
    finishFindingParcel: (FindingStatus) -> Unit,
    networkStatus: ConnectivityObserver.Status,
) {
    when (findingParcel) {
        FindingStatus.Loading -> {
            Column(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Loading(modifier)
            }
        }
        FindingStatus.Ready -> {
            Column(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(paddingValues)
                    .padding(top = 16.dp)
                    .wrapContentHeight(Alignment.Top)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var name by rememberSaveable { mutableStateOf("") }
                var code by rememberSaveable { mutableStateOf("") }
                TextField(
                    modifier = modifier.fillMaxWidth(0.9f),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(id = R.string.parcel_name)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    enabled = true,
                    singleLine = true,
                    placeholder = { Text(stringResource(id = R.string.parcel_name)) },
                )
                TextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("${stringResource(id = R.string.parcel_code)} *") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    modifier = modifier.fillMaxWidth(0.9f),
                    enabled = true,
                    singleLine = true,
                    placeholder = { Text(stringResource(id = R.string.parcel_code)) },
                )
                Text(
                    modifier = modifier.fillMaxWidth(0.9f),
                    text = "${stringResource(id = R.string.required)}(*)",
                )
                val container =
                    (LocalContext.current.applicationContext as TrackItEasyApplication).container
                Button(
                    onClick = {
                        if (code != "" && networkStatus == ConnectivityObserver.Status.Available) {
                            val parcelName = if (name == "") code else name
                            startFindingParcel()
                            scope.launch(defaultDispatcher) {
                                val result = container.parcelHandler.findParser(
                                    container,
                                    parcelName.trim(),
                                    code.substringBefore(",").trim(),
                                )
                                delay(2000)
                                if (result != FindingStatus.AlreadySearching)
                                    finishFindingParcel(result)
                            }
                        }
                    },
                    enabled = networkStatus == ConnectivityObserver.Status.Available,
                    shape = MaterialTheme.shapes.small,
                    modifier = modifier.fillMaxWidth(0.9f),
                ) {
                    Text(text = stringResource(id = R.string.search))
                }
            }
        }
        else -> {
            Column(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val text = if (findingParcel == FindingStatus.Found)
                    stringResource(id = R.string.found) else stringResource(id = R.string.not_found)
                val textAddNew = if (findingParcel == FindingStatus.Found)
                    stringResource(id = R.string.add_another) else stringResource(id = R.string.try_another)
                val icon =  if (findingParcel == FindingStatus.Found)
                    Icons.Rounded.SentimentSatisfied else Icons.Rounded.SentimentDissatisfied
                Text(
                    modifier = modifier
                        .weight(0.2f)
                        .wrapContentHeight(Alignment.CenterVertically),
                    text = text,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall
                )
                Icon(
                    modifier = modifier
                        .weight(0.6f, true)
                        .fillMaxSize(),
                    imageVector = icon,
                    contentDescription = null
                )
                Button(
                    modifier = modifier
                        .weight(0.3f)
                        .fillMaxSize(0.7f)
                        .wrapContentHeight(Alignment.CenterVertically),
                    onClick = startFindingParcel,
                ) {
                    Text(
                        text = textAddNew,
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}