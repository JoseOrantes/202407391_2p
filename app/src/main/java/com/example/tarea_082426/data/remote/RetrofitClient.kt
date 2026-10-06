package com.example.tarea_082426.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://202407391.aplicacionesweb2026.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiLogin: ApiLogin by lazy {
        retrofit.create(ApiLogin::class.java)
    }

    val apiProfile: ApiProfile by lazy {
        retrofit.create(ApiProfile::class.java)
    }

    val apiRegister: ApiRegister by lazy {
        retrofit.create(ApiRegister::class.java)
    }

    val apiCenso: ApiCenso by lazy {
        retrofit.create(ApiCenso::class.java)
    }

    val apiMap: ApiMap by lazy {
        retrofit.create(ApiMap::class.java)
    }
}
