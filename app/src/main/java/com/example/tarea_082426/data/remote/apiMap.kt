package com.example.tarea_082426.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface ApiMap {

    @GET("api/censos")
    suspend fun getCensos(): Response<Any>
}
