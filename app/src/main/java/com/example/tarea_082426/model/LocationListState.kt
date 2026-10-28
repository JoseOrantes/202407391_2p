package com.example.tarea_082426.model

data class LocationListState(
    val locations: List<MarkerItem> = emptyList(),
    val isLoading: Boolean = false,
    val mensaje: String = ""
)
