package com.example.tarea_082426.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarea_082426.data.MapRepository
import com.example.tarea_082426.model.LocationListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationListViewModel : ViewModel() {
    private val repository = MapRepository()
    private val _state = MutableStateFlow(LocationListState())
    val state: StateFlow<LocationListState> = _state

    fun cargarUbicaciones(userId: Int = 0) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, mensaje = "")
            val result = repository.getCensosMarkers(userId)
            result.onSuccess { list ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    locations = list
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    mensaje = error.message ?: "Error al cargar ubicaciones"
                )
            }
        }
    }
}
