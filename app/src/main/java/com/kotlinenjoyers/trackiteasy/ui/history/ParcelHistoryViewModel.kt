package com.kotlinenjoyers.trackiteasy.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory.ParcelHistory
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory.ParcelHistoryRepository
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
class ParcelHistoryViewModel(private val parcelRepository: ParcelHistoryRepository) : ViewModel() {
    var orderBy = ParcelOrder.NameAsc
    var filter: String = ""
    /**
     * Holds home ui state. The list of items are retrieved from [ParcelHistoryRepository] and mapped to
     * [HistoryUiState]
     */
    private val dispatcher = Dispatchers.IO
    private val _historyUiState: MutableStateFlow<HistoryUiState> = MutableStateFlow(HistoryUiState())
    val historyUiState: StateFlow<HistoryUiState> = _historyUiState.asStateFlow()

    fun updateState() {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(dispatcher) {
            var queryString = "SELECT * FROM ParcelHistory"
            if (filter != "")
                queryString += " WHERE name LIKE '%$filter%'"
            queryString += " ORDER BY ${orderBy.order}"

            val query = SimpleSQLiteQuery(queryString)
            parcelRepository.getParcelsFilteredSorted(query).collect {
                _historyUiState.update {currentState ->
                    currentState.copy(parcelList = it)
                }
            }
        }
    }
}

/**
 * Ui State for HomeScreen
 */
data class HistoryUiState(val parcelList: List<ParcelHistory> = listOf())