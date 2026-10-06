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
    private val repository = CensoRepository()

    private val _state = MutableStateFlow(CensoState())
    val state: StateFlow<CensoState> = _state

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
    fun onApellidoChange(valor: String) { _state.value = _state.value.copy(apellido = valor) }
    fun onTotalMiembrosChange(valor: String) { _state.value = _state.value.copy(totalMiembros = valor) }
    fun onCantidadTVsChange(valor: String) { _state.value = _state.value.copy(cantidadTVs = valor) }

    fun guardarCenso(userId: Int) {
        val s = _state.value
        if (s.nombre.isEmpty() || s.apellido.isEmpty() || s.municipioSeleccionado == null) {
            _state.value = _state.value.copy(
                mensaje = "Por favor complete todos los campos obligatorios",
                censoGuardadoExitoso = false
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, mensaje = "")

            val request = CensoRequest(
                nombre = s.nombre,
                apellido = s.apellido,
                total_miembros = s.totalMiembros.toIntOrNull() ?: 1,
                cantidad_televisiones = s.cantidadTVs.toIntOrNull() ?: 0,
                id_municipio = s.municipioSeleccionado.id_municipio,
                id_user = userId
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
                    cantidadTVs = ""
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
