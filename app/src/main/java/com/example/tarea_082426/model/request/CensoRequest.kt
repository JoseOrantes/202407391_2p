package com.example.tarea_082426.model.request

data class CensoRequest(
    val nombre: String,
    val apellido: String,
    val total_miembros: Int,
    val cantidad_televisiones: Int,
    val id_municipio: Int = 1,
    val id_user: Int,
    val latitud: Double? = null,
    val longitud: Double? = null
)
