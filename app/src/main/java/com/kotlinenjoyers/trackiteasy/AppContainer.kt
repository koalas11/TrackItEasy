package com.kotlinenjoyers.trackiteasy

import android.content.Context
import android.webkit.WebSettings
import androidx.datastore.core.DataStore
import com.kotlinenjoyers.trackiteasy.data.handler.ParcelHandler
import com.kotlinenjoyers.trackiteasy.data.storage.datastore.AccountsDataStore
import com.kotlinenjoyers.trackiteasy.data.storage.datastore.ParsersRetrieversDataStore
import com.kotlinenjoyers.trackiteasy.data.storage.datastore.SettingsDataStore
import com.kotlinenjoyers.trackiteasy.data.storage.room.ParcelDatabase
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.LocalParcelRepository
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.ParcelRepository
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory.LocalParcelHistoryRepository
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory.ParcelHistoryRepository
import com.kotlinenjoyers.trackiteasy.parceldatastore.Accounts
import com.kotlinenjoyers.trackiteasy.parceldatastore.ParsersRetriever
import com.kotlinenjoyers.trackiteasy.settingsdatastore.Settings
import com.kotlinenjoyers.trackiteasy.util.ConnectivityObserver
import com.kotlinenjoyers.trackiteasy.util.NetworkConnectivityObserver
import kotlinx.coroutines.flow.Flow

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val connectivityObserver: Flow<ConnectivityObserver.Status>

    val parcelsRepository: ParcelRepository

    val parcelsHistoryRepository: ParcelHistoryRepository

    val parcelHandler: ParcelHandler

    val settingsDataStore: DataStore<Settings>

    val accountsDataStore: DataStore<Accounts>

    val parsersRetrieversDataStore: DataStore<ParsersRetriever>

    val userAgent: String
}

class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [NetworkConnectivityObserver]
     */
    override val connectivityObserver: Flow<ConnectivityObserver.Status> by lazy {
        NetworkConnectivityObserver(context).observe()
    }

    /**
     * Implementation for [ParcelRepository]
     */
    override val parcelsRepository: ParcelRepository by lazy {
        LocalParcelRepository(ParcelDatabase.getDatabase(context).parcelDao())
    }

    /**
     * Implementation for [ParcelHistoryRepository]
     */
    override val parcelsHistoryRepository: ParcelHistoryRepository by lazy {
        LocalParcelHistoryRepository(ParcelDatabase.getDatabase(context).parcelHistoryDao())
    }

    /**
     * Implementation for [ParcelHandler]
     */
    override val parcelHandler: ParcelHandler by lazy {
        ParcelHandler.getInstance(this)
    }

    /**
     * Implementation for [DataStore<Settings>]
     */
    override val settingsDataStore: DataStore<Settings> by lazy {
        context.SettingsDataStore
    }

    /**
     * Implementation for [DataStore<Accounts>]
     */
    override val accountsDataStore: DataStore<Accounts> by lazy {
        context.AccountsDataStore
    }

    /**
     * Implementation for [DataStore<ParsersRetriever>]
     */
    override val parsersRetrieversDataStore: DataStore<ParsersRetriever> by lazy {
        context.ParsersRetrieversDataStore
    }

    /**
     * Implementation for [userAgent]
     */
    override val userAgent: String by lazy {
        val ua: String = WebSettings.getDefaultUserAgent(context)
        val androidOSString: String = ua.substring(ua.indexOf("("), ua.indexOf(")") + 1)
        val newUserAgent = WebSettings.getDefaultUserAgent(context).replace(androidOSString, "(X11; Linux x86_64)")
        newUserAgent
    }
}
