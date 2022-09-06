package com.topiichat.app.features.registration.domain.repo

import com.topiichat.app.core.domain.EmptyDomain
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.registration.domain.model.AuthDataDomain
import com.topiichat.app.features.registration.domain.model.RegisterDomain

interface RegisterRepository {
    suspend fun register(
        phoneNumber: String,
        code: String,
        authyId: String,
        pinCode: String
    ): ResultData<RegisterDomain>

    suspend fun getAuthData(): AuthDataDomain
    suspend fun saveAuthData(authData: AuthDataDomain): ResultData<EmptyDomain>
}