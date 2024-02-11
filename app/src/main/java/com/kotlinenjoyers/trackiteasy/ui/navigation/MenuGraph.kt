package com.kotlinenjoyers.trackiteasy.ui.navigation

import android.webkit.CookieManager
import android.webkit.WebStorage
import androidx.annotation.StringRes
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.ui.menu.MenuScreen
import com.kotlinenjoyers.trackiteasy.ui.menu.SettingsScreen
import com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts.LinkingAccountsScreen
import com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts.accounts.AmazonAccountScreen
import com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts.accounts.AmazonWebViewScreen
import com.kotlinenjoyers.trackiteasy.ui.menu.login.AccountMainScreen

sealed class MenuGraph(val route: String, @get:StringRes val titleRes: Int) {
    data object MenuNested : MenuGraph("menuNested", R.string.menu)
    data object Account : MenuGraph("Account", R.string.account)
    data object LinkingAccounts : MenuGraph("linkingAccounts", R.string.accountsLinking)
    data object LinkingAccountsNested : MenuGraph("linkingAccountsNested", R.string.accountsLinking) {
        data object AmazonLinkingAccounts : MenuGraph("amazonLinking", R.string.amazon_linking)
        data object AmazonWebView : MenuGraph("amazonWebView", R.string.amazon_linking)
    }
    data object Settings : MenuGraph("Settings", R.string.settings)
    data object Support : MenuGraph("Support", 0)
    data object Feedback : MenuGraph("Feedback", 0)
}

fun NavGraphBuilder.menuGraph(
    navLambda: (String) -> Unit,
    navBack: () -> Unit,
) {
    navigation(
        startDestination = Screens.Menu.route,
        route = MenuGraph.MenuNested.route,
    ) {
        composable(route = Screens.Menu.route) {
            MenuScreen(
                navLambda = navLambda,
            )
        }

        composable(route = MenuGraph.Account.route) {
            AccountMainScreen(
                navBack = navBack,
            )
        }

        navigation(
            startDestination = MenuGraph.LinkingAccounts.route,
            route = MenuGraph.LinkingAccountsNested.route,
        ) {
            composable(route = MenuGraph.LinkingAccounts.route) {
                LinkingAccountsScreen(
                    navBack = navBack,
                    navLambda = navLambda,
                )
            }

            composable(route = MenuGraph.LinkingAccountsNested.AmazonLinkingAccounts.route) {
                AmazonAccountScreen(
                    navLambda = navLambda,
                    navBack = navBack,
                )
            }

            composable(route = MenuGraph.Settings.route) {
                SettingsScreen(
                    navBack = navBack,
                )
            }

            composable(route = MenuGraph.Support.route) {
                val localuri = LocalUriHandler.current
                localuri.openUri("https://forms.gle/BxdqxfvmpRLP7Xcb8")
            }

            composable(route = MenuGraph.Feedback.route) {
                val localuri = LocalUriHandler.current
                localuri.openUri("https://forms.gle/J2VkwsXjX4VYmbB19")
            }

            composable(
                route = "${MenuGraph.LinkingAccountsNested.AmazonWebView.route}/{Id}",
                arguments = listOf(navArgument("Id") { type = NavType.StringType })
            ) { backStackEntry ->
                AmazonWebViewScreen(
                    navBack = {
                        WebStorage.getInstance().deleteAllData()
                        CookieManager.getInstance().removeAllCookies(null)
                        CookieManager.getInstance().flush()
                        navBack()
                    },
                    region = backStackEntry.arguments!!.getString("Id")!!
                )
            }
        }
    }
}