package com.topiichat.app.features.valid_phone_number.domain.repo

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.valid_phone_number.domain.model.VerifyPhoneDomain

interface ValidPhoneRepository {
    suspend fun verifyPhone(
        phoneNumber: String,
        code: String,
        isoCode: String
    ): ResultData<VerifyPhoneDomain>
}