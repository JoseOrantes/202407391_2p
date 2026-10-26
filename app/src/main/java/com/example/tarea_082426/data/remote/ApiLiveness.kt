package com.example.tarea_082426.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

data class GetSessionResultsRequest(
    val sessionId: String
)

data class GetSessionResultsResponse(
    val confidence: Any? = null,
    val status: String? = null,
    val referenceImageBase64: String? = null
)

// Petición para comparación de rostros (Lambda 3)
data class CompareFacesRequest(
    val sourceImageBase64: String,
    val targetImageBase64: String
)

// Respuesta de comparación facial
data class CompareFacesResponse(
    val isMatch: Boolean = false,
    val similarity: Double = 0.0,
    val message: String = ""
)

interface ApiLiveness {

    // Obtención de SessionId desde AWS Lambda 1
    @GET
    suspend fun getSessionId(@Url url: String = "https://z7x5f3gxwk5fo54d57h5dj3vu40dmnmz.lambda-url.us-east-2.on.aws/"): Response<Any>

    // Obtención de resultados de Liveness desde AWS Lambda 2
    @POST
    suspend fun getSessionResults(
        @Url url: String = "https://g5fknbtkojh5ev32sitjvslbwa0qxgmy.lambda-url.us-east-2.on.aws/",
        @Body request: GetSessionResultsRequest
    ): Response<GetSessionResultsResponse>

    // Comparación facial desde AWS Lambda 3
    @POST
    suspend fun compareFaces(
        @Url url: String = "https://fldpbsvh25yo2eftlcfuwi7mgy0csxdv.lambda-url.us-east-2.on.aws/",
        @Body request: CompareFacesRequest
    ): Response<CompareFacesResponse>
}
