package com.example.tarea_082426.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarea_082426.data.LocationRepository
import com.example.tarea_082426.model.Departamento
import com.example.tarea_082426.model.LocationState
import com.example.tarea_082426.model.Municipio
import com.example.tarea_082426.model.Pais
import com.example.tarea_082426.model.request.LocationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    private val repository = LocationRepository()

    private val _state = MutableStateFlow(LocationState())
    val state: StateFlow<LocationState> = _state

    init {
        cargarPaises()
    }

    private fun cargarPaises() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.getPaises()
            result.onSuccess { lista ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    paisesDisponibles = lista,
                    paisSeleccionado = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    mensaje = error.message ?: "Error al cargar países"
                )
            }
        }
    }

    fun onPaisSelected(pais: Pais) {
        _state.value = _state.value.copy(
            paisSeleccionado = pais,
            departamentoSeleccionado = null,
            departamentosDisponibles = emptyList(),
            municipioSeleccionado = null,
            municipiosDisponibles = emptyList()
        )
        cargarDepartamentos(pais.id_pais)
    }

    private fun cargarDepartamentos(idPais: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.getDepartamentos(idPais)
            result.onSuccess { lista ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    departamentosDisponibles = lista,
                    departamentoSeleccionado = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    mensaje = error.message ?: "Error al cargar departamentos"
                )
            }
        }
    }

    fun onDepartamentoSelected(dept: Departamento) {
        _state.value = _state.value.copy(
            departamentoSeleccionado = dept,
            municipioSeleccionado = null,
            municipiosDisponibles = emptyList()
        )
        cargarMunicipios(dept.id_departamento)
    }

    private fun cargarMunicipios(idDepartamento: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.getMunicipios(idDepartamento)
            result.onSuccess { lista ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    municipiosDisponibles = lista,
                    municipioSeleccionado = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    mensaje = error.message ?: "Error al cargar municipios"
                )
            }
        }
    }

    fun onMunicipioSelected(muni: Municipio) {
        _state.value = _state.value.copy(municipioSeleccionado = muni)
    }

    fun onNombreChange(valor: String) { _state.value = _state.value.copy(nombre = valor) }
    fun onDescripcionChange(valor: String) { _state.value = _state.value.copy(descripcion = valor) }

    fun onUbicacionSeleccionada(lat: Double, lng: Double) {
        _state.value = _state.value.copy(
            latitud = lat,
            longitud = lng,
            mensaje = ""
        )
    }

    fun guardarLocation(userId: Int) {
        val s = _state.value

        if (s.nombre.isEmpty() || s.descripcion.isEmpty()) {
            _state.value = _state.value.copy(
                mensaje = "Por favor complete nombre y descripción",
                locationGuardadaExitosa = false
            )
            return
        }

        if (s.latitud == null || s.longitud == null) {
            _state.value = _state.value.copy(
                mensaje = "Por favor seleccione la ubicación GPS",
                locationGuardadaExitosa = false
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, mensaje = "")

            val request = LocationRequest(
                nombre = s.nombre,
                apellido = s.descripcion,
                id_user = userId,
                latitud = s.latitud,
                longitud = s.longitud
            )

            val result = repository.guardarLocation(request)

            result.onSuccess {
                _state.value = _state.value.copy(
                    isLoading = false,
                    locationGuardadaExitosa = true,
                    mensaje = "Ubicación registrada exitosamente",
                    nombre = "",
                    descripcion = "",
                    latitud = null,
                    longitud = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    locationGuardadaExitosa = false,
                    mensaje = error.message ?: "Error al guardar ubicación"
                )
            }
        }
    }
}
