package com.topiichat.app.features.valid_phone_number.data.datasource

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.features.valid_phone_number.data.model.PhoneNumberDto
import com.topiichat.app.features.valid_phone_number.data.model.VerifyPhoneNumberDto
import com.topiichat.app.features.valid_phone_number.data.model.VerifyPhoneNumberRequestDto
import com.topiichat.core.data.datasource.BaseRemoteDataStore
import com.topiichat.core.domain.ResultData
import javax.inject.Inject

class ValidPhoneRemoteDataStore @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {

    suspend fun verifyPhoneNumber(
        phoneNumber: String,
        code: String,
        isoCode: String
    ): ResultData<VerifyPhoneNumberDto?> {
        return safeApiCall {
            apiService.verifyPhoneNumber(
                VerifyPhoneNumberRequestDto(
                    phoneNumber = PhoneNumberDto(
                        dialCountryCode = code,
                        mobileNumber = phoneNumber
                    ),
                    countryIso = isoCode
                )
            )
        }
    }
}