package com.topiichat.app.features.valid_phone_number.domain.model

sealed class ValidPhone {
    class Success(val phoneNumber: String) : ValidPhone()
    object EmptyPhoneNumber : ValidPhone()
    object NotMatchPinCode : ValidPhone()
    class Fail(val msgError: String) : ValidPhone()
}