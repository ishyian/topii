package com.topiichat.app.features.otp.data.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.otp.data.model.ValidOtpCodeDto
import com.topiichat.app.features.otp.domain.model.ValidOtpCodeDomain
import javax.inject.Inject

class ValidOtpCodeRemoteMapper @Inject constructor() : Mapper<ValidOtpCodeDto, ValidOtpCodeDomain> {
    override fun map(input: ValidOtpCodeDto?): ValidOtpCodeDomain {
        return ValidOtpCodeDomain(
            isSuccessful = input?.success ?: false,
            message = input?.message ?: ""
        )
    }
}