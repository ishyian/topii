package com.topiichat.app.core.data

import com.topiichat.app.features.home.data.model.AvailableCountryDto
import com.topiichat.app.features.home.data.model.RecentUsersDto
import com.topiichat.app.features.home.data.model.RemittanceHistoryDto
import com.topiichat.app.features.home.data.model.RemittanceHistoryRequestDto
import com.topiichat.app.features.kyc.base.data.model.KYCStatusDto
import com.topiichat.app.features.kyc.base.data.model.TokenAliceDto
import com.topiichat.app.features.kyc.base.data.model.TokenAliceRequestDto
import com.topiichat.app.features.otp.data.model.ResendOtpCodeDto
import com.topiichat.app.features.otp.data.model.ResendOtpCodeRequestDto
import com.topiichat.app.features.otp.data.model.ValidOtpCodeDto
import com.topiichat.app.features.otp.data.model.ValidOtpCodeRequestDto
import com.topiichat.app.features.personal_information.data.model.UpdateProfileInfoDto
import com.topiichat.app.features.pin_code.data.model.ValidPinCodeDto
import com.topiichat.app.features.pin_code.data.model.ValidPinCodeRequestDto
import com.topiichat.app.features.registration.data.model.RegisterDto
import com.topiichat.app.features.registration.data.model.RegisterRequestDto
import com.topiichat.app.features.request_remittance.data.model.RequestRemittanceDto
import com.topiichat.app.features.request_remittance.data.model.RequestRemittanceRequestDto
import com.topiichat.app.features.send_remittance.data.model.CardDto
import com.topiichat.app.features.send_remittance.data.model.FxRateDto
import com.topiichat.app.features.send_remittance.data.model.RemittanceDto
import com.topiichat.app.features.send_remittance.data.model.RemittancePurposeDto
import com.topiichat.app.features.send_remittance.data.model.SendPaymentIntentionRequestDto
import com.topiichat.app.features.send_remittance.data.model.SendRemittanceRequestDto
import com.topiichat.app.features.splash.data.model.ValidateAppDto
import com.topiichat.app.features.valid_phone_number.data.model.VerifyPhoneNumberDto
import com.topiichat.app.features.valid_phone_number.data.model.VerifyPhoneNumberRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/v1/alice/token_alice/")
    suspend fun getToken(
        @Body requestDto: TokenAliceRequestDto
    ): TokenAliceDto

    @POST("api/v1/twilio/verify_phone_number/")
    suspend fun verifyPhoneNumber(
        @Body verifyPhoneNumberRequestDto: VerifyPhoneNumberRequestDto
    ): VerifyPhoneNumberDto?

    @POST("api/v1/twilio/validate_code/")
    suspend fun validateOtpCode(
        @Body validOtpCodeRequest: ValidOtpCodeRequestDto
    ): ValidOtpCodeDto?

    @POST("api/v1/twilio/resend_code/")
    suspend fun resendOtpCode(
        @Body resendOtpCodeRequestDto: ResendOtpCodeRequestDto
    ): ResendOtpCodeDto?

    @POST("api/v1/auth/register/")
    suspend fun register(
        @Body registerRequestDto: RegisterRequestDto
    ): RegisterDto?

    @PATCH("api/v1/user/profile/")
    suspend fun updateProfile(
        @Header("Authorization") accessToken: String,
        @Body updateProfileInfoDto: UpdateProfileInfoDto
    ): RegisterDto

    @POST("api/v1/remittance/by_month/")
    suspend fun getRemittanceHistory(
        @Header("Authorization") accessToken: String,
        @Body homeRequestDto: RemittanceHistoryRequestDto
    ): RemittanceHistoryDto

    @GET("api/v1/user/kyc/available_countries/")
    suspend fun getAvailableCountries(
        @Header("Authorization") accessToken: String
    ): List<AvailableCountryDto>

    @GET("api/v1/remittance/debit/fx_rate/")
    suspend fun getFxRate(
        @Header("Authorization") accessToken: String,
        @Query("from_currency_code") fromCurrencyCode: String,
        @Query("from_country_code") fromCountryCode: String,
        @Query("to_currency_code") toCurrencyCode: String,
        @Query("to_country_code") toCountryCode: String,
        @Query("sending_amount") sendAmount: Double
    ): FxRateDto

    @GET("api/v1/user/kyc/status/")
    suspend fun getKYCStatus(
        @Header("Authorization") accessToken: String
    ): KYCStatusDto

    @GET("api/v1/remittance/purpose_code_options/")
    suspend fun getRemittancePurposes(): List<RemittancePurposeDto>

    @GET("api/v1/remittance/recent/transactions")
    suspend fun getRecentRemittances(
        @Header("Authorization") accessToken: String,
        @Query("skip") skip: Int = 0,
        @Query("first") first: Int = 0
    ): RecentUsersDto

    @POST("api/v1/remittance/debit/send/")
    suspend fun sendRemittance(
        @Header("Authorization") accessToken: String,
        @Body sendRemittanceRequestDto: SendRemittanceRequestDto
    ): RemittanceDto

    @GET("api/v1/remittance/debit/card/")
    suspend fun getCards(
        @Header("Authorization") accessToken: String
    ): List<CardDto>

    @POST("api/v1/remittance/debit/remittance_intention/")
    suspend fun createRemittanceIntention(
        @Header("Authorization") accessToken: String,
        @Body requestDto: SendPaymentIntentionRequestDto
    ): FxRateDto

    @GET("api/v1/remittance/{remittanceId}")
    suspend fun getRemittanceId(
        @Header("Authorization") accessToken: String,
        @Path("remittanceId") remittanceId: String
    ): RemittanceDto

    @POST("api/v1/auth/validate_pin/")
    suspend fun validatePinCode(
        @Body requestDto: ValidPinCodeRequestDto
    ): ValidPinCodeDto

    @POST("api/v1/remittance/debit/request/")
    suspend fun requestRemittance(
        @Header("Authorization") accessToken: String,
        @Body requestDto: RequestRemittanceRequestDto
    ): RequestRemittanceDto

    @GET("api/v1/auth/validate_app/")
    suspend fun validateApp(@Header("Authorization") accessToken: String): ValidateAppDto
}