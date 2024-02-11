package com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.ui.common.MenuTopBar
import com.kotlinenjoyers.trackiteasy.ui.navigation.MenuGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkingAccountsScreen(
    modifier: Modifier = Modifier,
    navLambda: (String) -> Unit,
    navBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            MenuTopBar(
                modifier = modifier,
                title = stringResource(MenuGraph.LinkingAccounts.titleRes),
                navBack = navBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Amazon Account",
                    modifier = Modifier.size(48.dp)
                )
                TextButton(onClick = { navLambda(MenuGraph.LinkingAccountsNested.AmazonLinkingAccounts.route) }  ) {
                    Text(text = stringResource(id = R.string.amazon_linking), fontSize = 20.sp)
                }
            }
        }
    }
}