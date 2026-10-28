package com.example.tarea_082426.data

import android.content.Context
import com.example.tarea_082426.data.remote.RetrofitClient
import com.example.tarea_082426.model.request.LoginRequest
import com.example.tarea_082426.model.response.Login.LoginResponse

class LoginRepository {

    private val apiLogin = RetrofitClient.apiLogin

    suspend fun login(context: Context, usuario: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiLogin.login(LoginRequest(usuario, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}
