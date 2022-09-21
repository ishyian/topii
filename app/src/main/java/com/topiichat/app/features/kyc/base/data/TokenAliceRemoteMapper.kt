package com.topiichat.app.features.kyc.base.data

import com.topiichat.app.core.domain.BaseMapper
import com.topiichat.app.features.kyc.base.data.model.TokenAliceDto
import com.topiichat.app.features.kyc.base.domain.model.TokenAliceDomain
import javax.inject.Inject

class TokenAliceRemoteMapper @Inject constructor() : BaseMapper<TokenAliceDto, TokenAliceDomain> {
    override fun map(input: TokenAliceDto): TokenAliceDomain {
        return TokenAliceDomain(token = input.token)
    }
}