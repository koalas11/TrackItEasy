package com.kotlinenjoyers.trackiteasy.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kotlinenjoyers.trackiteasy.R

enum class ParcelOrder(val order: String) {
    NameAsc("name ASC"), NameDesc("name DESC"), IdAsc("id ASC"), IdDesc("id DESC")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelOrderBottomSheet(
    modifier: Modifier = Modifier,
    viewModelOrder: ParcelOrder,
    closeSheet: () -> Unit,
    setOrder: (ParcelOrder) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState()

    var sortContent by remember {
        mutableStateOf(viewModelOrder)
    }

    ModalBottomSheet(
        modifier = modifier.wrapContentSize(Alignment.Center),
        onDismissRequest = closeSheet,
        sheetState = bottomSheetState,
    ) {
        Text(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.sort_by),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        Row(
            modifier = modifier.padding(bottom = 32.dp),
        ) {
            ElevatedButton(
                onClick = {
                    if (sortContent == ParcelOrder.NameAsc) {
                        sortContent = ParcelOrder.NameDesc
                        setOrder(ParcelOrder.NameDesc)
                    } else {
                        sortContent = ParcelOrder.NameAsc
                        setOrder(ParcelOrder.NameAsc)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),

                colors = if (sortContent in arrayOf(ParcelOrder.NameAsc, ParcelOrder.NameDesc))
                    ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                    )
                    else ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                    )
            ) {
                val text = if (sortContent == ParcelOrder.NameAsc || sortContent != ParcelOrder.NameDesc) "NAME (asc)" else "NAME (desc)"
                val icon = if (sortContent == ParcelOrder.NameAsc || sortContent != ParcelOrder.NameDesc) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
                Text(
                    modifier = modifier.weight(0.8f),
                    text = text,
                    textAlign = TextAlign.Center,
                    style = if (sortContent in arrayOf(ParcelOrder.NameAsc, ParcelOrder.NameDesc))
                        MaterialTheme.typography.titleMedium
                    else
                        MaterialTheme.typography.bodyMedium,
                )
                Icon(
                    modifier = modifier.weight(0.2f),
                    imageVector = icon,
                    contentDescription = null,
                )
            }
            ElevatedButton(
                onClick = {
                    if (sortContent == ParcelOrder.IdAsc) {
                        sortContent = ParcelOrder.IdDesc
                        setOrder(ParcelOrder.IdDesc)
                    } else {
                        sortContent = ParcelOrder.IdAsc
                        setOrder(ParcelOrder.IdAsc)
                    }
                },
                modifier = modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                colors = if (sortContent in arrayOf(ParcelOrder.IdAsc, ParcelOrder.IdDesc))
                    ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                    )
                else ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                )
            ) {
                val text = if (sortContent == ParcelOrder.IdAsc || sortContent != ParcelOrder.IdDesc) "ID (asc)" else "ID (desc)"
                val icon = if (sortContent == ParcelOrder.IdAsc || sortContent != ParcelOrder.IdDesc) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
                Text(
                    modifier = modifier.weight(0.8f),
                    text = text,
                    textAlign = TextAlign.Center,
                    style = if (sortContent in arrayOf(ParcelOrder.IdAsc, ParcelOrder.IdDesc))
                        MaterialTheme.typography.titleMedium
                    else
                        MaterialTheme.typography.bodyMedium,
                )
                Icon(
                    modifier = modifier.weight(0.2f),
                    imageVector = icon,
                    contentDescription = null,
                )
            }
        }
    }
}