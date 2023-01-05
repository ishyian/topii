package com.topiichat.app.features.splash.data.mapper

import com.topiichat.app.features.splash.data.model.ValidateAppDto
import com.topiichat.app.features.splash.domain.model.ValidateAppDomain
import com.topiichat.core.domain.Mapper
import javax.inject.Inject

class ValidateAppRemoteMapper @Inject constructor() : Mapper<ValidateAppDto, ValidateAppDomain> {
    override fun map(input: ValidateAppDto?): ValidateAppDomain {
        return ValidateAppDomain(
            accessToken = input?.accessToken ?: ""
        )
    }
}