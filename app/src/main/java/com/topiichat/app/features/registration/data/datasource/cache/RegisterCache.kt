package com.topiichat.app.features.registration.data.datasource.cache

import com.topiichat.app.core.data.EmptyDto
import com.topiichat.app.features.registration.data.model.AuthDataDto
import com.topiichat.app.features.registration.domain.model.AuthDataDomain

interface RegisterCache {
    suspend fun fetchAccessToken(): AuthDataDto
    suspend fun saveAccessToken(tokenDomain: AuthDataDomain): EmptyDto
}