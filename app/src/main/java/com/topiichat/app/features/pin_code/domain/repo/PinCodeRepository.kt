package com.topiichat.app.features.pin_code.domain.repo

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.pin_code.domain.model.ValidPinCodeDomain

interface PinCodeRepository {
    suspend fun validatePinCode(pinCode: String): ResultData<ValidPinCodeDomain>
}