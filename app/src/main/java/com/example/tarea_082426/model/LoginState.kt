package com.example.tarea_082426.model

import android.graphics.Bitmap

data class LoginState(
    val id: Int = 0,
    val email: String = "",
    val password: String = "",
    val mensaje: String = "",
    val loginExitoso: Boolean = false,
    val token: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val usuario: String = "",
    val id_rol: Int = 0,
    val rol: String = "",
    // Estado de verificación facial
    val isLivenessActive: Boolean = false,
    val livenessSessionId: String? = null,
    val isLivenessLoading: Boolean = false,
    val faceBitmap: Bitmap? = null
)
