package com.topiichat.app.features.registration.data.datasource.cache

import com.topiichat.app.core.data.EmptyDto
import com.topiichat.app.features.registration.data.model.AccessTokenDto
import com.topiichat.app.features.registration.domain.model.AccessTokenDomain

interface RegisterCache {
    suspend fun fetchAccessToken(): AccessTokenDto
    suspend fun saveAccessToken(tokenDomain: AccessTokenDomain): EmptyDto
}