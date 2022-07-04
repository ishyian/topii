package com.topiichat.app.features.valid_phone_number.domain.model

import com.topiichat.app.core.exception.domain.ErrorDomain
import com.topiichat.app.features.valid_phone_number.presentation.model.PhoneNumber

sealed class ValidPhone {
    class Success(val phoneNumber: PhoneNumber) : ValidPhone()
    object EmptyPhoneNumber : ValidPhone()
    object NotMatchPinCode : ValidPhone()
    class Fail(val error: ErrorDomain) : ValidPhone()
}