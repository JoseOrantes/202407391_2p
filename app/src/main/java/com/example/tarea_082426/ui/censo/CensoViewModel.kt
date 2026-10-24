package com.example.tarea_082426.ui.censo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarea_082426.data.CensoRepository
import com.example.tarea_082426.model.CensoState
import com.example.tarea_082426.model.Departamento
import com.example.tarea_082426.model.Municipio
import com.example.tarea_082426.model.Pais
import com.example.tarea_082426.model.request.CensoRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CensoViewModel : ViewModel() {
    // Repositorio y estado UI
    private val repository = CensoRepository()

    private val _state = MutableStateFlow(CensoState())
    val state: StateFlow<CensoState> = _state

    init {
        // Carga inicial de datos
        cargarPaises()
    }

    // Carga de lista de países
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

    // Selección de país
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

    // Carga de departamentos por país
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

    // Selección de departamento
    fun onDepartamentoSelected(dept: Departamento) {
        _state.value = _state.value.copy(
            departamentoSeleccionado = dept,
            municipioSeleccionado = null,
            municipiosDisponibles = emptyList()
        )
        cargarMunicipios(dept.id_departamento)
    }

    // Carga de municipios por departamento
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

    // Selección de municipio
    fun onMunicipioSelected(muni: Municipio) {
        _state.value = _state.value.copy(municipioSeleccionado = muni)
    }

    // Actualización de campos de texto
    fun onNombreChange(valor: String) { _state.value = _state.value.copy(nombre = valor) }
    fun onApellidoChange(valor: String) { _state.value = _state.value.copy(apellido = valor) }
    fun onTotalMiembrosChange(valor: String) { _state.value = _state.value.copy(totalMiembros = valor) }
    fun onCantidadTVsChange(valor: String) { _state.value = _state.value.copy(cantidadTVs = valor) }

    // Selección de ubicación en mapa
    fun onUbicacionSeleccionada(lat: Double, lng: Double) {
        _state.value = _state.value.copy(
            latitud = lat,
            longitud = lng,
            mensaje = ""
        )
    }

    // Guardado de censo en servidor
    fun guardarCenso(userId: Int) {
        val s = _state.value

        // Validar campos de texto
        if (s.nombre.isEmpty() || s.apellido.isEmpty()) {
            _state.value = _state.value.copy(
                mensaje = "Por favor complete nombre y apellido",
                censoGuardadoExitoso = false
            )
            return
        }

        // Validar selección de ubicación
        if (s.latitud == null || s.longitud == null) {
            _state.value = _state.value.copy(
                mensaje = "Por favor haga tap en el mapa para ubicar el censo",
                censoGuardadoExitoso = false
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, mensaje = "")

            // Petición de censo
            val request = CensoRequest(
                nombre = s.nombre,
                apellido = s.apellido,
                total_miembros = s.totalMiembros.toIntOrNull() ?: 1,
                cantidad_televisiones = s.cantidadTVs.toIntOrNull() ?: 0,
                // id_municipio = s.municipioSeleccionado?.id_municipio,
                id_user = userId,
                latitud = s.latitud,
                longitud = s.longitud
            )

            val result = repository.guardarCenso(request)

            result.onSuccess {
                _state.value = _state.value.copy(
                    isLoading = false,
                    censoGuardadoExitoso = true,
                    mensaje = "Censo registrado exitosamente",
                    nombre = "",
                    apellido = "",
                    totalMiembros = "",
                    cantidadTVs = "",
                    latitud = null,
                    longitud = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    censoGuardadoExitoso = false,
                    mensaje = error.message ?: "Error al guardar el censo"
                )
            }
        }
    }
}
