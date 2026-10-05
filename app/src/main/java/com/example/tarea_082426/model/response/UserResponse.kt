package com.example.tarea_082426.model.response

data class UserResponse(
    val id: Int?,
    val token: String?,
    val nombre: String?,
    val apellido: String?,
    val usuario: String?,
    val email: String?,
    val id_rol: Int? = null,
    val rol: String? = null
)
