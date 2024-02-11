package com.kotlinenjoyers.trackiteasy.ui.common

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kotlinenjoyers.trackiteasy.ui.navigation.Screens

/**
 * Customizable BottomUpBar (mostra le 3 icone di base e la principale è evidenziata)
 */

enum class NavIcon {
    Home, History, Menu
}

@Composable
fun ParcelBottomAppBar(
    modifier: Modifier = Modifier,
    navLambda: (String) -> Unit,
    selected: NavIcon,
) {
    BottomAppBar {
        NavigationBar(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ) {
            NavigationBarItem(
                modifier = modifier
                    .weight(1f)
                    .fillMaxHeight(0.9f),
                icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                label = { Text(stringResource(Screens.Home.titleRes)) },
                selected = selected == NavIcon.Home,
                enabled = selected != NavIcon.Home,
                onClick = { navLambda(Screens.Home.route) }
            )
            NavigationBarItem(
                modifier = modifier
                    .weight(1f)
                    .fillMaxHeight(0.9f),
                icon = { Icon(Icons.Filled.History, contentDescription = null) },
                label = { Text(stringResource(Screens.History.titleRes)) },
                selected = selected == NavIcon.History,
                enabled = selected != NavIcon.History,
                onClick = { navLambda(Screens.History.route) }
            )
            NavigationBarItem(
                modifier = modifier
                    .weight(1f)
                    .fillMaxHeight(0.9f),
                icon = { Icon(Icons.Filled.Menu, contentDescription = null) },
                label = { Text(stringResource(Screens.Menu.titleRes)) },
                selected = selected == NavIcon.Menu,
                enabled = selected != NavIcon.Menu,
                onClick = { navLambda(Screens.Menu.route) }
            )
        }
    }
}