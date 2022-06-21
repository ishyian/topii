package com.topiichat.app.features.pin_code.domain

sealed class ValidPinCode {
    class Success(val pinCode: String) : ValidPinCode()
    object Empty : ValidPinCode()
    object ErrorCountSymbols : ValidPinCode()
    object ErrorConsecutiveNumbers: ValidPinCode()
    object ErrorSameNumbers: ValidPinCode()
    object ErrorMatches : ValidPinCode()
}