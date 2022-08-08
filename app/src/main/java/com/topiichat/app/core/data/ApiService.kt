package com.topiichat.app.core.data

import com.topiichat.app.features.otp.data.model.ResendOtpCodeDto
import com.topiichat.app.features.otp.data.model.ResendOtpCodeRequestDto
import com.topiichat.app.features.otp.data.model.ValidOtpCodeDto
import com.topiichat.app.features.otp.data.model.ValidOtpCodeRequestDto
import com.topiichat.app.features.registration.data.model.RegisterDto
import com.topiichat.app.features.registration.data.model.RegisterRequestDto
import com.topiichat.app.features.splash.data.model.TokenDto
import com.topiichat.app.features.valid_phone_number.data.model.VerifyPhoneNumberDto
import com.topiichat.app.features.valid_phone_number.data.model.VerifyPhoneNumberRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET("api/v1/verify/token_alice")
    suspend fun fetchToken(): TokenDto?

    @POST("api/v1/twilio/verify_phone_number")
    suspend fun verifyPhoneNumber(
        @Body verifyPhoneNumberRequestDto: VerifyPhoneNumberRequestDto
    ): VerifyPhoneNumberDto?

    @POST("api/v1/twilio/validate_code")
    suspend fun validateOtpCode(
        @Body validOtpCodeRequest: ValidOtpCodeRequestDto
    ): ValidOtpCodeDto?

    @POST("api/v1/twilio/resend_code")
    suspend fun resendOtpCode(
        @Body resendOtpCodeRequestDto: ResendOtpCodeRequestDto
    ): ResendOtpCodeDto?

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body registerRequestDto: RegisterRequestDto
    ): RegisterDto?

    @POST("api/v1/remittance/by_month")
    suspend fun fetchRemittanceHistory(
        @Header("Authorization") accessToken: String
    ): EmptyDto
}