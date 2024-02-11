package com.kotlinenjoyers.trackiteasy.repository.linkingaccounts

import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts.accounts.EndingState
import kotlinx.coroutines.CoroutineScope

interface ILinkingAccountsRepository {
    fun addLinkedAccount(scope: CoroutineScope, container: AppContainer, topLevelDomain: String, email: String, cookies: String, endStateCallback: (EndingState) -> Unit)
}