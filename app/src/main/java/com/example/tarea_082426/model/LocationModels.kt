package com.example.tarea_082426.model

data class Pais(
    val id_pais: String,
    val nombre: String
)

data class Departamento(
    val id_departamento: Int,
    val nombre: String,
    val id_pais: String
)

data class Municipio(
    val id_municipio: Int,
    val id_departamento: Int,
    val nombre: String,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0
)
