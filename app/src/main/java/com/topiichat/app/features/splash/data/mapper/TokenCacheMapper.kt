package com.topiichat.app.features.splash.data.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.splash.data.model.TokenCacheDto
import com.topiichat.app.features.splash.domain.model.TokenDomain
import javax.inject.Inject

class TokenCacheMapper @Inject constructor() : Mapper<TokenCacheDto, TokenDomain> {

    override fun map(input: TokenCacheDto?): TokenDomain {
        return TokenDomain(
            token = input?.token ?: "",
            expireOfSeconds = input?.expireOfSeconds ?: 0
        )
    }
}