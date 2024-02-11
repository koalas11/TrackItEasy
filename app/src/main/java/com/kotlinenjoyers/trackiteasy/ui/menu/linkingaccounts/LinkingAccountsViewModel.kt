package com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinenjoyers.trackiteasy.parceldatastore.Account
import com.kotlinenjoyers.trackiteasy.parceldatastore.Accounts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve all accounts in the Proto DataStore.
 */
class LinkingAccountsViewModel(private val accountsDataStore: DataStore<Accounts>) : ViewModel() {
    /**
     * Holds linking accounts ui state. The list of accounts are retrieved from [DataStore<Accounts>] and mapped to
     * [LinkingAccountsUiState]
     */
    private val dispatcherIo = Dispatchers.IO
    private val _linkingAccountsUiState: MutableStateFlow<LinkingAccountsUiState> = MutableStateFlow(LinkingAccountsUiState())
    val linkingAccountsUiState: StateFlow<LinkingAccountsUiState> = _linkingAccountsUiState.asStateFlow()

    fun updateState(id: String) {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(dispatcherIo) {
            accountsDataStore.data.collect {accounts ->
                val list = accounts.accountList.filter {
                    it.idParserRetriever == id
                }
                _linkingAccountsUiState.update {currentState ->
                    currentState.copy(accountList = list)
                }
            }
        }
    }
}

/**
 * Ui State for HomeScreen
 */
data class LinkingAccountsUiState(val accountList: List<Account> = listOf())