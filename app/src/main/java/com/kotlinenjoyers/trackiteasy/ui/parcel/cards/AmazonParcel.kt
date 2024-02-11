package com.kotlinenjoyers.trackiteasy.ui.parcel.cards

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.data.storage.room.IParcel
import com.kotlinenjoyers.trackiteasy.ui.navigation.ParcelType
import com.kotlinenjoyers.trackiteasy.ui.parcel.ParcelDetailsButtons

@Composable
fun AmazonParcel(
    modifier: Modifier = Modifier,
    parcel: IParcel,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = modifier
                .weight(0.1f)
                .padding(start = 8.dp),
            painter = painterResource(R.drawable.amazon_icon),
            contentDescription = null,
            tint = Color.Unspecified,
        )
        Column(
            modifier = modifier
                .padding(16.dp)
                .weight(0.9f),
            verticalArrangement = spacedBy(1.dp)
        ) {
            val expectedArrival =
                if (parcel.json.has("ExpectedArrival")) parcel.json.getString("ExpectedArrival")
                else if (parcel.json.has("Problem")) parcel.json.getString("Problem") else "Error!"
            Text(
                text = parcel.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Account: " + parcel.extraInfo,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = expectedArrival,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun AmazonParcelDetails(
    modifier: Modifier = Modifier,
    parcel: IParcel,
    type: ParcelType,
    navBack: () -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = spacedBy(8.dp),
    ) {
        item {
            Text(
                modifier = modifier.padding(8.dp),
                text = "Name: " +  parcel.name,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        item {
            val image = if (parcel.json.has("Img")) parcel.json.getString("Img") else null

            if (image != null)
                AsyncImage(
                    modifier = modifier.padding(horizontal = 32.dp, vertical = 8.dp).fillMaxWidth(),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                )
        }

        item {
            val expectedArrival =
                if (parcel.json.has("ExpectedArrival")) parcel.json.getString("ExpectedArrival")
                else if (parcel.json.has("Problem")) parcel.json.getString("Problem") else "Error!"

            Text(
                modifier = modifier.padding(4.dp),
                text = "Account: " + parcel.extraInfo,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                modifier = modifier.padding(4.dp),
                text = expectedArrival,
                style = MaterialTheme.typography.bodyLarge,
            )
            val expectedArrivalDetails =
                if (parcel.json.has("ExpectedArrivalDetails")) parcel.json.getString("ExpectedArrivalDetails") else ""
            if (expectedArrivalDetails != "")
                Text(
                    modifier = modifier.padding(4.dp),
                    text = expectedArrivalDetails,
                    style = MaterialTheme.typography.bodyMedium,
                )
        }

        item {
            ParcelDetailsButtons(
                modifier = modifier,
                parcel = parcel,
                type = type,
                navBack = navBack,
            )
        }
    }
}