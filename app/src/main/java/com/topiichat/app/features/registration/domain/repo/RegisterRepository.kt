package com.topiichat.app.features.registration.domain.repo

import com.topiichat.app.features.registration.domain.model.AuthDataDomain
import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.core.domain.EmptyDomain
import com.topiichat.core.domain.ResultData

interface RegisterRepository {
    suspend fun register(
        phoneNumber: String,
        code: String,
        authyId: String,
        pinCode: String,
        aliceUserId: String
    ): ResultData<RegisterDomain>

    suspend fun getAuthData(): AuthDataDomain
    suspend fun saveAuthData(authData: AuthDataDomain): ResultData<EmptyDomain>
    suspend fun deleteAuthData(): ResultData<EmptyDomain>
}