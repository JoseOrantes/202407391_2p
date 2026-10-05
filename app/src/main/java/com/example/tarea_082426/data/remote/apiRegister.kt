package com.example.tarea_082426.data.remote

import com.example.tarea_082426.model.Role
import com.example.tarea_082426.model.request.RegisterRequest
import com.example.tarea_082426.model.response.Register.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiRegister {

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/roles")
    suspend fun getRoles(): Response<Any>
}

