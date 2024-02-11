package com.kotlinenjoyers.trackiteasy.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    navLambda: (String) -> Unit,
    navParcelDetails: (ParcelType, IParcel) -> Unit,
    viewModel : ParcelHistoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    LaunchedEffect(Unit) {
        viewModel.updateState()
    }
    val state by viewModel.historyUiState.collectAsState()
    var openSheet by remember {
        mutableStateOf(OpenSheet.Closed)
    }
    Scaffold(
        topBar = {
            ParcelTopAppBar(
                modifier = modifier,
                title = stringResource(Screens.History.titleRes),
                openOrderSheet = {
                    openSheet = OpenSheet.Order
                },
                openFilterSheet = {
                    openSheet = OpenSheet.Filter
                },
            )
        },
        bottomBar = {
            ParcelBottomAppBar (
                modifier = modifier,
                navLambda = navLambda,
                selected = NavIcon.History,
            )
        },
    ) { innerPadding ->
        if (state.parcelList.isEmpty()) {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EmptyParcelList(modifier = modifier)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(
                    items = state.parcelList,
                    key = { parcel -> parcel.trackingId },
                ) { parcel ->
                    ParcelItem(
                        modifier = modifier,
                        parcel = parcel,
                        onItemClick = navParcelDetails,
                        type = ParcelType.History,
                    )
                }
            }
        }
    }
    if (openSheet != OpenSheet.Closed) {
        if (openSheet == OpenSheet.Order)
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
        else
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