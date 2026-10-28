package com.example.tarea_082426.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarea_082426.data.MapRepository
import com.example.tarea_082426.model.MapState
import com.example.tarea_082426.model.MarkerItem
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val repository = MapRepository()

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state

    fun cargarCensosDesdeAPI(userId: Int = 0) {
        viewModelScope.launch {
            val result = repository.getCensosMarkers(userId)
            result.onSuccess { list ->
                cargarMarcadores(list)
            }
        }
    }

    fun cargarMarcadores(marcadores: List<MarkerItem>) {
        _state.value = _state.value.copy(markers = marcadores)

        if (marcadores.isNotEmpty()) {
            _state.value = _state.value.copy(
                cameraPosition = marcadores.first().position,
                zoom = 10f
            )
        }
    }

    fun agregarMarcador(marcador: MarkerItem) {
        val listaActual = _state.value.markers
        _state.value = _state.value.copy(markers = listaActual + marcador)
    }

    fun limpiarMarcadores() {
        _state.value = _state.value.copy(markers = emptyList())
    }

    fun centrar(latLng: LatLng, zoom: Float) {
        _state.value = _state.value.copy(cameraPosition = latLng, zoom = zoom)
    }
}
