package com.example.tarea_082426.model

data class RegisterState(
    val nombre: String = "",
    val apellido: String = "",
    val usuario: String = "",
    val password: String = "",
    val password1: String = "",
    val correo: String = "",
    val telefono: String = "",
    val fechaNac: String = "",
    val genero: String = "",
    val isLoading: Boolean = false,
    val mensaje: String = "",
    val registroExitoso: Boolean = false,
    val rolesDisponibles: List<Role> = emptyList(),
    val rolSeleccionado: Role? = null
)
