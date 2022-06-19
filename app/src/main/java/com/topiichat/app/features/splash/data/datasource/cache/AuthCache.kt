package com.topiichat.app.features.splash.data.datasource.cache

import com.topiichat.app.core.data.EmptyDto
import com.topiichat.app.features.splash.data.model.TokenCacheDto
import com.topiichat.app.features.splash.domain.model.TokenDomain

interface AuthCache {
    suspend fun fetchToken(): TokenCacheDto?
    suspend fun saveToken(token: TokenDomain): EmptyDto?
}