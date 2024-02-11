package com.kotlinenjoyers.trackiteasy.ui.parcel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.data.handler.Constants
import com.kotlinenjoyers.trackiteasy.data.storage.room.IParcel
import com.kotlinenjoyers.trackiteasy.ui.navigation.ParcelType
import com.kotlinenjoyers.trackiteasy.ui.parcel.cards.AmazonParcel
import com.kotlinenjoyers.trackiteasy.ui.parcel.cards.PosteItalianeParcel

@Composable
fun ParcelItem(
    modifier: Modifier = Modifier,
    parcel: IParcel,
    onItemClick: (ParcelType, IParcel) -> Unit,
    type: ParcelType = ParcelType.Home,
) {
    Card(
        modifier = modifier
            .clickable { onItemClick(type, parcel) }
            .padding(horizontal = 4.dp, vertical = 2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary,
        )
    ) {
        when (parcel.id) {
            Constants.AMAZON_ID -> AmazonParcel(modifier, parcel)
            Constants.POSTE_ITALIANE_ID -> PosteItalianeParcel(modifier, parcel)
        }
    }
}

@Composable
fun EmptyParcelList(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .fillMaxHeight(0.5f)
            .wrapContentHeight(Alignment.CenterVertically),
        text = stringResource(id = R.string.empty_parcels_list),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.displayMedium
    )
    Icon(
        modifier = modifier.size(400.dp),
        imageVector = ImageVector.vectorResource(id = R.drawable.logo_icon),
        contentDescription = null
    )
}