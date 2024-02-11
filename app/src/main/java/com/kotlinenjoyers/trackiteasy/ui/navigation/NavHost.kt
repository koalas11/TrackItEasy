package com.kotlinenjoyers.trackiteasy.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.data.storage.room.IParcel
import com.kotlinenjoyers.trackiteasy.ui.history.HistoryScreen
import com.kotlinenjoyers.trackiteasy.ui.home.HomeScreen
import com.kotlinenjoyers.trackiteasy.ui.home.ParcelEntryScreen
import com.kotlinenjoyers.trackiteasy.ui.parcel.ParcelDetailsScreen

/**
 * Provides Navigation graph for the application.
 */

sealed class Screens(val route: String, @get:StringRes val titleRes: Int) {
    data object Home : Screens("home", R.string.home)
    data object HomeNested : Screens("homeNested", R.string.home) {
        data object ParcelEntry : Screens("parcelEntry", R.string.parcel_entry_title)
        data object ParcelDetails : Screens("parcelHomeDetails", R.string.parcel_details)
    }
    data object History : Screens("history", R.string.history)
    data object Menu : Screens("menu", R.string.menu)
}

enum class FindingStatus {
    Ready, Loading, NotFound, Found, AlreadySearching
}

enum class OpenSheet {
    Closed, Order, Filter
}

enum class ParcelType {
    Home, History
}

@Composable
fun ParcelNavHost(
    modifier: Modifier = Modifier,
) {
    val navController: NavHostController = rememberNavController()

    val navLambda: (String) -> Unit = { route ->
        navController.navigate(route = route)
    }
    val navLambdaToParcelDetails: (ParcelType, IParcel) -> Unit = { type, p ->
        val trackingId = p.trackingId.replace("?", "£")
        navController.navigate(
            route = Screens.HomeNested.ParcelDetails.route + "/${type.name}/${p.id}/$trackingId"
        )
    }
    val navBack: () -> Unit = { navController.popBackStack() }
    val appScope = rememberCoroutineScope()

    var findingParcel by rememberSaveable {
        mutableStateOf(FindingStatus.Ready)
    }

    val startFindingParcel: () -> Unit = {
        findingParcel = if (findingParcel == FindingStatus.Ready)
            FindingStatus.Loading
        else
            FindingStatus.Ready
    }

    val finishFindingParcel: (FindingStatus) -> Unit = {
        findingParcel = it
    }

    NavHost(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = Screens.Home.route,
    ) {
        composable(route = Screens.Home.route) {
            HomeScreen(
                navLambda = navLambda,
                navParcelDetails = navLambdaToParcelDetails,
                scope = appScope,
            )
        }

        composable(route = Screens.HomeNested.ParcelEntry.route) {
            ParcelEntryScreen(
                navBack = navBack,
                scope = appScope,
                findingParcel = findingParcel,
                startFindingParcel = startFindingParcel,
                finishFindingParcel = finishFindingParcel,
            )
        }

        composable(
            route = Screens.HomeNested.ParcelDetails.route + "/{type}/{id}/{trackingId}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType },
                navArgument("trackingId") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val type = ParcelType.valueOf(backStackEntry.arguments?.getString("type")!!)
            val id = backStackEntry.arguments?.getString("id")!!
            val trackingId = backStackEntry.arguments?.getString("trackingId")!!.replace("£", "?")
            ParcelDetailsScreen(
                navBack = navBack,
                type = type,
                id = id,
                trackingId = trackingId,
            )
        }

        composable(route = Screens.History.route) {
            HistoryScreen(
                navLambda = navLambda,
                navParcelDetails = navLambdaToParcelDetails,
            )
        }

        menuGraph(navLambda = navLambda, navBack = navBack)
    }
}