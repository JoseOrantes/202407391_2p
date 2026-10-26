package com.example.tarea_082426.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarea_082426.model.ProfileState
import com.example.tarea_082426.data.ProfileRepository
import com.example.tarea_082426.model.request.ProfileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ProfileViewModel : ViewModel() {
    private val repository = ProfileRepository()

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    fun loadProfile(id: Int, context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val userResult = repository.getUserOfflineFirst(context, id)
            // val profileResult = repository.getProfile(id)
            val profileResult = repository.getProfileOfflineFirst(context, id)

            // CODIGO ORIGINAL:
            // if (userResult.isSuccess && profileResult.isSuccess) {
            //     val userLocal = userResult.getOrNull()
            //     val profileBody = profileResult.getOrNull()?.body
            //
            //     _state.value = _state.value.copy(
            //         isLoading = false,
            //         id = id,
            //         userId = id,
            //         nombre = userLocal?.nombre ?: "",
            //         apellido = userLocal?.apellido ?: "",
            //         usuario = userLocal?.id_user?.toString() ?: "", // O usar usuario si se agrega a la entidad User
            //         fotoBase64 = profileBody?.fotoBase64 ?: "",
            //         telefono = profileBody?.telefono ?: "",
            //         correo = userLocal?.nombre ?: "", // Nota: Ajustar según campos disponibles en User entity
            //         fechaNac = profileBody?.fechaNac ?: "",
            //         genero = profileBody?.genero ?: ""
            //     )
            // } else {
            //     _state.value = _state.value.copy(
            //         isLoading = false,
            //         error = "Error al cargar datos"
            //     )
            // }

            // CORRECCION:
            val userLocal = userResult.getOrNull()
            val profileBody = profileResult.getOrNull()?.body

            if (userResult.isSuccess || profileResult.isSuccess) {
                val base64 = profileBody?.fotoBase64 ?: ""
                val bitmap = base64ToBitmap(base64)

                _state.value = _state.value.copy(
                    isLoading = false,
                    id = id,
                    userId = id,
                    nombre = userLocal?.nombre ?: "",
                    apellido = userLocal?.apellido ?: "",
                    usuario = if (!userLocal?.usuario.isNullOrEmpty()) userLocal.usuario else userLocal?.email ?: "",
                    fotoBase64 = base64,
                    fotoPerfil = bitmap,
                    telefono = profileBody?.telefono ?: "",
                    correo = if (!userLocal?.email.isNullOrEmpty()) userLocal.email else profileBody?.correo ?: "",
                    fechaNac = profileBody?.fechaNac ?: "",
                    genero = profileBody?.genero ?: ""
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error al cargar datos"
                )
            }
        }
    }

    // Para lo de la foto
    fun onFotoTomada(bitmap: Bitmap, context: Context) {
        val base64 = bitmapToBase64(bitmap)
        _state.value = _state.value.copy(
            fotoPerfil = bitmap,
            fotoBase64 = base64
        )

        viewModelScope.launch {
            val currentState = _state.value
            val request = ProfileRequest(
                fotoBase64 = base64,
                telefono = currentState.telefono,
                correo = currentState.correo,
                fechaNac = currentState.fechaNac,
                genero = currentState.genero
            )
            repository.updateProfile(context, currentState.userId, request)
        }
    }

    // Compresión de imagen a bytes
    fun obtenerFotoEnBytes(): ByteArray? {
        val bitmap = _state.value.fotoPerfil ?: return null
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
    }

    // Decodificación de bytes a imagen
    fun cargarFotoDesdeBytes(bytes: ByteArray?) {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes?.size ?: 0)
        _state.value = _state.value.copy(fotoPerfil = bitmap)
    }

    // --- Funciones auxiliares para conversión entre Bitmap y Base64 ---

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            if (base64String.isEmpty()) return null
            val bytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }
}
