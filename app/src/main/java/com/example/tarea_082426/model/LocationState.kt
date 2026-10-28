package com.example.tarea_082426.model

data class LocationState(
    val nombre: String = "",
    val descripcion: String = "",
    val totalMiembros: String = "",
    val cantidadTVs: String = "",
    val paisesDisponibles: List<Pais> = emptyList(),
    val paisSeleccionado: Pais? = null,
    val departamentosDisponibles: List<Departamento> = emptyList(),
    val departamentoSeleccionado: Departamento? = null,
    val municipiosDisponibles: List<Municipio> = emptyList(),
    val municipioSeleccionado: Municipio? = null,
    // Coordenadas seleccionadas en mapa
    val latitud: Double? = null,
    val longitud: Double? = null,
    val isLoading: Boolean = false,
    val mensaje: String = "",
    val locationGuardadaExitosa: Boolean = false
)
