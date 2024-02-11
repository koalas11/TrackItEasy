package com.kotlinenjoyers.trackiteasy.ui.menu

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinenjoyers.trackiteasy.settingsdatastore.Settings
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
class SettingsViewModel(private val settingsDataStore: DataStore<Settings>) : ViewModel() {
    /**
     * Holds linking accounts ui state. The list of accounts are retrieved from [DataStore<Accounts>] and mapped to
     * [SettingsUiState]
     */
    private val dispatcherIo = Dispatchers.IO
    private val _settingsUiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()

    init {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(dispatcherIo) {
            settingsDataStore.data.collect {settings ->
                _settingsUiState.update {currentState ->
                    currentState.copy(settings = settings)
                }
            }
        }
    }
}

/**
 * Ui State for HomeScreen
 */
data class SettingsUiState(val settings: Settings = Settings.getDefaultInstance())