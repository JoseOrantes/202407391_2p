package com.example.tarea_082426.data

import android.content.Context
import com.example.tarea_082426.data.local.DatabaseProvider
import com.example.tarea_082426.data.local.entities.User
import com.example.tarea_082426.data.remote.RetrofitClient
import com.example.tarea_082426.model.request.LoginRequest
import com.example.tarea_082426.model.response.Login.LoginBody
import com.example.tarea_082426.model.response.Login.LoginResponse
import com.example.tarea_082426.model.response.StandardResponse
import com.example.tarea_082426.model.response.UserResponse

class LoginRepository {

    private val apiLogin = RetrofitClient.apiLogin
    private val apiProfile = RetrofitClient.apiProfile

    suspend fun login(context: Context, usuario: String, password: String): Result<LoginResponse> {
        val loginDao = DatabaseProvider.getDatabase(context).loginDao()

        return try {
            // 1. Intentar Login por Internet
            val response = apiLogin.login(LoginRequest(usuario, password))
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                
                // Guardamos en local para futuros inicios de sesión offline
                val userRes = loginResponse.body?.user
                if (userRes != null) {
                    val rawIdRol = userRes.id_rol
                    val rawRol = userRes.rol
                    val idRolReal = rawIdRol ?: if (userRes.id == 1 || userRes.email?.contains("admin", ignoreCase = true) == true) 1 else 2
                    val rolReal = if (!rawRol.isNullOrEmpty()) rawRol else if (idRolReal == 1) "ADMIN" else "USER"

                    val localUser = User(
                        id_user = userRes.id ?: 0,
                        nombre = userRes.nombre ?: "",
                        apellido = userRes.apellido ?: "",
                        usuario = userRes.usuario?.ifEmpty { null } ?: usuario,
                        password = password,
                        email = userRes.email?.ifEmpty { null } ?: usuario,
                        id_rol = idRolReal,
                        rol = rolReal
                    )
                    loginDao.insertAll(localUser)
                }
                
                Result.success(loginResponse)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            // 2. Falló el internet, intentamos Offline
            val localUser = loginDao.checkOfflineLogin(usuario, password)
            if (localUser != null) {
                // Creamos una respuesta ficticia para que la app siga funcionando
                val dummyResponse = LoginResponse(
                    standardResponse = StandardResponse(200, "Login Offline Exitoso"),
                    body = LoginBody(
                        token = "offline_token",
                        user = UserResponse(
                            id = localUser.id_user,
                            nombre = localUser.nombre,
                            apellido = localUser.apellido,
                            usuario = localUser.usuario,
                            email = localUser.email,
                            id_rol = localUser.id_rol,
                            rol = localUser.rol,
                            token = "offline_token"
                        )
                    )
                )
                Result.success(dummyResponse)
            } else {
                Result.failure(Exception("Sin conexión y no hay datos locales"))
            }
        }
    }
}
