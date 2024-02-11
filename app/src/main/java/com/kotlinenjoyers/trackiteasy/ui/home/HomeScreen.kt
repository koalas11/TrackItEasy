package com.kotlinenjoyers.trackiteasy.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.TrackItEasyApplication
import com.kotlinenjoyers.trackiteasy.data.handler.ParcelHandler
import com.kotlinenjoyers.trackiteasy.data.storage.room.IParcel
import com.kotlinenjoyers.trackiteasy.ui.AppViewModelProvider
import com.kotlinenjoyers.trackiteasy.ui.common.NavIcon
import com.kotlinenjoyers.trackiteasy.ui.common.ParcelBottomAppBar
import com.kotlinenjoyers.trackiteasy.ui.common.ParcelFilterBottomSheet
import com.kotlinenjoyers.trackiteasy.ui.common.ParcelOrderBottomSheet
import com.kotlinenjoyers.trackiteasy.ui.common.ParcelTopAppBar
import com.kotlinenjoyers.trackiteasy.ui.navigation.OpenSheet
import com.kotlinenjoyers.trackiteasy.ui.navigation.ParcelType
import com.kotlinenjoyers.trackiteasy.ui.navigation.Screens
import com.kotlinenjoyers.trackiteasy.ui.parcel.EmptyParcelList
import com.kotlinenjoyers.trackiteasy.ui.parcel.ParcelItem
import com.kotlinenjoyers.trackiteasy.util.ConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navLambda: (String) -> Unit,
    navParcelDetails: (ParcelType, IParcel) -> Unit,
    scope: CoroutineScope,
    viewModel : ParcelViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val connectivityObserver = (LocalContext.current.applicationContext as TrackItEasyApplication).container.connectivityObserver
    val networkStatus by connectivityObserver.collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    )
    LaunchedEffect(Unit) {
        viewModel.updateState()
    }
    var openSheet by remember {
        mutableStateOf(OpenSheet.Closed)
    }
    Scaffold(
        topBar = {
            ParcelTopAppBar(
                modifier = modifier,
                title = stringResource(Screens.Home.titleRes),
                openOrderSheet = {
                    openSheet = OpenSheet.Order
                },
                openFilterSheet = {
                    openSheet = OpenSheet.Filter
                },
                networkStatus = networkStatus
            )
        },
        bottomBar = {
            ParcelBottomAppBar(
                modifier = modifier,
                navLambda = navLambda,
                selected = NavIcon.Home,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = modifier,
                onClick = { navLambda(Screens.HomeNested.ParcelEntry.route) },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        val state by viewModel.homeUiState.collectAsState()
        if (state.parcelList.isEmpty()) {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(8.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                if (viewModel.filter == "" && networkStatus == ConnectivityObserver.Status.Available) {
                    var loading by rememberSaveable { mutableStateOf(false) }
                    val container = (LocalContext.current.applicationContext as TrackItEasyApplication).container
                    if (!loading) {
                        Button(
                            modifier = modifier
                                .fillMaxSize(0.6f),
                            onClick = {
                                loading = true
                                val dispatchersDefault = Dispatchers.Default
                                scope.launch(dispatchersDefault) {
                                    ParcelHandler.getInstance(container).startRetrieving(container)
                                    delay(1000)
                                    loading = false
                                }
                            },
                            enabled = !loading
                        ) {
                            Text(
                                modifier = modifier,
                                text = stringResource(id = R.string.reload),
                                textAlign = TextAlign.Center,
                                style = if (!loading)
                                    MaterialTheme.typography.displaySmall
                                else
                                    MaterialTheme.typography.titleSmall,
                            )
                        }
                    } else {
                        CircularProgressIndicator(
                            modifier = modifier
                                .fillMaxWidth(0.3f),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
                EmptyParcelList(modifier = modifier)
            }
        } else {
            val refreshState = rememberPullToRefreshState(
                positionalThreshold = 100.dp,
                enabled = { networkStatus == ConnectivityObserver.Status.Available },
            )

            if (refreshState.isRefreshing) {
                if (networkStatus == ConnectivityObserver.Status.Available) {
                    val container =
                        (LocalContext.current.applicationContext as TrackItEasyApplication).container
                    LaunchedEffect(Unit) {
                        val dispatchersDefault = Dispatchers.Default
                        scope.launch(dispatchersDefault) {
                            ParcelHandler.getInstance(container).startRetrieving(container)
                            refreshState.endRefresh()
                        }
                    }
                }
            }
            Box(
                modifier = modifier
                    .padding(innerPadding)
                    .nestedScroll(refreshState.nestedScrollConnection),
            ) {
                LazyColumn(
                    modifier = modifier
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = spacedBy(2.dp)
                ) {
                    items(
                        items = state.parcelList,
                        key = { parcel -> parcel.trackingId },
                    ) { parcel ->
                        ParcelItem(
                            modifier = modifier,
                            parcel = parcel,
                            onItemClick = navParcelDetails,
                            type = ParcelType.Home,
                        )
                    }
                }
                if (networkStatus == ConnectivityObserver.Status.Available) {
                    PullToRefreshContainer(
                        modifier = modifier
                            .align(Alignment.TopCenter),
                        state = refreshState,
                    )
                }
            }
        }
    }
    if (openSheet != OpenSheet.Closed) {
        if (openSheet == OpenSheet.Order) {
            ParcelOrderBottomSheet(
                viewModelOrder = viewModel.orderBy,
                closeSheet = {
                    openSheet = OpenSheet.Closed
                },
                setOrder = { order ->
                    viewModel.orderBy = order
                    viewModel.updateState()
                },
            )
        } else {
            ParcelFilterBottomSheet(
                viewModelFilter = viewModel.filter,
                closeSheet = {
                    openSheet = OpenSheet.Closed
                },
                setFilter = { filter ->
                    viewModel.filter = filter
                    viewModel.updateState()
                },
            )
        }
    }
}