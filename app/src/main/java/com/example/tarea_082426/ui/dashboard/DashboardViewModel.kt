package com.example.tarea_082426.ui.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DashboardState(
    val nombreCenso: String = "",
    val apellidoCenso: String = "",
    val totalMiembros: String = "",
    val cantidadTVs: String = "",
    val mensajeCenso: String = "",
    val censoGuardado: Boolean = false
)

class DashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state

    fun onNombreCensoChange(valor: String) { _state.value = _state.value.copy(nombreCenso = valor) }
    fun onApellidoCensoChange(valor: String) { _state.value = _state.value.copy(apellidoCenso = valor) }
    fun onTotalMiembrosChange(valor: String) { _state.value = _state.value.copy(totalMiembros = valor) }
    fun onCantidadTVsChange(valor: String) { _state.value = _state.value.copy(cantidadTVs = valor) }

    fun guardarCenso() {
        if (_state.value.nombreCenso.isEmpty() || _state.value.apellidoCenso.isEmpty()) {
            _state.value = _state.value.copy(mensajeCenso = "Complete los campos obligatorios del censo")
            return
        }
        _state.value = _state.value.copy(
            mensajeCenso = "Censo registrado exitosamente",
            censoGuardado = true,
            nombreCenso = "",
            apellidoCenso = "",
            totalMiembros = "",
            cantidadTVs = ""
        )
    }
}
