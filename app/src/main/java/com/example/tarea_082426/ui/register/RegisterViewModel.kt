package com.example.tarea_082426.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarea_082426.data.RegisterRepository
import com.example.tarea_082426.model.RegisterState
import com.example.tarea_082426.model.Role
import com.example.tarea_082426.model.request.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val repository = RegisterRepository()

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state

    init {
        cargarRoles()
    }

    private fun cargarRoles() {
        viewModelScope.launch {
            val result = repository.getRoles()
            result.onSuccess { listaRoles ->
                _state.value = _state.value.copy(
                    rolesDisponibles = listaRoles,
                    rolSeleccionado = listaRoles.find { it.id_rol == 2 } ?: listaRoles.firstOrNull()
                )
            }
        }
    }

    fun onNombreChange(valor: String) { _state.value = _state.value.copy(nombre = valor) }
    fun onApellidoChange(valor: String) { _state.value = _state.value.copy(apellido = valor) }
    fun onUsuarioChange(valor: String) { _state.value = _state.value.copy(usuario = valor) }
    fun onPasswordChange(valor: String) { _state.value = _state.value.copy(password = valor) }
    fun onPassword1Change(valor: String) { _state.value = _state.value.copy(password1 = valor) }
    fun onCorreoChange(valor: String) { _state.value = _state.value.copy(correo = valor) }
    fun onTelefonoChange(valor: String) { _state.value = _state.value.copy(telefono = valor) }
    fun onFechaNacChange(valor: String) { _state.value = _state.value.copy(fechaNac = valor) }
    fun onGeneroChange(valor: String) { _state.value = _state.value.copy(genero = valor) }
    fun onRolChange(role: Role) { _state.value = _state.value.copy(rolSeleccionado = role) }

    fun onRegisterClick() {
        viewModelScope.launch {
            //Si no coinciden no envia peticion a servidor
            if (_state.value.password != _state.value.password1) {
                _state.value = _state.value.copy(
                    mensaje = "Las contraseñas no coinciden",
                    registroExitoso = false
                )
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, mensaje = "")
            
            val request = RegisterRequest(
                nombre = _state.value.nombre,
                apellido = _state.value.apellido,
                usuario = _state.value.usuario,
                password = _state.value.password,
                password1 = _state.value.password1,
                fotoBase64 = "", // TODO: Implementar imagen
                telefono = _state.value.telefono,
                correo = _state.value.correo,
                fechaNac = _state.value.fechaNac,
                genero = _state.value.genero,
                id_rol = _state.value.rolSeleccionado?.id_rol ?: 2,
                role = _state.value.rolSeleccionado?.rol ?: "USER"
            )

            val result = repository.register(request)
            
            result.onSuccess {
                _state.value = _state.value.copy(
                    isLoading = false,
                    registroExitoso = true,
                    mensaje = "Registro exitoso"
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    registroExitoso = false,
                    mensaje = error.message ?: "Error en el registro"
                )
            }
        }
    }
}
