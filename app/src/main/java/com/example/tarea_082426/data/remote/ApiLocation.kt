package com.example.tarea_082426.data.remote

import com.example.tarea_082426.model.request.LocationRequest
import com.example.tarea_082426.model.response.StandardResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiLocation {

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
    suspend fun guardarLocation(@Body request: LocationRequest): Response<StandardResponse>

    @POST("api/censo")
    suspend fun guardarLocationSingular(@Body request: LocationRequest): Response<StandardResponse>

    @POST("api/users/{userId}/censos")
    suspend fun guardarLocationUser(@Path("userId") userId: Int, @Body request: LocationRequest): Response<StandardResponse>

    @POST("api/users/{userId}/censo")
    suspend fun guardarLocationUserSingular(@Path("userId") userId: Int, @Body request: LocationRequest): Response<StandardResponse>

    @POST("api/censos/crear")
    suspend fun guardarLocationCrear(@Body request: LocationRequest): Response<StandardResponse>

    @POST("api/censo/crear")
    suspend fun guardarLocationSingularCrear(@Body request: LocationRequest): Response<StandardResponse>
}
