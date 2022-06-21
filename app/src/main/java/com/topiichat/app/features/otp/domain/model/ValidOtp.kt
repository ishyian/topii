package com.topiichat.app.features.otp.domain.model

sealed class ValidOtp {
    object Success : ValidOtp()
    object SmallLength : ValidOtp()
    object Wrong : ValidOtp()
    class Fail(val msgError: String) : ValidOtp()
}