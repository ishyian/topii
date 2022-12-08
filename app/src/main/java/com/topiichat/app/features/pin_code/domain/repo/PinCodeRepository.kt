package com.topiichat.app.features.pin_code.domain.repo

import com.topiichat.app.features.pin_code.domain.model.ValidPinCodeDomain
import com.topiichat.core.domain.ResultData

interface PinCodeRepository {
    suspend fun validatePinCode(pinCode: String): ResultData<ValidPinCodeDomain>
}