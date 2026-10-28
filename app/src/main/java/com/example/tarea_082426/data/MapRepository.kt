package com.example.tarea_082426.data

import com.example.tarea_082426.data.remote.RetrofitClient
import com.example.tarea_082426.model.MarkerItem
import com.google.android.gms.maps.model.LatLng

class MapRepository {
    private val apiMap = RetrofitClient.apiMap

    // Petición GET para obtener ubicaciones filtradas por el usuario autenticado
    suspend fun getCensosMarkers(userId: Int = 0): Result<List<MarkerItem>> {
        return try {
            val response = apiMap.getCensos()
            if (response.isSuccessful && response.body() != null) {
                val rawBody = response.body()!!
                val markers = mutableListOf<MarkerItem>()

                val rawList = (rawBody as? List<*>)
                    ?: ((rawBody as? Map<*, *>)?.get("body") as? List<*>)
                    ?: ((rawBody as? Map<*, *>)?.get("censos") as? List<*>)

                rawList?.forEach { item ->
                    if (item is Map<*, *>) {
                        val itemUserId = (item["id_user"] as? Double)?.toInt()
                            ?: (item["id_user"] as? Int)
                            ?: (item["userId"] as? Double)?.toInt()
                            ?: (item["userId"] as? Int) ?: 0

                        // Muestra solo las ubicaciones pertenecientes al usuario autenticado
                        if (userId == 0 || itemUserId == userId) {
                            val nombre = item["nombre"]?.toString() ?: ""
                            val descripcion = item["descripcion"]?.toString() 
                                ?: item["apellido"]?.toString() ?: ""

                            val lat = item["latitud"]?.toString()?.toDoubleOrNull() 
                                ?: item["lat"]?.toString()?.toDoubleOrNull() 
                                ?: 14.6349
                            val lng = item["longitud"]?.toString()?.toDoubleOrNull() 
                                ?: item["lng"]?.toString()?.toDoubleOrNull() 
                                ?: -90.5069

                            val estadoNombre = item["estado_nombre"]?.toString() ?: item["estado"]?.toString() ?: ""
                            val isMasivo = estadoNombre.isNotEmpty() && !estadoNombre.equals("null", ignoreCase = true)

                            markers.add(
                                MarkerItem(
                                    position = LatLng(lat, lng),
                                    title = nombre,
                                    snippet = descripcion,
                                    isMasivo = isMasivo
                                )
                            )
                        }
                    }
                }

                Result.success(markers)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.success(emptyList())
        }
    }
}
