package com.topiichat.app.features.splash.data.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.splash.data.model.TokenDto
import com.topiichat.app.features.splash.domain.model.TokenDomain
import javax.inject.Inject

class TokenRemoteMapper @Inject constructor() : Mapper<TokenDto, TokenDomain> {

    override fun map(input: TokenDto?): TokenDomain {
        return TokenDomain(
            token = input?.token ?: "",
            expireOfSeconds = input?.expireOfSeconds ?: 0
        )
    }
}