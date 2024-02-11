package com.kotlinenjoyers.trackiteasy.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kotlinenjoyers.trackiteasy.TrackItEasyApplication
import com.kotlinenjoyers.trackiteasy.ui.history.ParcelHistoryViewModel
import com.kotlinenjoyers.trackiteasy.ui.home.ParcelViewModel
import com.kotlinenjoyers.trackiteasy.ui.menu.SettingsViewModel
import com.kotlinenjoyers.trackiteasy.ui.menu.linkingaccounts.LinkingAccountsViewModel
import com.kotlinenjoyers.trackiteasy.ui.parcel.ParcelDetailsViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ParcelViewModel(trackItEasyApplication().container.parcelsRepository)
        }

        initializer {
            ParcelHistoryViewModel(trackItEasyApplication().container.parcelsHistoryRepository)
        }

        initializer {
            ParcelDetailsViewModel(trackItEasyApplication().container)
        }

        initializer {
            LinkingAccountsViewModel(trackItEasyApplication().container.accountsDataStore)
        }

        initializer {
            SettingsViewModel(trackItEasyApplication().container.settingsDataStore)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [TrackItEasyApplication].
 */
fun CreationExtras.trackItEasyApplication(): TrackItEasyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TrackItEasyApplication)



