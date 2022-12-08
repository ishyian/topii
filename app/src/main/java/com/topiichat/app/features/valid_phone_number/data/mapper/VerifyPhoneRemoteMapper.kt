package com.topiichat.app.features.valid_phone_number.data.mapper

import com.topiichat.app.features.valid_phone_number.data.model.VerifyPhoneNumberDto
import com.topiichat.app.features.valid_phone_number.domain.model.VerifyPhoneDomain
import com.topiichat.core.domain.Mapper
import javax.inject.Inject

class VerifyPhoneRemoteMapper @Inject constructor() : Mapper<VerifyPhoneNumberDto, VerifyPhoneDomain> {
    override fun map(input: VerifyPhoneNumberDto?): VerifyPhoneDomain {
        return VerifyPhoneDomain(
            authyId = input?.authyId ?: "",
            phoneNumber = input?.phoneNumber?.mobileNumber ?: "",
            code = input?.phoneNumber?.dialCountryCode ?: ""
        )
    }
}