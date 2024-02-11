package com.kotlinenjoyers.trackiteasy.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kotlinenjoyers.trackiteasy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelFilterBottomSheet(
    modifier: Modifier = Modifier,
    viewModelFilter: String,
    closeSheet: () -> Unit,
    setFilter: (String) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState()

    var filter by remember {
        mutableStateOf(viewModelFilter)
    }

    ModalBottomSheet(
        modifier = modifier.wrapContentSize(Alignment.Center),
        onDismissRequest = closeSheet,
        sheetState = bottomSheetState,
    ) {
        TextField(
            modifier = modifier
                .padding(bottom = 32.dp, top = 8.dp)
                .fillMaxWidth(0.8f)
                .align(Alignment.CenterHorizontally),
            value = filter,
            onValueChange = {
                filter = it
                setFilter(it)
            },
            label = { Text(stringResource(id = R.string.filter)) },
            enabled = true,
            singleLine = true
        )
    }
}