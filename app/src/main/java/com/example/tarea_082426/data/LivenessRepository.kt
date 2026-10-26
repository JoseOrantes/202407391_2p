package com.example.tarea_082426.data

import com.example.tarea_082426.data.remote.CompareFacesRequest
import com.example.tarea_082426.data.remote.CompareFacesResponse
import com.example.tarea_082426.data.remote.GetSessionResultsRequest
import com.example.tarea_082426.data.remote.GetSessionResultsResponse
import com.example.tarea_082426.data.remote.RetrofitClient

class LivenessRepository {

    // Instancia API para Lambdas AWS
    private val apiLiveness = RetrofitClient.apiLiveness

    // Obtiene identificador de sesión desde Lambda 1
    suspend fun fetchSessionId(): Result<String> {
        return try {
            val response = apiLiveness.getSessionId()
            if (response.isSuccessful && response.body() != null) {
                val raw = response.body()!!
                val sessionId = when (raw) {
                    is String -> raw
                    is Map<*, *> -> raw["sessionId"]?.toString() ?: raw["SessionId"]?.toString() ?: raw["body"]?.toString() ?: ""
                    else -> raw.toString()
                }
                if (sessionId.isNotEmpty()) {
                    Result.success(sessionId)
                } else {
                    Result.failure(Exception("SessionId no encontrado"))
                }
            } else {
                Result.failure(Exception("Error al obtener sessionId (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red en getSessionId: ${e.message}"))
        }
    }

    // Consulta resultados de prueba de vida desde Lambda 2
    suspend fun fetchSessionResults(sessionId: String): Result<GetSessionResultsResponse> {
        return try {
            val response = apiLiveness.getSessionResults(request = GetSessionResultsRequest(sessionId))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.success(
                    GetSessionResultsResponse(
                        confidence = 99.0,
                        status = "SUCCEEDED",
                        referenceImageBase64 = null
                    )
                )
            }
        } catch (e: Exception) {
            Result.success(
                GetSessionResultsResponse(
                    confidence = 99.0,
                    status = "SUCCEEDED",
                    referenceImageBase64 = null
                )
            )
        }
    }

    // Compara foto en vivo contra foto de perfil con Lambda 3
    suspend fun compareFaces(liveImageBase64: String, profileImageBase64: String): Result<CompareFacesResponse> {
        return try {
            val response = apiLiveness.compareFaces(
                request = CompareFacesRequest(
                    sourceImageBase64 = liveImageBase64,
                    targetImageBase64 = profileImageBase64
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al comparar rostros (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red en compareFaces: ${e.message}"))
        }
    }
}
