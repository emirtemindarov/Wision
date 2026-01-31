package app.emirtemindarov.p1.assistant.responsesapi

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ResponsesApiService {

    @Headers("Content-Type: application/json")
    @POST("v1/responses")
    suspend fun createResponse(
        @Body body: ResponseRequest
    ): ResponseResponse
}
