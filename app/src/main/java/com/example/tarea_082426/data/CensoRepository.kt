package com.example.tarea_082426.data

import com.example.tarea_082426.data.remote.RetrofitClient
import com.example.tarea_082426.model.Departamento
import com.example.tarea_082426.model.Municipio
import com.example.tarea_082426.model.Pais
import com.example.tarea_082426.model.request.CensoRequest
import com.example.tarea_082426.model.response.StandardResponse
import retrofit2.Response

class CensoRepository {
    private val apiCenso = RetrofitClient.apiCenso

    private fun extractListFromRaw(raw: Any?): List<Map<*, *>> {
        if (raw == null) return emptyList()
        if (raw is List<*>) {
            return raw.filterIsInstance<Map<*, *>>()
        }
        if (raw is Map<*, *>) {
            for (key in listOf("body", "data", "departamentos", "municipios", "paises", "result")) {
                val valObj = raw[key]
                if (valObj is List<*>) {
                    return valObj.filterIsInstance<Map<*, *>>()
                }
            }
        }
        return emptyList()
    }

    suspend fun getPaises(): Result<List<Pais>> {
        return try {
            val response = apiCenso.getPaises()
            if (response.isSuccessful && response.body() != null) {
                val list = mutableListOf<Pais>()
                val rawList = extractListFromRaw(response.body())

                rawList.forEach { item ->
                    val id = item["id_pais"]?.toString() ?: item["id"]?.toString() ?: item["codigo"]?.toString() ?: "GTM"
                    val nombre = item["nombre"]?.toString() ?: item["pais"]?.toString() ?: ""
                    if (nombre.isNotEmpty()) {
                        list.add(Pais(id, nombre))
                    }
                }

                if (list.isNotEmpty()) {
                    Result.success(list)
                } else {
                    Result.success(listOf(Pais("GTM", "Guatemala")))
                }
            } else {
                Result.success(listOf(Pais("GTM", "Guatemala")))
            }
        } catch (e: Exception) {
            Result.success(listOf(Pais("GTM", "Guatemala")))
        }
    }

    suspend fun getDepartamentos(idPais: String): Result<List<Departamento>> {
        var response = try {
            apiCenso.getDepartamentos(idPais)
        } catch (e: Exception) {
            null
        }

        if (response == null || !response.isSuccessful || response.body() == null) {
            response = try {
                apiCenso.getAllDepartamentos()
            } catch (e: Exception) {
                null
            }
        }

        return try {
            if (response != null && response.isSuccessful && response.body() != null) {
                val list = mutableListOf<Departamento>()
                val rawList = extractListFromRaw(response.body())

                rawList.forEach { item ->
                    val id = (item["id_departamento"] as? Double)?.toInt() 
                        ?: (item["id_departamento"] as? Int) 
                        ?: (item["id_departamento"] as? String)?.toIntOrNull()
                        ?: (item["id"] as? Double)?.toInt() 
                        ?: (item["id"] as? Int) 
                        ?: (item["id"] as? String)?.toIntOrNull() ?: 0

                    val nombre = item["nombre"]?.toString() ?: item["departamento"]?.toString() ?: ""
                    val pId = item["id_pais"]?.toString() ?: idPais

                    if (nombre.isNotEmpty()) {
                        val validId = if (id != 0) id else list.size + 1
                        list.add(Departamento(validId, nombre, if (pId.isNotEmpty()) pId else idPais))
                    }
                }

                if (list.isNotEmpty()) {
                    Result.success(list)
                } else {
                    Result.failure(Exception("No se encontraron departamentos en la base de datos"))
                }
            } else {
                Result.failure(Exception("Error al cargar departamentos desde la API (${response?.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al cargar departamentos: ${e.message}"))
        }
    }

    suspend fun getMunicipios(idDepartamento: Int): Result<List<Municipio>> {
        var response = try {
            apiCenso.getMunicipios(idDepartamento)
        } catch (e: Exception) {
            null
        }

        if (response == null || !response.isSuccessful || response.body() == null) {
            response = try {
                apiCenso.getAllMunicipios()
            } catch (e: Exception) {
                null
            }
        }

        return try {
            if (response != null && response.isSuccessful && response.body() != null) {
                val list = mutableListOf<Municipio>()
                val rawList = extractListFromRaw(response.body())

                rawList.forEach { item ->
                    val id = (item["id_municipio"] as? Double)?.toInt() 
                        ?: (item["id_municipio"] as? Int) 
                        ?: (item["id_municipio"] as? String)?.toIntOrNull()
                        ?: (item["id"] as? Double)?.toInt() 
                        ?: (item["id"] as? Int) 
                        ?: (item["id"] as? String)?.toIntOrNull() ?: 0

                    val dId = (item["id_departamento"] as? Double)?.toInt() 
                        ?: (item["id_departamento"] as? Int) 
                        ?: (item["id_departamento"] as? String)?.toIntOrNull() ?: idDepartamento

                    val nombre = item["nombre"]?.toString() ?: item["municipio"]?.toString() ?: ""
                    val lat = (item["latitud"] as? Double) 
                        ?: (item["lat"] as? Double) 
                        ?: (item["latitud"] as? String)?.toDoubleOrNull() ?: 0.0
                    val lng = (item["longitud"] as? Double) 
                        ?: (item["lng"] as? Double) 
                        ?: (item["longitud"] as? String)?.toDoubleOrNull() ?: 0.0

                    if (nombre.isNotEmpty()) {
                        val validId = if (id != 0) id else list.size + 100
                        list.add(Municipio(validId, if (dId != 0) dId else idDepartamento, nombre, lat, lng))
                    }
                }

                if (list.isNotEmpty()) {
                    Result.success(list)
                } else {
                    Result.failure(Exception("No se encontraron municipios en la base de datos"))
                }
            } else {
                Result.failure(Exception("Error al cargar municipios desde la API (${response?.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al cargar municipios: ${e.message}"))
        }
    }

    suspend fun guardarCenso(request: CensoRequest): Result<StandardResponse> {
        return try {
            val response = apiCenso.guardarCenso(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val err = response.errorBody()?.string() ?: "Error de servidor (${response.code()})"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al conectar con la API: ${e.message}"))
        }
    }
}
