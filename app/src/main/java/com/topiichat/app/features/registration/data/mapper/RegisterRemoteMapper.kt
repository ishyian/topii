package com.topiichat.app.features.registration.data.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.registration.data.model.RegisterDto
import com.topiichat.app.features.registration.domain.model.RegisterDomain
import javax.inject.Inject

class RegisterRemoteMapper @Inject constructor() : Mapper<RegisterDto, RegisterDomain> {
    override fun map(input: RegisterDto?): RegisterDomain {
        return RegisterDomain(
            accessToken = input?.accessToken ?: ""
        )
    }
}