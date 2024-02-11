package com.kotlinenjoyers.trackiteasy.ui.menu.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SentimentDissatisfied
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kotlinenjoyers.trackiteasy.TrackItEasyApplication
import com.kotlinenjoyers.trackiteasy.ui.MainActivity
import com.kotlinenjoyers.trackiteasy.ui.common.MenuTopBar
import com.kotlinenjoyers.trackiteasy.ui.navigation.MenuGraph
import com.kotlinenjoyers.trackiteasy.util.ConnectivityObserver

enum class AccountState {
    Logged, Login, Register, Error
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountMainScreen(
    modifier: Modifier = Modifier,
    navBack: () -> Unit,
) {
    var accountState by rememberSaveable {
        mutableStateOf(AccountState.Login)
    }
    val context = LocalContext.current
    val connectivityObserver = (context.applicationContext as TrackItEasyApplication).container.connectivityObserver
    val networkStatus by connectivityObserver.collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    )

    val auth by remember {
        mutableStateOf((context as MainActivity).auth)
    }

   LaunchedEffect(Unit) {
       if (auth.currentUser != null)
            accountState = AccountState.Logged
   }

    val changeState: (AccountState) -> Unit = {
        accountState = it
    }

    Scaffold(
        topBar = {
            MenuTopBar(
                modifier = modifier,
                title = stringResource(MenuGraph.Account.titleRes),
                navBack = navBack,
                networkStatus = networkStatus
            )
        },
    ) { innerPadding ->
        when (accountState) {
            AccountState.Logged -> {
                AccountScreen(
                    modifier = modifier,
                    paddingValues = innerPadding,
                    changeState = changeState,
                    networkStatus = networkStatus,
                )
            }
            AccountState.Error -> {
                Column(
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        modifier = modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            modifier = modifier.weight(0.4f),
                            text = "Error !",
                            style = MaterialTheme.typography.displayMedium,
                        )
                        Icon(
                            modifier = modifier
                                .weight(0.6f, true)
                                .fillMaxSize(),
                            imageVector = Icons.Rounded.SentimentDissatisfied,
                            contentDescription = null
                        )
                    }
                }
            }
            else -> {
                LoginRegisterScreen(
                    modifier = modifier,
                    accountState = accountState,
                    paddingValues = innerPadding,
                    changeState = changeState,
                    networkStatus = networkStatus,
                )
            }
        }
    }
}