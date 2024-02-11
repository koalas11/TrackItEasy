package com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.TrackItEasyApplication
import com.kotlinenjoyers.trackiteasy.data.handler.Constants.AMAZON_ID
import com.kotlinenjoyers.trackiteasy.repository.linkingaccounts.AmazonRepository
import com.kotlinenjoyers.trackiteasy.ui.AppViewModelProvider
import com.kotlinenjoyers.trackiteasy.ui.common.MenuTopBar
import com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts.LinkingAccountsViewModel
import com.kotlinenjoyers.trackiteasy.ui.navigation.MenuGraph
import com.kotlinenjoyers.trackiteasy.util.ConnectivityObserver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmazonAccountScreen(
    modifier: Modifier = Modifier,
    navLambda: (String) -> Unit,
    navBack: () -> Unit,
    viewModel: LinkingAccountsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val container = (LocalContext.current.applicationContext as TrackItEasyApplication).container
    val connectivityObserver = container.connectivityObserver
    val networkStatus by connectivityObserver.collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    )
    var selectedText by rememberSaveable {
        mutableStateOf("com")
    }

    LaunchedEffect(Unit) {
        viewModel.updateState(AMAZON_ID)
    }
    val accountsState by viewModel.linkingAccountsUiState.collectAsState()

    Scaffold(
        topBar = {
            MenuTopBar(
                modifier = modifier,
                title = stringResource(MenuGraph.LinkingAccountsNested.AmazonLinkingAccounts.titleRes),
                navBack = navBack,
                networkStatus = networkStatus,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(innerPadding),
            verticalArrangement = spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Row(
                    modifier = modifier.padding(16.dp),
                    horizontalArrangement = spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    var expanded by remember {
                        mutableStateOf(false)
                    }
                    Text(text = stringResource(id = R.string.region) + ":", style = MaterialTheme.typography.titleLarge)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedText.uppercase(),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = {
                                Text(text = "COM")
                            },
                                onClick = {
                                    selectedText = "com"
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = "IT")
                                },
                                onClick = {
                                    selectedText = "it"
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Button(
                    enabled = networkStatus == ConnectivityObserver.Status.Available,
                    onClick = { navLambda(MenuGraph.LinkingAccountsNested.AmazonWebView.route + "/$selectedText") },
                ) {
                    Text(text = stringResource(id = R.string.link_account))
                }
                HorizontalDivider(
                    modifier = modifier.padding(8.dp),
                    thickness = 8.dp
                )
                Text(text = stringResource(id = R.string.linked_accounts))
                HorizontalDivider(
                    modifier = modifier.padding(8.dp),
                    thickness = 8.dp
                )
            }

            if (accountsState.accountList.isEmpty()) {
                item {
                    Column(
                        modifier = modifier
                            .fillParentMaxSize(0.6f)
                            .wrapContentSize(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = modifier.weight(0.3f),
                            text = "NO ACCOUNTS",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.displaySmall,
                        )
                        Icon(
                            modifier = modifier
                                .weight(0.7f, true)
                                .fillMaxSize(),
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = null,
                        )
                    }
                }
            } else {
                items(accountsState.accountList) {
                    Card(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = modifier,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    modifier = modifier.weight(0.1f),
                                    imageVector = ImageVector.vectorResource(R.drawable.amazon_icon),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                )
                                Column(
                                    modifier = modifier
                                        .padding(8.dp)
                                        .weight(0.9f),
                                    verticalArrangement = spacedBy(1.dp)
                                ) {
                                    Text(
                                        text = "Account: " + it.info,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = "${stringResource(id = R.string.region)}: ${it.extraInfo}",
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                            if (!it.active)
                                Row(
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = spacedBy(8.dp),
                                ) {
                                    Icon(
                                        modifier = modifier,
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                    Text(
                                        text = stringResource(id = R.string.relog),
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            val scope = rememberCoroutineScope()
                            Button(
                                modifier = modifier,
                                onClick = {
                                    AmazonRepository.removeAccount(scope, container, it)
                                }) {
                                Text(text = stringResource(id = R.string.remove))
                            }
                        }
                    }
                }
            }
        }
    }
}