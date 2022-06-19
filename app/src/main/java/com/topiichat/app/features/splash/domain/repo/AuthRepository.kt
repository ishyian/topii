package com.topiichat.app.features.splash.domain.repo

import com.topiichat.app.core.domain.EmptyDomain
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.splash.domain.model.TokenDomain

interface AuthRepository {
    suspend fun fetchTokenRemote(): ResultData<TokenDomain>
    suspend fun fetchTokenCache(): ResultData<TokenDomain>
    suspend fun saveToken(token: TokenDomain): ResultData<EmptyDomain>
}