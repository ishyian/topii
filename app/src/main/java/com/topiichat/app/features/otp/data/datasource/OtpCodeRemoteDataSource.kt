package com.topiichat.app.features.otp.data.datasource

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.core.data.datasource.BaseRemoteDataStore
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.otp.data.model.ResendOtpCodeDto
import com.topiichat.app.features.otp.data.model.ResendOtpCodeRequestDto
import com.topiichat.app.features.otp.data.model.ValidOtpCodeDto
import com.topiichat.app.features.otp.data.model.ValidOtpCodeRequestDto
import javax.inject.Inject

class OtpCodeRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {

    suspend fun validateOtpCode(authyId: String?, code: String?): ResultData<ValidOtpCodeDto?> {
        return safeApiCall {
            apiService.validateOtpCode(
                ValidOtpCodeRequestDto(
                    authyId = authyId,
                    otpCode = code
                )
            )
        }
    }

    suspend fun resendOtpCode(authyId: String?): ResultData<ResendOtpCodeDto?> {
        return safeApiCall {
            apiService.resendOtpCode(
                ResendOtpCodeRequestDto(
                    authyId = authyId
                )
            )
        }
    }
}