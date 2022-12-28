package com.topiichat.app.features.kyc.base.data

import com.topiichat.app.features.kyc.base.data.model.TokenAliceDto
import com.topiichat.app.features.kyc.base.domain.model.TokenAliceDomain
import com.topiichat.core.domain.BaseMapper
import javax.inject.Inject

class TokenAliceRemoteMapper @Inject constructor() : BaseMapper<TokenAliceDto, TokenAliceDomain> {
    override fun map(input: TokenAliceDto): TokenAliceDomain {
        return TokenAliceDomain(token = input.token)
    }
}