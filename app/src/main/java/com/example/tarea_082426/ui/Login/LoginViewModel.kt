package com.example.tarea_082426.ui.Login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarea_082426.data.LoginRepository
import com.example.tarea_082426.model.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = LoginRepository()
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    // Cambio de correo
    fun onEmailChange(nuevoEmail: String){
        _state.value = _state.value.copy(email = nuevoEmail)
    }

    // Cambio de contraseña
    fun onPasswordChange(nuevoPassword: String){
        _state.value = _state.value.copy(password = nuevoPassword)
    }

    // Petición de inicio de sesión
    fun onLoginClick(context: Context){
        viewModelScope.launch {
            val inputEmail = _state.value.email.trim()
            val resultado = repository.login(context, inputEmail, _state.value.password.trim())

            resultado.onSuccess { response ->
                val user = response.body?.user
                val rawIdRol = user?.id_rol
                val rawRol = user?.rol

                val idRolReal = rawIdRol ?: 2
                val rolReal = if (!rawRol.isNullOrEmpty()) rawRol else if (idRolReal == 1) "ADMIN" else "USER"

                val userEmail = user?.email?.ifEmpty { null } ?: user?.usuario?.ifEmpty { null } ?: inputEmail
                val userUsuario = user?.usuario?.ifEmpty { null } ?: userEmail

                _state.value = _state.value.copy(
                    loginExitoso = true,
                    id = user?.id ?: 0,
                    email = userEmail,
                    token = response.body?.token ?: "",
                    nombre = user?.nombre ?: "",
                    apellido = user?.apellido ?: "",
                    usuario = userUsuario,
                    id_rol = idRolReal,
                    rol = rolReal
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    loginExitoso = false,
                    mensaje = error.message ?: "Error desconocido"
                )
            }
        }
    }
}
