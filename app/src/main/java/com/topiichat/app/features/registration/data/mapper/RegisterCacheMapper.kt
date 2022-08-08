package com.topiichat.app.features.registration.data.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.registration.data.model.AccessTokenDto
import com.topiichat.app.features.registration.domain.model.AccessTokenDomain
import javax.inject.Inject

class RegisterCacheMapper @Inject constructor() : Mapper<AccessTokenDto, AccessTokenDomain> {

    override fun map(input: AccessTokenDto?): AccessTokenDomain {
        return AccessTokenDomain(
            token = input?.token ?: ""
        )
    }
}