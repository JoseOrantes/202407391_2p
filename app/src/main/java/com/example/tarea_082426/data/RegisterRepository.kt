package com.example.tarea_082426.data

import com.example.tarea_082426.data.remote.RetrofitClient
import com.example.tarea_082426.model.Role
import com.example.tarea_082426.model.request.RegisterRequest
import com.example.tarea_082426.model.response.Register.RegisterResponse

class RegisterRepository {
    private val apiRegister = RetrofitClient.apiRegister

    // Registro de usuario en servidor
    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = apiRegister.register(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en el registro"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Connection Error:  ${e.message}"))
        }
    }

    // Consulta de roles desde API
    suspend fun getRoles(): Result<List<Role>> {
        return try {
            val response = apiRegister.getRoles()
            if (response.isSuccessful && response.body() != null) {
                val rawBody = response.body()!!
                val rolesList = mutableListOf<Role>()

                if (rawBody is List<*>) {
                    rawBody.forEach { item ->
                        if (item is Map<*, *>) {
                            val id = (item["id_rol"] as? Double)?.toInt() 
                                ?: (item["id_rol"] as? Int) 
                                ?: (item["id"] as? Double)?.toInt() 
                                ?: (item["id"] as? Int) ?: 0
                            val nombreRol = item["rol"]?.toString() ?: item["nombre"]?.toString() ?: ""
                            if (id != 0 || nombreRol.isNotEmpty()) {
                                rolesList.add(Role(id, nombreRol))
                            }
                        }
                    }
                } else if (rawBody is Map<*, *>) {
                    val bodyList = (rawBody["body"] as? List<*>) ?: (rawBody["roles"] as? List<*>)
                    bodyList?.forEach { item ->
                        if (item is Map<*, *>) {
                            val id = (item["id_rol"] as? Double)?.toInt() 
                                ?: (item["id_rol"] as? Int) 
                                ?: (item["id"] as? Double)?.toInt() 
                                ?: (item["id"] as? Int) ?: 0
                            val nombreRol = item["rol"]?.toString() ?: item["nombre"]?.toString() ?: ""
                            if (id != 0 || nombreRol.isNotEmpty()) {
                                rolesList.add(Role(id, nombreRol))
                            }
                        }
                    }
                }

                if (rolesList.isNotEmpty()) {
                    Result.success(rolesList)
                } else {
                    Result.success(listOf(Role(1, "ADMIN"), Role(2, "USER")))
                }
            } else {
                Result.success(listOf(Role(1, "ADMIN"), Role(2, "USER")))
            }
        } catch (e: Exception) {
            // Opción de respaldo local si no hay red (roles por defecto)
            val defaultRoles = listOf(
                Role(1, "ADMIN"),
                Role(2, "USER")
            )
            Result.success(defaultRoles)
        }
    }
}