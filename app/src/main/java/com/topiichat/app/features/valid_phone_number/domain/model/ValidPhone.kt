package com.topiichat.app.features.valid_phone_number.domain.model

import com.topiichat.app.features.valid_phone_number.presentation.model.PhoneNumber
import com.topiichat.core.exception.domain.ErrorDomain

sealed class ValidPhone {
    class Success(val phoneNumber: PhoneNumber) : ValidPhone()
    object EmptyPhoneNumber : ValidPhone()
    object NotMatchPinCode : ValidPhone()
    class Fail(val error: ErrorDomain) : ValidPhone()
}