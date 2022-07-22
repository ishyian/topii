package com.topiichat.app.features.kyc.birthdate.domain

sealed class ValidBirthDate {
    class Success(val birthDate: String) : ValidBirthDate()
    object ErrorBirthDate : ValidBirthDate()
}