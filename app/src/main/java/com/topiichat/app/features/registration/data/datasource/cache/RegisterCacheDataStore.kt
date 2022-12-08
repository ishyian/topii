package com.topiichat.app.features.registration.data.datasource.cache

import com.topiichat.app.core.data.EmptyDto
import com.topiichat.app.core.data.datasource.BaseCacheDataStore
import com.topiichat.app.features.registration.data.model.AuthDataDto
import com.topiichat.app.features.registration.domain.model.AuthDataDomain
import com.topiichat.core.domain.CacheFailStatus
import com.topiichat.core.domain.ResultData
import javax.inject.Inject

class RegisterCacheDataStore @Inject constructor(
    private val registerCache: RegisterCache
) : BaseCacheDataStore() {

    suspend fun fetchToken(): AuthDataDto {
        return registerCache.fetchAccessToken()
    }

    suspend fun saveToken(token: AuthDataDomain): ResultData<EmptyDto?> {
        return getResult(cacheFailStatus = CacheFailStatus.Write()) {
            registerCache.saveAccessToken(token)
        }
    }

    suspend fun deleteAuthData(): ResultData<EmptyDto?> {
        return getResult(cacheFailStatus = CacheFailStatus.Write()) {
            registerCache.deleteAuthData()
        }
    }
}