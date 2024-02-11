package com.kotlinenjoyers.trackiteasy.ui.menu.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.util.ConnectivityObserver

@Composable
fun LoginRegisterScreen(
    modifier: Modifier = Modifier,
    accountState: AccountState,
    paddingValues: PaddingValues,
    changeState: (AccountState) -> Unit,
    networkStatus: ConnectivityObserver.Status,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(paddingValues)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            modifier = modifier.size(200.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.logo_icon),
            contentDescription = null,
        )
        val mainText = if (accountState == AccountState.Login) "Login" else stringResource(id = R.string.register)
        Text(
            modifier = modifier,
            text = mainText,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        if (accountState == AccountState.Login) {
            LoginInputs(
                modifier = modifier,
                changeState = changeState,
                networkStatus = networkStatus,
            )
        } else {
            RegistrationInputs(
                modifier = modifier,
                changeState = changeState,
                networkStatus = networkStatus,
            )
        }

        val secondaryText = if (accountState == AccountState.Login) stringResource(id = R.string.no_account) else stringResource(id = R.string.has_account)
        val tertiaryText = if (accountState == AccountState.Login) stringResource(id = R.string.register_now) else stringResource(id = R.string.login_now)
        val newState = if (accountState == AccountState.Login) AccountState.Register else AccountState.Login
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = modifier,
                text = secondaryText,
            )
            Text(
                modifier = modifier
                    .padding(start = 4.dp)
                    .clickable {
                        changeState(newState)
                    },
                text = tertiaryText,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}