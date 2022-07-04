package com.topiichat.app.features.otp.data.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.otp.data.model.ResendOtpCodeDto
import com.topiichat.app.features.otp.domain.model.ResendOtpCodeDomain
import javax.inject.Inject

class ResendOtpCodeRemoteMapper @Inject constructor() : Mapper<ResendOtpCodeDto, ResendOtpCodeDomain> {
    override fun map(input: ResendOtpCodeDto?): ResendOtpCodeDomain {
        return ResendOtpCodeDomain(
            isSuccessful = input?.success ?: false,
            message = input?.message ?: ""
        )
    }
}