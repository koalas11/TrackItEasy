package com.kotlinenjoyers.trackiteasy.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotlinenjoyers.trackiteasy.BuildConfig
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.repository.SettingsRepository
import com.kotlinenjoyers.trackiteasy.ui.AppViewModelProvider
import com.kotlinenjoyers.trackiteasy.ui.common.MenuTopBar
import com.kotlinenjoyers.trackiteasy.ui.navigation.MenuGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            MenuTopBar(
                modifier = modifier,
                title = stringResource(MenuGraph.Settings.titleRes),
                navBack = navBack,
            )
        },
    ) { innerPadding ->
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val state by viewModel.settingsUiState.collectAsState()
        var intervalRetrieving by rememberSaveable {
            mutableLongStateOf(state.settings.intervalRetrieving)
        }
        var intervalMoveToHistory by rememberSaveable {
            mutableLongStateOf(state.settings.intervalMoveToHistory)
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                text = stringResource(id = R.string.general_settings),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var expanded by remember {
                    mutableStateOf(false)
                }
                Text(
                    modifier = modifier.weight(0.5f, false),
                    text = "${stringResource(id = R.string.interval_retrieving)}: ",
                    style = MaterialTheme.typography.titleLarge
                )
                ExposedDropdownMenuBox(
                    modifier = modifier.weight(0.5f, false),
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = "$intervalRetrieving ${stringResource(id = R.string.minutes)}",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = {
                            Text(text = "15 ${stringResource(id = R.string.minutes)}")
                        },
                            onClick = {
                                intervalRetrieving = 15
                                expanded = false
                                SettingsRepository.updateSettings(context, scope, intervalRetrieving, intervalMoveToHistory)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(text = "30 ${stringResource(id = R.string.minutes)}")
                            },
                            onClick = {
                                intervalRetrieving = 30
                                expanded = false
                                SettingsRepository.updateSettings(context, scope, intervalRetrieving, intervalMoveToHistory)
                            }
                        )
                    }
                }
            }
            Row(
                modifier = modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var expanded by remember {
                    mutableStateOf(false)
                }
                Text(
                    modifier = modifier.weight(0.5f, false),
                    text = "${stringResource(id = R.string.interval_move)}: ",
                    style = MaterialTheme.typography.titleLarge,
                )
                ExposedDropdownMenuBox(
                    modifier = modifier.weight(0.5f, false),
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = "$intervalMoveToHistory ${stringResource(id = R.string.hours)}",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = {
                            Text(text = "24 ${stringResource(id = R.string.hours)}")
                        },
                            onClick = {
                                intervalMoveToHistory = 24
                                expanded = false
                                SettingsRepository.updateSettings(context, scope, intervalRetrieving, intervalMoveToHistory)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(text = "48 ${stringResource(id = R.string.hours)}")
                            },
                            onClick = {
                                intervalMoveToHistory = 48
                                expanded = false
                                SettingsRepository.updateSettings(context, scope, intervalRetrieving, intervalMoveToHistory)
                            }
                        )
                    }
                }
            }
            HorizontalDivider(
                modifier = modifier,
                thickness = 4.dp,
            )
            Text(
                modifier = modifier.fillMaxWidth(),
                text = "Version : ${BuildConfig.VERSION_NAME}",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}