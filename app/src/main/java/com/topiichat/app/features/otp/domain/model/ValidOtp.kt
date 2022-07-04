package com.topiichat.app.features.otp.domain.model

import com.topiichat.app.core.exception.domain.ErrorDomain

sealed class ValidOtp {
    object Success : ValidOtp()
    object SmallLength : ValidOtp()
    object Wrong : ValidOtp()
    class Fail(val errorDomain: ErrorDomain) : ValidOtp()
}