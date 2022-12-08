package com.topiichat.app.features.registration.data.datasource.remote

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.core.data.datasource.BaseRemoteDataStore
import com.topiichat.app.features.registration.data.model.RegisterDto
import com.topiichat.app.features.registration.data.model.RegisterRequestDto
import com.topiichat.app.features.valid_phone_number.data.model.PhoneNumberDto
import com.topiichat.core.domain.ResultData
import javax.inject.Inject

class RegisterRemoteDataStore @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {

    suspend fun register(
        phoneNumber: String,
        code: String,
        authyId: String,
        pinCode: String,
        aliceUserId: String
    ): ResultData<RegisterDto?> {
        return safeApiCall {
            apiService.register(
                RegisterRequestDto(
                    phoneNumber = PhoneNumberDto(
                        dialCountryCode = code,
                        mobileNumber = phoneNumber
                    ),
                    authyId = authyId,
                    pinCode = pinCode,
                    userIdAlice = aliceUserId
                )
            )
        }
    }
}