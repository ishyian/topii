package com.topiichat.app.data.features.otp.utils

import com.topiichat.app.features.otp.data.model.ResendOtpCodeDto
import com.topiichat.app.features.otp.data.model.ValidOtpCodeDto
import com.topiichat.app.features.otp.domain.model.ValidOtpCodeDomain

object OtpCodeTestUtils {
    val validOtpDto = ValidOtpCodeDto(
        success = true,
        message = "Valid otp code"
    )
    val validOtpDomain = ValidOtpCodeDomain(
        isSuccessful = true,
        message = "Valid otp code"
    )
    val resendOtpDto = ResendOtpCodeDto(
        success = true,
        message = "Valid otp code"
    )
    val resendOtpDomain = ValidOtpCodeDomain(
        isSuccessful = true,
        message = "Valid otp code"
    )
}