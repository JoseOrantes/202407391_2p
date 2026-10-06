package com.example.tarea_082426.data.remote

import com.example.tarea_082426.model.request.CensoRequest
import com.example.tarea_082426.model.response.StandardResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiCenso {

    @GET("api/paises")
    suspend fun getPaises(): Response<Any>

    @GET("api/paises/{idPais}/departamentos")
    suspend fun getDepartamentos(@Path("idPais") idPais: String): Response<Any>

    @GET("api/departamentos")
    suspend fun getAllDepartamentos(): Response<Any>

    @GET("api/departamentos/{idDepartamento}/municipios")
    suspend fun getMunicipios(@Path("idDepartamento") idDepartamento: Int): Response<Any>

    @GET("api/municipios")
    suspend fun getAllMunicipios(): Response<Any>

    @POST("api/censos")
    suspend fun guardarCenso(@Body request: CensoRequest): Response<StandardResponse>
}
