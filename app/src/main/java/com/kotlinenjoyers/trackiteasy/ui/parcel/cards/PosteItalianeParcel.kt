package com.kotlinenjoyers.trackiteasy.ui.parcel.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.data.storage.room.IParcel
import com.kotlinenjoyers.trackiteasy.ui.navigation.ParcelType
import com.kotlinenjoyers.trackiteasy.ui.parcel.ParcelDetailsButtons
import java.sql.Timestamp

@Composable
fun PosteItalianeParcel(
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
            painter = painterResource(R.drawable.poste_italiane_icon),
            contentDescription = null,
            tint = Color.Unspecified,
        )
        Column(
            modifier = modifier
                .padding(16.dp)
                .weight(0.9f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            val status = when (parcel.json.getString("stato")) {
                "3" -> "Transito"
                "5" -> "Consegnato"
                else -> "Error!"
            }
            Text(
                text = parcel.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Tracking ID:" + parcel.trackingId,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = status,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun PosteItalianeParcelDetails(
    modifier: Modifier = Modifier,
    parcel: IParcel,
    type: ParcelType,
    navBack: () -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        item {
            val statusInfo =
                if (parcel.json.has("sintesiStato")) parcel.json.getString("sintesiStato") else ""
            Column(
                modifier = modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                val status = when (parcel.json.getString("stato")) {
                    "2" -> "Presa In Carico"
                    "3" -> "In Transito"
                    "4" -> "In Consegna"
                    "5" -> "Consegnato"
                    else -> "Error!"
                }
                Text(
                    text = "Nome: ${parcel.name}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Tracking ID: " + parcel.trackingId,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Stato: $status",
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (statusInfo != "")
                    Text(
                        text = "Dettagli Stato: $statusInfo",
                        style = MaterialTheme.typography.bodyLarge,
                    )
            }
        }
        val movements = if (parcel.json.has("listaMovimenti")) parcel.json.getJSONArray("listaMovimenti") else null
        if (movements != null) {
            item {
                if (movements.length() > 0) {
                    HorizontalDivider(
                        modifier = modifier
                            .padding(4.dp),
                        thickness = 4.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        modifier = modifier.padding(start = 8.dp).fillMaxWidth(),
                        text = "Lista Movimenti",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider(
                        modifier = modifier
                            .padding(4.dp),
                        thickness = 4.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            items(movements.length()) {
                val jsonObj = movements.getJSONObject(it)
                Column(
                    modifier = modifier
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    Text(
                        text = "Data: " + Timestamp(jsonObj.getLong("dataOra")),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Stato Lavorazione: " + jsonObj.getString("statoLavorazione"),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Luogo: " + jsonObj.getString("luogo"),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
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