package com.topiichat.app.features.splash.data.datasource.cache

import com.topiichat.app.core.data.EmptyDto
import com.topiichat.app.core.data.datasource.BaseCacheDataStore
import com.topiichat.app.core.domain.CacheFailStatus
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.splash.data.model.TokenCacheDto
import com.topiichat.app.features.splash.domain.model.TokenDomain
import javax.inject.Inject

class AuthCacheDataStore @Inject constructor(
    private val authCache: AuthCache
) : BaseCacheDataStore() {

    suspend fun fetchToken(): ResultData<TokenCacheDto?> {
        return fetchResult {
            authCache.fetchToken()
        }
    }

    suspend fun saveToken(token: TokenDomain): ResultData<EmptyDto?> {
        return fetchResult(cacheFailStatus = CacheFailStatus.Write()) {
            authCache.saveToken(token)
        }
    }
}