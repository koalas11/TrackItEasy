package com.kotlinenjoyers.trackiteasy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.Parcel
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.ParcelRepository
import com.kotlinenjoyers.trackiteasy.ui.common.ParcelOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve all items in the Room database.
 */
class ParcelViewModel(private val parcelRepository: ParcelRepository) : ViewModel() {
    var orderBy = ParcelOrder.NameAsc
    var filter: String = ""
    /**
     * Holds home ui state. The list of items are retrieved from [ParcelRepository] and mapped to
     * [HomeUiState]
     */
    private val dispatcherIo = Dispatchers.IO
    private val _homeUiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    fun updateState() {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(dispatcherIo) {
            var queryString = "SELECT * FROM parcel"
            if (filter != "")
                queryString += " WHERE name LIKE '%$filter%'"
            queryString += " ORDER BY ${orderBy.order}"

            val query = SimpleSQLiteQuery(queryString)
            parcelRepository.getParcelsFilteredSorted(query).collect {
                _homeUiState.update {currentState ->
                    currentState.copy(parcelList = it)
                }
            }
        }
    }
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(val parcelList: List<Parcel> = listOf())