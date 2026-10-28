package com.example.tarea_082426.data

import com.example.tarea_082426.data.remote.RetrofitClient
import com.example.tarea_082426.model.MarkerItem
import com.google.android.gms.maps.model.LatLng

class MapRepository {
    private val apiMap = RetrofitClient.apiMap

    // Petición GET para obtener ubicaciones
    suspend fun getCensosMarkers(): Result<List<MarkerItem>> {
        return try {
            val response = apiMap.getCensos()
            if (response.isSuccessful && response.body() != null) {
                val rawBody = response.body()!!
                val markers = mutableListOf<MarkerItem>()

                // Extrae lista de ubicaciones desde respuesta JSON
                val rawList = (rawBody as? List<*>)
                    ?: ((rawBody as? Map<*, *>)?.get("body") as? List<*>)
                    ?: ((rawBody as? Map<*, *>)?.get("censos") as? List<*>)

                // Mapeo de cada ubicación a marcador
                rawList?.forEach { item ->
                    if (item is Map<*, *>) {
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

                        val title = nombre
                        val snippet = descripcion

                        markers.add(
                            MarkerItem(
                                position = LatLng(lat, lng),
                                title = title,
                                snippet = snippet,
                                isMasivo = isMasivo
                            )
                        )
                    }
                }

                if (markers.isNotEmpty()) {
                    Result.success(markers)
                } else {
                    Result.success(listOf(
                        MarkerItem(LatLng(14.6349, -90.5069), "Ciudad de Guatemala", "Capital")
                    ))
                }
            } else {
                Result.success(listOf(
                    MarkerItem(LatLng(14.6349, -90.5069), "Ciudad de Guatemala", "Capital")
                ))
            }
        } catch (e: Exception) {
            Result.success(listOf(
                MarkerItem(LatLng(14.6349, -90.5069), "Ciudad de Guatemala", "Capital")
            ))
        }
    }
}
