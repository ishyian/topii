package com.topiichat.app.features.registration.data.repo

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.registration.data.datasource.RegisterRemoteDataStore
import com.topiichat.app.features.registration.data.mapper.RegisterRemoteMapper
import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import kotlinx.coroutines.withContext

class RegisterRepositoryImpl(
    private val registerRemoteDataStore: RegisterRemoteDataStore,
    private val registerRemoteMapper: RegisterRemoteMapper,
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
}