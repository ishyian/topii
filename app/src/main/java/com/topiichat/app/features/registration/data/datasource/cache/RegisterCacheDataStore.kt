package com.topiichat.app.features.registration.data.datasource.cache

import com.topiichat.app.core.data.EmptyDto
import com.topiichat.app.core.data.datasource.BaseCacheDataStore
import com.topiichat.app.core.domain.CacheFailStatus
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.registration.data.model.AccessTokenDto
import com.topiichat.app.features.registration.domain.model.AccessTokenDomain
import javax.inject.Inject

class RegisterCacheDataStore @Inject constructor(
    private val registerCache: RegisterCache
) : BaseCacheDataStore() {

    suspend fun fetchToken(): ResultData<AccessTokenDto?> {
        return fetchResult {
            registerCache.fetchAccessToken()
        }
    }

    suspend fun saveToken(token: AccessTokenDomain): ResultData<EmptyDto?> {
        return fetchResult(cacheFailStatus = CacheFailStatus.Write()) {
            registerCache.saveAccessToken(token)
        }
    }
}