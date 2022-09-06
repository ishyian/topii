package com.topiichat.app.features.registration.data.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.registration.data.model.AuthDataDto
import com.topiichat.app.features.registration.domain.model.AuthDataDomain
import javax.inject.Inject

class RegisterCacheMapper @Inject constructor() : Mapper<AuthDataDto, AuthDataDomain> {

    override fun map(input: AuthDataDto?): AuthDataDomain {
        return AuthDataDomain(
            token = input?.token ?: "",
            senderId = input?.senderId ?: "",
            isoCode = input?.isoCode ?: ""
        )
    }
}