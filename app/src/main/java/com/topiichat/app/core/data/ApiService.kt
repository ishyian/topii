package com.topiichat.app.core.data

import com.topiichat.app.features.splash.data.model.TokenDto
import com.topiichat.app.features.valid_phone_number.data.ValidPhoneNumberDto
import com.topiichat.app.features.valid_phone_number.data.ValidPhoneNumberRequestDto
import kotlinx.coroutines.delay
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import kotlin.random.Random

interface ApiService {
    @GET("/api/v1/verify/token_alice")
    suspend fun fetchToken(): Response<TokenDto?>

    @POST("/api/v1/verify/valid_phone_number")
    suspend fun fetchValidPhoneNumber(
        @Body validPhoneNumberRequest: ValidPhoneNumberRequestDto
    ): Response<ValidPhoneNumberDto?>
}

class MockApiService : ApiService {

    override suspend fun fetchToken(): Response<TokenDto?> {
        delay(2000L)
        return if (Random.nextBoolean()) {
            val response = TokenDto(true, "blablabla", 30000)
            Response.success(response)
        } else {
            Response.error(500, null)
        }
    }

    override suspend fun fetchValidPhoneNumber(
        validPhoneNumberRequest: ValidPhoneNumberRequestDto
    ): Response<ValidPhoneNumberDto?> {
        delay(2000L)
        return if (Random.nextBoolean()) {
            val response = ValidPhoneNumberDto(true, "valid phone number")
            Response.success(response)
        } else {
            Response.error(500, null)
        }
    }
}