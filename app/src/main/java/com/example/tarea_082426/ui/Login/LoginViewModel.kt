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

    //_state sera la privada y state es el acceso para los demas
    private val repository = LoginRepository()

    private val _state = MutableStateFlow(LoginState())

    val state: StateFlow<LoginState> = _state

    fun onEmailChange(nuevoEmail: String){
        _state.value = _state.value.copy(email = nuevoEmail)
    }

    fun onPasswordChange(nuevoPassword: String){
        _state.value = _state.value.copy(password = nuevoPassword)
    }

    //Parametros vienen del modelo LoginState
    fun onLoginClick(context: Context){
        viewModelScope.launch {
            val resultado = repository.login(context, _state.value.email.trim(), _state.value.password.trim())

            resultado.onSuccess { response ->
                val user = response.body?.user
                val rawIdRol = user?.id_rol
                val rawRol = user?.rol

                val idRolReal = rawIdRol ?: if (user?.id == 1 || user?.email?.contains("admin", ignoreCase = true) == true || user?.usuario?.contains("admin", ignoreCase = true) == true) 1 else 2
                val rolReal = if (!rawRol.isNullOrEmpty()) rawRol else if (idRolReal == 1) "ADMIN" else "USER"

                _state.value = _state.value.copy(
                    loginExitoso = true,
                    id = user?.id ?: 0,
                    email = user?.email ?: "",
                    token = response.body?.token ?: "",
                    nombre = user?.nombre ?: "",
                    apellido = user?.apellido ?: "",
                    usuario = user?.usuario ?: "",
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
