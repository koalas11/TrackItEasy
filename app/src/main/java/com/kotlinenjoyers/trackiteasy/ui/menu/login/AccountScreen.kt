package com.kotlinenjoyers.trackiteasy.ui.menu.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.repository.firebase.FireStoreRepository
import com.kotlinenjoyers.trackiteasy.repository.firebase.FirebaseAuthRepository
import com.kotlinenjoyers.trackiteasy.ui.MainActivity
import com.kotlinenjoyers.trackiteasy.util.ConnectivityObserver

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    changeState: (AccountState) -> Unit,
    networkStatus: ConnectivityObserver.Status,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by rememberSaveable {
        mutableStateOf(false)
    }
    var state: String? by rememberSaveable {
        mutableStateOf(null)
    }
    val setState: (String) -> Unit = {
        state = it
        loading = false
    }
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
        val auth = (context as MainActivity).auth
        Icon(
            modifier = modifier.size(200.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.logo_icon),
            contentDescription = null,
        )
        Text(
            modifier = modifier,
            text = stringResource(id = R.string.my_account),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall,
        )
        Text(
            modifier = modifier,
            text = "Email: ${auth.currentUser!!.email}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )

        HorizontalDivider(
            modifier = modifier,
            thickness = 8.dp
        )
        Text(
            modifier = modifier,
            text = stringResource(id = R.string.actions),
            textAlign = TextAlign.Center,
        )
        HorizontalDivider(
            modifier = modifier,
            thickness = 8.dp
        )
        val newModifier = modifier.fillMaxWidth(0.8f)
        Button(
            modifier = newModifier,
            onClick = {
                loading = true
                FireStoreRepository.loadToCloud(context, auth, scope, setState)
            },
            enabled = networkStatus == ConnectivityObserver.Status.Available,
        ) {
            Text(
                modifier = modifier,
                text = stringResource(id = R.string.load_to_cloud),
            )
        }
        Button(
            modifier = newModifier,
            onClick = {
                loading = true
                FireStoreRepository.fetchParcels(context, auth, scope, setState)
            },
            enabled = networkStatus == ConnectivityObserver.Status.Available,
        ) {
            Text(
                modifier = modifier,
                text = stringResource(id = R.string.retrive_from_cloud),
            )
        }
        Button(
            modifier = newModifier,
            onClick = {
                loading = true
                FireStoreRepository.clearCloud(auth, scope, setState)
            },
            enabled = networkStatus == ConnectivityObserver.Status.Available,
        ) {
            Text(
                modifier = modifier,
                text = stringResource(id = R.string.clear_cloud),
            )
        }
        Button(
            modifier = newModifier,
            onClick = {
                loading = true
                FirebaseAuthRepository.setNewPassword(auth, auth.currentUser!!.email!!, scope, setState)
            },
            enabled = networkStatus == ConnectivityObserver.Status.Available,
        ) {
            Text(
                modifier = modifier,
                text = stringResource(id = R.string.reset_pw),
            )
        }
        Button(
            modifier = newModifier,
            onClick = {
                FirebaseAuthRepository.signOut(auth, scope, changeState)
            },
        ) {
            Text(
                modifier = modifier,
                text = stringResource(id = R.string.sign_out),
            )
        }
        if (state != null)
            Text(
                modifier = modifier,
                text = state!!,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        if (loading)
            CircularProgressIndicator(
                modifier = modifier
                    .fillMaxWidth(0.2f),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
    }
}