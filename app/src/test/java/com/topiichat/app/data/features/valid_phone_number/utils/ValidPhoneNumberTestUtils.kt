package com.topiichat.app.data.features.valid_phone_number.utils

import com.topiichat.app.features.valid_phone_number.data.model.PhoneNumberDto
import com.topiichat.app.features.valid_phone_number.data.model.VerifyPhoneNumberDto
import com.topiichat.app.features.valid_phone_number.domain.model.VerifyPhoneDomain

object ValidPhoneNumberTestUtils {
    val dto = VerifyPhoneNumberDto(
        authyId = "12345",
        PhoneNumberDto(
            dialCountryCode = "+51",
            mobileNumber = "987654321"
        )
    )
    val domain = VerifyPhoneDomain(
        authyId = "12345",
        phoneNumber = "987654321",
        code = "+51"
    )
}