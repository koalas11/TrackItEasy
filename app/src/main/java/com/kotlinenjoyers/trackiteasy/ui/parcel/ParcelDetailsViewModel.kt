package com.kotlinenjoyers.trackiteasy.ui.parcel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.data.storage.room.IParcel
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.ParcelRepository
import com.kotlinenjoyers.trackiteasy.ui.navigation.ParcelType
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
class ParcelDetailsViewModel(private val container: AppContainer) : ViewModel() {
    /**
     * Holds the Parcel Details ui state. The item is retrieved from [ParcelRepository] and mapped to
     * [ParcelDetailsUiState]
     */
    private val dispatcherIo = Dispatchers.IO
    private var _parcelDetailsUiState: MutableStateFlow<ParcelDetailsUiState> = MutableStateFlow(ParcelDetailsUiState())
    val parcelDetailsUiState: StateFlow<ParcelDetailsUiState> get() = _parcelDetailsUiState.asStateFlow()

    fun updateState(type: ParcelType, id: String, trackingId: String) {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(dispatcherIo) {
            when (type) {
                ParcelType.Home -> {
                    container.parcelsRepository.getParcelByIdStream(id, trackingId).collect {
                        _parcelDetailsUiState.update { currentState ->
                            currentState.copy(parcel = it)
                        }
                    }
                }
                ParcelType.History -> {
                    container.parcelsHistoryRepository.getParcelByIdStream(id, trackingId).collect {
                        _parcelDetailsUiState.update { currentState ->
                            currentState.copy(parcel = it)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Ui State for ParcelDetailsScreen
 */
data class ParcelDetailsUiState(val parcel: IParcel? = null)