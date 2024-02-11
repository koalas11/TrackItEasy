package com.kotlinenjoyers.trackiteasy.ui.parcel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.TrackItEasyApplication
import com.kotlinenjoyers.trackiteasy.data.handler.Constants
import com.kotlinenjoyers.trackiteasy.data.storage.room.IParcel
import com.kotlinenjoyers.trackiteasy.repository.ParcelRepository
import com.kotlinenjoyers.trackiteasy.ui.AppViewModelProvider
import com.kotlinenjoyers.trackiteasy.ui.common.ParcelTopAppBar
import com.kotlinenjoyers.trackiteasy.ui.navigation.ParcelType
import com.kotlinenjoyers.trackiteasy.ui.navigation.Screens
import com.kotlinenjoyers.trackiteasy.ui.parcel.cards.AmazonParcelDetails
import com.kotlinenjoyers.trackiteasy.ui.parcel.cards.PosteItalianeParcelDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelDetailsScreen(
    modifier : Modifier = Modifier,
    navBack : () -> Unit,
    type: ParcelType,
    id: String,
    trackingId: String,
    viewModel: ParcelDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    LaunchedEffect(Unit){
        viewModel.updateState(type, id, trackingId)
    }
    val state by viewModel.parcelDetailsUiState.collectAsState()
    val parcel = state.parcel

    Scaffold(
        topBar = {
            val image = when (id) {
                Constants.AMAZON_ID -> R.drawable.amazon_icon
                Constants.POSTE_ITALIANE_ID -> R.drawable.poste_italiane_icon
                else -> null
            }
            ParcelTopAppBar(
                modifier = modifier,
                title = stringResource(Screens.HomeNested.ParcelDetails.titleRes),
                navBack = navBack,
                image = image,
            )
        },
    ) { innerPadding ->
        if (parcel != null)
            Card(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    disabledContainerColor = CardDefaults.cardColors().disabledContainerColor,
                    disabledContentColor = CardDefaults.cardColors().disabledContentColor,
                )
            ) {
                when (parcel.id) {
                    Constants.AMAZON_ID -> AmazonParcelDetails(modifier, parcel, type, navBack)
                    Constants.POSTE_ITALIANE_ID -> PosteItalianeParcelDetails(modifier, parcel, type, navBack)
                }
            }
    }
}

@Composable
fun ParcelDetailsButtons(
    modifier: Modifier = Modifier,
    parcel: IParcel,
    type: ParcelType,
    navBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Button(
            modifier = modifier.padding(start = 8.dp, end = 8.dp),
            onClick = {
                ParcelRepository.deleteParcel((context as TrackItEasyApplication).container, scope, type, parcel, navBack)
            },
        ) {
            Text(
                modifier = modifier,
                text = stringResource(id = R.string.remove),
                textAlign = TextAlign.Center,
            )
        }

        if (type == ParcelType.Home && parcel.trackingEnd != null)
            Button(
                modifier = modifier.padding(start = 8.dp, end = 8.dp),
                onClick = {
                    ParcelRepository.moveToHistory((context as TrackItEasyApplication).container, scope, parcel, navBack)
                },
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(id = R.string.move_history),
                    textAlign = TextAlign.Center,
                )
            }
    }
}