package com.example.tarea_082426.data

import android.content.Context
import com.example.tarea_082426.data.local.DatabaseProvider
import com.example.tarea_082426.data.local.dao.Profile
import com.example.tarea_082426.data.local.entities.User
import com.example.tarea_082426.data.remote.RetrofitClient
import com.example.tarea_082426.model.request.ProfileRequest
import com.example.tarea_082426.model.response.Profile.ProfileBody
import com.example.tarea_082426.model.response.Profile.ProfileResponse
import com.example.tarea_082426.model.response.StandardResponse

class ProfileRepository {

    private val apiProfile = RetrofitClient.apiProfile

    suspend fun getProfile(id: Int): Result<ProfileResponse> {
        return try {
            val response = apiProfile.getProfile(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Profile not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Connection Error: ${e.message}"))
        }
    }

    suspend fun getProfileOfflineFirst(context: Context, id: Int): Result<ProfileResponse> {
        val profileDao = DatabaseProvider.getDatabase(context).profileDao()

        return try {
            val response = apiProfile.getProfile(id)
            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!
                val profileBody = profileResponse.body
                if (profileBody != null) {
                    val localProfile = Profile(
                        userId = id,
                        fotoBase64 = profileBody.fotoBase64 ?: "",
                        telefono = profileBody.telefono ?: "",
                        correo = profileBody.correo ?: "",
                        fechaNac = profileBody.fechaNac ?: "",
                        genero = profileBody.genero ?: ""
                    )
                    profileDao.insertAll(localProfile)
                }
                Result.success(profileResponse)
            } else {
                val localProfile = profileDao.loadById(id)
                if (localProfile != null) {
                    val dummyResponse = ProfileResponse(
                        standardResponse = StandardResponse(200, "Profile Offline Exitoso"),
                        body = ProfileBody(
                            id = localProfile.userId,
                            userId = localProfile.userId,
                            fotoBase64 = localProfile.fotoBase64,
                            telefono = localProfile.telefono,
                            correo = localProfile.correo,
                            fechaNac = localProfile.fechaNac,
                            genero = localProfile.genero
                        )
                    )
                    Result.success(dummyResponse)
                } else {
                    Result.failure(Exception("Profile not found"))
                }
            }
        } catch (e: Exception) {
            // Si falla la red (offline), usar SQLite local
            val localProfile = profileDao.loadById(id)
            if (localProfile != null) {
                val dummyResponse = ProfileResponse(
                    standardResponse = StandardResponse(200, "Profile Offline Exitoso"),
                    body = ProfileBody(
                        id = localProfile.userId,
                        userId = localProfile.userId,
                        fotoBase64 = localProfile.fotoBase64,
                        telefono = localProfile.telefono,
                        correo = localProfile.correo,
                        fechaNac = localProfile.fechaNac,
                        genero = localProfile.genero
                    )
                )
                Result.success(dummyResponse)
            } else {
                Result.failure(Exception("Sin conexión y no hay perfil local"))
            }
        }
    }

    suspend fun getUser(id: Int): Result<Map<String, Any>> {
        return try {
            val response = apiProfile.getUser(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    // CODIGO ORIGINAL:
    // suspend fun getUserOfflineFirst(context: Context, id: Int): Result<User> {
    //     val loginDao = DatabaseProvider.getDatabase(context).loginDao()
    //     val localUser = loginDao.loadById(intArrayOf(id)).firstOrNull()
    //     if (localUser != null) {
    //         return Result.success(localUser)
    //     }
    //     return try {
    //         val response = apiProfile.getUser(id)
    //         if (response.isSuccessful && response.body() != null) {
    //             val data = response.body()!!
    //             val userFromServer = User(
    //                 id_user = id,
    //                 nombre = data["nombre"]?.toString() ?: "",
    //                 apellido = data["apellido"]?.toString() ?: "",
    //                 usuario = data["usuario"]?.toString() ?: "",
    //                 password = "",
    //                 email = data["email"]?.toString() ?: ""
    //             )
    //             loginDao.insertAll(userFromServer)
    //             Result.success(userFromServer)
    //         } else { ... }
    //     } catch (e: Exception) { ... }
    // }

    suspend fun getUserOfflineFirst(context: Context, id: Int): Result<User> {
        val loginDao = DatabaseProvider.getDatabase(context).loginDao()

        // 1. Intentamos consultar primero la red para obtener los datos completos del servidor (nombre, apellido, usuario, email)
        return try {
            val response = apiProfile.getUser(id)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                // Extraer 'body' o 'user' si la respuesta viene envuelta en standardResponse/body
                val userMap = (data["body"] as? Map<*, *>) ?: (data["user"] as? Map<*, *>) ?: data

                val existingUser = loginDao.loadById(intArrayOf(id)).firstOrNull()
                val passwordToKeep = existingUser?.password ?: ""
                val parsedIdRol = (userMap["id_rol"] as? Double)?.toInt() 
                    ?: (userMap["id_rol"] as? Int) 
                    ?: existingUser?.id_rol 
                    ?: 0

                val userFromServer = User(
                    id_user = id,
                    nombre = userMap["nombre"]?.toString() ?: existingUser?.nombre ?: "",
                    apellido = userMap["apellido"]?.toString() ?: existingUser?.apellido ?: "",
                    usuario = userMap["usuario"]?.toString() ?: existingUser?.usuario ?: "",
                    password = passwordToKeep,
                    email = userMap["email"]?.toString() ?: existingUser?.email ?: "",
                    id_rol = parsedIdRol,
                    rol = userMap["rol"]?.toString() ?: existingUser?.rol ?: ""
                )

                // Guardar/Actualizar en SQLite local con la informacion completa
                loginDao.insertAll(userFromServer)

                Result.success(userFromServer)
            } else {
                val localUser = loginDao.loadById(intArrayOf(id)).firstOrNull()
                if (localUser != null) Result.success(localUser)
                else Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            // 2. Si falla la red (offline), usamos los datos locales como respaldo
            val localUser = loginDao.loadById(intArrayOf(id)).firstOrNull()
            if (localUser != null) {
                Result.success(localUser)
            } else {
                Result.failure(Exception("Sin conexión y no hay datos locales"))
            }
        }
    }

    suspend fun updateProfile(context: Context, id: Int, profileRequest: ProfileRequest): Result<StandardResponse> {
        val profileDao = DatabaseProvider.getDatabase(context).profileDao()

        // 1. Guardar localmente en SQLite
        val localProfile = Profile(
            userId = id,
            fotoBase64 = profileRequest.fotoBase64,
            telefono = profileRequest.telefono,
            correo = profileRequest.correo,
            fechaNac = profileRequest.fechaNac,
            genero = profileRequest.genero
        )
        profileDao.insertAll(localProfile)

        // 2. Enviar a MariaDB / AWS via Retrofit
        return try {
            val response = apiProfile.updateProfile(id, profileRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar perfil en servidor"))
            }
        } catch (e: Exception) {
            Result.success(StandardResponse(200, "Guardado en SQLite localmente"))
        }
    }
}
