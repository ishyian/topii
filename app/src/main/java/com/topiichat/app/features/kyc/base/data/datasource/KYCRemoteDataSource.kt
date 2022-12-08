package com.topiichat.app.features.kyc.base.data.datasource

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.core.data.datasource.BaseRemoteDataStore
import com.topiichat.app.features.kyc.base.data.model.KYCStatusDto
import com.topiichat.app.features.kyc.base.data.model.TokenAliceDto
import com.topiichat.app.features.kyc.base.data.model.TokenAliceRequestDto
import com.topiichat.core.domain.ResultData
import javax.inject.Inject

class KYCRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {

    suspend fun getTokenAlice(
        email: String,
        firstName: String,
        lastName: String
    ): ResultData<TokenAliceDto> {
        return safeApiCall {
            apiService.getToken(
                requestDto = TokenAliceRequestDto(
                    email = email,
                    firstName = firstName,
                    lastName = lastName
                )
            )
        }
    }

    suspend fun getKYCStatus(
        token: String
    ): ResultData<KYCStatusDto?> {
        return safeApiCall {
            apiService.getKYCStatus(
                accessToken = token
            )
        }
    }
}