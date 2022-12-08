package com.topiichat.app.features.pin_code.data.mapper

import com.topiichat.app.features.pin_code.data.model.ValidPinCodeDto
import com.topiichat.app.features.pin_code.domain.model.ValidPinCodeDomain
import com.topiichat.core.domain.BaseMapper
import javax.inject.Inject

class ValidPinCodeRemoteMapper @Inject constructor() : BaseMapper<ValidPinCodeDto, ValidPinCodeDomain> {
    override fun map(input: ValidPinCodeDto): ValidPinCodeDomain {
        return ValidPinCodeDomain(
            status = input.validation ?: "",
            pin = input.pin ?: ""
        )
    }
}