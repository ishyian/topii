package com.topiichat.app.features.otp.domain.model

sealed class SendSms {
    object Success : SendSms()
    class Fail(val msgError: String) : SendSms()
}