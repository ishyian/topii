package com.topiichat.app.features.pin_code.data.datasource

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.core.data.datasource.BaseRemoteDataStore
import com.topiichat.app.features.pin_code.data.model.ValidPinCodeDto
import com.topiichat.app.features.pin_code.data.model.ValidPinCodeRequestDto
import com.topiichat.core.domain.ResultData
import javax.inject.Inject

class PinCodeRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {

    suspend fun validatePinCode(pinCode: String): ResultData<ValidPinCodeDto> {
        return safeApiCall {
            apiService.validatePinCode(
                ValidPinCodeRequestDto(
                    pin = pinCode
                )
            )
        }
    }
}