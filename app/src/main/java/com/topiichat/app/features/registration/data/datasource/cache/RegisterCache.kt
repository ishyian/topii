package com.topiichat.app.features.registration.data.datasource.cache

import com.topiichat.app.features.registration.data.model.AuthDataDto
import com.topiichat.app.features.registration.domain.model.AuthDataDomain
import com.topiichat.core.data.EmptyDto

interface RegisterCache {
    suspend fun fetchAccessToken(): AuthDataDto
    suspend fun saveAccessToken(tokenDomain: AuthDataDomain): EmptyDto
    suspend fun deleteAuthData(): EmptyDto
}