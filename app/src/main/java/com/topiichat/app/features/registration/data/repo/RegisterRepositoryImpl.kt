package com.topiichat.app.features.registration.data.repo

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.core.data.EmptyMapper
import com.topiichat.app.core.domain.EmptyDomain
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.registration.data.datasource.cache.RegisterCacheDataStore
import com.topiichat.app.features.registration.data.datasource.remote.RegisterRemoteDataStore
import com.topiichat.app.features.registration.data.mapper.RegisterCacheMapper
import com.topiichat.app.features.registration.data.mapper.RegisterRemoteMapper
import com.topiichat.app.features.registration.domain.model.AccessTokenDomain
import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import kotlinx.coroutines.withContext

class RegisterRepositoryImpl(
    private val registerCacheDataStore: RegisterCacheDataStore,
    private val registerRemoteDataStore: RegisterRemoteDataStore,
    private val registerRemoteMapper: RegisterRemoteMapper,
    private val registerCacheMapper: RegisterCacheMapper,
    private val emptyMapper: EmptyMapper,
    private val appDispatchers: AppDispatchers
) : RegisterRepository {

    override suspend fun register(
        phoneNumber: String,
        code: String,
        authyId: String,
        pinCode: String
    ): ResultData<RegisterDomain> {
        return withContext(appDispatchers.network) {
            registerRemoteDataStore.register(phoneNumber, code, authyId, pinCode).transformData {
                registerRemoteMapper.map(it)
            }
        }
    }

    override suspend fun fetchAccessToken(): ResultData<AccessTokenDomain> {
        return withContext(appDispatchers.storage) {
            registerCacheDataStore.fetchToken().transformData {
                registerCacheMapper.map(it)
            }
        }
    }

    override suspend fun saveAccessToken(token: AccessTokenDomain): ResultData<EmptyDomain> {
        return withContext(appDispatchers.storage) {
            registerCacheDataStore.saveToken(token)
                .transformData {
                    emptyMapper.map(it)
                }
        }
    }
}